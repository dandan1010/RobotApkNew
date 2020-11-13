package com.example.robot.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.WorldPoseBean;
import com.example.robot.bean.TaskBean;

import java.util.ArrayList;

public class SharedPrefUtil {

    private static SharedPrefUtil sharedPrefUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private SharedPrefUtil(Context context, String map_name) {
        sharedPreferences = context.getSharedPreferences(map_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 获取唯一的instance
     */
    public static SharedPrefUtil getInstance(Context context, String map_name) {
        if (sharedPrefUtil == null) {
            synchronized (SharedPrefUtil.class) {
                if (sharedPrefUtil == null) {
                    sharedPrefUtil = new SharedPrefUtil(context, map_name);
                }
            }
        }
        return sharedPrefUtil;
    }

    public void setTaskQueue(String taskJson) {
        GsonUtils gsonUtils = new GsonUtils();
        editor.putString(gsonUtils.getType(taskJson), taskJson);
        editor.commit();
    }

    public String getTaskQueue(String taskName) {
        return sharedPreferences.getString(taskName, null);
    }

    public void deleteTaskQueue(String type) {
        editor.remove(type);
        editor.commit();
    }

    public void setPositionMsg(String taskName, ArrayList<TaskBean> arrayList) {
        GsonUtils gsonUtils = new GsonUtils();
        editor.putString(taskName, gsonUtils.putJsonPositionMessage(taskName, arrayList));
        editor.commit();
    }
    public String getPositionMsg(String taskName) {
        return sharedPreferences.getString(taskName, null);
    }

}
