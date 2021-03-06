package com.dcm360.controller.gs.controller.service;

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
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotAddPathAction;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotPathData;
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
import com.dcm360.controller.robot_interface.bean.Status;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The interface Gs com.example.robotapk.controller service.
 *
 * @author Fcj
 * @create 2019 /4/28
 * @Describe
 */
public interface GsControllerService {

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/laser_raw")
    Call<RobotLaserRaw> laserRaw();

    /**
     * ???????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/laser_phit")
    Call<RobotLaserPhit> laserPhit();

    /**
     * ?????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/odom_raw")
    Call<RobotOdomRaw> odomRaw();

    /**
     * gps????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/gps_raw")
    Call<RobotGpsRaw> gpsRaw();

    /**
     * ??????gps??????
     *
     * @param gpsData the sync gps data
     * @return the call
     */
    @POST("/gs-robot/cmd/sync_gps_data")
    Call<Status> syncGpsData(@Body RobotSyncGpsData gpsData);

    /**
     * ????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/protector")
    Call<RobotProtector> protector();

    /**
     * ???????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/mobile_data")
    Call<RobotMobileData> mobileData();

    /**
     * ??????????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/non_map_data")
    Call<RobotNonMapData> nonMapData();

    /**
     * ?????????????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/cmd_vel")
    Call<RobotCmdVel> cmdVel();

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/data/device_status")
    Call<RobotDeviceStatus> deviceStatus();

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/info")
    Call<VersionBean> deviceRobotVersion();

    /**
     * ???????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/footprint")
    Call<RobotFootprint> footprint();

    /**
     * ??????????????? http://10.7.5.88:8080/gs-robot/data/positions?map_name=new2&type=2
     *
     * @param map_name the map name
     * @param type     the type
     * @return the call
     */
    @GET("/gs-robot/data/positions")
    Observable<RobotPositions> positions(@Query("map_name") String map_name, @Query("type") int type);

    @GET("/gs-robot/data/positions")
    Observable<RobotPositions> getMapPositions(@Query("map_name") String map_name);

    /**
     * ?????????
     *
     * @param position_name the position name
     * @param type          the type
     * @return the call
     */
    @GET("/gs-robot/cmd/add_position")
    Call<Status> addPosition(@Query("position_name") String position_name, @Query("type") int type);

    /**
     * ?????????
     */
    @POST("/gs-robot/cmd/position/add_position")
    Call<Status> add_Position(@Body PositionListBean paramPositionListBean);

    /**
     * ?????????
     *
     * @param map_name      the map name
     * @param position_name the position name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_position")
    Call<Status> deletePosition(@Query("map_name") String map_name, @Query("position_name") String position_name);

    /**
     * ????????????
     *
     * @param map_name    the map name
     * @param origin_name the origin name
     * @param new_name    the new name
     * @return the call
     */
    @GET("/gs-robot/cmd/rename_position")
    Call<Status> renamePosition(@Query("map_name") String map_name, @Query("origin_name") String origin_name, @Query("new_name") String new_name);

    /**
     * ??????????????????
     *
     * @param map_name the map name
     * @param type     the type
     * @return the call
     */
    @GET("/gs-robot/cmd/start_scan_map")
    Call<Status> startScanMap(@Query("map_name") String map_name, @Query("type") int type);

    /**
     * ???????????????????????????(??????)
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_scan_map")
    Call<Status> stopScanMap();

    /**
     * ???????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_scan_map")
    Call<Status> cancelScanMap();

    /**
     * ????????????????????????(??????) //????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/async_stop_scan_map")
    Call<Status> asyncStopScanMap();

    /**
     * ?????????????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_stop_scan_finished")
    Call<Status> isScanFinished();

    /**
     * ???????????????????????????png
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/scan_map_png")
    Call<ResponseBody> scanMapPng();


    @GET("/gs-robot/real_time_data/scan_candidate_init_data")
    Observable<List<PositionListBean>> getInitPositions();

    @GET("/gs-robot/real_time_data/scan_candidate_position_data")
    Observable<List<PositionListBean>> getPositions();

    /**
     * ??????????????????png
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/data/map_png")
    Call<ResponseBody> mapPng(@Query("map_name") String map_name);

    /**
     * ??????????????????
     *
     * @return the maps
     */
    @GET("/gs-robot/data/maps")
    //Observable<RobotMap> getMapList();
    Call<RobotMap> getMapList();
    /**
     * ????????????
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_map")
    Call<Status> deleteMap(@Query("map_name") String map_name);

    /**
     * ??????????????????
     *
     * @param origin_map_name the origin map name
     * @param new_map_name    the new map name
     * @return the call
     */
    @GET("/gs-robot/cmd/rename_map")
    Call<Status> renameMap(@Query("origin_map_name") String origin_map_name, @Query("new_map_name") String new_map_name);

