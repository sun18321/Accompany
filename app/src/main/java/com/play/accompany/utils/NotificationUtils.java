package com.play.accompany.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.play.accompany.R;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.view.AccompanyApplication;
import com.play.accompany.view.AllOrderActivity;
import com.play.accompany.view.MainActivity;
import com.play.accompany.view.MasterActivity;
import com.play.accompany.view.WalletActivity;

public class NotificationUtils {

    public static NotificationCompat.Builder createNotification(int type, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(AccompanyApplication.getContext(), OtherConstant.NOTIFY_CHANNEL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(AccompanyApplication.getContext().getResources().getString(R.string.app_name));
        builder.setContentText(text);
        builder.setTicker(AccompanyApplication.getContext().getResources().getString(R.string.new_message));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        } else {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        PendingIntent pendingIntent = null;
        //0：不跳转 1：订单界面 2：聊天界面 3：申请大神界面 4：系统消息 5：我的钱包
        switch (type) {
            case 0:
                break;
            case 1:
                Intent orderIntent = new Intent(AccompanyApplication.getContext(), AllOrderActivity.class);
                pendingIntent = PendingIntent.getActivity(AccompanyApplication.getContext(), OtherConstant.NOTIFY_CODE, orderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 2:
                Intent chatIntent = new Intent(AccompanyApplication.getContext(), MainActivity.class);
                chatIntent.putExtra(OtherConstant.MAIN_INTNET, OtherConstant.GO_MESSAGE);
                pendingIntent = PendingIntent.getActivity(AccompanyApplication.getContext(), OtherConstant.NOTIFY_CODE, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 3:
                Intent masterIntent = new Intent(AccompanyApplication.getContext(), MasterActivity.class);
                pendingIntent = PendingIntent.getActivity(AccompanyApplication.getContext(), OtherConstant.NOTIFY_CODE, masterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 4:
                Intent systemIntent = new Intent(AccompanyApplication.getContext(), MainActivity.class);
                systemIntent.putExtra(OtherConstant.MAIN_INTNET, OtherConstant.GO_MESSAGE);
                pendingIntent = PendingIntent.getActivity(AccompanyApplication.getContext(), OtherConstant.NOTIFY_CODE, systemIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case 5:
                Intent walletIntent = new Intent(AccompanyApplication.getContext(), WalletActivity.class);
                pendingIntent = PendingIntent.getActivity(AccompanyApplication.getContext(), OtherConstant.NOTIFY_CODE, walletIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
        }

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        return builder;
    }
}
