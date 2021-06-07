package com.retron.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.retron.robot.service.SocketServices;
import com.retron.robot.utils.AlarmUtils;

public class BootCompletedReceiver extends BroadcastReceiver {
    private AlarmUtils mAlarmUtils;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentServer = new Intent(context, SocketServices.class);
        context.startService(intentServer);
    }
}
