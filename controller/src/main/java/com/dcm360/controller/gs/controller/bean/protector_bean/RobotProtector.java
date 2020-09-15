package com.dcm360.controller.gs.controller.bean.protector_bean;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotProtector {

    /**
     * header : {"stamp":121212,"frame_id":"protector"}
     * data : 1010
     */

    private HeaderBean header;
    private String data;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static class HeaderBean {
        /**
         * stamp : 121212
         * frame_id : protector
         */

        private int stamp;
        private String frame_id;

        public int getStamp() {
            return stamp;
        }

        public void setStamp(int stamp) {
            this.stamp = stamp;
        }

        public String getFrame_id() {
            return frame_id;
        }

        public void setFrame_id(String frame_id) {
            this.frame_id = frame_id;
        }
    }
}
