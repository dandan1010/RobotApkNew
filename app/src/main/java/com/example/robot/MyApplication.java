package com.example.robot;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.robot.service.NavigationService;
import com.example.robot.service.SimpleServer;
import com.example.robot.utils.Content;
import com.example.robot.uvclamp.CheckLztekLamp;

import java.net.InetSocketAddress;

public class MyApplication extends Application {

    private NavigationService navigationService;
    private Intent intentService;

    private CheckLztekLamp checkLztekLamp;

    @Override
    public void onCreate() {
        super.onCreate();
        checkLztekLamp = new CheckLztekLamp(this);
        Content.robotState = 1;
        Content.time = 4000;
        checkLztekLamp.startCheckSensorAtTime();
        checkLztekLamp.startLedLamp();
        checkLztekLamp.openEth();
        checkLztekLamp.setEthAddress();

        handler.sendEmptyMessage(1);
        thread.start();

    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.d("zdzd : " , "getEthEnable " + checkLztekLamp.getEthEnable());
                if (checkLztekLamp.getEthEnable()) {
                    navigationService = new NavigationService();
                    intentService = new Intent(getApplicationContext(), NavigationService.class);
                    startService(intentService);
                } else {
                    handler.sendEmptyMessageDelayed(1,1000);
                }
            }
        }
    };
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            intentService = new Intent(getApplicationContext(), NavigationService.class);
//            startService(intentService);
//            handler.sendEmptyMessage(1);
//        }
//    };

    Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            String host = Content.ip;
            int port = Content.port;
            Content.server = new SimpleServer(new InetSocketAddress(host, port));
            Content.server.run();
        }
    };

}
