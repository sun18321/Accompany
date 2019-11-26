package com.play.accompany.design

import android.media.MediaPlayer
import com.play.accompany.bean.ResponseSpeakBean
import com.play.accompany.constant.*
import com.play.accompany.utils.LogUtils
import java.util.*
import kotlin.concurrent.timerTask

class  LivingSoundPLayer{
    private val mPLayer = MediaPlayer()
    private var mDataSource: List<ResponseSpeakBean>? = null
    private var mCurrentIndex = 0
    private var mCallback: ((PlayerInfo) -> Unit)? = null
    private var mPause = false
    private var mTimer: Timer? = null

    fun setSource(list: List<ResponseSpeakBean>, startPosition: Int, callback: (PlayerInfo) -> Unit) {
        mCurrentIndex = startPosition
        mDataSource = list
        mCallback = callback
        playAudio()
    }

    fun playNext() {
        mCurrentIndex++
        if (mCurrentIndex < mDataSource!!.size) {
            playAudio()
        }
    }

    fun playPrevious() {
        mCurrentIndex--
        if (mCurrentIndex >= 0) {
            playAudio()
        }
    }

    fun pause() {
        mPause = true
        if (mPLayer.isPlaying) {
            mPLayer.pause()
        }
        mCallback?.let { it(PlayerInfo.Builder().type(PLAYER_STATUS_PAUSE).build()) }
        mTimer?.cancel()
        mTimer = null
    }

    fun destroy() {
        if (mPLayer.isPlaying) {
            mPLayer.stop()
        }
        mPause = false
        mPLayer.release()
        mCallback = null
        mTimer?.cancel()
        mTimer = null
    }

    fun reStart() {
        if (mPause) {
            LogUtils.d("player","playing status:${mPLayer.isPlaying}")
            mPause = false
            if (mPLayer.currentPosition > 0) {
                mPLayer.start()
                mCallback?.let { it(PlayerInfo.Builder().type(PLAYER_STATUS_START).build()) }
                startTimer()
            } else {
                playAudio()
            }
        }
    }


    private fun playAudio() {
        mCallback?.let { it(PlayerInfo.Builder().type(PLAYER_STATUS_LOADING).build()) }
        if (mPLayer.isPlaying) {
            mPLayer.stop()
        }
        mPLayer.reset()
        mPLayer.isLooping = true
        mPLayer.setDataSource(mDataSource?.get(mCurrentIndex)?.audioUrl)
        mPLayer.prepareAsync()
        mPLayer.setOnPreparedListener {
            LogUtils.d("player", "player ready pause:$mPause")
            if (!mPause) {
                it.start()
                mCallback?.let { callback -> callback(PlayerInfo.Builder().type(PLAYER_STATUS_START).build()) }
                startTimer()
            }
        }
        mPLayer.setOnErrorListener { _, _, _ ->
            if (mPLayer.isPlaying) {
                mCallback?.let {callback -> callback(PlayerInfo.Builder().type(PLAYER_STATUS_ERROR).build()) }
            }
            true
        }
    }

    private fun startTimer() {
        if (mTimer == null) {
            mTimer = Timer()
            mTimer?.schedule(timerTask {
                val progress = (mPLayer.currentPosition)
                val total = (mPLayer.duration)
                mCallback?.let { it(PlayerInfo.Builder().type(PLAYER_STATUS_PLAYING).progress(progress).totalLength(total).build()) }
            }, 0, 200)
        }
    }

    fun getPause() = mPause
}