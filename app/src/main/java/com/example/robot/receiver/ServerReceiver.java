package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.robot.service.SocketServices;

public class ServerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.android.robot.server.start".equals(intent.getAction())){
            Intent intentServer = new Intent(context, SocketServices.class);
            context.startService(intentServer);
        } else if ("com.android.robot.server.stop".equals(intent.getAction())){
            Intent intentServer = new Intent(context, SocketServices.class);
            context.stopService(intentServer);
        }
    }
}
