package com.retron.robotAgent.utils;

import android.content.Context;
import android.util.Log;

import com.retron.robotAgent.content.BaseEvent;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.service.SimpleServer;
import com.retron.robotAgent.service.SocketServices;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class HandleMessage {

    private GsonUtils gsonUtils;
    private JSONObject jsonObject;
    private static HandleMessage handleMessage = null;
    private static Context mContext;
    /**
     * 获取唯一的instance
     */
    public static HandleMessage getInstance(Context context) {
        mContext = context;
        if (handleMessage == null) {
            synchronized (HandleMessage.class) {
                if (handleMessage == null) {
                    handleMessage = new HandleMessage();
                }
            }
        }
        return handleMessage;
    }

    public HandleMessage() {
        gsonUtils = new GsonUtils();
        jsonObject = new JSONObject();
    }

    public void differentiateType(String message) throws JSONException {
        String mapName;
        String taskName;
        switch (gsonUtils.getType(message)) {
            case Content.PING:
                EventBus.getDefault().post(new EventBusMessage(30000, message));
                break;
            case Content.STARTLIGHT:
                jsonObject = new JSONObject(message);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTLIGHT, message));
                break;
            case Content.STOPLIGHT:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPLIGHT, message));
                break;
            case Content.STARTMOVE:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTMOVE, message));
                break;
            case Content.GETMAPLIST://地图列表
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETMAPLIST, message));
                break;
            case Content.SAVETASKQUEUE://存储任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SAVETASKQUEUE, message));
                break;
            case Content.DELETETASKQUEUE://删除任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DELETETASKQUEUE, message));
                break;
            case Content.GETTASKQUEUE://任务列表
                jsonObject = new JSONObject(message);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETTASKQUEUE, message));
                break;
            case Content.GETMAPPIC://地图图片
                jsonObject = new JSONObject(message);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETMAPPIC, message));
                break;
            case Content.ADD_POSITION://添加点
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.ADD_POSITION, message));
                break;
            case Content.STARTTASKQUEUE://开始任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTTASKQUEUE, message));
                break;
            case Content.STOPTASKQUEUE://停止任务
                jsonObject = new JSONObject(message);
                taskName = jsonObject.getString(Content.TASK_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPTASKQUEUE, taskName));
                break;
            case Content.CANCELTASKQUEUE://取消当前任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.CANCELTASKQUEUE, message));
                break;
            case Content.START_SCAN_MAP://开始扫描地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.START_SCAN_MAP, message));
                break;
            case Content.USE_MAP://选定地图
                jsonObject = new JSONObject(message);
                SocketServices.use_mapName = jsonObject.getString(Content.MAP_NAME_UUID);
                SocketServices.current_mapname = jsonObject.getString(Content.MAP_NAME);
                SharedPrefUtil.getInstance(mContext).setSharedPrefMapName(Content.MAP_NAME_UUID, jsonObject.getString(Content.MAP_NAME_UUID));
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPALLTASKQUEUE, message));
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.USE_MAP, message));
                break;
            case Content.GETPOINTPOSITION://地图点数据
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETPOINTPOSITION, message));
                break;
            case Content.CANCEL_SCAN_MAP://取消扫描并且保存地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.CANCEL_SCAN_MAP, message));
                break;
            case Content.DEVELOP_MAP://拓展地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DEVELOP_MAP, message));
                break;
            case Content.DELETE_MAP://删除地图
                jsonObject = new JSONObject(message);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DELETE_MAP, message));
                break;
            case Content.DELETE_POSITION://删除点
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DELETE_POSITION, message));
                break;
            case Content.CANCEL_SCAN_MAP_NO://取消扫描不保存地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.CANCEL_SCAN_MAP_NO, message));
                break;
            case Content.ROBOT_TASK_HISTORY://任务历史
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_HISTORY, message));
                break;
