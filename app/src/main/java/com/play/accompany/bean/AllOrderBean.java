package com.play.accompany.bean;

public class AllOrderBean {
    /**
     * id : 5c9ce3ea75919022519a4c6c
     * userId : 18555556688
     * targetId : 10000000005
     * price : 20
     * num : 1
     * typeGame : 1003
     * startTime : 1553875200000
     * sale : 0
     * state : 4
     * time : 1553787217141
     * remainMin : 0
     * remainSec : 0
     * name : 果er
     * showId : 10000000005
     */

    private String id;
    private String userId;
    private String targetId;
    private int price;
    private int num;
    private int typeGame;
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

    public int getTypeGame() {
        return typeGame;
    }

    public void setTypeGame(int typeGame) {
        this.typeGame = typeGame;
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

    /**
     * "id"        : 订单ID
     *                   "userId"    : 用户ID
     *                   "targetId"  : 陪玩的ID
     *                   "typeGame"  : 陪玩游戏的类型
     *                   "price"     : 陪玩单价
     *                   "num"       : 数量
     *                   "sale"      : 折扣，100：无折扣 90：九折
     *                   "comment"   : 备注
     *                   "startTime" : 开始时间，毫秒
     *                   "state"     : 订单状态 1：完成下单，未付款 3:已经付款 5：开始服务 7：服务完成 9：确认完成 未评价 11：评价完成
     *                                 退单状态 2:未付款 时间到自动取消 订单 4:完成下单 后退款 6：开始服务 后退款 8：服务完成 后退款
     *                   "time"      : 订单创建时间
     *                   "remainMin" : 不同的订单状态 剩余的分钟 0：不需要显示剩余时间，不用显示
     *                   "remainSec" : 不同的订单状态 剩余的秒数 0：不需要显示剩余时间，不用显示
     *                   "name"      : 显示订单对方的名字
     *                   "showId"    : 显示订单对方的ID,显示头像使用
     *
     */


}
