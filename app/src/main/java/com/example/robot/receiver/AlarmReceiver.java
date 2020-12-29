package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robot.service.NavigationService;
import com.example.robot.service.SocketServices;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private SqLiteOpenHelperUtils mSqLiteOpenHelperUtils;
    private Context mContext;
    private AlarmUtils mAlarmUtils;

    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if ("android.alarm.task.action".equals(intent.getAction())) {
            // 第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
            mSqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(context);
            mAlarmUtils = new AlarmUtils(context);
            String week = intent.getStringExtra("week");
            long time = System.currentTimeMillis();
            Cursor aTrue = mSqLiteOpenHelperUtils.searchAlarmTask(Content.dbAlarmIsRun, "true");
            Log.d("AlarmReceiver", "开启定时任务week ：" + week + ",   " + mAlarmUtils.getTimeMillis(time));
            Log.d("AlarmReceiver", "开启定时任务week ：" + aTrue.getCount());

            while (aTrue.moveToNext()) {
                Log.d("AlarmReceiver", "开启定时任务taskname ：" + Content.taskName);
                Log.d("AlarmReceiver", "开启定时任务dbAlarmCycle ：" + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)));
                Log.d("AlarmReceiver", "开启定时任务dbAlarmTime ：" + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)));
                Log.d("AlarmReceiver ", week.equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle))) + ",  "
                        + mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime))) + ", "
                        + TextUtils.isEmpty(Content.taskName) + ", "
                        + TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle))));
                Log.d("AlarmReceiver", "size : " + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)).length());
                if (week.equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
                    Content.mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    Log.d("Cursor 111 aTrue1111", aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)) + ",   " + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmIsRun)));
                    TaskManager.getInstances(context).use_map(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0]);
                    handler.post(runnable);
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
                    Content.mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    Log.d("Cursor 222 aTrue1111", aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)) + ",   " + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmIsRun)));
                    TaskManager.getInstances(context).use_map(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0]);
                    handler.post(runnable);
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && "FF:FF".equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
                    Content.mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    Log.d("Cursor 333 aTrue1111", aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)) + ",   " + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmIsRun)));
                    TaskManager.getInstances(context).use_map(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0]);
                    handler.post(runnable);
                }
            }
            mSqLiteOpenHelperUtils.close();
            // 可以继续设置下一次闹铃时间;
        }

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("ALARMreceiver :", "" + Content.is_initialize_finished + " ,  " + Content.mapName + " , " + Content.taskName);
            Log.d("alarm path", Environment.getExternalStorageDirectory().getPath()
                    + "/com.example.robot" +"/update.apk");
            if (Content.is_initialize_finished == 1) {
                handler.removeCallbacks(this::run);
                if (Content.taskName != null && Content.mapName != null) {
                    Log.d("ALARMreceiver :", "" + SocketServices.battery + " ,  " + Content.battery);
                    if (SocketServices.battery < Content.battery) {
                        Content.robotState = 6;
                        Content.time = 4000;
                        GsonUtils gsonUtils = new GsonUtils();
                        gsonUtils.setTvTime("电量回充,不能开始任务");
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                        }
                        Content.mapName = null;
                        Content.taskName = null;
                    } else {
                        Log.d("ALARM starttask :",  Content.mapName + " , " + Content.taskName);
                        TaskManager.getInstances(mContext).startTaskQueue(Content.mapName, Content.taskName);
                    }
                } else {
                    Log.d("ALARMreceiver :", " ,  " + Content.mapName + " , " + Content.taskName);
                }
            } else if (Content.is_initialize_finished == 0){
                handler.postDelayed(runnable, 1000);
            } else if (Content.is_initialize_finished == 2){
                Content.mapName = null;
                Content.taskName = null;
            } else if (Content.is_initialize_finished == -1) {
                handler.removeCallbacks(this::run);
            }
        }
    };
}
