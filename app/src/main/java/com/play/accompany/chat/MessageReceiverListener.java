package com.play.accompany.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.play.accompany.bean.OrderNotifyBean;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.NotificationUtils;
import com.play.accompany.view.AccompanyApplication;


import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

public class MessageReceiverListener implements RongIMClient.OnReceiveMessageListener {
    @Override
    public boolean onReceived(Message message, int i) {

//
        LogUtils.d("conversation", "新的消息:" + message.getConversationType());
        LogUtils.d("conversation", "extra:" + message.getExtra());
//
//        LogUtils.d("conversation", "message:" + message.toString());
//
//        TextMessage content = (TextMessage) message.getContent();
//
//        if (content instanceof TextMessage) {
//            TextMessage textMessage = content;
//            String display = textMessage.getContent();
//            String extra = textMessage.getExtra();
//            LogUtils.d("conversation", "display:" + display + "--" + "extra:" + extra);
//
//        }else {
//            LogUtils.d("conversation", "content is not text");
//        }

        int id = 0;
        if (message.getConversationType() == Conversation.ConversationType.SYSTEM) {
            if (message.getContent() instanceof TextMessage) {
                TextMessage content = (TextMessage) message.getContent();
                String extra = content.getExtra();
                OrderNotifyBean bean = GsonUtils.fromJson(extra, OrderNotifyBean.class);
                if (bean != null) {
                    long num = Long.parseLong(bean.getId());
                    id = (int) num;
                    LogUtils.d("conversation", "id:" + id);
                }
            }
            NotificationManager manager = (NotificationManager) AccompanyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = NotificationUtils.createNotification();
            Notification build = builder.build();
            build.flags = Notification.FLAG_AUTO_CANCEL;
            manager.notify(id, build);
        }
        return false;
    }
}