    /**
     * ????????????
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/data/download_map")
    Call<ResponseBody> downloadMap(@Query("map_name") String map_name);

    /**
     * ????????????
     *
     * @param map_name the map name
     * @param file     the file
     * @return the call
     */
    //@Multipart
    @POST("/gs-robot/data/upload_map")
    Call<Status> uploadMap(@Query("map_name") String map_name, @Body RequestBody file);

    /**
     * ????????????
     *
     * @param mapName       the map name
     * @param operationType the operation type
     * @param editMap       the robot edit map
     * @return the call
     */
    @POST(" /gs-robot/cmd/edit_map")
    Call<Status> editMap(@Query("map_name") String mapName, @Query("operation_type") String operationType, @Body RobotEditMap editMap);

    /**
     * ????????????
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/cmd/load_map")
    Call<Status> loadMap(@Query("map_name") String map_name);

    /**
     * ??????????????????
     *
     * @param map_name        the map name
     * @param init_point_name the init point name
     * @return the call
     */
    @GET("/gs-robot/cmd/initialize_directly")
    Call<Status> initDirectly(@Query("map_name") String map_name, @Query("init_point_name") String init_point_name);

    /**
     * ???????????????
     *
     * @param map_name        the map name
     * @param init_point_name the init point name
     * @return the call
     */
    @GET("/gs-robot/cmd/initialize")
    Call<Status> initRobot(@Query("map_name") String map_name, @Query("init_point_name") String init_point_name);

    /**
     * ??????????????????
     *
     * @param robotInitCustom the robot initialize customized
     * @return the call
     */
    @POST("/gs-robot/cmd/initialize_customized")
    Call<Status> initCustom(@Body RobotInitCustom robotInitCustom);

    /**
     * ???????????????????????????
     *
     * @param robotInitCustom the robot initialize customized
     * @return the call
     */
    @POST("/gs-robot/cmd/initialize_customized_directly")
    Call<Status> initCustomDirectly(@Body RobotInitCustom robotInitCustom);

    /**
     * ??????????????????
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/cmd/initialize_global")
    Call<Status> initGlobal(@Query("map_name") String map_name);

    /**
     * ???????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_initialize")
    Call<Status> stopInit();

    /**
     * ???????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_initialize_finished")
    Call<Status> isInitFinished();


    /**
     *
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/use_map")
    Call<Status> use_map(@Query("map_name") String mapName);

    /**
     * ????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/position")
    Call<RobotPosition> position();

    /**
     * ?????????????????????GPS??????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/gps")
    Call<RobotMapGPS> gps();

    /**
     * ??????????????????
     *
     * @param mapName       the map name
     * @param position_name the position name
     * @return the call
     */
    @GET("/gs-robot/cmd/position/navigate")
    Call<Status> navigate(@Query("map_name") String mapName, @Query("position_name") String position_name);

    /**
     * ??????????????????????????????
     *
     * @param navigatePosition the robot navigate position
     * @return the call
     */
    @POST("/gs-robot/cmd/quick/navigate")
    Call<Status> navigate(@Body RobotNavigatePosition navigatePosition);

