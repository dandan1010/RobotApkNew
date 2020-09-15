package com.dcm360.controller.robot_interface;


import com.dcm360.controller.robot_interface.status.DeviceStatus;
import com.dcm360.controller.robot_interface.status.NavigationStatus;

public interface RobotControllerStatus {
    void RobotStatus(NavigationStatus navigationStatus, String... args);
}
