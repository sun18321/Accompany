package com.play.accompany.wxapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.GoldBean;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

import okhttp3.RequestBody;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private final String WE_CHAT_PAY = "wx_pay";
    private IWXAPI api;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api= WXAPIFactory.createWXAPI(this, AppConstant.WE_CHAT_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.d(WE_CHAT_PAY, "res" + baseResp.errCode);

        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (baseResp.errCode == 0) {
                //支付成功
                LogUtils.d(WE_CHAT_PAY, "支付成功");
                checkGold();
                ToastUtils.showCommonToast("支付成功");
//                WXPayEntryActivity.this.finish();
            } else if (baseResp.errCode == -1) {
                //支付失败
                LogUtils.d(WE_CHAT_PAY, "支付失败" + "--" + baseResp.errStr);
                ToastUtils.showCommonToast("支付失败");
                WXPayEntryActivity.this.finish();
            } else if (baseResp.errCode == -2) {
                //取消支付
                LogUtils.d(WE_CHAT_PAY, "取消支付");
                ToastUtils.showCommonToast("取消支付");
                WXPayEntryActivity.this.finish();
            }
        }
    }

    private void checkGold() {
        showDialog();
        AccompanyRequest request = new AccompanyRequest();
        RequestBody body = EncodeUtils.encodeToken();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getGold(body), new TypeToken<BaseDecodeBean<List<GoldBean>>>() {
        }.getType(), new NetListener<List<GoldBean>>() {
            @Override
            public void onSuccess(List<GoldBean> list) {
                dismissDialog();
                if (list.isEmpty()) {
                    WXPayEntryActivity.this.finish();
                    return;
                }
                GoldBean bean = list.get(0);
                SPUtils.getInstance().put(SpConstant.MY_GOLDEN, bean.getGold());
                WXPayEntryActivity.this.finish();
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
                WXPayEntryActivity.this.finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    protected void showDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setCancelable(true);
            mDialog.setMessage(getResources().getString(R.string.loading));
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    protected void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
