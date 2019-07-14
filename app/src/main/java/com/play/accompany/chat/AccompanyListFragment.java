package com.play.accompany.chat;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.bean.MainReceiverMessage;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;

public class AccompanyListFragment extends ConversationListFragment {

    @Override
    protected List<View> onAddHeaderView() {

        List<View> listView = super.onAddHeaderView();
        View view = View.inflate(this.getContext(), R.layout.chat_order, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherConstant.FILTER_MAIN_RECEIVER);
                MainReceiverMessage message = new MainReceiverMessage();
                message.setMessageType(MainReceiverMessage.TYPE_ORDER);
                intent.putExtra(OtherConstant.MAIN_RECEIVER, message);
                AccompanyApplication.getContext().sendBroadcast(intent);
            }
        });

        listView.add(view);
        return listView;
    }

}
