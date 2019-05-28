package com.play.accompany.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.bean.AlipayBean;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.GoldBean;
import com.play.accompany.bean.RequestPayBean;
import com.play.accompany.bean.WxPayBean;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.view.AccompanyApplication;
import com.play.accompany.wxapi.WXPayEntryActivity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import okhttp3.RequestBody;

public class PayUtils {
    private  final int SDK_PAY_FLAG = 1;
    private  final int SDK_AUTH_FLAG = 2;

    private final String mPaySuccess = "9000";
    private final String mPayFailed = "4000";
    private final String mPayCancel = "6001";


    private Activity mActivity;

    public PayUtils(Activity activity) {
        mActivity = activity;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String status = payResult.getResultStatus();
                    if (TextUtils.equals(mPaySuccess, status)) {
                        doSuccess();
                        ToastUtils.showCommonToast(AccompanyApplication.getContext().getResources().getString(R.string.pay_success));
                    } else if (TextUtils.equals(mPayFailed, status)) {
                        ToastUtils.showCommonToast(AccompanyApplication.getContext().getResources().getString(R.string.pay_failed));
                    } else if (TextUtils.equals(mPayCancel, status)) {
                        ToastUtils.showCommonToast(AccompanyApplication.getContext().getResources().getString(R.string.pay_cancel));
                    }
                    break;
            }
        }
    };

    //支付宝回调成功处理  微信回调在WXPayEntryActivity
    private void doSuccess() {
        if (AccompanyApplication.getOrderPay()) {
            AccompanyApplication.endOrderPay();
            Intent intent = new Intent(OtherConstant.PAY_RECEIVER);
            AccompanyApplication.getContext().sendBroadcast(intent);
        } else {
            Intent intent = new Intent(OtherConstant.WALLET_RECEIVER);
            AccompanyApplication.getContext().sendBroadcast(intent);
        }
    }

//    private void checkGold() {
//        AccompanyRequest request = new AccompanyRequest();
//        RequestBody body = EncodeUtils.encodeToken();
//        request.beginRequest(NetFactory.getNetRequest().getNetService().getGold(body), new TypeToken<BaseDecodeBean<List<GoldBean>>>() {
//        }.getType(), new NetListener<List<GoldBean>>() {
//            @Override
//            public void onSuccess(List<GoldBean> list) {
//                if (list.isEmpty()) {
//                    return;
//                }
//                GoldBean bean = list.get(0);
//                SPUtils.getInstance().put(SpConstant.MY_GOLDEN, bean.getGold());
//            }
//
//            @Override
//            public void onFailed(int errCode) {
//
//            }
//
//            @Override
//            public void onError() {
//
//            }
//
//            @Override
//            public void onComplete() {
//            }
//        });
//    }



    //微信充值单位是分，一元就是100分 支付宝也统一
    public void requestWeChatPay(int money) {
        RequestPayBean bean = new RequestPayBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setGold(money * 100);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().requestWxPay(body), new TypeToken<BaseDecodeBean<List<WxPayBean>>>() {

        }.getType(), new NetListener<List<WxPayBean>>() {
            @Override
            public void onSuccess(List<WxPayBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                WxPayBean payBean = list.get(0);
                payByWeChat(payBean);
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

    public void requestAlipay(int money) {
        RequestPayBean bean = new RequestPayBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setGold(money * 100);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().requestAlipay(body), new TypeToken<BaseDecodeBean<List<AlipayBean>>>() {
        }.getType(), new NetListener<List<AlipayBean>>() {
            @Override
            public void onSuccess(List<AlipayBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                AlipayBean alipayBean = list.get(0);
                String orderStr = alipayBean.getOrderStr();
                payByAlipay(orderStr);
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

    private void payByWeChat(WxPayBean bean) {
        IWXAPI api = WXAPIFactory.createWXAPI(mActivity, AppConstant.WE_CHAT_ID);
        api.registerApp(AppConstant.WE_CHAT_ID);
        PayReq req = new PayReq();

        req.appId = bean.getAppid();//你的微信appid
        req.partnerId = bean.getMchId();//商户号
        req.prepayId = bean.getOut_trade_no();//预支付交易会话ID
        req.nonceStr = bean.getNonce_str();//随机字符串
        req.timeStamp = bean.getTime();//时间戳
        req.sign = bean.getSign();//签名
        req.packageValue = "Sign=WXPay";//扩展字段,这里固定填写Sign=WXPay
        api.sendReq(req);
    }

    private void payByAlipay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
