package com.retron.robot.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.retron.robot.content.Content;

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
        return sharedPreferences.getBoolean(key,Content.have_charging_mode);
    }

    /**
     * 地图名字
     */
    public void setSharedPrefMapName(String key, String level){
        editor.putString(key, level);
        editor.commit();
    }

    public String getSharedPrefMapName(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**
     * 机器人名字
     */
    public void setSharedPrefRobotName(String key, String level){
        editor.putString(key, level);
        editor.commit();
    }

    public String getSharedPrefRobotName(String key) {
        Log.d("get robotName " , ""+sharedPreferences.getString(key, ""));
        return sharedPreferences.getString(key, "");
    }

    /**
     * 清除所有数据
     */
    public void deleteAll(){
        editor.clear();
        editor.commit();
    }

    public void delete(String key){
        editor.remove(key);
        editor.commit();
    }

}
