package com.retron.robotAgent.bean;

import java.util.ArrayList;

public class RunningTaskBean {
    private String taskName;
    private String mapName;
    private String taskAlarm;
    private ArrayList<PointBean> arrayList;
    private String alarmCycle;
    private String isRunning;

    public String getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(String isRunning) {
        this.isRunning = isRunning;
    }

    public String getAlarmCycle() {
        return alarmCycle;
    }

    public void setAlarmCycle(String alarmCycle) {
        this.alarmCycle = alarmCycle;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getTaskAlarm() {
        return taskAlarm;
    }

    public void setTaskAlarm(String taskAlarm) {
        this.taskAlarm = taskAlarm;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public ArrayList<PointBean> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<PointBean> arrayList) {
        this.arrayList = arrayList;
    }

    public static class PointBean{
        private String pointName;
        private int spinnerIndex;
        private int pointX;
        private int pointY;

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }

        public int getSpinnerIndex() {
            return spinnerIndex;
        }

        public void setSpinnerIndex(int spinnerIndex) {
            this.spinnerIndex = spinnerIndex;
        }

        public int getPointX() {
            return pointX;
        }

        public void setPointX(int pointX) {
            this.pointX = pointX;
        }

        public int getPointY() {
            return pointY;
        }

        public void setPointY(int pointY) {
            this.pointY = pointY;
        }
    }
}
