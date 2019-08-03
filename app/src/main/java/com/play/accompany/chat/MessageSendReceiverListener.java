package com.play.accompany.chat;

import com.play.accompany.utils.EventUtils;
import com.umeng.analytics.MobclickAgent;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

public class MessageSendReceiverListener implements RongIM.OnSendMessageListener {
    @Override
    public Message onSend(Message message) {
        return message;
    }

    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        MessageContent content = message.getContent();
        if (content instanceof TextMessage) {
            EventUtils.getInstance().upChat(message.getTargetId(), ((TextMessage) content).getContent());
        }
        return false;
    }
}
