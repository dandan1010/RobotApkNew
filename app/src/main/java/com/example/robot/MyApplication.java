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

    }
}
