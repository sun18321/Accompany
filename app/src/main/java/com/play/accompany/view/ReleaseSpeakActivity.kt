package com.play.accompany.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.reflect.TypeToken
import com.play.accompany.R
import com.play.accompany.base.BaseActivity
import com.play.accompany.bean.BaseDecodeBean
import com.play.accompany.bean.BaseResponse
import com.play.accompany.bean.FilterAudioBean
import com.play.accompany.bean.SpeakTabBean
import com.play.accompany.constant.IntentConstant
import com.play.accompany.constant.OtherConstant
import com.play.accompany.constant.SpConstant
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.utils.*
import com.play.accompany.utils.AppUtils.goCrop
import com.yalantis.ucrop.UCrop
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_relase_speak.*
import kotlinx.android.synthetic.main.head_select.view.*
import kotlinx.android.synthetic.main.user_top.*
import okhttp3.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.IOException
import java.net.URLEncoder

@RuntimePermissions
class ReleaseSpeakActivity : BaseActivity() {
    private var mAudioBean: FilterAudioBean? = null
    private var mFile: File? = null
    private var mHeadPath: String? = null


    override fun getLayout(): Int {
        return R.layout.activity_relase_speak
    }

    override fun getTag(): String {
        return "ReleaseSpeakActivity"
    }

    override fun initViews() {
        initToolbar("发布说说","发布"){
            checkSpeak()
        }


        getTabs()

        lin_import.setOnClickListener {
            startActivityForResult(Intent(this, FilterAudioActivity::class.java), 1)
        }

        lin_record.setOnClickListener {
            startActivityForResult(Intent(this, SoundSettingActivity::class.java)
                    .putExtra(OtherConstant.SET_SOUND_SPEAK, OtherConstant.SET_SOUND_SPEAK_CODE), 2)
        }

        img_head.setOnClickListener {
            showImageDialog()
        }
    }

