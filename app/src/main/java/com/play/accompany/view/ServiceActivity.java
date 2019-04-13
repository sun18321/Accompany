package com.play.accompany.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.ServiceBean;
import com.play.accompany.bean.Token;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;

import java.util.List;

import okhttp3.RequestBody;

public class ServiceActivity extends BaseActivity {

    private TextView mTvService;

    @Override
    protected int getLayout() {
        return R.layout.activity_service;
    }

    @Override
    protected String getTag() {
        return "ServiceActivity";
    }

    @Override
    protected void initViews() {

        initToolbar(getResources().getString(R.string.service));

        mTvService = findViewById(R.id.tv_service);

        showDialog();
        Token token = new Token(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(token);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getService(body), new TypeToken<BaseDecodeBean<List<ServiceBean>>>() {
        }.getType(), new NetListener<List<ServiceBean>>() {
            @Override
            public void onSuccess(List<ServiceBean> list) {
                dismissDialog();
                if (list.isEmpty()) {
                    return;
                }
                ServiceBean bean = list.get(0);
                String serviceMsg = bean.getServiceMsg();
                mTvService.setText(serviceMsg);
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
}
