package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/28
 * @Describe
 */
public class RobotTaskQueueList {

    /**
     * data : [{"name":"aaa","loop":true,"tasks":[{"name":"PlayPathTask  ","start_param":{"map_name":"sss","path_name":"ppp"}},{"name":"PlayGraphPathTask","start_param":{"map_name":"aaa","graph_name":"aaa","graph_path_name":"aaa"}},{"name":"PlayGraphPathGroupTask","start_param":{"map_name":"aa","graph_name":"aaa","graph_path_group_name":"aaaxxx"}}]}]
     * errorCode :
     * msg : successed
     * successed : true
     */

    private String errorCode;
    private String msg;
    private boolean successed;
    private List<DataBean> data;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : aaa
         * loop : true
         * tasks : [{"name":"PlayPathTask  ","start_param":{"map_name":"sss","path_name":"ppp"}},{"name":"PlayGraphPathTask","start_param":{"map_name":"aaa","graph_name":"aaa","graph_path_name":"aaa"}},{"name":"PlayGraphPathGroupTask","start_param":{"map_name":"aa","graph_name":"aaa","graph_path_group_name":"aaaxxx"}}]
         */

        private String name;
        private boolean loop;
        private List<TasksBean> tasks;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isLoop() {
            return loop;
        }

        public void setLoop(boolean loop) {
            this.loop = loop;
        }

        public List<TasksBean> getTasks() {
            return tasks;
        }

        public void setTasks(List<TasksBean> tasks) {
            this.tasks = tasks;
        }

        public static class TasksBean {
            /**
             * name : PlayPathTask
             * start_param : {"map_name":"sss","path_name":"ppp"}
             */

            private String name;
            private StartParamBean start_param;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public StartParamBean getStart_param() {
                return start_param;
            }

            public void setStart_param(StartParamBean start_param) {
                this.start_param = start_param;
            }

            public static class StartParamBean {
                /**
                 * map_name : sss
                 * path_name : ppp
                 */

                private String map_name;
                private String path_name;

                public String getMap_name() {
                    return map_name;
                }

                public void setMap_name(String map_name) {
                    this.map_name = map_name;
                }

                public String getPath_name() {
                    return path_name;
                }

                public void setPath_name(String path_name) {
                    this.path_name = path_name;
                }
            }
        }
    }
}
