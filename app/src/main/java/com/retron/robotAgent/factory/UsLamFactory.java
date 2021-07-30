package com.retron.robotAgent.factory;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotWorkStatus;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.retron.robotAgent.controllerbean.TargetListBean;
import com.retron.robotAgent.icontroller.RobotEventController;
import com.retron.robotAgent.icontroller.UsLamController;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class UsLamFactory extends WorkFactory implements UsLamController {

    @Override
    public void setTargetPoint(TargetListBean targetPoint, RobotStatus<Status> status) {

    }

    @Override
    public void relocalization(float x, float y, float theta, RobotStatus<Status> status) {

    }

    @Override
    public void disRlocalization() {

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
    public void updateMap(String mapName, JSONObject umap, RobotStatus<Status> status) {

    }

    @Override
    public void editPosition(RobotStatus<Status> status) {

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
