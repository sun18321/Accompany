package com.play.accompany.bean;

public class StateResponseBean {
    private int state;
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrdrId(String orderId) {
        this.orderId = orderId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
