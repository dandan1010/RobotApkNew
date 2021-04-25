package com.dcm360.controller.gs.controller;

import android.annotation.SuppressLint;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
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
import com.dcm360.controller.gs.controller.interceptor.GsMoreBaseUrlInterceptor;
import com.dcm360.controller.gs.controller.listener.StatusMessageListener;
import com.dcm360.controller.gs.controller.service.GsControllerService;
import com.dcm360.controller.gs.controller.service.GsControllerWebSocket;
import com.dcm360.controller.gs.controller.service.StatusWebSocketListener;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author LiYan
 * @create 2019/4/28
 * @Describe
 */
public enum GsController implements IGsRobotController {
    INSTANCE;

    public static String TAG = "gs_retrofit_log";

    private GsControllerService gsControllerService = null;

    private LinkedHashMap<String, StatusMessageListener> listeners;

    GsController() {
        listeners = new LinkedHashMap<>();
    }

    public GsControllerService getGsControllerService() {
        return gsControllerService;
    }

    @Override
    public boolean isHasWebSocket(String url) {
        return listeners.containsKey(url);
    }

    @Override
    public void initialize(String baseUrl) {
        if (gsControllerService == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .addInterceptor(new GsMoreBaseUrlInterceptor())
                    .build();

            Retrofit retrofit =
                    new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client).build();

            gsControllerService = retrofit.create(GsControllerService.class);
        }
    }

    @Override
    public void laserRaw(RobotStatus<RobotLaserRaw> status) {
        if (gsControllerService != null)
            gsControllerService.laserRaw().enqueue(new ResponseCallback<RobotLaserRaw>().call(status));
    }

    @Override
    public Call<RobotLaserPhit> laserPhit() {
        if (gsControllerService != null)
            return gsControllerService.laserPhit();
        return null;
    }

    @Override
    public void odomRaw(RobotStatus<RobotOdomRaw> status) {
        if (gsControllerService != null)
            gsControllerService.odomRaw().enqueue(new ResponseCallback<RobotOdomRaw>().call(status));
    }

    @Override
    public void gpsRaw(RobotStatus<RobotGpsRaw> status) {
        if (gsControllerService != null)
            gsControllerService.gpsRaw().enqueue(new ResponseCallback<RobotGpsRaw>().call(status));
    }

    @Override
    public void syncGpsData(RobotSyncGpsData data, final RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.syncGpsData(data).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void protector(RobotStatus<RobotProtector> status) {
        if (gsControllerService != null)
            gsControllerService.protector().enqueue(new ResponseCallback<RobotProtector>().call(status));
    }

    @Override
    public void mobileData(RobotStatus<RobotMobileData> status) {
        if (gsControllerService != null)
            gsControllerService.mobileData().enqueue(new ResponseCallback<RobotMobileData>().call(status));
    }

    @Override
    public void nonMapData(RobotStatus<RobotNonMapData> status) {
        if (gsControllerService != null)
            gsControllerService.nonMapData().enqueue(new ResponseCallback<RobotNonMapData>().call(status));
    }

    @Override
    public void cmdVel(RobotStatus<RobotCmdVel> status) {
        if (gsControllerService != null)
            gsControllerService.cmdVel().enqueue(new ResponseCallback<RobotCmdVel>().call(status));
    }

    @Override
    public void deviceStatus(RobotStatus<RobotDeviceStatus> status) {
        if (gsControllerService != null)
            gsControllerService.deviceStatus().enqueue(new ResponseCallback<RobotDeviceStatus>().call(status));
    }

    @Override
    public void deviceRobotVersion(RobotStatus<VersionBean> status) {
        if (gsControllerService != null)
            gsControllerService.deviceRobotVersion().enqueue(new ResponseCallback<VersionBean>().call(status));
    }

    @Override
    public void footprint(RobotStatus<RobotFootprint> status) {
        if (gsControllerService != null)
            gsControllerService.footprint().enqueue(new ResponseCallback<RobotFootprint>().call(status));
    }

    @SuppressLint("CheckResult")
    @Override
    public void getPosition(String mapName, int type, final RobotStatus<RobotPositions> status) {
        if (gsControllerService != null) {
            gsControllerService.positions(mapName, type).subscribeOn(Schedulers.io()).subscribe(new Consumer<RobotPositions>() {
                @Override
                public void accept(RobotPositions robotPositions) throws Exception {
                    if (status != null)
                        status.success(robotPositions);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    if (status != null)
                        status.error(throwable);
                }
            });
        }
    }

    @Override
    public void getMapPositions(String mapName, final RobotStatus<RobotPositions> status) {
        if (gsControllerService != null) {
            gsControllerService.getMapPositions(mapName).subscribeOn(Schedulers.io()).subscribe(new Consumer<RobotPositions>() {
                @Override
                public void accept(RobotPositions robotPositions) throws Exception {
                    if (status != null)
                        status.success(robotPositions);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    if (status != null)
                        status.error(throwable);
                }
            });
        }
    }

    @Override
    public void addPosition(String positionName, int type, final RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.addPosition(positionName, type).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void add_Position(PositionListBean positionListBean, final RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.add_Position(positionListBean).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void deletePosition(String mapName, String positionName, final RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.deletePosition(mapName, positionName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void renamePosition(String mapName, String originName, String newName, final RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.renamePosition(mapName, originName, newName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void startScanMap(String mapName, int type, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.startScanMap(mapName, type).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stopScanMap(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.stopScanMap().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void cancelScanMap(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.cancelScanMap().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void asyncStopScanMap(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.asyncStopScanMap().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void isStopScanFinished(RobotStatus<Status> status) {
    }

    @Override
    public void scanMapPng(RobotStatus<ResponseBody> status) {
        if (gsControllerService != null)
            gsControllerService.scanMapPng().enqueue(new ResponseCallback<ResponseBody>().call(status));
    }

    @Override
    public void getMapPng(String mapName, RobotStatus<ResponseBody> status) {
        if (gsControllerService != null)
            gsControllerService.mapPng(mapName).enqueue(new ResponseCallback<ResponseBody>().call(status));
    }

    @Override
    public void deleteMap(String mapName, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.deleteMap(mapName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public Status deleteMapSyn(String mapName) {
        if (gsControllerService != null)
            try {
                return gsControllerService.deleteMap(mapName).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    @Override
    public void renameMap(String originMapName, String newMapName, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.renameMap(originMapName, newMapName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public Response<ResponseBody> downloadMap(String mapName) {
        if (gsControllerService != null) {
            try {
                return gsControllerService.downloadMap(mapName).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void uploadMap(String mapName, String mapPath, RobotStatus<Status> status) {
        if (gsControllerService != null) {
            File file = new File(mapPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            gsControllerService.uploadMap(mapName, requestFile).enqueue(new ResponseCallback<Status>().call(status));
        }
    }

    @Override
    public Status uploadMapSyn(String mapName, String mapPath) {
        if (gsControllerService != null) {
            File file = new File(mapPath);
//        RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
//        MultipartBody.Part body =
//                MultipartBody.Part.create("file", file.getName(), requestFile);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/otcet-stream"), file);
            try {
                return gsControllerService.uploadMap(mapName, requestBody).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void editMap(String mapName, String operationType, RobotEditMap editMap, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.editMap(mapName, operationType, editMap).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void loadMap(String mapName, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.loadMap(mapName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void initDirectly(String mapName, String initPointName, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.initDirectly(mapName, initPointName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void initRobot(String mapName, String initPointName, RobotStatus<Status> status) {//转圈初始化
        if (gsControllerService != null)
            gsControllerService.initRobot(mapName, initPointName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void initCustom(RobotInitCustom robotInitCustom, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.initCustom(robotInitCustom).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void initCustomDirectly(RobotInitCustom robotInitCustom, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.initCustomDirectly(robotInitCustom).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void initGlobal(String mapName, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.initGlobal(mapName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stopInit(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.stopInit().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void isInitFinished(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.isInitFinished().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void getPositions(String mapName, RobotStatus<RobotPosition> status) {
        if (gsControllerService != null)
            gsControllerService.position().enqueue(new ResponseCallback<RobotPosition>().call(status));
    }

    @Override
    public void gps(RobotStatus<RobotMapGPS> status) {
        if (gsControllerService != null)
            gsControllerService.gps().enqueue(new ResponseCallback<RobotMapGPS>().call(status));
    }

    @Override
    public void navigate(String mapName, String positionName, final RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.navigate(mapName, positionName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void use_map(String mapName, final RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.use_map(mapName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void navigate(RobotNavigatePosition position, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.navigate(position).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void pauseNavigate(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.pauseNavigate().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void resumeNavigate(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.resumeNavigate().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void cancelNavigate(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.cancelNavigate().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void navigationPath(RobotStatus<RobotNavigationPath> status) {
        if (gsControllerService != null)
            gsControllerService.navigationPath().enqueue(new ResponseCallback<RobotNavigationPath>().call(status));
    }

    @Override
    public void navigationPath(RobotNavigationToPath navigation, RobotStatus<RobotNavigationPath> status) {
        if (gsControllerService != null)
            gsControllerService.navigationPath(navigation).enqueue(new ResponseCallback<RobotNavigationPath>().call(status));
    }

    @Override
    public void getPath(String mapName, RobotStatus<RobotPath> status) {
        if (gsControllerService != null)
            gsControllerService.getPath(mapName).enqueue(new ResponseCallback<RobotPath>().call(status));
    }

    @Override
    public void deletePath(String mapName, String pathName, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.deletePath(mapName, pathName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void saveTaskQueue(RobotTaskQueue taskQueue, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.saveTaskQueue(taskQueue).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void taskQueues(String mapName, RobotStatus<RobotTaskQueueList> status) {
        if (gsControllerService != null)
            gsControllerService.taskQueues(mapName).enqueue(new ResponseCallback<RobotTaskQueueList>().call(status));
    }

    @Override
    public void deleteTaskQueue(String mapName, String taskQueueName, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.deleteTaskQueue(mapName, taskQueueName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void startTaskQueue(RobotTaskQueue queue, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.startTaskQueue(queue).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stopTaskQueue(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.stopTaskQueue().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void pauseTaskQueue(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.pauseTaskQueue().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void resumeTaskQueue(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.resumeTaskQueue().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stopCurrentTask(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.stopCurrentTask().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void isTaskQueueFinished(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.isTaskQueueFinished().enqueue(new ResponseCallback<Status>().call(status));
    }

    @SuppressLint("CheckResult")
    @Override
    public void robotMove(RobotMove move, final RobotStatus<Status> status) {
        if (gsControllerService != null) {
            gsControllerService.robotMove(move).subscribeOn(Schedulers.io()).subscribe(new Consumer<Status>() {
                @Override
                public void accept(Status s) throws Exception {
                    if (status != null)
                        status.success(s);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    if (status != null) {
                        status.error(throwable);
                    }
                }
            });
        }
    }

    @Override
    public void robotMoveTo(float distance, float speed, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.robotMoveTo(distance, speed).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void isMoveToFinished(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.isMoveToFinished().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stopMoveTo(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.stopMoveTo().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void rotate(RobotRotate rotate, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.rotate(rotate).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void isRotateFinished(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.isRotateFinished().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stopRotate(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.stopRotate().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void clearMcuError(int errorId, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.clearMcuError(errorId).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void powerOff(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.powerOff().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void connectGSWebSocket(String url, StatusMessageListener listener) {
        if (gsControllerService != null) {
            listeners.put(url, listener);
            GsControllerWebSocket.getInstance().connect_gs(url, new StatusWebSocketListener(url, listener));
        }
    }

    @Override
    public void ping(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.ping().enqueue(new ResponseCallback<Status>().call(status));
}


    @Override
    public void reportChargeStatus(String state, RobotStatus<ChargeStatus> status) {
        if (gsControllerService != null)
            gsControllerService.reportChargeStatus(state).enqueue(new ResponseCallback<ChargeStatus>().call(status));
    }

    @Override
    public void getRecordStatus(RobotStatus<RecordStatusBean> status) {
        if (gsControllerService != null)
            gsControllerService.getRecordStatus().enqueue(new ResponseCallback<RecordStatusBean>().call(status));
    }

    @Override
    public void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status) {
        if (gsControllerService != null)
            gsControllerService.getVirtualObstacleData(mapName).enqueue(new ResponseCallback<VirtualObstacleBean>().call(status));
    }

    @Override
    public void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean,String mapName, String obstacle_name, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.updateVirtualObstacleData(updataVirtualObstacleBean, mapName, obstacle_name).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void setSpeedLevel(String level, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.setSpeedLevel(level).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void setnavigationSpeedLevel(String level, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.setnavigationLevel(level).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void reset_robot(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.reset_robot().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void getUltrasonicPhit(RobotStatus<UltrasonicPhitBean> status) {
        if (gsControllerService != null)
            gsControllerService.getUltrasonicPhit().enqueue(new ResponseCallback<UltrasonicPhitBean>().call(status));
    }

    @Override
    public void modifyRobotParam(ModifyRobotParam.RobotParam[] modifyRobotParam, RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.modifyRobotParam(modifyRobotParam).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void reboot(RobotStatus<Status> status) {
        if (gsControllerService != null)
            gsControllerService.reboot().enqueue(new ResponseCallback<Status>().call(status));
    }
}
