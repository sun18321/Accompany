package com.play.accompany.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.utils.LogUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public abstract class BaseActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {
    protected ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        QMUIStatusBarHelper.setStatusBarLightMode(this);

        parseIntent();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getTag());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        MobclickAgent.onPageEnd(getTag());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        mDialog = null;
    }

    protected void setStatusColor(int color) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void finish() {
        super.finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);

        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }


    protected abstract int getLayout();

    protected abstract String getTag();

    protected abstract void initViews();

    protected void parseIntent(){}

    protected void initToolbar(String title) {
        Toolbar toolbar =  findViewById(R.id.toolbar);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseActivity.this.finish();
                }
            });
        }
    }

    protected void showDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setCancelable(true);
            mDialog.setMessage(getResources().getString(R.string.loading));
            mDialog.setOnCancelListener(this);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    protected void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        LogUtils.d("request", "father dialog dismiss");
    }
}
