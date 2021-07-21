package com.retron.robotAgent.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.retron.robotAgent.content.BaseEvent;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.utils.EventBusMessage;
import com.retron.robotAgent.utils.GsonUtils;
import com.retron.robotAgent.utils.HandleMessage;
import com.retron.robotAgent.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class MqttWebSocketServer extends WebSocketServer {
    public static final String TAG = "SimpleServer";
    private String address = "";
    private Context mContext;
    public WebSocket conn;

    private MqttWebSocketServer(InetSocketAddress address, Context mContext) {
        super(address);
        this.mContext = mContext;
    }

    /**
     * 获取唯一的instance
     */
    public static MqttWebSocketServer getMqttInstance(Context context) {
        if (Content.mqttServer == null) {
            synchronized (MqttWebSocketServer.class) {
                if (Content.mqttServer == null) {
                    Log.d(TAG, " getMqttInstance instance thread run");
                    Content.mqttServer = new MqttWebSocketServer(new InetSocketAddress(Content.locationIp, Content.port), context);
                    Content.mqttServer.start();
                }
            }
        }
        return Content.mqttServer;
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
        System.out.println("mqttServer received message: " + message);
        try {
            HandleMessage.getInstance(mContext).differentiateType(message);
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
        System.out.println("mqttserver started successfully");
        Intent intent = new Intent("com.android.mqtt.reconnect");
        intent.setPackage("com.retron.robotmqtt");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent. FLAG_INCLUDE_STOPPED_PACKAGES);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        super.stop();
        System.out.println("server stop successfully");
        if (Content.mqttServer != null) {
            Content.mqttServer = null;
        }
        if (!Content.isUpdate) {
            //ServerConnoct.getInstance().connect(mContext);
            EventBus.getDefault().post(new EventBusMessage(88888, ""));
        }

    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        System.out.println("server timeout stop successfully :" + timeout);
    }
}
