package com.play.accompany.chat;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

@MessageTag(value = "accompany_order_response", flag = MessageTag.NONE)
public class OrderResponseMessage extends MessageContent {
    private String mUid;
    private String mResponse;
    private String mContent;
    private String mSendId;
    private final String mStringUid = "message_uid";
    private final String mStringResponse = "message_response";
    private final String mStringContent = "message_content";
    private final String mStringSendId = "message_send_id";

    public OrderResponseMessage(String uid, String response, String content, String sendId) {
        mUid = uid;
        mResponse = response;
        mContent = content;
        mSendId = sendId;
    }

    public OrderResponseMessage(Parcel parcel) {
        mUid = ParcelUtils.readFromParcel(parcel);
        mResponse = ParcelUtils.readFromParcel(parcel);
        mContent = ParcelUtils.readFromParcel(parcel);
        mSendId = ParcelUtils.readFromParcel(parcel);
    }

    public OrderResponseMessage(byte[] data) {
        super(data);
        try {
            String s = new String(data, "UTF-8");
            JSONObject object = new JSONObject(s);
            mUid = object.optString(mStringUid);
            mResponse = object.optString(mStringResponse);
            mContent = object.optString(mStringContent);
            mSendId = object.optString(mStringSendId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final Creator<OrderResponseMessage> CREATOR = new Creator<OrderResponseMessage>() {
        @Override
        public OrderResponseMessage createFromParcel(Parcel source) {
            return new OrderResponseMessage(source);
        }

        @Override
        public OrderResponseMessage[] newArray(int size) {
            return new OrderResponseMessage[size];
        }
    };

    @Override
    public byte[] encode() {
        JSONObject object = new JSONObject();
        try {
            object.put(mStringUid, mUid);
            object.put(mStringResponse, mResponse);
            object.put(mStringContent, mContent);
            object.put(mStringSendId, mSendId);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, mUid);
        ParcelUtils.writeToParcel(dest, mResponse);
        ParcelUtils.writeToParcel(dest, mContent);
        ParcelUtils.writeToParcel(dest, mSendId);
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getResponse() {
        return mResponse;
    }

    public void setResponse(String response) {
        mResponse = response;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getSendId() {
        return mSendId;
    }

    public void setSendId(String sendId) {
        mSendId = sendId;
    }
}
