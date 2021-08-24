package com.uslam.factory;

import com.dcm360.controller.gs.controller.ResponseCallback;
import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.RecordingBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ModifyRobotParam;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotWorkStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.VersionBean;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotEditMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.dcm360.controller.gs.controller.bean.vel_bean.RobotCmdVel;
import com.dcm360.controller.gs.controller.interceptor.GsMoreBaseUrlInterceptor;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.uslam.bean.MapListBean;
import com.uslam.bean.MapPngBean;
import com.uslam.bean.MoveBean;
import com.uslam.bean.TargetPointBean;
import com.uslam.service.UsLamControllerService;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsLamFactory extends WorkFactory {

    private UsLamControllerService usLamControllerService = null;

    public UsLamFactory(String baseUrl) {
        initialize(baseUrl);
    }

    public void initialize(String baseUrl) {
        if (usLamControllerService == null) {
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

            usLamControllerService = retrofit.create(UsLamControllerService.class);
        }
    }

    @Override
    public void initialize(String map_name, String init_point_name, float x, float y, float theta, int type, RobotStatus<Status> status) {
        super.initialize(map_name, init_point_name, x, y, theta, type, status);
        if (usLamControllerService != null) {
            usLamControllerService.relocalization(x, y, theta).enqueue(new ResponseCallback<Status>().call(status));
        }
    }



    @Override
    public void connect_robot(String url, String uuid) {
        super.connect_robot(url, uuid);
        if (usLamControllerService != null) {
            try {
                usLamControllerService.robot_connect(uuid).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void navigate_Position(String map_name, String position_name, TargetPointBean targetPointBean, RobotStatus<Status> status) {
        super.navigate_Position(map_name, position_name, targetPointBean, status);
        if (usLamControllerService != null)
            usLamControllerService.navigation(targetPointBean).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void cmdVel(RobotStatus<RobotCmdVel> status) {
        super.cmdVel(status);

    }

    @Override
    public void deviceStatus(RobotStatus<RobotDeviceStatus> status) {
        super.deviceStatus(status);

    }

    @Override
    public void scanMapPng(RobotStatus<byte[]> status) {
        super.scanMapPng(status);

    }

    @Override
    public void cancelScanMap(String mapName, boolean saveMap, boolean save_map_force, RobotStatus<Status> status) {
        super.cancelScanMap(mapName, saveMap, save_map_force, status);
        if (usLamControllerService != null)
            usLamControllerService.stop_scan_map(mapName, saveMap, save_map_force).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void ping(RobotStatus<Status> status) {
        super.ping(status);
        //  if (usLamControllerService != null)
        // usLamControllerService.(mapName, saveMap, save_map_force).enqueue(new ResponseCallback<Status>().call(status));

    }

    @Override
    public void use_map(String mapName, RobotStatus<Status> status) {
        super.use_map(mapName, status);
//        if (usLamControllerService != null)
//            usLamControllerService.stop_scan_map(mapName, saveMap, save_map_force).enqueue(new ResponseCallback<Status>().call(status));

    }

    @Override
    public void getMapPositions(String mapName, RobotStatus<RobotPositions> status) {
        super.getMapPositions(mapName, status);
        if (usLamControllerService != null)
            usLamControllerService.get_all_points().enqueue(new ResponseCallback<RobotPositions>().call(status));

    }

    @Override
    public void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status) {
        super.getVirtualObstacleData(mapName, status);
    }

    @Override
    public void getRecordStatus(RobotStatus<RecordStatusBean> status) {
        super.getRecordStatus(status);

    }

    @Override
    public void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status) {
        super.updateVirtualObstacleData(updataVirtualObstacleBean, mapName, obstacle_name, status);

    }

    @Override
    public void setSpeedLevel(String level, RobotStatus<Status> status) {
        super.setSpeedLevel(level, status);

    }

    @Override
    public void reset_robot(RobotStatus<Status> status) {
        super.reset_robot(status);

    }

    @Override
    public void getUltrasonicPhit(RobotStatus<UltrasonicPhitBean> status) {
        super.getUltrasonicPhit(status);

    }

    @Override
    public void deviceRobotVersion(RobotStatus<VersionBean> status) {
        super.deviceRobotVersion(status);

    }

    @Override
    public void modifyRobotParam(ModifyRobotParam.RobotParam[] modifyRobotParam, RobotStatus<Status> status) {
        super.modifyRobotParam(modifyRobotParam, status);

    }

    @Override
    public void reboot(RobotStatus<Status> status) {
        super.reboot(status);

    }

    @Override
    public void recording(RecordingBean recordingBean, RobotStatus<Status> status) {
        super.recording(recordingBean, status);

    }

    @Override
    public void getBag(String bagName, RobotStatus<byte[]> status) {
        super.getBag(bagName, status);

    }

    @Override
    public void deleteBag(String bagName, RobotStatus<Status> status) {
        super.deleteBag(bagName, status);

    }

    @Override
    public void setnavigationSpeedLevel(String level, RobotStatus<Status> status) {
        super.setnavigationSpeedLevel(level, status);

    }

    @Override
    public void move(MoveBean moveBean, RobotStatus<Status> status) {
        super.move(moveBean, status);
        if (usLamControllerService != null)
            usLamControllerService.robot_move(moveBean).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void startScanMap(String mapName, int type, RobotStatus<Status> status) {
        super.startScanMap(mapName, type, status);
        if (usLamControllerService != null)
            usLamControllerService.start_scan_map().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stopScanMap(String mapName, boolean saveMap, boolean save_map_force, RobotStatus<Status> status) {
        super.stopScanMap(mapName, saveMap, save_map_force, status);
        if (usLamControllerService != null)
            usLamControllerService.stop_scan_map(mapName, saveMap, save_map_force).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void getMapPng(MapPngBean mapPngBean, RobotStatus<byte[]> status) {
        super.getMapPng(mapPngBean, status);
        if (usLamControllerService != null)
            usLamControllerService.get_map(mapPngBean.getMapName(), mapPngBean.isPng_map(),mapPngBean.isUmap()).enqueue(new ResponseCallback<byte[]>().call(status));
    }

    @Override
    public void deleteMap(String mapName, RobotStatus<Status> status) {
        super.deleteMap(mapName, status);
        if (usLamControllerService != null)
            usLamControllerService.delete_map(mapName).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void loadMapList(final RobotStatus<RobotMap> mapList) {
        super.loadMapList(mapList);
        if (usLamControllerService != null)
            usLamControllerService.getMapList().enqueue(new ResponseCallback<MapListBean>().call(new RobotStatus<MapListBean>() {
                @Override
                public void success(MapListBean status) {
                    RobotMap robotMap = new RobotMap();
                    //塞数据
                    mapList.success(robotMap);
                }

                @Override
                public void error(Throwable error) {
                    mapList.error(error);
                }
            }));

    }

    @Override
    public void updateMap(String originMapName, String newMapName, JSONObject umap, RobotStatus<Status> status) {
        super.updateMap(originMapName, newMapName, umap, status);
        if (usLamControllerService != null)
            usLamControllerService.update_map(originMapName, umap).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public Response<ResponseBody> downloadMap(String mapName) {
        try {
            return usLamControllerService.export_mapZip(mapName).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void uploadMap(String mapName, String mapPath, RobotStatus<Status> status) {
        super.uploadMap(mapName, mapPath, status);
        if (usLamControllerService != null) {
            File file = new File(mapPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
            usLamControllerService.import_mapZip(true, requestFile).enqueue(new ResponseCallback<Status>().call(status));
        }
    }

    @Override
    public void add_Position(PositionListBean positionListBean, RobotStatus<Status> status) {
        super.add_Position(positionListBean, status);
        if (usLamControllerService != null)
            usLamControllerService.add_point(positionListBean).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void deletePosition(String mapName, String point_name, PositionListBean positionListBean, RobotStatus<Status> status) {
        super.deletePosition(mapName, point_name, positionListBean, status);
        if (usLamControllerService != null)
            usLamControllerService.delete_point(positionListBean).enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void editPosition(String mapName, String originName, String newName, RobotStatus<Status> status) {
        super.editPosition(mapName, originName, newName, status);
    }

    @Override
    public void getPositions(String mapName, RobotStatus<RobotPosition> status) {
        super.getPositions(mapName, status);
        
    }

    @Override
    public void cancelNavigate(RobotStatus<Status> status) {
        super.cancelNavigate(status);
        if (usLamControllerService != null)
            usLamControllerService.stop_navigation().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void is_initialize_finished(RobotStatus<Status> status) {
        super.is_initialize_finished(status);
        if (usLamControllerService != null)
            usLamControllerService.search_relocalization().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void stop_initialize(RobotStatus<Status> status) {
        super.stop_initialize(status);
        if (usLamControllerService != null)
            usLamControllerService.stop_relocalization().enqueue(new ResponseCallback<Status>().call(status));
    }

    @Override
    public void work_status(RobotStatus<RobotWorkStatus> status) {
        super.work_status(status);
        if (usLamControllerService != null)
            usLamControllerService.get_scan_map().enqueue(new ResponseCallback<RobotWorkStatus>().call(status));
    }
}
