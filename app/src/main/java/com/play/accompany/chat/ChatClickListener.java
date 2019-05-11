package com.play.accompany.chat;

import android.content.Context;
import android.view.View;

import com.play.accompany.bean.OrderNotifyBean;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.view.AccompanyApplication;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

public class ChatClickListener implements RongIM.ConversationClickListener {
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        if (message == null) {
            return false;
        }
        Conversation.ConversationType conversationType = message.getConversationType();
        if (conversationType == Conversation.ConversationType.SYSTEM) {
            if ((message.getContent() instanceof TextMessage)) {
                TextMessage content = (TextMessage) message.getContent();
                String extra = content.getExtra();
                OrderNotifyBean notifyBean = GsonUtils.fromJson(extra, OrderNotifyBean.class);
                if (notifyBean == null) {
                    return false;
                }
                RongIM.getInstance().startPrivateChat(AccompanyApplication.getContext(), notifyBean.getId(), notifyBean.getName());
            }
        }
        return true;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s, Message message) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
}
