package com.dcm360.controller.gs.controller.bean.map_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotMap {


    /**
     * data : [{"DIYPngName":"","createdAt":"2019-01-31 09:51:40","dataFileName":"16abfb55-99a7-4b94-8a69-4e32a6ad9c64.data","extendDataFileName":"4c539545-98d3-4dc1-a030-2d008ee4bb9f.exdata","id":"851c4c35-e331-4d19-9d5a-4b6fad5359cd","lethalPgmName":"ef10a08e-d0a1-451c-a072-1b4963f2739d.pgm","lethalPngName":"3e3eefcf-018d-4536-bd96-b72fae3aede0.png","mapInfo":{"gridHeight":224,"gridWidth":544,"originX":-12.8,"originY":-4.800000000000001,"resolution":0.05000000074505806},"name":"new2","obstacleFileName":"0d4279ac-88be-46d1-b887-e78668cbc21f.json","pgmFileName":"527d248e-609f-400f-9f2e-32b06b3312d6.pgm","pngFileName":"72280293-7759-45e5-9c4e-0bf24658e726.png","slopeFileName":"","yamlFileName":"8be23d8f-da6f-4fb3-9fea-d510a4ed97d3.yaml"}]
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
         * DIYPngName :
         * createdAt : 2019-01-31 09:51:40
         * dataFileName : 16abfb55-99a7-4b94-8a69-4e32a6ad9c64.data
         * extendDataFileName : 4c539545-98d3-4dc1-a030-2d008ee4bb9f.exdata
         * id : 851c4c35-e331-4d19-9d5a-4b6fad5359cd
         * lethalPgmName : ef10a08e-d0a1-451c-a072-1b4963f2739d.pgm
         * lethalPngName : 3e3eefcf-018d-4536-bd96-b72fae3aede0.png
         * mapInfo : {"gridHeight":224,"gridWidth":544,"originX":-12.8,"originY":-4.800000000000001,"resolution":0.05000000074505806}
         * name : new2
         * obstacleFileName : 0d4279ac-88be-46d1-b887-e78668cbc21f.json
         * pgmFileName : 527d248e-609f-400f-9f2e-32b06b3312d6.pgm
         * pngFileName : 72280293-7759-45e5-9c4e-0bf24658e726.png
         * slopeFileName :
         * yamlFileName : 8be23d8f-da6f-4fb3-9fea-d510a4ed97d3.yaml
         */

        private String DIYPngName;
        private String createdAt;
        private String dataFileName;
        private String extendDataFileName;
        private String id;
        private String lethalPgmName;
        private String lethalPngName;
        private MapInfoBean mapInfo;
        private String name;
        private String mapName;
        private String obstacleFileName;
        private String pgmFileName;
        private String pngFileName;
        private String slopeFileName;
        private String yamlFileName;
        private String map_link;
        private String map_md5;
        private String dump_link;
        private String dump_md5;

        public String getMap_link() {
            return map_link;
        }

        public void setMap_link(String map_link) {
            this.map_link = map_link;
        }

        public String getMap_md5() {
            return map_md5;
        }

        public void setMap_md5(String map_md5) {
            this.map_md5 = map_md5;
        }

        public String getDump_link() {
            return dump_link;
        }

        public void setDump_link(String dump_link) {
            this.dump_link = dump_link;
        }

        public String getDump_md5() {
            return dump_md5;
        }

        public void setDump_md5(String dump_md5) {
            this.dump_md5 = dump_md5;
        }

        public String getDIYPngName() {
            return DIYPngName;
        }

        public void setDIYPngName(String DIYPngName) {
            this.DIYPngName = DIYPngName;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDataFileName() {
            return dataFileName;
        }

        public void setDataFileName(String dataFileName) {
            this.dataFileName = dataFileName;
        }

        public String getExtendDataFileName() {
            return extendDataFileName;
        }

        public void setExtendDataFileName(String extendDataFileName) {
            this.extendDataFileName = extendDataFileName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLethalPgmName() {
            return lethalPgmName;
        }

        public void setLethalPgmName(String lethalPgmName) {
            this.lethalPgmName = lethalPgmName;
        }

        public String getLethalPngName() {
            return lethalPngName;
        }

        public void setLethalPngName(String lethalPngName) {
            this.lethalPngName = lethalPngName;
        }

        public MapInfoBean getMapInfo() {
            return mapInfo;
        }

        public void setMapInfo(MapInfoBean mapInfo) {
            this.mapInfo = mapInfo;
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

        public String getObstacleFileName() {
            return obstacleFileName;
        }

        public void setObstacleFileName(String obstacleFileName) {
            this.obstacleFileName = obstacleFileName;
        }

        public String getPgmFileName() {
            return pgmFileName;
        }

        public void setPgmFileName(String pgmFileName) {
            this.pgmFileName = pgmFileName;
        }

        public String getPngFileName() {
            return pngFileName;
        }

        public void setPngFileName(String pngFileName) {
            this.pngFileName = pngFileName;
        }

        public String getSlopeFileName() {
            return slopeFileName;
        }

        public void setSlopeFileName(String slopeFileName) {
            this.slopeFileName = slopeFileName;
        }

        public String getYamlFileName() {
            return yamlFileName;
        }

        public void setYamlFileName(String yamlFileName) {
            this.yamlFileName = yamlFileName;
        }

        public static class MapInfoBean {
            /**
             * gridHeight : 224
             * gridWidth : 544
             * originX : -12.8
             * originY : -4.800000000000001
             * resolution : 0.05000000074505806
             */

            private int gridHeight;
            private int gridWidth;
            private double originX;
            private double originY;
            private double resolution;

            public int getGridHeight() {
                return gridHeight;
            }

            public void setGridHeight(int gridHeight) {
                this.gridHeight = gridHeight;
            }

            public int getGridWidth() {
                return gridWidth;
            }

            public void setGridWidth(int gridWidth) {
                this.gridWidth = gridWidth;
            }

            public double getOriginX() {
                return originX;
            }

            public void setOriginX(double originX) {
                this.originX = originX;
            }

            public double getOriginY() {
                return originY;
            }

            public void setOriginY(double originY) {
                this.originY = originY;
            }

            public double getResolution() {
                return resolution;
            }

            public void setResolution(double resolution) {
                this.resolution = resolution;
            }
        }
    }
}
