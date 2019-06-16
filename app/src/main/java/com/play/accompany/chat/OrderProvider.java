package com.play.accompany.chat;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
    public static final String mStateReject = "accompany.reject";
    private String mTargetId = "";

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
        if (TextUtils.equals(targetId, SPUtils.getInstance().getString(SpConstant.MY_USER_ID))) {
            holder.tvResult.setVisibility(View.GONE);
            int orderType = orderMessage.getOrderType();
            if (orderType == OrderMessage.ORDER_EARLY_START) {
                holder.tvTitle.setText("大神请求提前开始服务");
                holder.tvPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAgree(holder,uiMessage);
                    }
                });

                holder.tvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doReject(holder, uiMessage);
                    }
                });

            } else {
                holder.tvTitle.setText("大神请求提前结束服务");

                holder.tvPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAgree(holder, uiMessage);
                    }
                });

                holder.tvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doReject(holder, uiMessage);
                    }
                });
            }
        } else {
            LogUtils.d("message","i am master");
            holder.linOperation.setVisibility(View.GONE);
            holder.tvResult.setText("等待玩家操作");
        }


        RongIMClient.getInstance().getMessage(uiMessage.getMessageId(), new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                ToastUtils.showCommonToast("读取成功");
                String extra = message.getExtra();
                LogUtils.d("message", message.getMessageId() + "----" + extra + "---" + message.getUId());

                //是否已操作
                if (TextUtils.isEmpty(extra)) {
//                    if (TextUtils.equals(mTargetId,SPUtils.getInstance().getString())) {
//                    }
//                    holder.tvResult.setVisibility(View.GONE);
//                    holder.linOperation.setVisibility(View.VISIBLE);  n
                }else {
                    holder.linOperation.setVisibility(View.GONE);
                    holder.tvResult.setVisibility(View.VISIBLE);
                    if (TextUtils.equals(mStateAccept, extra)) {
                        holder.tvResult.setText("已同意");
                    } else {
                        holder.tvResult.setText("已拒绝");
                    }
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.showCommonToast("读取失败");

            }
        });

    }

    private void doAgree(OrderHolder holder, UIMessage uiMessage) {
        ToastUtils.showCommonToast("同意");
        holder.linOperation.setVisibility(View.GONE);
        holder.tvResult.setVisibility(View.VISIBLE);
        holder.tvResult.setText("已同意");

        saveExtra(uiMessage.getMessageId(), mStateAccept, uiMessage.getUId(), "玩家同意了您的请求");
        sendAgreeBroadcast();
    }

    private void doReject(OrderHolder holder, UIMessage uiMessage) {
        ToastUtils.showCommonToast("拒绝");
        holder.linOperation.setVisibility(View.GONE);
        holder.tvResult.setVisibility(View.VISIBLE);
        holder.tvResult.setText("已拒绝");

        saveExtra(uiMessage.getMessageId(), mStateReject, uiMessage.getUId(),"玩家拒绝了您的请求");
    }

    private void saveExtra(int messageId, String extra, String uid,String content) {
        sendResponse(uid, extra,content);

        RongIMClient.getInstance().setMessageExtra(messageId, extra, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtils.showCommonToast("保存成功");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.showCommonToast("保存失败");
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
                ToastUtils.showCommonToast("response success");
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
        Intent intent = new Intent(OtherConstant.ORDER_RESPONSE_RECEIVER);
        intent.putExtra(IntentConstant.INTENT_ORDER_RESPONSE_TYPE, OtherConstant.ORDER_RESPONSE_AGREE_ADVANCE);
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
