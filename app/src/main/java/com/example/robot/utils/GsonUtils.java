package com.example.robot.utils;

import android.util.Log;

import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.example.robot.MyApplication;
import com.example.robot.bean.TaskBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GsonUtils {

    private static final String TAG = "GsonUtils";
    private static final String TYPE = "type";
    private JSONObject jsonObject;

    private String callback = null;
    private String spinnerTime = null;
    private String tvTime = null;
    private String mapName = null;
    private String taskName = null;
    private List<String> data;
    private byte[] bytes;
    private int time;
    private double x;
    private double y;
    private int gridHeight;
    private int gridWidth;
    private double originX;
    private double originY;
    private double resolution;
    private double angle;
    private RobotPositions mRobotPositions = null;
    private String battery = null;
    private String testCallBack = null;
    private String healthyMsg = null;
    private String taskState = null;

    public String getHealthyMsg() {
        return healthyMsg;
    }

    public void setHealthyMsg(String healthyMsg) {
        this.healthyMsg = healthyMsg;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getTestCallBack() {
        return testCallBack;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setTestCallBack(String testCallBack) {
        this.testCallBack = testCallBack;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public RobotPositions getmRobotPositions() {
        return mRobotPositions;
    }

    public void setmRobotPositions(RobotPositions mRobotPositions) {
        this.mRobotPositions = mRobotPositions;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public double getOriginX() {
        return originX;
    }

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public double getOriginY() {
        return originY;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }

    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setSpinnerTime(String spinnerTime) {
        this.spinnerTime = spinnerTime;
    }

    public void setTvTime(String tvTime) {
        this.tvTime = tvTime;
    }

    public String putTaskHistory(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.TEST_SENSOR_CALLBACK, testCallBack);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putTestSensorCallBack(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.TEST_SENSOR_CALLBACK, testCallBack);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putSocketMsg(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.ROBOT_HEALTHY, healthyMsg);
            jsonObject.put(Content.ROBOT_TASK_STATE, taskState);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putConnMsg(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.MAP_NAME, mapName);
            jsonObject.put(Content.TASK_NAME, taskName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putCallBackMsg(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.REQUEST_MSG, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putBattery(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.BATTERY_DATA, battery);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putTVTime(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.TV_TIME, tvTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putRobotPosition(String type){
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.ROBOT_X, x);
            jsonObject.put(Content.ROBOT_Y, y);
            jsonObject.put(Content.GRID_HEIGHT, gridHeight);
            jsonObject.put(Content.GRID_WIDTH, gridWidth);
            jsonObject.put(Content.ORIGIN_X, originX);
            jsonObject.put(Content.ORIGIN_Y, originY);
            jsonObject.put(Content.RESOLUTION, resolution);
            jsonObject.put(Content.ANGLE, angle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putMapListMessage(String type, RobotMap robotMap) {
        jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put(TYPE, type);
            if (robotMap != null) {
                for (int i = 0; i < robotMap.getData().size(); i++) {
                    JSONObject object = new JSONObject();
                    object.put(Content.MAP_NAME, robotMap.getData().get(i).getName());
                    object.put(Content.GRID_HEIGHT, robotMap.getData().get(i).getMapInfo().getGridHeight());
                    object.put(Content.GRID_WIDTH, robotMap.getData().get(i).getMapInfo().getGridWidth());
                    object.put(Content.ORIGIN_X, robotMap.getData().get(i).getMapInfo().getOriginX());
                    object.put(Content.ORIGIN_Y, robotMap.getData().get(i).getMapInfo().getOriginX());
                    object.put(Content.RESOLUTION, robotMap.getData().get(i).getMapInfo().getResolution());
                    jsonArray.put(i, object);
                }
            }
            jsonObject.put(type, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public String putJsonMessage(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.TV_TIME, tvTime);
            jsonObject.put(Content.MAP_NAME, mapName);
            JSONArray jsonArray = new JSONArray(data);
            jsonObject.put(Content.DATATIME, jsonArray);
            JSONArray mRobotPositionsArray = new JSONArray();
            if (mRobotPositions != null) {
                for (int i = 0; i < mRobotPositions.getData().size(); i++) {
                    JSONObject object = new JSONObject();
                    object.put(Content.POINT_NAME, mRobotPositions.getData().get(i).getName());
                    object.put(Content.POINT_X, mRobotPositions.getData().get(i).getGridX());
                    object.put(Content.POINT_Y, mRobotPositions.getData().get(i).getGridY());
                    object.put(Content.ANGLE, mRobotPositions.getData().get(i).getAngle());
                    object.put(Content.POINT_TYPE, mRobotPositions.getData().get(i).getType());
                    mRobotPositionsArray.put(i, object);
                }
            }
            jsonObject.put(Content.SENDPOINTPOSITION, mRobotPositionsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public String getType(String message) {
        String type = null;
        if (message == null) {
            Log.d(TAG, "getType message is null");
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(message);
            type = jsonObject.getString(TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }
//存储任务
    public String putJsonPositionMessage(String taskName, ArrayList<TaskBean> arrayList) {
        jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            Log.d(TAG, "存sp的任务 ： " + arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                TaskBean taskBean = arrayList.get(i);
                JSONObject object = new JSONObject();
                object.put(Content.TASK_NAME, taskBean.getName());
                object.put(Content.TASK_X, taskBean.getX());
                object.put(Content.TASK_Y, taskBean.getY());
                object.put(Content.TASK_ANGLE, taskBean.getAngle());
                object.put(Content.TASK_DISINFECT_TIME, taskBean.getDisinfectTime());
                object.put(Content.POINT_STATE, taskBean.getPointState());
                jsonArray.put(i, object);
            }
            jsonObject.put(taskName, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "存sp的任务 ： " + jsonObject.toString());
        return jsonObject.toString();
    }


}
