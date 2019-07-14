package com.play.accompany.utils;

import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.GoldBean;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.present.CommonListener;

import java.util.List;

import okhttp3.RequestBody;

public class UpdateUtils {

    private static UpdateUtils sInstance;

    private UpdateUtils() {

    }

    public static UpdateUtils getInstance() {
        if (sInstance == null) {
            synchronized (UpdateUtils.class) {
                if (sInstance == null) {
                    sInstance = new UpdateUtils();
                }
            }
        }
        return sInstance;
    }

    public void updateGold(@NonNull final CommonListener.BooleanListener listener) {
        AccompanyRequest request = new AccompanyRequest();
        RequestBody body = EncodeUtils.encodeToken();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getGold(body), new TypeToken<BaseDecodeBean<List<GoldBean>>>() {
        }.getType(), new NetListener<List<GoldBean>>() {
            @Override
            public void onSuccess(List<GoldBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                GoldBean bean = list.get(0);
                SPUtils.getInstance().put(SpConstant.MY_GOLDEN, bean.getGold());
                listener.onListener(true);
            }

            @Override
            public void onFailed(int errCode) {
                listener.onListener(false);
            }

            @Override
            public void onError() {
                listener.onListener(false);
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
