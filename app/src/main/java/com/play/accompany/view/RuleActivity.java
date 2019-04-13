package com.play.accompany.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;

public class RuleActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_rule;
    }

    @Override
    protected String getTag() {
        return "RuleActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.rule));
    }
}
