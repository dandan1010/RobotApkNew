package com.dcm360.controller.gs.controller.bean.map_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotEditMap {

    /**
     * obstacles : {"polygons":[[{"x":2,"y":3}]]}
     */

    private ObstaclesBean obstacles;

    public ObstaclesBean getObstacles() {
        return obstacles;
    }

    public void setObstacles(ObstaclesBean obstacles) {
        this.obstacles = obstacles;
    }

    public static class ObstaclesBean {
        private List<List<PolygonsBean>> polygons;

        public List<List<PolygonsBean>> getPolygons() {
            return polygons;
        }

        public void setPolygons(List<List<PolygonsBean>> polygons) {
            this.polygons = polygons;
        }

        public static class PolygonsBean {
            /**
             * x : 2
             * y : 3
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
    }
}
