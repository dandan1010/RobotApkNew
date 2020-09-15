package com.dcm360.controller.robot_interface.status;

import com.dcm360.controller.gs.controller.bean.system_bean.RobotDeviceStatus;

/**
 * @author LiYan
 * @create 2019/5/15
 * @Describe
 */
public interface DeviceStatus {
    void battery(double battery);

    void batteryVoltage(double batteryVoltage);

    void charger(int charger);

    void emergency(boolean isStop);

//    void connectStatus(boolean isconnect);

    void getDeviceStatus(RobotDeviceStatus deviceStatus);
}