    /**
     * ????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/pause_navigate")
    Call<Status> pauseNavigate();

    /**
     * ????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/resume_navigate")
    Call<Status> resumeNavigate();

    /**
     * ????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_navigate")
    Call<Status> cancelNavigate();

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/navigation_path")
    Call<RobotNavigationPath> navigationPath();

    /**
     * ?????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/work_status")
    Call<RobotWorkStatus> work_status();

    /**
     * ??????????????????????????????
     *
     * @param path the robot navigation to path
     * @return the call
     */
    @POST("/gs-robot/data/navigation_path")
    Call<RobotNavigationPath> navigationPath(@Body RobotNavigationToPath path);

    /**
     * ????????????
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/data/paths")
    Call<RobotPath> getPath(@Query("map_name") String map_name);

    /**
     * ????????????
     *
     * @param map_name  the map name
     * @param path_name the path name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_path")
    Call<Status> deletePath(@Query("map_name") String map_name, @Query("path_name") String path_name);

    /**
     * ???????????????
     *
     * @param map_name         the map name
     * @param origin_path_name the origin path name
     * @param new_path_name    the new path name
     * @return the call
     */
    @GET("/gs-robot/cmd/rename_path")
    Call<Status> renamePath(@Query("map_name") String map_name, @Query("origin_path_name") String origin_path_name, @Query("new_path_name") String new_path_name);

    /**
     * ??????????????????
     *
     * @param map_name  the map name
     * @param path_name the path name
     * @return the call
     */
    @GET("/gs-robot/cmd/start_record_path")
    Call<Status> startRecordPath(@Query("map_name") String map_name, @Query("path_name") String path_name);

    /**
     * ?????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_record_path")
    Call<Status> stopRecordPath();

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_record_path")
    Call<Status> cancelRecordPath();

    /**
     * ????????????????????????
     *
     * @param mapName   the map name
     * @param path_name the path name
     * @return the call
     */
    @GET("/gs-robot/cmd/start_record_area")
    Call<Status> startRecordArea(@Query("map_name") String mapName, @Query("path_name") String path_name);

    /**
     * ???????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_record_area")
    Call<Status> stopRecordArea();

    /**
     * ????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_record_area")
    Call<Status> cancelRecordArea();

    /**
     * ?????????????????????
     *
     * @param action the robot add path action
     * @return the call
     */
    @POST("/gs-robot/cmd/add_path_action")
    Call<Status> addPathAction(@Body RobotAddPathAction action);

    /**
     * ?????????????????????
     *
     * @param mapName  the map name
     * @param pathName the path name
     * @return the call
     */
    @GET("/gs-robot/data/path_data_list")
    Call<RobotPathData> pathDataList(@Query("map_name") String mapName, @Query("path_name") String pathName);


    /**
     * Save task queue call.
     *
     * @param taskQueue the robot save task queue
     * @return the call
     */
    @POST("/gs-robot/cmd/save_task_queue")
    Call<Status> saveTaskQueue(@Body RobotTaskQueue taskQueue);

    /**
     * Task queues call.
     *
     * @param mapName the map name
     * @return the call
     */
    @GET("/gs-robot/data/task_queues")
    Call<RobotTaskQueueList> taskQueues(@Query("map_name") String mapName);

    /**
     * ??????????????????
     *
     * @param mapName         the map name
     * @param task_queue_name the task queue name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_task_queue")
    Call<Status> deleteTaskQueue(@Query("map_name") String mapName, @Query("task_queue_name") String task_queue_name);

    /**
     * ????????????????????????
     *
     * @param queue the robot start task queue
     * @return the call
     */
    @POST("/gs-robot/cmd/start_task_queue")
    Call<Status> startTaskQueue(@Body RobotTaskQueue queue);

    /**
     * Stop task queue call.
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_task_queue")
    Call<Status> stopTaskQueue();

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/pause_task_queue")
    Call<Status> pauseTaskQueue();

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/resume_task_queue")
    Call<Status> resumeTaskQueue();

    /**
     * ??????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_current_task")
    Call<Status> stopCurrentTask();

    /**
     * ????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_task_queue_finished")
    Call<Status> isTaskQueueFinished();

    /**
     * ????????????
     *
     * @param move the robot move
     * @return the call
     */
    @POST("/gs-robot/cmd/move")
    Observable<Status> robotMove(@Body RobotMove move);

