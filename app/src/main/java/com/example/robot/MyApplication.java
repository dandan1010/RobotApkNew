package com.example.robot;

import android.app.Application;
import android.content.Intent;

import com.example.robot.service.NavigationService;
import com.example.robot.service.SimpleServer;
import com.example.robot.utils.Content;
import com.example.robot.utils.Utilities;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MyApplication extends Application {

    private WebSocketServer server = null;
    private NavigationService navigationService;
    private Intent intentService;

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread() {
            @Override
            public void run() {
                super.run();
                String host = "10.7.5.166";
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
        navigationService = new NavigationService();
        intentService = new Intent(this, NavigationService.class);
        startService(intentService);
    }

    public WebSocketServer getServer(){
        return server;
    }
}
