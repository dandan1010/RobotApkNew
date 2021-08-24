package com.uslam.icontroller;

import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.RecordingBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ChargeStatus;
import com.dcm360.controller.gs.controller.bean.charge_bean.ModifyRobotParam;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotFootprint;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotMobileData;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotNonMapData;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.data_bean.VersionBean;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotMapGPS;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotSyncGpsData;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserPhit;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserRaw;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotEditMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotInitCustom;
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
import com.uslam.bean.TargetPointBean;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface GXController {

    //初始化
    void connect_robot(String url, String uuid);

    //导航
    void navigate_Position(String map_name, String position_name, TargetPointBean targetPointBean, RobotStatus<Status> status);//导航到点  传入之前标记的导航点的名字和地图名字

    void cmdVel(RobotStatus<RobotCmdVel> status); //实时角速度和线速度数据

    void deviceStatus(RobotStatus<RobotDeviceStatus> status); //设备状态数据

    void getPosition(String mapName, int type, RobotStatus<RobotPositions> status);//地图点数据

    void scanMapPng(RobotStatus<byte[]> status);//获取实时扫地图图片png

    Status deleteMapSyn(String mapName);//同步删除地图

    Status uploadMapSyn(String mapName, String mapPath);//上传地图

    void ping(RobotStatus<Status> status);//心跳

    void use_map(String mapName, RobotStatus<Status> status);

    void getMapPositions(String mapName, RobotStatus<RobotPositions> status);//地图所有的点

    void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status);//获取虚拟墙

    void getRecordStatus(RobotStatus<RecordStatusBean> status);

    void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status);//添加虚拟强

    void setSpeedLevel(String level, RobotStatus<Status> status);//跑路线速度

    void reset_robot(RobotStatus<Status> status);//恢复出厂设置

    void getUltrasonicPhit(RobotStatus<UltrasonicPhitBean> status);//声呐数据

    void deviceRobotVersion(RobotStatus<VersionBean> status);//设备版本信息

    void modifyRobotParam(ModifyRobotParam.RobotParam[] modifyRobotParam, RobotStatus<Status> status);//回桩距离

    void reboot(RobotStatus<Status> status);//导航重启

    void recording(RecordingBean recordingBean, RobotStatus<Status> status);//录制bag

    void getBag(String bagName, RobotStatus<byte[]> status);

    void deleteBag(String bagName, RobotStatus<Status> status);

}
