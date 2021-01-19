package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.example.robot.activity.RobotDetailActivity;
import com.example.robot.service.SocketServices;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;

public class BootCompletedReceiver extends BroadcastReceiver {
    private AlarmUtils mAlarmUtils;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompletedReceiver", "开机启动q1111" + intent.getAction());
        Intent intentServer = new Intent(context, SocketServices.class);
        context.startService(intentServer);
        mAlarmUtils = new AlarmUtils(context);
        mAlarmUtils.setAlarmTime(System.currentTimeMillis(), 60 * 1000, Content.AlarmAction);
        Content.battery = SharedPrefUtil.getInstance(context).getSharedPrefBattery(Content.SET_LOW_BATTERY);
        Content.led = SharedPrefUtil.getInstance(context).getSharedPrefLed(Content.SET_LED_LEVEL);
    }
}
