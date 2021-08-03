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
    public void initialize_directly(String map_name, String init_point_name, RobotStatus<Status> status) {

    }

    @Override
    public void initDirectly(String mapName, String initPointName, RobotStatus<Status> status) {

    }

    @Override
    public void initRobot(String mapName, String initPointName, RobotStatus<Status> status) {

    }

    @Override
    public void initCustom(RobotInitCustom robotInitCustom, RobotStatus<Status> status) {

    }

    @Override
    public void initCustomDirectly(RobotInitCustom robotInitCustom, RobotStatus<Status> status) {

    }

    @Override
    public void initGlobal(String mapName, RobotStatus<Status> status) {

    }

    @Override
    public void pauseNavigate(RobotStatus<Status> status) {

    }

    @Override
    public void resumeNavigate(RobotStatus<Status> status) {

    }

    @Override
    public void laserRaw(RobotStatus<RobotLaserRaw> status) {

    }

    @Override
    public Call<RobotLaserPhit> laserPhit() {
        return null;
    }

    @Override
    public void odomRaw(RobotStatus<RobotOdomRaw> status) {

    }

    @Override
    public void gpsRaw(RobotStatus<RobotGpsRaw> status) {

    }

    @Override
    public void syncGpsData(RobotSyncGpsData data, RobotStatus<Status> status) {

    }

    @Override
    public void protector(RobotStatus<RobotProtector> status) {

    }

    @Override
    public void mobileData(RobotStatus<RobotMobileData> status) {

    }

    @Override
    public void nonMapData(RobotStatus<RobotNonMapData> status) {

    }

    @Override
    public void connect_robot(String url) {

    }

    @Override
    public void navigatePosition(RobotNavigatePosition position, RobotStatus<Status> status) {

    }

    @Override
    public void real_time_data(RobotStatus<Status> status) {

    }

    @Override
    public void navigate_Position(String map_name, String position_name, RobotStatus<Status> status) {

    }

    @Override
    public void navigation_list(String map_name, RobotStatus<List<String>> status) {

    }

    @Override
    public void navigationList(String map_name, RobotStatus<RobotPositions> robotStatus) {

    }

    @Override
    public void charge_Position(String map_name, RobotStatus<String> pos) {

    }

    @Override
    public void start_taskQueue(RobotTaskQueue queue, RobotStatus<Status> status) {

    }

    @Override
    public void save_taskQueue(RobotTaskQueue taskQueue, RobotStatus<Status> status) {

    }

    @Override
    public void cmdVel(RobotStatus<RobotCmdVel> status) {

    }

    @Override
    public void deviceStatus(RobotStatus<RobotDeviceStatus> status) {

    }

    @Override
    public void footprint(RobotStatus<RobotFootprint> status) {

    }

    @Override
    public void getPosition(String mapName, int type, RobotStatus<RobotPositions> status) {

    }

    @Override
    public void asyncStopScanMap(RobotStatus<Status> status) {

    }

    @Override
    public void isStopScanFinished(RobotStatus<Status> status) {

    }

    @Override
    public void scanMapPng(RobotStatus<ResponseBody> status) {

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
    public void loadMap(String mapName, RobotStatus<Status> status) {

    }

    @Override
    public void cancelScanMap(RobotStatus<Status> status) {

    }

    @Override
    public void gps(RobotStatus<RobotMapGPS> status) {

    }

    @Override
    public void navigate(String mapName, String positionName, RobotStatus<Status> status) {

    }

    @Override
    public void navigate(RobotNavigatePosition position, RobotStatus<Status> status) {

    }

    @Override
    public void navigationPath(RobotStatus<RobotNavigationPath> status) {

    }

    @Override
    public void navigationPath(RobotNavigationToPath navigation, RobotStatus<RobotNavigationPath> status) {

    }

    @Override
    public void getPath(String mapName, RobotStatus<RobotPath> status) {

    }

    @Override
    public void deletePath(String mapName, String pathName, RobotStatus<Status> status) {

    }

    @Override
    public void saveTaskQueue(RobotTaskQueue taskQueue, RobotStatus<Status> status) {

    }

    @Override
    public void taskQueues(String mapName, RobotStatus<RobotTaskQueueList> status) {

    }

    @Override
    public void deleteTaskQueue(String mapName, String taskQueueName, RobotStatus<Status> status) {

    }

    @Override
    public void startTaskQueue(RobotTaskQueue queue, RobotStatus<Status> status) {

    }

    @Override
    public void stopTaskQueue(RobotStatus<Status> status) {

    }

    @Override
    public void pauseTaskQueue(RobotStatus<Status> status) {

    }

    @Override
    public void resumeTaskQueue(RobotStatus<Status> status) {

    }

    @Override
    public void stopCurrentTask(RobotStatus<Status> status) {

    }

    @Override
    public void isTaskQueueFinished(RobotStatus<Status> status) {

    }

    @Override
    public void robotMove(RobotMove move, RobotStatus<Status> status) {

    }

    @Override
    public void robotMoveTo(float distance, float speed, RobotStatus<Status> status) {

    }

    @Override
    public void isMoveToFinished(RobotStatus<Status> status) {

    }

    @Override
    public void stopMoveTo(RobotStatus<Status> status) {

    }

    @Override
    public void rotate(RobotRotate rotate, RobotStatus<Status> status) {

    }

    @Override
    public void isRotateFinished(RobotStatus<Status> status) {

    }

    @Override
    public void stopRotate(RobotStatus<Status> status) {

    }

    @Override
    public void clearMcuError(int errorId, RobotStatus<Status> status) {

    }

    @Override
    public void powerOff(RobotStatus<Status> status) {

    }

    @Override
    public void connectGSWebSocket(String url, StatusMessageListener listener) {

    }

    @Override
    public void ping(RobotStatus<Status> status) {

    }

    @Override
    public void reportChargeStatus(String state, RobotStatus<ChargeStatus> status) {

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
    public void getBag(String bagName, RobotStatus<ResponseBody> status) {

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
    public void getMapPng(MapPngBean mapPngBean, RobotStatus<ResponseBody> status) {

    }

    @Override
    public void deleteMap(String mapName, RobotStatus<Status> status) {

    }

    @Override
    public void loadMapList() {

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
    public void initialize(String map_name, String init_point_name, float x, float y, float theta, int i, RobotStatus<Status> status) {

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