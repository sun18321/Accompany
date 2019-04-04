package com.play.accompany.utils;

import android.text.TextUtils;

import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.view.AccompanyApplication;

public class UserInfoDatabaseUtils {
    public static void saveUserInfo(final UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        //存入数据库的同时，将token，gold,userid存入sp，使用更方便
        String token = userInfo.getToken();
        if (!TextUtils.isEmpty(token)) {
            SPUtils.getInstance().put(SpConstant.APP_TOKEN, token);
        }
        Integer gold = userInfo.getGold();
        if (gold != null) {
            int saveGold = gold;
            SPUtils.getInstance().put(SpConstant.MY_GOLDEN, saveGold);
        }

        final String userId = userInfo.getUserId();
        if (!TextUtils.isEmpty(userId)) {
            SPUtils.getInstance().put(SpConstant.MY_USER_ID, userId);
        }

        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                UserInfo info = AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().getUserInfo(userId);
                if (info == null) {
                    AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().insert(userInfo);
                } else {
                    AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().update(userInfo);
                }
            }
        });
    }


    public static void updateUserInfo(final UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }

        SPUtils.getInstance().put(SpConstant.IS_USER_EDIT, true);
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                UserInfo savedUserInfo = AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().getUserInfo(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
                savedUserInfo.setName(userInfo.getName());
                savedUserInfo.setGender(userInfo.getGender());
                savedUserInfo.setDate(userInfo.getDate());
                savedUserInfo.setSign(userInfo.getSign());
                AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().update(savedUserInfo);
            }
        });
    }

    public static void updateUrl(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        SPUtils.getInstance().put(SpConstant.IS_USER_EDIT, true);
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                UserInfo savedUserInfo = AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().getUserInfo(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
                savedUserInfo.setUrl(url);
                AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().update(savedUserInfo);
            }
        });
    }
}
