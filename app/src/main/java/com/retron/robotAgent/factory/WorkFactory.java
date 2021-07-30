package com.retron.robotAgent.factory;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotWorkStatus;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.retron.factorybean.MapPngBean;
import com.retron.factorybean.MoveBean;
import com.retron.robotAgent.icontroller.RobotEventController;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class WorkFactory implements RobotEventController {

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
    public void getPositions(String mapName, RobotStatus<RobotPosition> status) {

    }

    @Override
    public void cancelNavigate(RobotStatus<Status> status) {

    }

    @Override
    public void is_initialize_finished(RobotStatus<Status> status) {

    }

    @Override
    public void work_status(RobotStatus<RobotWorkStatus> status) {

    }

    @Override
    public void RobotStatus(NavigationStatus navigationStatus, String... args) {

    }
}