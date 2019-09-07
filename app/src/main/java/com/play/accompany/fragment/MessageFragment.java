package com.play.accompany.fragment;

import android.net.Uri;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.chat.AccompanyListFragment;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.AppUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MessageFragment extends BaseFragment {
    private static MessageFragment sMessageFragment;
    private AccompanyListFragment mFragment;

    public static MessageFragment newInstance() {
        if (sMessageFragment == null) {
            sMessageFragment = new MessageFragment();
            return sMessageFragment;
        }
        return sMessageFragment;
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initViews(View view) {

        View statusView = view.findViewById(R.id.view_status);
        int statusBarHeight = AppUtils.getStatusBarHeight();
        ViewGroup.LayoutParams params = statusView.getLayoutParams();
        params.height = statusBarHeight;
        statusView.setLayoutParams(params);
        LogUtils.d("height", "height:" + statusBarHeight);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(getResources().getString(R.string.message));

        enterFragment();
    }

    @Override
    protected String getFragmentName() {
        return "MessageFragment";
    }


    private void enterFragment() {
        RongIM.getInstance().setConversationToTop(Conversation.ConversationType.PRIVATE, OtherConstant.OFFICE_NUMBER, true, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                initConversationList();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

    }

    private void initConversationList() {
        mFragment = (AccompanyListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);

        Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
//                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();

        mFragment.setUri(uri);
    }

    public void setUnread(int count) {
        if (mFragment != null) {
            mFragment.setReadPoint(count);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (RongIM.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED
                && RongIM.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING) {
            RongIM.connect(SPUtils.getInstance().getString(SpConstant.CHAT_TOKEN), null);
        }
    }
}
