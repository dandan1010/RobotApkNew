package com.dcm360.controller.robot_interface;


import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotMapGPS;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotSaveTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

import java.util.List;

public interface RobotNavigate {

    void pause_navigate(RobotStatus<Status> status);//暂停导航

    void resume_navigate(RobotStatus<Status> status);//恢复导航

    void cancel_navigate(RobotStatus<Status> status);//取消导航

    void real_time_data(RobotStatus<Status> status);//机器人在地图上的实时位置

    void gps(RobotStatus<RobotMapGPS> status);//机器人在地图上的位置

    void navigate_Position(String map_name, String position_name, RobotStatus<Status> status);//导航到点  传入之前标记的导航点的名字和地图名字

    void navigation_list(String map_name, RobotStatus<List<String>> status);//获取地图上的导航列表   获取特定地图导航点的列表，通常只取名字，跟点列表一样，type传2

    void navigationList(String map_name, RobotStatus<RobotPositions> robotStatus);//获取地图上的导航列表

    void charge_Position(String map_name, RobotStatus<String> pos);//获取充电点

    void start_taskQueue(RobotTaskQueue queue, RobotStatus<Status> status);//开始执行任务队列

    void save_taskQueue(RobotTaskQueue taskQueue, RobotStatus<Status> status);
}
