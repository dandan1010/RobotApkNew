package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/23
 * @Describe
 */
public class SubWorldPosition {
    private  SubclassOrientation orientation;
    private SubPosition position;

    public SubclassOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(SubclassOrientation orientation) {
        this.orientation = orientation;
    }

    public SubPosition getPosition() {
        return position;
    }

    public void setPosition(SubPosition position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SubWorldPosition{" +
                "orientation=" + orientation +
                ", position=" + position +
                '}';
    }
}
