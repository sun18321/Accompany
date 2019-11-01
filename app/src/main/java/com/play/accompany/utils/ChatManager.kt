package com.play.accompany.utils

import android.content.Context
import io.rong.imkit.RongIM

class ChatManager private constructor() {

    companion object{
        val instance:ChatManager by lazy (mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ChatManager()
        }
    }

    fun startChat(context: Context, userId: String, userName: String) {
        RongIM.getInstance().startPrivateChat(context, userId, userName)
    }
}
