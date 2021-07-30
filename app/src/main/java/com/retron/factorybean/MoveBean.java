package com.retron.factorybean;

public class MoveBean {

     /*
    * "simple": "bool, true"
      "speed": "string, low|normal|high",
      "direction": "string, left|right|forward|backward|stop|"*/

    private float linearSpeed;
    private float angularSpeed;
    private boolean simple;
    private String speed;
    private String direction;

    public float getLinearSpeed() {
        return linearSpeed;
    }

    public void setLinearSpeed(float linearSpeed) {
        this.linearSpeed = linearSpeed;
    }

    public float getAngularSpeed() {
        return angularSpeed;
    }

    public void setAngularSpeed(float angularSpeed) {
        this.angularSpeed = angularSpeed;
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
