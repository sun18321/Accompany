package com.play.accompany.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.view.AccountActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

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

    }

    @Override
    protected String getFragmentName() {
        return "MessageFragment";
    }

}
