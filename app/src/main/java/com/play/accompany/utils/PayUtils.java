package com.play.accompany.utils;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.RequestPayBean;
import com.play.accompany.bean.WxPayBean;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.view.AccompanyApplication;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import okhttp3.RequestBody;

public class PayUtils {
    public void doWxPay(int money) {
        RequestPayBean bean = new RequestPayBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setGold(money);
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

    public void requestAlipay() {

    }

    private void payByWeChat(WxPayBean bean) {
        IWXAPI api = WXAPIFactory.createWXAPI(AccompanyApplication.getContext(), AppConstant.WE_CHAT_ID);
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

    private void payByAlipay() {

    }

//    private String genAppSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(Constants.API_KEY);
//        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e("orion", "appSign:" + appSign);
//        return appSign;
//    }

//    private String creatSign() {
//        StringBuffer sb = new StringBuffer();
//        Set es = parameters.entrySet();// 所有参与传参的参数按照accsii排序（升序）
//        Iterator it = es.iterator();
//        while (it.hasNext()) {
//       @SuppressWarnings("rawtypes")
//       Map.Entry entry = (Map.Entry) it.next();
//       String k = (String) entry.getKey();
//       Object v = entry.getValue();
//       if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
//        sb.append(k + "=" + v + "&");
//       }
//      }
//      sb.append("key=" + ConstantsMember.KEY); //KEY是商户秘钥
//      String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
//      return sign;
//    }
//
//    public  String createSign(String characterEncoding, SortedMap<Object, Object> parameters) {
//      StringBuffer sb = new StringBuffer();
//      Set es = parameters.entrySet();// 所有参与传参的参数按照accsii排序（升序）
//      Iterator it = es.iterator();
//      while (it.hasNext()) {
//       @SuppressWarnings("rawtypes")
//       Map.Entry entry = (Map.Entry) it.next();
//       String k = (String) entry.getKey();
//       Object v = entry.getValue();
//       if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
//        sb.append(k + "=" + v + "&");
//       }
//      }
//      sb.append("key=" + ConstantsMember.KEY); //KEY是商户秘钥
//      String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
//      return sign; // D3A5D13E7838E1D453F4F2EA526C4766
//          // D3A5D13E7838E1D453F4F2EA526C4766
// }
}
