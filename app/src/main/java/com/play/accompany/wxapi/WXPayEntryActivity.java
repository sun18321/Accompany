package com.play.accompany.wxapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.play.accompany.R;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private final String WE_CHAT_PAY = "wx_pay";
    private IWXAPI api;
    private ProgressDialog mDialog;
    private int mMoney;

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
                ToastUtils.showCommonToast(getResources().getString(R.string.pay_success));
                if (AccompanyApplication.getOrderPay()) {
                    sendPay();
                    AccompanyApplication.endOrderPay();
                } else {
                    sendWallet();
                }
//                WXPayEntryActivity.this.finish();
            } else if (baseResp.errCode == -1) {
                //支付失败
                LogUtils.d(WE_CHAT_PAY, "支付失败" + "--" + baseResp.errStr);
                ToastUtils.showCommonToast(getResources().getString(R.string.pay_failed));
                WXPayEntryActivity.this.finish();
            } else if (baseResp.errCode == -2) {
                //取消支付
                LogUtils.d(WE_CHAT_PAY, "取消支付");
                ToastUtils.showCommonToast(getResources().getString(R.string.pay_cancel));
                WXPayEntryActivity.this.finish();
            }
        }
    }

    private void sendPay() {
        Intent intent = new Intent(OtherConstant.PAY_RECEIVER);
        sendBroadcast(intent);
        WXPayEntryActivity.this.finish();
    }

    private void sendWallet() {
        Intent intent = new Intent(OtherConstant.WALLET_RECEIVER);
        sendBroadcast(intent);
        WXPayEntryActivity.this.finish();
    }

//    private void checkGold() {
//        showDialog();
//        AccompanyRequest request = new AccompanyRequest();
//        RequestBody body = EncodeUtils.encodeToken();
//        request.beginRequest(NetFactory.getNetRequest().getNetService().getGold(body), new TypeToken<BaseDecodeBean<List<GoldBean>>>() {
//        }.getType(), new NetListener<List<GoldBean>>() {
//            @Override
//            public void onSuccess(List<GoldBean> list) {
//                dismissDialog();
//                if (list.isEmpty()) {
//                    WXPayEntryActivity.this.finish();
//                    return;
//                }
//                GoldBean bean = list.get(0);
//                SPUtils.getInstance().put(SpConstant.MY_GOLDEN, bean.getGold());
//                WXPayEntryActivity.this.finish();
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
//                dismissDialog();
//                WXPayEntryActivity.this.finish();
//            }
//        });
//    }

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
