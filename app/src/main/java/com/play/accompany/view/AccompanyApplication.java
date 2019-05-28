package com.play.accompany.view;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.play.accompany.bean.UserInfo;
import com.play.accompany.bean.WeChatInfo;
import com.play.accompany.chat.ChatClickListener;
import com.play.accompany.chat.ChatConnectListener;
import com.play.accompany.chat.MessageReceiverListener;
import com.play.accompany.constant.AppConstant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import io.rong.imkit.RongIM;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

public class AccompanyApplication extends Application {
    private static Context mContext;
    private static WeChatInfo mInfo;
    private static boolean mOrderPay = false;

    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        //umeng
        UMConfigure.setLogEnabled(true);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        UMConfigure.init(this, AppConstant.UMENG_KEY, "default", UMConfigure.DEVICE_TYPE_PHONE, null);

        //rong
        rongInit();

    }

    private void rongInit() {
        /**
         *
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
            //连接状态
            RongIM.setConnectionStatusListener(new ChatConnectListener());
            //收到消息
            RongIM.setOnReceiveMessageListener(new MessageReceiverListener());
            //会话点击
            RongIM.setConversationClickListener(new ChatClickListener());
        }
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager == null) {
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    public static WeChatInfo getWeChatInfo() {
        return mInfo;
    }

    public static void setWeChatInfo(WeChatInfo info) {
        mInfo = info;
    }

    public static void clearWeChatInfo() {
        mInfo = null;
    }

    public static boolean getOrderPay() {
        return mOrderPay;
    }

    public static void startOrderPay() {
        mOrderPay = true;
    }

    public static void endOrderPay() {
        mOrderPay = false;
    }

}
