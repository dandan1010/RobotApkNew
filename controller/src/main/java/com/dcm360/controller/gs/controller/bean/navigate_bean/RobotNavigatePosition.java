package com.dcm360.controller.gs.controller.bean.navigate_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotNavigatePosition {

    /**
     * destination : {"angle":100,"gridPosition":{"x":100,"y":100}}
     */

    private DestinationBean destination;

    public DestinationBean getDestination() {
        return destination;
    }

    public void setDestination(DestinationBean destination) {
        this.destination = destination;
    }

    public static class DestinationBean {
        /**
         * angle : 100
         * gridPosition : {"x":100,"y":100}
         */

        private int angle;
        private GridPositionBean gridPosition;

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
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
             * x : 100
             * y : 100
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
