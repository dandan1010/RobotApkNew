package com.dcm360.controller.gs.controller.service;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ChargeStatus;
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
     * 激光设备数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/laser_raw")
    Call<RobotLaserRaw> laserRaw();

    /**
     * 激光设备栅格化数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/laser_phit")
    Call<RobotLaserPhit> laserPhit();

    /**
     * 里程计设备数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/odom_raw")
    Call<RobotOdomRaw> odomRaw();

    /**
     * gps设备数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/gps_raw")
    Call<RobotGpsRaw> gpsRaw();

    /**
     * 同步gps数据
     *
     * @param gpsData the sync gps data
     * @return the call
     */
    @POST("/gs-robot/cmd/sync_gps_data")
    Call<Status> syncGpsData(@Body RobotSyncGpsData gpsData);

    /**
     * 碰撞开关设备数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/protector")
    Call<RobotProtector> protector();

    /**
     * 移动障碍物检测数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/mobile_data")
    Call<RobotMobileData> mobileData();

    /**
     * 非地图障碍物检测数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/non_map_data")
    Call<RobotNonMapData> nonMapData();

    /**
     * 实时角速度和线速度数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/cmd_vel")
    Call<RobotCmdVel> cmdVel();

    /**
     * 设备状态数据
     *
     * @return the call
     */
    @GET("/gs-robot/data/device_status")
    Call<RobotDeviceStatus> deviceStatus();

    /**
     * 设备版本数据
     *
     * @return the call
     */
    @GET("/gs-robot/info")
    Call<VersionBean> deviceRobotVersion();

    /**
     * 机器人外观形状数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/footprint")
    Call<RobotFootprint> footprint();

    /**
     * 地图点数据 http://10.7.5.88:8080/gs-robot/data/positions?map_name=new2&type=2
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
     * 记录点
     *
     * @param position_name the position name
     * @param type          the type
     * @return the call
     */
    @GET("/gs-robot/cmd/add_position")
    Call<Status> addPosition(@Query("position_name") String position_name, @Query("type") int type);

    /**
     * 添加点
     */
    @POST("/gs-robot/cmd/position/add_position")
    Call<Status> add_Position(@Body PositionListBean paramPositionListBean);

    /**
     * 删除点
     *
     * @param map_name      the map name
     * @param position_name the position name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_position")
    Call<Status> deletePosition(@Query("map_name") String map_name, @Query("position_name") String position_name);

    /**
     * 重命名点
     *
     * @param map_name    the map name
     * @param origin_name the origin name
     * @param new_name    the new name
     * @return the call
     */
    @GET("/gs-robot/cmd/rename_position")
    Call<Status> renamePosition(@Query("map_name") String map_name, @Query("origin_name") String origin_name, @Query("new_name") String new_name);

    /**
     * 开始扫描地图
     *
     * @param map_name the map name
     * @param type     the type
     * @return the call
     */
    @GET("/gs-robot/cmd/start_scan_map")
    Call<Status> startScanMap(@Query("map_name") String map_name, @Query("type") int type);

    /**
     * 结束扫描并保存地图(同步)
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_scan_map")
    Call<Status> stopScanMap();

    /**
     * 取消扫描不保存地图
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_scan_map")
    Call<Status> cancelScanMap();

    /**
     * 结束扫描保存地图(异步) //推荐使用
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/async_stop_scan_map")
    Call<Status> asyncStopScanMap();

    /**
     * 异步结束扫地图是否完成
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_stop_scan_finished")
    Call<Status> isScanFinished();

    /**
     * 获取实时扫地图图片png
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
     * 获取地图图片png
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/data/map_png")
    Call<ResponseBody> mapPng(@Query("map_name") String map_name);

    /**
     * 获取地图列表
     *
     * @return the maps
     */
    @GET("/gs-robot/data/maps")
    Observable<RobotMap> getMapList();

    /**
     * 删除地图
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_map")
    Call<Status> deleteMap(@Query("map_name") String map_name);

    /**
     * 修改地图名称
     *
     * @param origin_map_name the origin map name
     * @param new_map_name    the new map name
     * @return the call
     */
    @GET("/gs-robot/cmd/rename_map")
    Call<Status> renameMap(@Query("origin_map_name") String origin_map_name, @Query("new_map_name") String new_map_name);

    /**
     * 下载地图
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/data/download_map")
    Call<ResponseBody> downloadMap(@Query("map_name") String map_name);

    /**
     * 上传地图
     *
     * @param map_name the map name
     * @param file     the file
     * @return the call
     */
    //@Multipart
    @POST("/gs-robot/data/upload_map")
    Call<Status> uploadMap(@Query("map_name") String map_name, @Body RequestBody file);

    /**
     * 编辑地图
     *
     * @param mapName       the map name
     * @param operationType the operation type
     * @param editMap       the robot edit map
     * @return the call
     */
    @POST(" /gs-robot/cmd/edit_map")
    Call<Status> editMap(@Query("map_name") String mapName, @Query("operation_type") String operationType, @Body RobotEditMap editMap);

    /**
     * 加载地图
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/cmd/load_map")
    Call<Status> loadMap(@Query("map_name") String map_name);

    /**
     * 不转圈初始化
     *
     * @param map_name        the map name
     * @param init_point_name the init point name
     * @return the call
     */
    @GET("/gs-robot/cmd/initialize_directly")
    Call<Status> initDirectly(@Query("map_name") String map_name, @Query("init_point_name") String init_point_name);

    /**
     * 转圈初始化
     *
     * @param map_name        the map name
     * @param init_point_name the init point name
     * @return the call
     */
    @GET("/gs-robot/cmd/initialize")
    Call<Status> initRobot(@Query("map_name") String map_name, @Query("init_point_name") String init_point_name);

    /**
     * 自定义初始化
     *
     * @param robotInitCustom the robot initialize customized
     * @return the call
     */
    @POST("/gs-robot/cmd/initialize_customized")
    Call<Status> initCustom(@Body RobotInitCustom robotInitCustom);

    /**
     * 自定义不转圈初始化
     *
     * @param robotInitCustom the robot initialize customized
     * @return the call
     */
    @POST("/gs-robot/cmd/initialize_customized_directly")
    Call<Status> initCustomDirectly(@Body RobotInitCustom robotInitCustom);

    /**
     * 全地图初始化
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/cmd/initialize_global")
    Call<Status> initGlobal(@Query("map_name") String map_name);

    /**
     * 停止初始化
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_initialize")
    Call<Status> stopInit();

    /**
     * 检查是否初始化完成
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
     * 获取初始化点列表
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/position")
    Call<RobotPosition> position();

    /**
     * 机器人在地图的GPS数据
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/gps")
    Call<RobotMapGPS> gps();

    /**
     * 导航到导航点
     *
     * @param mapName       the map name
     * @param position_name the position name
     * @return the call
     */
    @GET("/gs-robot/cmd/position/navigate")
    Call<Status> navigate(@Query("map_name") String mapName, @Query("position_name") String position_name);

    /**
     * 导航到任意指定坐标点
     *
     * @param navigatePosition the robot navigate position
     * @return the call
     */
    @POST("/gs-robot/cmd/quick/navigate")
    Call<Status> navigate(@Body RobotNavigatePosition navigatePosition);

    /**
     * 暂停导航
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/pause_navigate")
    Call<Status> pauseNavigate();

    /**
     * 恢复导航
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/resume_navigate")
    Call<Status> resumeNavigate();

    /**
     * 取消导航
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_navigate")
    Call<Status> cancelNavigate();

    /**
     * 导航实时路线
     *
     * @return the call
     */
    @GET("/gs-robot/real_time_data/navigation_path")
    Call<RobotNavigationPath> navigationPath();

    /**
     * 任意两点得到导航路线
     *
     * @param path the robot navigation to path
     * @return the call
     */
    @POST("/gs-robot/data/navigation_path")
    Call<RobotNavigationPath> navigationPath(@Body RobotNavigationToPath path);

    /**
     * 获取路径
     *
     * @param map_name the map name
     * @return the call
     */
    @GET("/gs-robot/data/paths")
    Call<RobotPath> getPath(@Query("map_name") String map_name);

    /**
     * 删除路径
     *
     * @param map_name  the map name
     * @param path_name the path name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_path")
    Call<Status> deletePath(@Query("map_name") String map_name, @Query("path_name") String path_name);

    /**
     * 重命名路径
     *
     * @param map_name         the map name
     * @param origin_path_name the origin path name
     * @param new_path_name    the new path name
     * @return the call
     */
    @GET("/gs-robot/cmd/rename_path")
    Call<Status> renamePath(@Query("map_name") String map_name, @Query("origin_path_name") String origin_path_name, @Query("new_path_name") String new_path_name);

    /**
     * 开始录制路径
     *
     * @param map_name  the map name
     * @param path_name the path name
     * @return the call
     */
    @GET("/gs-robot/cmd/start_record_path")
    Call<Status> startRecordPath(@Query("map_name") String map_name, @Query("path_name") String path_name);

    /**
     * 停止并保存路径
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_record_path")
    Call<Status> stopRecordPath();

    /**
     * 取消录制路径
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_record_path")
    Call<Status> cancelRecordPath();

    /**
     * 开始录制区域路径
     *
     * @param mapName   the map name
     * @param path_name the path name
     * @return the call
     */
    @GET("/gs-robot/cmd/start_record_area")
    Call<Status> startRecordArea(@Query("map_name") String mapName, @Query("path_name") String path_name);

    /**
     * 停止并保存路径区域
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_record_area")
    Call<Status> stopRecordArea();

    /**
     * 取消录制路径区域
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/cancel_record_area")
    Call<Status> cancelRecordArea();

    /**
     * 添加路径动作点
     *
     * @param action the robot add path action
     * @return the call
     */
    @POST("/gs-robot/cmd/add_path_action")
    Call<Status> addPathAction(@Body RobotAddPathAction action);

    /**
     * 路径的详细数据
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
     * 删除任务队列
     *
     * @param mapName         the map name
     * @param task_queue_name the task queue name
     * @return the call
     */
    @GET("/gs-robot/cmd/delete_task_queue")
    Call<Status> deleteTaskQueue(@Query("map_name") String mapName, @Query("task_queue_name") String task_queue_name);

    /**
     * 开始执行任务队列
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
     * 暂停队列任务
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/pause_task_queue")
    Call<Status> pauseTaskQueue();

    /**
     * 恢复队列任务
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/resume_task_queue")
    Call<Status> resumeTaskQueue();

    /**
     * 停止当前任务
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_current_task")
    Call<Status> stopCurrentTask();

    /**
     * 队列任务是否完成
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_task_queue_finished")
    Call<Status> isTaskQueueFinished();

    /**
     * 移动控制
     *
     * @param move the robot move
     * @return the call
     */
    @POST("/gs-robot/cmd/move")
    Observable<Status> robotMove(@Body RobotMove move);

    /**
     * 定距离定速度移动控制
     *
     * @param distance the distance
     * @param speed    the speed
     * @return the call
     */
    @GET("/gs-robot/cmd/move_to")
    Call<Status> robotMoveTo(@Query("distance") float distance, @Query("speed") float speed);

    /**
     * 定距离定速度移动控制是否结束
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_move_to_finished")
    Call<Status> isMoveToFinished();

    /**
     * 停止定距离定速度移动控制
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_move_to")
    Call<Status> stopMoveTo();

    /**
     * 转动固定角度
     *
     * @param rotate the robot rotate
     * @return the call
     */
    @POST("/gs-robot/cmd/rotate")
    Call<Status> rotate(@Body RobotRotate rotate);

    /**
     * 转动固定角度是否结束
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/is_rotate_finished")
    Call<Status> isRotateFinished();

    /**
     * 停止转动固定角度.
     *
     * @return the call
     */
    @GET("/gs-robot/cmd/stop_rotate")
    Call<Status> stopRotate();


    /**
     * 清除驱动器错误
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
     * 心跳机制
     */
    @GET("/gs-robot/cmd/ping")
    Call<Status> ping();


