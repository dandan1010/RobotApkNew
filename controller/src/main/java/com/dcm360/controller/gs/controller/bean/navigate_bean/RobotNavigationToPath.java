package com.dcm360.controller.gs.controller.bean.navigate_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotNavigationToPath {

    /**
     * start : {"angle":0.5,"gridPosition":{"x":0,"y":0}}
     * end : {"angle":0.5,"gridPosition":{"x":0,"y":0}}
     * mapName : l8
     */

    private StartBean start;
    private EndBean end;
    private String mapName;

    public StartBean getStart() {
        return start;
    }

    public void setStart(StartBean start) {
        this.start = start;
    }

    public EndBean getEnd() {
        return end;
    }

    public void setEnd(EndBean end) {
        this.end = end;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public static class StartBean {
        /**
         * angle : 0.5
         * gridPosition : {"x":0,"y":0}
         */

        private double angle;
        private GridPositionBean gridPosition;

        public double getAngle() {
            return angle;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public GridPositionBean getGridPosition() {
            return gridPosition;
        }

        public void setGridPosition(GridPositionBean gridPosition) {
            this.gridPosition = gridPosition;
        }

        public static class GridPositionBean {
            /**
             * x : 0
             * y : 0
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

    public static class EndBean {
        /**
         * angle : 0.5
         * gridPosition : {"x":0,"y":0}
         */

        private double angle;
        private GridPositionBeanX gridPosition;

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

        public static class GridPositionBeanX {
            /**
             * x : 0
             * y : 0
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
