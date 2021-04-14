package com.dcm360.controller.gs.controller.bean.charge_bean;

import java.util.ArrayList;

public class ModifyRobotParam {

    //[{"namespace":"/strategy/charger_base/backward_dis","type":"double","value":"0.6"}]

    public static class RobotParam {
        private String namespace;
        private String type;
        private String value;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
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
    }
}
