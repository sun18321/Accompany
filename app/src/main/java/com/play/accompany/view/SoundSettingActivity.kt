package com.play.accompany.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import androidx.core.app.ActivityCompat
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.google.gson.reflect.TypeToken
import com.play.accompany.R
import com.play.accompany.base.BaseActivity
import com.play.accompany.bean.*
import com.play.accompany.constant.AppConstant
import com.play.accompany.constant.IntentConstant
import com.play.accompany.constant.OtherConstant
import com.play.accompany.constant.SpConstant
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.utils.*
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder
import kotlinx.android.synthetic.main.activity_sound_setting.*
import kotlinx.android.synthetic.main.blue_toolbar.*
import java.io.File
import java.util.*

class SoundSettingActivity : BaseActivity() {
    private var mFile: File? = null
    private var mName = ""
    private lateinit var mAnimator: ObjectAnimator
    private var mProgress = 0
    private var mUpload = false
    private var mCode = -1

    override fun getLayout(): Int {
        return R.layout.activity_sound_setting
    }

    override fun getTag(): String {
        return "SoundSettingActivity"
    }

    override fun parseIntent() {
        super.parseIntent()

        mCode = intent.getIntExtra(OtherConstant.SET_SOUND_SPEAK, -1)

    }

    override fun initViews() {
        initToolbar("设置声音")

        if (mCode == -1) {
            requestData()
        } else {
            lin_play.visibility = View.GONE
            lin_record.visibility = View.VISIBLE
            tv_time.text = "按住录音，不超过60S"
            showRecord()
        }

        tv_right.setOnClickListener {
            if (mCode == -1) {
                uploadUserSound()
            } else {
                backSound()
            }
        }

    }

    private fun backSound() {
        if (mFile == null) {
            ToastUtils.showCommonToast("您还没录制音频，请先录制声音")
            return
        }

        if (mFile!!.length() > OtherConstant.MAX_AUDIO_SIZE) {
            ToastUtils.showCommonToast("音频过大，最大可支持3M")
            return
        }

        if (mProgress > 60) {
            ToastUtils.showCommonToast("音频超时，最长可支持60S")
            return
        }

        val audioBean = FilterAudioBean(mName, mFile!!.absolutePath, mFile!!.length(), AppUtils.FormatFileSize(mFile!!.length()),
                mProgress, AppUtils.timeParse(mProgress.toLong()))

        setResult(100, Intent().putExtra(IntentConstant.INTENT_AUDIO, audioBean))
        this.finish()
    }

    private fun uploadUserSound() {
        if (mFile == null) {
            showDeleteDialog()
        } else {
            if (!mUpload) {
                mUpload = true
                showDialog("正在上传")
                SoundManager.instance.uploadSound(mFile!!, OtherConstant.SOUND_HOME, mProgress.toString(),object :SoundManager.UploadListener{
                    override fun onFailed() {
                        runOnUiThread {
                            dismissDialog()
                            ToastUtils.showCommonToast("上传失败")
                            mUpload = false
                        }
                    }

                    override fun onSuccess() {
                        runOnUiThread {
                            dismissDialog()
                            ToastUtils.showCommonToast("上传成功")
                            tv_right.visibility = View.INVISIBLE
                            mUpload = false
                        }
                    }
                })
            }
        }
    }

    private fun showPlay(bean: SoundBean) {
        lin_play.visibility = View.VISIBLE
        lin_record.visibility = View.INVISIBLE
        sound_view.setData(bean.audioUrl, bean.audioLen)

        img_delete.setOnClickListener {
            tv_size.text = ""
            tv_size.visibility = View.GONE
            showRecord()
        }
    }

