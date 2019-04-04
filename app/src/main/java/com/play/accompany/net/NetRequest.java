package com.play.accompany.net;

import com.play.accompany.constant.AppConstant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetRequest {
    private final OkHttpClient mOkHttpClient;
    private final Retrofit mRetrofit;
    private final NetService mNetService;

    public NetRequest() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mNetService = mRetrofit.create(NetService.class);
    }

    public NetService getNetService() {
        return mNetService;
    }


}
