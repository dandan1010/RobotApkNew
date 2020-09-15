package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/13
 * @Describe
 */
public class SubclassWorldPosePositionResponse {
    private Double x;
    private Double y;
    private Integer z;

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

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "SubclassWorldPosePositionResponse{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
