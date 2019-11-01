package com.play.accompany.utils

import android.media.*
import android.text.TextUtils
import com.play.accompany.constant.AppConstant
import com.play.accompany.constant.SpConstant
import com.play.accompany.view.AccompanyApplication
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timerTask

class SoundManager private constructor(){
    private var mMaxLength = 16
    private val mMinLength = 4
    private lateinit var mListener: SoundListener
    private lateinit var mFile: File
    private var mIsRecord = false
    private val mRecordRate = 44100
    private var mProgress = 0
    private  var mTimer: Timer? = null
    private var mAudioRecord: AudioRecord? = null
    private var mPlayer: MediaPlayer? = null

    companion object {
        val instance: SoundManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            SoundManager()
        }
    }

    fun startRecord(filePath: File, maxLength: Int, listener: SoundListener) {
        mMaxLength = maxLength
        mListener = listener
        mFile = filePath
        mIsRecord = true
        AccompanyRecord().start()
        mTimer = Timer()
        val task = timerTask {
            if (mProgress >= mMaxLength) {
                stopRecord()
//                mListener.onFinish()
            } else {
                mListener.onProgress(mProgress)
                mProgress++
            }
        }
        mTimer!!.schedule(task, 0, 1000)
    }

     fun stopRecord() {
//         mProgress = 0
         mIsRecord = false
         mTimer?.cancel()
         mAudioRecord?.stop()
         mAudioRecord?.release()
         mAudioRecord = null
    }

    private fun getWavHeader(totalAudioLen: Long): ByteArray {
        val mChannels = 1
        val totalDataLen = totalAudioLen + 36
        val longSampleRate = mRecordRate
        val byteRate = mRecordRate * 2 * mChannels.toLong()

        val header = ByteArray(44)
        header[0] = 'R'.toByte()  // RIFF/WAVE header
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte()  // 'fmt ' chunk
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1  // format = 1
        header[21] = 0
        header[22] = mChannels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * mChannels).toByte()  // block align
        header[33] = 0
        header[34] = 16  // bits per sample
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()

        return header
    }

    private fun playExistAudio(filePath: File, listener: () -> Unit) {
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
        if (mPlayer != null && mPlayer!!.isPlaying) {
            return
        }
        mPlayer!!.reset()
        mPlayer!!.setDataSource(filePath.absolutePath)
        mPlayer!!.prepareAsync()
        mPlayer!!.setOnCompletionListener {
            LogUtils.d("sound","media complete")
            listener()
        }

        // 返回false会调用oncomplete true则不会
        mPlayer!!.setOnErrorListener { _, _, _ ->
            filePath.delete()
            false
        }

        mPlayer!!.setOnPreparedListener {
            it.start()
        }
    }

    fun stopPlay() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.stop()
        }
    }

    fun playAudio(file: File, listener: () -> Unit) {
        if (file.exists()) {
            playExistAudio(file, listener)
        }
    }


    fun playAudio(url: String, listener: () -> Unit) {
        val name = url2name(url)
        val dir = AccompanyApplication.getContext().getExternalFilesDir(AppConstant.SOUND_FILE)
        val file = File(dir, name)
        if (file.exists() && file.length() != 0L) {
            playExistAudio(file, listener)
        } else {
            if (file.length() == 0L) {
                LogUtils.d("sound", "delete 0 size file")
                file.delete()
            }
            downloadSound(file, url) {
                playExistAudio(file, listener)
            }
        }
    }

    fun destroy() {
        stopRecord()

        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer?.stop()
        }
        mPlayer?.release()
        mPlayer = null
    }

    internal inner class AccompanyRecord : Thread() {
        override fun run() {
            super.run()

            val bufferSize = AudioRecord.getMinBufferSize(mRecordRate, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
            mAudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, mRecordRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
            mAudioRecord!!.startRecording()
            val byteArrayStream = ByteArrayOutputStream()
            val outputStream = FileOutputStream(mFile)
            val byteArray = ByteArray(bufferSize)
            while (mIsRecord) {
                val read = mAudioRecord!!.read(byteArray, 0, bufferSize)
                if (read > 0) {
                    byteArrayStream.write(byteArray, 0, bufferSize)
                }

            }
            val buffer = byteArrayStream.toByteArray()
            outputStream.write(getWavHeader(buffer.size.toLong()))
            outputStream.write(buffer)
            byteArrayStream.flush()
            byteArrayStream.close()
            outputStream.flush()
            outputStream.close()

            LogUtils.d("sound", "end progress:$mProgress")

            if (mProgress < mMinLength) {
                if (mFile != null && mFile.exists()) {
                    mFile.delete()
                }
                mListener.onTooShort()
            } else {
                mListener.onFinish()
            }
            mProgress = 0
        }
    }

    fun uploadSound(file: File, name: String, length: String, listener: UploadListener) {
        val url = "http://39.100.96.9:7070" + "/file/upAudio"
        val client = OkHttpClient()
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val body = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(name, "", requestBody)
                .addFormDataPart("token", SPUtils.getInstance().getString(SpConstant.APP_TOKEN))
                .addFormDataPart("audioLen", length).build()
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogUtils.d("sound", "上传失败")
                listener.onFailed()
            }

            override fun onResponse(call: Call, response: Response) {
                LogUtils.d("sound", "thead:" + Thread.currentThread() + "上传成功")
                listener.onSuccess()
            }

        })

    }

    private fun downloadSound(file: File, url: String, listener: () -> Unit) {
        LogUtils.d("sound","start download")
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    var len = 0
                    val buff: ByteArray = ByteArray(1024)
                    val outputStream = FileOutputStream(file)
                    val inputStream = response.body()!!.byteStream()
                    while (((inputStream.read(buff)).also { len = it }) != -1) {
                        outputStream.write(buff, 0, len)
                    }
                    outputStream.flush()
                    inputStream.close()
                    outputStream.close()
                    listener()
                    LogUtils.d("sound","download complete")
                } catch (e: Exception) {

                }finally {

                }
            }
        })
    }

    private fun url2name(url: String): String {
        if (TextUtils.isEmpty(url)) {
            return ""
        }
        val index = url.lastIndexOf("/")
        if (index == -1) {
            return ""
        }
        return url.substring(index + 1, url.length) + ".wav"
    }


    public interface SoundListener{
        fun onProgress(progress: Int)

        fun onFinish()

        fun onTooShort()
    }

    interface UploadListener{
        fun onFailed()

        fun onSuccess()
    }
}

