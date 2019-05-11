package com.play.accompany.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.play.accompany.R;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.view.AccompanyApplication;
import com.play.accompany.view.AllOrderActivity;

public class NotificationUtils {

    public static NotificationCompat.Builder createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(AccompanyApplication.getContext(), OtherConstant.ORDER_CHANNEL);
        Intent intent = new Intent(AccompanyApplication.getContext(), AllOrderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(AccompanyApplication.getContext(), OtherConstant.NOTIFY_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(AccompanyApplication.getContext().getResources().getString(R.string.app_name));
        builder.setContentText(AccompanyApplication.getContext().getResources().getString(R.string.new_order));
        builder.setTicker(AccompanyApplication.getContext().getResources().getString(R.string.new_order));
        builder.setDefaults(Notification.DEFAULT_SOUND);

        builder.setContentIntent(pendingIntent);
        return builder;
    }
}
