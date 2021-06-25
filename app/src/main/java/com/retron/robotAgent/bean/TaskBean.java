package com.retron.robotAgent.bean;

public class TaskBean extends Object{
    private String name ;
    private int x;
    private int y;
    private int disinfectTime;
    private double angle;


    public TaskBean(String name, int disinfectTime, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.disinfectTime = disinfectTime;
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

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
