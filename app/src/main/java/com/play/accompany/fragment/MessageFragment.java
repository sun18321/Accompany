package com.play.accompany.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.view.AccountActivity;
import com.play.accompany.view.MainActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class MessageFragment extends BaseFragment {
    private static MessageFragment sMessageFragment;

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
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((MainActivity) mContext).setSupportActionBar(toolbar);
        ((MainActivity) mContext).getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(getResources().getString(R.string.message));

        enterFragment();
    }

    @Override
    protected String getFragmentName() {
        return "MessageFragment";
    }


    private void enterFragment() {

        ConversationListFragment fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);

        Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();

        fragment.setUri(uri);
    }

}
