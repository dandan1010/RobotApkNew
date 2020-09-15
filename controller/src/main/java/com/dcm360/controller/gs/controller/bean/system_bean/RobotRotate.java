package com.dcm360.controller.gs.controller.bean.system_bean;

/**
 * @author LiYan
 * @create 2019/4/28
 * @Describe
 */
public class RobotRotate {

    /**
     * rotateAngle : 30
     * rotateSpeed : 0.3
     */

    private int rotateAngle;
    private double rotateSpeed;

    public int getRotateAngle() {
        return rotateAngle;
    }

    public void setRotateAngle(int rotateAngle) {
        this.rotateAngle = rotateAngle;
    }

    public double getRotateSpeed() {
        return rotateSpeed;
    }

    public void setRotateSpeed(double rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }
}
