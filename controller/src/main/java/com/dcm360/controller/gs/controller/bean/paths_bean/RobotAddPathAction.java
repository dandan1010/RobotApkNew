package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public class RobotAddPathAction {

    /**
     * fields : [{"fields":[],"name":"millisecond","type":"uint64","value":""}]
     * name : Pause
     */

    private String name;
    private List<FieldsBean> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldsBean> getFields() {
        return fields;
    }

    public void setFields(List<FieldsBean> fields) {
        this.fields = fields;
    }

    public static class FieldsBean {
        /**
         * fields : []
         * name : millisecond
         * type : uint64
         * value :
         */

        private String name;
        private String type;
        private String value;
        private List<?> fields;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<?> getFields() {
            return fields;
        }

        public void setFields(List<?> fields) {
            this.fields = fields;
        }
    }
}
