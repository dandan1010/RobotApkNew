package com.dcm360.controller.gs.controller.bean.gps_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotSyncGpsData {

    /**
     * time : 1512518400
     * altitude : 3.23
     * latitude : 3.254
     * longitude : 1.45
     * speed : 0.6
     * trueAngle : 23
     * magneticAngle : 23
     * status : 1
     * type : A
     */

    private int time;
    private double altitude;
    private double latitude;
    private double longitude;
    private double speed;
    private int trueAngle;
    private int magneticAngle;
    private int status;
    private String type;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getTrueAngle() {
        return trueAngle;
    }

    public void setTrueAngle(int trueAngle) {
        this.trueAngle = trueAngle;
    }

    public int getMagneticAngle() {
        return magneticAngle;
    }

    public void setMagneticAngle(int magneticAngle) {
        this.magneticAngle = magneticAngle;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
