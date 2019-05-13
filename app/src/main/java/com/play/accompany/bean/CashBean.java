package com.play.accompany.bean;

import java.io.Serializable;

public class CashBean implements Serializable {


    /**
     * isCash : 0
     * cashMin : 5
     * cashMax : 6
     * cashInteger : 100
     * cashDailyNum : 9
     */

    private int isCash;
    private int cashMin;
    private int cashMax;
    private int cashInteger;
    private int cashDailyNum;

    public int getIsCash() {
        return isCash;
    }

    public void setIsCash(int isCash) {
        this.isCash = isCash;
    }

    public int getCashMin() {
        return cashMin;
    }

    public void setCashMin(int cashMin) {
        this.cashMin = cashMin;
    }

    public int getCashMax() {
        return cashMax;
    }

    public void setCashMax(int cashMax) {
        this.cashMax = cashMax;
    }

    public int getCashInteger() {
        return cashInteger;
    }

    public void setCashInteger(int cashInteger) {
        this.cashInteger = cashInteger;
    }

    public int getCashDailyNum() {
        return cashDailyNum;
    }

    public void setCashDailyNum(int cashDailyNum) {
        this.cashDailyNum = cashDailyNum;
    }
}
