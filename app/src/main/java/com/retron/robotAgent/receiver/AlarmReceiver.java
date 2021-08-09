package com.retron.robotAgent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.retron.robotAgent.content.BaseEvent;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.utils.EventBusMessage;

import org.greenrobot.eventbus.EventBus;

public class AlarmReceiver extends BroadcastReceiver {
    private int week = 0;

    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver ", "收到alarm ：" + System.currentTimeMillis() + "------ACTION : " + intent.getAction());
        if (Content.AlarmAction.equals(intent.getAction())) {
            week = intent.getIntExtra("week", -1);
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ALARM_CODE, week));
        } else if (Content.DeleteFileAlarmAction.equals(intent.getAction())) {
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.DELETE_FILE_ALARM_CODE, ""));
        }
        Log.d("AlarmReceiver ", "结束alarm ：" + System.currentTimeMillis());
    }
}
