package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/13
 * @Describe
 */
public class SubclassWorldPoseOrientationRespones {

    private Double w;
    private Integer x;
    private Integer y;
    private Double z;

    public Double getW() {
        return w;
    }

    public void setW(Double w) {
        this.w = w;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
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
        return "SubclassWorldPoseOrientationRespones{" +
                "w=" + w +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
