package com.example.robot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.dcm360.controller.robot_interface.bean.Status;
import com.example.robot.BuildConfig;
import com.example.robot.R;
import com.example.robot.bean.PointStateBean;
import com.example.robot.bean.RobotPointPositions;
import com.example.robot.bean.RunningTaskBean;
import com.example.robot.bean.SaveTaskBean;
import com.example.robot.bean.TaskBean;
import com.example.robot.broadcast.BroadCastUtils;
import com.example.robot.content.BaseEvent;
import com.example.robot.log.LogcatHelper;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.AssestFile;
import com.example.robot.content.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.utils.PropertyUtils;
import com.example.robot.utils.SharedPrefUtil;
import com.example.robot.utils.TimeUtils;
import com.example.robot.utils.VirtualBeanUtils;
import com.example.robot.uvclamp.CheckLztekLamp;
import com.example.robot.uvclamp.UvcWarning;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SocketServices extends BaseService {

    private static final String TAG = "SocketServices";
    private NavigationService navigationService;
    private Intent intentService;
    private Context mContext;
    private UvcWarning uvcWarning;
    public static CheckLztekLamp checkLztekLamp;
    private GsonUtils gsonUtils;
    private String[] spinner;
    public static boolean toLightControlBtn = false;
    private int ledtime = 0;
    private Long workTime;
    private String tvText;
    private TimeUtils mTimeUtils;
    public static MyHandler myHandler;
    private long pauseTime = 0;
    public static int battery = 0;
    private boolean isTaskFlag = false;
    private float x;
    private float y;
    private double angle;
    private String spinnerString;
    private boolean isDevelop = false;
    private boolean threadDestory = false;
    private VirtualBeanUtils mVirtualBeanUtils;
    private SqLiteOpenHelperUtils mSqLiteOpenHelperUtils;
    private AlarmUtils mAlarmUtils;
    private int spinnerIndex;
    private AssestFile assestFile;
    private String index = "0";
    private int RotateCount = 120;
    public static String use_mapName = "";
    private RobotMap robotMap;
    private int mapCount = 0;
    private ArrayList<RobotPointPositions> arrayList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogcatHelper.getInstance(mContext).getFileLength();
        LogcatHelper.getInstance(this).alarmDeleteFile(this);
        mContext = this;
        spinner = mContext.getResources().getStringArray(R.array.spinner_time);
        isNewSerialPort();
        //Log.d(TAG, "新的apk: " + BuildConfig.VERSION_NAME);
        handler.sendEmptyMessage(1);
        use_mapName = SharedPrefUtil.getInstance(mContext).getSharedPrefMapName(Content.MAP_NAME);
    }

    public void isNewSerialPort() {
        initView();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.d("c : ", "getEthEnable " + checkLztekLamp.getEthEnable());
                if (checkLztekLamp.getEthEnable()) {
                    navigationService = new NavigationService();
                    intentService = new Intent(getApplicationContext(), NavigationService.class);
                    startService(intentService);
                    myHandler.sendEmptyMessage(7);
                    myHandler.sendEmptyMessage(14);
                    threadDestory = true;

                    Cursor cursor = mSqLiteOpenHelperUtils.searchTaskIndex();
                    while (cursor.moveToNext()) {
                        String mapName = cursor.getString(cursor.getColumnIndex(Content.dbTaskMapName));
                        String taskName = cursor.getString(cursor.getColumnIndex(Content.dbTaskName));
                        index = cursor.getString(cursor.getColumnIndex(Content.dbTaskIndex));
                        Cursor cursorList = mSqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, mapName + "," + taskName);
                        if (Integer.parseInt(index) < cursorList.getCount() - 1) {
                            Log.d(TAG, "执行任务到第 " + (Integer.parseInt(index)) + "个任务");
                            Content.taskName = taskName;
                            Content.taskIndex = -1;
                            TaskManager.getInstances(mContext).use_map(use_mapName);
                            mSqLiteOpenHelperUtils.deleteHistory(cursor.getInt(cursor.getColumnIndex("_id")));
                            Cursor cursorTotal = mSqLiteOpenHelperUtils.searchTaskTotalCount();
                            long taskCount = 0, taskTime = 0, area = 0;
                            while (cursorTotal.moveToNext()) {
                                taskCount = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTaskTotalCount)));
                                taskTime = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTimeTotalCount)));
                                area = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbAreaTotalCount)));
                            }
                            mSqLiteOpenHelperUtils.reset_Db(Content.dbTotalCount);
                            mSqLiteOpenHelperUtils.saveTaskTotalCount((taskCount - 1) + "", taskTime + "", area + "");
                            mSqLiteOpenHelperUtils.close();
                            handler.post(runnable);
                        }
                    }

                } else {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };

    private void initView() {

        mAlarmUtils = new AlarmUtils(mContext);
        mAlarmUtils.setAlarmTime(System.currentTimeMillis(), 60 * 1000, Content.AlarmAction);
        Content.battery = SharedPrefUtil.getInstance(mContext).getSharedPrefBattery(Content.SET_LOW_BATTERY);
        Content.led = SharedPrefUtil.getInstance(mContext).getSharedPrefLed(Content.SET_LED_LEVEL);
        Content.Working_mode = SharedPrefUtil.getInstance(mContext).getSharedPrefWorkingMode(Content.WORKING_MODE);
        Content.have_charging_mode = SharedPrefUtil.getInstance(mContext).getSharedPrefChargingMode(Content.GET_CHARGING_MODE);

        uvcWarning = new UvcWarning(mContext);
        checkLztekLamp = new CheckLztekLamp(mContext);
        gsonUtils = new GsonUtils();
        myHandler = new MyHandler(SocketServices.this);
        checkLztekLamp.readBatteryFactory();
        if (!checkLztekLamp.getEthEnable()) {
            Log.d(TAG, "网络设置失败");
        } else {
            Log.d(TAG, "网络设置成功");
        }
        mTimeUtils = new TimeUtils(this);
        mVirtualBeanUtils = new VirtualBeanUtils(this);
        mSqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(this);
        mAlarmUtils = new AlarmUtils(this);
        assestFile = new AssestFile(mContext);
        assestFile.deleteUpdateFile();
        Content.robotState = 1;
        Content.time = 4000;
        checkLztekLamp.startCheckSensorAtTime();
        checkLztekLamp.startLedLamp();
        checkLztekLamp.initUvcMode();
        checkLztekLamp.setChargingGpio(1);
        Content.chargingState = 0;
        checkLztekLamp.getSpeed();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "ZDZD : " + Content.is_initialize_finished + " ,  " + use_mapName + " , " + Content.taskName);
            if (Content.is_initialize_finished == 1) {
                RotateCount = 120;
                handler.removeCallbacks(this::run);
                if (Content.taskName != null && use_mapName != null) {
                    if (SocketServices.battery < Content.battery) {
                        Content.robotState = 6;
                        Content.time = 4000;
                        GsonUtils gsonUtils = new GsonUtils();
                        gsonUtils.setTvTime("电量回充,不能开始任务");
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                        }
                        if (Content.have_charging_mode && !Content.isCharging) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.CHARGING_POINT);
                        } else if (!Content.have_charging_mode) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.InitializePositionName);
                        }
                        Content.taskName = null;
                        Content.taskIndex = -1;
                    } else {
                        Log.d(TAG, "TASK INDEX111 = " + Content.taskIndex);
                        TaskManager.getInstances(mContext).startTaskQueue(use_mapName, Content.taskName, Integer.parseInt(index));
                    }
                } else {
                    Log.d(TAG, " ,  " + use_mapName + " , " + Content.taskName);
                }
            } else if (Content.is_initialize_finished == 0) {
                if (RotateCount >= 0) {
                    RotateCount--;
                    handler.postDelayed(runnable, 1000);
                } else {
                    RotateCount = 120;
                    handler.removeCallbacks(this::run);
                    TaskManager.getInstances(mContext).use_map(use_mapName);
                    handler.post(this::run);
                }
            } else if (Content.is_initialize_finished == 2) {
                Content.taskName = null;
                RotateCount = 120;
                handler.removeCallbacks(this::run);
            } else if (Content.is_initialize_finished == -1) {
                RotateCount = 120;
                handler.removeCallbacks(this::run);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "server onDestroy");
