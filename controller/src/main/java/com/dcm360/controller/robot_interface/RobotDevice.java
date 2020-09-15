package com.dcm360.controller.robot_interface;


import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

public interface RobotDevice {
    void power_Off(RobotStatus<Status> status);

    void protector(RobotStatus<Status> status);
}
