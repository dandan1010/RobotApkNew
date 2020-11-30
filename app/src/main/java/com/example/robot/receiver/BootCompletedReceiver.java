package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.robot.activity.RobotDetailActivity;
import com.example.robot.service.SocketServices;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompletedReceiver", "开机启动");
        Intent intentServer = new Intent(context, SocketServices.class);
        context.startService(intentServer);
        Intent intent1 = new Intent(context, RobotDetailActivity.class);
        context.startActivity(intent1);
    }
}
