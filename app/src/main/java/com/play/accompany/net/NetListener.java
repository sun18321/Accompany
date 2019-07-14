package com.play.accompany.net;


public interface NetListener<T> {

    void onSuccess(T t);

    void onFailed(int errCode);

    void onError();

    void onComplete();
}
