package com.play.accompany.chat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;

import com.play.accompany.bean.MainReceiverMessage;
import com.play.accompany.bean.MessageBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.NotificationUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.view.AccompanyApplication;


import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

public class MessageReceiverListener implements RongIMClient.OnReceiveMessageListener {
    @Override
    public boolean onReceived(Message message, int i) {


        LogUtils.d("message", "新的消息:" + message.getConversationType());
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


        if (TextUtils.equals(message.getSenderUserId(), OtherConstant.OFFICE_NUMBER)) {
            //开启通知权限
                if (message.getContent() instanceof TextMessage) {
                    LogUtils.d("message", "thread:" + Thread.currentThread().getName());

                    TextMessage content = (TextMessage) message.getContent();
                    String extra = content.getExtra();
                    LogUtils.d("message", "extra:" + extra);
                    MessageBean bean = GsonUtils.fromJson(extra, MessageBean.class);
                    if (bean != null) {
                        LogUtils.d("message", "bean:" + bean);
                        String push = bean.getPush();
                        LogUtils.d("message", "push:" + push);

                        //通过push是否为空来弹通知
                        if (!TextUtils.isEmpty(push)) {
                            if (SPUtils.getInstance().getBoolean(SpConstant.ACCEPT_NEW_NOTICE)) {
                                createNotification(bean.getType(), push);
                            }
                        }
                        String chat = bean.getChat();
                        if (!TextUtils.isEmpty(chat)) {
                            insertMessage(bean.getTargetId(), chat);
                        }
                        int num = bean.getOrderNum();
                        if (num > 0) {
                            Intent intent = new Intent(OtherConstant.FILTER_MAIN_RECEIVER);
                            MainReceiverMessage mainReceiverMessage = new MainReceiverMessage();
                            mainReceiverMessage.setMessageType(MainReceiverMessage.TYPE_REMAIN);
                            mainReceiverMessage.setRemainMessage(num);
                            intent.putExtra(OtherConstant.MAIN_RECEIVER, mainReceiverMessage);
                            AccompanyApplication.getContext().sendBroadcast(intent);
                        }

                        //为了满足刷新聊天界面的"接单"按钮，真是个傻逼需求
                        if (bean.getType() == 6) {
                            Intent intent = new Intent(OtherConstant.CONVERSATION_ACTIVITY_RECEIVER);
                            intent.putExtra(IntentConstant.INTENT_CONVERSATION_RECEIVER_TYPE, OtherConstant.CONVERSATION_SHOW_ACCEPT);
                            intent.putExtra(IntentConstant.INTENT_USER_ID, bean.getTargetId());
                            AccompanyApplication.getContext().sendBroadcast(intent);
                        }

                        return true;
                    } else {
                        LogUtils.d("message", "bean is null");
                    }
                }
        }

        //收到提前的客户回馈
        if (message.getContent() instanceof OrderResponseMessage) {
            LogUtils.d("message","order message");
            OrderResponseMessage content = (OrderResponseMessage) message.getContent();
            String response = content.getResponse();
            String uid = content.getUid();
            String tip = content.getContent();
            String sendId = content.getSendId();
            Intent intent = new Intent(OtherConstant.CONVERSATION_ACTIVITY_RECEIVER);
            intent.putExtra(IntentConstant.INTENT_CONVERSATION_RECEIVER_TYPE, OtherConstant.CONVERSATION_NOTIFY_MASTER);
            intent.putExtra(IntentConstant.INTENT_ORDER_UID, uid);
            intent.putExtra(IntentConstant.INTENT_ORDER_RESPONSE, response);
            intent.putExtra(IntentConstant.INTENT_ORDER_CONTENT, tip);
            intent.putExtra(IntentConstant.INTENT_ORDER_SEND_ID, sendId);
            AccompanyApplication.getContext().sendBroadcast(intent);
        }

        if (message.getContent() instanceof TextMessage) {
            TextMessage txtMessage = (TextMessage) message.getContent();
            String content = txtMessage.getContent();

            LogUtils.d("message", "receive text message content is text");
            LogUtils.d("message", "tostring:" + message.toString());
            LogUtils.d("message", "content:" + content);
        }

        return false;
    }

    private void createNotification(int type, String text) {
        LogUtils.d("message", "notify start");
        NotificationManager manager = (NotificationManager) AccompanyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(OtherConstant.NOTIFY_CHANNEL, "新消息通知", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);

                manager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = NotificationUtils.createNotification(type, text);
            Notification build = builder.build();
            build.flags = Notification.FLAG_AUTO_CANCEL;
            int id = (int) System.currentTimeMillis();

            LogUtils.d("message","notify end");

            manager.notify(id, build);
        }
    }

    private void insertMessage(String id, String text) {
        Message.ReceivedStatus status = new Message.ReceivedStatus(1);
        InformationNotificationMessage notificationContent = InformationNotificationMessage.obtain(text);
        RongIM.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE, id, id, status, notificationContent, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }
}
