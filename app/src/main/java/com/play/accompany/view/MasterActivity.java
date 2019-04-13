package com.play.accompany.view;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.design.TypeDialog;

import java.util.ArrayList;
import java.util.List;

public class MasterActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditName;
    private EditText mEditId;
    private EditText mEditWeChat;
    private TextView mTvType;

    @Override
    protected int getLayout() {
        return R.layout.activity_master;
    }

    @Override
    protected String getTag() {
        return "MasterActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.master));


        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.lin_type).setOnClickListener(this);
        mEditName = findViewById(R.id.edit_name);
        mEditId = findViewById(R.id.edit_id);
        mEditWeChat = findViewById(R.id.edit_wechat);
        mTvType = findViewById(R.id.tv_type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_type:
                List<Integer> list = new ArrayList<>();
                final TypeDialog dialog = new TypeDialog(this, list);
                dialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.create().show();

                break;
            case R.id.btn_submit:

                break;
        }
    }
}
