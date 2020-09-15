package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/23
 * @Describe
 */
public class SubclassExpectedDestinationResponse {
    private Double angle;
    private SubclassGridPositionResponse gridPosition;
    private String name;
    private SubWorldPosition worldPosition;

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public SubclassGridPositionResponse getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(SubclassGridPositionResponse gridPosition) {
        this.gridPosition = gridPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SubWorldPosition getWorldPosition() {
        return worldPosition;
    }

    public void setWorldPosition(SubWorldPosition worldPosition) {
        this.worldPosition = worldPosition;
    }

    @Override
    public String toString() {
        return "SubclassExpectedDestinationResponse{" +
                "angle=" + angle +
                ", gridPosition=" + gridPosition +
                ", name='" + name + '\'' +
                ", worldPosition=" + worldPosition +
                '}';
    }
}
