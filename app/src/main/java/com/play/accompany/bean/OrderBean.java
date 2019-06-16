package com.play.accompany.bean;

import java.io.Serializable;

public class OrderBean implements Serializable {
    /*
    * "token"    : 客户端登录时，保存的token
   "targetId" : 陪玩的ID
   "gameType" : 游戏类型的ID
   "price"    : 陪玩单价
   "num"      : 数量
   "sale"     : 折扣，100：无折扣 90：九折
   "comment"  : 备注
   "startTime": 开始时间，毫秒
     */

    private String token;

    private String targetId;

    private int gameType;

    private int price;

    private int num;

    private String comment;

    private long startTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getgameType() {
        return gameType;
    }

    public void setgameType(int gameType) {
        this.gameType = gameType;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
