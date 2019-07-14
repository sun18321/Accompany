package com.play.accompany.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;

public class AccompanyListAdapter extends ConversationListAdapter {
    public AccompanyListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {

        return super.newView(context, position, group);

    }

    @Override
    protected void bindView(View v, int position, UIConversation data) {
        super.bindView(v, position, data);

    }
}
