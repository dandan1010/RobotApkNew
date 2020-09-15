package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/8
 * @Describe 响应bean
 */
public class BaseResponse {

    private String data;
    private String errorCode;
    private String msg;
    private Boolean successed;

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

    public Boolean getSuccessed() {
        return successed;
    }

    public void setSuccessed(Boolean successed) {
        this.successed = successed;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "data='" + data + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", msg='" + msg + '\'' +
                ", successed=" + successed +
                '}';
    }
}
