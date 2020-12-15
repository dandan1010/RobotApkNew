package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.robot.service.SocketServices;
import com.example.robot.utils.EventBusMessage;

import org.greenrobot.eventbus.EventBus;

public class ServerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ServerReceiver", "升级: " + intent.getAction());
        if ("com.android.robot.server.start".equals(intent.getAction())){
            Intent intentServer = new Intent(context, SocketServices.class);
            context.startService(intentServer);
        } else if ("com.android.robot.server.stop".equals(intent.getAction())){
            Intent intentServer = new Intent(context, SocketServices.class);
            context.stopService(intentServer);
        } else if (intent.getAction() == "com.android.update") {
            EventBus.getDefault().post(new EventBusMessage(30001, ""));
        }
    }
}
