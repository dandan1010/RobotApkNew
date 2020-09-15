package com.dcm360.controller.gs.controller.bean.gps_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotMapGPS {

    /**
     * angle : -173.41528128678252
     * gridPosition : {"x":372,"y":502}
     * mapInfo : {"gridHeight":992,"gridWidth":992,"originX":-24.8,"originY":-24.8,"resolution":0.05000000074505806}
     * worldPosition : {"orientation":{"w":-0.05743089347363588,"x":0,"y":0,"z":0.9983494841361015},"position":{"x":-6.189813393986145,"y":0.3017086724551712,"z":0}}
     */

    private double angle;
    private GridPositionBean gridPosition;
    private MapInfoBean mapInfo;
    private WorldPositionBean worldPosition;

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public GridPositionBean getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(GridPositionBean gridPosition) {
        this.gridPosition = gridPosition;
    }

    public MapInfoBean getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(MapInfoBean mapInfo) {
        this.mapInfo = mapInfo;
    }

    public WorldPositionBean getWorldPosition() {
        return worldPosition;
    }

    public void setWorldPosition(WorldPositionBean worldPosition) {
        this.worldPosition = worldPosition;
    }

    public static class GridPositionBean {
        /**
         * x : 372
         * y : 502
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

    public static class MapInfoBean {
        /**
         * gridHeight : 992
         * gridWidth : 992
         * originX : -24.8
         * originY : -24.8
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

    public static class WorldPositionBean {
        /**
         * orientation : {"w":-0.05743089347363588,"x":0,"y":0,"z":0.9983494841361015}
         * position : {"x":-6.189813393986145,"y":0.3017086724551712,"z":0}
         */

        private OrientationBean orientation;
        private PositionBean position;

        public OrientationBean getOrientation() {
            return orientation;
        }

        public void setOrientation(OrientationBean orientation) {
            this.orientation = orientation;
        }

        public PositionBean getPosition() {
            return position;
        }

        public void setPosition(PositionBean position) {
            this.position = position;
        }

        public static class OrientationBean {
            /**
             * w : -0.05743089347363588
             * x : 0
             * y : 0
             * z : 0.9983494841361015
             */

            private double w;
            private int x;
            private int y;
            private double z;

            public double getW() {
                return w;
            }

            public void setW(double w) {
                this.w = w;
            }

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

        public static class PositionBean {
            /**
             * x : -6.189813393986145
             * y : 0.3017086724551712
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
}
