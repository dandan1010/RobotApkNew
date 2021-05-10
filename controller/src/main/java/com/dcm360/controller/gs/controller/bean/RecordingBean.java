package com.dcm360.controller.gs.controller.bean;

import java.util.List;

public class RecordingBean {


    /**
     * args : ["/root/GAUSSIAN_RUNTIME_DIR//bag/test.bag"]
     * node_name : record-bag
     * op_name : launch
     */
    private List<String> args;
    private String node_name;
    private String op_name;

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public void setOp_name(String op_name) {
        this.op_name = op_name;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getNode_name() {
        return node_name;
    }

    public String getOp_name() {
        return op_name;
    }
}
