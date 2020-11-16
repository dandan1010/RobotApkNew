package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.robot.task.TaskManager;

public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver","定时时间到，执行任务");
        if ("android.alarm.task.action".equals(intent.getAction())) {
            // 第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
            String mapName = intent.getStringExtra("mapName");
            String taskName = intent.getStringExtra("taskName");
            Log.d("AlarmReceiver","定时时间到，执行任务" + "mapNAME :" +mapName + ",   taskName : " + taskName);
            if (mapName != null && taskName != null) {
                TaskManager.getInstances(context).startTaskQueue(mapName, taskName);
            }

            // 可以继续设置下一次闹铃时间;
        }
    }
}
