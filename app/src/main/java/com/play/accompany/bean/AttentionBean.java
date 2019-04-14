package com.play.accompany.bean;

public class AttentionBean {
    /**
     * "token"     : 客户端登录时，保存的token
     * "targetId"  : 关注的用户ID
     * "flag"      : 关注和取消关注的标志 1：关注，0：取消关注
     */

    private String token;
    private String targetId;
    private int flag;

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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
