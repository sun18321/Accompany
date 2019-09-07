package com.play.accompany.wxapi;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.ToastUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private  final int RETURN_MSG_TYPE_LOGIN = 1; //登录
    private  final int RETURN_MSG_TYPE_SHARE = 2; //分享

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d("wechat", "oncreate");
        api = WXAPIFactory.createWXAPI(this, AppConstant.WE_CHAT_ID, false);
        api.handleIntent(getIntent(), this);
        api.registerApp(AppConstant.WE_CHAT_ID);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    public void onReq(BaseReq baseReq) {

        LogUtils.d("wechat", "onreq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int type = baseResp.getType();

        LogUtils.d("wechat", "onresp" + "====" + "type:" + type);

        String message;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    String code = ((SendAuth.Resp) baseResp).code;
                    Intent intent = new Intent(OtherConstant.WE_CHAT_RECEIVER);
                    intent.putExtra(IntentConstant.INTENT_WE_CHAT_CODE, code);
                    sendBroadcast(intent);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消登录";
                } else {
                    message = "取消分享";
                }
                ToastUtils.showCommonToast(message);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "拒绝登录";
                } else {
                    message = "拒绝分享";
                }
                ToastUtils.showCommonToast(message);
                break;
        }
        this.finish();
    }
}
