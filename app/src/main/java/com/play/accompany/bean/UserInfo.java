package com.play.accompany.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.text.TextUtils;

import com.play.accompany.R;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.view.AccompanyApplication;

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

    @ColumnInfo(name = "show_id")
    private String userName;

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
    private Double gold;

    @Ignore
    private int gameZone;

    @ColumnInfo(name = "user_sign")
    private String sign;

    @Ignore
    private Double lbs;

    @Ignore
    private String address;

    @ColumnInfo(name = "user_url")
    private String url;

    @ColumnInfo(name = "user_invite_code")
    private String inviteCode;

    @Ignore
    private List<GameProperty> gameType;

    @ColumnInfo(name = "user_token")
    private String token;

    @ColumnInfo(name = "user_interest")
    private String interest;

    @ColumnInfo(name = "user_profession")
    private String profession;

    @ColumnInfo(name = "user_attention_followed")
    private Integer favor;

    @ColumnInfo(name = "user_other_game")
    private String otherGame;

    @Ignore
    private Integer favorListSize;

    @Ignore
    private Integer inviteFlag;

    @Ignore
    private Integer inviteNum;

    @Ignore
    private Double grade;

    @Ignore
    private List<String> favorList;

    @Ignore
    private Boolean fromChat;

    @Ignore
    private String wxOpenId;

    @Ignore
    private String wxUnionid;

    @Ignore
    private String wxImgurl;

    @Ignore
    private int orderNum;

    @Ignore
    private int userNameUpset;

    @Ignore
    private String audioUrl;

    @Ignore
    private int audioLen;

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public int getAudioLen() {
        return audioLen;
    }

    public void setAudioLen(int audioLen) {
        this.audioLen = audioLen;
    }

    public int getUserNameUpset() {
        return userNameUpset;
    }

    public void setUserNameUpset(int userNameUpset) {
        this.userNameUpset = userNameUpset;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getWxUnionid() {
        return wxUnionid;
    }

    public void setWxUnionid(String wxUnionid) {
        this.wxUnionid = wxUnionid;
    }

    public String getWxImgurl() {
        return wxImgurl;
    }

    public void setWxImgurl(String wxImgurl) {
        this.wxImgurl = wxImgurl;
    }

    public String getOtherGame() {
        return otherGame;
    }

    public void setOtherGame(String otherGame) {
        this.otherGame = otherGame;
    }

    public Integer getFavor() {
        return favor;
    }

    public void setFavor(Integer favor) {
        this.favor = favor;
    }

    public Integer getFavorListSize() {
        return favorListSize;
    }

    public void setFavorListSize(Integer favorListSize) {
        this.favorListSize = favorListSize;
    }

    public Boolean getFromChat() {
        return fromChat;
    }

    public void setFromChat(Boolean fromChat) {
        this.fromChat = fromChat;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public List<String> getFavorList() {
        return favorList;
    }

    public void setFavorList(List<String> favorList) {
        this.favorList = favorList;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Integer getInviteFlag() {
        return inviteFlag;
    }

    public void setInviteFlag(Integer inviteFlag) {
        this.inviteFlag = inviteFlag;
    }

    public Integer getInviteNum() {
        return inviteNum;
    }

    public void setInviteNum(Integer inviteNum) {
        this.inviteNum = inviteNum;
    }

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

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Double getGold() {
        return gold;
    }

    public void setGold(Double gold) {
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

    public Double getLbs() {
        return lbs;
    }

    public void setLbs(Double lbs) {
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

    public List<GameProperty> getGameType() {
        return gameType;
    }

    public void setGameType(List<GameProperty> gameType) {
        this.gameType = gameType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGamePrice(int type) {
        String text = "";
        if (type < 0) {
            return text;
        }
        if (gameType == null || gameType.isEmpty()) {
            return text;
        }
        for (GameProperty property : gameType) {
            if (property.getType() == type) {
                text = property.getPrice() + AccompanyApplication.getContext().getResources().getString(R.string.money) + "/" + (TextUtils.isEmpty(property.getUnit()) ? OtherConstant.DEFAULT_UNIT : property.getUnit());
                break;
            }
        }
        return text;
    }
}
