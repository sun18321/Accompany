package com.play.accompany.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.SPUtils;

public class InviteCodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditInvite;

    @Override
    protected int getLayout() {
        return R.layout.activity_invite_code;
    }

    @Override
    protected String getTag() {
        return "InviteCodeActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.invite_code));

        TextView tvInviteCode = findViewById(R.id.tv_invite);
        String inviteCode = SPUtils.getInstance().getString(SpConstant.MY_INVITE_CODE);
        tvInviteCode.setText(inviteCode);

        mEditInvite = findViewById(R.id.edit_invite);
        findViewById(R.id.btn_copy).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copy:
                break;
            case R.id.btn_submit:
                break;
        }
    }
}
