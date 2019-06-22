package com.play.accompany.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.CashBean;
import com.play.accompany.bean.GoldBean;
import com.play.accompany.bean.RequestCashBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;

public class CashActivity extends BaseActivity {
    private TextView mTvMoney;
    private CashBean mCashBean;
    private int mMin;
    private int mMax;
    private int mBase;
    private EditText mEditCash;
    private int mMoney;
    private boolean isNet = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_cash;
    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    protected void initViews() {

        initToolbar(getResources().getString(R.string.cash));
        mTvMoney = findViewById(R.id.tv_money);
        mTvMoney.setText(String.valueOf(SPUtils.getInstance().getDouble(SpConstant.MY_GOLDEN)));
        TextView tvRule = findViewById(R.id.tv_rule);
        String s = getResources().getString(R.string.cash_rule_detail);
        if (mCashBean != null) {
            mMin = mCashBean.getCashMin();
            mMax = mCashBean.getCashMax();
            mBase = mCashBean.getCashInteger();
            String format = String.format(s, mMin, mBase, mMax, mCashBean.getCashDailyNum());
            tvRule.setText(format);
        }
        mEditCash = findViewById(R.id.edit_cash);
        findViewById(R.id.btn_cash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashFormat();
            }
        });
    }

    private void cashFormat() {
        String s = mEditCash.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.cash_tip));
            return;
        }
        mMoney = Integer.parseInt(s);
        double currentMoney = SPUtils.getInstance().getDouble(SpConstant.MY_GOLDEN);
        if (mMoney > currentMoney ) {
            mMoney = (int) currentMoney;
        }
        int extra = mMoney % mBase;
        if (extra != 0) {
            mMoney = mMoney / mBase * mBase;
        }

        if (mMoney > mMax) {
            mMoney = mMax;
        }

        if (mMoney < mMin && mMoney != 0) {
            mMoney = mMin;
        }

        mEditCash.setText(String.valueOf(mMoney));
        mEditCash.setSelection(String.valueOf(mMoney).length());

        if (mMoney == 0) {
            ToastUtils.showCommonToast(getResources().getString(R.string.cash_no_more));
            return;
        }
        String format = getResources().getString(R.string.format_cash);
        String tips = String.format(format, mMoney);
        new QMUIDialog.MessageDialogBuilder(this).setTitle(getResources().getString(R.string.tips)).setMessage(tips)
                .addAction(getResources().getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction(getResources().getString(R.string.confirm), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                doCash(mMoney);
            }
        }).create().show();
    }

    private void doCash(int money) {
        if (isNet) {
            return;
        }
        isNet = true;

        RequestCashBean bean = new RequestCashBean();
        bean.setMoney(money);
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().doCash(body), new TypeToken<BaseDecodeBean<String>>() {
        }.getType(), new NetListener<String>() {
            @Override
            public void onSuccess(String s) {
                isNet = false;
                ToastUtils.showCommonToast(getResources().getString(R.string.cash_success));
                double gold = SPUtils.getInstance().getDouble(SpConstant.MY_GOLDEN);
                gold -= mMoney;
                mTvMoney.setText(String.valueOf(gold));
                SPUtils.getInstance().put(SpConstant.MY_GOLDEN, gold);
            }

            @Override
            public void onFailed(int errCode) {
                isNet = false;
            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {
                isNet = false;
            }
        });
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();
        Intent intent = getIntent();
        if (intent != null) {
            mCashBean = (CashBean) intent.getSerializableExtra(IntentConstant.INTENT_CASH);
        }
    }
}
