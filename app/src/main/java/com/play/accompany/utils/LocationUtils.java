package com.play.accompany.utils;

import android.annotation.SuppressLint;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.play.accompany.bean.LocalBean;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.view.AccompanyApplication;

import okhttp3.RequestBody;

public class LocationUtils {
    private static final String mTag = "location";

    //声明AMapLocationClient类对象
    @SuppressLint("StaticFieldLeak")
    private static AMapLocationClient mLocationClient = null;
    public static void startLocate() {
        LogUtils.d(mTag,"start locate");

        //声明定位回调监听器
         AMapLocationListener mLocationListener = new AMapLocationListener() {
             @Override
             public void onLocationChanged(AMapLocation aMapLocation) {
                 if (aMapLocation != null) {
                     double longitude = aMapLocation.getLongitude();
                     double latitude = aMapLocation.getLatitude();
                     String address = aMapLocation.getAddress();
                     String city = aMapLocation.getCity();
                     LogUtils.d(mTag, "long:" + longitude + "lat:" + latitude);
                     LogUtils.d(mTag, address);
                     LogUtils.d(mTag, "区:" + aMapLocation.getDistrict());
                     LogUtils.d(mTag, "error:" + aMapLocation.getErrorCode() + "error info:" + aMapLocation.getErrorInfo());
                     sendLocal(StringUtils.cutCityWord(city), latitude, longitude);
                     mLocationClient.onDestroy();
                     mLocationClient = null;
                 } else {
                     LogUtils.d(mTag, "amap is null");
                 }
             }
         };
        //初始化定位
        mLocationClient = new AMapLocationClient(AccompanyApplication.getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //声明AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = null;
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        AMapLocationClientOption option = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationClient.setLocationOption(option);
//        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//        mLocationClient.stopLocation();
//        mLocationClient.startLocation();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    private static void sendLocal(String city, double latitude, double longitude) {
        LocalBean bean = new LocalBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setAddress(city);
        bean.setLocalX(latitude);
        bean.setLocalY(longitude);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.onlyRequest(NetFactory.getNetRequest().getNetService().sendLocal(body));
    }

}