    private fun showRecord() {
        tv_right.text = "保存"
        tv_right.visibility = View.VISIBLE

        lin_record.visibility = View.VISIBLE
        lin_play.visibility = View.INVISIBLE

        img_record.setOnClickListener {
            checkPermission()
        }

        img_record.setOnTouchListener { _, event ->
            if (checkPermission()) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startRecord()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopRecord()
                    }
                }
            } else {
                ToastUtils.showCommonToast("请打开麦克风再尝试录音")
//                requestPermission(arrayOf(android.Manifest.permission.RECORD_AUDIO))
            }
            true
        }

    }

    private fun startRecord() {
        tv_right.visibility = View.INVISIBLE
        addAnim()
        val dir = getExternalFilesDir(AppConstant.SOUND_FILE)
        mName = SPUtils.getInstance().getString(SpConstant.MY_USER_ID) + "_" + System.currentTimeMillis() + ".wav"
        mFile = File(dir, mName)
        var maxLength = 16

        if (mCode == OtherConstant.SET_SOUND_SPEAK_CODE) {
            maxLength = 61
        }
        SoundManager.instance.startRecord(mFile!!,maxLength,object :SoundManager.SoundListener{
            override fun onProgress(progress: Int) {
                runOnUiThread {
                    mProgress = progress
                    val text = progress.toString() + "S"
                    tv_time.text = text
                }
            }

            override fun onFinish() {
                runOnUiThread {
                    recordFinish()
                }
            }

            override fun onTooShort() {
                runOnUiThread {
                    ToastUtils.showCommonToast("录制时间过短，请重新录制")
                    if (mCode == -1) {
                        tv_time.text = resources.getString(R.string.sound_tip)
                    } else {
                        tv_time.text = "按住录音,不超过60S"
                    }
                }
            }
        })
    }

    private fun recordFinish() {
        lin_play.visibility = View.VISIBLE
        lin_record.visibility = View.INVISIBLE
        sound_view.setData(mFile, mProgress)

        if (mCode !=-1) {
            tv_size.text = AppUtils.FormatFileSize(mFile!!.length())
            tv_size.visibility = View.VISIBLE
        }

        LogUtils.d("sound","length:$mProgress")
        LogUtils.d("sound", "address:${mFile?.absolutePath}")
        tv_right.text = "保存"
        tv_right.visibility = View.VISIBLE
    }

    private fun deleteSound() {
        val bean = DeleteSoundBean()
        bean.audioId = OtherConstant.SOUND_HOME
        val json = GsonUtils.toJson(bean)
        AccompanyRequest().beginRequest(NetFactory.getNetRequest().netService.deleteAudio(EncodeUtils.encodeInBody(json)), object : TypeToken<BaseDecodeBean<OnlyCodeBean>>() {}.type,
                object : NetListener<OnlyCodeBean> {
                    override fun onSuccess(t: OnlyCodeBean?) {
                        ToastUtils.showCommonToast("删除成功")
                    }

                    override fun onFailed(errCode: Int) {
                    }

                    override fun onError() {
                    }

                    override fun onComplete() {
                    }

                })
    }

    private fun showDeleteDialog() {
        QMUIDialog.MessageDialogBuilder(this).setMessage("确定要清空自己的声音吗？")
                .addAction("取消") { dialog, _ -> dialog.dismiss() }
                .addAction("确定") { dialog, _ ->
                    dialog.dismiss()
                    deleteSound()
                }.create().show()
    }

    private fun stopRecord() {
        mAnimator.cancel()
        SoundManager.instance.stopRecord()
    }

    private fun addAnim() {
        mAnimator = ObjectAnimator.ofFloat(img_side, "rotation",0f, 360f).setDuration(1000)
        mAnimator.repeatMode = ValueAnimator.RESTART
        mAnimator.repeatCount = ValueAnimator.INFINITE
        mAnimator.interpolator = LinearInterpolator()
        mAnimator.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
                img_side.visibility = View.INVISIBLE
            }

            override fun onAnimationStart(animation: Animator?) {
                img_side.visibility = View.VISIBLE
            }

        })
        mAnimator.start()
    }

    private fun checkPermission():Boolean {
        val permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: Array<String>) {
        ActivityCompat.requestPermissions(this, permission, 999)
    }


    private fun requestData() {
        val bean = QuerySoundBean(SPUtils.getInstance().getString(SpConstant.APP_TOKEN), OtherConstant.SOUND_HOME)
        AccompanyRequest().beginRequest(NetFactory.getNetRequest().netService.getAudio(EncodeUtils.encodeInBody(GsonUtils.toJson(bean))),
                object:TypeToken<BaseDecodeBean<List<SoundBean>>>(){}.type,object:NetListener<List<SoundBean>>{
            override fun onSuccess(list: List<SoundBean>?) {
                if (list.isNullOrEmpty()) {
                    showRecord()
                } else {
                    val soundBean = list[0]
                    if (TextUtils.isEmpty(soundBean.audioUrl)) {
                        showRecord()
                    } else {
                        showPlay(soundBean)
                    }
                }
                if (!checkPermission()) {
                    requestPermission(arrayOf(android.Manifest.permission.RECORD_AUDIO))
                }
            }

            override fun onFailed(errCode: Int) {

            }

            override fun onError() {

            }

            override fun onComplete() {

            }

        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            startRecord()
        } else {
            ToastUtils.showCommonToast("访问失败，请打开您的麦克风")
        }
    }

    override fun onStop() {
        super.onStop()

        sound_view.stopPlay()
    }

    override fun onDestroy() {
        super.onDestroy()

        sound_view.destroy()
    }

}