    /**
     * ??????????????????????????????
     *
     * @param distance the distance
     * @param speed    the speed
     * @return the call
     */
    @GET("/gs-robot/cmd/move_to")
    Call<Status> robotMoveTo(@Query("distance") float distance, @Query("speed") float speed);

    /**
     * ??????????????????????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_move_to_finished")
    Call<Status> isMoveToFinished();

    /**
     * ????????????????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_move_to")
    Call<Status> stopMoveTo();

    /**
     * ??????????????????
     *
     * @param rotate the robot rotate
     * @return the call
     */
    @POST("/gs-robot/cmd/rotate")
    Call<Status> rotate(@Body RobotRotate rotate);

    /**
     * ??????????????????????????????
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_rotate_finished")
    Call<Status> isRotateFinished();

    /**
     * ????????????????????????.
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_rotate")
    Call<Status> stopRotate();


    /**
     * ?????????????????????
     *
     * @param errorId the error id
     * @return the call
     */
    @GET("/gs-robot/cmd/clear_mcu_error")
    Call<Status> clearMcuError(@Query("error_id") int errorId);

    /**
     * Power off call.
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/power_off")
    Call<Status> powerOff();


    /**
     * ????????????
     */
    @GET("/gs-robot/cmd/ping")
    Call<Status> ping();


//    http://10.7.6.88:8000/gs-robot/cmd/report_charge_status?status=

    @Headers({"url_name:charge"})
    @GET("/gs-robot/cmd/report_charge_status")
    Call<ChargeStatus> reportChargeStatus(@Query("status") String status);

    /**
     * ???????????????
     * */
//    @GET("/gs-robot/data/virtual_obstacles")
//    Call<VirtualObstacleBean> getVirtualObstacleData(@Query("map_name") String paramString);

    /**
     * ???????????????
     * */

    @GET("/gs-robot/data/virtual_obstacles")
    Call<VirtualObstacleBean> getVirtualObstacleData(@Query("map_name") String paramString);

    @GET("/gs-robot/real_time_data/node_status")
    Call<RecordStatusBean> getRecordStatus();

    /**
     * ???????????????
     * */
    @POST("/gs-robot/cmd/update_virtual_obstacles")
    Call<Status> updateVirtualObstacleData(@Body UpdataVirtualObstacleBean updataVirtualObstacleBean, @Query("map_name") String paramString, @Query("obstacle_name") String obstacle_name);

    /**
     * ????????????
     * */
    @GET("/gs-robot/cmd/set_speed_level")
    Call<Status> setSpeedLevel(@Query("level") String paramString);

    /**
     * ????????????
     * */
    @GET("/gs-robot/cmd/set_navigation_speed_level")
    Call<Status> setnavigationLevel(@Query("level") String paramString);

    /**
     * ??????????????????
     * */
    @GET("/gs-robot/cmd/reset_robot_setting")
    Call<Status> reset_robot();

    /**
     * ???????????????????????????
     */
    @GET("/gs-robot/real_time_data/ultrasonic_phit")
    Call<UltrasonicPhitBean> getUltrasonicPhit();
    /**
     * ?????????????????????
     */
    @POST("/gs-robot/cmd/modify_robot_param")
    Call<Status> modifyRobotParam(@Body ModifyRobotParam.RobotParam[] modifyRobotParam);

    /**
     * ?????????????????????
     */
    @GET("/gs-robot/cmd/reboot")
    Call<Status> reboot();


    /*
    * ????????????bag
    * */
    @POST("/gs-robot/cmd/op")
    Call<Status> recording(@Body RecordingBean recordingBean);

    /*
     * ????????????bag
     * */
    @GET ("/GAUSSIAN_RUNTIME_DIR/bag/{bagName}")
    Call<ResponseBody> getBag(@Path("bagName") String bagName);

    /*
     * ????????????bag
     * */
    @GET("/gs-robot/cmd/delete_runtime_file")
    Call<Status> deleteBag(@Query("file_path") String bagName);

}