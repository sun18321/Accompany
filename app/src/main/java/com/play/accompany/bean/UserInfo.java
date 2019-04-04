package com.play.accompany.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity
public class UserInfo implements Serializable {

    /**
     * id : 5c4c67d3e382ae5a218dbdf2
     * userId : 10000000001
     * name : 池鱼
     * gender : 0
     * date : 2000-01-31T16:00:00.000Z
     * type : 0
     * gameType : [1000,1001,1002]
     * price : 10
     * gold : 100
     * gameZone : 0
     * sign : 软甜又凶
     * lbs : 500
     * url : http://47.92.255.86:7070/peipeiImg/10000000001
     * inviteCode : TestCode
     * GameTypeName : ["王者荣耀","绝地求生","LOL"]
     */

    @PrimaryKey(autoGenerate = true)
    private Integer databaseId;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "user_name")
    private String name;

    @ColumnInfo(name = "user_gender")
    private int gender;

    @ColumnInfo(name = "user_date")
    private String date;

    @ColumnInfo (name = "user_type")
    private Integer type;

    @ColumnInfo(name = "user_price")
    private Integer price;

    @ColumnInfo(name = "user_gold")
    private Integer gold;

    @Ignore
    private int gameZone;

    @ColumnInfo(name = "user_sign")
    private String sign;

    @Ignore
    private Integer lbs;

    @ColumnInfo(name = "user_url")
    private String url;

    @ColumnInfo(name = "user_invite_code")
    private String inviteCode;

    @Ignore
    private List<Integer> gameType;

    @Ignore
    private List<String> GameTypeName;

    @ColumnInfo(name = "user_token")
    private String token;


    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getGameZone() {
        return gameZone;
    }

    public void setGameZone(int gameZone) {
        this.gameZone = gameZone;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getLbs() {
        return lbs;
    }

    public void setLbs(int lbs) {
        this.lbs = lbs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public List<Integer> getGameType() {
        return gameType;
    }

    public void setGameType(List<Integer> gameType) {
        this.gameType = gameType;
    }

    public List<String> getGameTypeName() {
        return GameTypeName;
    }

    public void setGameTypeName(List<String> GameTypeName) {
        this.GameTypeName = GameTypeName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
