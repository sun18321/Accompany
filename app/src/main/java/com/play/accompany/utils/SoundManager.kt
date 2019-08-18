package com.play.accompany.utils

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import com.play.accompany.view.AccompanyApplication
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.concurrent.timerTask

class SoundManager private constructor(){
    private val mMaxLength = 15
    private lateinit var mListener: SoundListener
    private lateinit var mFile: File
    private var mIsRecord = false
    private val mRecordRate = 44100
    private var mProgress = 0
    private lateinit var mTimer: Timer

    companion object {
        val instance: SoundManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            SoundManager()
        }
    }

    public fun startRecord(filePath: File) {
        mFile = filePath
        mIsRecord = true
        AccompanyRecord().start()
        mTimer = Timer()
        val task = timerTask {
            mProgress++
            mListener.onProgress(mProgress)
        }
        mTimer.schedule(task, 1000, 1000)
    }

    public fun stopRecord() {
        mIsRecord = false
        mListener.onFinish()
        mTimer.cancel()
    }

    public fun setListener(listener: SoundListener) {
        mListener = listener
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


    internal inner class AccompanyRecord : Thread() {

        override fun run() {
            super.run()

            val bufferSize = AudioRecord.getMinBufferSize(mRecordRate, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
            val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, mRecordRate, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
            audioRecord.startRecording()

            val byteArrayStream = ByteArrayOutputStream()
            val outputStream = FileOutputStream(mFile)
            val byteArray = ByteArray(bufferSize)
            while (mIsRecord) {
                val read = audioRecord.read(byteArray, 0, bufferSize)
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
        }
    }

    public interface SoundListener{
        fun onProgress(progress: Int)

        fun onFinish()
    }

}

