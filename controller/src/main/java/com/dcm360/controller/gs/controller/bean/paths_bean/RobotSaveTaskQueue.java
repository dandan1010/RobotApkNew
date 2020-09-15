package com.dcm360.controller.gs.controller.bean.paths_bean;

import java.util.List;

/**
 * @author LiYan
 * @create 2019/4/28
 * @Describe
 */
public class RobotSaveTaskQueue {

    /**
     * name : aaa
     * map_name :
     * map_id :
     * loop : false
     * tasks : [{"name":"PlayPathTask","start_param":{"map_name":"sss","path_name":"ppp"}},{"name":"PlayGraphPathTask","start_param":{"map_name":"sss","graph_name":"ppp","graph_path_name":"bb"}},{"name":"PlayGraphPathGroupTask","start_param":{"map_name":"sss","graph_name":"ppp","graph_path_group_name":"aa"}},{"name":"NavigationTask","start_param":{"map_name":"sss","position_name":"ppp"}},{"name":"NavigationTask","start_param":{"destination":{"angle":30,"gridPosition":{"x":123,"y":123}}}}]
     */

    private String name;
    private String map_name;
    private String map_id;
    private boolean loop;
    private List<TasksBean> tasks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMap_name() {
        return map_name;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }

    public String getMap_id() {
        return map_id;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
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
             * position_name : ppp
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
