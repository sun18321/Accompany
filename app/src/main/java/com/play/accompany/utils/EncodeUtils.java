package com.play.accompany.utils;

import com.play.accompany.bean.Token;
import com.play.accompany.constant.SpConstant;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.play.accompany.utils.CipherUtil.desEncrypt;

public class EncodeUtils {
    public static RequestBody encodeInBody(String data) {
        RequestBody body = null;
        try {
            String encrypt = desEncrypt(data);
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }


    public static RequestBody encodeToken() {
        RequestBody body = null;
        try {
            Token token = new Token(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
            String json = GsonUtils.toJson(token);
            String encrypt = desEncrypt(json);
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }
}
