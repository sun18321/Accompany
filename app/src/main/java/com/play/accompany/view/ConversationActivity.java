package com.play.accompany.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.AcceptOrderBean;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.BaseResponse;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.bean.StateBean;
import com.play.accompany.bean.StateResponseBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.chat.OrderMessage;
import com.play.accompany.chat.OrderProvider;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OrderConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;
import okhttp3.RequestBody;

public class ConversationActivity extends BaseActivity {
    private String mTitle;
    private String mTargetId;
    private OrderResponseReceiver mReceiver;
    private ConversationFragment mFragment;
    private TextView mTvRight;
    private String mOrderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerResponseReceiver();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_conversation;
    }

    @Override
    protected String getTag() {
        return "ConversationActivity";
    }

    @Override
    protected void initViews() {
        mTvRight = findViewById(R.id.tv_right);
        setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtils.getInstance().getInt(SpConstant.USER_TYPE) == OtherConstant.USER_TYPE_ACCOMPANY) {
            requestState();
        }
    }

    private void agreeAdvance() {
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().orderAgreeEarly(getBossBody()), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
                }.getType(), new NetListener<List<OnlyCodeBean>>() {
                    @Override
                    public void onSuccess(List<OnlyCodeBean> list) {

                    }

                    @Override
                    public void onFailed(int errCode) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setTitle(String title) {
        if (title.length() > 5) {
            String s = title.substring(0, 5) + "...";
            initToolbar(s);
        } else {
            initToolbar(title);
        }
    }

    private void sendOrderAdvance() {
//        ToastUtils.showCommonToast("发送");
        OrderMessage orderMessage;
        String s = mTvRight.getText().toString();
        if (TextUtils.equals(s, getResources().getString(R.string.advance_start))) {
            orderMessage = new OrderMessage(SPUtils.getInstance().getString(SpConstant.MY_USER_ID), mTargetId, OrderMessage.ORDER_EARLY_START, System.currentTimeMillis());
        } else {
            orderMessage = new OrderMessage(SPUtils.getInstance().getString(SpConstant.MY_USER_ID), mTargetId, OrderMessage.ORDER_EARLY_END, System.currentTimeMillis());
        }
        Message message = Message.obtain(mTargetId, Conversation.ConversationType.PRIVATE, orderMessage);
        RongIM.getInstance().sendMessage(message,"提前",null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                ToastUtils.showCommonToast("发送成功");
                LogUtils.d("message", "uid:" + message.getUId());
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtils.showCommonToast("发送失败");
            }
        });
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mTargetId = intent.getData().getQueryParameter("targetId");
        mTitle = intent.getData().getQueryParameter("title");
