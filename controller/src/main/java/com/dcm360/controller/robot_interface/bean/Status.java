package com.dcm360.controller.robot_interface.bean;

public class Status {
    private String data;
    private String errorCode;
    private String msg;
    private boolean successed;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    @Override
    public String toString() {
        return "Status{" +
                "data='" + data + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", msg='" + msg + '\'' +
                ", successed=" + successed +
                '}';
    }
}
