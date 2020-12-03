package com.example.robot.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.robot.receiver.AlarmReceiver;

public class AlarmUtils {

    private Context mContext;
    private AlarmManager mAlarmManager;

    public AlarmUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void setAlarmTime(String taskName, long triggerAtMillis, long interval) {
        Log.d("AlarmReceiver", "开启定时任务 ：" + triggerAtMillis);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("android.alarm.task.action");
        intent.putExtra("mapName", Content.mapName);
        intent.putExtra("taskName", taskName);
        intent.setClass(mContext, AlarmReceiver.class);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播
        mAlarmManager.setRepeating(AlarmManager.RTC, triggerAtMillis, interval, mPendingIntent);
//        am.set(AlarmManager.RTC, triggerAtMillis, sender);
    }
}
