package com.example.robot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.robot.utils.Content;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SocketServices extends Service {

    private NavigationService navigationService;
    private Intent intentService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //handler.sendEmptyMessage(1);
        Log.d("zdzd --- " , "server oncreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("zdzd --- " , "server onDestroy");
    }


}
