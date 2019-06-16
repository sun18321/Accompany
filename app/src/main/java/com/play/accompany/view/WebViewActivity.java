package com.play.accompany.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.utils.LogUtils;

public class WebViewActivity extends BaseActivity {
    private String mUrl;
    private String mTitle;


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
        initToolbar(mTitle);
//        setStatusColor(R.color.color_red);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
//        // 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
//        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
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
            mTitle = intent.getStringExtra(IntentConstant.INTENT_TITLE);
            LogUtils.d(getTag(), "url:" + mUrl);
        }
    }
}
