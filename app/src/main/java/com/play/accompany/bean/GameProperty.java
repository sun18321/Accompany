package com.play.accompany.bean;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.play.accompany.R;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.view.AccompanyApplication;

import java.io.Serializable;

public class GameProperty implements Serializable {

    private int type;
    private int price;
    private String unit;
    private String name;
    private String url;

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @NonNull
    @Override
    public String toString() {
        return price + AccompanyApplication.getContext().getResources().getString(R.string.money) + "/" + (TextUtils.isEmpty(unit) ? OtherConstant.DEFAULT_UNIT : unit);
    }
}
