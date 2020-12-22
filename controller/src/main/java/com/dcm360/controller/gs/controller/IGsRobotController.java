package com.dcm360.controller.gs.controller;


import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ChargeStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotFootprint;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotMobileData;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotNonMapData;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotMapGPS;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotSyncGpsData;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserPhit;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserRaw;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotEditMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotInitCustom;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.navigate_bean.RobotNavigatePosition;
import com.dcm360.controller.gs.controller.bean.navigate_bean.RobotNavigationPath;
import com.dcm360.controller.gs.controller.bean.navigate_bean.RobotNavigationToPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.protector_bean.RobotProtector;
import com.dcm360.controller.gs.controller.bean.raw_bean.RobotGpsRaw;
import com.dcm360.controller.gs.controller.bean.raw_bean.RobotOdomRaw;
import com.dcm360.controller.gs.controller.bean.system_bean.RobotMove;
import com.dcm360.controller.gs.controller.bean.system_bean.RobotRotate;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.dcm360.controller.gs.controller.bean.vel_bean.RobotCmdVel;
import com.dcm360.controller.gs.controller.listener.StatusMessageListener;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * @author LiYan
 * @create 2019/4/25
 * @Describe
 */
public interface IGsRobotController {
    boolean isHasWebSocket(String url);

    void initialize(String baseUrl);

    void laserRaw(RobotStatus<RobotLaserRaw> status); //激光设备数据

    Call<RobotLaserPhit> laserPhit();//激光设备栅格化数据

    void odomRaw(RobotStatus<RobotOdomRaw> status);//里程计设备数据

    void gpsRaw(RobotStatus<RobotGpsRaw> status);//gps设备数据

    void syncGpsData(RobotSyncGpsData data, RobotStatus<Status> status); //同步gps数据

    void protector(RobotStatus<RobotProtector> status);//碰撞开关设备数据

    void mobileData(RobotStatus<RobotMobileData> status);//移动障碍物检测数据

    void nonMapData(RobotStatus<RobotNonMapData> status);//非地图障碍物检测数据

    void cmdVel(RobotStatus<RobotCmdVel> status); //实时角速度和线速度数据

    void deviceStatus(RobotStatus<RobotDeviceStatus> status); //设备状态数据

    void footprint(RobotStatus<RobotFootprint> status);//机器人外观形状数据

    void getPosition(String mapName, int type, RobotStatus<RobotPositions> status);//地图点数据

    void addPosition(String positionName, int type, RobotStatus<Status> status);//记录点

    void deletePosition(String mapName, String positionName, RobotStatus<Status> status);//删除点

    void renamePosition(String mapName, String originName, String newName, RobotStatus<Status> status);//重命名点

    void startScanMap(String mapName, int type, RobotStatus<Status> status);// 开始扫描地图

    void stopScanMap(RobotStatus<Status> status);//结束扫描并保存地图(同步)

    void cancelScanMap(RobotStatus<Status> status);//取消扫描不保存地图

    void asyncStopScanMap(RobotStatus<Status> status);//结束扫描保存地图(异步) //推荐使用

    void isStopScanFinished(RobotStatus<Status> status);//异步结束扫地图是否完成

    void scanMapPng(RobotStatus<ResponseBody> status);//获取实时扫地图图片png

    void getMapPng(String mapName, RobotStatus<ResponseBody> status);//获取地图图片png

    void deleteMap(String mapName, RobotStatus<Status> status);//删除地图

    Status deleteMapSyn(String mapName);//同步删除地图

    void renameMap(String originMapName, String newMapName, RobotStatus<Status> status);//修改地图名称

    Response<ResponseBody> downloadMap(String mapName);//下载地图

    void uploadMap(String mapName, String mapPath, RobotStatus<Status> status);//上传地图

    Status uploadMapSyn(String mapName, String mapPath);//上传地图

    void editMap(String mapName, String operationType, RobotEditMap editMap, RobotStatus<Status> status);//编辑地图

    void loadMap(String mapName, RobotStatus<Status> status);//加载地图

    void initDirectly(String mapName, String initPointName, RobotStatus<Status> status);//不转圈初始化

