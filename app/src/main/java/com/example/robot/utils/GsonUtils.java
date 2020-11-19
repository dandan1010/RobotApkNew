package com.example.robot.utils;

import android.util.Log;

import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
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
    private float x;
    private float y;
    private float gridHeight;
    private float gridWidth;
    private float originX;
    private float originY;
    private float resolution;
    private RobotPositions mRobotPositions = null;
    private String battery = null;

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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(float gridHeight) {
        this.gridHeight = gridHeight;
    }

    public float getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(float gridWidth) {
        this.gridWidth = gridWidth;
    }

    public float getOriginX() {
        return originX;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
    }

    public float getResolution() {
        return resolution;
    }

    public void setResolution(float resolution) {
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
