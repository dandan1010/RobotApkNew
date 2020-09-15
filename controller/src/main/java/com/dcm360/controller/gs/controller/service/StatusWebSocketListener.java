package com.dcm360.controller.gs.controller.service;

import android.os.SystemClock;
import android.util.Log;

import com.dcm360.controller.gs.controller.listener.StatusMessageListener;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author LiYan
 * @create 2019/5/10
 * @Describe
 */
public class StatusWebSocketListener extends WebSocketListener {
    private static final String TAG = "gs_status";

    private String mUrl = null;
    private StatusMessageListener listener;

    public StatusWebSocketListener(String url, StatusMessageListener listener) {
        mUrl = url;
        this.listener = listener;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
//        listener.connectStatus(true);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
//        listener.connectStatus(false);
        reConnect();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
//        listener.connectStatus(false);
        reConnect();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        this.listener.onMessage(text);
    }

    private void reConnect() {
        SystemClock.sleep(1000);
//        Log.d(TAG, "gs websocket reconnect:" + mUrl);
        GsControllerWebSocket.getInstance().connect_gs(mUrl, this);
    }


}
