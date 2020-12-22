package com.dcm360.controller;


import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ChargeStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserPhit;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.navigate_bean.RobotNavigatePosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.robot_interface.RobotControllerStatus;
import com.dcm360.controller.robot_interface.RobotDevice;
import com.dcm360.controller.robot_interface.RobotInitialize;
import com.dcm360.controller.robot_interface.RobotMap;
import com.dcm360.controller.robot_interface.RobotNavigate;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

import retrofit2.Call;

public interface RobotController extends RobotInitialize, RobotMap, RobotNavigate, RobotControllerStatus, RobotDevice {

    void connect_robot(String url);

    void move(float linearSpeed, float angularSpeed, RobotStatus<Status> status);

    void move_to(float distance, float speed, RobotStatus<Status> status);

    void stop_move_to(RobotStatus<Status> status);

    void rotate(int rotateAngle, float rotateSpeed, RobotStatus<Status> status);

    void stop_rotate(RobotStatus<Status> status);

    void reportChargeStatus(String state, RobotStatus<ChargeStatus> status);

    void ping(RobotStatus<Status> status);

    void navigatePosition(RobotNavigatePosition position, RobotStatus<Status> status);//导航到任意指定坐标点

    void isTaskQueueFinished(RobotStatus<Status> status);

    void getCurrentPosition(String mapName, RobotStatus<RobotPosition> position);

    Call<RobotLaserPhit> laserPhit();

    void use_map(String map_name, RobotStatus<Status> status);

    void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status);//获取虚拟墙

    void getRecordStatus(RobotStatus<RecordStatusBean> status);

    void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status);//添加虚拟强

    void setSpeedLevel(String level, RobotStatus<Status> status);//跑线速度

    void setnavigationSpeedLevel(String level, RobotStatus<Status> status);//导航速度
}