package com.dcm360.controller.gs.controller.bean.data_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotDeviceStatus {

    /**
     * data : {"battery":45.435484,"batteryVoltage":21.815,"charge":false,"charger":0,"chargerCurrent":0.604688,"chargerStatus":false,"chargerVoltage":0.021,"emergency":false,"emergencyStop":false,"indexUpdatedAt":1515901694,"mileage":null,"navigationSpeedLevel":1,"playPathSpeedLevel":2,"speed":0,"statusUpdatedAt":1515896014,"totalMileage":0,"uptime":5709}
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
         * battery : 45.435484
         * batteryVoltage : 21.815
         * charge : false
         * charger : 0
         * chargerCurrent : 0.604688
         * chargerStatus : false
         * chargerVoltage : 0.021
         * emergency : false
         * emergencyStop : false
         * indexUpdatedAt : 1515901694
         * mileage : null
         * navigationSpeedLevel : 1
         * playPathSpeedLevel : 2
         * speed : 0
         * statusUpdatedAt : 1515896014
         * totalMileage : 0
         * uptime : 5709
         */

        private double battery;
        private double batteryVoltage;
        private boolean charge;
        private int charger;
        private double chargerCurrent;
        private boolean chargerStatus;
        private double chargerVoltage;
        private boolean emergency;
        private boolean emergencyStop;
        private int indexUpdatedAt;
        private Object mileage;
        private int navigationSpeedLevel;
        private int playPathSpeedLevel;
        private int speed;
        private int statusUpdatedAt;
        private String totalMileage;
        private int uptime;

        public double getBattery() {
            return battery;
        }

        public void setBattery(double battery) {
            this.battery = battery;
        }

        public double getBatteryVoltage() {
            return batteryVoltage;
        }

        public void setBatteryVoltage(double batteryVoltage) {
            this.batteryVoltage = batteryVoltage;
        }

        public boolean isCharge() {
            return charge;
        }

        public void setCharge(boolean charge) {
            this.charge = charge;
        }

        public int getCharger() {
            return charger;
        }

        public void setCharger(int charger) {
            this.charger = charger;
        }

        public double getChargerCurrent() {
            return chargerCurrent;
        }

        public void setChargerCurrent(double chargerCurrent) {
            this.chargerCurrent = chargerCurrent;
        }

        public boolean isChargerStatus() {
            return chargerStatus;
        }

        public void setChargerStatus(boolean chargerStatus) {
            this.chargerStatus = chargerStatus;
        }

        public double getChargerVoltage() {
            return chargerVoltage;
        }

        public void setChargerVoltage(double chargerVoltage) {
            this.chargerVoltage = chargerVoltage;
        }

        public boolean isEmergency() {
            return emergency;
        }

        public void setEmergency(boolean emergency) {
            this.emergency = emergency;
        }

        public boolean isEmergencyStop() {
            return emergencyStop;
        }

        public void setEmergencyStop(boolean emergencyStop) {
            this.emergencyStop = emergencyStop;
        }

        public int getIndexUpdatedAt() {
            return indexUpdatedAt;
        }

        public void setIndexUpdatedAt(int indexUpdatedAt) {
            this.indexUpdatedAt = indexUpdatedAt;
        }

        public Object getMileage() {
            return mileage;
        }

        public void setMileage(Object mileage) {
            this.mileage = mileage;
        }

        public int getNavigationSpeedLevel() {
            return navigationSpeedLevel;
        }

        public void setNavigationSpeedLevel(int navigationSpeedLevel) {
            this.navigationSpeedLevel = navigationSpeedLevel;
        }

        public int getPlayPathSpeedLevel() {
            return playPathSpeedLevel;
        }

        public void setPlayPathSpeedLevel(int playPathSpeedLevel) {
            this.playPathSpeedLevel = playPathSpeedLevel;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getStatusUpdatedAt() {
            return statusUpdatedAt;
        }

        public void setStatusUpdatedAt(int statusUpdatedAt) {
            this.statusUpdatedAt = statusUpdatedAt;
        }

        public String getTotalMileage() {
            return totalMileage;
        }

        public void setTotalMileage(String totalMileage) {
            this.totalMileage = totalMileage;
        }

        public int getUptime() {
            return uptime;
        }

        public void setUptime(int uptime) {
            this.uptime = uptime;
        }
    }
}
