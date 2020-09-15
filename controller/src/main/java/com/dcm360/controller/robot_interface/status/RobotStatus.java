package com.dcm360.controller.robot_interface.status;

public interface RobotStatus<T> {
    void success(T t);

    void error(Throwable error);
}
