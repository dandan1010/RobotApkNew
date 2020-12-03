package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

public class VirtualObstacleBean implements Cloneable {

    /**
     * msg : successed
     * data : {"carpets":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"decelerations":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"highlight":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"slopesWorld":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"obstaclesWorld":{"polylines":[[{"x":1.1250000882893794,"y":0.7250000823289149},{"x":0.4250000778585665,"y":-0.47499993555247855},{"x":0.4250000778585665,"y":-0.47499993555247855}]],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"displaysWorld":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"highlightWorld":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"obstacles":{"polylines":[[{"x":118,"y":110},{"x":104,"y":86},{"x":104,"y":86}]],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"decelerationsWorld":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"slopes":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"displays":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]},"carpetsWorld":{"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}}
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

    public static class DataEntity {
        /**
         * carpets : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * decelerations : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * highlight : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * slopesWorld : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * obstaclesWorld : {"polylines":[[{"x":1.1250000882893794,"y":0.7250000823289149},{"x":0.4250000778585665,"y":-0.47499993555247855},{"x":0.4250000778585665,"y":-0.47499993555247855}]],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * displaysWorld : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * highlightWorld : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * obstacles : {"polylines":[[{"x":118,"y":110},{"x":104,"y":86},{"x":104,"y":86}]],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * decelerationsWorld : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * slopes : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * displays : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         * carpetsWorld : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
         */
        private CarpetsEntity carpets;
        private DecelerationsEntity decelerations;
        private HighlightEntity highlight;
        private SlopesWorldEntity slopesWorld;
        private ObstaclesWorldEntity obstaclesWorld;
        private DisplaysWorldEntity displaysWorld;
        private HighlightWorldEntity highlightWorld;
        private ObstaclesEntity obstacles;
        private DecelerationsWorldEntity decelerationsWorld;
        private SlopesEntity slopes;
        private DisplaysEntity displays;
        private CarpetsWorldEntity carpetsWorld;

        public void setCarpets(CarpetsEntity carpets) {
            this.carpets = carpets;
        }

        public void setDecelerations(DecelerationsEntity decelerations) {
            this.decelerations = decelerations;
        }

        public void setHighlight(HighlightEntity highlight) {
            this.highlight = highlight;
        }

        public void setSlopesWorld(SlopesWorldEntity slopesWorld) {
            this.slopesWorld = slopesWorld;
        }

        public void setObstaclesWorld(ObstaclesWorldEntity obstaclesWorld) {
            this.obstaclesWorld = obstaclesWorld;
        }

        public void setDisplaysWorld(DisplaysWorldEntity displaysWorld) {
            this.displaysWorld = displaysWorld;
        }

        public void setHighlightWorld(HighlightWorldEntity highlightWorld) {
            this.highlightWorld = highlightWorld;
        }

        public void setObstacles(ObstaclesEntity obstacles) {
            this.obstacles = obstacles;
        }

        public void setDecelerationsWorld(DecelerationsWorldEntity decelerationsWorld) {
            this.decelerationsWorld = decelerationsWorld;
        }

        public void setSlopes(SlopesEntity slopes) {
            this.slopes = slopes;
        }

        public void setDisplays(DisplaysEntity displays) {
            this.displays = displays;
        }

        public void setCarpetsWorld(CarpetsWorldEntity carpetsWorld) {
            this.carpetsWorld = carpetsWorld;
        }

        public CarpetsEntity getCarpets() {
            return carpets;
        }

        public DecelerationsEntity getDecelerations() {
            return decelerations;
        }

        public HighlightEntity getHighlight() {
            return highlight;
        }

        public SlopesWorldEntity getSlopesWorld() {
            return slopesWorld;
        }

        public ObstaclesWorldEntity getObstaclesWorld() {
            return obstaclesWorld;
        }

        public DisplaysWorldEntity getDisplaysWorld() {
            return displaysWorld;
        }

        public HighlightWorldEntity getHighlightWorld() {
            return highlightWorld;
        }

        public ObstaclesEntity getObstacles() {
            return obstacles;
        }

        public DecelerationsWorldEntity getDecelerationsWorld() {
            return decelerationsWorld;
        }

        public SlopesEntity getSlopes() {
            return slopes;
        }

        public DisplaysEntity getDisplays() {
            return displays;
        }

        public CarpetsWorldEntity getCarpetsWorld() {
            return carpetsWorld;
        }

        public class CarpetsEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class DecelerationsEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class HighlightEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class SlopesWorldEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class ObstaclesWorldEntity {
            /**
             * polylines : [[{"x":1.1250000882893794,"y":0.7250000823289149},{"x":0.4250000778585665,"y":-0.47499993555247855},{"x":0.4250000778585665,"y":-0.47499993555247855}]]
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<List<PolylinesEntity>> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<List<PolylinesEntity>> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<List<PolylinesEntity>> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }

            public class PolylinesEntity {
                /**
                 * x : 1.1250000882893794
                 * y : 0.7250000823289149
                 */
                private double x;
                private double y;

                public void setX(double x) {
                    this.x = x;
                }

                public void setY(double y) {
                    this.y = y;
                }

                public double getX() {
                    return x;
                }

                public double getY() {
                    return y;
                }
            }
        }

        public class DisplaysWorldEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class HighlightWorldEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class ObstaclesEntity {
            /**
             * polylines : [[{"x":118,"y":110},{"x":104,"y":86},{"x":104,"y":86}]]
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<List<PolylinesEntity>> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<List<PolylinesEntity>> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<List<PolylinesEntity>> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }

            public class PolylinesEntity {
                /**
                 * x : 118
                 * y : 110
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

        public class DecelerationsWorldEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class SlopesEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class DisplaysEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }

        public class CarpetsWorldEntity {
            /**
             * polylines : []
             * rectangles : []
             * polygons : []
             * circles : []
             * lines : []
             */
            private List<?> polylines;
            private List<?> rectangles;
            private List<?> polygons;
            private List<?> circles;
            private List<?> lines;

            public void setPolylines(List<?> polylines) {
                this.polylines = polylines;
            }

            public void setRectangles(List<?> rectangles) {
                this.rectangles = rectangles;
            }

            public void setPolygons(List<?> polygons) {
                this.polygons = polygons;
            }

            public void setCircles(List<?> circles) {
                this.circles = circles;
            }

            public void setLines(List<?> lines) {
                this.lines = lines;
            }

            public List<?> getPolylines() {
                return polylines;
            }

            public List<?> getRectangles() {
                return rectangles;
            }

            public List<?> getPolygons() {
                return polygons;
            }

            public List<?> getCircles() {
                return circles;
            }

            public List<?> getLines() {
                return lines;
            }
        }
    }

}