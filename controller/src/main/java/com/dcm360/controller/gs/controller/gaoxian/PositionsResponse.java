package com.dcm360.controller.gs.controller.gaoxian;

import java.util.List;

/**
 * @author LiYan
 * @create 2018/11/13
 * @Describe
 */
public class PositionsResponse {
    private List<SubclassPositionsDataResponse> data;
    private String errorCode;
    private String msg;
    private Boolean successed;

    public List<SubclassPositionsDataResponse> getData() {
        return data;
    }

    public void setData(List<SubclassPositionsDataResponse> data) {
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
        return "PositionsResponse{" +
                "data=" + data +
                ", errorCode='" + errorCode + '\'' +
                ", msg='" + msg + '\'' +
                ", successed=" + successed +
                '}';
    }
}