//            case Content.GET_VIRTUAL://获取虚拟强数据
//                jsonObject = new JSONObject(message);
//                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_VIRTUAL, message));
//                break;
            case Content.UPDATA_VIRTUAL://更新虚拟强
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPDATA_VIRTUAL, message));
                break;
            case Content.TASK_ALARM://定时任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TASK_ALARM, message));
                break;
            case Content.RENAME_MAP://重命名地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.RENAME_MAP, message));
                break;
            case Content.RENAME_POSITION://点重命名
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.RENAME_POSITION, message));
                break;
            case Content.ADD_POWER_POINT://添加充电点
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.ADD_POWER_POINT, message));
                break;
            case Content.EDITTASKQUEUE://编辑任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.EDITTASKQUEUE, message));
                break;
            case Content.SYSTEM_DATE://设置系统时间
                long date = new JSONObject(message).getLong(Content.SYSTEM_DATE);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SYSTEM_DATE, date));
                break;
            case Content.GET_TASK_STATE://是否有任务正在执行
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, message));
                break;
            case Content.SET_LED_LEVEL://设置led亮度
                Content.led = new JSONObject(message).getInt(Content.SET_LED_LEVEL);
                SharedPrefUtil.getInstance(mContext).setSharedPrefLed(Content.SET_LED_LEVEL, Content.led);
                break;
            case Content.GET_LED_LEVEL://获取led亮度
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_LED_LEVEL, message));
                break;
            case Content.RESET_ROBOT://重置设置
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.RESET_ROBOT, message));
                break;
            case Content.GET_ULTRASONIC://声呐设备
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ULTRASONIC, message));
                break;
            case Content.dbTotalCount://统计一共执行任务个数，时间，面积
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.dbTotalCount, message));
                break;
            case Content.dbCurrentCount://统计当月执行任务个数，时间，面积
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.dbCurrentCount, message));
                break;
            case Content.GET_SETTING_MODE://获取设置信息
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_SETTING_MODE, message));
                break;
            case Content.SET_SETTING_MODE://设置信息
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SET_SETTING_MODE, message));
                break;
            case Content.INITIALIZE://重新初始化
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE, message));
                break;
            case Content.START_UVC_MODE://手动模式开启uvc
                jsonObject = new JSONObject(message);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.START_UVC,jsonObject.getInt(Content.UVC_KILL_TIME)));
                Content.robotState = 5;
                Content.time = 1000;
                break;
            case Content.STOP_UVC_MODE://手动模式关闭uvc
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOP_UVC, message));
                break;
            case Content.UPLOAD_MAP:
                jsonObject = new JSONObject(message);
                if (jsonObject.has(Content.UPLOAD_MAP)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(Content.UPLOAD_MAP);
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPLOAD_MAP, jsonArray));
                }
                break;
//            case Content.DOWNLOAD_MAP:
//                jsonObject = new JSONObject(message);
//                if (jsonObject.has(Content.DOWNLOAD_MAP)) {
//                    JSONArray jsonArray = jsonObject.getJSONArray(Content.DOWNLOAD_MAP);
//                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.DOWNLOAD_MAP, jsonArray));
//                }
//                break;
            case Content.MQTT_ADD_POINT:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.MQTT_ADD_POINT, message));
                break;
            case Content.MQTT_UPDATA_VIRTUAL:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.MQTT_UPDATA_VIRTUAL, message));
                break;
            case Content.MQTT_RENAME_MAP:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.MQTT_RENAME_MAP, message));
                break;


//test request:
            case Content.TEST_UVCSTART_1:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTART_1, message));
                break;
            case Content.TEST_UVCSTART_2:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTART_2, message));
                break;
            case Content.TEST_UVCSTART_3:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTART_3, message));
                break;
            case Content.TEST_UVCSTART_4:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTART_4, message));
                break;

            case Content.TEST_UVCSTOP_1:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTOP_1, message));
                break;
            case Content.TEST_UVCSTOP_2:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTOP_2, message));
                break;
            case Content.TEST_UVCSTOP_3:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTOP_3, message));
                break;
            case Content.TEST_UVCSTOP_4:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTOP_4, message));
                break;

            case Content.TEST_UVCSTART_ALL:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTART_ALL, message));
                break;

            case Content.TEST_UVCSTOP_ALL:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_UVCSTOP_ALL, message));
                break;
            case Content.TEST_LIGHTSTART:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_LIGHTSTART, message));
                break;
            case Content.TEST_LIGHTSTOP:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_LIGHTSTOP, message));
                break;
            case Content.TEST_SENSOR:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_SENSOR, message));
                break;
            case Content.TEST_WARNINGSTART:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_WARNINGSTART, message));
                break;
            case Content.TEST_WARNINGSTOP:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TEST_WARNINGSTOP, message));
                break;
            case Content.GET_LOG_LIST:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_LOG_LIST, message));
                break;
            case Content.DOWNLOAD_LOG:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DOWNLOAD_LOG, message));
                break;
            case Content.MQTT_DOWNLOAD_LOG:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.MQTT_DOWNLOAD_LOG, message));
                break;
            case Content.CANCEL_DOWNLOAD_LOG:
                Content.isDownloadCancel = false;
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETMAPPIC, message));
                break;
            case Content.UPDATE_FILE_LENGTH:
                jsonObject = new JSONObject(message);
                Content.update_file_length = jsonObject.getLong(Content.UPDATE_FILE_LENGTH);
                Content.update_file_name = jsonObject.getString(Content.UPDATE_FILE_NAME);
                break;
            default:
                break;
        }

    }
}
