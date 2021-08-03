package com.uslam.icontroller;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotWorkStatus;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.uslam.bean.MapPngBean;
import com.uslam.bean.MoveBean;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;

public interface RobotEventController {

    void setnavigationSpeedLevel(String level, RobotStatus<Status> status);//导航速度

    void move(MoveBean moveBean, RobotStatus<Status> status);

    void startScanMap(String mapName, int type, RobotStatus<Status> status);// 开始扫描地图,type = 1扩展地图

    /*
    * "saveMap": boolean, save the building map or not
  "save_map_force: boolean, no condition save the map to the robot, even if the map_name is repeated."
  "map_name": "string, map name.",*/
    void stopScanMap(String mapName, boolean saveMap, boolean save_map_force, RobotStatus<Status> status);//结束扫描并保存地图(同步)

    void getMapPng(MapPngBean mapPngBean, RobotStatus<ResponseBody> status);//获取地图图片png

    void deleteMap(String mapName, RobotStatus<Status> status);//删除地图

    void loadMapList();//获取地图列表

    /*
    * "map_name": "string, map name.",
  "umap": "umap json object."*/
    void updateMap(String originMapName, String newMapName, JSONObject umap, RobotStatus<Status> status);//更新地图

    Response<ResponseBody> downloadMap(String mapName);//下载地图压缩包

    void uploadMap(String mapName, String mapPath, RobotStatus<Status> status);//上传地图

    /**
     * {
     *     "points_array":
     *     [
     *         {
     *             "point_name": string, 目标点名称
     *             "point_type": string, 目标点类型（normal_position，charge_position）
     *             "map_x": double, 目标点坐标x(m)
     *             "map_y": double, 目标点坐标y(m)
     *             "theta": double, 目标点朝向(rad)
     *             "description": string, 目标点详细描述信息
     *         },
     *         {
     *             "point_name": string, 目标点名称
     *             "point_type": string, 目标点类型（normal_position，charge_position）
     *             "map_x": double, 目标点坐标x(m)
     *             "map_y": double, 目标点坐标y(m)
     *             "theta": double, 目标点朝向(rad)
     *             "description": string, 目标点详细描述信息
     *         }
     *     ]
     * }*/

    void add_Position(PositionListBean positionListBean, RobotStatus<Status> status);//添加点

    void deletePosition(String mapName, String point_name, PositionListBean positionListBean, RobotStatus<Status> status);//删除点

    void editPosition(String mapName, String originName, String newName, RobotStatus<Status> status);//编辑点

    void getPositions(String mapName, RobotStatus<RobotPosition> status);//获取初始化点列表

    void cancelNavigate(RobotStatus<Status> status);//取消导航

    /*
        * "simple": "bool, true"
          "speed": "string, low|normal|high",
          "direction": "string, left|right|forward|backward|stop|"
          * i:区分调用gx的哪一种初始化*/
    void initialize(String map_name, String init_point_name, float x, float y, float theta, int i, RobotStatus<Status> status);//转圈初始化

    void is_initialize_finished(RobotStatus<Status> status);//初始化是否完成

    void stop_initialize(com.dcm360.controller.robot_interface.status.RobotStatus<Status> status);//停止初始化

    void work_status(RobotStatus<RobotWorkStatus> status);//构建地图状态

    //机器人任务状态
    void RobotStatus(NavigationStatus navigationStatus, String... args);

}
