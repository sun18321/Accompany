package com.play.accompany.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;

import java.util.HashMap;

public class WalletActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_recharge;
    }

    @Override
    protected String getTag() {
        return "WalletActivity";
    }

    @Override
    protected void initViews() {

        initToolbar(getResources().getString(R.string.wallet));

        new HashMap<String, View>();

    }
}
