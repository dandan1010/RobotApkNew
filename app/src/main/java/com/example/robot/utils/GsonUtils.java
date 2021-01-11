package com.example.robot.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.example.robot.BuildConfig;
import com.example.robot.bean.PointStateBean;
import com.example.robot.bean.TaskBean;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;

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
    private PointStateBean taskState = null;
    private VirtualObstacleBean virtualObstacleBean;
    private int navigationSpeedLevel;
    private int playPathSpeedLevel;

    private int led_level;
    private int low_battery;
    private int voice;
    private List<String> editTaskType;
    private String editTime;
    private String task_state;
    private UltrasonicPhitBean ultrasonicPhitBean;
    private List<TaskBean> taskBeans;
    private int workingMode;
    private boolean emergency;

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public void setNavigationSpeedLevel(int navigationSpeedLevel) {
        this.navigationSpeedLevel = navigationSpeedLevel;
    }

    public void setPlayPathSpeedLevel(int playPathSpeedLevel) {
        this.playPathSpeedLevel = playPathSpeedLevel;
    }

    public void setWorkingMode(int workingMode) {
        this.workingMode = workingMode;
    }

    public void setTaskBeans(List<TaskBean> taskBeans) {
        this.taskBeans = taskBeans;
    }

    public void setUltrasonicPhitBean(UltrasonicPhitBean ultrasonicPhitBean) {
        this.ultrasonicPhitBean = ultrasonicPhitBean;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public void setLow_battery(int low_battery) {
        this.low_battery = low_battery;
    }

    public void setLed_level(int led_level) {
        this.led_level = led_level;
    }

    public void setTask_state(String task_state) {
        this.task_state = task_state;
    }

    public void setEditTaskType(List<String> editTaskType) {
        this.editTaskType = editTaskType;
    }

    public void setVirtualObstacleBean(VirtualObstacleBean virtualObstacleBean) {
        this.virtualObstacleBean = virtualObstacleBean;
    }
    public void setHealthyMsg(String healthyMsg) {
        this.healthyMsg = healthyMsg;
    }

    public void setTaskState(PointStateBean taskState) {
        this.taskState = taskState;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setTestCallBack(String testCallBack) {
        this.testCallBack = testCallBack;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public void setmRobotPositions(RobotPositions mRobotPositions) {
        this.mRobotPositions = mRobotPositions;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
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

    public String putVirtualObstacle(String type) {
        jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put(TYPE, type);
            for (int i = 0; i < virtualObstacleBean.getData().getObstacles().getPolylines().size(); i++) {
                JSONArray jsArray = new JSONArray();
                for (int j = 0; j< virtualObstacleBean.getData().getObstacles().getPolylines().get(i).size(); j++){
                    JSONObject js = new JSONObject();
                    js.put(Content.VIRTUAL_X, virtualObstacleBean.getData().getObstacles().getPolylines().get(i).get(j).getX());
                    js.put(Content.VIRTUAL_Y, virtualObstacleBean.getData().getObstacles().getPolylines().get(i).get(j).getY());
                    jsArray.put(j, js);
                }
                jsonArray.put(i, jsArray);
            }
            jsonObject.put(Content.SEND_VIRTUAL, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "JS string : " + jsonObject.toString());
        return jsonObject.toString();
    }

    public String putTestSensorCallBack(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.TEST_SENSOR, testCallBack);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putSocketHealthyMsg(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.ROBOT_HEALTHY, healthyMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putSocketTaskMsg(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.TASK_NAME, taskName);
            jsonObject.put(Content.MAP_NAME, mapName);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < taskState.getList().size(); i++) {
                JSONObject js = new JSONObject();
                js.put(Content.POINT_NAME, taskState.getList().get(i).getPointName());
                js.put(Content.POINT_STATE, taskState.getList().get(i).getPointState());
                jsonArray.put(i, js);
            }
            jsonObject.put(Content.ROBOT_TASK_STATE, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("gsonutils ", "robot_state : " + jsonObject.toString());
        return jsonObject.toString();
    }

    public String putSocketTaskHistory(String type, Context context) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            JSONArray jsonArray = new JSONArray();
            SqLiteOpenHelperUtils sqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(context);
            Cursor cursor = sqLiteOpenHelperUtils.searchTaskHistory();
            while (cursor.moveToNext()) {
                JSONObject js = new JSONObject();
                js.put(Content.dbTaskMapName, cursor.getString(cursor.getColumnIndex(Content.dbTaskMapName)));
                js.put(Content.dbTaskName, cursor.getString(cursor.getColumnIndex(Content.dbTaskName)));
                js.put(Content.dbTime, cursor.getString(cursor.getColumnIndex(Content.dbTime)));
                js.put(Content.dbData, cursor.getString(cursor.getColumnIndex(Content.dbData)));
                jsonArray.put(js);
            }
            jsonObject.put(Content.ROBOT_TASK_HISTORY, jsonArray);
            sqLiteOpenHelperUtils.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("history gson : ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String putConnMsg(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.MAP_NAME, mapName);
            jsonObject.put(Content.TASK_NAME, taskName);
            jsonObject.put(Content.versionCode, BuildConfig.VERSION_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putCallBackMsg(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.REQUEST_MSG, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putBattery(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.BATTERY_DATA, battery);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putTVTime(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.TV_TIME, tvTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String putRobotPosition(String type) {
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
        Log.d("putRobotPosition " , jsonObject.toString());
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
            jsonObject.put(Content.DEVICES_STATUS, emergency);
            jsonObject.put(Content.GET_NAVIGATIONSPEEDLEVEL, navigationSpeedLevel);
            jsonObject.put(Content.GET_PLAYPATHSPEEDLEVEL, playPathSpeedLevel);
            jsonObject.put(Content.GET_LED_LEVEL, led_level);
            jsonObject.put(Content.GET_LOW_BATTERY, low_battery);
            jsonObject.put(Content.GET_VOICE_LEVEL, voice);
            jsonObject.put(Content.GET_WORKING_MODE, workingMode);
            jsonObject.put(Content.MAP_NAME, mapName);
            jsonObject.put(Content.GET_TASK_STATE, task_state);
            jsonObject.put(Content.versionCode, BuildConfig.VERSION_CODE);
            JSONArray edPointArray = new JSONArray();
            if (taskBeans != null) {
                for (int i = 0; i < taskBeans.size(); i++) {
                    JSONObject edJson = new JSONObject();
                    edJson.put(Content.dbPointName, taskBeans.get(i).getName());
                    edJson.put(Content.dbSpinnerTime, taskBeans.get(i).getDisinfectTime());
                    edJson.put(Content.dbPointX, taskBeans.get(i).getX());
                    edJson.put(Content.dbPointY, taskBeans.get(i).getY());
                    edPointArray.put(i, edJson);
                }
            }
            jsonObject.put(Content.EDITTASKQUEUE, edPointArray);
            jsonObject.put(Content.EDITTASKQUEUETIME, editTime);
            JSONArray edTypeArray = new JSONArray();
            if (editTaskType != null) {
                for (int i = 0; i < editTaskType.size(); i++) {
                    edTypeArray.put(i, editTaskType.get(i));
                }
            }
            jsonObject.put(Content.EDITTASKQUEUETYPE, edTypeArray);
            JSONArray jsonArray = new JSONArray(data);
            jsonObject.put(Content.DATATIME, jsonArray);
            JSONArray mUltrasonicPhitBeanArray = new JSONArray();
            if (ultrasonicPhitBean != null) {
                for (int i = 0; i < ultrasonicPhitBean.getGridPhits().size(); i++) {
                    JSONObject object = new JSONObject();
                    object.put(Content.GET_ULTRASONIC_X, ultrasonicPhitBean.getGridPhits().get(i).getX());
                    object.put(Content.GET_ULTRASONIC_Y, ultrasonicPhitBean.getGridPhits().get(i).getY());
                    mUltrasonicPhitBeanArray.put(i, object);
                }
            }
            jsonObject.put(Content.GET_ULTRASONIC, mUltrasonicPhitBeanArray);
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
        Log.d("GSONUTILS " , jsonObject.toString());
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
                object.put(Content.POINT_NAME, taskBean.getName());
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
