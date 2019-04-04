package com.play.accompany.utils;

import android.util.Log;

import com.play.accompany.BuildConfig;

public class LogUtils {
    private static final String mDefaultTag = "accompany";

    public static void d(String tag, String message) {
        if (BuildConfig.isDebug) {
            Log.d(tag, message);
        }
    }

    public static void defaultD(String message) {
        if (BuildConfig.isDebug) {
            Log.d(mDefaultTag, message);
        }
    }
}
