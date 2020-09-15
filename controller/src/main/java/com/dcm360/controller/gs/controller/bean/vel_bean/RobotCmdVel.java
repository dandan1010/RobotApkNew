package com.dcm360.controller.gs.controller.bean.vel_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotCmdVel {

    /**
     * data : {"angular":{"x":0,"y":0,"z":-0.1},"linear":{"x":0.2,"y":0,"z":0}}
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
         * angular : {"x":0,"y":0,"z":-0.1}
         * linear : {"x":0.2,"y":0,"z":0}
         */

        private AngularBean angular;
        private LinearBean linear;

        public AngularBean getAngular() {
            return angular;
        }

        public void setAngular(AngularBean angular) {
            this.angular = angular;
        }

        public LinearBean getLinear() {
            return linear;
        }

        public void setLinear(LinearBean linear) {
            this.linear = linear;
        }

        public static class AngularBean {
            /**
             * x : 0
             * y : 0
             * z : -0.1
             */

            private int x;
            private int y;
            private double z;

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }

            public double getZ() {
                return z;
            }

            public void setZ(double z) {
                this.z = z;
            }
        }

        public static class LinearBean {
            /**
             * x : 0.2
             * y : 0
             * z : 0
             */

            private double x;
            private int y;
            private int z;

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }

            public int getZ() {
                return z;
            }

            public void setZ(int z) {
                this.z = z;
            }
        }
    }
}
