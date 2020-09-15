package com.dcm360.controller.gs.controller.bean.data_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotMobileData {

    /**
     * data : {"mobile_value":[{"angle":7.017333030700684,"distance":19,"gridPosition":{"x":353,"y":496},"is_human":0,"label":35,"objectAngle":132.7894287109375,"objectSpeed":1.2691669464111328,"possibility":0.8199999928474426}]}
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
        private List<MobileValueBean> mobile_value;

        public List<MobileValueBean> getMobile_value() {
            return mobile_value;
        }

        public void setMobile_value(List<MobileValueBean> mobile_value) {
            this.mobile_value = mobile_value;
        }

        public static class MobileValueBean {
            /**
             * angle : 7.017333030700684
             * distance : 19
             * gridPosition : {"x":353,"y":496}
             * is_human : 0
             * label : 35
             * objectAngle : 132.7894287109375
             * objectSpeed : 1.2691669464111328
             * possibility : 0.8199999928474426
             */

            private double angle;
            private int distance;
            private GridPositionBean gridPosition;
            private int is_human;
            private int label;
            private double objectAngle;
            private double objectSpeed;
            private double possibility;

            public double getAngle() {
                return angle;
            }

            public void setAngle(double angle) {
                this.angle = angle;
            }

            public int getDistance() {
                return distance;
            }

            public void setDistance(int distance) {
                this.distance = distance;
            }

            public GridPositionBean getGridPosition() {
                return gridPosition;
            }

            public void setGridPosition(GridPositionBean gridPosition) {
                this.gridPosition = gridPosition;
            }

            public int getIs_human() {
                return is_human;
            }

            public void setIs_human(int is_human) {
                this.is_human = is_human;
            }

            public int getLabel() {
                return label;
            }

            public void setLabel(int label) {
                this.label = label;
            }

            public double getObjectAngle() {
                return objectAngle;
            }

            public void setObjectAngle(double objectAngle) {
                this.objectAngle = objectAngle;
            }

            public double getObjectSpeed() {
                return objectSpeed;
            }

            public void setObjectSpeed(double objectSpeed) {
                this.objectSpeed = objectSpeed;
            }

            public double getPossibility() {
                return possibility;
            }

            public void setPossibility(double possibility) {
                this.possibility = possibility;
            }

            public static class GridPositionBean {
                /**
                 * x : 353
                 * y : 496
                 */

                private int x;
                private int y;

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
            }
        }
    }
}
