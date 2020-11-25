package com.example.robot;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.robot.service.NavigationService;
import com.example.robot.service.SimpleServer;
import com.example.robot.utils.Content;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MyApplication extends Application {

    private NavigationService navigationService;
    private Intent intentService;

    @Override
    public void onCreate() {
        super.onCreate();

        navigationService = new NavigationService();
        intentService = new Intent(this, NavigationService.class);
        startService(intentService);

        new Thread() {
            @Override
            public void run() {
                super.run();
                String host = "10.7.5.176";
                int port = 8887;
                Content.server = new SimpleServer(new InetSocketAddress(host, port));
                Content.server.run();
            }
        }.start();

        CheckLztekLamp checkLztekLamp = new CheckLztekLamp(this);
        Content.robotState = 1;
        Content.time = 4000;
        checkLztekLamp.startCheckSensorAtTime();
        checkLztekLamp.startLedLamp();
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                Content.server.run();
                Log.d("zdzd", "application on create333");
            }
        }
    };

}
