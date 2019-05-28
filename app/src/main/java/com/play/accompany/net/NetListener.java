package com.play.accompany.net;

import java.io.UnsupportedEncodingException;

public interface NetListener<T> {

    void onSuccess(T t) throws UnsupportedEncodingException;

    void onFailed(int errCode);

    void onError();

    void onComplete();
}
