package com.example.robotapk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import retrofit2.http.PUT;

public class SharedPrefUtil {

    private static final String SP_NAME = "task_sp";

    private static SharedPrefUtil sharedPrefUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private SharedPrefUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 获取唯一的instance
     */
    public static SharedPrefUtil getInstance(Context context) {
        if (sharedPrefUtil == null) {
            synchronized (SharedPrefUtil.class) {
                if (sharedPrefUtil == null) {
                    sharedPrefUtil = new SharedPrefUtil(context);
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

    public String getTaskQueue(String type) {
        return sharedPreferences.getString(type, null);
    }

    public void deleteTaskQueue(String type) {
        editor.remove(type);
        editor.commit();
    }

}
