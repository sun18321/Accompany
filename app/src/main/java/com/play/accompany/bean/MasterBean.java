package com.play.accompany.bean;

import java.util.List;

public class MasterBean {
    /**
     * token        : 客户端登录时，保存的token
     *     applyName    : 申请大神 真实姓名
     *     applyIdentity: 申请大神 身份证
     *     applyPhone   : 申请大神 手机号码
     *     applyAccount : 申请大神 账号，目前是微信账号
     *     gameType     : 可以陪玩游戏类型，格式：[ 1001,1003,…… ],
     *
     */

    private String token;
    private String applyName;
    private String applyIdentity;
    private String applyPhone;
    private String applyAccount;
    private List<Integer> gameType;

    public List<Integer> getGameType() {
        return gameType;
    }

    public void setGameType(List<Integer> gameType) {
        this.gameType = gameType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getApplyIdentity() {
        return applyIdentity;
    }

    public void setApplyIdentity(String applyIdentity) {
        this.applyIdentity = applyIdentity;
    }

    public String getApplyPhone() {
        return applyPhone;
    }

    public void setApplyPhone(String applyPhone) {
        this.applyPhone = applyPhone;
    }

    public String getApplyAccount() {
        return applyAccount;
    }

    public void setApplyAccount(String applyAccount) {
        this.applyAccount = applyAccount;
    }
}
