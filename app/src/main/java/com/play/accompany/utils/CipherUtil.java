package com.play.accompany.utils;

import com.play.accompany.secret.BASE64Decoder;
import com.play.accompany.secret.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CipherUtil {
    private static final String DES = "DES";
    private static String defaultKey = "4942e826d55c12c9119afd6fb0bd79ae";

    /**
     * 加密 使用默认key
     *
     * @param data
     * @return
     */
    public static String desEncrypt(String data) throws Exception  {
        return CipherUtil.desEncrypt(data, defaultKey);
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    private static String desEncrypt(String data, String key) throws Exception {
        DESKeySpec keySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherData = cipher.doFinal(data.getBytes());

        return new BASE64Encoder().encode(cipherData);
    }

    /**
     * 解密 使用默认key
     *
     * @param data
     * @return
     */
    public static String desDecrypt(String data) throws Exception {
        return CipherUtil.desDecrypt(data, defaultKey);
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     */
    private static String desDecrypt(String data, String key) throws Exception {
        DESKeySpec keySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainData = cipher.doFinal(new BASE64Decoder().decodeBuffer(data));

        return new String(plainData);
    }

    public static RequestBody desEncryptBody(String data) throws Exception {
        String encrypt = desEncrypt(data);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), encrypt);
        return body;
    }
}