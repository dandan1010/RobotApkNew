package com.example.robot.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.WorldPoseBean;
import com.example.robot.bean.TaskBean;

import java.util.ArrayList;

public class SharedPrefUtil {

    private static SharedPrefUtil sharedPrefUtil;
    private static final String SpName = "SpName";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private SharedPrefUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(SpName, Context.MODE_PRIVATE);
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

    /**
     * 任务详情
     * @param key
     */
//    public void deleteTaskQueue(String key) {
//        editor.remove(key);
//        editor.commit();
//    }
//
//    public void setPositionMsg(String mapName, String taskName, ArrayList<TaskBean> arrayList) {
//        GsonUtils gsonUtils = new GsonUtils();
//        editor.putString(mapName +","+ taskName, gsonUtils.putJsonPositionMessage(taskName, arrayList));
//        editor.commit();
//    }
//
//    public String getPositionMsg(String mapName, String taskName) {
//        return sharedPreferences.getString(mapName +","+ taskName, null);
//    }

    /**
     * LED亮度
     * @param key
     * @param level
     */
    public void setSharedPrefLed(String key, int level){
        editor.putInt(key, level);
        editor.commit();
    }

    public int getSharedPrefLed(String key) {
        Log.d("get battery " , ""+sharedPreferences.getInt(key, Content.led));
        return sharedPreferences.getInt(key, Content.led);
    }


    /**
     * 低电量回充
     * @param key
     * @param level
     */
    public void setSharedPrefBattery(String key, int level){
        editor.putInt(key, level);
        editor.commit();
    }

    public int getSharedPrefBattery(String key) {
        Log.d("get battery " , ""+sharedPreferences.getInt(key, Content.battery));
        return sharedPreferences.getInt(key, Content.battery);
    }
    /**
     * 工作模式
     */
    public void setSharedPrefWorkingMode(String key, int level){
        editor.putInt(key, level);
        editor.commit();
    }

    public int getSharedPrefWorkingMode(String key) {
        Log.d("get battery " , ""+sharedPreferences.getInt(key, Content.Working_mode));
        return sharedPreferences.getInt(key, Content.Working_mode);
    }

    /**
     * 工作模式
     */
    public void setSharedPrefChargingMode(String key, boolean level){
        editor.putBoolean(key, level);
        editor.commit();
    }

    public boolean getSharedPrefChargingMode(String key) {
        Log.d("get battery " , ""+sharedPreferences.getBoolean(key, Content.have_charging_mode));
        return sharedPreferences.getBoolean(key,Content.have_charging_mode);
    }

    /**
     * 清除所有数据
     */
    public void deleteAll(){
        editor.clear();
        editor.commit();
    }

    /**
     * 工作开始时间
     */
    public void setSharedPrefStartTime(String key, long level){
        editor.putLong(key, level);
        editor.commit();
    }

    public Long getSharedPrefStartTime(String key) {
        Log.d("get startTime " , ""+sharedPreferences.getLong(key, Content.startTime));
        return sharedPreferences.getLong(key, Content.startTime);
    }
}
