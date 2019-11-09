package com.play.accompany.design

import com.play.accompany.Computer

class PlayerInfo {
    private var mType: Int? = null
    private var mProgress: Int? = null
    private var mTotalLength: Int? = null

    fun setType(type: Int) {
        mType = type
    }

    fun setProgress(progress: Int) {
        mProgress = progress
    }

    fun setLength(length: Int) {
        mTotalLength = length
    }

    fun getType() = mType

    fun getProgress() = mProgress

    fun getTotalLength() = mTotalLength

    class Builder {
        private var buildType: Int? = null
        private var buildProgress: Int? = null
        private var buildTotalLength: Int? = null

        fun type(type: Int): Builder {
            buildType = type
            return this
        }

        fun progress(progress: Int): Builder {
            buildProgress = progress
            return this
        }

        fun totalLength(length: Int): Builder {
            buildTotalLength = length
            return this
        }

        fun build(): PlayerInfo {
            val info = PlayerInfo()
            buildType?.run {
                info.setType(this)
            }

            buildProgress?.run {
                info.setProgress(this)
            }

            buildTotalLength?.run {
                info.setLength(this)
            }
            return info
        }
    }

}


