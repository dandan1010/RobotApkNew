package com.example.robot;

import android.app.Application;

import com.example.robot.service.SimpleServer;
import com.example.robot.utils.Content;
import com.example.robot.utils.Utilities;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MyApplication extends Application {

    private WebSocketServer server = null;

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread() {
            @Override
            public void run() {
                super.run();
                String host = "10.7.5.8";
                int port = 8887;
                server = new SimpleServer(new InetSocketAddress(host, port));
                server.run();
            }
        }.start();

        CheckLztekLamp checkLztekLamp = new CheckLztekLamp(this);
        Content.robotState = 1;
        Content.time = 4000;
        checkLztekLamp.startCheckSensorAtTime();
        checkLztekLamp.startLedLamp();
        Utilities.exec("tcpip 5555");
    }

    public WebSocketServer getServer(){
        return server;
    }
}
