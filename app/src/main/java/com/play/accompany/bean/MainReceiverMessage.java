package com.play.accompany.bean;

import java.io.Serializable;

public class MainReceiverMessage implements Serializable {
    public static final int TYPE_ORDER = 999;
    public static final int TYPE_REMAIN = 998;

    private int remainMessage;
    private int messageType;

    public int getRemainMessage() {
        return remainMessage;
    }

    public void setRemainMessage(int remainMessage) {
        this.remainMessage = remainMessage;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
