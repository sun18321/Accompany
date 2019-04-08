package com.play.accompany.bean;

import java.io.Serializable;

public class IntentPayInfo implements Serializable {
    private String url;
    private String name;
    private String game;
    private String detail;
    private int all;
    private String id;

    public IntentPayInfo(String url, String name, String game, String detail, int all, String id) {
        this.url = url;
        this.name = name;
        this.game = game;
        this.detail = detail;
        this.all = all;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
