package com.uslam.factory;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.RecordingBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ChargeStatus;
import com.dcm360.controller.gs.controller.bean.charge_bean.ModifyRobotParam;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotFootprint;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotMobileData;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotNonMapData;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotWorkStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.VersionBean;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotMapGPS;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotSyncGpsData;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserPhit;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserRaw;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotEditMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotInitCustom;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
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
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.uslam.bean.MapPngBean;
import com.uslam.bean.MoveBean;
import com.uslam.bean.TargetPointBean;
import com.uslam.icontroller.GXController;
import com.uslam.icontroller.RobotEventController;
import com.uslam.icontroller.UsLamController;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class WorkFactory implements RobotEventController, GXController, UsLamController {

    @Override
    public void connect_robot(String url, RobotStatus<Status> status) {

    }

    @Override
    public void navigate_Position(String map_name, String position_name, TargetPointBean targetPointBean, RobotStatus<Status> status) {

    }

    @Override
    public void charge_Position(String map_name, RobotStatus<String> pos) {

    }

    @Override
    public void cmdVel(RobotStatus<RobotCmdVel> status) {

    }

    @Override
    public void deviceStatus(RobotStatus<RobotDeviceStatus> status) {

    }

    @Override
    public void getPosition(String mapName, int type, RobotStatus<RobotPositions> status) {

    }

    @Override
    public void scanMapPng(RobotStatus<byte[]> status) {

    }

    @Override
    public Status deleteMapSyn(String mapName) {
        return null;
    }

    @Override
    public Status uploadMapSyn(String mapName, String mapPath) {
        return null;
    }

    @Override
    public void editMap(String mapName, String operationType, RobotEditMap editMap, RobotStatus<Status> status) {

    }

    @Override
    public void ping(RobotStatus<Status> status) {

    }

    @Override
    public void use_map(String mapName, RobotStatus<Status> status) {

    }

    @Override
    public void getMapPositions(String mapName, RobotStatus<RobotPositions> status) {

    }

    @Override
    public void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status) {

    }

    @Override
    public void getRecordStatus(RobotStatus<RecordStatusBean> status) {

    }

    @Override
    public void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status) {

    }

    @Override
    public void setSpeedLevel(String level, RobotStatus<Status> status) {

    }

    @Override
    public void reset_robot(RobotStatus<Status> status) {

    }

    @Override
    public void getUltrasonicPhit(RobotStatus<UltrasonicPhitBean> status) {

    }

    @Override
    public void deviceRobotVersion(RobotStatus<VersionBean> status) {

    }

    @Override
    public void modifyRobotParam(ModifyRobotParam.RobotParam[] modifyRobotParam, RobotStatus<Status> status) {

    }

    @Override
    public void reboot(RobotStatus<Status> status) {

    }

    @Override
    public void recording(RecordingBean recordingBean, RobotStatus<Status> status) {

    }

    @Override
    public void getBag(String bagName, RobotStatus<byte[]> status) {

    }

    @Override
    public void deleteBag(String bagName, RobotStatus<Status> status) {

    }

    @Override
    public void setnavigationSpeedLevel(String level, RobotStatus<Status> status) {

    }

    @Override
    public void move(MoveBean moveBean, RobotStatus<Status> status) {

    }

    @Override
    public void startScanMap(String mapName, int type, RobotStatus<Status> status) {

    }

    @Override
    public void stopScanMap(String mapName, boolean saveMap, boolean save_map_force, RobotStatus<Status> status) {

    }

    @Override
    public void cancelScanMap(String mapName, boolean saveMap, boolean save_map_force, RobotStatus<Status> status) {

    }

    @Override
    public void getMapPng(MapPngBean mapPngBean, RobotStatus<byte[]> status) {

    }

    @Override
    public void deleteMap(String mapName, RobotStatus<Status> status) {

    }

//    @Override
//    public RobotMap[] loadMapList() {
//        return loadMapList();
//    }


    @Override
    public void loadMapList(RobotStatus<RobotMap> status) {

    }

    @Override
    public void updateMap(String originMapName, String newMapName, JSONObject umap, RobotStatus<Status> status) {

    }

    @Override
    public Response<ResponseBody> downloadMap(String mapName) {
        return null;
    }

    @Override
    public void uploadMap(String mapName, String mapPath, RobotStatus<Status> status) {

    }

    @Override
    public void add_Position(PositionListBean positionListBean, RobotStatus<Status> status) {

    }

    @Override
    public void deletePosition(String mapName, String point_name, PositionListBean positionListBean, RobotStatus<Status> status) {

    }

    @Override
    public void editPosition(String mapName, String originName, String newName, RobotStatus<Status> status) {

    }

    @Override
    public void getPositions(String mapName, RobotStatus<RobotPosition> status) {

    }

    @Override
    public void cancelNavigate(RobotStatus<Status> status) {

    }

    @Override
    public void initialize(String map_name, String init_point_name, float x, float y, float theta, int type, RobotStatus<Status> status) {

    }

    @Override
    public void is_initialize_finished(RobotStatus<Status> status) {

    }

    @Override
    public void stop_initialize(RobotStatus<Status> status) {

    }

    @Override
    public void work_status(RobotStatus<RobotWorkStatus> status) {

    }

    @Override
    public void RobotStatus(NavigationStatus navigationStatus, String... args) {

    }

    @Override
    public void setTargetPoint(TargetPointBean targetPoint, RobotStatus<Status> status) {

    }

    @Override
    public void getRlocalization() {

    }

    @Override
    public void searchNavigation() {

    }

    @Override
    public void setFlowPattern(boolean fluent_mode, RobotStatus<Status> status) {

    }

    @Override
    public void checkRepeatMap(String mapName, RobotStatus<Status> status) {

    }

    @Override
    public void import_map(boolean import_map_force, String map_name, JSONObject umap, RobotStatus<Status> status) {

    }

    @Override
    public void auto_scanMap(boolean suto_status, boolean points_list, RobotStatus<Status> status) {

    }

    @Override
    public void controll_auto_scanmap(double distance, double area, boolean fov, RobotStatus<Status> status) {

    }

    @Override
    public void stop_auto_scanmap(boolean saveMap, boolean save_map_force, String map_name, RobotStatus<Status> status) {

    }
}