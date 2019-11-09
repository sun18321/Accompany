package com.play.accompany.view

import android.content.Intent
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.play.accompany.R
import com.play.accompany.adapter.AudioFileAdapter
import com.play.accompany.base.BaseActivity
import com.play.accompany.bean.FilterAudioBean
import com.play.accompany.constant.IntentConstant
import com.play.accompany.constant.OtherConstant.MAX_AUDIO_DURATION
import com.play.accompany.constant.OtherConstant.MAX_AUDIO_SIZE
import com.play.accompany.utils.AppUtils
import com.play.accompany.utils.LogUtils
import com.play.accompany.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_filter_audio.*

class FilterAudioActivity : BaseActivity() {
    private var mSelectAudio: FilterAudioBean? = null
    private val mAudioList = mutableListOf<FilterAudioBean>()
    override fun getLayout(): Int {
        return R.layout.activity_filter_audio
    }

    override fun getTag(): String {
        return "FilterAudioActivity"
    }

    override fun initViews() {

        initToolbar("选择音频","确定"){
            if (mSelectAudio == null) {
                ToastUtils.showCommonToast(resources.getString(R.string.audio_first))
            }else{
                confirmAudio()
            }
        }

        getAudioSource()
//        showDialog("扫描文件中.....")
    }

    private fun getAudioSource() {
        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        while (cursor?.moveToNext()!!) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

            val bean = FilterAudioBean(name, path, size, AppUtils.FormatFileSize(size), duration/1000, AppUtils.timeParse(duration.toLong()), false)
            mAudioList.add(bean)
        }

        val internalCursor = contentResolver.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null, null)
        while (internalCursor?.moveToNext()!!) {
            val name = internalCursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val path = internalCursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val size = internalCursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
            val duration = internalCursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

            val bean = FilterAudioBean(name, path, size, AppUtils.FormatFileSize(size), duration/1000, AppUtils.timeParse(duration.toLong()), false)
            mAudioList.add(bean)
        }

        cursor.close()
        internalCursor.close()

        displayAudio()
    }

    private fun confirmAudio() {
        if (mSelectAudio!!.sizeLong > MAX_AUDIO_SIZE) {
            ToastUtils.showCommonToast("选择音频文件不得大于3M")
            return
        }

        if (mSelectAudio!!.audioDuration > MAX_AUDIO_DURATION) {
            ToastUtils.showCommonToast("选择音频文件不得大于60S")
            return
        }

        backSound()
    }

    private fun backSound() {
        setResult(100, Intent().putExtra(IntentConstant.INTENT_AUDIO,mSelectAudio))
        this.finish()
    }

//    private fun upAudio() {
//        LogUtils.d("http","start upload")
//
//        val url = "http://39.100.96.9:7070" + "/file/saveVoice"
//        val client = OkHttpClient()
//        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), File(mSelectAudio!!.path))
//        val body = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("name", mSelectAudio!!.name, requestBody)
//                .addFormDataPart("token", SPUtils.getInstance().getString(SpConstant.APP_TOKEN))
//                .addFormDataPart("audioId:", "home")
//                .addFormDataPart("imgUrl", img)
//                .addFormDataPart("title", "标题")
//                .addFormDataPart("content","文字泡")
//                .addFormDataPart("tabs","1,2,3,4")
//                .build()
//        val request = Request.Builder().url(url).post(body).build()
//        client.newCall(request).enqueue(object :Callback{
//            override fun onFailure(call: Call, e: IOException) {
//                LogUtils.d("http","up failed")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                LogUtils.d("http", "up success")
//            }
//
//        })
//    }

    private fun displayAudio() {
        val iterator = mAudioList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.audioDuration < 5 || next.sizeLong > 3 * 1024 * 1024) {
                iterator.remove()
            }
        }

        mAudioList.sortBy { it.name }

        LogUtils.d("sound", "sort over")

        dismissDialog()
        lin_scan.visibility = View.GONE
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recycler.adapter = AudioFileAdapter(mAudioList){
            mSelectAudio = it
        }

    }
}
