package com.play.accompany

class Computer {

    private var mBoard: String? = null
    private var mDisplay: String? = null
    private var mOS: String? = null

    fun getmBoard(): String? {
        return mBoard
    }

    fun setmBoard(mBoard: String?) {
        this.mBoard = mBoard
    }

    fun getmDisplay(): String? {
        return mDisplay
    }

    fun setmDisplay(mDisplay: String?) {
        this.mDisplay = mDisplay
    }

    fun getmOS(): String? {
        return mOS
    }

    fun setmOS() {
        this.mOS = mOS
    }


    class Builder {

        private var mBoard: String? = null
        private var mDisplay: String? = null
        private val mOS: String? = null

        fun setBoard(board: String): Builder {
            this.mBoard = board
            return this
        }

        fun setDisplay(display: String): Builder {
            this.mDisplay = display
            return this
        }

        fun setOs(): Builder {
            return this
        }

        /**
         * 组装产品
         */
        private fun construct(computer: Computer) {
            computer.setmBoard(mBoard)
            computer.setmDisplay(mDisplay)
            computer.setmOS()
        }

        fun create(): Computer {
            val computer = Computer()
            construct(computer)
            return computer
        }
    }
}
