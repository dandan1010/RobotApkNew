package com.retron.robotAgent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.retron.robotAgent.service.SocketServices;
import com.retron.robotAgent.utils.AlarmUtils;
import com.retron.robotAgent.content.Content;

public class ServerReceiver extends BroadcastReceiver {
    private AlarmUtils mAlarmUtils;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ServerReceiver", "升级: " + intent.getAction());
        if ("com.android.robot.server.start".equals(intent.getAction())){
            Content.isUpdate = false;
            Intent intentServer = new Intent(context, SocketServices.class);
            context.startService(intentServer);
        } else if ("com.android.robot.server.stop".equals(intent.getAction())){
            Intent intentServer = new Intent(context, SocketServices.class);
            context.stopService(intentServer);
        }
    }
}
