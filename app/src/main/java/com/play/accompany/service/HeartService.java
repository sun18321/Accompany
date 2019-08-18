package com.play.accompany.service;

import android.animation.TimeAnimator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

public class HeartService extends Service {
    private final long period = 5 * 60 * 1000;

    private Timer mTimer;
    private AccompanyRequest mRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        startTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTask() {
        mRequest = new AccompanyRequest();
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mRequest.onlyRequest(NetFactory.getNetRequest().getNetService().sendHeart(EncodeUtils.encodeToken()));
            }
        };
        mTimer.scheduleAtFixedRate(task, 0, period);
    }

}
