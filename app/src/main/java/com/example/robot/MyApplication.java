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

    @Override
    public void onCreate() {
        super.onCreate();

        CheckLztekLamp checkLztekLamp = new CheckLztekLamp(this);
        Content.robotState = 1;
        Content.time = 4000;
        checkLztekLamp.startCheckSensorAtTime();
        checkLztekLamp.startLedLamp();
        Utilities.exec("tcpip 5555");

    }

}
