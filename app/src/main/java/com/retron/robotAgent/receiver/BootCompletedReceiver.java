package com.retron.robotAgent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.retron.robotAgent.service.SocketServices;
import com.retron.robotAgent.utils.AlarmUtils;

public class BootCompletedReceiver extends BroadcastReceiver {
    private AlarmUtils mAlarmUtils;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentServer = new Intent(context, SocketServices.class);
        context.startService(intentServer);
    }
}
