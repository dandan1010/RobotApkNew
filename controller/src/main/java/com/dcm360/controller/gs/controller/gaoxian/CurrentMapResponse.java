package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/13
 * @Describe
 */
public class CurrentMapResponse {
    private String currentMap;
    private String currentInitPoint;

    public String getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public String getCurrentInitPoint() {
        return currentInitPoint;
    }

    public void setCurrentInitPoint(String currentInitPoint) {
        this.currentInitPoint = currentInitPoint;
    }

    @Override
    public String toString() {
        return "CurrentMapResponse{" +
                "currentMap='" + currentMap + '\'' +
                ", currentInitPoint='" + currentInitPoint + '\'' +
                '}';
    }
}
