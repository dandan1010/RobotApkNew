package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotPathData {

    /**
     * data : {"data":[{"gridPosition":[{"x":923,"y":1414},{"x":923,"y":1414}],"length":20.90999999999986,"name":"__DEFAULT","namedPoints":[{"actions":[{"fields":[{"fields":[],"name":"spray_motor","type":"string","value":"false"}],"name":"OperateDevice"},{"fields":[{"fields":[],"name":"water_level","type":"string","value":"1"}],"name":"OperateDevice"}],"angle":9.404949325192778,"gridPosition":{"x":803,"y":1350},"index":0,"name":"__DEFAULT_0"},{"actions":[{"fields":[{"fields":[],"name":"squeegee_motor","type":"string","value":"false"}],"name":"OperateDevice"}],"angle":-173.43650045468152,"gridPosition":{"x":931,"y":1414},"index":2052,"name":"__DEFAULT_1"},{"actions":[],"angle":177.3961748890896,"gridPosition":{"x":923,"y":1414},"index":2093,"name":"__DEFAULT_2"}],"pointCount":2093}],"mapInfo":{"gridHeight":3040,"gridWidth":1632},"map_name":"B1-FULL","path_name":"zyx2","type":0}
     * errorCode :
     * msg : successed
     * successed : true
     */

    private DataBeanX data;
    private String errorCode;
    private String msg;
    private boolean successed;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

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

    public static class DataBeanX {
        /**
         * data : [{"gridPosition":[{"x":923,"y":1414},{"x":923,"y":1414}],"length":20.90999999999986,"name":"__DEFAULT","namedPoints":[{"actions":[{"fields":[{"fields":[],"name":"spray_motor","type":"string","value":"false"}],"name":"OperateDevice"},{"fields":[{"fields":[],"name":"water_level","type":"string","value":"1"}],"name":"OperateDevice"}],"angle":9.404949325192778,"gridPosition":{"x":803,"y":1350},"index":0,"name":"__DEFAULT_0"},{"actions":[{"fields":[{"fields":[],"name":"squeegee_motor","type":"string","value":"false"}],"name":"OperateDevice"}],"angle":-173.43650045468152,"gridPosition":{"x":931,"y":1414},"index":2052,"name":"__DEFAULT_1"},{"actions":[],"angle":177.3961748890896,"gridPosition":{"x":923,"y":1414},"index":2093,"name":"__DEFAULT_2"}],"pointCount":2093}]
         * mapInfo : {"gridHeight":3040,"gridWidth":1632}
         * map_name : B1-FULL
         * path_name : zyx2
         * type : 0
         */

        private MapInfoBean mapInfo;
        private String map_name;
        private String path_name;
        private int type;
        private List<DataBean> data;

        public MapInfoBean getMapInfo() {
            return mapInfo;
        }

        public void setMapInfo(MapInfoBean mapInfo) {
            this.mapInfo = mapInfo;
        }

        public String getMap_name() {
            return map_name;
        }

        public void setMap_name(String map_name) {
            this.map_name = map_name;
        }

        public String getPath_name() {
            return path_name;
        }

        public void setPath_name(String path_name) {
            this.path_name = path_name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class MapInfoBean {
            /**
             * gridHeight : 3040
             * gridWidth : 1632
             */

            private int gridHeight;
            private int gridWidth;

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
        }

        public static class DataBean {
            /**
             * gridPosition : [{"x":923,"y":1414},{"x":923,"y":1414}]
             * length : 20.90999999999986
             * name : __DEFAULT
             * namedPoints : [{"actions":[{"fields":[{"fields":[],"name":"spray_motor","type":"string","value":"false"}],"name":"OperateDevice"},{"fields":[{"fields":[],"name":"water_level","type":"string","value":"1"}],"name":"OperateDevice"}],"angle":9.404949325192778,"gridPosition":{"x":803,"y":1350},"index":0,"name":"__DEFAULT_0"},{"actions":[{"fields":[{"fields":[],"name":"squeegee_motor","type":"string","value":"false"}],"name":"OperateDevice"}],"angle":-173.43650045468152,"gridPosition":{"x":931,"y":1414},"index":2052,"name":"__DEFAULT_1"},{"actions":[],"angle":177.3961748890896,"gridPosition":{"x":923,"y":1414},"index":2093,"name":"__DEFAULT_2"}]
             * pointCount : 2093
             */

            private double length;
            private String name;
            private int pointCount;
            private List<GridPositionBean> gridPosition;
            private List<NamedPointsBean> namedPoints;

            public double getLength() {
                return length;
            }

            public void setLength(double length) {
                this.length = length;
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

            public List<GridPositionBean> getGridPosition() {
                return gridPosition;
            }

            public void setGridPosition(List<GridPositionBean> gridPosition) {
                this.gridPosition = gridPosition;
            }

            public List<NamedPointsBean> getNamedPoints() {
                return namedPoints;
            }

            public void setNamedPoints(List<NamedPointsBean> namedPoints) {
                this.namedPoints = namedPoints;
            }

            public static class GridPositionBean {
                /**
                 * x : 923
                 * y : 1414
                 */

                private int x;
                private int y;

                public int getX() {
                    return x;
                }

                public void setX(int x) {
                    this.x = x;
                }

                public int getY() {
                    return y;
                }

                public void setY(int y) {
                    this.y = y;
                }
            }

            public static class NamedPointsBean {
                /**
                 * actions : [{"fields":[{"fields":[],"name":"spray_motor","type":"string","value":"false"}],"name":"OperateDevice"},{"fields":[{"fields":[],"name":"water_level","type":"string","value":"1"}],"name":"OperateDevice"}]
                 * angle : 9.404949325192778
                 * gridPosition : {"x":803,"y":1350}
                 * index : 0
                 * name : __DEFAULT_0
                 */

                private double angle;
                private GridPositionBeanX gridPosition;
                private int index;
                private String name;
                private List<ActionsBean> actions;

                public double getAngle() {
                    return angle;
                }

                public void setAngle(double angle) {
                    this.angle = angle;
                }

                public GridPositionBeanX getGridPosition() {
                    return gridPosition;
                }

                public void setGridPosition(GridPositionBeanX gridPosition) {
                    this.gridPosition = gridPosition;
                }

                public int getIndex() {
                    return index;
                }

                public void setIndex(int index) {
                    this.index = index;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<ActionsBean> getActions() {
                    return actions;
                }

                public void setActions(List<ActionsBean> actions) {
                    this.actions = actions;
                }

                public static class GridPositionBeanX {
                    /**
                     * x : 803
                     * y : 1350
                     */

                    private int x;
                    private int y;

                    public int getX() {
                        return x;
                    }

                    public void setX(int x) {
                        this.x = x;
                    }

                    public int getY() {
                        return y;
                    }

                    public void setY(int y) {
                        this.y = y;
                    }
                }

                public static class ActionsBean {
                    /**
                     * fields : [{"fields":[],"name":"spray_motor","type":"string","value":"false"}]
                     * name : OperateDevice
                     */

                    private String name;
                    private List<FieldsBean> fields;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public List<FieldsBean> getFields() {
                        return fields;
                    }

                    public void setFields(List<FieldsBean> fields) {
                        this.fields = fields;
                    }

                    public static class FieldsBean {
                        /**
                         * fields : []
                         * name : spray_motor
                         * type : string
                         * value : false
                         */

                        private String name;
                        private String type;
                        private String value;
                        private List<?> fields;

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public String getType() {
                            return type;
                        }

                        public void setType(String type) {
                            this.type = type;
                        }

                        public String getValue() {
                            return value;
                        }

                        public void setValue(String value) {
                            this.value = value;
                        }

                        public List<?> getFields() {
                            return fields;
                        }

                        public void setFields(List<?> fields) {
                            this.fields = fields;
                        }
                    }
                }
            }
        }
    }
}
