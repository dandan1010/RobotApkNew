package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/28
 * @Describe
 */
public class RobotTaskQueue {

    /**
     * name : sss
     * loop : false
     * loop_count : 0
     * map_name :
     * tasks : [{"name":"PlayPathTask","start_param":{"map_name":"sss","path_name":"ppp"}}]
     */

    private String name;
    private boolean loop;
    private int loop_count;
    private String map_name;
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

    public int getLoop_count() {
        return loop_count;
    }

    public void setLoop_count(int loop_count) {
        this.loop_count = loop_count;
    }

    public String getMap_name() {
        return map_name;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
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
            private String position_name;

            public String getMap_name() {
                return map_name;
            }

            public void setMap_name(String map_name) {
                this.map_name = map_name;
            }

            public String getPosition_name() {
                return position_name;
            }

            public void setPosition_name(String position_name) {
                this.position_name = position_name;
            }
        }
    }
}
