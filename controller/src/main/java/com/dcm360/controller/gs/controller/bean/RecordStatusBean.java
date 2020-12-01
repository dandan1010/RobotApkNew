package com.dcm360.controller.gs.controller.bean;

import java.util.List;

public class RecordStatusBean {


    /**
     * msg : successed
     * data : {"values":[{"navigation":"running"}],"name":"op_status","message":"prepared","hardware_id":""}
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

    public class DataEntity {
        /**
         * values : [{"navigation":"running"}]
         * name : op_status
         * message : prepared
         * hardware_id :
         */
        private List<ValuesEntity> values;
        private String name;
        private String message;
        private String hardware_id;

        public void setValues(List<ValuesEntity> values) {
            this.values = values;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setHardware_id(String hardware_id) {
            this.hardware_id = hardware_id;
        }

        public List<ValuesEntity> getValues() {
            return values;
        }

        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }

        public String getHardware_id() {
            return hardware_id;
        }

        public class ValuesEntity {
            /**
             * navigation : running
             */
            private String navigation;

            public void setNavigation(String navigation) {
                this.navigation = navigation;
            }

            public String getNavigation() {
                return navigation;
            }
        }
    }
}
