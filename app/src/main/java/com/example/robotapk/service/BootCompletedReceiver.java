package com.example.robotapk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.robotapk.activity.MapManagerActivity;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompletedReceiver", "开机启动");
        //Intent intent1 = new Intent(context, MapManagerActivity.class);
        //context.startActivity(intent1);
    }
}
