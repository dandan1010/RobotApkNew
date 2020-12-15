package com.dcm360.controller.gs.controller.bean.system_bean;

import java.util.List;

public class UltrasonicPhitBean {

    /**
     * mapInfo : {"gridHeight":100,"originX":-2.5,"originY":-2.5,"gridWidth":100,"resolution":0.05}
     * gridPhits : [{"x":47,"y":55},{"x":47,"y":75},{"x":44,"y":52},{"x":24,"y":52},{"x":44,"y":47},{"x":24,"y":47},{"x":47,"y":44},{"x":47,"y":44},{"x":52,"y":44},{"x":52,"y":44},{"x":55,"y":47},{"x":75,"y":47},{"x":55,"y":52},{"x":75,"y":52},{"x":52,"y":55},{"x":52,"y":75}]
     * header : {"frame_ids":["ultrasonic0","ultrasonic1","ultrasonic2","ultrasonic3","ultrasonic4","ultrasonic5","ultrasonic6","ultrasonic7"],"stamp":520775385}
     * worldPhits : [{"x":-0.13999999999999999,"y":0.255,"z":0},{"x":-0.13999999999999993,"y":1.255,"z":0},{"x":-0.255,"y":0.14000000000000004,"z":0},{"x":-1.2549999999932537,"y":0.13999632679489668,"z":0},{"x":-0.255,"y":-0.13999999999999999,"z":0},{"x":-1.2549999999932537,"y":-0.14000367320510335,"z":0},{"x":-0.14000000000000004,"y":-0.255,"z":0},{"x":-0.13999970614359836,"y":-0.294999999104851,"z":0},{"x":0.13999999999999999,"y":-0.255,"z":0},{"x":0.14000029385640167,"y":-0.294999999104851,"z":0},{"x":0.255,"y":-0.14000000000000004,"z":0},{"x":1.2549999999932537,"y":-0.1400036732051035,"z":0},{"x":0.255,"y":0.13999999999999999,"z":0},{"x":1.2549999999932537,"y":0.13999632679489651,"z":0},{"x":0.14000000000000004,"y":0.255,"z":0},{"x":0.1400000000000001,"y":1.255,"z":0}]
     */
    private MapInfoEntity mapInfo;
    private List<GridPhitsEntity> gridPhits;
    private HeaderEntity header;
    private List<WorldPhitsEntity> worldPhits;

    public void setMapInfo(MapInfoEntity mapInfo) {
        this.mapInfo = mapInfo;
    }

    public void setGridPhits(List<GridPhitsEntity> gridPhits) {
        this.gridPhits = gridPhits;
    }

    public void setHeader(HeaderEntity header) {
        this.header = header;
    }

    public void setWorldPhits(List<WorldPhitsEntity> worldPhits) {
        this.worldPhits = worldPhits;
    }

    public MapInfoEntity getMapInfo() {
        return mapInfo;
    }

    public List<GridPhitsEntity> getGridPhits() {
        return gridPhits;
    }

    public HeaderEntity getHeader() {
        return header;
    }

    public List<WorldPhitsEntity> getWorldPhits() {
        return worldPhits;
    }

    public class MapInfoEntity {
        /**
         * gridHeight : 100
         * originX : -2.5
         * originY : -2.5
         * gridWidth : 100
         * resolution : 0.05
         */
        private int gridHeight;
        private double originX;
        private double originY;
        private int gridWidth;
        private double resolution;

        public void setGridHeight(int gridHeight) {
            this.gridHeight = gridHeight;
        }

        public void setOriginX(double originX) {
            this.originX = originX;
        }

        public void setOriginY(double originY) {
            this.originY = originY;
        }

        public void setGridWidth(int gridWidth) {
            this.gridWidth = gridWidth;
        }

        public void setResolution(double resolution) {
            this.resolution = resolution;
        }

        public int getGridHeight() {
            return gridHeight;
        }

        public double getOriginX() {
            return originX;
        }

        public double getOriginY() {
            return originY;
        }

        public int getGridWidth() {
            return gridWidth;
        }

        public double getResolution() {
            return resolution;
        }
    }

    public class GridPhitsEntity {
        /**
         * x : 47
         * y : 55
         */
        private int x;
        private int y;

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public class HeaderEntity {
        /**
         * frame_ids : ["ultrasonic0","ultrasonic1","ultrasonic2","ultrasonic3","ultrasonic4","ultrasonic5","ultrasonic6","ultrasonic7"]
         * stamp : 520775385
         */
        private List<String> frame_ids;
        private int stamp;

        public void setFrame_ids(List<String> frame_ids) {
            this.frame_ids = frame_ids;
        }

        public void setStamp(int stamp) {
            this.stamp = stamp;
        }

        public List<String> getFrame_ids() {
            return frame_ids;
        }

        public int getStamp() {
            return stamp;
        }
    }

    public class WorldPhitsEntity {
        /**
         * x : -0.13999999999999999
         * y : 0.255
         * z : 0
         */
        private double x;
        private double y;
        private int z;

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(int z) {
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }
}
