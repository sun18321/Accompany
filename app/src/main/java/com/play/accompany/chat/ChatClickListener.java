package com.play.accompany.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.play.accompany.R;
import com.play.accompany.bean.OrderNotifyBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;

import java.util.Locale;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

public class ChatClickListener implements RongIM.ConversationClickListener {

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {

        String userId = userInfo.getUserId();
        LogUtils.d("conversation", "userid:" + userId);
        String officialNumber = AccompanyApplication.getContext().getResources().getString(R.string.official_number);
        String tips = AccompanyApplication.getContext().getResources().getString(R.string.official_tips);
        if (TextUtils.equals(userId, officialNumber) || userId.length() < 8) {
            ToastUtils.showCommonToast(tips);
            return false;
        }
        Intent intent = new Intent(OtherConstant.CONVERSATION_ACTIVITY_RECEIVER);
        intent.putExtra(IntentConstant.INTENT_USER_ID, userId);
        intent.putExtra(IntentConstant.INTENT_CONVERSATION_RECEIVER_TYPE, OtherConstant.CONVERSATION_GO_USER);
        AccompanyApplication.getContext().sendBroadcast(intent);
        return true;
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
//                RongIM.getInstance().startPrivateChat(AccompanyApplication.getContext(), notifyBean.getId(), notifyBean.getName());

                if (RongContext.getInstance() == null) {
                    throw new ExceptionInInitializerError("RongCloud SDK not init");
                } else {
                    Uri uri = Uri.parse("rong://" + AccompanyApplication.getContext().getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase(Locale.US))
                            .appendQueryParameter("targetId", notifyBean.getId()).appendQueryParameter("title", notifyBean.getName()).build();
                    AccompanyApplication.getContext().startActivity(new Intent("android.intent.action.VIEW", uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
            return true;
        }
        return false;
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
