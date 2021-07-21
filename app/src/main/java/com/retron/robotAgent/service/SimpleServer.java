package com.retron.robotAgent.service;

import android.content.Context;
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

public class SimpleServer extends WebSocketServer {
    public static final String TAG = "SimpleServer";

    private String address = "";
    private Context mContext;
    public WebSocket conn;

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
                    Log.d(TAG, "instance thread run");
                    Content.server = new SimpleServer(new InetSocketAddress(Content.ip, Content.port), context);
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
        System.out.println("server started successfully");
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        super.stop();
        System.out.println("server stop successfully");
        if (Content.server != null) {
            Content.server = null;
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
