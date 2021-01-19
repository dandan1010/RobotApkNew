package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;

import com.example.robot.bean.TaskBean;
import com.example.robot.service.SocketServices;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;

public class ServerReceiver extends BroadcastReceiver {
    private AlarmUtils mAlarmUtils;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ServerReceiver", "升级: " + intent.getAction());
        if ("com.android.robot.server.start".equals(intent.getAction())){
            Content.isUpdate = false;
            Intent intentServer = new Intent(context, SocketServices.class);
            context.startService(intentServer);
            mAlarmUtils = new AlarmUtils(context);
            mAlarmUtils.setAlarmTime(System.currentTimeMillis(), 60 * 1000, Content.AlarmAction);
            Content.battery = SharedPrefUtil.getInstance(context).getSharedPrefBattery(Content.SET_LOW_BATTERY);
            Content.led = SharedPrefUtil.getInstance(context).getSharedPrefLed(Content.SET_LED_LEVEL);
            SqLiteOpenHelperUtils sqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(context);
            Cursor cursor = sqLiteOpenHelperUtils.searchTaskHistory();
            while (cursor.moveToLast()) {
                String substring = cursor.getString(cursor.getColumnIndex(Content.dbTime)).substring(0, 2);
                if (!Content.isCharging && "-1".equals(substring)) {
                    TaskManager.getInstances(context).navigate_Position(
                            cursor.getString(cursor.getColumnIndex(Content.dbTaskMapName)),
                            Content.CHARGING_POINT
                    );
                }
            }
        } else if ("com.android.robot.server.stop".equals(intent.getAction())){
            Intent intentServer = new Intent(context, SocketServices.class);
            context.stopService(intentServer);
        }
    }
}
