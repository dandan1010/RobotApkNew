package com.example.robot.bean;

import java.util.List;

public class PointStateBean {
    private String mapName;
    private String taskName;
    private List<PointState> list;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<PointState> getList() {
        return list;
    }

    public void setList(List<PointState> list) {
        this.list = list;
    }

    public static class PointState{
        private String pointName;
        private String pointState;
        private String timeCount;

        public String getTimeCount() {
            return timeCount;
        }

        public void setTimeCount(String timeCount) {
            this.timeCount = timeCount;
        }

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }

        public String getPointState() {
            return pointState;
        }

        public void setPointState(String pointState) {
            this.pointState = pointState;
        }

        @Override
        public String toString() {
            return "PointState{" +
                    "pointName='" + pointName + '\'' +
                    ", pointState='" + pointState + '\'' +
                    ", timeCount=" + timeCount +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PointStateBean{" +
                "mapName='" + mapName + '\'' +
                ", taskName='" + taskName + '\'' +
                ", list=" + list +
                '}';
    }
}
