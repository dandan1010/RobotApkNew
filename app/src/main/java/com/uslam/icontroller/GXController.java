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

    //?????????
    void connect_robot(String url, RobotStatus<Status> status);

    //??????
    void navigate_Position(String map_name, String position_name, TargetPointBean targetPointBean, RobotStatus<Status> status);//????????????  ??????????????????????????????????????????????????????

    void charge_Position(String map_name, RobotStatus<String> pos);//???????????????

    void cmdVel(RobotStatus<RobotCmdVel> status); //?????????????????????????????????

    void deviceStatus(RobotStatus<RobotDeviceStatus> status); //??????????????????

    void getPosition(String mapName, int type, RobotStatus<RobotPositions> status);//???????????????

    void scanMapPng(RobotStatus<byte[]> status);//???????????????????????????png

    Status deleteMapSyn(String mapName);//??????????????????

    Status uploadMapSyn(String mapName, String mapPath);//????????????

    void editMap(String mapName, String operationType, RobotEditMap editMap, RobotStatus<Status> status);//????????????

    void ping(RobotStatus<Status> status);//??????

    void use_map(String mapName, RobotStatus<Status> status);

    void getMapPositions(String mapName, RobotStatus<RobotPositions> status);//??????????????????

    void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status);//???????????????

    void getRecordStatus(RobotStatus<RecordStatusBean> status);

    void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status);//???????????????

    void setSpeedLevel(String level, RobotStatus<Status> status);//???????????????

    void reset_robot(RobotStatus<Status> status);//??????????????????

    void getUltrasonicPhit(RobotStatus<UltrasonicPhitBean> status);//????????????

    void deviceRobotVersion(RobotStatus<VersionBean> status);//??????????????????

    void modifyRobotParam(ModifyRobotParam.RobotParam[] modifyRobotParam, RobotStatus<Status> status);//????????????

    void reboot(RobotStatus<Status> status);//????????????

    void recording(RecordingBean recordingBean, RobotStatus<Status> status);//??????bag

    void getBag(String bagName, RobotStatus<byte[]> status);

    void deleteBag(String bagName, RobotStatus<Status> status);

}
