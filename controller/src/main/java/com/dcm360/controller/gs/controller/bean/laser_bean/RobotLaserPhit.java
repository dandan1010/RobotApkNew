package com.dcm360.controller.gs.controller.bean.laser_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotLaserPhit {

    /**
     * header : {"stamp":14321212,"frame_id":"base_laser"}
     * mapInfo : {"gridWidth":1000,"gridHeight":1000,"originX":-41.273852042959405,"originY":-39.639522665878005,"resolution":0.05}
     * gridPhits : [{"x":111,"y":111},{"x":111,"y":111},{"x":111,"y":111}]
     * worldPhits : [{"x":-1.80135178E-12,"y":2.584042026E-8,"z":0},{"x":-1.80135178E-12,"y":2.584042026E-8,"z":0},{"x":-1.80135178E-12,"y":2.584042026E-8,"z":0}]
     */

    private HeaderBean header;
    private MapInfoBean mapInfo;
    private List<GridPhitsBean> gridPhits;
    private List<WorldPhitsBean> worldPhits;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

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

    public List<WorldPhitsBean> getWorldPhits() {
        return worldPhits;
    }

    public void setWorldPhits(List<WorldPhitsBean> worldPhits) {
        this.worldPhits = worldPhits;
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

    public static class MapInfoBean {
        /**
         * gridWidth : 1000
         * gridHeight : 1000
         * originX : -41.273852042959405
         * originY : -39.639522665878005
         * resolution : 0.05
         */

        private int gridWidth;
        private int gridHeight;
        private double originX;
        private double originY;
        private double resolution;

        public int getGridWidth() {
            return gridWidth;
        }

        public void setGridWidth(int gridWidth) {
            this.gridWidth = gridWidth;
        }

        public int getGridHeight() {
            return gridHeight;
        }

        public void setGridHeight(int gridHeight) {
            this.gridHeight = gridHeight;
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
         * x : 111
         * y : 111
         */

        private float x;
        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    public static class WorldPhitsBean {
        /**
         * x : -1.80135178E-12
         * y : 2.584042026E-8
         * z : 0
         */

        private double x;
        private double y;
        private int z;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
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
