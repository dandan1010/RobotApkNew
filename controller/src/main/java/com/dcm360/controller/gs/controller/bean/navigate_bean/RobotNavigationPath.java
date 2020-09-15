package com.dcm360.controller.gs.controller.bean.navigate_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotNavigationPath {

    /**
     * data : {"gridPhits":[{"x":0,"y":0}],"mapInfo":{"gridHeight":736,"gridWidth":736,"originX":-17.6,"originY":-17.6,"resolution":0.05000000074505806}}
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
         * gridPhits : [{"x":0,"y":0}]
         * mapInfo : {"gridHeight":736,"gridWidth":736,"originX":-17.6,"originY":-17.6,"resolution":0.05000000074505806}
         */

        private MapInfoBean mapInfo;
        private List<GridPhitsBean> gridPhits;

        public MapInfoBean getMapInfo() {
            return mapInfo;
        }

        public void setMapInfo(MapInfoBean mapInfo) {
            this.mapInfo = mapInfo;
        }

        public List<GridPhitsBean> getGridPhits() {
            return gridPhits;
        }

        public void setGridPhits(List<GridPhitsBean> gridPhits) {
            this.gridPhits = gridPhits;
        }

        public static class MapInfoBean {
            /**
             * gridHeight : 736
             * gridWidth : 736
             * originX : -17.6
             * originY : -17.6
             * resolution : 0.05000000074505806
             */

            private int gridHeight;
            private int gridWidth;
            private double originX;
            private double originY;
            private double resolution;

            public int getGridHeight() {
                return gridHeight;
            }

            public void setGridHeight(int gridHeight) {
                this.gridHeight = gridHeight;
            }

            public int getGridWidth() {
                return gridWidth;
            }

            public void setGridWidth(int gridWidth) {
                this.gridWidth = gridWidth;
            }

            public double getOriginX() {
                return originX;
            }

            public void setOriginX(double originX) {
                this.originX = originX;
            }

            public double getOriginY() {
                return originY;
            }

            public void setOriginY(double originY) {
                this.originY = originY;
            }

            public double getResolution() {
                return resolution;
            }

            public void setResolution(double resolution) {
                this.resolution = resolution;
            }
        }

        public static class GridPhitsBean {
            /**
             * x : 0
             * y : 0
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
