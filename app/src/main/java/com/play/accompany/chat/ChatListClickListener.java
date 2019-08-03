package com.play.accompany.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.play.accompany.constant.OtherConstant;
import com.play.accompany.utils.LogUtils;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;

public class ChatListClickListener implements RongIM.ConversationListBehaviorListener {
    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        Conversation.ConversationType type = uiConversation.getConversationType();
        String senderId = uiConversation.getConversationSenderId();

        LogUtils.d("rong", "id:" + senderId);

        boolean top = uiConversation.isTop();
        if (top && TextUtils.isEmpty(senderId)) {
            return true;
        }
        if (type == Conversation.ConversationType.SYSTEM || TextUtils.equals(senderId, OtherConstant.OFFICE_NUMBER)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }
}
