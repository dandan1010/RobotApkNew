package com.example.robotapk.bean;

public class TaskBean extends Object{
    private String name ;
    private int x;
    private int y;
    private int disinfectTime;
    private int angle;

    public TaskBean(String name, int x, int y, int disinfectTime, int angle) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.disinfectTime = disinfectTime;
        this.angle = angle;
    }

    public TaskBean() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getDisinfectTime() {
        return disinfectTime;
    }

    public void setDisinfectTime(int disinfectTime) {
        this.disinfectTime = disinfectTime;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
