package com.play.accompany.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.CodeLogin;
import com.play.accompany.bean.PhoneLogin;
import com.play.accompany.bean.ResponseLogin;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

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
        findViewById(R.id.btn_login).setOnClickListener(this);
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
    }

    private void requestCode(String num) {
        CodeLogin codeLogin = new CodeLogin();
        codeLogin.setUserId(num);
        String json = GsonUtils.toJson(codeLogin);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.onlyRequest(NetFactory.getNetRequest().getNetService().getCode(body));
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
        mAccompanyRequest.beginRequest(NetFactory.getNetRequest().getNetService().loginCode(body), new TypeToken<BaseDecodeBean<List<UserInfo>>>() {}.getType(),new NetListener<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> list) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_code:
                checkPhone();
                break;
            case R.id.btn_login:
                login();
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
}
