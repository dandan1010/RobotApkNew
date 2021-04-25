package com.dcm360.controller.gs;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dcm360.controller.RobotController;
import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.gs.controller.ResponseCallback;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ChargeStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.gps_bean.RobotMapGPS;
import com.dcm360.controller.gs.controller.bean.laser_bean.RobotLaserPhit;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.navigate_bean.RobotNavigatePosition;
import com.dcm360.controller.gs.controller.bean.navigate_bean.RobotNavigationPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.protector_bean.RobotProtector;
import com.dcm360.controller.gs.controller.bean.system_bean.RobotMove;
import com.dcm360.controller.gs.controller.bean.system_bean.RobotRotate;
import com.dcm360.controller.gs.controller.listener.StatusMessageListener;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class GSRobotController implements RobotController {
    private static final String TAG = GSRobotController.class.getSimpleName();

    @Override
    public void connect_robot(String url) {
        GsController.INSTANCE.initialize(url);
    }

    @Override
    public void move(float linearSpeed, float angularSpeed, RobotStatus<Status> status) {
        RobotMove robotMove = new RobotMove();
        robotMove.setSpeed(new RobotMove.SpeedBean(linearSpeed, angularSpeed));
        GsController.INSTANCE.robotMove(robotMove, status);
    }

    @Override
    public void move_to(float distance, float speed, RobotStatus<Status> status) {
        GsController.INSTANCE.robotMoveTo(distance, speed, status);
    }

    @Override
    public void stop_move_to(RobotStatus<Status> status) {
        GsController.INSTANCE.stopMoveTo(status);
    }

    @Override
    public void rotate(int rotateAngle, float rotateSpeed, RobotStatus<Status> status) {
        RobotRotate robotRotate = new RobotRotate();
        robotRotate.setRotateAngle(rotateAngle);
        robotRotate.setRotateSpeed(rotateSpeed);
        GsController.INSTANCE.rotate(robotRotate, status);
    }

    @Override
    public void stop_rotate(RobotStatus<Status> status) {
        GsController.INSTANCE.stopRotate(status);
    }

    @Override
    public void reportChargeStatus(String state, RobotStatus<ChargeStatus> status) {
        GsController.INSTANCE.reportChargeStatus(state, status);
    }

    @Override
    public void ping(RobotStatus<Status> status) {
        GsController.INSTANCE.ping(status);
    }

    @Override
    public void RobotStatus(final NavigationStatus navigationStatus, String... args) {
        if (args == null || args.length <= 0) {
            return;
        }

        if (!GsController.INSTANCE.isHasWebSocket(args[0])) {
            GsController.INSTANCE.connectGSWebSocket(args[0], new StatusMessageListener() {

                @Override
                public void onMessage(String data) {
                    if (TextUtils.isEmpty(data)) {
                        return;
                    }
                    JSONObject jsonObject = JSON.parseObject(data);
                    String noticeType = jsonObject.getString("noticeType");
                    String noticeDataFields = jsonObject.getString("noticeDataFields");
                    JSONObject objectData = jsonObject.getJSONObject("data");
                    JSONObject object = objectData.getJSONObject(noticeDataFields);
                    navigationStatus.noticeType(noticeType, object.getString("name"));
                }
            });
        }
        if (!GsController.INSTANCE.isHasWebSocket(args[1])) {
            GsController.INSTANCE.connectGSWebSocket(args[1], new StatusMessageListener() {

                @Override
                public void onMessage(String data) {
                    if (TextUtils.isEmpty(data)) {
                        return;
                    }
                    JSONObject jsonObject = JSON.parseObject(data);
                    int statusCode = jsonObject.getIntValue("statusCode");
                    String statusData = jsonObject.getString("statusData");
                    if (!TextUtils.isEmpty(statusData)) {
                        navigationStatus.statusCode(statusCode, statusData);
                    }
                }
            });
        }
    }

    @Override
    public void initialize_directly(String map_name, String init_point_name, RobotStatus<Status> status) {
        GsController.INSTANCE.initDirectly(map_name, init_point_name, status);
    }

    @Override
    public void initialize(String map_name, String init_point_name, RobotStatus<Status> status) {
        GsController.INSTANCE.initRobot(map_name, init_point_name, status);
    }

    @Override
    public void stop_initialize(RobotStatus<Status> status) {
        GsController.INSTANCE.stopInit(status);

    }

    @Override
    public void is_initialize_finished(RobotStatus<Status> status) {
        GsController.INSTANCE.isInitFinished(status);
    }

    @Override
    public void getMapPicture(String mapName, final RobotStatus<byte[]> png) {
        GsController.INSTANCE.getMapPng(mapName, new RobotStatus<ResponseBody>() {
            @Override
            public void success(ResponseBody responseBody) {
                try {
                    png.success(responseBody.bytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    png.error(e);
                }
            }

            @Override
            public void error(Throwable error) {
                png.error(error);
            }
        });
    }

    @Override
    public Response<ResponseBody> download_map(String mapName) {
        return GsController.INSTANCE.downloadMap(mapName);
    }

    @Override
    public void upload_map(String mapName, String path, RobotStatus<Status> status) {
        GsController.INSTANCE.uploadMap(mapName, path, status);
    }

    @Override
    public Status upload_map_syn(String mapName, String path) {
        return GsController.INSTANCE.uploadMapSyn(mapName, path);
    }

    @Override
    public Status delete_map_syn(String map_name) {
        return GsController.INSTANCE.deleteMapSyn(map_name);
    }


    @Override
    public void pause_navigate(RobotStatus<Status> status) {
        GsController.INSTANCE.pauseNavigate(status);
    }

    @Override
    public void resume_navigate(RobotStatus<Status> status) {
        GsController.INSTANCE.resumeNavigate(status);
    }

    @Override
    public void cancel_navigate(RobotStatus<Status> status) {
        GsController.INSTANCE.cancelNavigate(status);
    }

    @Override
    public void real_time_data(RobotStatus<Status> status) {
        GsController.INSTANCE.navigationPath(new RobotStatus<RobotNavigationPath>() {
            @Override
            public void success(RobotNavigationPath robotNavigationPath) {

            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    @Override
    public void gps(RobotStatus<RobotMapGPS> status) {
        GsController.INSTANCE.gps(status);
    }

    @Override
    public void navigate_Position(String map_name, String position_name, RobotStatus<Status> status) {
        GsController.INSTANCE.navigate(map_name, position_name, status);
    }

    @Override
    public void use_map(String map_name, RobotStatus<Status> status) {
        GsController.INSTANCE.use_map(map_name, status);
    }

    @Override
    public void navigatePosition(RobotNavigatePosition position, RobotStatus<Status> status) {
        GsController.INSTANCE.navigate(position, status);
    }

    @Override
    public void navigation_list(String map_name, final RobotStatus<List<String>> status) {
        final List<String> positions = new ArrayList<>();
        GsController.INSTANCE.getPosition(map_name, 2, new RobotStatus<RobotPositions>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void success(RobotPositions robotPositions) {
                robotPositions.getData().forEach(new Consumer<RobotPositions.DataBean>() {
                    @Override
                    public void accept(RobotPositions.DataBean dataBean) {
                        positions.add(dataBean.getName());
                    }
                });
                status.success(positions);
            }

            @Override
            public void error(Throwable error) {
                status.error(error);
            }
        });
    }

    @Override
    public void navigationList(String map_name, final RobotStatus<RobotPositions> robotStatus) {
        GsController.INSTANCE.getPosition(map_name, 2, new RobotStatus<RobotPositions>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void success(RobotPositions robotPositions) {
                robotStatus.success(robotPositions);
            }

            @Override
            public void error(Throwable error) {
                robotStatus.error(error);
            }
        });
    }

    @Override
    public void charge_Position(String map_name, final RobotStatus<String> pos) {
        GsController.INSTANCE.getPosition(map_name, 1, new RobotStatus<RobotPositions>() {
            @Override
            public void success(RobotPositions robotPositions) {
                if (robotPositions.getData() != null && robotPositions.getData().size() > 0) {
                    pos.success(robotPositions.getData().get(0).getName());
                } else {
                    pos.error(new Exception("该地图没有充电点"));
                }
            }

            @Override
            public void error(Throwable error) {
                pos.error(error);
            }
        });
    }

    @Override
    public void power_Off(RobotStatus<Status> status) {
        GsController.INSTANCE.powerOff(status);
    }

    @Override
    public void protector(final RobotStatus<Status> status) {
        GsController.INSTANCE.protector(new RobotStatus<RobotProtector>() {
            @Override
            public void success(RobotProtector robotProtector) {
                if (status == null) {
                    return;
                }
                Status success = new Status();
                success.setData(robotProtector.getData());
                success.setMsg("successed");
                status.success(success);
            }

            @Override
            public void error(Throwable error) {
                if (status == null) {
                    return;
                }
                status.error(error);
            }
        });
    }

    @Override
    public void isTaskQueueFinished(RobotStatus<Status> status) {
        GsController.INSTANCE.isTaskQueueFinished(status);
    }


    @Override
    public void getCurrentPosition(String mapName, RobotStatus<RobotPosition> position) {
        GsController.INSTANCE.getPositions(mapName, position);
    }

    @Override
    public Call<RobotLaserPhit> laserPhit() {
        return GsController.INSTANCE.laserPhit();
    }


    @Override
    public void start_taskQueue(RobotTaskQueue queue, RobotStatus<Status> status) {
        GsController.INSTANCE.startTaskQueue(queue, status);
    }

    @Override
    public void save_taskQueue(RobotTaskQueue taskQueue, RobotStatus<Status> status) {
        GsController.INSTANCE.saveTaskQueue(taskQueue, status);
    }

    @Override
    public void scanMapPng(final RobotStatus<byte[]> png) {
        GsController.INSTANCE.scanMapPng(new RobotStatus<ResponseBody>() {
            @Override
            public void success(ResponseBody responseBody) {
                try {
                    png.success(responseBody.bytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    png.error(e);
                }
            }

            @Override
            public void error(Throwable error) {
                png.error(error);
            }
        });
    }

    @Override
    public void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status) {
        GsController.INSTANCE.getVirtualObstacleData(mapName, status);
    }
    @Override
    public void getRecordStatus(RobotStatus<RecordStatusBean> status) {
        GsController.INSTANCE.getRecordStatus(status);
    }

    @Override
    public void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status) {
        GsController.INSTANCE.updateVirtualObstacleData(updataVirtualObstacleBean, mapName, obstacle_name, status);
    }

    @Override
    public void setSpeedLevel(String level, RobotStatus<Status> status) {
        GsController.INSTANCE.setSpeedLevel(level, status);
    }

    @Override
    public void setnavigationSpeedLevel(String level, RobotStatus<Status> status) {
        GsController.INSTANCE.setnavigationSpeedLevel(level, status);
    }

    @Override
    public void reboot(RobotStatus<Status> status){
        GsController.INSTANCE.reboot(status);
    }

}
