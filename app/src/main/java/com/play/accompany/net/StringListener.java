package com.play.accompany.net;

public interface StringListener {
    void onSuccess(String s);

    void onFailed(int errorCode);

    void onError();

    void onComplete();
}
