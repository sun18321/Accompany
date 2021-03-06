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

    @POST("invite")
    Observable<BaseResponse> inviteCode(@Body RequestBody body);

    @POST("getCfgService")
    Observable<BaseResponse> getService(@Body RequestBody body);

    @POST("getGameType")
    Observable<BaseResponse> getOtherGame(@Body RequestBody body);

    @POST("applyAnchor")
    Observable<BaseResponse> applyMaster(@Body RequestBody body);

    @POST("favor")
    Observable<BaseResponse> attention(@Body RequestBody body);

    @POST("getGameTypeAll")
    Observable<BaseResponse> getAllGame(@Body RequestBody body);

    @POST("rongCloudRegister")
    Observable<BaseResponse> getChatToken(@Body RequestBody body);

    @POST("rongCloudGetInfo")
    Observable<BaseResponse> getChaterInfo(@Body RequestBody body);

    @POST("isCash")
    Observable<BaseResponse> getCashPermission(@Body RequestBody body);

    @POST("cash")
    Observable<BaseResponse> doCash(@Body RequestBody body);

    @POST("wx_unifiedorder")
    Observable<BaseResponse> requestWxPay(@Body RequestBody body);

    @POST("getGold")
    Observable<BaseResponse> getGold(@Body RequestBody body);

    @POST("loginWx")
    Observable<BaseResponse> loginWeChat(@Body RequestBody body);

    @POST("sendLocal")
    Observable<BaseResponse> sendLocal(@Body RequestBody body);

    @POST("ali_order")
    Observable<BaseResponse> requestAlipay(@Body RequestBody body);

    @POST("orderCancel")
    Observable<BaseResponse> cancelOrder(@Body RequestBody body);

    @POST("orderApplyForwardStart")
    Observable<BaseResponse> orderApplyEarlyStart(@Body RequestBody body);

    @POST("orderApplyForwardEnd")
    Observable<BaseResponse> orderApplyEarlyEnd(@Body RequestBody body);

    @POST("orderForwardAgree")
    Observable<BaseResponse> orderAgreeEarly(@Body RequestBody body);

    @POST("orderChat")
    Observable<BaseResponse> orderStateCheck(@Body RequestBody body);

    @POST("findUser")
    Observable<BaseResponse> getUserInfo(@Body RequestBody body);

    @POST("getApplyAnchor")
    Observable<BaseResponse> getCheckMasterInfo(@Body RequestBody body);

    @POST("getAnchorGameType")
    Observable<BaseResponse> getAfterMasterInfo(@Body RequestBody body);

    @POST("getAnchorGameTypeApply")
    Observable<BaseResponse> getCheckMaterDetail(@Body RequestBody body);

    @POST("saveAnchorGameType")
    Observable<BaseResponse> editMasterPrice(@Body RequestBody body);

    @POST("addAnchorGameType")
    Observable<BaseResponse> addMasterItem(@Body RequestBody body);

    @POST("getOrderState")
    Observable<BaseResponse> getRedPointCount(@Body RequestBody body);

    @POST("setOrderState")
    Observable<BaseResponse> clearRedPoint(@Body RequestBody body);

    @POST("getVersion")
    Observable<BaseResponse> getVersion();

    @POST("setUserName")
    Observable<BaseResponse> editId(@Body RequestBody body);

    @POST("findFavorList")
    Observable<BaseResponse> getAttentionDetail(@Body RequestBody body);

    @POST("findFansList")
    Observable<BaseResponse> getFansDetail(@Body RequestBody body);

    @POST("setUserOnline")
    Observable<BaseResponse> sendHeart(@Body RequestBody body);

    @POST("upAudio")
    Observable<BaseResponse> setAudio(@Body RequestBody body);

    @POST("getAudio")
    Observable<BaseResponse> getAudio(@Body RequestBody body);

    @POST("delAudio")
    Observable<BaseResponse> deleteAudio(@Body RequestBody body);

    @POST("selVoiceTabs")
    Observable<BaseResponse> getSpeakTab(@Body RequestBody body);

    @POST("selVoice")
    Observable<BaseResponse> querySpeak(@Body RequestBody body);

    @POST("selRecommendVoice")
    Observable<BaseResponse> getRecommendSound(@Body RequestBody body);

    @POST("setVoiceLike")
    Observable<BaseResponse> setVoiceLike(@Body RequestBody body);

    @POST("delVoice")
    Observable<BaseResponse> deleteVoice(@Body RequestBody body);
}