//        unregisterReceiver(broadcastReceiver);
        stopService(intentService);
    }

    public void onCheckedChanged(int index) {
        Log.d(TAG, "onCheckedChanged : " + toLightControlBtn + ", spinner index: " + index);
        if (toLightControlBtn) {
            Content.completeFlag = false;
            ledtime = 0;
            uvcWarning.startWarning();
            Content.robotState = 5;
            Content.time = 1000;
            spinnerIndex = index;
            myHandler.sendEmptyMessageDelayed(1, 0);
        } else {
            ledtime = 0;
            Content.robotState = 1;
            Content.time = 4000;
            uvcWarning.stopWarning();
            checkLztekLamp.setUvcMode(1);
//            tvText = mTimeUtils.calculateDays(System.currentTimeMillis());
//            gsonUtils.setTvTime(tvText);
            Log.d(TAG, "case 2 click " + tvText);
//            if (Content.server != null) {
//                Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
//            }
        }
    }

    public class MyHandler extends Handler {//防止内存泄漏
        //持有弱引用MainActivity,GC回收时会被回收掉.
        private final WeakReference<SocketServices> mAct;

        private MyHandler(SocketServices socketServices) {
            mAct = new WeakReference<SocketServices>(socketServices);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "case 1  " + workTime);
                    startLoopDetection();
                    break;
                case 2:
                    Log.d(TAG, "case 2  " + Content.completeFlag + ", worktime : " + workTime + ",  countTime : " + Content.countTime);
                    int time = (int) ((workTime * 1000 - Content.countTime * Content.delayTime) / 1000);
                    if (time < 0) {
                        Content.completeFlag = true;
                    }
                    if (!Content.completeFlag) {
                        startUvcDetection();
                        tvText = time + "";
                        Log.d(TAG, "case 2  " + tvText + " , WORKTIME :" + workTime);
                        if (Content.taskIndex >= 0) {
                            TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount(tvText);
                            EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                        }

                        Log.d("zdzd prop111 : ", "" + PropertyUtils.getProperty(Content.isRobotAngularSpeed, "false"));
                        if ("true".equals(PropertyUtils.getProperty(Content.isRobotAngularSpeed, "false"))) {
                            NavigationService.move(0, 0.2f);
                        }
                    } else {
                        Content.taskIsFinish = false;
                        toLightControlBtn = false;
                        onCheckedChanged(0);
                        checkLztekLamp.setUvcMode(1);
                        Content.robotState = 1;
                        Content.time = 4000;
                        Content.completeFlag = false;
                    }
                    break;
                case 3:
                    Log.d(TAG, "case 3  " + workTime);
                    if (Content.taskIndex >= 0) {
                        TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount(tvText);
                        EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                    }
                    startUvcDetection();
                    break;
                case 4:
                    Log.d(TAG, "case 4  " + workTime);
//                    gsonUtils.setTvTime("电量回充,消毒未完成");
//                    if (Content.server != null) {
//                        Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
//                    }
                    TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount("电量回充,消毒未完成");
                    EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                    checkLztekLamp.setUvcMode(1);
                    uvcWarning.stopWarning();
                    toLightControlBtn = false;
                    onCheckedChanged(0);
                    Content.robotState = 6;
                    Content.time = 4000;
//                    if (Content.taskState != 2) {
//                        TaskManager.getInstances(mContext).pauseTaskQueue();
//                    }
                    if (Content.have_charging_mode) {
                        TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.CHARGING_POINT);
                    } else {
                        TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.InitializePositionName);
                    }
                    myHandler.sendEmptyMessageDelayed(6, 5000);
                    myHandler.removeMessages(9);
                    break;
                case 5:
                    if (/*checkLztekLamp.getGpioSensorState()||*/  Content.EMERGENCY
                            || Content.robotState == 6 || Content.robotState == 4) {
                        checkLztekLamp.setUvcModeForDemo(1);
                    } else {
                        if (battery > Content.battery) {
                            checkLztekLamp.setUvcModeForDemo(0);
                        }
                    }
                    myHandler.sendEmptyMessageDelayed(5, 1000);
                    break;
                case 6:
                    Log.d(TAG, "case 6 : " + "检测充电状态恢复任务battery : " + battery + ",  taskName: " + Content.taskName + ",   taskState: " + Content.taskState);
                    TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount("电量回充,消毒未完成");
                    EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                    if (battery > Content.maxBattery && Content.taskName != null) {
                        //TaskManager.getInstances(mContext).resumeTaskQueue();
                        Content.taskIsFinish = false;
                        Content.robotState = 1;
                        Content.time = 4000;
                        myHandler.sendEmptyMessage(9);
                        myHandler.removeMessages(6);
                    } else if (Content.taskName == null) {
                        myHandler.removeMessages(6);
                    } else {
                        myHandler.sendEmptyMessageDelayed(6, 5000);
                    }
                    break;
                case 7:
                    Log.d(TAG, "case 7  " + "设备信息");
                    TaskManager.getInstances(mContext).deviceStatus();
                    myHandler.sendEmptyMessageDelayed(7, 1000);
                    break;
                case 8:

                    break;
                case 9:
                    Log.d(TAG, "case 9  " + Content.Sum_Time);
                    gsonUtils.setTvTime(Content.Sum_Time + "");
                    if (Content.server != null) {
                        Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                    }
                    if (pauseTime == 0 && !isTaskFlag && Content.Sum_Time >= 0) {
                        Content.Sum_Time = Content.Sum_Time - 1;
                        myHandler.sendEmptyMessageDelayed(9, 1000);
                    } else if (Content.Sum_Time < 0) {
                        myHandler.removeMessages(9);
                    } else {
                        myHandler.sendEmptyMessageDelayed(9, 1000);
                    }
                    break;
                case 10:
                    Content.CONNECT_ADDRESS = "";
                    TaskManager.getInstances(mContext).cancleScanMap();
                    break;
                case 11://定时任务
                    getTaskQueue(msg.arg1);
                    break;
                case 12:
                    NavigationService.move(0.2f, 0.0f);
                    myHandler.sendEmptyMessageDelayed(12, 20);
                    break;
                case 13:
                    myHandler.removeMessages(12);
                    myHandler.removeMessages(13);
                    Content.taskIndex = 0;
                    TaskManager.getInstances(mContext).use_map(use_mapName);
                    myHandler.post(alarmRunnable);
                    break;
                case 14:
                    if (Content.server != null) {
                        if (Content.isCharging) {
                            Content.server.broadcast(gsonUtils.getRobotStatus(Content.STATUS, "Charging at " + Content.CHARGING_POINT));
                        } else if (Content.is_initialize_finished == 0) {
                            Content.server.broadcast(gsonUtils.getRobotStatus(Content.STATUS, "Initialize"));
                        } else if (!Content.isCharging && Content.taskName == null) {
                            Content.server.broadcast(gsonUtils.getRobotStatus(Content.STATUS, "Standby"));
                        } else {
                            Content.server.broadcast(gsonUtils.getRobotStatus(Content.STATUS, Content.robotStatus));
                        }
                    }
                    myHandler.sendEmptyMessageDelayed(14, 1000);
                    break;
                case 15:
                    Content.is_initialize_finished = 2;
                    handlerInitialize.removeCallbacks(runnableInitialize);
                    myHandler.removeMessages(15);
                    EventBus.getDefault().post(new EventBusMessage(10000, Content.initialize_fail));
                    break;
                default:
                    break;
            }

        }
    }

    Runnable alarmRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("ALARMreceiver :", "" + Content.is_initialize_finished + " ,  " + use_mapName + " , " + Content.taskName);
            if (Content.is_initialize_finished == 1) {
                RotateCount = 120;
                myHandler.removeCallbacks(this::run);
                if (Content.taskName != null && use_mapName != null) {
                    if (SocketServices.battery < Content.battery) {
                        Content.robotState = 6;
                        Content.time = 4000;
                        GsonUtils gsonUtils = new GsonUtils();
                        gsonUtils.setTvTime("电量回充,不能开始任务");
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                        }
                        if (Content.have_charging_mode && !Content.isCharging) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.CHARGING_POINT);
                        } else if (!Content.have_charging_mode) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.InitializePositionName);
                        }
                        Content.taskName = null;
                        Content.taskIndex = -1;
                    } else {
                        ///
                        TaskManager.getInstances(mContext).startTaskQueue(use_mapName, Content.taskName, 0);
                    }
                } else {
                    Log.d("ALARMreceiver :", " ,  " + use_mapName + " , " + Content.taskName);
                }
            } else if (Content.is_initialize_finished == 0) {
                Log.d("ALARMreceiver 000:", "" + Content.is_initialize_finished + " ,  " + use_mapName + " , " + Content.taskName);
                if (RotateCount >= 0) {
                    RotateCount--;
                    myHandler.postDelayed(this::run, 1000);
                } else {
                    RotateCount = 120;
                    myHandler.removeCallbacks(this::run);
                    Content.taskIndex = 0;
                    TaskManager.getInstances(mContext).use_map(use_mapName);
                    myHandler.post(this::run);
                }
            } else if (Content.is_initialize_finished == 2) {
                Content.taskName = null;
                RotateCount = 120;
                Content.taskIndex = -1;
                myHandler.removeCallbacks(this::run);
            } else if (Content.is_initialize_finished == -1) {
                RotateCount = 120;
                Content.taskIndex = -1;
                myHandler.removeCallbacks(this::run);
            }
        }
    };

    public void getTaskQueue(int week) {
        // 第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
        long time = System.currentTimeMillis();
        if (Content.charging_gpio == 1 && Content.isCharging) {
            EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.have_charging_mode)));
        } else {
            Cursor aTrue = mSqLiteOpenHelperUtils.searchAlarmTask(Content.dbAlarmIsRun, "true");
            while (aTrue.moveToNext()) {
                Log.d("AlarmReceiver ", mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        + ", taskName" + TextUtils.isEmpty(Content.taskName)
                        + ",  dbAlarmCycle :" + TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle))));
                if (!TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && week == Integer.parseInt(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
//                    use_mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    //if (Content.isCharging || checkLztekLamp.getChargingGpio()) {
                    checkLztekLamp.setLeaveChargingLimit();
                    myHandler.sendEmptyMessageDelayed(12, 10000);
                    myHandler.sendEmptyMessageDelayed(13, 12000);
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
//                    use_mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    checkLztekLamp.setLeaveChargingLimit();
                    myHandler.sendEmptyMessageDelayed(12, 10000);
                    myHandler.sendEmptyMessageDelayed(13, 14000);
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && "FF:FF".equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
//                    use_mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    checkLztekLamp.setLeaveChargingLimit();
                    myHandler.sendEmptyMessageDelayed(12, 10000);
                    myHandler.sendEmptyMessageDelayed(13, 12000);
                }
            }
            mSqLiteOpenHelperUtils.close();
        }
    }

    /**
     * 10秒的sensor检查
     */
    private void startLoopDetection() {
        isTaskFlag = true;
        Log.d(TAG, "startLoopDetection: " + (10 * 1000 - ledtime * Content.delayTime) + "秒");
        tvText = (float) ((10 * 1000 - ledtime * Content.delayTime) / 1000) + "";
//        gsonUtils.setTvTime(tvText);
//        if (Content.server != null) {
//            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
//        }
        if (Content.taskIndex > 0) {
            TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount(tvText);
            EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
        }
        if (battery <= Content.battery) {//是否到达回冲电量
            myHandler.sendEmptyMessageDelayed(4, Content.delayTime);
        } else {
            ledtime++;
            Log.d(TAG, "toLightControlBtn : " + toLightControlBtn);
            if (!toLightControlBtn) {
                ledtime = 0;
                return;
            } else if (ledtime * Content.delayTime <= 10 * 1000) {
                if (checkLztekLamp.getGpioSensorState()) {
                    //有人靠近
                    Log.v(TAG, "10秒重置");
                    Content.Sum_Time = Content.Sum_Time + (ledtime * Content.delayTime) / 1000;
                    ledtime = 0;
                }
                myHandler.sendEmptyMessageDelayed(1, Content.delayTime);
            } else {
                Log.d(TAG, "警告结束，关闭警告和led，开启uvc灯");
                myHandler.removeMessages(1);
                ledtime = 0;
                uvcWarning.stopWarning();
                Content.robotState = 5;
                Content.time = 1000;
                String spinnerItem = (String) spinner[spinnerIndex];
                if (battery > Content.battery) {
                    checkLztekLamp.setUvcMode(0);
                }
//                Date d2 = new Date(System.currentTimeMillis());
//                long diff = d2.getTime();
                workTime = Long.parseLong(spinnerItem) * 60;
                Content.countTime = 0;
                Log.d(TAG, "onCheckedChanged：workTime : " + System.currentTimeMillis() + ",    " + workTime + ",    " + Long.parseLong(spinnerItem));
                startUvcDetection();
                return;
            }
        }
    }

    /**
     * 开启uvc灯
     */
    private void startUvcDetection() {
        isTaskFlag = false;
        Log.d(TAG, "startUvcDetection" + battery);
        if (!toLightControlBtn) {
            return;
        }
        if (battery <= Content.battery) {//是否到达回冲电量
            myHandler.sendEmptyMessageDelayed(4, Content.delayTime);
        } else if (!checkLztekLamp.getGpioSensorState()) {
            Log.d(TAG, "startUvcDetection" + "关led灯,开uvc灯 : ");
            if (pauseTime != 0) {
                uvcWarning.stopWarning();
                if (battery > 30) {
                    checkLztekLamp.setUvcMode(0);
                }
                //workTime = workTime + System.currentTimeMillis() - pauseTime;
                pauseTime = 0;
            }
            Content.robotState = 5;
            Content.time = 1000;
            Content.countTime++;
            myHandler.sendEmptyMessageDelayed(2, Content.delayTime);
        } else {
            if (pauseTime == 0) {
                pauseTime = System.currentTimeMillis();
            }
            Log.d(TAG, "startUvcDetection" + "关uvc灯");
            uvcWarning.startWarning();
            Content.robotState = 5;
            Content.time = 1000;
            checkLztekLamp.setUvcMode(1);
            myHandler.sendEmptyMessageDelayed(3, Content.delayTime);
        }
    }

    private void sendRequest() {
        gsonUtils.setMapName(use_mapName);
        TaskManager.getInstances(mContext).getTaskQueues(use_mapName);
        TaskManager.getInstances(mContext).getMapPic(use_mapName);

        TaskManager.getInstances(mContext).getVirtual_obstacles(use_mapName);
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_SETTING_MODE, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_HISTORY, ""));
        if (TaskManager.pointStateBean != null) {
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, TaskManager.pointStateBean));
        }
    }

    @Override
    protected void onBaseEventMessage(EventBusMessage messageEvent) {
        super.onBaseEventMessage(messageEvent);
        //phone 发送的命令
        if (messageEvent.getState() == 1007) {
            toLightControlBtn = true;
            Log.d(TAG, "spinner index" + (Integer) messageEvent.getT());
            onCheckedChanged((Integer) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.REQUEST_MSG) {//callback信息的返回
            if (Content.server != null) {
                gsonUtils.setCallback((String) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putCallBackMsg(Content.REQUEST_MSG));
            }
        } else if (messageEvent.getState() == BaseEvent.STARTDOWN) {//后退
            handler1.postDelayed(runnable1, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == BaseEvent.STARTUP) {//前进
            handler2.postDelayed(runnable2, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == BaseEvent.STARTLEFT) {//左转
            handler3.postDelayed(runnable3, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == BaseEvent.STARTRIGHT) {//右转
            handler4.postDelayed(runnable4, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == BaseEvent.STARTLIGHT) {//开始消毒检测
            toLightControlBtn = true;
            startDemoMode();
        } else if (messageEvent.getState() == BaseEvent.STOPLIGHT) {//停止消毒检测
            toLightControlBtn = false;
            stopDemoMode();
        } else if (messageEvent.getState() == BaseEvent.STOPUP) {//停前
            handler2.removeCallbacks(runnable2);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == BaseEvent.STOPDOWN) {//停退
            handler1.removeCallbacks(runnable1);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == BaseEvent.STOPLEFT) {//停左
            handler3.removeCallbacks(runnable3);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == BaseEvent.STOPRIGHT) {//停右
            handler4.removeCallbacks(runnable4);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == BaseEvent.GETMAPLIST) {//地图列表
            TaskManager.getInstances(mContext).loadMapList();
        } else if (messageEvent.getState() == BaseEvent.SENDMAPNAME) {//地图列表获取后发送
            robotMap = (RobotMap) messageEvent.getT();
            if (!Content.is_reset_robot) {
//                if (Content.server != null) {
//                    Content.server.broadcast(gsonUtils.putMapListMessage(Content.SENDMAPNAME, robotMap));
                for (int i = 0; i < robotMap.getData().size(); i++) {
                    TaskManager.getInstances(mContext).getPosition(robotMap.getData().get(i).getName());
                }
                for (int i = 0; i < robotMap.getData().size(); i++) {
                    if (Content.TempMapName.equals(robotMap.getData().get(i).getName())) {
                        use_mapName = Content.TempMapName;
                    }
                }
                Log.d(TAG,"地图名字 ： " + use_mapName);
//                }
            }
            if (Content.is_reset_robot) {
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < robotMap.getData().size(); i++) {
                            Log.d(TAG, "reset mapName : " + robotMap.getData().get(i).getName());
                            TaskManager.getInstances(mContext).deleteMap(robotMap.getData().get(i).getName());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Content.is_reset_robot = false;
                    }
                }.run();
            }
        } else if (messageEvent.getState() == BaseEvent.SAVETASKQUEUE) {//存储任务队列
            String messageEventT = (String) messageEvent.getT();
//            Cursor searchAllAlarmTask = mSqLiteOpenHelperUtils.searchAllAlarmTask();
//            int taskName = 0;
//            while (searchAllAlarmTask.moveToNext()) {
//                taskName = searchAllAlarmTask.getInt(searchAllAlarmTask.getColumnIndex("_id"));
//            }
//            taskName++;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(messageEventT);
                String taskName = jsonObject.getString(Content.TASK_NAME);
                String mapName = jsonObject.getString(Content.MAP_NAME);
                List<SaveTaskBean> points = new ArrayList<>();
                for (int i = 0; i < jsonObject.getJSONArray(Content.SAVETASKQUEUE).length(); i++) {
                    SaveTaskBean saveTaskBean = new SaveTaskBean();
                    JSONObject jsonObject1 = (JSONObject) jsonObject.getJSONArray(Content.SAVETASKQUEUE).get(i);
                    saveTaskBean.setPositionName(jsonObject1.getString(Content.POINT_NAME));
                    saveTaskBean.setTime(jsonObject1.getInt(Content.SPINNERTIME));
                    points.add(saveTaskBean);
                }
                TaskManager.getInstances(mContext).save_taskQueue(mapName, taskName, points);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.DELETETASKQUEUE) {//删除任务队列
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject((String) messageEvent.getT());
                TaskManager.getInstances(mContext).deleteTaskQueue(jsonObject.getString(Content.MAP_NAME)
                        , jsonObject.getString(Content.TASK_NAME));
                mSqLiteOpenHelperUtils.deleteAlarmTask(jsonObject.getString(Content.MAP_NAME)
                        + "," + jsonObject.getString(Content.TASK_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (messageEvent.getState() == BaseEvent.GETTASKQUEUE) {//获取任务列表
            TaskManager.getInstances(mContext).getTaskQueues(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.SENDTASKQUEUE) {//返回任务列表
            RobotTaskQueueList robotTaskQueueList = (RobotTaskQueueList) messageEvent.getT();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < robotTaskQueueList.getData().size(); i++) {
                list.add(robotTaskQueueList.getData().get(i).getName());
            }
//            gsonUtils.setData(list);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.sendRobotMsg(Content.SENDTASKQUEUE, list));
            }
        } else if (messageEvent.getState() == BaseEvent.SENDPOINTPOSITION) {//返回地图点数据
            mapCount++;
            RobotPositions robotPositions = (RobotPositions) messageEvent.getT();
            Cursor cursor = mSqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, use_mapName);
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
                RobotPointPositions robotPointPositions = new RobotPointPositions();
                for (int i = 0; i < robotPositions.getData().size(); i++) {
                    if (robotPositions.getData().get(i).getName().equals(cursor.getString(cursor.getColumnIndex(Content.dbPointName)))) {
                        robotPointPositions.setName(cursor.getString(cursor.getColumnIndex(Content.dbPointName)));
                        robotPointPositions.setPointTime(cursor.getInt(cursor.getColumnIndex(Content.dbSpinnerTime)));
                        robotPointPositions.setAngle(robotPositions.getData().get(i).getAngle());
                        robotPointPositions.setType(robotPositions.getData().get(i).getType());
                        robotPointPositions.setGridX(robotPositions.getData().get(i).getGridX());
                        robotPointPositions.setGridY(robotPositions.getData().get(i).getGridY());
                        robotPointPositions.setMapName(robotPositions.getData().get(i).getMapName());
                    }
                }
                arrayList.add(robotPointPositions);
            }
            //gsonUtils.setmRobotPositions(arrayList);
            if (Content.server != null && robotMap.getData().size() == mapCount) {
                mapCount = 0;
                Content.server.broadcast(gsonUtils.sendRobotMsg(Content.SENDMAPNAME, arrayList, robotMap));
                sendRequest();
            }

        } else if (messageEvent.getState() == BaseEvent.GETMAPPIC) {//请求地图图片
            TaskManager.getInstances(mContext).getMapPic(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.SENDMAPICON) {//返回地图图片
            byte[] bytes = (byte[]) messageEvent.getT();
            if (Content.server != null) {
                Content.server.broadcast(bytes);
            }

        } else if (messageEvent.getState() == BaseEvent.ADD_POSITION) {//添加点
            String s = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(s);
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName(jsonObject.getString(Content.POINT_NAME));
                positionListBean.setGridX((int) x);
                positionListBean.setGridY((int) y);
                positionListBean.setAngle(angle);
                positionListBean.setType(2);
                positionListBean.setMapName(use_mapName);
                TaskManager.getInstances(mContext).add_Position(positionListBean, jsonObject.getInt(Content.POINT_TIME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.STARTTASKQUEUE) {//开始任务
            Log.d(TAG, "start task taskName : " + Content.taskName);
//            Content.IS_STOP_TASK = false;
            try {
                mSqLiteOpenHelperUtils.updateAlarmTask(
                        new JSONObject((String) messageEvent.getT()).getString(Content.MAP_NAME)
                                + "," + new JSONObject((String) messageEvent.getT()).getString(Content.TASK_NAME),
                        Content.dbAlarmIsRun, "true");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Cursor cursor = mSqLiteOpenHelperUtils.searchAllAlarmTask();
            while (cursor.moveToNext()) {
                Log.d(TAG, "starttask: " + cursor.getString(cursor.getColumnIndex(Content.dbAlarmMapTaskName)) + "  ,  "
                        + cursor.getString(cursor.getColumnIndex(Content.dbAlarmIsRun)));
            }
            mSqLiteOpenHelperUtils.close();
        } else if (messageEvent.getState() == BaseEvent.STOPTASKQUEUE) {//停止任务
            Log.d(TAG, "stoptask taskName : " + (String) messageEvent.getT());
            mSqLiteOpenHelperUtils.updateAlarmTask(
                    use_mapName + "," + (String) messageEvent.getT(),
                    Content.dbAlarmIsRun, "false");
            mSqLiteOpenHelperUtils.close();
        } else if (messageEvent.getState() == BaseEvent.CANCELTASKQUEUE) {//取消当前任务
            if (Content.taskName != null) {
                TaskManager.getInstances(mContext).stopTaskQueue(use_mapName);
                toLightControlBtn = false;
                onCheckedChanged(0);
            }
            Content.is_initialize_finished = -1;
            Content.Sum_Time = 0;
            myHandler.removeMessages(9);
        } else if (messageEvent.getState() == BaseEvent.SENDGPSPOSITION) {//返回机器人位置
            RobotPosition robotPosition = (RobotPosition) messageEvent.getT();
            x = (float) robotPosition.getGridPosition().getX();
            y = (float) robotPosition.getGridPosition().getY();
            angle = (double) robotPosition.getAngle();
            gsonUtils.setX((double) robotPosition.getGridPosition().getX());
            gsonUtils.setY((double) robotPosition.getGridPosition().getY());
            gsonUtils.setGridHeight((int) robotPosition.getMapInfo().getGridHeight());
            gsonUtils.setGridWidth((int) robotPosition.getMapInfo().getGridWidth());
            gsonUtils.setOriginX((double) robotPosition.getMapInfo().getOriginX());
            gsonUtils.setOriginY((double) robotPosition.getMapInfo().getOriginY());
            gsonUtils.setResolution((double) robotPosition.getMapInfo().getResolution());
            gsonUtils.setAngle((double) robotPosition.getAngle());
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putRobotPosition(Content.SENDGPSPOSITION));
            }
        } else if (messageEvent.getState() == BaseEvent.START_SCAN_MAP) {//开始扫描地图
            TaskManager.getInstances(mContext).start_scan_map((String) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.USE_MAP) {//应用地图
            TaskManager.getInstances(mContext).use_map(use_mapName);
            myHandler.removeCallbacks(runnablePosition);
            myHandler.postDelayed(runnablePosition, 1000);
        } else if (messageEvent.getState() == BaseEvent.INITIALIZE_RESULE) {//转圈初始化结果
            Log.d(TAG, "Initialize result ： " + (String) messageEvent.getT() + ",     isDevelop :" + isDevelop);
            if ("successed".equals((String) messageEvent.getT())) {
                handlerInitialize.removeCallbacks(runnableInitialize);
                handlerInitialize.postDelayed(runnableInitialize, 1000);
            }
        } else if (messageEvent.getState() == BaseEvent.GETPOINTPOSITION) {//请求地图点列表
            TaskManager.getInstances(mContext).getPosition(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.CANCEL_SCAN_MAP) {//取消扫描地图并保存
            TaskManager.getInstances(mContext).stopScanMap();
            isDevelop = false;
        } else if (messageEvent.getState() == BaseEvent.DEVELOP_MAP) {//拓展地图
            isDevelop = true;
            NavigationService.initialize_directly(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.DELETE_MAP) {//删除地图
            use_mapName = SharedPrefUtil.getInstance(mContext).getSharedPrefMapName(Content.MAP_NAME);
            try {
                JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                JSONArray jsonArray = jsonObject.getJSONArray(Content.DELETE_MAP);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (Content.TempMapName.equals(jsonArray.getString(i))) {
                        //删除临时地图
                        TaskManager.getInstances(mContext).use_map(use_mapName);
                        TaskManager.getInstances(mContext).deleteMap(Content.TempMapName);
                        TaskManager.getInstances(mContext).loadMapList();
                    } else {
                        TaskManager.getInstances(mContext).deleteMap(jsonArray.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.DELETE_POSITION) {//删除点
            try {
                TaskManager.getInstances(mContext).deletePosition(
                        new JSONObject((String) messageEvent.getT()).getString(Content.MAP_NAME),
                        new JSONObject((String) messageEvent.getT()).getString(Content.POINT_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.BATTERY_DATA) {//电池电量
            byte[] bytes = (byte[]) messageEvent.getT();
            if ((battery - bytes[23]) < 10) {
                battery = bytes[23];
            }
            if (Content.server != null) {
                gsonUtils.setBattery(battery + "%");
//                Content.server.broadcast(gsonUtils.putBattery(Content.BATTERY_DATA));
            }
            if (battery <= Content.battery) {
                Content.robotState = 6;
                Content.time = 1000;
            }

        } else if (messageEvent.getState() == 10034) {
            Log.d(TAG, "是否完成初始化" + (Status) messageEvent.getT());
            Status status = (Status) messageEvent.getT();
            if ("true".equals(status.getData())) {
                myHandler.removeMessages(15);
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(10000, Content.initialize_success));
                if (isDevelop) {
                    TaskManager.getInstances(mContext).start_develop_map(use_mapName);
                }
                Content.is_initialize_finished = 1;
                Log.d(TAG, "是否完成初始化" + Content.is_initialize_finished);
                if (Content.taskName != null) {
                    myHandler.sendEmptyMessage(9);
                }
            } else if ("failed".equals(status.getData())) {
                Content.is_initialize_finished = 2;
                handlerInitialize.removeCallbacks(runnableInitialize);
                myHandler.removeMessages(15);
                EventBus.getDefault().post(new EventBusMessage(10000, Content.initialize_fail));
            } else if ("false".equals(status.getData()) && Content.is_initialize_finished == 0) {
                Content.is_initialize_finished = 0;
                handlerInitialize.postDelayed(runnableInitialize, 1000);
                if (!myHandler.hasMessages(15)) {
                    myHandler.sendEmptyMessageDelayed(15, 1 * 60 * 1000);
                }
                EventBus.getDefault().post(new EventBusMessage(10000, Content.initializing));
            } else {
                myHandler.removeMessages(15);
                Content.is_initialize_finished = 2;
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(10000, status.getErrorCode()));
            }
        } else if (messageEvent.getState() == 10035) {
            Log.d(TAG, "是否完成初始化error: " + (String) messageEvent.getT());
            EventBus.getDefault().post(new EventBusMessage(10000, Content.initialize_fail));
            handlerInitialize.removeCallbacks(runnableInitialize);
            Content.is_initialize_finished = 2;
        } else if (messageEvent.getState() == BaseEvent.CANCEL_SCAN_MAP_NO) {//取消扫描不保存地图
            TaskManager.getInstances(mContext).cancleScanMap();
        } else if (messageEvent.getState() == BaseEvent.ROBOT_HEALTHY) {//系统健康
            if (Content.server != null) {
                gsonUtils.setHealthyMsg((String) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putSocketHealthyMsg(Content.ROBOT_HEALTHY));
            }
        } else if (messageEvent.getState() == BaseEvent.ROBOT_TASK_STATE) {//任务状态
            if (Content.server != null) {
                Log.d(TAG, "ZDZD ROBOT_TASK_STATE : " + BaseEvent.ROBOT_TASK_STATE);
                gsonUtils.setMapName(use_mapName);
                gsonUtils.setTaskName(Content.taskName);
                gsonUtils.setTaskState((PointStateBean) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putSocketTaskMsg(Content.ROBOT_TASK_STATE));
            }
        } else if (messageEvent.getState() == BaseEvent.ROBOT_TASK_HISTORY) {//任务历史
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putSocketTaskHistory(Content.ROBOT_TASK_HISTORY, mContext));
            }
        } else if (messageEvent.getState() == BaseEvent.GET_VIRTUAL) {//获取虚拟强数据
            TaskManager.getInstances(mContext).getVirtual_obstacles(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.SEND_VIRTUAL) {//返回虚拟强数据
            VirtualObstacleBean virtualObstacleBean = (VirtualObstacleBean) messageEvent.getT();
            gsonUtils.setVirtualObstacleBean(virtualObstacleBean);
            if (Content.server != null && virtualObstacleBean != null) {
                Content.server.broadcast(gsonUtils.putVirtualObstacle(Content.SEND_VIRTUAL));
            }
        } else if (messageEvent.getState() == BaseEvent.UPDATA_VIRTUAL) {//更新虚拟强
            mVirtualBeanUtils.updateVirtual(4, use_mapName, "carpets", null);
            mVirtualBeanUtils.updateVirtual(6, use_mapName, "decelerations", null);
            mVirtualBeanUtils.updateVirtual(1, use_mapName, "slopes", null);
            mVirtualBeanUtils.updateVirtual(5, use_mapName, "displays", null);
            String message = (String) messageEvent.getT();
            //最外边「」
            List<List<UpdataVirtualObstacleBean.ObstaclesEntity.PolylinesEntity>> polylinesEntities = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(message);
                JSONArray jsonArray = jsonObject.getJSONArray(Content.UPDATA_VIRTUAL);
                for (int i = 0; i < jsonArray.length(); i++) {
                    //每个「」
                    List<UpdataVirtualObstacleBean.ObstaclesEntity.PolylinesEntity> polylinesEntitiesList = new ArrayList<>();
                    JSONArray jsarray = jsonArray.getJSONArray(i);
                    for (int j = 0; j < jsarray.length(); j++) {
                        JSONObject js = jsarray.getJSONObject(j);
                        UpdataVirtualObstacleBean.ObstaclesEntity.PolylinesEntity polylinesEntity = new UpdataVirtualObstacleBean.ObstaclesEntity.PolylinesEntity();
                        polylinesEntity.setX(js.getDouble(Content.VIRTUAL_X));
                        polylinesEntity.setY(js.getDouble(Content.VIRTUAL_Y));
                        polylinesEntitiesList.add(polylinesEntity);
                    }
                    polylinesEntities.add(polylinesEntitiesList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mVirtualBeanUtils.updateVirtual(0, use_mapName, "obstacles", polylinesEntities);
        } else if (messageEvent.getState() == BaseEvent.TASK_ALARM) {//定时任务
            String message = (String) messageEvent.getT();
            Cursor searchAllAlarmTask = mSqLiteOpenHelperUtils.searchAllAlarmTask();
//            int taskName = 0;
//            while (searchAllAlarmTask.moveToNext()) {
//                taskName = searchAllAlarmTask.getInt(searchAllAlarmTask.getColumnIndex("_id"));
//            }
            try {
                JSONObject jsonObject = new JSONObject(message);
                JSONArray jsonArray = jsonObject.getJSONArray(Content.dbAlarmCycle);
                if (jsonArray.length() == 0) {
                    mSqLiteOpenHelperUtils.saveAlarmTask(
                            jsonObject.getString(Content.MAP_NAME) + "," + jsonObject.getString(Content.TASK_NAME),
                            jsonObject.getString(Content.dbAlarmTime),
                            "",
                            "false");
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mSqLiteOpenHelperUtils.saveAlarmTask(
                                jsonObject.getString(Content.MAP_NAME) + "," + jsonObject.getString(Content.TASK_NAME),
                                jsonObject.getString(Content.dbAlarmTime),
                                jsonArray.getString(i),
                                "false");
                    }
                }
                mSqLiteOpenHelperUtils.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.RENAME_MAP) {//重命名地图
            String message = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(message);
                TaskManager.getInstances(mContext).renameMapName(jsonObject.getString(Content.OLD_MAP_NAME),
                        jsonObject.getString(Content.NEW_MAP_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        } else if (messageEvent.getState() == BaseEvent.SET_PLAYPATHSPEEDLEVEL) {//设置跑线速度
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject((String) messageEvent.getT());
//                int playPathSpeedLevel = jsonObject.getInt(Content.SET_PLAYPATHSPEEDLEVEL);
//                TaskManager.getInstances(mContext).setSpeedLevel(playPathSpeedLevel);
//                TaskManager.getInstances(mContext).setnavigationSpeedLevel(playPathSpeedLevel);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } else if (messageEvent.getState() == BaseEvent.RENAME_POSITION) {//重命名点
            String message = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(message);
                TaskManager.getInstances(mContext).renamePosition(jsonObject.getString(Content.MAP_NAME),
                        jsonObject.getString(Content.OLD_POINT_NAME),
                        jsonObject.getString(Content.NEW_POINT_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        } else if (messageEvent.getState() == BaseEvent.GET_SPEED_LEVEL) {//获取速度
//            TaskManager.getInstances(mContext).deviceStatus();
        } else if (messageEvent.getState() == BaseEvent.DEVICES_STATUS) {//返回设备信息
            RobotDeviceStatus robotDeviceStatus = (RobotDeviceStatus) messageEvent.getT();
            if (Content.speed != robotDeviceStatus.getData().getSpeed()
                    || Content.EMERGENCY != (robotDeviceStatus.getData().isEmergency()
                    || robotDeviceStatus.getData().isEmergencyStop())) {
                Content.speed = robotDeviceStatus.getData().getSpeed();
                Log.d(TAG, "devices speed : " + Content.speed);
                if (Content.server != null) {
                    gsonUtils.setPlayPathSpeedLevel(robotDeviceStatus.getData().getPlayPathSpeedLevel());
                    gsonUtils.setNavigationSpeedLevel(robotDeviceStatus.getData().getNavigationSpeedLevel());
                    Log.d(TAG, "紧急急停 : " + robotDeviceStatus.getData().isEmergency() + ",   " + robotDeviceStatus.getData().isEmergencyStop());
                    if (robotDeviceStatus.getData().isEmergency() || robotDeviceStatus.getData().isEmergencyStop()) {
                        //gsonUtils.setEmergency(true);
                        Content.EMERGENCY = true;
                        Content.taskIsFinish = false;
                        toLightControlBtn = false;
                        onCheckedChanged(0);
                    } else {
//                    if (Content.IS_STOP_TASK && Content.EMERGENCY) {
//                        myHandler.sendEmptyMessage(8);
//                    }
                        //gsonUtils.setEmergency(false);
                        Content.EMERGENCY = false;
                    }
                    Content.server.broadcast(gsonUtils.sendRobotMsg(Content.DEVICES_STATUS, Content.EMERGENCY));
                }
            }
            //电压>0,放电状态
            Content.chargerVoltage = (int) robotDeviceStatus.getData().getChargerVoltage();
            if (Content.chargerVoltage > 0) {
                Content.chargingState = 1;
            }
        } else if (messageEvent.getState() == BaseEvent.ADD_POWER_POINT) {//添加充电点
            Log.d(TAG, "Add charging : " + Content.isCharging + ",   " + angle);
            if (Content.isCharging) {
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName(Content.CHARGING_POINT);
                positionListBean.setGridX((int) x);
                positionListBean.setGridY((int) y);
                positionListBean.setAngle(angle);
                positionListBean.setType(1);
                positionListBean.setMapName(use_mapName);
                TaskManager.getInstances(mContext).add_Position(positionListBean, -1);
            }
        } else if (messageEvent.getState() == BaseEvent.EDITTASKQUEUE) {//编辑任务
            if (Content.server != null) {
                try {
                    String mapName = new JSONObject((String) messageEvent.getT()).getString(Content.MAP_NAME);
                    String taskName = new JSONObject((String) messageEvent.getT()).getString(Content.TASK_NAME);

                    Cursor cursor = mSqLiteOpenHelperUtils.searchAlarmTask(Content.dbAlarmMapTaskName, mapName + "," + taskName);
                    List<String> list = new ArrayList<>();
                    List<TaskBean> taskBeans = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        list.add(cursor.getString(cursor.getColumnIndex(Content.dbAlarmCycle)));
                        gsonUtils.setEditTime(cursor.getString(cursor.getColumnIndex(Content.dbAlarmTime)));
                    }
                    Cursor cursorPoint = mSqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, mapName + "," + taskName);
                    while (cursorPoint.moveToNext()) {
                        TaskBean taskBean = new TaskBean(cursorPoint.getString(cursorPoint.getColumnIndex(Content.dbPointName)),
                                Integer.parseInt(cursorPoint.getString(cursorPoint.getColumnIndex(Content.dbSpinnerTime))),
                                Integer.parseInt(cursorPoint.getString(cursorPoint.getColumnIndex(Content.dbPointX))),
                                Integer.parseInt(cursorPoint.getString(cursorPoint.getColumnIndex(Content.dbPointY))));
                        taskBeans.add(taskBean);
                    }
                    mSqLiteOpenHelperUtils.close();
//                    gsonUtils.setEditTaskType(list);
//                    gsonUtils.setTaskBeans(taskBeans);
                    Content.server.broadcast(gsonUtils.sendEditTaskMsg(Content.EDITTASKQUEUE, list, taskBeans));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (messageEvent.getState() == BaseEvent.SYSTEM_DATE) {//设置系统时间
            checkLztekLamp.setSystemTime((Long) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.GET_TASK_STATE) {//是否有任务正在执行
            Cursor aTrue = mSqLiteOpenHelperUtils.searchAlarmTask(Content.dbAlarmIsRun, "true");
            Log.d(TAG, "是否有正在执行的任务" + aTrue.getCount());
            String nextTaskaskName = "", nextTaskTime = "", nextTaskCycle = "";
            ArrayList<RunningTaskBean> runningTaskBeanArrayList = new ArrayList<>();
            while (aTrue.moveToNext()) {
                ArrayList<RunningTaskBean.PointBean> pointBeanArrayList = new ArrayList<>();
//                    if ("FF:FF".equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))) {
//                        nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
//                        use_mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
//                        break;
//                    } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle))) ||
//                            mAlarmUtils.getWeek(System.currentTimeMillis()) == Integer.parseInt(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))) {
//                        long integerTime = mAlarmUtils.stringToTimestamp("2021-02-22 " + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)) + ":00");
//                        long nowTime = mAlarmUtils.stringToTimestamp("2021-02-22 " + mAlarmUtils.getTimeMillis(System.currentTimeMillis()) + ":00");
//                        if (integerTime - nowTime < nextTaskTime) {
//                            nextTaskTime = integerTime - nowTime;
//                            nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
//                            use_mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
//                        }
//                    } else {
//                        long integerTime = mAlarmUtils.stringToTimestamp("2021-02-22 " + aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)) + ":00");
//                        long nowTime = mAlarmUtils.stringToTimestamp("2021-02-22 " + mAlarmUtils.getTimeMillis(System.currentTimeMillis()) + ":00");
//                        if (integerTime - nowTime < nextTaskTime) {
//                            nextTaskTime = integerTime - nowTime;
//                            nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
//                            use_mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[0];
//                        }
//                    }
//                nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(",")[1];
                String nextTaskCycle11 = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle));
                if (nextTaskaskName.equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)))) {
                    nextTaskCycle = nextTaskCycle + "," + nextTaskCycle11;
                    Log.d(TAG, "ZDZD GET_TASK_STATE nextTaskCycle----: " + nextTaskCycle);
                    if (aTrue.isLast()) {
                        Log.d(TAG, "ZDZD GET_TASK_STATE1111 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
                        Cursor pointCuror = mSqLiteOpenHelperUtils.searchPointTask(
                                Content.dbPointTaskName,
                                aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)));
                        while (pointCuror.moveToNext()) {
                            RunningTaskBean.PointBean pointBean = new RunningTaskBean.PointBean();
                            pointBean.setPointName(pointCuror.getString(pointCuror.getColumnIndex(Content.dbPointName)));
                            pointBean.setSpinnerIndex(Integer.parseInt(pointCuror.getString(pointCuror.getColumnIndex(Content.dbSpinnerTime))));
                            pointBean.setPointX(Integer.parseInt(pointCuror.getString(pointCuror.getColumnIndex(Content.dbPointX))));
                            pointBean.setPointY(Integer.parseInt(pointCuror.getString(pointCuror.getColumnIndex(Content.dbPointY))));
                            pointBeanArrayList.add(pointBean);
                        }
                        RunningTaskBean runningTaskBean = new RunningTaskBean();
                        runningTaskBean.setArrayList(pointBeanArrayList);
                        runningTaskBean.setTaskName(nextTaskaskName);
                        runningTaskBean.setTaskAlarm(nextTaskTime);
                        runningTaskBean.setAlarmCycle(nextTaskCycle);
                        runningTaskBean.setMapName(SharedPrefUtil.getInstance(mContext).getSharedPrefMapName(Content.MAP_NAME));
                        runningTaskBeanArrayList.add(runningTaskBean);
                        nextTaskaskName = "";
                        nextTaskCycle = "";
                    }
                } else {
                    if (TextUtils.isEmpty(nextTaskaskName)) {
                        nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName));
                        nextTaskCycle = nextTaskCycle + "," + nextTaskCycle11;
                        nextTaskTime = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime));
                        Log.d(TAG, "ZDZD GET_TASK_STATE2222 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
                    } else {
                        Log.d(TAG, "ZDZD GET_TASK_STATE3333 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
                        Cursor pointCuror = mSqLiteOpenHelperUtils.searchPointTask(
                                Content.dbPointTaskName,
                                aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)));
                        while (pointCuror.moveToNext()) {
                            RunningTaskBean.PointBean pointBean = new RunningTaskBean.PointBean();
                            pointBean.setPointName(pointCuror.getString(pointCuror.getColumnIndex(Content.dbPointName)));
                            pointBean.setSpinnerIndex(Integer.parseInt(pointCuror.getString(pointCuror.getColumnIndex(Content.dbSpinnerTime))));
                            pointBean.setPointX(Integer.parseInt(pointCuror.getString(pointCuror.getColumnIndex(Content.dbPointX))));
                            pointBean.setPointY(Integer.parseInt(pointCuror.getString(pointCuror.getColumnIndex(Content.dbPointY))));
                            pointBeanArrayList.add(pointBean);
                        }
                        RunningTaskBean runningTaskBean = new RunningTaskBean();
                        runningTaskBean.setArrayList(pointBeanArrayList);
                        runningTaskBean.setTaskName(nextTaskaskName);
                        runningTaskBean.setTaskAlarm(nextTaskTime);
                        runningTaskBean.setAlarmCycle(nextTaskCycle);
                        runningTaskBean.setMapName(SharedPrefUtil.getInstance(mContext).getSharedPrefMapName(Content.MAP_NAME));
                        runningTaskBeanArrayList.add(runningTaskBean);
                        nextTaskaskName = "";
                        nextTaskCycle = "";
                    }
                }
            }
            mSqLiteOpenHelperUtils.close();
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.getTaskRun(Content.GET_TASK_STATE, runningTaskBeanArrayList));
            }
            //TaskManager.getInstances(mContext).getTaskPositionMsg(use_mapName, nextTaskaskName);
            //Log.d(TAG,"ZDZD : pointStateBean" + (TaskManager.pointStateBean != null));
        } else if (messageEvent.getState() == 10054) {//设置led亮度

//        } else if (messageEvent.getState() == BaseEvent.GET_LED_LEVEL) {//获取led亮度
//            int level = SharedPrefUtil.getInstance(mContext).getSharedPrefLed(Content.SET_LED_LEVEL);
//            Log.d(TAG, "level get led" + level);
//            if (Content.server != null) {
//                gsonUtils.setLed_level(level);
//                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_LED_LEVEL));
//            }
//        } else if (messageEvent.getState() == BaseEvent.GET_LOW_BATTERY) {//获取低电量回充
//            int sharedPrefBattery = SharedPrefUtil.getInstance(mContext).getSharedPrefBattery(Content.SET_LOW_BATTERY);
//            Log.d(TAG, "level get battery " + sharedPrefBattery);
//            if (Content.server != null) {
//                gsonUtils.setLow_battery(sharedPrefBattery);
//                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_LOW_BATTERY));
//            }
        } else if (messageEvent.getState() == BaseEvent.RESET_ROBOT) {//重置设备
            //TaskManager.getInstances(mContext).robot_reset();
            mSqLiteOpenHelperUtils.reset_Db(Content.dbAlarmName);
            mSqLiteOpenHelperUtils.reset_Db(Content.tableName);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbPointTime);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbTaskState);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbTotalCount);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbCurrentCount);
            SharedPrefUtil.getInstance(mContext).deleteAll();
            mSqLiteOpenHelperUtils.close();
            Content.is_reset_robot = true;
            Content.battery = 30;
            Content.led = 2;
            Content.Working_mode = 2;
            TaskManager.getInstances(mContext).setSpeedLevel(2);
            TaskManager.getInstances(mContext).setnavigationSpeedLevel(2);
            AudioManager mAudioManager1 = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
            mAudioManager1.setStreamVolume(AudioManager.STREAM_MUSIC,
                    5,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
            TaskManager.getInstances(mContext).loadMapList();
        } else if (messageEvent.getState() == BaseEvent.GET_ULTRASONIC) {//声呐设备
            TaskManager.getInstances(mContext).getUltrasonicPhit();
        } else if (messageEvent.getState() == BaseEvent.SEND_ULTRASONIC) {//返回声呐设备信息
            UltrasonicPhitBean ultrasonicPhitBean = (UltrasonicPhitBean) messageEvent.getT();
            if (Content.server != null) {
                //gsonUtils.setUltrasonicPhitBean(ultrasonicPhitBean);
                Content.server.broadcast(gsonUtils.sendRobotMsg(Content.GET_ULTRASONIC, ultrasonicPhitBean));
            }
//        } else if (messageEvent.getState() == BaseEvent.VERSIONCODE) {//版本
//            TaskManager.getInstances(mContext).deviceRobotVersion();
//            if (Content.server != null) {
//                Content.server.broadcast(gsonUtils.putJsonMessage(Content.VERSIONCODE));
//            }
//        } else if (messageEvent.getState() == BaseEvent.GET_WORKING_MODE) {//获取工作模式
//            int workingMode = SharedPrefUtil.getInstance(mContext).getSharedPrefWorkingMode(Content.WORKING_MODE);
//            Log.d(TAG, "working_mode get " + workingMode);
//            if (Content.server != null) {
//                gsonUtils.setWorkingMode(workingMode);
//                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_WORKING_MODE));
//            }
        } else if (messageEvent.getState() == BaseEvent.ROBOTVERSIONCODE) {//获取下位机版本信息
//            if (Content.server != null) {
            gsonUtils.setRobotVersion((String) messageEvent.getT());
//                Content.server.broadcast(gsonUtils.putJsonMessage(Content.ROBOTVERSIONCODE));
//            }
        } else if (messageEvent.getState() == 10063) {//获取电池的电压
//            if (Content.server != null) {
//                gsonUtils.setRobotVersion((String) messageEvent.getT());
//                Content.server.broadcast(gsonUtils.putJsonMessage(Content.ROBOTVERSIONCODE));
//            }
//        } else if (messageEvent.getState() == BaseEvent.GET_CHARGING_MODE) {
//            if (Content.server != null) {
//                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_CHARGING_MODE));
//            }
        } else if (messageEvent.getState() == BaseEvent.dbTotalCount) {//消毒面积
            if (Content.server != null) {
                Cursor cursorTotal = mSqLiteOpenHelperUtils.searchTaskTotalCount();
                long taskCount = 0, taskTime = 0, area = 0;
                while (cursorTotal.moveToNext()) {
                    taskCount = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTaskTotalCount)));
                    taskTime = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTimeTotalCount)));
                    area = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbAreaTotalCount)));
                }
                mSqLiteOpenHelperUtils.close();

                gsonUtils.setTotalArea(area);
                gsonUtils.setTotalTaskCount(taskCount);
                gsonUtils.setTotalTime(taskTime);
                Content.server.broadcast(gsonUtils.sendRobotWorkingCount(Content.dbTotalCount));
            }
        } else if (messageEvent.getState() == BaseEvent.dbCurrentCount) {//当月消毒
            if (Content.server != null) {
                Cursor cursorTotal = mSqLiteOpenHelperUtils.searchTaskCurrentCount(mAlarmUtils.getTimeMonth(System.currentTimeMillis()));
                long taskCount = 0, taskTime = 0, area = 0;
                while (cursorTotal.moveToNext()) {
                    taskCount = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTaskCurrentCount)));
                    taskTime = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbTimeCurrentCount)));
                    area = Long.parseLong(cursorTotal.getString(cursorTotal.getColumnIndex(Content.dbAreaCurrentCount)));
                }
                mSqLiteOpenHelperUtils.close();

                gsonUtils.setCurrentArea(area);
                gsonUtils.setCurrentTaskCount(taskCount);
                gsonUtils.setCurrentTime(taskTime);
                Content.server.broadcast(gsonUtils.sendRobotWorkingCount(Content.dbCurrentCount));
            }
        } else if (messageEvent.getState() == BaseEvent.Robot_Error) {
            if (Content.server != null) {
                //gsonUtils.setRobot_error((String) messageEvent.getT());
                Content.server.broadcast(gsonUtils.sendRobotMsg(Content.Robot_Error, (String) messageEvent.getT()));
            }
        } else if (messageEvent.getState() == BaseEvent.GET_SETTING_MODE) {//获取设置信息
            Log.d(TAG, "获取设置里的信息");
            if (Content.server != null) {
                //低电量
                int sharedPrefBattery = SharedPrefUtil.getInstance(mContext).getSharedPrefBattery(Content.SET_LOW_BATTERY);
                gsonUtils.setLow_battery(sharedPrefBattery);
                //音量
                AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
                int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                gsonUtils.setVoice(current);
                //工作模式
                int workingMode = SharedPrefUtil.getInstance(mContext).getSharedPrefWorkingMode(Content.WORKING_MODE);
                Log.d(TAG, "working_mode get " + workingMode);
                gsonUtils.setWorkingMode(workingMode);
                gsonUtils.setMapName(use_mapName);
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_SETTING_MODE));
            }
        } else if (messageEvent.getState() == BaseEvent.SET_SETTING_MODE) {//获取设置信息
            try {
                //有无充电桩
                JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                Content.have_charging_mode = jsonObject.getBoolean(Content.SET_CHARGING_MODE);
                SharedPrefUtil.getInstance(mContext).setSharedPrefChargingMode(Content.GET_CHARGING_MODE, Content.have_charging_mode);
                //工作模式
                Content.Working_mode = jsonObject.getInt(Content.WORKING_MODE);
                SharedPrefUtil.getInstance(mContext).setSharedPrefWorkingMode(Content.WORKING_MODE, Content.Working_mode);
                //音量
                int voice = jsonObject.getInt(Content.SET_VOICE_LEVEL);
                AudioManager mAudioManager1 = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
                mAudioManager1.setStreamVolume(AudioManager.STREAM_MUSIC,
                        voice,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                //设置回冲电量
                Content.battery = jsonObject.getInt(Content.SET_LOW_BATTERY);
                SharedPrefUtil.getInstance(mContext).setSharedPrefBattery(Content.SET_LOW_BATTERY, Content.battery);
                //设置速度
                int playPathSpeedLevel = jsonObject.getInt(Content.SET_PLAYPATHSPEEDLEVEL);
                TaskManager.getInstances(mContext).setSpeedLevel(playPathSpeedLevel);
                TaskManager.getInstances(mContext).setnavigationSpeedLevel(playPathSpeedLevel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


//test request
        else if (messageEvent.getState() == BaseEvent.TEST_UVCSTART_1) {
            checkLztekLamp.test_uvc_start1();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTART_2) {
            checkLztekLamp.test_uvc_start2();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTART_3) {
            checkLztekLamp.test_uvc_start3();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTART_4) {
            checkLztekLamp.test_uvc_start4();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTOP_1) {
            checkLztekLamp.test_uvc_stop1();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTOP_2) {
            checkLztekLamp.test_uvc_stop2();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTOP_3) {
            checkLztekLamp.test_uvc_stop3();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTOP_4) {
            checkLztekLamp.test_uvc_stop4();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTART_ALL) {
            checkLztekLamp.test_uvc_startAll();
        } else if (messageEvent.getState() == BaseEvent.TEST_UVCSTOP_ALL) {
            checkLztekLamp.test_uvc_stopAll();


        } else if (messageEvent.getState() == BaseEvent.TEST_LIGHTSTART) {
            checkLztekLamp.startLedLamp();
        } else if (messageEvent.getState() == BaseEvent.TEST_LIGHTSTOP) {
            checkLztekLamp.stopLedLamp();
        } else if (messageEvent.getState() == BaseEvent.TEST_SENSOR) {
            String s = checkLztekLamp.testGpioSensorState();
            gsonUtils.setTestCallBack(s);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putTestSensorCallBack(Content.TEST_SENSOR));
            }
        } else if (messageEvent.getState() == BaseEvent.TEST_WARNINGSTART) {
            uvcWarning.startWarning();
        } else if (messageEvent.getState() == BaseEvent.TEST_WARNINGSTOP) {
            uvcWarning.stopWarning();
        } else if (messageEvent.getState() == 30001) {
            Content.isUpdate = true;
            assestFile.writeBytesToFile((ByteBuffer) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.UPDATE) {
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.UPDATE));
            }
        } else if (messageEvent.getState() == BaseEvent.PING) {
            String address = "";
            try {
                address = new JSONObject((String) messageEvent.getT()).getString(Content.Address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "PING ADDRESS : " + address + ",   当前连接的address : " + Content.CONNECT_ADDRESS);
            if (Content.server != null) {
                if (TextUtils.isEmpty(Content.CONNECT_ADDRESS)) {
                    Content.server.broadcast(gsonUtils.putConnMsg(Content.CONN_OK));
                    Content.server.broadcast(gsonUtils.sendFileCount(Content.FileCount));
                    Content.CONNECT_ADDRESS = address;
                    myHandler.sendEmptyMessageDelayed(10, 5000);
                    Log.d(TAG, "open connect：" + Content.CONNECT_ADDRESS);
                    TaskManager.getInstances(mContext).loadMapList();
                } else if (Content.CONNECT_ADDRESS.equals(address)) {
                    Content.server.broadcast(gsonUtils.putConnMsg(Content.CONN_OK));
                    myHandler.removeMessages(10);
                    myHandler.sendEmptyMessageDelayed(10, 5000);
                } else if (!Content.CONNECT_ADDRESS.equals(address)) {
                    gsonUtils.setAddress(Content.CONNECT_ADDRESS);
                    SimpleServer.getInstance(mContext).conn.send(gsonUtils.putConnMsg(Content.NO_CONN));
                }
            }

        } else if (messageEvent.getState() == BaseEvent.ALARM_CODE) {
            Message message = new Message();
            message.arg1 = (int) messageEvent.getT();
            message.what = 11;
            myHandler.sendMessage(message);
        } else if (messageEvent.getState() == BaseEvent.DELETE_FILE_ALARM_CODE) {
            LogcatHelper.getInstance(mContext).getFileLength();
        } else if (messageEvent.getState() == BaseEvent.DOWNLOAD_LOG) {
            Log.d(TAG, "开始下载log");
            assestFile.mCopyFile("/data/data/com.example.robot/databases/RobotDatabase", "/sdcard/robotLog");
        } else if (messageEvent.getState() == BaseEvent.COPY_FILE) {
            int length = (Integer) messageEvent.getT();
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.sendCopyLength(Content.COPY_FILE_LENGTH, length));
            }
            if (length == 100) {
                Content.server.broadcast(gsonUtils.startFTPServer(Content.START_PTP_SERVER, "启动FPT服务"));
                Log.d(TAG, "启动FPT服务");
                BroadCastUtils.getInstance(mContext).sendFTPBroadcast();
            }
        } else if (messageEvent.getState() == BaseEvent.GO_TO_LOG_URL) {
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.startFTPServer(Content.START_PTP_SERVER, (String) messageEvent.getT()));
            }
        }

    }

    private Runnable runnablePosition = new Runnable() {
        @Override
        public void run() {
            TaskManager.getInstances(mContext).getPositions(use_mapName);
            myHandler.postDelayed(this, 1000);
        }
    };

    //初始化结果
    Handler handlerInitialize = new Handler();
    Runnable runnableInitialize = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.is_initialize_finished();
        }
    };

    //持续移动 下
    Handler handler1 = new Handler();
    Runnable runnable1 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.move(-0.2f, 0.0f);
            handler1.postDelayed(runnable1, 10);
        }
    };

    //持续移动 上
    Handler handler2 = new Handler();
    Runnable runnable2 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.move(0.2f, 0.0f);
            handler2.postDelayed(runnable2, 10);
        }
    };

    //持续移动 左
    Handler handler3 = new Handler();
    Runnable runnable3 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.move(0.0f, 0.2f);
            handler3.postDelayed(runnable3, 10);
        }
    };

    //持续移动 右
    Handler handler4 = new Handler();
    Runnable runnable4 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.move(0.0f, -0.2f);
            handler4.postDelayed(runnable4, 10);
        }
    };

    private void startDemoMode() {
        if (battery > Content.battery) {
            checkLztekLamp.setUvcModeForDemo(0);
        }
        myHandler.sendEmptyMessageDelayed(5, 1000);
    }

    public static void stopDemoMode() {
        checkLztekLamp.setUvcModeForDemo(1);
        myHandler.removeMessages(5);
    }
}
