package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.robot.service.SocketServices;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.content.Content;
import com.example.robot.utils.SharedPrefUtil;

public class BootCompletedReceiver extends BroadcastReceiver {
    private AlarmUtils mAlarmUtils;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentServer = new Intent(context, SocketServices.class);
        context.startService(intentServer);
    }
}
