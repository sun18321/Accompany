package com.play.accompany.design

import com.play.accompany.bean.ResponseSpeakBean

data class SpeakCallbackInfo(val type: Int, val info: ResponseSpeakBean?)

const val SPEAK_TYPE_DELETE = 1
const val SPEAK_TYPE_CHANGE_PAUSE =2
const val SPEAK_TYPE_SHOW_HIDE = 3
const val SPEAK_TYPE_HEAD_CLICK = 4