//    http://10.7.6.88:8000/gs-robot/cmd/report_charge_status?status=

    @Headers({"url_name:charge"})
    @GET("/gs-robot/cmd/report_charge_status")
    Call<ChargeStatus> reportChargeStatus(@Query("status") String status);

    /**
     * 添加虚拟墙
     * */
//    @GET("/gs-robot/data/virtual_obstacles")
//    Call<VirtualObstacleBean> getVirtualObstacleData(@Query("map_name") String paramString);

    /**
     * 获取虚拟墙
     * */

    @GET("/gs-robot/data/virtual_obstacles")
    Call<VirtualObstacleBean> getVirtualObstacleData(@Query("map_name") String paramString);

    @GET("/gs-robot/real_time_data/node_status")
    Call<RecordStatusBean> getRecordStatus();

    /**
     * 添加虚拟墙
     * */
    @POST("/gs-robot/cmd/update_virtual_obstacles")
    Call<Status> updateVirtualObstacleData(@Body UpdataVirtualObstacleBean updataVirtualObstacleBean, @Query("map_name") String paramString, @Query("obstacle_name") String obstacle_name);

    /**
     * 跑线速度
     * */
    @GET("/gs-robot/cmd/set_speed_level")
    Call<Status> setSpeedLevel(@Query("level") String paramString);

    /**
     * 导航速度
     * */
    @GET("/gs-robot/cmd/set_navigation_speed_level")
    Call<Status> setnavigationLevel(@Query("level") String paramString);

    /**
     * 恢复出厂设置
     * */
    @GET("/gs-robot/cmd/reset_robot_setting")
    Call<Status> reset_robot();

    /**
     * 声呐设备栅格化数据
     */
    @GET("/gs-robot/real_time_data/ultrasonic_phit")
    Call<UltrasonicPhitBean> getUltrasonicPhit();

}