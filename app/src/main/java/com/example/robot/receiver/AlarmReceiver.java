package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.robot.R;
import com.example.robot.service.NavigationService;
import com.example.robot.service.SocketServices;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.AssestFile;
import com.example.robot.content.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.greenrobot.eventbus.EventBus;

public class AlarmReceiver extends BroadcastReceiver {
    private SqLiteOpenHelperUtils mSqLiteOpenHelperUtils;
    private Context mContext;
    private AlarmUtils mAlarmUtils;
    private AssestFile mAssestFile;
    private int RotateCount = 120;
    private CheckLztekLamp checkLztekLamp;
    private int week = 0;

    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if (Content.AlarmAction.equals(intent.getAction())) {
            mAssestFile = new AssestFile(context);
            checkLztekLamp = new CheckLztekLamp(context);
            mSqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(context);
            mAlarmUtils = new AlarmUtils(context);
            week = intent.getIntExtra("week", -1);
            handler.sendEmptyMessage(1000);
            // 可以继续设置下一次闹铃时间;
        }
    }

    public void getTaskQueue(){
        // 第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
        long time = System.currentTimeMillis();
        if (!Content.have_charging_mode && Content.isCharging) {
            EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.have_charging_mode)));
        } else {
            Cursor aTrue = mSqLiteOpenHelperUtils.searchAlarmTask(Content.dbAlarmIsRun, "true");
            while (aTrue.moveToNext()) {
                Log.d("AlarmReceiver ", mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        + ", taskName" + TextUtils.isEmpty(Content.taskName)
                        + ",  dbAlarmCycle :" + TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle))));
                mAssestFile.deepFile("dbAlarmTime " + mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        + ", taskName : " + TextUtils.isEmpty(Content.taskName)
                        + ",  dbAlarmCycle : " + TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle))));
                if (!TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && week == Integer.parseInt(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
                    Content.mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    //if (Content.isCharging || checkLztekLamp.getChargingGpio()) {
                        handler.sendEmptyMessageDelayed(1001, 0);
                        handler.sendEmptyMessageDelayed(1002, 2000);
//                    } else {
//                        handler.sendEmptyMessageDelayed(1002, 0);
//                    }
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
                    Content.mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    //if (Content.isCharging || checkLztekLamp.getChargingGpio()) {
                        handler.sendEmptyMessageDelayed(1001, 0);
                        handler.sendEmptyMessageDelayed(1002, 2000);
//                    } else {
//                        handler.sendEmptyMessageDelayed(1002, 0);
//                    }
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && "FF:FF".equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
                    Content.mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    //if (Content.isCharging || checkLztekLamp.getChargingGpio()) {
                        handler.sendEmptyMessageDelayed(1001, 0);
                        handler.sendEmptyMessageDelayed(1002, 2000);
//                    } else {
//                        handler.sendEmptyMessageDelayed(1002, 0);
//                    }
                }
            }
            mSqLiteOpenHelperUtils.close();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                getTaskQueue();
            } else if (msg.what == 1001) {
                NavigationService.move(0.2f, 0.0f);
                handler.sendEmptyMessageDelayed(1001, 20);
            } else if (msg.what == 1002) {
                handler.removeMessages(1001);
                handler.removeMessages(1002);
                Content.taskIndex = 0;
                TaskManager.getInstances(mContext).use_map(Content.mapName);
                handler.post(runnable);
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("ALARMreceiver :", "" + Content.is_initialize_finished + " ,  " + Content.mapName + " , " + Content.taskName);
            if (Content.is_initialize_finished == 1) {
                RotateCount = 120;
                handler.removeCallbacks(this::run);
                if (Content.taskName != null && Content.mapName != null) {
                    if (SocketServices.battery < Content.battery) {
                        Content.robotState = 6;
                        Content.time = 4000;
                        GsonUtils gsonUtils = new GsonUtils();
                        gsonUtils.setTvTime("电量回充,不能开始任务");
                        mAssestFile.deepFile("电量回充,不能开始任务 : 当前：" + SocketServices.battery + " , 回充： " + Content.battery);
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                        }
                        if (Content.have_charging_mode && !Content.isCharging) {
                            TaskManager.getInstances(mContext).navigate_Position(Content.mapName, Content.CHARGING_POINT);
                        } else if (!Content.have_charging_mode){
                            TaskManager.getInstances(mContext).navigate_Position(Content.mapName, Content.InitializePositionName);
                        }
                        Content.taskName = null;
                        Content.taskIndex = -1;
                    } else {
                        TaskManager.getInstances(mContext).startTaskQueue(Content.mapName, Content.taskName, 0);
                    }
                } else {
                    Log.d("ALARMreceiver :", " ,  " + Content.mapName + " , " + Content.taskName);
                }
            } else if (Content.is_initialize_finished == 0) {
                if (RotateCount >= 0) {
                    RotateCount --;
                    handler.postDelayed(runnable, 1000);
                } else {
                    RotateCount = 120;
                    handler.removeCallbacks(this::run);
                    Content.taskIndex = 0;
                    TaskManager.getInstances(mContext).use_map(Content.mapName);
                    handler.post(this::run);
                }
            } else if (Content.is_initialize_finished == 2) {
                Content.taskName = null;
                RotateCount = 120;
                Content.taskIndex = -1;
                handler.removeCallbacks(this::run);
            } else if (Content.is_initialize_finished == -1) {
                RotateCount = 120;
                Content.taskIndex = -1;
                handler.removeCallbacks(this::run);
            }
        }
    };
}
