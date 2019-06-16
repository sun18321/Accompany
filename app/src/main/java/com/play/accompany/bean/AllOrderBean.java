package com.play.accompany.bean;

import java.io.Serializable;

public class AllOrderBean implements Serializable {
    /**
     *"id"            : 订单ID
     *"userId"        : 用户ID
     *"targetId"      : 陪玩的ID
     *"gameType"      : 陪玩游戏的类型
     *"price"         : 陪玩单价
     *"num"           : 数量
     *"sale"          : 折扣，100：无折扣 90：九折
     *"comment"       : 备注
     *"startTime"     : 开始时间，毫秒
     *"state"         : 订单流程: 1：完成下单，未付款 3:已经付款
     *        5：已接单 7：开始服务 9：服务完成
     *        11：确认完成 并且评价
     *退单流程 2: 未付款 时间到自动取消 或 自己主动的取消 订单
     *        4: 完成下单 未接单 或 主播主动取消订单
     *        8：开始服务 后退款 10：服务完成 后退款
     *"time"          : 订单创建时间
     *"remainMin"     : 不同的订单状态 剩余的分钟 0：不需要显示剩余时间，不用显示
     *"remainSec"     : 不同的订单状态 剩余的秒数 0：不需要显示剩余时间，不用显示
     *"name"          : 显示订单对方的名字
     *"url"           : 头像地址
     *"evaluateGrade" : 订单评价 等级 1-10级
     *"evaluate"      : 评价内容
     *"grade"         : 评分
     *"sign"          : 个人签名
     *"date"          : 生日
     */

    private String id;
    private String userId;
    private String targetId;
    private int gameType;
    private int price;
    private int num;
    private String comment;
    private long startTime;
    private int sale;
    private int state;
    private long time;
    private int remainMin;
    private int remainSec;
    private String name;
    private String showId;
    private String url;
    private int evaluateGrade;
    private String evaluate;
    private double grade;
    private int gender;

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String sign;
    private String date;

    public int getEvaluateGrade() {
        return evaluateGrade;
    }

    public void setEvaluateGrade(int evaluateGrade) {
        this.evaluateGrade = evaluateGrade;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getgameType() {
        return gameType;
    }

    public void setgameType(int gameType) {
        this.gameType = gameType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRemainMin() {
        return remainMin;
    }

    public void setRemainMin(int remainMin) {
        this.remainMin = remainMin;
    }

    public int getRemainSec() {
        return remainSec;
    }

    public void setRemainSec(int remainSec) {
        this.remainSec = remainSec;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}
