package com.play.accompany.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.CodeLogin;
import com.play.accompany.bean.LoginWeChat;
import com.play.accompany.bean.PhoneLogin;
import com.play.accompany.bean.ResponseLogin;
import com.play.accompany.bean.Token;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.bean.WeChatBean;
import com.play.accompany.bean.WeChatInfo;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.AppUtils;
import com.play.accompany.utils.CipherUtil;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.utils.UserInfoDatabaseUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private TextInputLayout mInputAccount;
    private TextInputLayout mInputCode;
    private EditText mEditAccount;
    private EditText mEditCode;
    private Button mButtonGetCode;
    private int mUnitTime = 1;
    private final int mAllTime = 60;
    private int mCountdownTime = mAllTime;
    private Disposable mDisposable;
    private AccompanyRequest mAccompanyRequest;
    private WeChatReceiver mReceiver;
    private WeChatInfo mInfo;
    private TextView mTvRule;
    private Button mButtonLogin;
    private LinearLayout mLinWeChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new WeChatReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OtherConstant.WE_CHAT_RECEIVER);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_account;
    }

    @Override
    protected String getTag() {
        return "accountActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.login));
        mInputAccount = findViewById(R.id.input_account);
        mInputCode = findViewById(R.id.input_code);
        mEditAccount = findViewById(R.id.edit_account);
        mEditCode = findViewById(R.id.edit_code);
        mButtonGetCode = findViewById(R.id.btn_get_code);
        mButtonGetCode.setOnClickListener(this);
        mButtonLogin = findViewById(R.id.btn_login);
        mLinWeChat = findViewById(R.id.lin_wechat);
        mLinWeChat.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        mEditAccount.requestFocus();
        mEditAccount.setOnEditorActionListener(this);
        mEditCode.setOnEditorActionListener(this);

        mEditAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 11) {
                    mInputAccount.setEnabled(true);
                    mInputAccount.setError(getResources().getString(R.string.phone_beyond));
                } else {
                    mInputAccount.setErrorEnabled(false);
                }
            }
        });

        mTvRule = findViewById(R.id.tv_rule);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(AccountActivity.this, RuleActivity.class).putExtra(IntentConstant.INTENT_TITLE, getResources().getString(R.string.rule)));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        };

        SpannableString spannableString = new SpannableString(getResources().getString(R.string.login_rule));
        spannableString.setSpan(clickableSpan, 7, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvRule.setText(spannableString);
        mTvRule.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void requestCode(String num) {
        CodeLogin codeLogin = new CodeLogin();
        codeLogin.setUserId(num);
        String json = GsonUtils.toJson(codeLogin);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getCode(body), new TypeToken<BaseDecodeBean<String>>() {
        }.getType(), new NetListener<String>() {
            @Override
            public void onSuccess(String o) {
                ToastUtils.showCommonToast(getResources().getString(R.string.code_success));
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
        mButtonGetCode.setClickable(false);
        mButtonGetCode.setText(mCountdownTime / mUnitTime + "S");
        Observable.interval(mUnitTime, TimeUnit.SECONDS)
                .take(mAllTime)
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        mCountdownTime -= mUnitTime;
                        mButtonGetCode.setText(mCountdownTime / mUnitTime + "S");
                        if (mCountdownTime == 0) {
                            mButtonGetCode.setClickable(true);
                            mButtonGetCode.setText(getResources().getString(R.string.get_code));
                            mCountdownTime = mAllTime;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void checkPhone() {
        String phone = mEditAccount.getText().toString();
        if (AppUtils.isMobileNumber(phone)) {
            requestCode(phone);
        } else {
            ToastUtils.showCenterToast(getResources().getString(R.string.phone_error));
        }
    }

    private void login() {
        hideKeyBoard(mButtonLogin);
        final String account = mEditAccount.getText().toString();
        String code = mEditCode.getText().toString();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(code)) {
            Toast.makeText(this, getResources().getString(R.string.not_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog();
        CodeLogin codeLogin = new CodeLogin();
        codeLogin.setUserId(account);
        codeLogin.setCode(code);
        String json = GsonUtils.toJson(codeLogin);
        RequestBody body = EncodeUtils.encodeInBody(json);
        mAccompanyRequest = new AccompanyRequest();
        mAccompanyRequest.beginRequest(NetFactory.getNetRequest().getNetService().loginCode(body), new TypeToken<BaseDecodeBean<List<UserInfo>>>() {
        }.getType(), new NetListener<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> list) throws UnsupportedEncodingException {
                if (list.isEmpty()) {
                    Toast.makeText(AccountActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    return;
                }
                UserInfo bean = list.get(0);
                String token = bean.getToken();
                if (!TextUtils.isEmpty(token)) {
                    UserInfoDatabaseUtils.saveUserInfo(bean);
                    String name = bean.getName();
                    if (name == null || TextUtils.isEmpty(name)) {
                        Intent intent = new Intent(AccountActivity.this, EditUserActivity.class);
                        intent.putExtra(IntentConstant.INTENT_CODE, EditUserActivity.INTENT_REGISTER);
                        if (mInfo != null) {
                            intent.putExtra(IntentConstant.INTENT_USER, weChat2UserInfo());
                        }
                        startActivity(intent);
                    } else {
                        MainActivity.launch(AccountActivity.this);
                    }
                    AccountActivity.this.finish();
                } else {
                    Toast.makeText(AccountActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {
                dismissDialog();
            }
        });

    }

    private void weChatLogin() {
        IWXAPI wxapi = WXAPIFactory.createWXAPI(this, AppConstant.WE_CHAT_ID, false);
        wxapi.registerApp(AppConstant.WE_CHAT_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "jialanguo";
        wxapi.sendReq(req);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_code:
                checkPhone();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.lin_wechat:
                weChatLogin();
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDisposable != null) {
            mDisposable.dispose();
        }

        if (mAccompanyRequest != null) {
            mAccompanyRequest.destroy();
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.edit_account:
                checkPhone();
                break;
            case R.id.edit_code:
                login();
                break;
        }
        return false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
//        super.onCancel(dialog);
        LogUtils.d("request", "dialog dismiss");
        if (mAccompanyRequest != null) {
            mAccompanyRequest.destroy();
        }
    }

    //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
    private void getAccessToken(String code) {
        LogUtils.d("wechat", "code:" + code);

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + AppConstant.WE_CHAT_ID + "&secret=" +
                AppConstant.WE_CHAT_SECRET + "&code=" + code + "&grant_type=authorization_code";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d("okhttp", "thread:" + Thread.currentThread().getName());
                String string = response.body().string();
                WeChatBean bean = GsonUtils.fromJson(string, WeChatBean.class);
                if (bean != null) {
                    getWeChatInfo(bean.getAccess_token(), bean.getOpenid());
                }
            }
        });
    }

    //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
    private void getWeChatInfo(String token, String openid) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + token + "&openid=" + openid;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                mInfo = GsonUtils.fromJson(string, WeChatInfo.class);
                if (mInfo != null) {
                    doWeChatLogin(mInfo.getOpenid());
                    AccompanyApplication.setWeChatInfo(mInfo);
                }
            }
        });

    }

    private void doWeChatLogin(String openid) {
        LogUtils.d("wechat", "openid:" + openid);

        LoginWeChat weChat = new LoginWeChat();
        weChat.setCode(openid);
        String json = GsonUtils.toJson(weChat);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().loginWeChat(body), new TypeToken<BaseDecodeBean<List<Token>>>() {
                }.getType(), new NetListener<List<Token>>() {
                    @Override
                    public void onSuccess(List<Token> list) {
                        if (list.isEmpty()) {
                            return;
                        }
                        Token token = list.get(0);
                        String s = token.getToken();
                        if (TextUtils.isEmpty(s)) {
                            initToolbar("绑定手机号");
                            mLinWeChat.setVisibility(View.GONE);
                            mTvRule.setVisibility(View.GONE);
                            mButtonLogin.setText("绑定");
                            ToastUtils.showCommonToast("请先绑定手机号");
                        } else {
                            SPUtils.getInstance().put(SpConstant.APP_TOKEN, s);
                            verifyToken(s);
                        }
                    }

                    @Override
                    public void onFailed(int errCode) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void verifyToken(String s) {
        Token token = new Token(s);
        String json = GsonUtils.toJson(token);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().verifyToken(body), new TypeToken<BaseDecodeBean<List<UserInfo>>>() {
        }.getType(), new NetListener<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> list) {
                if (list.isEmpty()) {
                } else {
                    UserInfo userInfo = list.get(0);
                    String name = userInfo.getName();
                    if (TextUtils.isEmpty(name)) {
                        Intent intent = new Intent(AccountActivity.this, EditUserActivity.class);
                        intent.putExtra(IntentConstant.INTENT_CODE, EditUserActivity.INTENT_REGISTER);
                        startActivity(intent);
                    } else {
                        UserInfoDatabaseUtils.saveUserInfo(list.get(0));
                        MainActivity.launch(AccountActivity.this);
                    }
                    AccountActivity.this.finish();
                }
            }

            @Override
            public void onFailed(int errCode) {
            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private UserInfo weChat2UserInfo() throws UnsupportedEncodingException {
        UserInfo userInfo = new UserInfo();
        if (mInfo == null) {
            return userInfo;
        }
        userInfo.setName(mInfo.getNickname());
        //1男2女
        int sex = mInfo.getSex();
        if (sex == 1) {
            userInfo.setGender(OtherConstant.GENDER_MALE);
        } else {
            userInfo.setGender(OtherConstant.GENDER_FEMALE);
        }
        userInfo.setUrl(mInfo.getHeadimgurl());
        return userInfo;
    }

    class WeChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String code = intent.getStringExtra(IntentConstant.INTENT_WE_CHAT_CODE);
                getAccessToken(code);
            }
        }
    }
}
