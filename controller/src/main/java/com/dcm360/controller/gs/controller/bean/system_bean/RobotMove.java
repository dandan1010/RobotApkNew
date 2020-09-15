package com.dcm360.controller.gs.controller.bean.system_bean;

/**
 * @author LiYan
 * @create 2019/4/28
 * @Describe
 */
public class RobotMove {

    /**
     * speed : {"linearSpeed":0.5,"angularSpeed":0.2}
     */

    private SpeedBean speed;

    public SpeedBean getSpeed() {
        return speed;
    }

    public void setSpeed(SpeedBean speed) {
        this.speed = speed;
    }

    public static class SpeedBean {
        /**
         * linearSpeed : 0.5
         * angularSpeed : 0.2
         */

        private double linearSpeed;
        private double angularSpeed;

        public SpeedBean(double linearSpeed, double angularSpeed) {
            this.linearSpeed = linearSpeed;
            this.angularSpeed = angularSpeed;
        }

        public double getLinearSpeed() {
            return linearSpeed;
        }

        public void setLinearSpeed(double linearSpeed) {
            this.linearSpeed = linearSpeed;
        }

        public double getAngularSpeed() {
            return angularSpeed;
        }

        public void setAngularSpeed(double angularSpeed) {
            this.angularSpeed = angularSpeed;
        }
    }
}
