package com.dcm360.controller.gs.controller.bean.data_bean;

import java.util.List;

public class RobotWorkStatus {
    /**
     * msg : successed
     * data : {"task_status":{"task":null,"task_queue":null,"map":{}},"data_status":{"unfilled_paths":[]},"work_status":{"work_type_id":1,"current_path_name":"","work_type":"SCANNING_MAP","current_map_name":"1625126830040","map":{"pgmFileName":"map.pgm","lethalPngName":"lethal.png","DIYPngName":"","extendDataFileName":"map.exdata","obstacleFileName":"","yamlFileName":"map.yaml","dataFileName":"map.data","mapInfo":{"gridHeight":0,"originX":0,"originY":0,"gridWidth":0,"resolution":0},"createdAt":"2021-07-01 16:05:48","pngFileName":"map.png","name":"1625126830040","id":"8a7a00c6-0ce5-46a6-9f64-873b315cea26","md5":"58a83481d35c05816ff5a03729b420e5","updatedAt":1625126748}}}
     * successed : true
     * errorCode :
     */
    private String msg;
    private DataEntity data;
    private boolean successed;
    private String errorCode;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public DataEntity getData() {
        return data;
    }

    public boolean isSuccessed() {
        return successed;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public class DataEntity {
        /**
         * task_status : {"task":null,"task_queue":null,"map":{}}
         * data_status : {"unfilled_paths":[]}
         * work_status : {"work_type_id":1,"current_path_name":"","work_type":"SCANNING_MAP","current_map_name":"1625126830040","map":{"pgmFileName":"map.pgm","lethalPngName":"lethal.png","DIYPngName":"","extendDataFileName":"map.exdata","obstacleFileName":"","yamlFileName":"map.yaml","dataFileName":"map.data","mapInfo":{"gridHeight":0,"originX":0,"originY":0,"gridWidth":0,"resolution":0},"createdAt":"2021-07-01 16:05:48","pngFileName":"map.png","name":"1625126830040","id":"8a7a00c6-0ce5-46a6-9f64-873b315cea26","md5":"58a83481d35c05816ff5a03729b420e5","updatedAt":1625126748}}
         */
        private Task_statusEntity task_status;
        private Data_statusEntity data_status;
        private Work_statusEntity work_status;

        public void setTask_status(Task_statusEntity task_status) {
            this.task_status = task_status;
        }

        public void setData_status(Data_statusEntity data_status) {
            this.data_status = data_status;
        }

        public void setWork_status(Work_statusEntity work_status) {
            this.work_status = work_status;
        }

        public Task_statusEntity getTask_status() {
            return task_status;
        }

        public Data_statusEntity getData_status() {
            return data_status;
        }

        public Work_statusEntity getWork_status() {
            return work_status;
        }

        public class Task_statusEntity {
            /**
             * task : null
             * task_queue : null
             * map : {}
             */
            private String task;
            private String task_queue;
            private MapEntity map;

            public void setTask(String task) {
                this.task = task;
            }

            public void setTask_queue(String task_queue) {
                this.task_queue = task_queue;
            }

            public void setMap(MapEntity map) {
                this.map = map;
            }

            public String getTask() {
                return task;
            }

            public String getTask_queue() {
                return task_queue;
            }

            public MapEntity getMap() {
                return map;
            }

            public class MapEntity {
            }
        }

        public class Data_statusEntity {
            /**
             * unfilled_paths : []
             */
            private List<?> unfilled_paths;

            public void setUnfilled_paths(List<?> unfilled_paths) {
                this.unfilled_paths = unfilled_paths;
            }

            public List<?> getUnfilled_paths() {
                return unfilled_paths;
            }
        }

        public class Work_statusEntity {
            /**
             * work_type_id : 1
             * current_path_name :
             * work_type : SCANNING_MAP
             * current_map_name : 1625126830040
             * map : {"pgmFileName":"map.pgm","lethalPngName":"lethal.png","DIYPngName":"","extendDataFileName":"map.exdata","obstacleFileName":"","yamlFileName":"map.yaml","dataFileName":"map.data","mapInfo":{"gridHeight":0,"originX":0,"originY":0,"gridWidth":0,"resolution":0},"createdAt":"2021-07-01 16:05:48","pngFileName":"map.png","name":"1625126830040","id":"8a7a00c6-0ce5-46a6-9f64-873b315cea26","md5":"58a83481d35c05816ff5a03729b420e5","updatedAt":1625126748}
             */
            private int work_type_id;
            private String current_path_name;
            private String work_type;
            private String current_map_name;
            private MapEntity map;

            public void setWork_type_id(int work_type_id) {
                this.work_type_id = work_type_id;
            }

            public void setCurrent_path_name(String current_path_name) {
                this.current_path_name = current_path_name;
            }

            public void setWork_type(String work_type) {
                this.work_type = work_type;
            }

            public void setCurrent_map_name(String current_map_name) {
                this.current_map_name = current_map_name;
            }

            public void setMap(MapEntity map) {
                this.map = map;
            }

            public int getWork_type_id() {
                return work_type_id;
            }

            public String getCurrent_path_name() {
                return current_path_name;
            }

            public String getWork_type() {
                return work_type;
            }

            public String getCurrent_map_name() {
                return current_map_name;
            }

            public MapEntity getMap() {
                return map;
            }

            public class MapEntity {
                /**
                 * pgmFileName : map.pgm
                 * lethalPngName : lethal.png
                 * DIYPngName :
                 * extendDataFileName : map.exdata
                 * obstacleFileName :
                 * yamlFileName : map.yaml
                 * dataFileName : map.data
                 * mapInfo : {"gridHeight":0,"originX":0,"originY":0,"gridWidth":0,"resolution":0}
                 * createdAt : 2021-07-01 16:05:48
                 * pngFileName : map.png
                 * name : 1625126830040
                 * id : 8a7a00c6-0ce5-46a6-9f64-873b315cea26
                 * md5 : 58a83481d35c05816ff5a03729b420e5
                 * updatedAt : 1625126748
                 */
                private String pgmFileName;
                private String lethalPngName;
                private String DIYPngName;
                private String extendDataFileName;
                private String obstacleFileName;
                private String yamlFileName;
                private String dataFileName;
                private MapInfoEntity mapInfo;
                private String createdAt;
                private String pngFileName;
                private String name;
                private String id;
                private String md5;
                private int updatedAt;

                public void setPgmFileName(String pgmFileName) {
                    this.pgmFileName = pgmFileName;
                }

                public void setLethalPngName(String lethalPngName) {
                    this.lethalPngName = lethalPngName;
                }

                public void setDIYPngName(String DIYPngName) {
                    this.DIYPngName = DIYPngName;
                }

                public void setExtendDataFileName(String extendDataFileName) {
                    this.extendDataFileName = extendDataFileName;
                }

                public void setObstacleFileName(String obstacleFileName) {
                    this.obstacleFileName = obstacleFileName;
                }

                public void setYamlFileName(String yamlFileName) {
                    this.yamlFileName = yamlFileName;
                }

                public void setDataFileName(String dataFileName) {
                    this.dataFileName = dataFileName;
                }

                public void setMapInfo(MapInfoEntity mapInfo) {
                    this.mapInfo = mapInfo;
                }

                public void setCreatedAt(String createdAt) {
                    this.createdAt = createdAt;
                }

                public void setPngFileName(String pngFileName) {
                    this.pngFileName = pngFileName;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public void setMd5(String md5) {
                    this.md5 = md5;
                }

                public void setUpdatedAt(int updatedAt) {
                    this.updatedAt = updatedAt;
                }

                public String getPgmFileName() {
                    return pgmFileName;
                }

                public String getLethalPngName() {
                    return lethalPngName;
                }

                public String getDIYPngName() {
                    return DIYPngName;
                }

                public String getExtendDataFileName() {
                    return extendDataFileName;
                }

                public String getObstacleFileName() {
                    return obstacleFileName;
                }

                public String getYamlFileName() {
                    return yamlFileName;
                }

                public String getDataFileName() {
                    return dataFileName;
                }

                public MapInfoEntity getMapInfo() {
                    return mapInfo;
                }

                public String getCreatedAt() {
                    return createdAt;
                }

                public String getPngFileName() {
                    return pngFileName;
                }

                public String getName() {
                    return name;
                }

                public String getId() {
                    return id;
                }

                public String getMd5() {
                    return md5;
                }

                public int getUpdatedAt() {
                    return updatedAt;
                }

                public class MapInfoEntity {
                    /**
                     * gridHeight : 0
                     * originX : 0
                     * originY : 0
                     * gridWidth : 0
                     * resolution : 0
                     */
                    private int gridHeight;
                    private int originX;
                    private int originY;
                    private int gridWidth;
                    private int resolution;

                    public void setGridHeight(int gridHeight) {
                        this.gridHeight = gridHeight;
                    }

                    public void setOriginX(int originX) {
                        this.originX = originX;
                    }

                    public void setOriginY(int originY) {
                        this.originY = originY;
                    }

                    public void setGridWidth(int gridWidth) {
                        this.gridWidth = gridWidth;
                    }

                    public void setResolution(int resolution) {
                        this.resolution = resolution;
                    }

                    public int getGridHeight() {
                        return gridHeight;
                    }

                    public int getOriginX() {
                        return originX;
                    }

                    public int getOriginY() {
                        return originY;
                    }

                    public int getGridWidth() {
                        return gridWidth;
                    }

                    public int getResolution() {
                        return resolution;
                    }
                }
            }
        }
    }
}
