package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/23
 * @Describe
 */
public class SubclassGridPositionResponse {
    private Double x;
    private Double y;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "SubclassGridPositionResponse{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