    private fun showImageDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.head_select, null)
        view.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        view.tv_album.setOnClickListener {
            dialog.dismiss()
            selectAlbumWithPermissionCheck()
        }

        view.tv_camera.setOnClickListener {
            dialog.dismiss()
            selectCameraWithPermissionCheck()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun checkSpeak() {
        if (mAudioBean == null) {
            ToastUtils.showCommonToast("请先选择音频文件")
            return
        }
        val list = flowlayout.selectedList
        if (list.size == 0) {
            ToastUtils.showCommonToast("请选择至少一个标签")
            return
        }

        val tabs: StringBuilder = StringBuilder()
        for (i in list) {
            tabs.append(i)
        }
        val finalTabs = tabs.substring(0, tabs.length - 1)

        val title = edit_title.text.toString()
        if (title.isEmpty()) {
            ToastUtils.showCommonToast("请先输入标题")
            return
        }

        if (title.length > 15) {
            ToastUtils.showCommonToast("标题过长")
            return
        }

        val word = edit_detail.text.toString()
        if (word.isEmpty()) {
            ToastUtils.showCommonToast("请先输入文字泡")
            return
        }

        if (word.length > 50) {
            ToastUtils.showCommonToast("文字泡过长")
            return
        }

        if (mHeadPath == null) {
            ToastUtils.showCommonToast("请先选择封面")
            return
        }
        uploadSpeak(title, word, finalTabs)
    }

    private fun uploadSpeak(title: String, word: String, tabs: String) {
        showDialog("发布说说")

        val url = "http://39.100.96.9:7070" + "/file/saveVoice"
        val client = OkHttpClient()
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), File(mAudioBean!!.path))
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", URLEncoder.encode(mAudioBean!!.name, "UTF-8"), requestBody)
                .addFormDataPart("token", SPUtils.getInstance().getString(SpConstant.APP_TOKEN))
                .addFormDataPart("audioId:", "speak")
                .addFormDataPart("imgUrl", mHeadPath)
                .addFormDataPart("title", title)
                .addFormDataPart("content", word)
                .addFormDataPart("tabs", tabs)
                .build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogUtils.d("http","up failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val string = response.body()?.string()
                LogUtils.d("response", "string:$string")

                val baseResponse = GsonUtils.fromJson(string, BaseResponse::class.java)
                val desDecrypt = CipherUtil.desDecrypt(baseResponse.recEncode)
                val bean = GsonUtils.fromJson<BaseDecodeBean<String>>(desDecrypt, object : TypeToken<BaseDecodeBean<String>>() {}.type)
                runOnUiThread {
                    dismissDialog()

                    LogUtils.d("response","bean:$bean")

                    if (bean.code == 1) {
                        ToastUtils.showCommonToast("发布成功")
                        setResult(Activity.RESULT_OK)
                        this@ReleaseSpeakActivity.finish()
                    } else {
                        ToastUtils.showCommonToast("发布失败")
                    }
                }

            }

        })

    }

    private fun showTabs(list: List<String>) {
        val adapter = object : TagAdapter<String>(list) {
            override fun getView(parent: FlowLayout, position: Int, bean: String): View {
                val tv = LayoutInflater.from(this@ReleaseSpeakActivity).inflate(R.layout.flowlayout_text, flowlayout, false) as TextView
                tv.text = bean
                return tv
            }
        }
        flowlayout.adapter = adapter
        flowlayout.setMaxSelectCount(5)
    }


    private fun getTabs() {
        val request = AccompanyRequest()
        request.beginRequest(NetFactory.getNetRequest().netService.getSpeakTab(EncodeUtils.encodeToken()),
                object : TypeToken<BaseDecodeBean<List<SpeakTabBean>>>() {}.type, object : NetListener<List<SpeakTabBean>> {
            override fun onSuccess(t: List<SpeakTabBean>?) {
                if (t != null) {
                    showTabs(t[0].tabs)
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

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun selectAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, IntentConstant.INTENT_CODE_ALBUM)
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun selectCamera() {
        takePhoto()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onRequestPermissionsResult(requestCode, grantResults)
    }


    private fun takePhoto() {
        // 步骤一：创建存储照片的文件
        mFile = File(Environment.getExternalStorageDirectory().absolutePath + "/accompany", System.currentTimeMillis().toString() + ".png")
        if (!mFile!!.parentFile.exists()) {
            mFile!!.parentFile.mkdirs()
        }
        val uri: Uri
        LogUtils.d(tag, "file path:" + Environment.getExternalStorageDirectory().absolutePath + "/accompany")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //步骤二：Android 7.0及以上获取文件 Uri
            uri = FileProvider.getUriForFile(this, packageName + OtherConstant.FILE_PROVIDER_NAME, mFile!!)

            LogUtils.d(tag, "uri path:$uri")

        } else {
            //步骤三：获取文件Uri
            uri = Uri.fromFile(mFile)
        }
        //步骤四：调取系统拍照
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, IntentConstant.INTENT_CODE_CAMERA)
    }



    private fun setAudioData() {
        lin_audio.visibility = View.VISIBLE
        sound.setData(File(mAudioBean!!.path), mAudioBean!!.audioDuration)
        img_delete.setOnClickListener {
            sound.stopPlay()
            mAudioBean = null
            lin_audio.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LogUtils.d("result","intent:$data")

        if (data != null && resultCode == 100) {
            mAudioBean = data.getSerializableExtra(IntentConstant.INTENT_AUDIO) as FilterAudioBean
            if (mAudioBean != null) {
                setAudioData()
            }
        } else if (requestCode == IntentConstant.INTENT_CODE_ALBUM && data != null) {
            goCrop(data.data, this)
        } else if (requestCode == IntentConstant.INTENT_CODE_CAMERA) {
            if (mFile != null && mFile!!.exists()) {
                goCrop(FileProvider.getUriForFile(this, packageName + OtherConstant.FILE_PROVIDER_NAME, mFile!!),this)
            }
        } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
            val output = UCrop.getOutput(data)
            val path = AppUtils.getImagePath(output)
            val image = StringUtils.imageToBase64(path)
            val sb = StringBuilder()
            sb.append(OtherConstant.IMAGE_HEAD)
            sb.append(image)
            mHeadPath = sb.toString()

            GlideUtils.commonLoad(this, output, img_head)
        }
    }
}
