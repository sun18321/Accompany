package com.play.accompany.chat;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.bean.MainReceiverMessage;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.RongIMClient;

public class AccompanyListFragment extends ConversationListFragment {

    private View mView;
    private TextView mTvUnread;

    @Override
    protected List<View> onAddHeaderView() {

        List<View> listView = super.onAddHeaderView();
        mView = View.inflate(this.getContext(), R.layout.chat_order, null);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherConstant.FILTER_MAIN_RECEIVER);
                MainReceiverMessage message = new MainReceiverMessage();
                message.setMessageType(MainReceiverMessage.TYPE_ORDER);
                intent.putExtra(OtherConstant.MAIN_RECEIVER, message);
                AccompanyApplication.getContext().sendBroadcast(intent);
            }
        });

        mTvUnread = mView.findViewById(R.id.tv_unread);
        int count =AccompanyApplication.getMessageUnread();
        setReadPoint(count);
        listView.add(mView);
        return listView;
    }

    public void setReadPoint(int num) {
        LogUtils.d("red", "head view red:" + num);

        if (num == 0) {
            mTvUnread.setVisibility(View.INVISIBLE);
        } else {
            mTvUnread.setVisibility(View.VISIBLE);
            mTvUnread.setText(String.valueOf(num));
        }
    }

}
