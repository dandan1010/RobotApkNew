package com.dcm360.controller.gs.controller.bean.data_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotNonMapData {

    /**
     * data : {"non_map_value":[{"angle":-3.861445097052465,"distance":63.52965764660549,"gridPosition":{"x":304,"y":498}}]}
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
        private List<NonMapValueBean> non_map_value;

        public List<NonMapValueBean> getNon_map_value() {
            return non_map_value;
        }

        public void setNon_map_value(List<NonMapValueBean> non_map_value) {
            this.non_map_value = non_map_value;
        }

        public static class NonMapValueBean {
            /**
             * angle : -3.861445097052465
             * distance : 63.52965764660549
             * gridPosition : {"x":304,"y":498}
             */

            private double angle;
            private double distance;
            private GridPositionBean gridPosition;

            public double getAngle() {
                return angle;
            }

            public void setAngle(double angle) {
                this.angle = angle;
            }

            public double getDistance() {
                return distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }

            public GridPositionBean getGridPosition() {
                return gridPosition;
            }

            public void setGridPosition(GridPositionBean gridPosition) {
                this.gridPosition = gridPosition;
            }

            public static class GridPositionBean {
                /**
                 * x : 304
                 * y : 498
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
