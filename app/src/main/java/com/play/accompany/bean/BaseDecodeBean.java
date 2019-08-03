package com.play.accompany.bean;

import java.util.List;

public class BaseDecodeBean<T> {

    /**
     * code : 1
     * msg : {"userId":"18555556688","token":"b9e3b5bfa3c81f43c6911df26239b3d9","time":1552917852303}
     */

    private int code;
    private T msg;
    private List<T> msgList;
    private String errMsg;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

    public List<T> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<T> msgList) {
        this.msgList = msgList;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
