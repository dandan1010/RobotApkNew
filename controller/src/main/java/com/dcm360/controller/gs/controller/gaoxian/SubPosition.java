package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/23
 * @Describe
 */
public class SubPosition {
    private Double x;
    private Double y;
    private Double z;

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

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "SubPosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
