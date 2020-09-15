package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/23
 * @Describe
 */
public class NavigationStatusResponse {
    private NavigationStatusDataResponse data;
    private String noticeDataFields;
    private String noticeType;
    private String noticeTypeLevel;

    public NavigationStatusDataResponse getData() {
        return data;
    }

    public void setData(NavigationStatusDataResponse data) {
        this.data = data;
    }

    public String getNoticeDataFields() {
        return noticeDataFields;
    }

    public void setNoticeDataFields(String noticeDataFields) {
        this.noticeDataFields = noticeDataFields;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeTypeLevel() {
        return noticeTypeLevel;
    }

    public void setNoticeTypeLevel(String noticeTypeLevel) {
        this.noticeTypeLevel = noticeTypeLevel;
    }

    @Override
    public String toString() {
        return "NavigationStatusResponse{" +
                "data=" + data +
                ", noticeDataFields='" + noticeDataFields + '\'' +
                ", noticeType='" + noticeType + '\'' +
                ", noticeTypeLevel='" + noticeTypeLevel + '\'' +
                '}';
    }
}
