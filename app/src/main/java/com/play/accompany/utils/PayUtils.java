package com.play.accompany.utils;

import com.play.accompany.constant.AppConstant;
import com.play.accompany.view.AccompanyApplication;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class PayUtils {


    public void requestWX() {

    }

    public void requestAlipay() {

    }

    private void payByWeChat() {
        IWXAPI api = WXAPIFactory.createWXAPI(AccompanyApplication.getContext(), AppConstant.WE_CHAT_ID);
        api.registerApp(AppConstant.WE_CHAT_ID);
        PayReq req = new PayReq();

        req.appId = "wx8888888888888888";//你的微信appid
        req.partnerId = "1900000109";//商户号
        req.prepayId = "WX1217752501201407033233368018";//预支付交易会话ID
        req.nonceStr = "5K8264ILTKCH16CQ2502SI8ZNMTM67VS";//随机字符串
        req.timeStamp = "1412000000";//时间戳
        req.packageValue = "Sign=WXPay";//扩展字段,这里固定填写Sign=WXPay
        req.sign = "C380BEC2BFD727A4B6845133519F3AD6";//签名

        api.sendReq(req);
    }

    private void payByAlipay() {

    }
}
