package com.example.robot.bean;

import java.util.List;

public class PointStateBean {
    private String taskName;
    private List<PointState> list;

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
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PointStateBean{" +
                "taskName='" + taskName + '\'' +
                ", list=" + list +
                '}';
    }
}
