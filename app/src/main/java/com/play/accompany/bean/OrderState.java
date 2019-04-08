package com.play.accompany.bean;

public class OrderState {
    private int stateBackground;
    private String stateText;
    private int stateAction;
    private String tip;

    public int getStateAction() {
        return stateAction;
    }

    public void setStateAction(int stateAction) {
        this.stateAction = stateAction;
    }

    public int getStateBackground() {
        return stateBackground;
    }

    public void setStateBackground(int stateBackground) {
        this.stateBackground = stateBackground;
    }

    public String getStateText() {
        return stateText;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
