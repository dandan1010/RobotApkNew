package com.dcm360.controller.gs.controller.bean.data_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotFootprint {

    /**
     * gridPhits : [{"x":44,"y":42},{"x":44,"y":55},{"x":45,"y":56},{"x":45,"y":57},{"x":54,"y":57},{"x":54,"y":56},{"x":55,"y":55},{"x":55,"y":42}]
     * mapInfo : {"gridHeight":100,"gridWidth":100,"originX":-2.5,"originY":-2.5,"resolution":0.05}
     * worldPhits : [{"x":-0.26,"y":-0.37,"z":0},{"x":-0.26,"y":0.28,"z":0},{"x":-0.23999999999999996,"y":0.335,"z":0},{"x":-0.19999999999999998,"y":0.36,"z":0},{"x":0.20000000000000004,"y":0.36,"z":0},{"x":0.24000000000000002,"y":0.335,"z":0},{"x":0.26,"y":0.28,"z":0},{"x":0.26,"y":-0.37,"z":0}]
     */

    private MapInfoBean mapInfo;
    private List<GridPhitsBean> gridPhits;
    private List<WorldPhitsBean> worldPhits;

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

    public static class MapInfoBean {
        /**
         * gridHeight : 100
         * gridWidth : 100
         * originX : -2.5
         * originY : -2.5
         * resolution : 0.05
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
         * x : 44
         * y : 42
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

    public static class WorldPhitsBean {
        /**
         * x : -0.26
         * y : -0.37
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
