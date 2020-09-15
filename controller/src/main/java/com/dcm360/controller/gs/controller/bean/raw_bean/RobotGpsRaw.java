package com.dcm360.controller.gs.controller.bean.raw_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotGpsRaw {

    /**
     * data : {"altitude":0.12,"latitude":3.435,"longitude":3.234,"status":0}
     * errorCode :
     * msg : successed
     * successed : true
     */

    private DataBean data;
    private String errorCode;
    private String msg;
    private boolean successed;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
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

    public static class DataBean {
        /**
         * altitude : 0.12
         * latitude : 3.435
         * longitude : 3.234
         * status : 0
         */

        private double altitude;
        private double latitude;
        private double longitude;
        private int status;

        public double getAltitude() {
            return altitude;
        }

        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
