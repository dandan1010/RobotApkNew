package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/23
 * @Describe
 */
public class NavigationStatusDataResponse {
    private SubclassExpectedDestinationResponse expectedDestination;

    public SubclassExpectedDestinationResponse getExpectedDestination() {
        return expectedDestination;
    }

    public void setExpectedDestination(SubclassExpectedDestinationResponse expectedDestination) {
        this.expectedDestination = expectedDestination;
    }

    @Override
    public String toString() {
        return "NavigationStatusDataResponse{" +
                "expectedDestination=" + expectedDestination +
                '}';
    }
}
