package com.example.robot.utils;

import android.util.Log;

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

    private String spinnerTime = null;
    private String tvTime = null;
    private String mapName = null;
    private String taskName = null;
    private List<String> data;
    private byte[] bytes;
    private List<TaskBean> mTaskList;
    private int time;

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

    public String putJsonMessage(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.SPINNERTIME, spinnerTime);
            jsonObject.put(Content.MAP_NAME, mapName);
            JSONArray jsonArray = new JSONArray(data);
            jsonObject.put(Content.DATATIME, jsonArray);
            jsonObject.put(Content.SENDMAPICON, new String(bytes));
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

    public List<TaskBean> getmTaskList() {
        return mTaskList;
    }

    public void setmTaskList(List<TaskBean> mTaskList) {
        this.mTaskList = mTaskList;
    }

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
