package com.play.accompany.net;

public class NetFactory {
    static volatile NetRequest mNetRequest = null;

    private NetFactory() {

    }

    public static NetRequest getNetRequest() {
        if (mNetRequest == null) {
            synchronized (NetRequest.class) {
                if (mNetRequest == null) {
                    mNetRequest = new NetRequest();
                }
            }
        }
        return mNetRequest;
    }

}
