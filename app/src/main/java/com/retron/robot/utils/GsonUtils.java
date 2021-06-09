package com.retron.robot.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.retron.robot.BuildConfig;
import com.retron.robot.bean.PointStateBean;
import com.retron.robot.bean.RobotPointPositions;
import com.retron.robot.bean.RunningTaskBean;
import com.retron.robot.bean.TaskBean;
import com.retron.robot.content.Content;
import com.retron.robot.service.SocketServices;
import com.retron.robot.sqlite.SqLiteOpenHelperUtils;

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
    private String tvTime = null;
    private String mapName = "";
    private String taskName = "";
    private double x;
    private double y;
    private int gridHeight;
    private int gridWidth;
    private double originX;
    private double originY;
    private double resolution;
    private double angle;
    private String battery = null;
    private String testCallBack = null;
    private String healthyMsg = null;
    private PointStateBean taskState = null;
    private VirtualObstacleBean virtualObstacleBean;
    private int navigationSpeedLevel;
    private int playPathSpeedLevel;

    private int low_battery;
    private int voice;
    private int workingMode;
    private String robotVersion;
    private String address = "";
    private String robotName = "";

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRobotVersion(String robotVersion) {
        this.robotVersion = robotVersion;
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

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public void setLow_battery(int low_battery) {
        this.low_battery = low_battery;
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

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
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
                for (int j = 0; j < virtualObstacleBean.getData().getObstacles().getPolylines().get(i).size(); j++) {
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
        Log.d(TAG, "putVirtualObstacle : " + jsonObject.toString());
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
        Log.d("putTestSensorCallBack ", jsonObject.toString());

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
        Log.d("putSocketHealthyMsg ", jsonObject.toString());
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
                js.put(Content.POINT_INDEX, Content.taskIndex);
                js.put(Content.POINT_NAME, taskState.getList().get(i).getPointName());
                js.put(Content.POINT_STATE, taskState.getList().get(i).getPointState());
                js.put(Content.POINT_TIME, taskState.getList().get(i).getTimeCount());
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
            Log.d(TAG, "HISTORY :cursor ---- " + cursor.getCount());
            while (cursor.moveToNext()) {
                JSONObject js = new JSONObject();
                js.put(Content.dbTaskMapName, cursor.getString(cursor.getColumnIndex(Content.dbTaskMapName)));
                js.put(Content.dbTaskName, cursor.getString(cursor.getColumnIndex(Content.dbTaskName)));
                js.put(Content.dbTime, cursor.getString(cursor.getColumnIndex(Content.dbTime)));
                js.put(Content.dbDate, cursor.getString(cursor.getColumnIndex(Content.dbData)));
                js.put(Content.dbStartBattery, cursor.getString(cursor.getColumnIndex(Content.dbStartBattery)));
                js.put(Content.dbEndBattery, cursor.getString(cursor.getColumnIndex(Content.dbEndBattery)));
                js.put(Content.dbTaskIndex, cursor.getString(cursor.getColumnIndex(Content.dbTaskIndex)));
                js.put(Content.dbTaskPointState, cursor.getString(cursor.getColumnIndex(Content.dbTaskPointState)));
                jsonArray.put(js);
            }
            jsonObject.put(Content.ROBOT_TASK_HISTORY, jsonArray);
            Cursor cursorTotal = sqLiteOpenHelperUtils.searchTaskTotalCount();
            long taskCount = 0, taskTime = 0, area = 0;
            while (cursorTotal.moveToNext()) {
                taskCount = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTaskTotalCount)));
                taskTime = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTimeTotalCount)));
                area = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbAreaTotalCount)));
            }
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.dbAreaTotalCount, area);
            jsonObject.put(Content.dbTaskTotalCount, taskCount);
            jsonObject.put(Content.dbTimeTotalCount, taskTime);

            Cursor cursorCurrent = sqLiteOpenHelperUtils.searchTaskCurrentCount(new AlarmUtils(context).getTimeMonth(System.currentTimeMillis()));
            long taskCurrentCount = 0, taskCurrentTime = 0, areaCurrent = 0;
            while (cursorTotal.moveToNext()) {
                taskCurrentCount = Long.parseLong(cursorCurrent.getString(cursorCurrent.getColumnIndex(Content.dbTaskCurrentCount)));
                taskCurrentTime = Long.parseLong(cursorCurrent.getString(cursorCurrent.getColumnIndex(Content.dbTimeCurrentCount)));
                areaCurrent = Long.parseLong(cursorCurrent.getString(cursorCurrent.getColumnIndex(Content.dbAreaCurrentCount)));
            }
            jsonObject.put(Content.dbAreaCurrentCount, areaCurrent);
            jsonObject.put(Content.dbTaskCurrentCount, taskCurrentCount);
            jsonObject.put(Content.dbTimeCurrentCount, taskCurrentTime);
            Log.d("sendRobotMsg ", jsonObject.toString());
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
            jsonObject.put(Content.MAP_NAME, SocketServices.use_mapName);
            jsonObject.put(Content.TASK_NAME, taskName);
            jsonObject.put(Content.Address, address);
            jsonObject.put(Content.VERSIONCODE, BuildConfig.VERSION_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("putConnMsg ", jsonObject.toString());

        return jsonObject.toString();
    }

    public String sendCopyLength(String type, int length) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.COPY_FILE_LENGTH, length);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendCopyLength ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String startFTPServer(String type, String message) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.START_PTP_SERVER, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("startFTPServer ", jsonObject.toString());
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
        Log.d("putCallBackMsg ", jsonObject.toString());
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
        Log.d("putTVTime ", jsonObject.toString());
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
        Log.d("putRobotPosition ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String getRobotStatus(String type, String status) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.ROBOT_STATUS, status);
            jsonObject.put(Content.DEVICES_STATUS, Content.EMERGENCY);
            jsonObject.put(Content.BATTERY_DATA, battery);
            jsonObject.put(Content.TASK_NAME, TextUtils.isEmpty(Content.taskName) ? "" : Content.taskName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getRobotStatus : " + jsonObject.toString());
        return jsonObject.toString();
    }

    public String getTaskRun(String type, ArrayList<RunningTaskBean> runningTaskBeanArrayList) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            JSONArray jsonArrayList = new JSONArray();
            for (int i = 0; i < runningTaskBeanArrayList.size(); i++) {
                JSONArray jsonArray = new JSONArray();
                JSONObject obj = new JSONObject();
                for (int j = 0; j < runningTaskBeanArrayList.get(i).getArrayList().size(); j++) {
                    JSONObject js = new JSONObject();
                    js.put(Content.POINT_NAME, runningTaskBeanArrayList.get(i).getArrayList().get(j).getPointName());
                    js.put(Content.POINT_X, runningTaskBeanArrayList.get(i).getArrayList().get(j).getPointX());
                    js.put(Content.POINT_Y, runningTaskBeanArrayList.get(i).getArrayList().get(j).getPointY());
                    jsonArray.put(js);
                }
                obj.put(Content.POINT, jsonArray);
                obj.put(Content.TASK_NAME, runningTaskBeanArrayList.get(i).getTaskName());
                obj.put(Content.MAP_NAME, runningTaskBeanArrayList.get(i).getMapName());
                obj.put(Content.dbAlarmTime, runningTaskBeanArrayList.get(i).getTaskAlarm());
                obj.put(Content.dbAlarmCycle, runningTaskBeanArrayList.get(i).getAlarmCycle());
                obj.put(Content.dbAlarmIsRun, runningTaskBeanArrayList.get(i).getIsRunning());
                jsonArrayList.put(obj);
            }
            jsonObject.put(Content.TASK, jsonArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("getTaskRun ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String sendRobotMsg(String type, String msg) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(type, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendRobotMsg ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String sendRobotMsg(String type, long msg) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(type, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendRobotMsg ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String sendRobotMsg(String type, boolean msg) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(type, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendRobotMsg ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String sendRobotMsg(String type, List<String> list) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            JSONArray jsonArray = new JSONArray(list);
            jsonObject.put(Content.DATATIME, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendRobotMsg ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String sendRobotMsg(String type, ArrayList<ArrayList<RobotPointPositions>> robotPointPositions, RobotMap robotMap) {
        Log.d(TAG, "ZDZD : SIZE ----- " + robotPointPositions.size());
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            JSONArray mapArray = new JSONArray();
            if (robotMap != null) {
                for (int i = 0; i < robotMap.getData().size(); i++) {
                    JSONObject object = new JSONObject();
                    object.put(Content.MAP_NAME, robotMap.getData().get(i).getName());
                    object.put(Content.GRID_HEIGHT, robotMap.getData().get(i).getMapInfo().getGridHeight());
                    object.put(Content.GRID_WIDTH, robotMap.getData().get(i).getMapInfo().getGridWidth());
                    object.put(Content.ORIGIN_X, robotMap.getData().get(i).getMapInfo().getOriginX());
                    object.put(Content.ORIGIN_Y, robotMap.getData().get(i).getMapInfo().getOriginY());
                    object.put(Content.RESOLUTION, robotMap.getData().get(i).getMapInfo().getResolution());

                    JSONArray mRobotPositionsArray = new JSONArray();
                    if (robotPointPositions.size() != 0) {
                        for (int j = 0; j < robotPointPositions.size(); j++) {
                            for (int k = 0; k < robotPointPositions.get(j).size(); k++) {
                                if (robotMap.getData().get(i).getName().equals(robotPointPositions.get(j).get(k).getMapName())) {
                                    JSONObject pointObject = new JSONObject();
                                    pointObject.put(Content.POINT_NAME, robotPointPositions.get(j).get(k).getName());
                                    pointObject.put(Content.POINT_X, robotPointPositions.get(j).get(k).getGridX());
                                    pointObject.put(Content.POINT_Y, robotPointPositions.get(j).get(k).getGridY());
                                    pointObject.put(Content.ANGLE, robotPointPositions.get(j).get(k).getAngle());
                                    pointObject.put(Content.POINT_TYPE, robotPointPositions.get(j).get(k).getType());
                                    pointObject.put(Content.POINT_TIME, robotPointPositions.get(j).get(k).getPointTime());
                                    mRobotPositionsArray.put(pointObject);
                                }
                            }
                        }
                    }
                    object.put(Content.POINT, mRobotPositionsArray);
                    mapArray.put(i, object);
                }
            }
            jsonObject.put(type, mapArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendRobotMsg ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String sendRobotMsg(String type, ArrayList<RobotPointPositions> robotPointPositions) {
        Log.d(TAG, "ZDZD : SIZE ----- " + robotPointPositions.size());
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            JSONArray mRobotPositionsArray = new JSONArray();
            if (robotPointPositions.size() != 0) {
                for (int j = 0; j < robotPointPositions.size(); j++) {
                    JSONObject pointObject = new JSONObject();
                    pointObject.put(Content.POINT_NAME, robotPointPositions.get(j).getName());
                    pointObject.put(Content.POINT_X, robotPointPositions.get(j).getGridX());
                    pointObject.put(Content.POINT_Y, robotPointPositions.get(j).getGridY());
                    pointObject.put(Content.ANGLE, robotPointPositions.get(j).getAngle());
                    pointObject.put(Content.POINT_TYPE, robotPointPositions.get(j).getType());
                    pointObject.put(Content.POINT_TIME, robotPointPositions.get(j).getPointTime());
                    mRobotPositionsArray.put(pointObject);
                }
            }
            jsonObject.put(Content.POINT, mRobotPositionsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendRobotMsg ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String sendRobotMsg(String type, UltrasonicPhitBean ultrasonicPhitBean) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            JSONArray mUltrasonicPhitBeanArray = new JSONArray();
            JSONArray mWorldUltrasonicArray = new JSONArray();
            if (ultrasonicPhitBean != null
                    && ultrasonicPhitBean.getGridPhits().size() > 0
                    && ultrasonicPhitBean.getWorldPhits().size() > 0) {
                for (int i = 0; i < ultrasonicPhitBean.getGridPhits().size(); i++) {
                    JSONObject object = new JSONObject();
                    object.put(Content.GET_ULTRASONIC_X, ultrasonicPhitBean.getGridPhits().get(i).getX());
                    object.put(Content.GET_ULTRASONIC_Y, ultrasonicPhitBean.getGridPhits().get(i).getY());
                    mUltrasonicPhitBeanArray.put(i, object);
                }
                for (int i = 0; i < ultrasonicPhitBean.getWorldPhits().size(); i++) {
                    JSONObject object = new JSONObject();
                    object.put(Content.WORLD_ULTRASONIC_X, ultrasonicPhitBean.getWorldPhits().get(i).getX());
                    object.put(Content.WORLD_ULTRASONIC_Y, ultrasonicPhitBean.getWorldPhits().get(i).getY());
                    mWorldUltrasonicArray.put(i, object);
                }
            }
            jsonObject.put(Content.GET_ULTRASONIC, mUltrasonicPhitBeanArray);
            jsonObject.put(Content.WORLD_ULTRASONIC, mWorldUltrasonicArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("sendRobotMsg ", jsonObject.toString());
        return jsonObject.toString();
    }

    public String putJsonMessage(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.ROBOT_NAME, robotName);
            jsonObject.put(Content.GET_NAVIGATIONSPEEDLEVEL, navigationSpeedLevel);
            jsonObject.put(Content.GET_PLAYPATHSPEEDLEVEL, playPathSpeedLevel);
            jsonObject.put(Content.GET_LOW_BATTERY, low_battery);
            jsonObject.put(Content.GET_VOICE_LEVEL, voice);
            jsonObject.put(Content.GET_WORKING_MODE, workingMode);
            jsonObject.put(Content.GET_CHARGING_MODE, Content.have_charging_mode);
            jsonObject.put(Content.MAP_NAME, mapName);
            jsonObject.put(Content.VERSIONCODE, BuildConfig.VERSION_NAME);
            jsonObject.put(Content.ROBOTVERSIONCODE, robotVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("GSONUTILS ", jsonObject.toString());
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
