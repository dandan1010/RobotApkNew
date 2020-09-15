package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/13
 * @Describe
 */
public class SubclassPositionsDataWordPoseResponse {
    private SubclassWorldPoseOrientationRespones subclassWorldPoseOrientationRespones;
    private SubclassWorldPosePositionResponse subclassWorldPosePositionResponse;

    public SubclassWorldPoseOrientationRespones getSubclassWorldPoseOrientationRespones() {
        return subclassWorldPoseOrientationRespones;
    }

    public void setSubclassWorldPoseOrientationRespones(SubclassWorldPoseOrientationRespones subclassWorldPoseOrientationRespones) {
        this.subclassWorldPoseOrientationRespones = subclassWorldPoseOrientationRespones;
    }

    public SubclassWorldPosePositionResponse getSubclassWorldPosePositionResponse() {
        return subclassWorldPosePositionResponse;
    }

    public void setSubclassWorldPosePositionResponse(SubclassWorldPosePositionResponse subclassWorldPosePositionResponse) {
        this.subclassWorldPosePositionResponse = subclassWorldPosePositionResponse;
    }

    @Override
    public String toString() {
        return "SubclassPositionsDataWordPoseResponse{" +
                "subclassWorldPoseOrientationRespones=" + subclassWorldPoseOrientationRespones +
                ", subclassWorldPosePositionResponse=" + subclassWorldPosePositionResponse +
                '}';
    }
}
