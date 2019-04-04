package com.play.accompany.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.utils.LogUtils;

public class WebViewActivity extends BaseActivity {
    private String mUrl;


    @Override
    protected int getLayout() {
        return R.layout.activity_web_view;
    }

    @Override
    protected String getTag() {
        return "WebViewActivity";
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {
        initToolbar("活动");
        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        if (!TextUtils.isEmpty(mUrl)) {
            webView.loadUrl(mUrl);
        }
    }

    @Override
    protected void parseIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra(IntentConstant.INTENT_URL);
            LogUtils.d(getTag(), "url:" + mUrl);
        }
    }
}
