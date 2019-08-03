package com.play.accompany.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.CashBean;
import com.play.accompany.bean.GoldBean;
import com.play.accompany.bean.RequestPayBean;
import com.play.accompany.bean.WxPayBean;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.MD5;
import com.play.accompany.utils.PayUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

public class WalletActivity extends BaseActivity implements View.OnClickListener {
    private final int mTypeWechat = 1001;
    private final int mTypeAlipay = 1002;
    private final int mMaxMoney = 50000;

    private TextView mTvGold;
    private LinearLayout mLinFirst;
    private LinearLayout mLinSecond;
    private LinearLayout mLinThird;
    private LinearLayout mLinFourth;
    private LinearLayout mLinFifth;
    private LinearLayout mLinSixth;
    private LinearLayout mCurrentLin;
    private HashMap<LinearLayout, Integer> mHashMap = new HashMap<>();
    private CheckBox mCheckAlipay;
    private CheckBox mCheckWechat;
    private int mPayMoney = -1;
    private int mPayType = -1;
    private Button mButtonPay;
    private LinearLayout mLinInput;
    private EditText mEditInput;
    private Button mButtonCash;
    private CashBean mCashBean;
    private WalletReceiver mReceiver;

    @Override
    protected int getLayout() {
        return R.layout.activity_wallet;
    }

    @Override
    protected String getTag() {
        return "WalletActivity";
    }

