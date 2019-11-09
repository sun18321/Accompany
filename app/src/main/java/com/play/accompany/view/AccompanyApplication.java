package com.play.accompany.view;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.bean.WeChatInfo;
import com.play.accompany.chat.ChatClickListener;
import com.play.accompany.chat.ChatConnectListener;
import com.play.accompany.chat.ChatListClickListener;
import com.play.accompany.chat.MessageReceiverListener;
import com.play.accompany.chat.MessageSendReceiverListener;
import com.play.accompany.chat.OrderMessage;
import com.play.accompany.chat.OrderProvider;
import com.play.accompany.chat.OrderResponseMessage;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.present.ApplicationListener;
import com.play.accompany.present.CommonListener;
import com.play.accompany.utils.FileSaveUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ThreadPool;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.rong.imkit.RongIM;

public class AccompanyApplication extends Application {
    private static Context mContext;
    //wechat login
    private static WeChatInfo mInfo;
    private static boolean mOrderPay = false;
    public static Context getContext() {
        return mContext;
    }
    private static List<TopGameBean> mGameList;
    private static List<String> mAttentionList;
    //订单未读
    private static int mMessageUnread = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

        //umeng
        UMConfigure.setLogEnabled(true);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        UMConfigure.setProcessEvent(true);
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
            //会话列表点击
            RongIM.setConversationListBehaviorListener(new ChatListClickListener());
            //自定义
            RongIM.registerMessageType(OrderMessage.class);
            RongIM.registerMessageTemplate(new OrderProvider());

            RongIM.registerMessageType(OrderResponseMessage.class);

            //发出消息
            RongIM.getInstance().setSendMessageListener(new MessageSendReceiverListener());

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

    public static void setGameList(final List<TopGameBean> list) {
        mGameList = list;
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                FileSaveUtils.getInstance().saveData(OtherConstant.FILE_GAME, list);
            }
        });
    }

    public static List<TopGameBean> getGameList() {
        return mGameList;
    }

    public static void setAttentionList(final List<String> list) {
        mAttentionList = list;
        SPUtils.getInstance().put(SpConstant.ATTENTION_COUNT, mAttentionList.size());
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                FileSaveUtils.getInstance().saveData(OtherConstant.FILE_ATTENTION, list);
            }
        });
    }

    public static List<String> getAttentionList() {
        return mAttentionList;
    }

    public static void getGameList(@NonNull final ApplicationListener.GameListListener listener) {
        if (mGameList == null || mGameList.isEmpty()) {
            FileSaveUtils.getInstance().getData(OtherConstant.FILE_GAME, new CommonListener.StringListener() {
                @Override
                public void onListener(String data) {
                    if (data == null || TextUtils.isEmpty(data)) {
                        listener.onGameListener(null);
                    } else {
                        mGameList = GsonUtils.fromJson(data, new TypeToken<List<TopGameBean>>() {
                         }.getType());
                        listener.onGameListener(mGameList);
                    }
                }
            });
        } else {
            listener.onGameListener(mGameList);
        }
    }

    public static void getAttentionList(@NonNull final ApplicationListener.AttentionListListener listener) {
        if (mAttentionList == null || mAttentionList.isEmpty()) {
            FileSaveUtils.getInstance().getData(OtherConstant.FILE_ATTENTION, new CommonListener.StringListener() {
                @Override
                public void onListener(String data) {
                    if (data == null || TextUtils.isEmpty(data)) {
                         listener.onAttentionListener(null);
                         }else {
                        mAttentionList =  GsonUtils.fromJson(data, new TypeToken<List<String>>() {
                        }.getType());
                        listener.onAttentionListener(mAttentionList);
                    }
                }
            });
        } else {
            listener.onAttentionListener(mAttentionList);
        }
    }

    public static void setAttentionChange(final String id) {
        if (mAttentionList == null || mAttentionList.isEmpty()) {
            FileSaveUtils.getInstance().getData(OtherConstant.FILE_ATTENTION, new CommonListener.StringListener() {
                @Override
                public void onListener(String data) {
                    mAttentionList = GsonUtils.fromJson(data, new TypeToken<List<String>>() {
                    }.getType());
                    doAttentionChange(id);
                }
            });
        } else {
            doAttentionChange(id);
        }
    }

    private static void doAttentionChange(String id) {
        if (mAttentionList == null || mAttentionList.isEmpty()) {
            return;
        }
        if (mAttentionList.contains(id)) {
            mAttentionList.remove(id);
        } else {
            mAttentionList.add(id);
        }
        SPUtils.getInstance().put(SpConstant.ATTENTION_COUNT, mAttentionList.size());
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                FileSaveUtils.getInstance().saveData(OtherConstant.FILE_ATTENTION, mAttentionList);
            }
        });
    }

    public static void setMessageUnread(int count) {
        mMessageUnread = count;
        LogUtils.d("order", "set count:" + count);
    }

    public static int getMessageUnread() {
        return mMessageUnread;
    }
}
