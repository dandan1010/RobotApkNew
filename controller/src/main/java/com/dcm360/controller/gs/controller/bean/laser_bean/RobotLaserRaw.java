package com.dcm360.controller.gs.controller.bean.laser_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotLaserRaw {

    /**
     * header : {"stamp":14321212,"frame_id":"base_laser"}
     * angle_min : -2.3
     * angle_max : 2.3
     * angle_increment : 0.00581718236207962
     * range_min : 0.05000000074505806
     * range_max : 10
     * ranges : [4.941999912261963,4.9710001945495605,2.4079999923706055]
     * intensities : [0,284,282,286,284,283,284,230,0,0]
     */

    private HeaderBean header;
    private double angle_min;
    private double angle_max;
    private double angle_increment;
    private double range_min;
    private int range_max;
    private List<Double> ranges;
    private List<Integer> intensities;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public double getAngle_min() {
        return angle_min;
    }

    public void setAngle_min(double angle_min) {
        this.angle_min = angle_min;
    }

    public double getAngle_max() {
        return angle_max;
    }

    public void setAngle_max(double angle_max) {
        this.angle_max = angle_max;
    }

    public double getAngle_increment() {
        return angle_increment;
    }

    public void setAngle_increment(double angle_increment) {
        this.angle_increment = angle_increment;
    }

    public double getRange_min() {
        return range_min;
    }

    public void setRange_min(double range_min) {
        this.range_min = range_min;
    }

    public int getRange_max() {
        return range_max;
    }

    public void setRange_max(int range_max) {
        this.range_max = range_max;
    }

    public List<Double> getRanges() {
        return ranges;
    }

    public void setRanges(List<Double> ranges) {
        this.ranges = ranges;
    }

    public List<Integer> getIntensities() {
        return intensities;
    }

    public void setIntensities(List<Integer> intensities) {
        this.intensities = intensities;
    }

    public static class HeaderBean {
        /**
         * stamp : 14321212
         * frame_id : base_laser
         */

        private int stamp;
        private String frame_id;

        public int getStamp() {
            return stamp;
        }

        public void setStamp(int stamp) {
            this.stamp = stamp;
        }

        public String getFrame_id() {
            return frame_id;
        }

        public void setFrame_id(String frame_id) {
            this.frame_id = frame_id;
        }
    }
}
