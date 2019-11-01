package com.play.accompany.design

import android.media.MediaPlayer

class LivingSoundPLayer private constructor() {
    private var mPLayer: MediaPlayer = MediaPlayer()
    private var mDataSource: List<String>? = null
    private var mCurrentIndex = 0

    companion object{
            val instance:LivingSoundPLayer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
                LivingSoundPLayer()
            }
        }

    fun setSource(list: List<String>) {
        mDataSource = list
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

    private fun playAudio() {
        if (mPLayer.isPlaying) {
            mPLayer.stop()
        }
        mPLayer.reset()
        mPLayer.setDataSource(mDataSource?.get(mCurrentIndex))
        mPLayer.prepareAsync()
        mPLayer.setOnPreparedListener {
            it.start()
        }
    }

}