package com.play.accompany.chat;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.ChatBean;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.SPUtils;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.RequestBody;

public class ChatConnectListener implements RongIMClient.ConnectionStatusListener {

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus){

            case CONNECTED://连接成功。

                break;
            case DISCONNECTED://断开连接。
                ReConnect();
                break;
            case CONNECTING://连接中。

                break;
            case NETWORK_UNAVAILABLE://网络不可用。

                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线

                break;
        }
    }

    private void ReConnect() {
        String token = SPUtils.getInstance().getString(SpConstant.CHAT_TOKEN);
        if (TextUtils.isEmpty(token)) {
            getToken();
        } else {
            connect(token);
        }
    }

    private void connect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    private void getToken() {
        RequestBody body = EncodeUtils.encodeToken();
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getChatToken(body), new TypeToken<BaseDecodeBean<List<ChatBean>>>() {
        }.getType(), new NetListener<List<ChatBean>>() {
            @Override
            public void onSuccess(List<ChatBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                ChatBean bean = list.get(0);
                String token = bean.getRcToken();
                SPUtils.getInstance().put(SpConstant.CHAT_TOKEN, token);
                connect(token);
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
}