    void initRobot(String mapName, String initPointName, RobotStatus<Status> status);//转圈初始化

    void initCustom(RobotInitCustom robotInitCustom, RobotStatus<Status> status);//自定义初始化

    void initCustomDirectly(RobotInitCustom robotInitCustom, RobotStatus<Status> status);//自定义不转圈初始化

    void initGlobal(String mapName, RobotStatus<Status> status);//全地图初始化

    void stopInit(RobotStatus<Status> status);//停止初始化

    void isInitFinished(RobotStatus<Status> status);//检查是否初始化完成

    void getPositions(String mapName, RobotStatus<RobotPosition> status);//获取初始化点列表

    void gps(RobotStatus<RobotMapGPS> status);//机器人在地图的GPS数据

    void navigate(String mapName, String positionName, RobotStatus<Status> status);//导航到导航点

    void navigate(RobotNavigatePosition position, RobotStatus<Status> status);//导航到任意指定坐标点

    void pauseNavigate(RobotStatus<Status> status);//暂停导航

    void resumeNavigate(RobotStatus<Status> status);//恢复导航

    void cancelNavigate(RobotStatus<Status> status);//取消导航

    void navigationPath(RobotStatus<RobotNavigationPath> status);//导航实时路线

    void navigationPath(RobotNavigationToPath navigation, RobotStatus<RobotNavigationPath> status);//任意两点得到导航路线

    void getPath(String mapName, RobotStatus<RobotPath> status);//导航实时路线

    void deletePath(String mapName, String pathName, RobotStatus<Status> status);//导航实时路线

    void saveTaskQueue(RobotTaskQueue taskQueue, RobotStatus<Status> status);//添加保存任务队列

    void taskQueues(String mapName, RobotStatus<RobotTaskQueueList> status);// 任务队列列表

    void deleteTaskQueue(String mapName, String taskQueueName, RobotStatus<Status> status);// 删除任务队列

    void startTaskQueue(RobotTaskQueue queue, RobotStatus<Status> status);//开始执行任务队列

    void stopTaskQueue(RobotStatus<Status> status);//停止所有队列任务

    void pauseTaskQueue(RobotStatus<Status> status);//暂停队列任务

    void resumeTaskQueue(RobotStatus<Status> status);//恢复队列任务

    void stopCurrentTask(RobotStatus<Status> status);//停止当前任务

    void isTaskQueueFinished(RobotStatus<Status> status);//队列任务是否完成

    void robotMove(RobotMove move, RobotStatus<Status> status);//移动控制

    void robotMoveTo(float distance, float speed, RobotStatus<Status> status);//定距离定速度移动控制

    void isMoveToFinished(RobotStatus<Status> status);//定距离定速度移动控制是否结束

    void stopMoveTo(RobotStatus<Status> status);//停止定距离定速度移动控制

    void rotate(RobotRotate rotate, RobotStatus<Status> status);//转动固定角度

    void isRotateFinished(RobotStatus<Status> status);//转动固定角度是否结束

    void stopRotate(RobotStatus<Status> status);//停止转动固定角度

    void clearMcuError(int errorId, RobotStatus<Status> status);//清除驱动器错误

    void powerOff(RobotStatus<Status> status);//关机

    void connectGSWebSocket(String url, StatusMessageListener listener);//监听状态

    void ping(RobotStatus<Status> status);//心跳

    void reportChargeStatus(String state, RobotStatus<ChargeStatus> status);//传感器板会返回充电口状态

    void use_map(String mapName, RobotStatus<Status> status);

    void add_Position(PositionListBean positionListBean, RobotStatus<Status> status);

    void getMapPositions(String mapName, RobotStatus<RobotPositions> status);//地图所有的点

    void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status);//获取虚拟墙

    void getRecordStatus(RobotStatus<RecordStatusBean> status);

    void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status);//添加虚拟强

    void setSpeedLevel(String level, RobotStatus<Status> status);//跑路线速度

    void setnavigationSpeedLevel(String level, RobotStatus<Status> status);//导航速度

    void reset_robot(RobotStatus<Status> status);//恢复出厂设置

    void getUltrasonicPhit(RobotStatus<UltrasonicPhitBean> status);//声呐数据
}
