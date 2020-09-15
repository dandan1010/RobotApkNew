package com.dcm360.controller.robot_interface;


import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

public interface RobotInitialize {
    void initialize_directly(String map_name, String init_point_name, RobotStatus<Status> status);//不转圈初始化

    void initialize(String map_name, String init_point_name, RobotStatus<Status> status);//转圈初始化

    void stop_initialize(RobotStatus<Status> status);//停止初始化

    void is_initialize_finished(RobotStatus<Status> status);//初始化是否完成
}
