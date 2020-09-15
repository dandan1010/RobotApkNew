package com.dcm360.controller.robot_interface;


import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public interface RobotMap {

    void getMapPicture(String mapName, RobotStatus<byte[]> png);//获取地图图片

    Response<ResponseBody> download_map(String mapName);//获取地图数据

    void upload_map(String mapName, String path, RobotStatus<Status> status);//恢复地图数据

    Status upload_map_syn(String mapName, String path);//恢复地图数据

    Status delete_map_syn(String map_name);//删除地图

    void scanMapPng(RobotStatus<byte[]> png);

}
