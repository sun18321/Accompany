package com.play.accompany.bean;

public class EditPriceBean {
    private String token;
    private GameProperty gameTypeDao;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GameProperty getGameTypeDao() {
        return gameTypeDao;
    }

    public void setGameTypeDao(GameProperty gameTypeDao) {
        this.gameTypeDao = gameTypeDao;
    }
}
