package com.play.accompany.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.media.AudioManager
import android.support.v4.app.ActivityCompat
import android.view.View
import com.play.accompany.R
import com.play.accompany.base.BaseActivity
import com.play.accompany.constant.SpConstant
import com.play.accompany.utils.SPUtils
import com.play.accompany.utils.SoundManager
import com.play.accompany.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_sound_setting.*
import java.io.File

class SoundSettingActivity : BaseActivity() {
    private var mFile: File? = null
    private var mRecording = false
    private lateinit var mAnimator: ObjectAnimator

    override fun getLayout(): Int {
        return R.layout.activity_sound_setting
    }

    override fun getTag(): String {
        return "SoundSettingActivity"
    }

    override fun initViews() {
        lin_record.visibility = View.VISIBLE
        img_record.setOnClickListener {
            if (mRecording) {
                stopRecord()
            } else {
                startRecord()
            }
        }

        img_play.setOnClickListener {
            SoundManager.instance.playAudio(mFile!!)
        }
    }

    private fun startRecord() {
        mRecording = true
        addAnim()
        val dir = getExternalFilesDir("accompany")
        mFile = File(dir, SPUtils.getInstance().getString(SpConstant.MY_USER_ID) + "_" + System.currentTimeMillis() + ".wav")
        SoundManager.instance.startRecord(mFile!!,object :SoundManager.SoundListener{
            override fun onProgress(progress: Int) {
                runOnUiThread {
                    val text = progress.toString() + "S"
                    tv_time.text = text
                }
            }
            override fun onFinish() {
                runOnUiThread {
                    lin_play.visibility = View.VISIBLE
                    lin_record.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun stopRecord() {
        mRecording = false
        mAnimator.cancel()
        SoundManager.instance.stopRecord()
    }

    private fun addAnim() {
        mAnimator = ObjectAnimator.ofFloat(img_side, "rotation", -360f, 0f, 360f).setDuration(1000)
        mAnimator.repeatMode = ValueAnimator.RESTART
        mAnimator.repeatCount = ValueAnimator.INFINITE
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

    private fun checkPermisson() {
        val permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            val per = arrayOf(android.Manifest.permission.RECORD_AUDIO)
            requestPermission(per)
        } else {
            startRecord()
        }
    }

    private fun requestPermission(permission: Array<String>) {
        ActivityCompat.requestPermissions(this, permission, 999)
    }


    private fun requestData() {

    }

    private fun playAudio() {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecord()
        } else {
            ToastUtils.showCommonToast("访问失败，请打开您的麦克风")
        }
    }

}
