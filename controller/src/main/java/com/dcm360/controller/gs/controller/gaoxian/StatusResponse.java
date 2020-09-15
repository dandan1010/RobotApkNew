package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/13
 * @Describe
 */
public class StatusResponse {
    private Integer statusCode;
    private String statusMsg;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "statusCode=" + statusCode +
                ", statusMsg='" + statusMsg + '\'' +
                '}';
    }
}
