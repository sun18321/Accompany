package com.play.accompany.utils;

import android.text.TextUtils;

import com.play.accompany.bean.FavoriteInfo;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.view.AccompanyApplication;

import java.util.ArrayList;
import java.util.List;

public class UserInfoDatabaseUtils {
    private static UserInfoDatabaseUtils mUtils;

    public static UserInfoDatabaseUtils getInstance() {
        if (mUtils == null) {
            synchronized (UserInfoDatabaseUtils.class) {
                if (mUtils == null) {
                    mUtils = new UserInfoDatabaseUtils();
                }
            }
        }
            return mUtils;
    }

    public void saveUserInfo(final UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        //存入数据库的同时，将token，gold,userid,邀请码，存入sp，使用更方便
        String token = userInfo.getToken();
        if (!TextUtils.isEmpty(token)) {
            SPUtils.getInstance().put(SpConstant.APP_TOKEN, token);
        }
        Double gold = userInfo.getGold();
        if (gold != null) {
            double saveGold = gold;
            SPUtils.getInstance().put(SpConstant.MY_GOLDEN, saveGold);
        }

        final String userId = userInfo.getUserId();
        if (!TextUtils.isEmpty(userId)) {
            SPUtils.getInstance().put(SpConstant.MY_USER_ID, userId);
        }

        String name = userInfo.getName();
        if (!TextUtils.isEmpty(name)) {
            SPUtils.getInstance().put(SpConstant.MY_USER_NAME, name);
        }

        String url = userInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            SPUtils.getInstance().put(SpConstant.MY_USER_URL, url);
        }

        String inviteCode = userInfo.getInviteCode();
        if (!TextUtils.isEmpty(inviteCode)) {
            SPUtils.getInstance().put(SpConstant.MY_INVITE_CODE, inviteCode);
        }

        int upset = userInfo.getUserNameUpset();
        if (upset == OtherConstant.ID_EDITED) {
            SPUtils.getInstance().put(SpConstant.ID_IS_EDITED, true);
        } else {
            SPUtils.getInstance().put(SpConstant.ID_IS_EDITED, false);
        }

        Integer inviteFlag = userInfo.getInviteFlag();
        if (inviteFlag != null) {
            int flag = inviteFlag;
            boolean invited;
            if (flag == OtherConstant.INVITE_SUBMITED) {
                invited = true;
            } else {
                invited = false;
            }
            SPUtils.getInstance().put(SpConstant.INVITE_FLAG, invited);
        }

        Integer inviteNum = userInfo.getInviteNum();
        if (inviteNum != null) {
            int num = inviteNum;
            SPUtils.getInstance().put(SpConstant.INVITE_NUMBER, num);
        }

        Integer type = userInfo.getType();
        if (type != null) {
            int userType = type;
            SPUtils.getInstance().put(SpConstant.USER_TYPE, userType);
        }

        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                UserInfo info = AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().getUserInfo(userId);
                if (info == null) {
                    AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().insert(userInfo);
                } else {
                    Integer databaseId = info.getDatabaseId();
                    userInfo.setDatabaseId(databaseId);
                    AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().update(userInfo);
                }
            }
        });

        List<String> favorList = userInfo.getFavorList();
        saveFavorite(favorList);
    }


    public void updateUserInfo(final UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }

        SPUtils.getInstance().put(SpConstant.IS_USER_EDIT, true);
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                UserInfo savedUserInfo = AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().getUserInfo(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
                savedUserInfo.setUserName(userInfo.getUserName());
                savedUserInfo.setName(userInfo.getName());
                savedUserInfo.setGender(userInfo.getGender());
                savedUserInfo.setDate(userInfo.getDate());
                savedUserInfo.setSign(userInfo.getSign());
                savedUserInfo.setInterest(userInfo.getInterest());
                savedUserInfo.setProfession(userInfo.getProfession());
                savedUserInfo.setOtherGame(userInfo.getOtherGame());
                AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getUserDao().update(savedUserInfo);
            }
        });
    }

    public void updateUrl(final String url) {
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

    private void saveFavorite(final List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        AccompanyApplication.setAttentionList(list);
    }

//    private static void saveFavorite(final List<String> list) {
//        if (list.isEmpty()) {
//            return;
//        }
//        int count = list.size();
//        SPUtils.getInstance().put(SpConstant.ATTENTION_COUNT, count);
//
//        ThreadPool.newInstance().add(new Runnable() {
//            @Override
//            public void run() {
//                List<FavoriteInfo> saveList = AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getFavoriteDao().getAllFavoriteByUserId(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
//                if (saveList.isEmpty()) {
//                    List<FavoriteInfo> insertList = getFavoriteList(list);
//                    AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getFavoriteDao().insertList(insertList);
//                } else {
//                    if (saveList.size() > list.size()) {
//                        List<FavoriteInfo> updateList = getFavoriteList(list);
//                        AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getFavoriteDao().deleteAll(saveList);
//                        AccompanyDatabase.getInstance(AccompanyApplication.getContext()).getFavoriteDao().insertList(updateList);
//                    }
//                }
//            }
//        });
//
//    }

//    private static List<FavoriteInfo> getFavoriteList(List<String> list) {
//        List<FavoriteInfo> favoriteList = new ArrayList<>();
//        for (String s : list) {
//            FavoriteInfo info = new FavoriteInfo();
//            info.setFavoriteId(s);
//            info.setUserId(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
//            favoriteList.add(info);
//        }
//        return favoriteList;
//    }
}
