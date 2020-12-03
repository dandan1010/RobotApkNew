package com.example.robot.service;

import android.text.TextUtils;
import android.util.Log;

import com.example.robot.task.TaskManager;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;

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

    public SimpleServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
//        conn.send("OK conn");
        InetSocketAddress remoteSocketAddress = conn.getRemoteSocketAddress();
        address = remoteSocketAddress.getHostName();
        Log.d("zdzd_server", address);
        if (TextUtils.isEmpty(Content.CONNECT_ADDRESS)) {
            gsonUtils.setMapName(Content.mapName);
            gsonUtils.setTaskName(Content.taskName);
            broadcast(gsonUtils.putConnMsg(Content.CONN_OK)); //This method sends a message to all clients connected
            System.out.println("new connection to " + conn.getRemoteSocketAddress());
            Content.CONNECT_ADDRESS = remoteSocketAddress.getHostName();
            Log.d("zdzd_server" , "连接的地址open：" + Content.CONNECT_ADDRESS);
        } else {
            gsonUtils.setMapName(Content.CONNECT_ADDRESS);
            broadcast(gsonUtils.putConnMsg(Content.NO_CONN));
        }
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
    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        System.out.println("server timeout stop successfully :" + timeout);
    }

    private void differentiateType(String message) throws JSONException {
        Log.d("zdzd ", "getType :  " + gsonUtils.getType(message));
        String mapName;
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
                EventBus.getDefault().post(new EventBusMessage(10015, message));
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
                jsonObject = new JSONObject(message);
                Content.taskName = jsonObject.getString(Content.TASK_NAME);
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
                EventBus.getDefault().post(new EventBusMessage(10028, message));
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
                jsonObject = new JSONObject(message);
                EventBus.getDefault().post(new EventBusMessage(10032, jsonObject.getString(Content.POINT_NAME)));
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


//test request:
            case Content.TEST_UVCSTART:
                EventBus.getDefault().post(new EventBusMessage(20001, message));
                break;
            case Content.TEST_UVCSTOP:
                EventBus.getDefault().post(new EventBusMessage(20002, message));
                break;
            case Content.TEST_LIGHTSTART:
                EventBus.getDefault().post(new EventBusMessage(20003, message));
                break;
            case Content.TEST_LIGHTSTOP:
                EventBus.getDefault().post(new EventBusMessage(20004, message));
                break;
            case Content.TEST_SENSOR:
                EventBus.getDefault().post(new EventBusMessage(20005, message));
                break;
            case Content.TEST_WARINGSTART:
                EventBus.getDefault().post(new EventBusMessage(20006, message));
                break;
            case Content.TEST_WARINGSTOP:
                EventBus.getDefault().post(new EventBusMessage(20007, message));
                break;
            default:
                break;
        }

    }
}
