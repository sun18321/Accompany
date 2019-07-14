package com.play.accompany.chat;

import android.os.Parcel;

import com.play.accompany.bean.OrderState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 用于操作订单提前开始，提前结束
 */

@MessageTag(value = "accompany.order",flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class OrderMessage extends MessageContent {

    public static final int ORDER_EARLY_START = 999;
    public static final int ORDER_EARLY_END = 998;
    public static final String ORDER_DEAL_TYPE = "order_deal_type";
    public static final String SEND_ID = "send_id";
    public static final String TARGET_ID = "target_id";
    public static final String ORDER_STATE = "order_state";
    public static final String SEND_TIME = "send_time";
    public static final int ORDER_ACCEPT = 0;
    public static final int ORDER_REJECT = 1;

    private String mSendId;
    private String mTargetId;
    private int mOrderType;
    private int mOrderState;
    private long mSendTime;

    public OrderMessage(byte[] data) {
        super(data);
        try {
            String s = new String(data, "UTF-8");
            JSONObject object = new JSONObject(s);
            mSendId = object.optString(SEND_ID);
            mTargetId = object.optString(TARGET_ID);
            mOrderType = object.optInt(ORDER_DEAL_TYPE);
            mOrderState = object.optInt(ORDER_STATE);
            mSendTime = object.optLong(SEND_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OrderMessage(Parcel parcel) {
        mSendId = ParcelUtils.readFromParcel(parcel);
        mTargetId = ParcelUtils.readFromParcel(parcel);
        mOrderType = ParcelUtils.readIntFromParcel(parcel);
        mOrderState = ParcelUtils.readIntFromParcel(parcel);
        mSendTime = ParcelUtils.readLongFromParcel(parcel);
    }

    public OrderMessage(String sendId, String targetId, int orderType, long sendTime) {
        mSendId = sendId;
        mTargetId = targetId;
        mOrderType = orderType;
        mSendTime = sendTime;
    }

    @Override
    public byte[] encode() {
        JSONObject object = new JSONObject();
        try {
            object.put(SEND_ID, getSendId());
            object.put(TARGET_ID, getTargetId());
            object.put(ORDER_DEAL_TYPE, getOrderType());
            object.put(ORDER_STATE, getOrderState());
            object.put(SEND_TIME, getSendTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            return object.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final Creator<OrderMessage> CREATOR = new Creator<OrderMessage>() {
        @Override
        public OrderMessage createFromParcel(Parcel source) {
            return new OrderMessage(source);
        }

        @Override
        public OrderMessage[] newArray(int size) {
            return new OrderMessage[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, mSendId);
        ParcelUtils.writeToParcel(dest, mTargetId);
        ParcelUtils.writeToParcel(dest, mOrderType);
        ParcelUtils.writeToParcel(dest, mOrderState);
        ParcelUtils.writeToParcel(dest, mSendTime);
    }

    public String getSendId() {
        return mSendId;
    }

    public void setSendId(String sendId) {
        mSendId = sendId;
    }

    public String getTargetId() {
        return mTargetId;
    }

    public void setTargetId(String targetId) {
        mTargetId = targetId;
    }

    public int getOrderType() {
        return mOrderType;
    }

    public void setOrderType(int orderType) {
        mOrderType = orderType;
    }

    public int getOrderState() {
        return mOrderState;
    }

    public void setOrderState(int orderState) {
        mOrderState = orderState;
    }

    public long getSendTime() {
        return mSendTime;
    }

    public void setSendTime(long sendTime) {
        mSendTime = sendTime;
    }
}
