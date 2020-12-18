package com.example.robot.service;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.robot.task.TaskManager;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.utils.ServerConnoct;
import com.example.robot.utils.SharedPrefUtil;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PipedReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class SimpleServer extends WebSocketServer {
    private GsonUtils gsonUtils;
    private JSONObject jsonObject;
    private String address = "";
    private Context mContext;

    private SimpleServer(InetSocketAddress address, Context mContext) {
        super(address);
        this.mContext = mContext;
    }

    /**
     * 获取唯一的instance
     */
    public static SimpleServer getInstance(Context context) {
        if (Content.server == null) {
            synchronized (SimpleServer.class) {
                if (Content.server == null) {
                    String host = Content.ip;
                    int port = Content.port;
                    Content.server = new SimpleServer(new InetSocketAddress(host, port), context);
                }
            }
        }
        return Content.server;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
//        conn.send("OK conn");
        InetSocketAddress remoteSocketAddress = conn.getRemoteSocketAddress();
        address = remoteSocketAddress.getHostName();
        Log.d("zdzd_server", address);
//        if (TextUtils.isEmpty(Content.CONNECT_ADDRESS)) {
            broadcast(gsonUtils.putConnMsg(Content.CONN_OK)); //This method sends a message to all clients connected
            System.out.println("new connection to " + conn.getRemoteSocketAddress());
            Content.CONNECT_ADDRESS = remoteSocketAddress.getHostName();
            Log.d("zdzd_server", "连接的地址open：" + Content.CONNECT_ADDRESS);
//        } else {
//            gsonUtils.setMapName(Content.CONNECT_ADDRESS);
//            conn.send(gsonUtils.putConnMsg(Content.NO_CONN));
//        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        InetSocketAddress remoteSocketAddress = conn.getRemoteSocketAddress();
        Log.d("zdzd_server", "close : " + remoteSocketAddress.getHostName());
        if (Content.CONNECT_ADDRESS.equals(remoteSocketAddress.getHostName())) {
            Content.CONNECT_ADDRESS = null;
            Log.d("zdzd_server", "连接的地址close：" + Content.CONNECT_ADDRESS);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message from " + conn.getRemoteSocketAddress());
        Log.d("zdzd : ", "收到信息 ： " + message);
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
        ServerConnoct.getInstance().connect(mContext);
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
        ServerConnoct.getInstance().connect(mContext);
    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        System.out.println("server timeout stop successfully :" + timeout);
        //ServerConnoct.getInstance().connect(mContext);
    }

    private void differentiateType(String message) throws JSONException {
        String mapName;
        String taskName;
        switch (gsonUtils.getType(message)) {
            case Content.STARTDOWN:
                EventBus.getDefault().post(new EventBusMessage(10001, message));
                break;
            case Content.STARTUP:
                EventBus.getDefault().post(new EventBusMessage(10002, message));
                break;
            case Content.STARTLEFT:
                EventBus.getDefault().post(new EventBusMessage(10003, message));
                break;
            case Content.STARTRIGHT:
                EventBus.getDefault().post(new EventBusMessage(10004, message));
                break;
            case Content.STARTLIGHT:
                jsonObject = new JSONObject(message);
                int string = jsonObject.getInt(Content.SPINNERTIME);
                EventBus.getDefault().post(new EventBusMessage(10005, string));
                break;
            case Content.STOPLIGHT:
                EventBus.getDefault().post(new EventBusMessage(10006, message));
                break;
            case Content.STOPUP:
                EventBus.getDefault().post(new EventBusMessage(10007, message));
                break;
            case Content.STOPDOWN:
                EventBus.getDefault().post(new EventBusMessage(10008, message));
                break;
            case Content.STOPLEFT:
                EventBus.getDefault().post(new EventBusMessage(10009, message));
                break;
            case Content.STOPRIGHT:
                EventBus.getDefault().post(new EventBusMessage(10010, message));
                break;
            case Content.GETMAPLIST://地图列表
                EventBus.getDefault().post(new EventBusMessage(10011, message));
                break;
            case Content.SAVETASKQUEUE://存储任务
                EventBus.getDefault().post(new EventBusMessage(10013, message));
                break;
            case Content.DELETETASKQUEUE://删除任务
                EventBus.getDefault().post(new EventBusMessage(10014, message));
                break;
            case Content.GETTASKQUEUE://任务列表
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(10015, mapName));
                break;
            case Content.GETMAPPIC://地图图片
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(10019, mapName));
                break;
            case Content.ADD_POSITION://添加点
                EventBus.getDefault().post(new EventBusMessage(10021, message));
                break;
            case Content.STARTTASKQUEUE://开始任务
                EventBus.getDefault().post(new EventBusMessage(10022, message));
                break;
            case Content.STOPTASKQUEUE://停止任务
                EventBus.getDefault().post(new EventBusMessage(10023, message));
                break;
            case Content.START_SCAN_MAP://开始扫描地图
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(10025, mapName));
                break;
            case Content.USE_MAP://选定地图
                jsonObject = new JSONObject(message);
                Content.mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(10026, message));
                break;
            case Content.GETPOINTPOSITION://地图点数据
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(10028, mapName));
                break;
            case Content.CANCEL_SCAN_MAP://取消扫描并且保存地图
                EventBus.getDefault().post(new EventBusMessage(10029, message));
                break;
            case Content.DEVELOP_MAP://拓展地图
                EventBus.getDefault().post(new EventBusMessage(10030, message));
                break;
            case Content.DELETE_MAP://删除地图
                jsonObject = new JSONObject(message);
                String deleteMapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(10031, deleteMapName));
                break;
            case Content.DELETE_POSITION://删除点
                EventBus.getDefault().post(new EventBusMessage(10032, message));
                break;
            case Content.CANCEL_SCAN_MAP_NO://取消扫描不保存地图
                EventBus.getDefault().post(new EventBusMessage(10036, message));
                break;
            case Content.ROBOT_TASK_HISTORY://任务历史
                EventBus.getDefault().post(new EventBusMessage(10039, message));
                break;
            case Content.GET_VIRTUAL://获取虚拟强数据
                jsonObject = new JSONObject(message);
                mapName = jsonObject.getString(Content.MAP_NAME);
                EventBus.getDefault().post(new EventBusMessage(10041, mapName));
                break;
            case Content.UPDATA_VIRTUAL://更新虚拟强
                EventBus.getDefault().post(new EventBusMessage(10043, message));
                break;
            case Content.TASK_ALARM://定时任务
                EventBus.getDefault().post(new EventBusMessage(10044, message));
                break;
            case Content.RENAME_MAP://重命名地图
                EventBus.getDefault().post(new EventBusMessage(10045, message));
                break;
            case Content.SET_SPEED_LEVEL://设置导航速度
                jsonObject = new JSONObject(message);
                int level = jsonObject.getInt(Content.SET_SPEED_LEVEL);
                EventBus.getDefault().post(new EventBusMessage(10046, level));
                SharedPrefUtil.getInstance(mContext).setSharedPrefSpeed(Content.SET_SPEED_LEVEL, level);
                break;
            case Content.RENAME_POSITION://点重命名
                EventBus.getDefault().post(new EventBusMessage(10047, message));
                break;
            case Content.GET_SPEED_LEVEL://获取导航速度
                EventBus.getDefault().post(new EventBusMessage(10048, message));
                break;
            case Content.ADD_POWER_POINT://添加充电点
                EventBus.getDefault().post(new EventBusMessage(10050, message));
                break;
            case Content.EDITTASKQUEUE://编辑任务
                EventBus.getDefault().post(new EventBusMessage(10051, message));
                break;
            case Content.SYSTEM_DATE://设置系统时间
                long date = new JSONObject(Content.SYSTEM_DATE).getLong(Content.SYSTEM_DATE);
                EventBus.getDefault().post(new EventBusMessage(10052, date));
                break;
            case Content.GET_TASK_STATE://是否有任务正在执行
                EventBus.getDefault().post(new EventBusMessage(10053, message));
                break;
            case Content.SET_LED_LEVEL://设置led亮度
                int led_level = new JSONObject(message).getInt(Content.SET_LED_LEVEL);
                Log.d("level ", ""+led_level);
                SharedPrefUtil.getInstance(mContext).setSharedPrefLed(Content.SET_LED_LEVEL, led_level);
                break;
            case Content.GET_LED_LEVEL://获取led亮度
                EventBus.getDefault().post(new EventBusMessage(10055, message));
                break;
            case Content.SET_LOW_BATTERY://设置低电量回充
                jsonObject = new JSONObject(message);
                Content.battery = jsonObject.getInt(Content.SET_LOW_BATTERY);
                Log.d("battery " , ""+Content.battery);
                SharedPrefUtil.getInstance(mContext).setSharedPrefBattery(Content.SET_LOW_BATTERY, Content.battery);
                break;
            case Content.GET_LOW_BATTERY://获取低电量回冲
                EventBus.getDefault().post(new EventBusMessage(10056, message));
                break;
            case Content.GET_VOICE_LEVEL://获取音量
                AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
                int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
                int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
                Log.d("SYSTEM", "max : " + max + " current : " + current);
                gsonUtils.setVoice(current);
                broadcast(gsonUtils.putJsonMessage(Content.GET_VOICE_LEVEL));
                break;
            case Content.SET_VOICE_LEVEL://设置音量
                jsonObject = new JSONObject(message);
                int voice = jsonObject.getInt(Content.SET_VOICE_LEVEL);
                Log.d("voice " , ""+voice);
                AudioManager mAudioManager1 = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
                mAudioManager1.setStreamVolume(AudioManager.STREAM_MUSIC,
                        voice,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                break;
            case Content.RESET_ROBOT://重置设置
                EventBus.getDefault().post(new EventBusMessage(10057, message));
                break;
            case Content.GET_ULTRASONIC://声呐设备
                EventBus.getDefault().post(new EventBusMessage(10058, message));
                break;
            case Content.versionCode://版本号
                EventBus.getDefault().post(new EventBusMessage(10059, message));
                break;


//test request:
            case Content.TEST_UVCSTART_1:
                EventBus.getDefault().post(new EventBusMessage(20001, message));
                break;
            case Content.TEST_UVCSTART_2:
                EventBus.getDefault().post(new EventBusMessage(20002, message));
                break;
            case Content.TEST_UVCSTART_3:
                EventBus.getDefault().post(new EventBusMessage(20003, message));
                break;
            case Content.TEST_UVCSTART_4:
                EventBus.getDefault().post(new EventBusMessage(20004, message));
                break;

            case Content.TEST_UVCSTOP_1:
                EventBus.getDefault().post(new EventBusMessage(20005, message));
                break;
            case Content.TEST_UVCSTOP_2:
                EventBus.getDefault().post(new EventBusMessage(20006, message));
                break;
            case Content.TEST_UVCSTOP_3:
                EventBus.getDefault().post(new EventBusMessage(20007, message));
                break;
            case Content.TEST_UVCSTOP_4:
                EventBus.getDefault().post(new EventBusMessage(20008, message));
                break;

            case Content.TEST_UVCSTART_ALL:
                EventBus.getDefault().post(new EventBusMessage(20009, message));
                break;

            case Content.TEST_UVCSTOP_ALL:
                EventBus.getDefault().post(new EventBusMessage(20010, message));
                break;
            case Content.TEST_LIGHTSTART:
                EventBus.getDefault().post(new EventBusMessage(20011, message));
                break;
            case Content.TEST_LIGHTSTOP:
                EventBus.getDefault().post(new EventBusMessage(20012, message));
                break;
            case Content.TEST_SENSOR:
                EventBus.getDefault().post(new EventBusMessage(20013, message));
                break;
            case Content.TEST_WARNINGSTART:
                EventBus.getDefault().post(new EventBusMessage(20014, message));
                break;
            case Content.TEST_WARNINGSTOP:
                EventBus.getDefault().post(new EventBusMessage(20015, message));
                break;
            default:
                break;
        }

    }
}
