package com.play.accompany.view;

import android.content.Intent;
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
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.bean.OrderBean;
import com.play.accompany.bean.PayBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;

import java.util.List;

import okhttp3.RequestBody;

public class OrderPayActivity extends BaseActivity implements View.OnClickListener {

    private RoundedImageView mImgHead;
    private TextView mTvName;
    private TextView mTvType;
    private TextView mTvDetail;
    private int mPayType = 0;
    private ImageView mImgAppCheck;
    private ImageView mImgWechatCheck;
    private ImageView mImgAlipayCheck;
    private int mAll;
    private Button mBtnPay;
    private IntentPayInfo mInfo;
    private String mOrderId;

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
        mBtnPay.setText(getResources().getString(R.string.button_pay) + mAll + getResources().getString(R.string.money));
    }

    private void goPay() {
        showDialog();
        PayBean bean = new PayBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setId(mOrderId);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().orderPay(body), new TypeToken<BaseDecodeBean<String>>() {
        }.getType(), new NetListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(OrderPayActivity.this, getResources().getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OrderPayActivity.this, AllOrderActivity.class));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_app:
                break;
            case R.id.rl_alipay:
                Toast.makeText(this, getResources().getString(R.string.developing), Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_wechat:
                Toast.makeText(this, getResources().getString(R.string.developing), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_pay:
                goPay();
                break;
        }
    }
}
