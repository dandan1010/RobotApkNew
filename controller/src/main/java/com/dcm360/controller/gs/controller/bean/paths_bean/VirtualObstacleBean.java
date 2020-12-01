package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

public class VirtualObstacleBean implements Cloneable {

    /**
     * obstacles : {"polylines":[[{"x":476,"y":672},{"x":489,"y":748},{"x":522,"y":719},{"x":554,"y":765},{"x":569,"y":676}]],"rectangles":[{"start":{"x":529,"y":281},"end":{"x":613,"y":362}}],"polygons":[[{"x":476,"y":672},{"x":489,"y":748},{"x":522,"y":719},{"x":554,"y":765},{"x":569,"y":676}]],"circles":[{"center":{"x":109,"y":76},"radius":60.108235708594876}],"lines":[{"start":{"x":449,"y":806},"end":{"x":560,"y":802}}]}
     * description : for test
     * ID :
     * mapName : test
     * createDate :
     */
    private ObstaclesEntity obstacles;
    private String description;
    private String ID;
    private String mapName;
    private String createDate;

    public void setObstacles(ObstaclesEntity obstacles) {
        this.obstacles = obstacles;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public ObstaclesEntity getObstacles() {
        return obstacles;
    }

    public String getDescription() {
        return description;
    }

    public String getID() {
        return ID;
    }

    public String getMapName() {
        return mapName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public class ObstaclesEntity {
        /**
         * polylines : [[{"x":476,"y":672},{"x":489,"y":748},{"x":522,"y":719},{"x":554,"y":765},{"x":569,"y":676}]]
         * rectangles : [{"start":{"x":529,"y":281},"end":{"x":613,"y":362}}]
         * polygons : [[{"x":476,"y":672},{"x":489,"y":748},{"x":522,"y":719},{"x":554,"y":765},{"x":569,"y":676}]]
         * circles : [{"center":{"x":109,"y":76},"radius":60.108235708594876}]
         * lines : [{"start":{"x":449,"y":806},"end":{"x":560,"y":802}}]
         */
        private List<List<PolylinesEntity>> polylines;
        private List<RectanglesEntity> rectangles;
        private List<List<PolygonsEntity>> polygons;
        private List<CirclesEntity> circles;
        private List<LinesEntity> lines;

        public void setPolylines(List<List<PolylinesEntity>> polylines) {
            this.polylines = polylines;
        }

        public void setRectangles(List<RectanglesEntity> rectangles) {
            this.rectangles = rectangles;
        }

        public void setPolygons(List<List<PolygonsEntity>> polygons) {
            this.polygons = polygons;
        }

        public void setCircles(List<CirclesEntity> circles) {
            this.circles = circles;
        }

        public void setLines(List<LinesEntity> lines) {
            this.lines = lines;
        }

        public List<List<PolylinesEntity>> getPolylines() {
            return polylines;
        }

        public List<RectanglesEntity> getRectangles() {
            return rectangles;
        }

        public List<List<PolygonsEntity>> getPolygons() {
            return polygons;
        }

        public List<CirclesEntity> getCircles() {
            return circles;
        }

        public List<LinesEntity> getLines() {
            return lines;
        }

        public class PolylinesEntity {
            /**
             * x : 476
             * y : 672
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

        public class RectanglesEntity {
            /**
             * start : {"x":529,"y":281}
             * end : {"x":613,"y":362}
             */
            private StartEntity start;
            private EndEntity end;

            public void setStart(StartEntity start) {
                this.start = start;
            }

            public void setEnd(EndEntity end) {
                this.end = end;
            }

            public StartEntity getStart() {
                return start;
            }

            public EndEntity getEnd() {
                return end;
            }

            public class StartEntity {
                /**
                 * x : 529
                 * y : 281
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

            public class EndEntity {
                /**
                 * x : 613
                 * y : 362
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

        public class PolygonsEntity {
            /**
             * x : 476
             * y : 672
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

        public class CirclesEntity {
            /**
             * center : {"x":109,"y":76}
             * radius : 60.108235708594876
             */
            private CenterEntity center;
            private double radius;

            public void setCenter(CenterEntity center) {
                this.center = center;
            }

            public void setRadius(double radius) {
                this.radius = radius;
            }

            public CenterEntity getCenter() {
                return center;
            }

            public double getRadius() {
                return radius;
            }

            public class CenterEntity {
                /**
                 * x : 109
                 * y : 76
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

        public class LinesEntity {
            /**
             * start : {"x":449,"y":806}
             * end : {"x":560,"y":802}
             */
            private StartEntity start;
            private EndEntity end;

            public void setStart(StartEntity start) {
                this.start = start;
            }

            public void setEnd(EndEntity end) {
                this.end = end;
            }

            public StartEntity getStart() {
                return start;
            }

            public EndEntity getEnd() {
                return end;
            }

            public class StartEntity {
                /**
                 * x : 449
                 * y : 806
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

            public class EndEntity {
                /**
                 * x : 560
                 * y : 802
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

    @Override
    public String toString() {
        return "VirtualObstacleBean{" +
                "obstacles=" + obstacles +
                ", description='" + description + '\'' +
                ", ID='" + ID + '\'' +
                ", mapName='" + mapName + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}