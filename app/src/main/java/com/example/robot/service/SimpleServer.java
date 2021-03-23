package com.example.robot.service;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.example.robot.content.BaseEvent;
import com.example.robot.content.Content;
import com.example.robot.utils.AssestFile;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.utils.ServerConnoct;
import com.example.robot.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class SimpleServer extends WebSocketServer {
    public static final String TAG = "SimpleServer";
    private GsonUtils gsonUtils;
    private JSONObject jsonObject;
    private String address = "";
    private Context mContext;
    public WebSocket conn;
    private AssestFile assestFile;

    private SimpleServer(InetSocketAddress address, Context mContext) {
        super(address);
        this.mContext = mContext;
        assestFile = new AssestFile(mContext);
    }

    /**
     * 获取唯一的instance
     */
    public static SimpleServer getInstance(Context context) {
        if (Content.server == null) {
            synchronized (SimpleServer.class) {
                if (Content.server == null) {
                    Log.d(TAG, "instance thread run");
                    String host = Content.ip;
                    int port = Content.port;
                    Content.server = new SimpleServer(new InetSocketAddress(host, port), context);
                    Content.server.start();
                }
            }
        }
        return Content.server;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        InetSocketAddress remoteSocketAddress = conn.getRemoteSocketAddress();
        this.address = remoteSocketAddress.getHostName();
        this.conn = conn;
        Log.d(TAG, "server address :" + address);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        InetSocketAddress remoteSocketAddress = conn.getRemoteSocketAddress();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message: " + message);
        try {
            differentiateType(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
        EventBus.getDefault().post(new EventBusMessage(30001, message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (conn != null) {
            System.err.println("an error occured on connection " + conn.getRemoteSocketAddress() + ":" + ex);
        } else {
            System.err.println("an error occured on connection " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
        gsonUtils = new GsonUtils();
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        super.stop();
        System.out.println("server stop successfully");
        if (Content.server != null) {
            Content.server = null;
        }
        if (!Content.isUpdate) {
            ServerConnoct.getInstance().connect(mContext);
        }

    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        System.out.println("server timeout stop successfully :" + timeout);
    }

    private void differentiateType(String message) throws JSONException {
        String mapName;
        String taskName;
        switch (gsonUtils.getType(message)) {
            case Content.PING:
                EventBus.getDefault().post(new EventBusMessage(30000, message));
                break;
            case Content.STARTDOWN:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTDOWN, message));
                break;
            case Content.STARTUP:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTUP, message));
                break;
            case Content.STARTLEFT:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTLEFT, message));
                break;
            case Content.STARTRIGHT:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTRIGHT, message));
                break;
            case Content.STARTLIGHT:
                jsonObject = new JSONObject(message);
                int string = jsonObject.getInt(Content.SPINNERTIME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTLIGHT, string));
                break;
            case Content.STOPLIGHT:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPLIGHT, message));
                break;
            case Content.STOPUP:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPUP, message));
                break;
            case Content.STOPDOWN:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPDOWN, message));
                break;
            case Content.STOPLEFT:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPLEFT, message));
                break;
            case Content.STOPRIGHT:
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPRIGHT, message));
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
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETTASKQUEUE, mapName));
                break;
            case Content.GETMAPPIC://地图图片
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETMAPPIC, mapName));
                break;
            case Content.ADD_POSITION://添加点
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.ADD_POSITION, message));
                break;
            case Content.STARTTASKQUEUE://开始任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTTASKQUEUE, message));
                break;
            case Content.STOPTASKQUEUE://停止任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.STOPTASKQUEUE, message));
                break;
            case Content.START_SCAN_MAP://开始扫描地图
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.START_SCAN_MAP, mapName));
                break;
            case Content.USE_MAP://选定地图
                jsonObject = new JSONObject(message);
                Content.mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.USE_MAP, message));
                break;
            case Content.GETPOINTPOSITION://地图点数据
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GETPOINTPOSITION, mapName));
                break;
            case Content.CANCEL_SCAN_MAP://取消扫描并且保存地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.CANCEL_SCAN_MAP, message));
                break;
            case Content.DEVELOP_MAP://拓展地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DEVELOP_MAP, message));
                break;
            case Content.DELETE_MAP://删除地图
                jsonObject = new JSONObject(message);
                String deleteMapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DELETE_MAP, deleteMapName));
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
            case Content.GET_VIRTUAL://获取虚拟强数据
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_VIRTUAL, mapName));
                break;
            case Content.UPDATA_VIRTUAL://更新虚拟强
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPDATA_VIRTUAL, message));
                break;
            case Content.TASK_ALARM://定时任务
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.TASK_ALARM, message));
                break;
            case Content.RENAME_MAP://重命名地图
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.RENAME_MAP, message));
                break;
            case Content.SET_PLAYPATHSPEEDLEVEL://设置跑线速度
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SET_PLAYPATHSPEEDLEVEL, message));
                break;
            case Content.RENAME_POSITION://点重命名
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.RENAME_POSITION, message));
                break;
            case Content.GET_SPEED_LEVEL://获取导航速度
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_SPEED_LEVEL, message));
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
            case Content.SET_LOW_BATTERY://设置低电量回充
                jsonObject = new JSONObject(message);
                Content.battery = jsonObject.getInt(Content.SET_LOW_BATTERY);
                SharedPrefUtil.getInstance(mContext).setSharedPrefBattery(Content.SET_LOW_BATTERY, Content.battery);
                break;
            case Content.GET_LOW_BATTERY://获取低电量回冲
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_LOW_BATTERY, message));
                break;
            case Content.GET_VOICE_LEVEL://获取音量
                AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
                int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                gsonUtils.setVoice(current);
                broadcast(gsonUtils.putJsonMessage(Content.GET_VOICE_LEVEL));
                break;
            case Content.SET_VOICE_LEVEL://设置音量
                jsonObject = new JSONObject(message);
                int voice = jsonObject.getInt(Content.SET_VOICE_LEVEL);
                AudioManager mAudioManager1 = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
                mAudioManager1.setStreamVolume(AudioManager.STREAM_MUSIC,
                        voice,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;
            case Content.RESET_ROBOT://重置设置
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.RESET_ROBOT, message));
                break;
            case Content.GET_ULTRASONIC://声呐设备
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ULTRASONIC, message));
                break;
            case Content.VERSIONCODE://版本号
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.VERSIONCODE, message));
                break;
            case Content.WORKING_MODE://工作模式
                JSONObject jsonObject = new JSONObject(message);
                Content.Working_mode = new JSONObject(message).getInt(Content.WORKING_MODE);
                SharedPrefUtil.getInstance(mContext).setSharedPrefWorkingMode(Content.WORKING_MODE, Content.Working_mode);
                break;
            case Content.GET_WORKING_MODE://获取工作模式
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_WORKING_MODE, message));
                break;
            case Content.SET_CHARGING_MODE://有无充电桩模式
                Content.have_charging_mode = new JSONObject(message).getBoolean(Content.SET_CHARGING_MODE);
                SharedPrefUtil.getInstance(mContext).setSharedPrefChargingMode(Content.GET_CHARGING_MODE, Content.have_charging_mode);
                break;
            case Content.GET_CHARGING_MODE://有无充电桩模式
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_CHARGING_MODE, message));
                break;
            case Content.dbTotalCount://统计一共执行任务个数，时间，面积
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.dbTotalCount, message));
                break;
            case Content.dbCurrentCount://统计当月执行任务个数，时间，面积
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.dbCurrentCount, message));
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
            default:
                break;
        }

    }
}
