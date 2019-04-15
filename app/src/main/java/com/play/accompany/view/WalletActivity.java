package com.play.accompany.view;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.SPUtils;

import java.util.HashMap;

public class WalletActivity extends BaseActivity implements View.OnClickListener {
    private final int mTypeWechat = 1001;
    private final int mTypeAlipay = 1002;

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
    private int mPayMoney;
    private int mPayType = -1;
    private Button mButtonPay;

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
        mLinFirst.setOnClickListener(this);
        mLinSecond.setOnClickListener(this);
        mLinThird.setOnClickListener(this);
        mLinFourth.setOnClickListener(this);
        mLinFifth.setOnClickListener(this);
        mLinSixth.setOnClickListener(this);
        findViewById(R.id.lin_wechat).setOnClickListener(this);
        findViewById(R.id.lin_alipay).setOnClickListener(this);
        mButtonPay = findViewById(R.id.btn_pay);
        mButtonPay.setOnClickListener(this);

        mHashMap.clear();
        mHashMap.put(mLinFirst, 30);
        mHashMap.put(mLinSecond, 50);
        mHashMap.put(mLinThird, 100);
        mHashMap.put(mLinFourth, 200);
        mHashMap.put(mLinFifth, 500);
        mHashMap.put(mLinSixth, 1000);

        mTvGold.setText(SPUtils.getInstance().getInt(SpConstant.MY_GOLDEN) + "");
        doSelect(mLinFirst);
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

                break;
        }
    }
}
