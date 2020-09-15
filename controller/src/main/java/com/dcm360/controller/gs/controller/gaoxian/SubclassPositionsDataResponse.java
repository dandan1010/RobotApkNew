package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/13
 * @Describe
 */
public class SubclassPositionsDataResponse {
    private Double angle;
    private String createdAt;
    private Integer gridX;
    private Integer gridY;
    private Integer id;
    private String mapId;
    private String mapName;
    private String name;
    private Integer type;
    private SubclassPositionsDataWordPoseResponse subclassPositionsDataWordPoseResponse;

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getGridX() {
        return gridX;
    }

    public void setGridX(Integer gridX) {
        this.gridX = gridX;
    }

    public Integer getGridY() {
        return gridY;
    }

    public void setGridY(Integer gridY) {
        this.gridY = gridY;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public SubclassPositionsDataWordPoseResponse getSubclassPositionsDataWordPoseResponse() {
        return subclassPositionsDataWordPoseResponse;
    }

    public void setSubclassPositionsDataWordPoseResponse(SubclassPositionsDataWordPoseResponse subclassPositionsDataWordPoseResponse) {
        this.subclassPositionsDataWordPoseResponse = subclassPositionsDataWordPoseResponse;
    }

    @Override
    public String toString() {
        return "SubclassPositionsDataResponse{" +
                "angle=" + angle +
                ", createdAt='" + createdAt + '\'' +
                ", gridX=" + gridX +
                ", gridY=" + gridY +
                ", id=" + id +
                ", mapId=" + mapId +
                ", mapName='" + mapName + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", subclassPositionsDataWordPoseResponse=" + subclassPositionsDataWordPoseResponse +
                '}';
    }
}
