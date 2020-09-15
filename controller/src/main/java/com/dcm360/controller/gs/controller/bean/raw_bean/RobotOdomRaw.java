package com.dcm360.controller.gs.controller.bean.raw_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotOdomRaw {

    /**
     * header : {"frame_id":"base_odom","stamp":848461584}
     * pose : {"orientation":{"w":1,"x":0,"y":0,"z":0},"position":{"x":0,"y":0,"z":0}}
     * twist : {"angular":{"x":0,"y":0,"z":0},"linear":{"x":0,"y":0,"z":0}}
     */

    private HeaderBean header;
    private PoseBean pose;
    private TwistBean twist;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public PoseBean getPose() {
        return pose;
    }

    public void setPose(PoseBean pose) {
        this.pose = pose;
    }

    public TwistBean getTwist() {
        return twist;
    }

    public void setTwist(TwistBean twist) {
        this.twist = twist;
    }

    public static class HeaderBean {
        /**
         * frame_id : base_odom
         * stamp : 848461584
         */

        private String frame_id;
        private int stamp;

        public String getFrame_id() {
            return frame_id;
        }

        public void setFrame_id(String frame_id) {
            this.frame_id = frame_id;
        }

        public int getStamp() {
            return stamp;
        }

        public void setStamp(int stamp) {
            this.stamp = stamp;
        }
    }

    public static class PoseBean {
        /**
         * orientation : {"w":1,"x":0,"y":0,"z":0}
         * position : {"x":0,"y":0,"z":0}
         */

        private OrientationBean orientation;
        private PositionBean position;

        public OrientationBean getOrientation() {
            return orientation;
        }

        public void setOrientation(OrientationBean orientation) {
            this.orientation = orientation;
        }

        public PositionBean getPosition() {
            return position;
        }

        public void setPosition(PositionBean position) {
            this.position = position;
        }

        public static class OrientationBean {
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

            public int getW() {
                return w;
            }

            public void setW(int w) {
                this.w = w;
            }

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

            public int getZ() {
                return z;
            }

            public void setZ(int z) {
                this.z = z;
            }
        }

        public static class PositionBean {
            /**
             * x : 0
             * y : 0
             * z : 0
             */

            private int x;
            private int y;
            private int z;

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

            public int getZ() {
                return z;
            }

            public void setZ(int z) {
                this.z = z;
            }
        }
    }

    public static class TwistBean {
        /**
         * angular : {"x":0,"y":0,"z":0}
         * linear : {"x":0,"y":0,"z":0}
         */

        private AngularBean angular;
        private LinearBean linear;

        public AngularBean getAngular() {
            return angular;
        }

        public void setAngular(AngularBean angular) {
            this.angular = angular;
        }

        public LinearBean getLinear() {
            return linear;
        }

        public void setLinear(LinearBean linear) {
            this.linear = linear;
        }

        public static class AngularBean {
            /**
             * x : 0
             * y : 0
             * z : 0
             */

            private int x;
            private int y;
            private int z;

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

            public int getZ() {
                return z;
            }

            public void setZ(int z) {
                this.z = z;
            }
        }

        public static class LinearBean {
            /**
             * x : 0
             * y : 0
             * z : 0
             */

            private int x;
            private int y;
            private int z;

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

            public int getZ() {
                return z;
            }

            public void setZ(int z) {
                this.z = z;
            }
        }
    }
}
