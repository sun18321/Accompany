package com.play.accompany.net;

import com.play.accompany.bean.BaseResponse;


import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NetService {


    //    @POST("register")
//    Observable<BaseResponse> register(@Body RequestBody body);
//
    @POST("getUserList")
    Observable<BaseResponse> getType(@Body RequestBody body);

    @POST("saveUser")
    Observable<BaseResponse> saveUser(@Body RequestBody body);

    @POST("sendVeriftyCode")
    Observable<BaseResponse> getCode(@Body RequestBody body);

    @POST("loginCode")
    Observable<BaseResponse> loginCode(@Body RequestBody body);

    @POST("getHomePage")
    Observable<BaseResponse> getHomePage(@Body RequestBody body);

    @POST("orderCreate")
    Observable<BaseResponse> createOrder(@Body RequestBody body);

    @POST("orderPay")
    Observable<BaseResponse> orderPay(@Body RequestBody body);

    @POST("getOrderList")
    Observable<BaseResponse> getAllOrder(@Body RequestBody body);

    @POST("token")
    Observable<BaseResponse> verifyToken(@Body RequestBody body);

    @POST("uploadIcon")
    Observable<BaseResponse> upLoadImage(@Body RequestBody body);

    @POST("orderEvaluable")
    Observable<BaseResponse> sendComment(@Body RequestBody body);

    @POST("order")
    Observable<BaseResponse> acceptOrder(@Body RequestBody body);
}
