package com.play.accompany.view;

import android.app.Application;
import android.content.Context;

import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.AppConstant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class AccompanyApplication extends Application {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    private static UserInfo mUserInfo = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        //umeng
        UMConfigure.setLogEnabled(true);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        UMConfigure.init(this, AppConstant.UMENG_KEY, "default", UMConfigure.DEVICE_TYPE_PHONE, null);
    }

    public static void setUserInfo(UserInfo info) {
        mUserInfo = info;
    }

    public static UserInfo getUserInfo() {
        return mUserInfo;
    }
}