    @Override
    protected void initViews() {

        initToolbar(getResources().getString(R.string.wallet));

        mTvGold = findViewById(R.id.tv_gold);
        mCheckAlipay = findViewById(R.id.check_alipay);
        mCheckWechat = findViewById(R.id.check_wechat);
        mLinFirst = findViewById(R.id.lin_first);
        mLinSecond = findViewById(R.id.lin_second);
        mLinThird = findViewById(R.id.lin_third);
        mLinFourth = findViewById(R.id.lin_fourth);
        mLinFifth = findViewById(R.id.lin_fifth);
        mLinSixth = findViewById(R.id.lin_sixth);
        mLinInput = findViewById(R.id.lin_input);
        mLinFirst.setOnClickListener(this);
        mLinSecond.setOnClickListener(this);
        mLinThird.setOnClickListener(this);
        mLinFourth.setOnClickListener(this);
        mLinFifth.setOnClickListener(this);
        mLinSixth.setOnClickListener(this);
        mLinInput.setOnClickListener(this);
        findViewById(R.id.lin_wechat).setOnClickListener(this);
        findViewById(R.id.lin_alipay).setOnClickListener(this);
        mButtonPay = findViewById(R.id.btn_pay);
        mButtonPay.setOnClickListener(this);
        mButtonCash = findViewById(R.id.btn_cash);
        mButtonCash.setOnClickListener(this);

        mEditInput = findViewById(R.id.edit_input);
        mEditInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    doSelect(mLinInput);
                }
            }
        });
        mEditInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text)) {
                    int money = Integer.parseInt(text);
                    mPayMoney = money;
                    if (money > mMaxMoney) {
                        money = mMaxMoney;
                        mPayMoney = money;
                        mEditInput.setText(String.valueOf(mPayMoney));
                        mEditInput.setSelection(String.valueOf(mMaxMoney).length());
                    }
                    String format = getResources().getString(R.string.format_bill);
                    String word = String.format(format, mPayMoney + "");
                    mButtonPay.setText(word);
                } else {
                    mPayMoney = -1;
                    mButtonPay.setText(getResources().getString(R.string.wallet_now));
                }
            }
        });

        mHashMap.clear();
        mHashMap.put(mLinFirst, 30);
        mHashMap.put(mLinSecond, 50);
        mHashMap.put(mLinThird, 100);
        mHashMap.put(mLinFourth, 200);
        mHashMap.put(mLinFifth, 500);
        mHashMap.put(mLinSixth, 1000);

        doSelect(mLinFirst);
        requestCash();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new WalletReceiver();
        IntentFilter intentFilter = new IntentFilter(OtherConstant.WALLET_RECEIVER);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDialog != null && mDialog.isShowing()) {
            dismissDialog();
        }

        if (mTvGold != null) {
            mTvGold.setText(SPUtils.getInstance().getDouble(SpConstant.MY_GOLDEN) + "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void requestCash() {
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getCashPermission(EncodeUtils.encodeToken()), new TypeToken<BaseDecodeBean<List<CashBean>>>() {
        }.getType(), new NetListener<List<CashBean>>() {
            @Override
            public void onSuccess(List<CashBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                CashBean cashBean = list.get(0);
                double gold = cashBean.getGold();
                SPUtils.getInstance().put(SpConstant.MY_GOLDEN, gold);
                mTvGold.setText(String.valueOf(gold));
                if (cashBean.getIsCash() == OtherConstant.MONEY_CAN_CASH) {
                    mCashBean = cashBean;
                    mButtonCash.setVisibility(View.VISIBLE);
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

    private void doSelect(LinearLayout linearLayout) {
        if (mCurrentLin == linearLayout) {
            return;
        }
        if (mCurrentLin != null) {
            mCurrentLin.setSelected(false);
        }
        linearLayout.setSelected(true);
        mCurrentLin = linearLayout;
        if (linearLayout == mLinInput) {
            mEditInput.requestFocus();
            mPayMoney = -1;
            mButtonPay.setText(getResources().getString(R.string.wallet_now));
            showKeyBoard(mEditInput);
            return;
        } else {
            mEditInput.setText("");
            mEditInput.clearFocus();
            hideKeyBoard(mEditInput);
        }
        mPayMoney = mHashMap.get(linearLayout);
        String format = getResources().getString(R.string.format_bill);
        String s = String.format(format, mPayMoney + "");
        mButtonPay.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_first:
                doSelect(mLinFirst);
                break;
            case R.id.lin_second:
                doSelect(mLinSecond);
                break;
            case R.id.lin_third:
                doSelect(mLinThird);
                break;
            case R.id.lin_fourth:
                doSelect(mLinFourth);
                break;
            case R.id.lin_fifth:
                doSelect(mLinFifth);
                break;
            case R.id.lin_sixth:
                doSelect(mLinSixth);
                break;
            case R.id.lin_input:
                doSelect(mLinInput);
                break;
            case R.id.lin_wechat:
                if (!mCheckWechat.isChecked()) {
                    mCheckAlipay.setChecked(false);
                    mCheckWechat.setChecked(true);
                }
                mPayType = mTypeWechat;
                break;
            case R.id.lin_alipay:
                if (!mCheckAlipay.isChecked()) {
                    mCheckAlipay.setChecked(true);
                    mCheckWechat.setChecked(false);
                }
                mPayType = mTypeAlipay;
                break;
            case R.id.btn_pay:
                doPay();
                break;
            case R.id.btn_cash:
                if (mCashBean != null) {
                    startActivity(new Intent(this, CashActivity.class).putExtra(IntentConstant.INTENT_CASH, mCashBean));
                }
                break;
        }
    }

    //充值单位是分，1元就传100
    private void doPay() {
        if (mPayMoney == -1) {
            ToastUtils.showCommonToast(getResources().getString(R.string.wallet_tips_money));
            return;
        }

        if (mPayType == -1) {
            ToastUtils.showCommonToast(getResources().getString(R.string.wallet_tips_type));
            return;
        }
        PayUtils payUtils = new PayUtils(this);
        if (mPayType == mTypeWechat) {
            payUtils.requestWeChatPay(mPayMoney);
        } else {
            payUtils.requestAlipay(mPayMoney);
        }
        showDialog();
    }

    private void checkGold() {
        EventUtils.getInstance().upPay(mPayMoney, null);

        AccompanyRequest request = new AccompanyRequest();
        RequestBody body = EncodeUtils.encodeToken();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getGold(body), new TypeToken<BaseDecodeBean<List<GoldBean>>>() {
        }.getType(), new NetListener<List<GoldBean>>() {
            @Override
            public void onSuccess(List<GoldBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                GoldBean bean = list.get(0);
                SPUtils.getInstance().put(SpConstant.MY_GOLDEN, bean.getGold());
                if (mTvGold != null) {
                    mTvGold.setText(String.valueOf(bean.getGold()));
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

    class WalletReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("wallet", "receiver success");

            checkGold();
        }
    }
}
