package com.dcm360.controller.gs.controller.service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class GsControllerWebSocket {

    private GsControllerWebSocket() {
    }

    private static class SingletonInstance {
        private static final GsControllerWebSocket INSTANCE = new GsControllerWebSocket();
    }

    public static GsControllerWebSocket getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public WebSocket connect_gs(String url, WebSocketListener webSocketListener) {
        OkHttpClient client = new Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).build();
        return client.newWebSocket(request, webSocketListener);
    }

}