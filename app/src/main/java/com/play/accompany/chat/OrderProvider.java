package com.play.accompany.chat;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;

import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

@ProviderTag(messageContent = OrderMessage.class)
public class OrderProvider extends IContainerItemProvider.MessageProvider<OrderMessage> {
    public static final String mStateAccept = "accompany.accept";
    private final String mStateReject = "accompany.reject";
    private final String mStateTimeOut = "accompany.time.out";
    private String mTargetId = "";
    private SparseArray<String> mSparseArray = new SparseArray<>();
    private final int mTimeOut = 10 * 60 * 1000;


    @Override
    public void bindView(View view, int i, OrderMessage orderMessage, UIMessage uiMessage) {
        mTargetId = orderMessage.getSendId();
        displayMessage(view, orderMessage, uiMessage);

    }

    @Override
    public Spannable getContentSummary(OrderMessage orderMessage) {
        int orderType = orderMessage.getOrderType();
        if (orderType == OrderMessage.ORDER_EARLY_START) {
            return new SpannableString("大神请求提前开始服务");
        } else {
            return new SpannableString("大神请求提前结束服务");
        }
    }

    @Override
    public void onItemClick(View view, int i, OrderMessage orderMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_operation, viewGroup, false);
        OrderHolder holder = new OrderHolder();
        holder.tvTitle = view.findViewById(R.id.tv_title);
        holder.tvNegative = view.findViewById(R.id.tv_negative);
        holder.tvPositive = view.findViewById(R.id.tv_positive);
        holder.tvResult = view.findViewById(R.id.tv_result);
        holder.linOperation = view.findViewById(R.id.lin_operation);
        view.setTag(holder);
        return view;
    }

    private void displayMessage(View view, final OrderMessage orderMessage, final UIMessage uiMessage) {
        final OrderHolder holder = (OrderHolder) view.getTag();
        Context context = view.getContext();
        String targetId = orderMessage.getTargetId();

        //是否为玩家
        int orderType = orderMessage.getOrderType();
        final long time = orderMessage.getSendTime();
        LogUtils.d("time", "time:" + time);

        if (TextUtils.equals(targetId, SPUtils.getInstance().getString(SpConstant.MY_USER_ID))) {
            holder.tvResult.setVisibility(View.GONE);
            if (orderType == OrderMessage.ORDER_EARLY_START) {
                holder.tvTitle.setText("大神请求提前开始服务");
                holder.tvPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAgree(time, holder, uiMessage);
                    }
                });

                holder.tvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doReject(time, holder, uiMessage);
                    }
                });

            } else {
                holder.tvTitle.setText("大神请求提前结束服务");

                holder.tvPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAgree(time,holder, uiMessage);
                    }
                });

                holder.tvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doReject(time,holder, uiMessage);
                    }
                });
            }
        } else {
            if (orderType == OrderMessage.ORDER_EARLY_START) {
                holder.tvTitle.setText("大神请求提前开始服务");
            } else {
                holder.tvTitle.setText("大神请求提前结束服务");
            }
            LogUtils.d("message","i am master");
            holder.linOperation.setVisibility(View.GONE);
            holder.tvResult.setText("等待玩家操作");
        }

        String extra = mSparseArray.get(uiMessage.getMessageId());
        if (TextUtils.isEmpty(extra)) {
            readData(uiMessage, holder,time);
        } else {
            holder.linOperation.setVisibility(View.GONE);
            holder.tvResult.setVisibility(View.VISIBLE);
            if (TextUtils.equals(mStateAccept, extra)) {
                holder.tvResult.setText("已同意");
            } else if (TextUtils.equals(mStateReject, extra)) {
                holder.tvResult.setText("已拒绝");
            } else if (TextUtils.equals(mStateTimeOut, extra)) {
                holder.tvResult.setText("超时未操作");
            }
        }

    }

    private void doExtra(UIMessage message, String extra, OrderHolder holder, long time) {
        //是否已操作
        if (TextUtils.isEmpty(extra)) {
            if (System.currentTimeMillis() - time > mTimeOut) {
                holder.linOperation.setVisibility(View.GONE);
                holder.tvResult.setVisibility(View.VISIBLE);
                holder.tvResult.setText("超时未处理");
                mSparseArray.put(message.getMessageId(), mStateTimeOut);
                saveExtra(message.getMessageId(), mStateTimeOut, message.getUId(), null);
            }
        } else {
            mSparseArray.put(message.getMessageId(), extra);
            holder.linOperation.setVisibility(View.GONE);
            holder.tvResult.setVisibility(View.VISIBLE);
            if (TextUtils.equals(mStateAccept, extra)) {
                holder.tvResult.setText("已同意");
            } else if(TextUtils.equals(mStateReject,extra)){
                holder.tvResult.setText("已拒绝");
            } else if (TextUtils.equals(mStateTimeOut, extra)) {
                holder.tvResult.setText("超时未处理");
            }
        }
    }

    private void readData(final UIMessage uiMessage, final OrderHolder holder, final long time) {
        RongIMClient.getInstance().getMessage(uiMessage.getMessageId(), new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
//                ToastUtils.showCommonToast("读取成功");
                String extra = message.getExtra();
                LogUtils.d("message", message.getMessageId() + "----" + extra + "---" + message.getUId());
                doExtra(uiMessage, extra, holder, time);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
//                ToastUtils.showCommonToast("读取失败");

            }
        });
    }

    private void doAgree(long time, OrderHolder holder, UIMessage uiMessage) {
        if (checkTime(time, holder,uiMessage)) {
            return;
        }

        ToastUtils.showCommonToast("同意");
        holder.linOperation.setVisibility(View.GONE);
        holder.tvResult.setVisibility(View.VISIBLE);
        holder.tvResult.setText("已同意");

        saveExtra(uiMessage.getMessageId(), mStateAccept, uiMessage.getUId(), "玩家同意了您的请求");
        sendAgreeBroadcast();
    }

    private void doReject(long time, OrderHolder holder, UIMessage uiMessage) {
        if (checkTime(time, holder,uiMessage)) {
            return;
        }

        ToastUtils.showCommonToast("拒绝");
        holder.linOperation.setVisibility(View.GONE);
        holder.tvResult.setVisibility(View.VISIBLE);
        holder.tvResult.setText("已拒绝");

        saveExtra(uiMessage.getMessageId(), mStateReject, uiMessage.getUId(), "玩家拒绝了您的请求");
    }

    private boolean checkTime(long time, OrderHolder holder, UIMessage message) {
        if (System.currentTimeMillis() - time > mTimeOut) {
            mSparseArray.put(message.getMessageId(), mStateTimeOut);
            saveExtra(message.getMessageId(), mStateTimeOut, message.getUId(), null);

            ToastUtils.showCommonToast("操作已超时");
            holder.linOperation.setVisibility(View.GONE);
            holder.tvResult.setText("超时未操作");
            return true;
        } else {
            return false;
        }
    }

    private void saveExtra(int messageId, String extra, String uid,String content) {
        if (!TextUtils.equals(extra, mStateTimeOut)) {
            sendResponse(uid, extra, content);
        }

        RongIMClient.getInstance().setMessageExtra(messageId, extra, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
//                ToastUtils.showCommonToast("保存成功");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
//                ToastUtils.showCommonToast("保存失败");
            }
        });
    }

    private void sendResponse(String uid, String response, String tipString) {
        if (TextUtils.isEmpty(mTargetId)) {
            return;
        }
        OrderResponseMessage content = new OrderResponseMessage(uid, response, tipString, SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        Message message = Message.obtain(mTargetId, Conversation.ConversationType.PRIVATE, content);
        RongIM.getInstance().sendMessage(message, "提前", null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
//                ToastUtils.showCommonToast("response success");
                LogUtils.d("message", "response success");
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtils.showCommonToast("response failed");
                LogUtils.d("message", "response failed");
            }
        });
    }

    private void sendAgreeBroadcast() {
        Intent intent = new Intent(OtherConstant.CONVERSATION_ACTIVITY_RECEIVER);
        intent.putExtra(IntentConstant.INTENT_CONVERSATION_RECEIVER_TYPE, OtherConstant.CONVERSATION_AGREE_ADVANCE);
        AccompanyApplication.getContext().sendBroadcast(intent);
    }

    class OrderHolder {
        TextView tvTitle;
        TextView tvNegative;
        TextView tvPositive;
        TextView tvResult;
        LinearLayout linOperation;
    }
}
