package com.play.accompany.view;

import android.annotation.SuppressLint;
import android.arch.persistence.room.PrimaryKey;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;

public class WebViewActivity extends BaseActivity {
    private String mUrl;
    private String mTitle;
    private WebView mWebView;


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

        mWebView = findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
//        // 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
//        //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.addJavascriptInterface(this, "accompany");
        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                LogUtils.d(getTag(),"js prompt");

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                LogUtils.d(getTag(),"js alert" + "-" + url + "-" + message);

                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                LogUtils.d(getTag(),"js confirm");

                return super.onJsConfirm(view, url, message, result);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost();
                String scheme = uri.getScheme();
                LogUtils.d(getTag(), "parse:" + uri.toString());
                LogUtils.d(getTag(), "host:" + host + "scheme:" + scheme);

                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
        }
    }

    private void uploadToken() {
        mWebView.loadUrl("javascript:butOnclick(\"" + SPUtils.getInstance().getString(SpConstant.APP_TOKEN) + "\")");
    }

    @JavascriptInterface
    public void buttonCLick() {
        LogUtils.d(getTag(), "js click");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadToken();
            }
        });
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
