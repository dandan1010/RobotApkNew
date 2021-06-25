package com.retron.robotAgent.bean;

public class RobotPointPositions {

        private double angle;
        private int gridX;
        private int gridY;
        private String name;
        private String mapName;
        private int type;
        private int pointTime;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getPointTime() {
        return pointTime;
    }

    public void setPointTime(int pointTime) {
        this.pointTime = pointTime;
    }

    public double getAngle() {
            return angle;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public int getGridX() {
            return gridX;
        }

        public void setGridX(int gridX) {
            this.gridX = gridX;
        }

        public int getGridY() {
            return gridY;
        }

        public void setGridY(int gridY) {
            this.gridY = gridY;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
}
