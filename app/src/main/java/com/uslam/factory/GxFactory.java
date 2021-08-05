package com.uslam.factory;


import com.dcm360.controller.gs.controller.GsController;
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
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.controller.RobotManagerController;
import com.uslam.bean.MapPngBean;
import com.uslam.bean.MoveBean;

import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class GxFactory extends WorkFactory {

    @Override
    public void initialize(String map_name, String init_point_name, float x, float y, float theta, int type, RobotStatus<Status> status) {
        super.initialize(map_name, init_point_name, x, y, theta, type, status);
        if (type == 1) {
            RobotManagerController.getInstance().getRobotController().initialize_directly(map_name, Content.InitializePositionName, status);
        } else if (type == 2) {
            RobotManagerController.getInstance().getRobotController().initialize(map_name, init_point_name, status);
        } else {
            GsController.INSTANCE.initGlobal(map_name, status);
        }
    }

    @Override
    public void connect_robot(String url) {
        super.connect_robot(url);
        RobotManagerController.getInstance().getRobotController().connect_robot(url);
    }

    @Override
    public void navigate_Position(String map_name, String position_name, RobotStatus<Status> status) {
        super.navigate_Position(map_name, position_name, status);
        RobotManagerController.getInstance().getRobotController().navigate_Position(map_name, position_name, status);
    }

    @Override
    public void charge_Position(String map_name, RobotStatus<String> status) {
        super.charge_Position(map_name, status);
    }

    @Override
    public void cmdVel(RobotStatus<RobotCmdVel> status) {
        super.cmdVel(status);
        GsController.INSTANCE.cmdVel(status);
    }

    @Override
    public void deviceStatus(RobotStatus<RobotDeviceStatus> status) {
        super.deviceStatus(status);
        GsController.INSTANCE.deviceStatus(status);
    }

    @Override
    public void scanMapPng(RobotStatus<byte[]> status) {
        super.scanMapPng(status);
        RobotManagerController.getInstance().getRobotController().scanMapPng(status);
    }

    @Override
    public void editMap(String mapName, String operationType, RobotEditMap editMap, RobotStatus<Status> status) {
        super.editMap(mapName, operationType, editMap, status);
    }

    @Override
    public void cancelScanMap(RobotStatus<Status> status) {
        super.cancelScanMap(status);
        GsController.INSTANCE.cancelScanMap(status);
    }

    @Override
    public void ping(RobotStatus<Status> status) {
        super.ping(status);
        RobotManagerController.getInstance().getRobotController().ping(status);
    }

    @Override
    public void use_map(String mapName, RobotStatus<Status> status) {
        super.use_map(mapName, status);
        RobotManagerController.getInstance().getRobotController().use_map(mapName, status);
    }

    @Override
    public void getMapPositions(String mapName, RobotStatus<RobotPositions> status) {
        super.getMapPositions(mapName, status);
        GsController.INSTANCE.getMapPositions(mapName, status);
    }

    @Override
    public void getVirtualObstacleData(String mapName, RobotStatus<VirtualObstacleBean> status) {
        super.getVirtualObstacleData(mapName, status);
        RobotManagerController.getInstance().getRobotController().getVirtualObstacleData(mapName, status);
    }

    @Override
    public void getRecordStatus(RobotStatus<RecordStatusBean> status) {
        super.getRecordStatus(status);
        RobotManagerController.getInstance().getRobotController().getRecordStatus(status);

    }

    @Override
    public void updateVirtualObstacleData(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name, RobotStatus<Status> status) {
        super.updateVirtualObstacleData(updataVirtualObstacleBean, mapName, obstacle_name, status);
        RobotManagerController.getInstance().getRobotController().updateVirtualObstacleData(updataVirtualObstacleBean, mapName, obstacle_name, status);

    }

    @Override
    public void setSpeedLevel(String level, RobotStatus<Status> status) {
        super.setSpeedLevel(level, status);
        RobotManagerController.getInstance().getRobotController().setSpeedLevel(level, status);
    }

    @Override
    public void reset_robot(RobotStatus<Status> status) {
        super.reset_robot(status);
        GsController.INSTANCE.reset_robot(status);
    }

    @Override
    public void getUltrasonicPhit(RobotStatus<UltrasonicPhitBean> status) {
        super.getUltrasonicPhit(status);
        GsController.INSTANCE.getUltrasonicPhit(status);
    }

    @Override
    public void deviceRobotVersion(RobotStatus<VersionBean> status) {
        super.deviceRobotVersion(status);
        GsController.INSTANCE.deviceRobotVersion(status);
    }

    @Override
    public void modifyRobotParam(ModifyRobotParam.RobotParam[] modifyRobotParam, RobotStatus<Status> status) {
        super.modifyRobotParam(modifyRobotParam, status);
        GsController.INSTANCE.modifyRobotParam(modifyRobotParam, status);
    }

    @Override
    public void reboot(RobotStatus<Status> status) {
        super.reboot(status);
        RobotManagerController.getInstance().getRobotController().reboot(status);
    }

    @Override
    public void recording(RecordingBean recordingBean, RobotStatus<Status> status) {
        super.recording(recordingBean, status);
        GsController.INSTANCE.recording(recordingBean, status);
    }

    @Override
    public void getBag(String bagName, RobotStatus<byte[]> status) {
        super.getBag(bagName, status);
        RobotManagerController.getInstance().getRobotController().getBag(bagName, status);
    }

    @Override
    public void deleteBag(String bagName, RobotStatus<Status> status) {
        super.deleteBag(bagName, status);
        GsController.INSTANCE.deleteBag(bagName, status);
    }

    @Override
    public void setnavigationSpeedLevel(String level, RobotStatus<Status> status) {
        super.setnavigationSpeedLevel(level, status);
        RobotManagerController.getInstance().getRobotController().setnavigationSpeedLevel(level, status);
    }

    @Override
    public void move(MoveBean moveBean, RobotStatus<Status> status) {
        super.move(moveBean, status);
        RobotManagerController.getInstance().getRobotController().move(moveBean.getLinearSpeed(), moveBean.getAngularSpeed(), status);
    }

    @Override
    public void startScanMap(String mapName, int type, RobotStatus<Status> status) {
        super.startScanMap(mapName, type, status);
        GsController.INSTANCE.startScanMap(mapName, type, status);
    }

    @Override
    public void stopScanMap(String mapName, boolean saveMap, boolean save_map_force, RobotStatus<Status> status) {
        super.stopScanMap(mapName, saveMap, save_map_force, status);
        GsController.INSTANCE.stopScanMap(status);
    }

    @Override
    public void getMapPng(MapPngBean mapPngBean, RobotStatus<byte[]> status) {
        super.getMapPng(mapPngBean, status);
        RobotManagerController.getInstance().getRobotController().getMapPicture(mapPngBean.getMapName(), status);
    }

    @Override
    public void deleteMap(String mapName, RobotStatus<Status> status) {
        super.deleteMap(mapName, status);
        GsController.INSTANCE.deleteMap(mapName, status);
    }

    @Override
    public RobotMap[] loadMapList() {
        final RobotMap[] robotMapList = {null};
        GsController.INSTANCE.getGsControllerService().getMapList()
                .filter(robotMap -> robotMap != null && robotMap.getData() != null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RobotMap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RobotMap robotMap) {
                        robotMapList[0] = robotMap;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return robotMapList;
    }

    @Override
    public void updateMap(String originMapName, String newMapName, JSONObject umap, RobotStatus<Status> status) {
        super.updateMap(originMapName, newMapName, umap, status);
    }

    @Override
    public Response<ResponseBody> downloadMap(String mapName) {
        return RobotManagerController.getInstance().getRobotController()
                .download_map(mapName);
    }

    @Override
    public void uploadMap(String mapName, String mapPath, RobotStatus<Status> status) {
        super.uploadMap(mapName, mapPath, status);
        GsController.INSTANCE.uploadMap(mapName, mapPath, status);
    }

    @Override
    public void add_Position(PositionListBean positionListBean, RobotStatus<Status> status) {
        super.add_Position(positionListBean, status);
        GsController.INSTANCE.add_Position(positionListBean, status);
    }

    @Override
    public void deletePosition(String mapName, String point_name, PositionListBean positionListBean, RobotStatus<Status> status) {
        super.deletePosition(mapName, point_name, positionListBean, status);
        GsController.INSTANCE.deletePosition(mapName, point_name, status);
    }

    @Override
    public void editPosition(String mapName, String originName, String newName, RobotStatus<Status> status) {
        super.editPosition(mapName, originName, newName, status);
        GsController.INSTANCE.renamePosition(mapName, originName, newName, status);
    }

    @Override
    public void getPositions(String mapName, RobotStatus<RobotPosition> status) {
        super.getPositions(mapName, status);
        GsController.INSTANCE.getPositions(mapName, status);
    }

    @Override
    public void cancelNavigate(RobotStatus<Status> status) {
        super.cancelNavigate(status);
        GsController.INSTANCE.cancelNavigate(status);
    }

    @Override
    public void is_initialize_finished(RobotStatus<Status> status) {
        super.is_initialize_finished(status);
        RobotManagerController.getInstance().getRobotController().is_initialize_finished(status);
    }

    @Override
    public void stop_initialize(RobotStatus<Status> status) {
        super.stop_initialize(status);
        RobotManagerController.getInstance().getRobotController().stop_initialize(status);
    }

    @Override
    public void work_status(RobotStatus<RobotWorkStatus> status) {
        super.work_status(status);
        GsController.INSTANCE.work_status(status);
    }

}
