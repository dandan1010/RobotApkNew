package com.retron.robotAgent.bean;

public class NavigationStatesBean {


    /**
     * noticeDataFields : expectedDestination
     * data : {"expectedDestination":{"worldPosition":{"orientation":{"w":1,"x":0,"y":0,"z":0},"position":{"x":-2.8249996844679117,"y":0.37500007711350847,"z":0}},"gridPosition":{"x":423,"y":103},"name":"office","angle":0}}
     * noticeType : HEADING
     * noticeTypeLevel : INFO
     */
    private String noticeDataFields;
    private DataEntity data;
    private String noticeType;
    private String noticeTypeLevel;

    public void setNoticeDataFields(String noticeDataFields) {
        this.noticeDataFields = noticeDataFields;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public void setNoticeTypeLevel(String noticeTypeLevel) {
        this.noticeTypeLevel = noticeTypeLevel;
    }

    public String getNoticeDataFields() {
        return noticeDataFields;
    }

    public DataEntity getData() {
        return data;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public String getNoticeTypeLevel() {
        return noticeTypeLevel;
    }

    public class DataEntity {
        /**
         * expectedDestination : {"worldPosition":{"orientation":{"w":1,"x":0,"y":0,"z":0},"position":{"x":-2.8249996844679117,"y":0.37500007711350847,"z":0}},"gridPosition":{"x":423,"y":103},"name":"office","angle":0}
         */
        private ExpectedDestinationEntity expectedDestination;

        public void setExpectedDestination(ExpectedDestinationEntity expectedDestination) {
            this.expectedDestination = expectedDestination;
        }

        public ExpectedDestinationEntity getExpectedDestination() {
            return expectedDestination;
        }

        public class ExpectedDestinationEntity {
            /**
             * worldPosition : {"orientation":{"w":1,"x":0,"y":0,"z":0},"position":{"x":-2.8249996844679117,"y":0.37500007711350847,"z":0}}
             * gridPosition : {"x":423,"y":103}
             * name : office
             * angle : 0
             */
            private WorldPositionEntity worldPosition;
            private GridPositionEntity gridPosition;
            private String name;
            private int angle;

            public void setWorldPosition(WorldPositionEntity worldPosition) {
                this.worldPosition = worldPosition;
            }

            public void setGridPosition(GridPositionEntity gridPosition) {
                this.gridPosition = gridPosition;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setAngle(int angle) {
                this.angle = angle;
            }

            public WorldPositionEntity getWorldPosition() {
                return worldPosition;
            }

            public GridPositionEntity getGridPosition() {
                return gridPosition;
            }

            public String getName() {
                return name;
            }

            public int getAngle() {
                return angle;
            }

            public class WorldPositionEntity {
                /**
                 * orientation : {"w":1,"x":0,"y":0,"z":0}
                 * position : {"x":-2.8249996844679117,"y":0.37500007711350847,"z":0}
                 */
                private OrientationEntity orientation;
                private PositionEntity position;

                public void setOrientation(OrientationEntity orientation) {
                    this.orientation = orientation;
                }

                public void setPosition(PositionEntity position) {
                    this.position = position;
                }

                public OrientationEntity getOrientation() {
                    return orientation;
                }

                public PositionEntity getPosition() {
                    return position;
                }

                public class OrientationEntity {
                    /**
                     * w : 1
                     * x : 0
                     * y : 0
                     * z : 0
                     */
                    private int w;
                    private int x;
                    private int y;
                    private int z;

                    public void setW(int w) {
                        this.w = w;
                    }

                    public void setX(int x) {
                        this.x = x;
                    }

                    public void setY(int y) {
                        this.y = y;
                    }

                    public void setZ(int z) {
                        this.z = z;
                    }

                    public int getW() {
                        return w;
                    }

                    public int getX() {
                        return x;
                    }

                    public int getY() {
                        return y;
                    }

                    public int getZ() {
                        return z;
                    }
                }

                public class PositionEntity {
                    /**
                     * x : -2.8249996844679117
                     * y : 0.37500007711350847
                     * z : 0
                     */
                    private double x;
                    private double y;
                    private int z;

                    public void setX(double x) {
                        this.x = x;
                    }

                    public void setY(double y) {
                        this.y = y;
                    }

                    public void setZ(int z) {
                        this.z = z;
                    }

                    public double getX() {
                        return x;
                    }

                    public double getY() {
                        return y;
                    }

                    public int getZ() {
                        return z;
                    }
                }
            }

            public class GridPositionEntity {
                /**
                 * x : 423
                 * y : 103
                 */
                private int x;
                private int y;

                public void setX(int x) {
                    this.x = x;
                }

                public void setY(int y) {
                    this.y = y;
                }

                public int getX() {
                    return x;
                }

                public int getY() {
                    return y;
                }
            }
        }
    }
}
