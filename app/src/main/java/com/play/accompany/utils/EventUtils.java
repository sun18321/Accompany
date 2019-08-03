package com.play.accompany.utils;

import android.text.TextUtils;

import com.play.accompany.constant.SpConstant;
import com.play.accompany.view.AccompanyApplication;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class EventUtils {
    private final String EDIT_PRICE = "event_edit_price";
    private final String REJECT_ORDER = "event_reject_order";
    private final String ORDER_COMMENT = "event_order_comment";
    private final String PAY = "event_pay";
    private final String MASTER_CLICK = "event_master_click";
    private final String CLICK_GAME_TYPE = "event_click_game_type";
    private final String USER_TYPE = "event_user_type";
    private final String CHAT = "event_chat";
    private final String OPEN_ACTIVITY = "event_open_activity";
    private final String CREATE_ORDER = "event_create_order";
    private final String APPLY_MASTER = "event_apply_master";
    private final String TAG = "upload";
    private final String CLICK_ORDER = "event_click_order";
    private final String ACCEPT_ORDER = "event_accept_order";

    private static EventUtils mUtils;

    private EventUtils() {

    }

    public static EventUtils getInstance() {
        if (mUtils == null) {
            synchronized (EventUtils.class) {
                if (mUtils == null) {
                    mUtils = new EventUtils();
                }
            }
        }
        return mUtils;
    }

    public void upUserType() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("type", String.valueOf(SPUtils.getInstance().getInt(SpConstant.USER_TYPE)));
        MobclickAgent.onEvent(AccompanyApplication.getContext(), USER_TYPE, hashMap);

        LogUtils.d(TAG, "up user");
    }

    public void upEditPrice(String game, int oldPrice, int newPrice) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("game", game);
        hashMap.put("old_price", String.valueOf(oldPrice));
        hashMap.put("new_price", String.valueOf(newPrice));
        MobclickAgent.onEvent(AccompanyApplication.getContext(), EDIT_PRICE, hashMap);
    }

    public void upPay(int money, String orderId) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(orderId)) {
            hashMap.put("order_id", orderId);
        }
        hashMap.put("money", String.valueOf(money));
        MobclickAgent.onEvent(AccompanyApplication.getContext(), PAY, hashMap);
    }

    public void upCreateOrder(String createTime, String startTime, String orderId, String masterId, String game, String money, String count, String mark) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("create_time", createTime);
        hashMap.put("start_time", startTime);
        hashMap.put("order_id", orderId);
        hashMap.put("mater_id", masterId);
        hashMap.put("game", game);
        hashMap.put("money", money);
        hashMap.put("count", count);
        hashMap.put("mark", mark);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), CREATE_ORDER, hashMap);
    }

    public void upRejectOrder(String time, String orderId, String masterId, String customId, String game) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("time", time);
        hashMap.put("order_id", orderId);
        hashMap.put("master_id", masterId);
        hashMap.put("custom_id", customId);
        hashMap.put("game", game);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), REJECT_ORDER, hashMap);
    }

    public void upOrderComment(String customId, String masterId, String orderId, String game, String text, String grade, String money, String count) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("custom_id", customId);
        hashMap.put("master_id", masterId);
        hashMap.put("order_id", orderId);
        hashMap.put("game", game);
        hashMap.put("comment", text);
        hashMap.put("grade", grade);
        hashMap.put("money", money);
        hashMap.put("count", count);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), ORDER_COMMENT, hashMap);
    }

    public void upClickGameType(String game, String time) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("game", game);
        hashMap.put("time", time);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), CLICK_GAME_TYPE, hashMap);
    }

    public void upMasterClick(String index, String masterId, String game, String time) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("index", index);
        hashMap.put("custom_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("master_id", masterId);
        hashMap.put("game", game);
        hashMap.put("time", time);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), MASTER_CLICK, hashMap);
    }

    public void upApplyMaster(String game, String time) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("game", game);
        hashMap.put("time", time);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), APPLY_MASTER, hashMap);
    }


    public void upChat(String targetId, String content) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("send_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("target_id", targetId);
        hashMap.put("content", content);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), CHAT, hashMap);
    }

    public void upOpenActivity(String time) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("type", String.valueOf(SPUtils.getInstance().getInt(SpConstant.USER_TYPE)));
        hashMap.put("time", time);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), OPEN_ACTIVITY, hashMap);
    }

    public void upClickOrder(String targetId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("master_id", targetId);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), CLICK_ORDER, hashMap);
    }

    public void upAcceptOrder(String targetId, String orderId, String game, String time, String num, String money) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        hashMap.put("master_id", targetId);
        hashMap.put("order_id", orderId);
        hashMap.put("game", game);
        hashMap.put("time", time);
        hashMap.put("count", num);
        hashMap.put("money", money);
        MobclickAgent.onEvent(AccompanyApplication.getContext(), ACCEPT_ORDER, hashMap);
    }
}
