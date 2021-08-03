package com.uslam.icontroller;

import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.uslam.bean.TargetPointBean;

import org.json.JSONObject;

public interface UsLamController {

    void setTargetPoint(TargetPointBean targetPoint, RobotStatus<Status> status);//设置目标点

    void getRlocalization();//查询重定位

    void searchNavigation();//查询导航状态

    //"fluent_mode": "bool, false: safe mode, true: fluent mode",
    void setFlowPattern(boolean fluent_mode, RobotStatus<Status> status);//设置流畅模式

    //"map_name": "string, the name of the map",
    void checkRepeatMap(String mapName, RobotStatus<Status> status);//检查重复的地图名字

    /*
    * "import_map_force: boolean, no condition import the umap to the robot, even if the map_name is repeated."
  "map_name": "string, map name.",
  "umap": "umap json object."*/
    void import_map(boolean import_map_force, String map_name, JSONObject umap, RobotStatus<Status> status);//导入地图

    void auto_scanMap(boolean suto_status, boolean points_list, RobotStatus<Status> status);//获取自主构图状态

    /*
    * "distance": double, 雷达探测最大距离，默认25m
  "area": double, 最大建图面积，默认225㎡
  "fov": int, 最大视角，默认150°*/
    void controll_auto_scanmap(double distance, double area, boolean fov, RobotStatus<Status> status);//控制自主构图

    /*
    * "saveMap": boolean, save the building map or not
  "save_map_force: boolean, no condition save the map to the robot, even if the map_name is repeated."
  "map_name": "string, map name.",*/
    void stop_auto_scanmap(boolean saveMap,boolean save_map_force, String map_name, RobotStatus<Status> status);//结束自主构图

}
