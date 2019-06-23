package com.play.accompany.bean;

import java.util.List;

public class MasterCheckBean {

    /**
     * typeId : 1002
     * name : LOL
     * price : 10
     * unit : 小时
     * interval : [10,20,30]
     * isApply : 1
     */

    private int typeId;
    private String name;
    private int price;
    private String unit;
    private int isApply;
    private List<Integer> interval;
    private String urlGameType;

    public String getUrlGameType() {
        return urlGameType;
    }

    public void setUrlGameType(String urlGameType) {
        this.urlGameType = urlGameType;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getIsApply() {
        return isApply;
    }

    public void setIsApply(int isApply) {
        this.isApply = isApply;
    }

    public List<Integer> getInterval() {
        return interval;
    }

    public void setInterval(List<Integer> interval) {
        this.interval = interval;
    }
}
