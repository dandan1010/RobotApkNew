package com.example.robot.utils;

import android.util.Log;

import com.example.robot.bean.TaskBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GsonUtils {

    private static final String TAG = "GsonUtils";
    private static final String TYPE = "type";
    private JSONObject jsonObject;

    private String spinnerTime = null;
    private String tvTime = null;
    private List<String> data;

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
            JSONArray jsonArray = new JSONArray(data);
            jsonObject.put(Content.DATATIME, jsonArray);
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

    private List<TaskBean> mTaskList;

    public List<TaskBean> getmTaskList() {
        return mTaskList;
    }

    public void setmTaskList(List<TaskBean> mTaskList) {
        this.mTaskList = mTaskList;
    }

    public String putJsonTaskMessage(String taskName) {
        jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < mTaskList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Content.TASK_NAME, mTaskList.get(i).getName());
                jsonObject.put(Content.TASK_X, mTaskList.get(i).getX());
                jsonObject.put(Content.TASK_Y, mTaskList.get(i).getY());
                jsonObject.put(Content.TASK_ANGLE, mTaskList.get(i).getAngle());
                jsonObject.put(Content.TASK_DISINFECT_TIME, mTaskList.get(i).getDisinfectTime());
                jsonArray.put(i, jsonObject);
            }
            jsonObject.put(TYPE, taskName);
            jsonObject.put(taskName, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "存sp的任务 ： " + jsonObject.toString());
        return jsonObject.toString();
    }
}
