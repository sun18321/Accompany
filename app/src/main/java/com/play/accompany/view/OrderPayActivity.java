package com.play.accompany.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.GoldBean;
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.PayBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.PayUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;

import java.util.List;

import okhttp3.RequestBody;

public class OrderPayActivity extends BaseActivity implements View.OnClickListener {
    private final int mAppPay = 1;
    private final int mWXPay = 2;
    private final int mAliPay = 3;

    private RoundedImageView mImgHead;
    private TextView mTvName;
    private TextView mTvType;
    private TextView mTvDetail;
    private int mPayType = -1;
    private ImageView mImgAppCheck;
    private ImageView mImgWechatCheck;
    private ImageView mImgAlipayCheck;
    private int mAll;
    private Button mBtnPay;
    private IntentPayInfo mInfo;
    private String mOrderId;
    private TextView mTvGold;
    private int mCurrentGold;
    private PayReceiver mPayReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPayReceiver = new PayReceiver();
        IntentFilter intentFilter = new IntentFilter(OtherConstant.PAY_RECEIVER);
        registerReceiver(mPayReceiver, intentFilter);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_order_pay;
    }

    @Override
    protected String getTag() {
        return "OrderPayActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.order_pay));
        mImgHead = findViewById(R.id.img_head);
        mTvName = findViewById(R.id.tv_name);
        mTvType = findViewById(R.id.tv_type);
        mTvDetail = findViewById(R.id.tv_details);
        mImgAppCheck = findViewById(R.id.app_check);
        mImgWechatCheck = findViewById(R.id.wechat_check);
        mImgAlipayCheck = findViewById(R.id.alipay_check);
        mBtnPay = findViewById(R.id.btn_pay);
        mBtnPay.setOnClickListener(this);
        mTvGold = findViewById(R.id.tv_gold);
        findViewById(R.id.rl_app).setOnClickListener(this);
        findViewById(R.id.rl_alipay).setOnClickListener(this);
        findViewById(R.id.rl_wechat).setOnClickListener(this);

        if (mInfo == null) {
            Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            setViews();
        }
    }

    @Override
    protected void parseIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mInfo = (IntentPayInfo) intent.getSerializableExtra(IntentConstant.INTENT_PAY_INFO);
        } else {
            Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void setViews() {
        String url = mInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this).load(url).into(mImgHead);
        }
        mTvName.setText(mInfo.getName());
        String game = mInfo.getGame();
        mTvType.setText(game);
        String detail = mInfo.getDetail();
        mTvDetail.setText(detail);
        mOrderId = mInfo.getId();
        mAll = mInfo.getAll();
        mCurrentGold = SPUtils.getInstance().getInt(SpConstant.MY_GOLDEN);
        if (mAll > mCurrentGold) {
            mImgAppCheck.setVisibility(View.INVISIBLE);
            mBtnPay.setText(getResources().getString(R.string.button_pay));
        } else {
            mBtnPay.setText(getResources().getString(R.string.button_pay) + mAll + getResources().getString(R.string.money));
            mPayType = mAppPay;
        }
        mTvGold.setText(String.valueOf(mCurrentGold));
    }

    private void goPay() {
        if (mPayType == -1) {
            ToastUtils.showCommonToast("请先选择付款方式");
            return;
        }
        switch (mPayType) {
            case mAppPay:
                appPay();
                break;
            case mWXPay:
                weChatPay();
                break;
            case mAliPay:
                alipay();
                break;
        }
    }

    private void weChatPay() {
        PayUtils payUtils = new PayUtils(this);
        AccompanyApplication.startOrderPay();
        payUtils.requestWeChatPay(mAll);
    }

    private void alipay() {
        PayUtils payUtils = new PayUtils(this);
        AccompanyApplication.startOrderPay();
        payUtils.requestAlipay(mAll);
    }

    private void appPay() {
        showDialog();
        PayBean bean = new PayBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setId(mOrderId);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().orderPay(body), new TypeToken<BaseDecodeBean<List<GoldBean>>>() {
        }.getType(), new NetListener<List<GoldBean>>() {
            @Override
            public void onSuccess(List<GoldBean> list) {
                Toast.makeText(OrderPayActivity.this, getResources().getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OrderPayActivity.this, AllOrderActivity.class));
                if (!list.isEmpty()) {
                    GoldBean goldBean = list.get(0);
                    SPUtils.getInstance().put(SpConstant.MY_GOLDEN, goldBean.getGold());
                }
                OrderPayActivity.this.finish();
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

    private void otherPay() {
        String s = getResources().getString(R.string.format_bill);
        String format = String.format(s, String.valueOf(mAll));
        mBtnPay.setText(format);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_app:
                if (mPayType == mAppPay) {
                    return;
                }
                if (mAll > mCurrentGold) {
                    ToastUtils.showCommonToast(getResources().getString(R.string.pay_no_more_gold));
                    return;
                }
                mImgAlipayCheck.setVisibility(View.INVISIBLE);
                mImgWechatCheck.setVisibility(View.INVISIBLE);
                mImgAppCheck.setVisibility(View.VISIBLE);
                mPayType = mAppPay;
                mBtnPay.setText(getResources().getString(R.string.button_pay) + mAll + getResources().getString(R.string.money));
                break;
            case R.id.rl_alipay:
                if (mPayType == mAliPay) {
                    return;
                }
                mPayType = mAliPay;
                mImgAppCheck.setVisibility(View.INVISIBLE);
                mImgWechatCheck.setVisibility(View.INVISIBLE);
                mImgAlipayCheck.setVisibility(View.VISIBLE);
                otherPay();
                break;
            case R.id.rl_wechat:
                if (mPayType == mWXPay) {
                    return;
                }
                mPayType = mWXPay;
                mImgAppCheck.setVisibility(View.INVISIBLE);
                mImgAlipayCheck.setVisibility(View.INVISIBLE);
                mImgWechatCheck.setVisibility(View.VISIBLE);
                otherPay();
                break;
            case R.id.btn_pay:
                goPay();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPayReceiver != null) {
            unregisterReceiver(mPayReceiver);
        }
    }

    class PayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            appPay();
        }
    }

}