//        mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        enterFragment(conversationType, mTargetId);
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        mFragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        if (mFragment != null) {
            mFragment.setUri(uri);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void registerResponseReceiver() {
        mReceiver = new OrderResponseReceiver();
        IntentFilter intentFilter = new IntentFilter(OtherConstant.CONVERSATION_ACTIVITY_RECEIVER);
        registerReceiver(mReceiver, intentFilter);
    }

    private void getMessageId(Intent intent) {
        String uid = intent.getStringExtra(IntentConstant.INTENT_ORDER_UID);
        final String response = intent.getStringExtra(IntentConstant.INTENT_ORDER_RESPONSE);
        final String content = intent.getStringExtra(IntentConstant.INTENT_ORDER_CONTENT);
        final String sendId = intent.getStringExtra(IntentConstant.INTENT_ORDER_SEND_ID);
        RongIMClient.getInstance().getMessageByUid(uid, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                int messageId = message.getMessageId();
                saveData(messageId, response, content, sendId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    private void saveData(int messageId, String response, String content, final String sendId) {
        RongIMClient.getInstance().setMessageExtra(messageId, response, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                mFragment.getMessageAdapter().notifyDataSetChanged();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

        InformationNotificationMessage notificationContent = InformationNotificationMessage.obtain(content);
        Message.ReceivedStatus status = new Message.ReceivedStatus(1);
        RongIM.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE, sendId, sendId, status, notificationContent, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                if (TextUtils.equals(sendId, mTargetId)) {
                    mFragment.getMessageAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        if (TextUtils.equals(response, OrderProvider.mStateAccept)) {
            rightTitleNext();
        }
    }

    private void rightTitleNext() {
        String title = mTvRight.getText().toString();
        if (TextUtils.equals(title, getResources().getString(R.string.advance_start))) {
            mTvRight.setText(getResources().getString(R.string.advance_finish));
        } else if (TextUtils.equals(title, getResources().getString(R.string.advance_finish))) {
            mTvRight.setText("");
            mTvRight.setVisibility(View.INVISIBLE);
        }
    }


    private void setRightTitle(String title) {
        mTvRight.setText(title);
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mTvRight.getText().toString();
                if (TextUtils.equals(s,"接单")) {
                    showTips();
                }else {
                    requestAdvance();
                }
            }
        });
    }

    private void showTips() {
        new QMUIDialog.MessageDialogBuilder(this).setMessage("确定接受此订单吗？").addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        }).addAction("确定", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                acceptOrder();
                dialog.dismiss();
            }
        }).create().show();
    }

    private void acceptOrder() {
        AcceptOrderBean bean = new AcceptOrderBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setId(mOrderId);
        RequestBody body = EncodeUtils.encodeInBody(GsonUtils.toJson(bean));
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().acceptOrder(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
        }.getType(), new NetListener<List<OnlyCodeBean>>() {
            @Override
            public void onSuccess(List<OnlyCodeBean> list) {
                ToastUtils.showCommonToast("接单成功");
                requestState();
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void requestAdvance() {
        AccompanyRequest request = new AccompanyRequest();
        String s = mTvRight.getText().toString();
        Observable<BaseResponse> advanceObservable;
        if (TextUtils.equals(s, getResources().getString(R.string.advance_start))) {
            advanceObservable = NetFactory.getNetRequest().getNetService().orderApplyEarlyStart(getMasterBody());
        } else if (TextUtils.equals(s, getResources().getString(R.string.advance_finish))) {
            advanceObservable = NetFactory.getNetRequest().getNetService().orderApplyEarlyEnd(getMasterBody());
        } else {
            return;
        }
        request.beginRequest(advanceObservable, new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
        }.getType(), new NetListener<List<OnlyCodeBean>>() {
            @Override
            public void onSuccess(List<OnlyCodeBean> list) {
                sendOrderAdvance();
            }

            @Override
            public void onFailed(int errCode) {
                requestState();
            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void requestState() {
        if (TextUtils.equals(mTargetId, OtherConstant.OFFICE_NUMBER)) {
            return;
        }
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().orderStateCheck(getMasterBody()), new TypeToken<BaseDecodeBean<List<StateResponseBean>>>() {
                }.getType(), new NetListener<List<StateResponseBean>>() {

            @Override
            public void onSuccess(List<StateResponseBean> list){
                if (list.isEmpty()) {
                    return;
                }
                int state = list.get(0).getState();
                mOrderId= list.get(0).getOrderId();
                //提前开始
                if (state == OrderConstant.ACCEPT_ORDER) {
                    setRightTitle(getResources().getString(R.string.advance_start));
                } else if (state == OrderConstant.START_SERVICE) {
                    //提前结束
                    setRightTitle(getResources().getString(R.string.advance_finish));
                } else if (state == OrderConstant.PAY) {
                    setRightTitle("接单");
                }
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    //大神专用
    private RequestBody getMasterBody() {
        StateBean bean = new StateBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setTargetId(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        //客户id，是跟大神聊天的人，所以赋值targetid
        bean.setUserId(mTargetId);
        String json = GsonUtils.toJson(bean);
        return EncodeUtils.encodeInBody(json);
    }

    //玩家专用
    private RequestBody getBossBody() {
        StateBean bean = new StateBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setTargetId(mTargetId);
        bean.setUserId(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        String json = GsonUtils.toJson(bean);
        return EncodeUtils.encodeInBody(json);
    }

    private void goUserCenter(String userId) {
        UserInfo info = new UserInfo();
        info.setUserId(userId);
        info.setFromChat(true);
        Intent intent = new Intent(this, UserCenterActivity.class).putExtra(IntentConstant.INTENT_USER, info);
        startActivity(intent);
    }

    class OrderResponseReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                //大神
                int extra = intent.getIntExtra(IntentConstant.INTENT_CONVERSATION_RECEIVER_TYPE, -1);
                if (extra == OtherConstant.CONVERSATION_NOTIFY_MASTER) {
                    getMessageId(intent);
                } else if (extra == OtherConstant.CONVERSATION_AGREE_ADVANCE) {
                    //玩家
                    agreeAdvance();
                } else if (extra == OtherConstant.CONVERSATION_GO_USER) {
                    String id = intent.getStringExtra(IntentConstant.INTENT_USER_ID);
                    goUserCenter(id);
                } else if (extra == OtherConstant.CONVERSATION_UPDATE_NAME) {
                    String name = intent.getStringExtra(IntentConstant.INTENT_USER_NAME);
                    setTitle(name);
                }
            }
        }
    }
}
