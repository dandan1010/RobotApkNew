package com.uslam.icontroller;

public interface RobotStatus<T> {
    void success(T t);

    void error(Throwable error);
}
