package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

public class UpdataVirtualObstacleBean {

    /**
     * carpets : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
     * decelerations : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
     * obstacles : {"polylines":[[{"x":130,"y":109},{"x":99,"y":126},{"x":99,"y":126},{"x":99,"y":126}],[{"x":96,"y":96},{"x":51,"y":119}],[{"x":104,"y":98},{"x":83,"y":64}]],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
     * mapName : test00
     * slopes : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
     * displays : {"polylines":[],"rectangles":[],"polygons":[],"circles":[],"lines":[]}
     * type : 0
     */
    private CarpetsEntity carpets;
    private DecelerationsEntity decelerations;
    private ObstaclesEntity obstacles;
    private String mapName;
    private SlopesEntity slopes;
    private DisplaysEntity displays;
    private int type;

    public void setCarpets(CarpetsEntity carpets) {
        this.carpets = carpets;
    }

    public void setDecelerations(DecelerationsEntity decelerations) {
        this.decelerations = decelerations;
    }

    public void setObstacles(ObstaclesEntity obstacles) {
        this.obstacles = obstacles;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setSlopes(SlopesEntity slopes) {
        this.slopes = slopes;
    }

    public void setDisplays(DisplaysEntity displays) {
        this.displays = displays;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CarpetsEntity getCarpets() {
        return carpets;
    }

    public DecelerationsEntity getDecelerations() {
        return decelerations;
    }

    public ObstaclesEntity getObstacles() {
        return obstacles;
    }

    public String getMapName() {
        return mapName;
    }

    public SlopesEntity getSlopes() {
        return slopes;
    }

    public DisplaysEntity getDisplays() {
        return displays;
    }

    public int getType() {
        return type;
    }

    public static class CarpetsEntity {
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

    public static class DecelerationsEntity {
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

    public static class ObstaclesEntity {
        /**
         * polylines : [[{"x":130,"y":109},{"x":99,"y":126},{"x":99,"y":126},{"x":99,"y":126}],[{"x":96,"y":96},{"x":51,"y":119}],[{"x":104,"y":98},{"x":83,"y":64}]]
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

        public static class PolylinesEntity {
            /**
             * x : 130.0
             * y : 109.0
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

    public static class SlopesEntity {
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

    public static class DisplaysEntity {
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

    @Override
    public String toString() {
        return "UpdataVirtualObstacleBean{" +
                "carpets=" + carpets +
                ", decelerations=" + decelerations +
                ", obstacles=" + obstacles +
                ", mapName='" + mapName + '\'' +
                ", slopes=" + slopes +
                ", displays=" + displays +
                ", type=" + type +
                '}';
    }
}
