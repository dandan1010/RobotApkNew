package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotPath {

    /**
     * data : [{"createdAt":"2016-08-11 04:11:49","fileName":"0f172cc6-0c72-4467-8ec7-bb7b68f90106.csv","id":0,"length":57.29019084917738,"mapId":0,"mapName":"demo","name":"main_path","pointCount":5730}]
     * errorCode :
     * msg : successed
     * successed : true
     */

    private String errorCode;
    private String msg;
    private boolean successed;
    private List<DataBean> data;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * createdAt : 2016-08-11 04:11:49
         * fileName : 0f172cc6-0c72-4467-8ec7-bb7b68f90106.csv
         * id : 0
         * length : 57.29019084917738
         * mapId : 0
         * mapName : demo
         * name : main_path
         * pointCount : 5730
         */

        private String createdAt;
        private String fileName;
        private int id;
        private double length;
        private int mapId;
        private String mapName;
        private String name;
        private int pointCount;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public int getMapId() {
            return mapId;
        }

        public void setMapId(int mapId) {
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

        public int getPointCount() {
            return pointCount;
        }

        public void setPointCount(int pointCount) {
            this.pointCount = pointCount;
        }
    }
}
