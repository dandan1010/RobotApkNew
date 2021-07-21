package com.retron.robotAgent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.dcm360.controller.gs.controller.bean.vel_bean.RobotCmdVel;
import com.dcm360.controller.robot_interface.bean.Status;
import com.retron.robotAgent.bean.PointStateBean;
import com.retron.robotAgent.bean.RobotPointPositions;
import com.retron.robotAgent.bean.RunningTaskBean;
import com.retron.robotAgent.broadcast.BroadCastUtils;
import com.retron.robotAgent.content.BaseEvent;
import com.retron.robotAgent.log.LogcatHelper;
import com.retron.robotAgent.sqlite.SqLiteOpenHelperUtils;
import com.retron.robotAgent.task.TaskManager;
import com.retron.robotAgent.utils.AlarmUtils;
import com.retron.robotAgent.utils.AssestFile;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.utils.EventBusMessage;
import com.retron.robotAgent.utils.GsonUtils;
import com.retron.robotAgent.utils.Md5Utils;
import com.retron.robotAgent.utils.PropertyUtils;
import com.retron.robotAgent.utils.SharedPrefUtil;
import com.retron.robotAgent.utils.VirtualBeanUtils;
import com.retron.robotAgent.utils.ZipUtils;
import com.retron.robotAgent.uvclamp.CheckLztekLamp;
import com.retron.robotAgent.uvclamp.UvcWarning;
import com.retron.robotAgent.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SocketServices extends BaseService {

    private static final String TAG = "SocketServices";

    private static final int UVC_LOOP = 1;//uvc10秒倒计时
    private static final int UVC_WORKING = 2;//uvc开灯倒计时
    private static final int UVC_LOOP_WORKING = 3;//uvc开灯倒计时
    private static final int LOW_POWER = 4;//任务中低电量
    private static final int CHECK_DEMO_POWER = 5;//检测demo模式的低电量
    private static final int CHECK_POWER = 6;//检测formal模式的低电量
    private static final int ROBOT_DEVICE = 7;//设备信息
    private static final int TASK_SUM_TIME = 9;//任务总计时
    private static final int DISCONNECT = 10;//断开连接
    private static final int GET_ALARM_TASK = 11;//获取定时任务
    private static final int MOVE_UP = 12;//任务开启前向前走
    private static final int START_ALARM_TASK = 13;//开启任务
    private static final int ROBOT_STATUS = 14;//机器人状态
    private static final int INITIALIZE_FAIL = 15;//初始化失败
    private static final int DELETE_MAP = 16;//应用上一张地图，删除临时地图
    private static final int ROBOT_MOVE = 17;//移动
    private static final int CHECK_WIRELESS_APK = 18;//保活wireless
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
    private String tvText = "0";
    public static MyHandler myHandler;
    private long pauseTime = 0;
    public static int battery = 0;
    private boolean isTaskFlag = false;
    private float x;
    private float y;
    private double angle;
    public static boolean isDevelop = false;
    private VirtualBeanUtils mVirtualBeanUtils;
    private SqLiteOpenHelperUtils mSqLiteOpenHelperUtils;
    private AlarmUtils mAlarmUtils;
    private int spinnerIndex;
    private AssestFile assestFile;
    private String index = "0";
    private int RotateCount = 120;
    public static String use_mapName = "";
    public static String current_mapname = "";
    private RobotMap robotMap;
    private int robotVirtualId = 0;
    private int robotMapImgId = 0;
    private int mapCount = 0;
    private ArrayList<RobotPointPositions> arrayList;
    private ArrayList<ArrayList<RobotPointPositions>> robotPointArry = new ArrayList<>();
    private ArrayList<RobotPointPositions> addPointArrayList;
    private PackageManager localPackageManager;
    private int robotMapListSize = 0;
    private String totalTime = "";
    private boolean flag = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        assestFile = new AssestFile(mContext);
        LogcatHelper.getInstance(mContext).getFileLength();
        LogcatHelper.getInstance(this).createAlarmFile(this);

        localPackageManager = getPackageManager();
        spinner = mContext.getResources().getStringArray(R.array.spinner_time);
        handler.sendEmptyMessage(1);
        isNewSerialPort();
    }

    public void isNewSerialPort() {
        initView();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.d(TAG, "getEthEnable " + checkLztekLamp.getEthEnable());
                if (checkLztekLamp.getEthEnable()) {
                    navigationService = new NavigationService();
                    intentService = new Intent(getApplicationContext(), NavigationService.class);
                    startService(intentService);
                    myHandler.sendEmptyMessage(ROBOT_DEVICE);
                    myHandler.sendEmptyMessage(ROBOT_STATUS);

                    Cursor cursor = mSqLiteOpenHelperUtils.searchTaskIndex();
                    while (cursor.moveToNext()) {
                        String mapName = cursor.getString(cursor.getColumnIndex(Content.dbTaskMapName));
                        String taskName = cursor.getString(cursor.getColumnIndex(Content.dbTaskName));
                        index = cursor.getString(cursor.getColumnIndex(Content.dbTaskIndex));
                        Cursor cursorList = mSqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, mapName + Content.dbSplit + taskName);
                        if (Integer.parseInt(index) < cursorList.getCount() - 1) {
                            Log.d(TAG, "执行任务到第 " + (Integer.parseInt(index)) + "个任务");
                            Content.taskName = taskName;
                            Content.taskIndex = -1;
                            if (Content.hasLocalization) {
                                TaskManager.getInstances(mContext).use_map(use_mapName);
                                handler.post(runnable);
                            } else {
                                Log.d(TAG, "有位置，开始任务");
                                TaskManager.getInstances(mContext).startTaskQueue(use_mapName, current_mapname, Content.taskName, Integer.parseInt(index));
                            }
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
        Content.battery = SharedPrefUtil.getInstance(mContext).getSharedPrefBattery(Content.GET_LOW_BATTERY);
        Content.led = SharedPrefUtil.getInstance(mContext).getSharedPrefLed(Content.GET_LED_LEVEL);
        Content.Working_mode = SharedPrefUtil.getInstance(mContext).getSharedPrefWorkingMode(Content.GET_WORKING_MODE);
        Content.have_charging_mode = SharedPrefUtil.getInstance(mContext).getSharedPrefChargingMode(Content.GET_CHARGING_MODE);

        uvcWarning = new UvcWarning(mContext);
        checkLztekLamp = new CheckLztekLamp(mContext);
        gsonUtils = new GsonUtils();
        myHandler = new MyHandler(SocketServices.this);
        getRunningProgressCount(mContext);
        checkLztekLamp.readBatteryFactory();
        if (!checkLztekLamp.getEthEnable()) {
            Log.d(TAG, "网络设置失败");
        } else {
            Log.d(TAG, "网络设置成功");
        }
        mVirtualBeanUtils = new VirtualBeanUtils(this);
        mSqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(this);
        use_mapName = SharedPrefUtil.getInstance(mContext).getSharedPrefMapName(Content.MAP_NAME_UUID);
        Cursor cursor = mSqLiteOpenHelperUtils.searchMapName(Content.dbMapNameUuid, use_mapName);
        Log.d(TAG, "current_mapname : " + use_mapName);
        while (cursor.moveToNext() && !TextUtils.isEmpty(use_mapName)) {
            current_mapname = cursor.getString(cursor.getColumnIndex(Content.dbMapName));
            Log.d(TAG, "current_mapname111 : " + current_mapname);
        }
        mSqLiteOpenHelperUtils.close();
        mAlarmUtils = new AlarmUtils(this);
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
                        gsonUtils.setTvTime("-1");
                        gsonUtils.setTotal_time(totalTime);
                        gsonUtils.serverSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                        gsonUtils.mqttSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                        if (Content.have_charging_mode && !Content.isCharging) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.CHARGING_POINT);
                        } else if (!Content.have_charging_mode) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.InitializePositionName);
                        }
                        Content.taskName = null;
                        Content.taskIndex = -1;
                    } else {
                        Log.d(TAG, "TASK INDEX111 = " + Content.taskIndex);
                        TaskManager.getInstances(mContext).startTaskQueue(use_mapName, current_mapname, Content.taskName, Integer.parseInt(index));
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
            myHandler.sendEmptyMessageDelayed(UVC_LOOP, 0);
        } else {
            ledtime = 0;
            Content.robotState = 1;
            Content.time = 4000;
            uvcWarning.stopWarning();
            checkLztekLamp.setUvcMode(1);
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
                case UVC_LOOP:
                    Log.d(TAG, "case 1  " + workTime);
                    startLoopDetection();
                    break;
                case UVC_WORKING:
                    Log.d(TAG, "case 2  " + Content.completeFlag + ", worktime : " + workTime + ",  countTime : " + Content.countTime);
                    int time = (int) ((workTime * 1000 - Content.countTime * Content.delayTime) / 1000);
                    if (time < 0) {
                        Content.completeFlag = true;
                    }
                    if (!Content.completeFlag) {
                        startUvcDetection();
                        String tv_text = time + "";
                        Log.d(TAG, "case 2  " + tvText + " , WORKTIME :" + workTime);
                        if (!tv_text.equals(tvText)) {
                            tvText = tv_text;
                            gsonUtils.setTvTime(tvText);
                            gsonUtils.setTotal_time(totalTime);
                            gsonUtils.serverSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                            gsonUtils.mqttSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                            if (Content.taskIndex >= 0 && TaskManager.pointStateBean != null) {
                                TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount(tvText);
                                EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                            }
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
                        if (Content.taskIndex > 0 && TaskManager.pointStateBean != null) {
                            Log.d(TAG, "case 2 任务结束 ");
                            TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setPointState(mContext.getResources().getString(R.string.done));
                            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, TaskManager.pointStateBean));
                        }
                    }
                    break;
                case UVC_LOOP_WORKING:
                    Log.d(TAG, "case 3  " + workTime);
                    if (Content.taskIndex >= 0 && TaskManager.pointStateBean != null) {
                        TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount(tvText);
                        EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                    }
                    startUvcDetection();
                    break;
                case LOW_POWER:
                    Log.d(TAG, "case 4  " + workTime);
                    gsonUtils.setTvTime("-1");
                    gsonUtils.setTotal_time(totalTime);
                    gsonUtils.serverSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                    gsonUtils.mqttSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                    if (TaskManager.pointStateBean != null) {
                        TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount("LOW POWER");
                        EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                    }
                    checkLztekLamp.setUvcMode(1);
                    uvcWarning.stopWarning();
                    toLightControlBtn = false;
                    onCheckedChanged(0);
                    Content.robotState = 6;
                    Content.time = 4000;
                    if (Content.have_charging_mode) {
                        TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.CHARGING_POINT);
                    } else {
                        TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.InitializePositionName);
                    }
                    myHandler.sendEmptyMessageDelayed(CHECK_POWER, 5000);
                    myHandler.removeMessages(TASK_SUM_TIME);
                    break;
                case CHECK_DEMO_POWER:
                    if (Content.EMERGENCY || Content.robotState == 6 || Content.robotState == 4) {
                        checkLztekLamp.setUvcModeForDemo(1);
                    } else {
                        if (battery > Content.battery) {
                            checkLztekLamp.setUvcModeForDemo(0);
                        }
                    }
                    myHandler.sendEmptyMessageDelayed(CHECK_DEMO_POWER, 1000);
                    break;
                case CHECK_POWER:
                    Log.d(TAG, "case 6 : " + "检测充电状态恢复任务battery : " + battery + ",  taskName: " + Content.taskName + ",   taskState: " + Content.taskState);
                    if (TaskManager.pointStateBean != null) {
                        TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount("电量回充,消毒未完成");
                        EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
                    }
                    if (battery > Content.maxBattery && Content.taskName != null) {
                        Content.taskIsFinish = false;
                        Content.robotState = 1;
                        Content.time = 4000;
                        myHandler.sendEmptyMessage(TASK_SUM_TIME);
                        myHandler.removeMessages(CHECK_POWER);
                    } else if (Content.taskName == null) {
                        myHandler.removeMessages(CHECK_POWER);
                    } else {
                        myHandler.sendEmptyMessageDelayed(CHECK_POWER, 5000);
                    }
                    break;
                case ROBOT_DEVICE:
                    Log.d(TAG, "case 7  " + "设备信息");
                    TaskManager.getInstances(mContext).deviceStatus();
                    myHandler.sendEmptyMessageDelayed(ROBOT_DEVICE, 1000);
                    break;
//                case TASK_SUM_TIME:
//                    Log.d(TAG, "case 9  " + Content.Sum_Time);
//                    gsonUtils.setTvTime(Content.Sum_Time + "");
//                    if (Content.server != null) {
//                        Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
//                    }
//                    if (pauseTime == 0 && !isTaskFlag && Content.Sum_Time >= 0) {
//                        Content.Sum_Time = Content.Sum_Time - 1;
//                        myHandler.sendEmptyMessageDelayed(TASK_SUM_TIME, 1000);
//                    } else if (Content.Sum_Time < 0) {
//                        myHandler.removeMessages(TASK_SUM_TIME);
//                    } else {
//                        myHandler.sendEmptyMessageDelayed(TASK_SUM_TIME, 1000);
//                    }
//                    break;
                case DISCONNECT:
                    Content.CONNECT_ADDRESS = "";
                    if (TaskManager.scanningFlag) {
                        TaskManager.getInstances(mContext).cancleScanMap();
                    }
                    myHandler.removeMessages(ROBOT_MOVE);
                    break;
                case GET_ALARM_TASK://定时任务
                    getTaskQueue(msg.arg1);
                    break;
                case MOVE_UP:
                    NavigationService.move(0.2f, 0.0f);
                    myHandler.sendEmptyMessageDelayed(MOVE_UP, 20);
                    break;
                case START_ALARM_TASK:
                    myHandler.removeMessages(MOVE_UP);
                    myHandler.removeMessages(START_ALARM_TASK);
                    Content.taskIndex = 0;
                    if (!Content.hasLocalization) {
                        TaskManager.getInstances(mContext).use_map(use_mapName);
                        myHandler.post(alarmRunnable);
                    } else if (SocketServices.battery < Content.battery) {
                        Content.robotState = 6;
                        Content.time = 4000;
                        gsonUtils.setTvTime("-1");
                        gsonUtils.setTotal_time(totalTime);
                        gsonUtils.serverSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                        gsonUtils.mqttSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                        if (Content.have_charging_mode && !Content.isCharging) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.CHARGING_POINT);
                        } else if (!Content.have_charging_mode) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.InitializePositionName);
                        }
                        Content.taskName = null;
                        Content.taskIndex = -1;
                    } else {
                        TaskManager.getInstances(mContext).startTaskQueue(use_mapName, current_mapname, Content.taskName, 0);
                    }
                    break;
                case ROBOT_STATUS:
                    //1625039117972   1625036875597
                    Log.d(TAG, "ROBOT_STATUS : use_mapName : " + use_mapName);
                    TaskManager.getInstances(mContext).getPositions(use_mapName);
                    TaskManager.getInstances(mContext).cmdVel();
                    gsonUtils.setMapNameUuid(use_mapName);
                    gsonUtils.setCurrent_mapname(current_mapname);
                    if (Content.isCharging) {
                        gsonUtils.serverSendMsg(gsonUtils.getRobotStatus(Content.STATUS, "Charging at " + Content.CHARGING_POINT));
                        gsonUtils.mqttSendMsg(gsonUtils.getRobotStatus(Content.STATUS, "Charging at " + Content.CHARGING_POINT));
                    } else if (Content.is_initialize_finished == 0) {
                        gsonUtils.serverSendMsg(gsonUtils.getRobotStatus(Content.STATUS, "Initialize"));
                        gsonUtils.mqttSendMsg(gsonUtils.getRobotStatus(Content.STATUS, "Initialize"));
                    } else if (!Content.isCharging && Content.taskName == null) {
                        gsonUtils.serverSendMsg(gsonUtils.getRobotStatus(Content.STATUS, "Standby"));
                        gsonUtils.mqttSendMsg(gsonUtils.getRobotStatus(Content.STATUS, "Standby"));
                    } else {
                        gsonUtils.serverSendMsg(gsonUtils.getRobotStatus(Content.STATUS, Content.robotStatus));
                        gsonUtils.mqttSendMsg(gsonUtils.getRobotStatus(Content.STATUS, Content.robotStatus));
                    }
                    myHandler.sendEmptyMessageDelayed(ROBOT_STATUS, 1000);
                    break;
                case INITIALIZE_FAIL:
                    Content.is_initialize_finished = 2;
                    handlerInitialize.removeCallbacks(runnableInitialize);
                    myHandler.removeMessages(INITIALIZE_FAIL);
                    NavigationService.stopInitialize();
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, Content.initialize_fail));
                    break;
                case DELETE_MAP:
                    TaskManager.getInstances(mContext).deleteMap((String) msg.obj, msg.arg1);
                    break;
                case ROBOT_MOVE:
                    Log.d(TAG, "ZDZD startMove : ");
                    String obj = (String) msg.obj;
                    NavigationService.move(Float.parseFloat(obj.split(",")[0]),
                            Float.parseFloat(obj.split(",")[1]));
                    Message message = myHandler.obtainMessage();
                    message.what = ROBOT_MOVE;
                    message.obj = msg.obj;
                    myHandler.sendMessageDelayed(message, 20);
                    break;
                case CHECK_WIRELESS_APK:
                    getRunningProgressCount(mContext);
                    break;
                default:
                    break;
            }
        }
    }

    /*
     * 正在运行的进程数量
     * */
    public void getRunningProgressCount(Context context) {
        List localList = localPackageManager.getInstalledPackages(0);
        for (int i = 0; i < localList.size(); i++) {
            PackageInfo localPackageInfo1 = (PackageInfo) localList.get(i);
            String str1 = localPackageInfo1.packageName.split(":")[0];
            if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo1.applicationInfo.flags) == 0)
                    && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo1.applicationInfo.flags) == 0)
                    && ((ApplicationInfo.FLAG_STOPPED & localPackageInfo1.applicationInfo.flags) == 0)) {
                Log.v("MainActivity", "packageName :" + str1);
                if ("com.retron.wireLessApk".equals(str1)) {
                    myHandler.sendEmptyMessageDelayed(CHECK_WIRELESS_APK, 5000);
                    return;
                }
            }
            if (i == localList.size() - 1) {
                Intent intent = new Intent("com.android.robot.wireLess.start");
                intent.setPackage("com.retron.wireLessApk");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                context.sendBroadcast(intent);
            }
        }
        myHandler.sendEmptyMessageDelayed(CHECK_WIRELESS_APK, 5000);
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
                        gsonUtils.setTvTime("-1");
                        gsonUtils.setTotal_time(totalTime);
                        gsonUtils.serverSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                        gsonUtils.mqttSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
                        if (Content.have_charging_mode && !Content.isCharging) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.CHARGING_POINT);
                        } else if (!Content.have_charging_mode) {
                            TaskManager.getInstances(mContext).navigate_Position(use_mapName, Content.InitializePositionName);
                        }
                        Content.taskName = null;
                        Content.taskIndex = -1;
                    } else {
                        ///
                        TaskManager.getInstances(mContext).startTaskQueue(use_mapName, current_mapname, Content.taskName, 0);
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
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.have_charging_mode)));
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
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(Content.dbSplit)[1];
                    checkLztekLamp.setLeaveChargingLimit();
                    myHandler.sendEmptyMessageDelayed(MOVE_UP, 10000);
                    myHandler.sendEmptyMessageDelayed(START_ALARM_TASK, 12000);
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && mAlarmUtils.getTimeMillis(time).equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(Content.dbSplit)[1];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    checkLztekLamp.setLeaveChargingLimit();
                    myHandler.sendEmptyMessageDelayed(MOVE_UP, 10000);
                    myHandler.sendEmptyMessageDelayed(START_ALARM_TASK, 14000);
                } else if (TextUtils.isEmpty(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle)))
                        && "FF:FF".equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))
                        && TextUtils.isEmpty(Content.taskName)) {
                    Content.taskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(Content.dbSplit)[1];
//                    use_mapName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)).split(Content.dbSplit)[0];
                    mSqLiteOpenHelperUtils.updateAlarmTask(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)), Content.dbAlarmIsRun, "false");
                    checkLztekLamp.setLeaveChargingLimit();
                    myHandler.sendEmptyMessageDelayed(MOVE_UP, 10000);
                    myHandler.sendEmptyMessageDelayed(START_ALARM_TASK, 12000);
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
        String tv_text = (float) ((10 * 1000 - ledtime * Content.delayTime) / 1000) + "";
        if (!tvText.equals(tv_text)) {
            tvText = tv_text;
            gsonUtils.setTvTime(tvText);
            gsonUtils.setTotal_time(totalTime);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                gsonUtils.mqttSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
            }
        }
        if (Content.taskIndex > 0 && TaskManager.pointStateBean != null) {
            TaskManager.pointStateBean.getList().get(Content.taskIndex - 1).setTimeCount(tvText);
            EventBus.getDefault().post(new EventBusMessage(10038, TaskManager.pointStateBean));
        }
        if (battery <= Content.battery) {//是否到达回冲电量
            myHandler.sendEmptyMessageDelayed(LOW_POWER, Content.delayTime);
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
                myHandler.sendEmptyMessageDelayed(UVC_LOOP, Content.delayTime);
            } else {
                Log.d(TAG, "警告结束，关闭警告和led，开启uvc灯");
                myHandler.removeMessages(UVC_LOOP);
                ledtime = 0;
                uvcWarning.stopWarning();
                Content.robotState = 5;
                Content.time = 1000;
                String spinnerItem = (String) spinner[spinnerIndex];
                if (battery > Content.battery) {
                    checkLztekLamp.setUvcMode(0);
                }
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
            myHandler.sendEmptyMessageDelayed(LOW_POWER, Content.delayTime);
        } else if (!checkLztekLamp.getGpioSensorState()) {
            Log.d(TAG, "startUvcDetection" + "关led灯,开uvc灯 : ");
            if (pauseTime != 0) {
                uvcWarning.stopWarning();
                if (battery > 30) {
                    checkLztekLamp.setUvcMode(0);
                }
                pauseTime = 0;
            }
            Content.robotState = 5;
            Content.time = 1000;
            Content.countTime++;
            myHandler.sendEmptyMessageDelayed(UVC_WORKING, Content.delayTime);
        } else {
            if (pauseTime == 0) {
                pauseTime = System.currentTimeMillis();
            }
            Log.d(TAG, "startUvcDetection" + "关uvc灯");
            uvcWarning.startWarning();
            Content.robotState = 5;
            Content.time = 1000;
            checkLztekLamp.setUvcMode(1);
            myHandler.sendEmptyMessageDelayed(UVC_LOOP_WORKING, Content.delayTime);
        }
    }

    private void sendRequest() {
        gsonUtils.setMapNameUuid(use_mapName);
        gsonUtils.setCurrent_mapname(current_mapname);
        TaskManager.getInstances(mContext).getMapPic(use_mapName);
        TaskManager.getInstances(mContext).getPosition(SocketServices.use_mapName, "point");
        TaskManager.getInstances(mContext).getVirtual_obstacles(use_mapName, current_mapname, "oneMapVirtual");
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ALL_TASK_STATE, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_SETTING_MODE, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, TaskManager.pointStateBean));
    }

    @Override
    protected void onBaseEventMessage(EventBusMessage messageEvent) {
        super.onBaseEventMessage(messageEvent);
        //phone 发送的命令
        if (messageEvent.getState() == BaseEvent.START_UVC) {
            toLightControlBtn = true;
            Log.d(TAG, "spinner index" + (Integer) messageEvent.getT());
            totalTime = "" + (Integer) messageEvent.getT();
            onCheckedChanged((Integer) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.STOP_UVC) {
            toLightControlBtn = false;
            onCheckedChanged(0);
            Content.is_initialize_finished = -1;
            Content.Sum_Time = 0;
            myHandler.removeMessages(TASK_SUM_TIME);

            tvText = "0";
            gsonUtils.setTvTime(tvText);
            gsonUtils.setTotal_time(totalTime);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                gsonUtils.mqttSendMsg(gsonUtils.putTVTime(Content.TV_TIME));
            }

        } else if (messageEvent.getState() == BaseEvent.REQUEST_MSG) {//callback信息的返回
            gsonUtils.setCallback((String) messageEvent.getT());
            gsonUtils.serverSendMsg(gsonUtils.putCallBackMsg(Content.REQUEST_MSG));
        } else if (messageEvent.getState() == BaseEvent.STARTMOVE) {
            myHandler.removeMessages(ROBOT_MOVE);
            try {
                JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                float linear = (float) jsonObject.getDouble(Content.LINEAR_SPEED);
                float angular = (float) jsonObject.getDouble(Content.ANGULAR_SPEED);
                if (linear == 0 && angular == 0) {
                    myHandler.removeMessages(ROBOT_MOVE);
                } else {
                    Message message = myHandler.obtainMessage();
                    message.what = ROBOT_MOVE;
                    message.obj = linear + "," + angular;
                    myHandler.sendMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.STARTLIGHT) {//开始消毒检测
            toLightControlBtn = true;
            startDemoMode();
        } else if (messageEvent.getState() == BaseEvent.STOPLIGHT) {//停止消毒检测
            toLightControlBtn = false;
            stopDemoMode();
        } else if (messageEvent.getState() == BaseEvent.GETMAPLIST) {//地图列表
            TaskManager.getInstances(mContext).loadMapList();
        } else if (messageEvent.getState() == BaseEvent.SENDMAPNAME) {//地图列表获取后发送
            robotMap = (RobotMap) messageEvent.getT();
            mapCount = 0;
            if (!Content.is_reset_robot) {
                Log.d(TAG, "地图名字 列表 : " + robotMap.getData().size());
                if (robotMap.getData().size() == 0) {
                    SharedPrefUtil.getInstance(mContext).delete(Content.MAP_NAME_UUID);
                    use_mapName = current_mapname = "";
                    gsonUtils.setMapNameUuid(use_mapName);
                    gsonUtils.setCurrent_mapname(current_mapname);
                    sendRequest();
                    gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.SENDMAPNAME, robotPointArry, robotMap));
                    gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.SENDMAPNAME, robotPointArry, robotMap));

                } else {
                    for (int i = 0; i < robotMap.getData().size(); i++) {
                        Cursor cursor = mSqLiteOpenHelperUtils.searchMapName(Content.dbMapNameUuid, robotMap.getData().get(i).getName());
                        if (cursor.getCount() == 0) {
                            robotMap.getData().get(i).setMapName(robotMap.getData().get(i).getName());
                            mSqLiteOpenHelperUtils.saveMapName(robotMap.getData().get(i).getName(),
                                    robotMap.getData().get(i).getName(),
                                    Md5Utils.md5(robotMap.getData().get(i).getName()),
                                    "",
                                    "",
                                    "");

                        } else {
                            while (cursor.moveToNext()) {
                                robotMap.getData().get(i).setMapName(cursor.getString(cursor.getColumnIndex(Content.dbMapName)));
                                robotMap.getData().get(i).setDump_md5(Md5Utils.md5(robotMap.getData().get(i).getName()));
                                if (Content.TempMapName.equals(cursor.getString(cursor.getColumnIndex(Content.dbMapName)))) {
                                    use_mapName = robotMap.getData().get(i).getName();
                                    current_mapname = Content.TempMapName;
                                    if (!Content.hasLocalization) {
                                        TaskManager.getInstances(mContext).use_map(use_mapName);
                                    }
                                }
                            }
                        }
                        TaskManager.getInstances(mContext).getPosition(robotMap.getData().get(i).getName(), "mapList");
                    }
                    Log.d(TAG, "地图名字 ： " + use_mapName + " , 用户看到的名字 ： " + current_mapname);
                    sendRequest();
                }
            } else {
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < robotMap.getData().size(); i++) {
                            Log.d(TAG, "reset mapName : " + robotMap.getData().get(i).getName());
//                            if (i == robotMap.getData().size() - 1) {
//                                TaskManager.getInstances(mContext).deleteMap(robotMap.getData().get(i).getName(), true);
//                            } else {
                            TaskManager.getInstances(mContext).deleteMap(robotMap.getData().get(i).getName(), robotMap.getData().size());
//                            }
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
            TaskManager.getInstances(mContext).getPosition(SocketServices.use_mapName, "point");
            //存储任务之前先删除任务队列
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(messageEventT);
                String taskName = jsonObject.getString(Content.TASK_NAME);
                String mapNameUuid = jsonObject.getString(Content.MAP_NAME_UUID);
                String mapName = jsonObject.getString(Content.MAP_NAME);
                TaskManager.getInstances(mContext).deleteTaskQueue(mapNameUuid, mapName, taskName, true, messageEventT);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (messageEvent.getState() == BaseEvent.DELETETASKQUEUE) {//删除任务队列
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject((String) messageEvent.getT());
                TaskManager.getInstances(mContext).deleteTaskQueue(jsonObject.getString(Content.MAP_NAME_UUID),
                        jsonObject.getString(Content.MAP_NAME),
                        jsonObject.getString(Content.TASK_NAME), false, null);
                mSqLiteOpenHelperUtils.deleteAlarmTask(jsonObject.getString(Content.MAP_NAME_UUID)
                        + Content.dbSplit + jsonObject.getString(Content.TASK_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.SENDPOINTPOSITION) {//返回地图点数据
            mapCount++;
            RobotPositions robotPositions = (RobotPositions) messageEvent.getT();
            arrayList = new ArrayList<>();
            if (robotPositions != null && robotPositions.getData().size() > 0) {
                Cursor cursor = mSqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, robotPositions.getData().get(0).getMapName());
                while (cursor.moveToNext()) {
                    RobotPointPositions robotPointPositions = new RobotPointPositions();
                    for (int i = 0; i < robotPositions.getData().size(); i++) {
                        if (robotPositions.getData().get(i).getName().equals(cursor.getString(cursor.getColumnIndex(Content.dbPointName)))) {
                            if (robotPositions.getData().get(i).getType() == 0) {
                                continue;
                            }
                            robotPointPositions.setName(cursor.getString(cursor.getColumnIndex(Content.dbPointName)));
                            robotPointPositions.setPointTime(cursor.getInt(cursor.getColumnIndex(Content.dbSpinnerTime)));
                            robotPointPositions.setAngle(robotPositions.getData().get(i).getAngle());
                            robotPointPositions.setType(robotPositions.getData().get(i).getType());
                            robotPointPositions.setGridX(robotPositions.getData().get(i).getGridX());
                            robotPointPositions.setGridY(robotPositions.getData().get(i).getGridY());
                            robotPointPositions.setMapName(robotPositions.getData().get(i).getMapName());
                            arrayList.add(robotPointPositions);
                            Log.d(TAG, "ZDZD :POINT NAME ----: " + cursor.getString(cursor.getColumnIndex(Content.dbPointName)));
                        }
                    }
                }
            }
            robotPointArry.add(arrayList);
            Log.d(TAG, "ZDZD robotPointArry ： " + robotMap.getData().size() + ",  mapCount : " + mapCount);
            if (robotMap.getData().size() == mapCount) {
                Log.d(TAG, "ZDZD 点数据 ： " + arrayList.size() + ",   mapList: " + robotMap.getData().size());
                gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.SENDMAPNAME, robotPointArry, robotMap));
                gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.SENDMAPNAME, robotPointArry, robotMap));
                robotPointArry.clear();
                if (robotMap.getData().size() != 0) {
                    robotVirtualId = 0;
                    robotMapImgId = 0;
                    TaskManager.getInstances(mContext).getVirtual_obstacles(robotMap.getData().get(robotVirtualId).getName(), robotMap.getData().get(robotVirtualId).getMapName(), "AllVirtual");
                    //TaskManager.getInstances(mContext).download_map(robotMap.getData().get(robotMapImgId).getName());
                }
                mapCount = 0;
                //sendRequest();
            }
        } else if (messageEvent.getState() == BaseEvent.ADDPOINTPOSITION) {//返回地图点数据
            RobotPositions robotPositions = (RobotPositions) messageEvent.getT();
            addPointArrayList = new ArrayList<>();
            if (robotPositions != null && robotPositions.getData().size() > 0) {
                Cursor cursor = mSqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, robotPositions.getData().get(0).getMapName());
                while (cursor.moveToNext()) {
                    RobotPointPositions robotPointPositions = new RobotPointPositions();
                    for (int i = 0; i < robotPositions.getData().size(); i++) {
                        if (robotPositions.getData().get(i).getName().equals(cursor.getString(cursor.getColumnIndex(Content.dbPointName)))) {
                            if (robotPositions.getData().get(i).getType() == 0) {
                                continue;
                            }
                            robotPointPositions.setName(cursor.getString(cursor.getColumnIndex(Content.dbPointName)));
                            robotPointPositions.setPointTime(cursor.getInt(cursor.getColumnIndex(Content.dbSpinnerTime)));
                            robotPointPositions.setAngle(robotPositions.getData().get(i).getAngle());
                            robotPointPositions.setType(robotPositions.getData().get(i).getType());
                            robotPointPositions.setGridX(robotPositions.getData().get(i).getGridX());
                            robotPointPositions.setGridY(robotPositions.getData().get(i).getGridY());
                            robotPointPositions.setMapName(robotPositions.getData().get(i).getMapName());
                            addPointArrayList.add(robotPointPositions);
                            Log.d(TAG, "ZDZD :添加POINT NAME ----: " + cursor.getString(cursor.getColumnIndex(Content.dbPointName)));
                        }
                    }
                }
            }
            Log.d(TAG, "ZDZD 添加点数据 ： " + addPointArrayList.size() + ",   mapList: " + robotMap.getData().size());
            gsonUtils.setMapNameUuid(use_mapName);
            gsonUtils.setCurrent_mapname(current_mapname);
            gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.ADDPOINTPOSITION, addPointArrayList));
            gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.ADDPOINTPOSITION, addPointArrayList));
            //sendRequest();
        } else if (messageEvent.getState() == BaseEvent.GETMAPPIC) {//请求地图图片
            TaskManager.getInstances(mContext).getMapPic(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.SENDMAPICON) {//返回地图图片
            byte[] bytes = (byte[]) messageEvent.getT();
            if (Content.server != null) {
                gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.DOWNLOAD_LOG, "false"));
                Content.server.broadcast(bytes);
                Content.mqttServer.broadcast(bytes);
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
                TaskManager.getInstances(mContext).deletePosition(
                        use_mapName,
                        jsonObject.getString(Content.POINT_NAME),
                        positionListBean,
                        jsonObject.getInt(Content.POINT_TIME),
                        "addPosition");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.STARTTASKQUEUE) {//开始任务
            Log.d(TAG, "start task taskName : " + Content.taskName);
            try {
                mSqLiteOpenHelperUtils.updateAlarmTask(
                        use_mapName
                                + Content.dbSplit + new JSONObject((String) messageEvent.getT()).getString(Content.TASK_NAME),
                        Content.dbAlarmIsRun, "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ALL_TASK_STATE, ""));
            mSqLiteOpenHelperUtils.close();
        } else if (messageEvent.getState() == BaseEvent.STOPTASKQUEUE) {//停止任务
            Log.d(TAG, "stoptask taskName : " + (String) messageEvent.getT());
            mSqLiteOpenHelperUtils.updateAlarmTask(
                    use_mapName + Content.dbSplit + (String) messageEvent.getT(),
                    Content.dbAlarmIsRun, "false");
            mSqLiteOpenHelperUtils.close();
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ALL_TASK_STATE, ""));
        } else if (messageEvent.getState() == BaseEvent.CANCELTASKQUEUE) {//取消当前任务
            if (Content.taskName != null) {
                TaskManager.getInstances(mContext).stopTaskQueue(use_mapName);
                toLightControlBtn = false;
                onCheckedChanged(0);
            }
            Content.is_initialize_finished = -1;
            Content.Sum_Time = 0;
            myHandler.removeMessages(TASK_SUM_TIME);
        } else if (messageEvent.getState() == BaseEvent.STOPALLTASKQUEUE) {//取消所有任务
            Log.d(TAG, "stoptask taskName : " + (String) messageEvent.getT());
            mSqLiteOpenHelperUtils.updateAllAlarmTask(Content.dbAlarmIsRun, "false");
            mSqLiteOpenHelperUtils.close();
        } else if (messageEvent.getState() == BaseEvent.SENDGPSPOSITION) {//返回机器人位置
            RobotPosition robotPosition = (RobotPosition) messageEvent.getT();
            if (robotPosition != null && robotPosition.getGridPosition() != null) {
                Content.hasLocalization = true;
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
            } else {
                Content.hasLocalization = false;
                gsonUtils.setX(0);
                gsonUtils.setY(0);
                gsonUtils.setGridHeight(0);
                gsonUtils.setGridWidth(0);
                gsonUtils.setOriginX(0);
                gsonUtils.setOriginY(0);
                gsonUtils.setResolution(0);
                gsonUtils.setAngle(0);
            }
            gsonUtils.setMapNameUuid(use_mapName);
            gsonUtils.setCurrent_mapname(current_mapname);
            gsonUtils.serverSendMsg(gsonUtils.putRobotPosition(Content.SENDGPSPOSITION));
            gsonUtils.mqttSendMsg(gsonUtils.putRobotPosition(Content.SENDGPSPOSITION));
        } else if (messageEvent.getState() == BaseEvent.CMDVEL) {//机器人实时速度
            RobotCmdVel robotCmdVel = (RobotCmdVel) messageEvent.getT();
            gsonUtils.setRobotCmdVel(robotCmdVel);
        } else if (messageEvent.getState() == BaseEvent.START_SCAN_MAP) {//开始扫描地图
            TaskManager.getInstances(mContext).start_scan_map("" + System.currentTimeMillis());
        } else if (messageEvent.getState() == BaseEvent.USE_MAP) {//应用地图
            TaskManager.getInstances(mContext).use_map(use_mapName);
            TaskManager.getInstances(mContext).loadMapList();
        } else if (messageEvent.getState() == BaseEvent.INITIALIZE_RESULE) {//转圈初始化结果
            Log.d(TAG, "Initialize result ： " + (String) messageEvent.getT() + ",     isDevelop :" + isDevelop);
            if ("successed".equals((String) messageEvent.getT())) {
                handlerInitialize.removeCallbacks(runnableInitialize);
                handlerInitialize.postDelayed(runnableInitialize, 1000);
            } else {
                Content.is_initialize_finished = 2;
                handlerInitialize.removeCallbacks(runnableInitialize);
                myHandler.removeMessages(INITIALIZE_FAIL);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, Content.initialize_fail));
            }
        } else if (messageEvent.getState() == BaseEvent.IS_INITIALIZE_FINISHED) {
            Log.d(TAG, "是否完成初始化" + (Status) messageEvent.getT() + ",  Content.is_initialize_finished:  " + Content.is_initialize_finished);
            Status status = (Status) messageEvent.getT();
            if ("true".equals(status.getData())) {
                myHandler.removeMessages(INITIALIZE_FAIL);
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, Content.initialize_success));
                if (isDevelop) {
                    TaskManager.getInstances(mContext).start_develop_map(use_mapName);
                }
                Content.is_initialize_finished = 1;
                Log.d(TAG, "是否完成初始化" + Content.is_initialize_finished);
                if (Content.taskName != null) {
                    myHandler.sendEmptyMessage(TASK_SUM_TIME);
                }
            } else if ("failed".equals(status.getData())) {
                Content.is_initialize_finished = 2;
                handlerInitialize.removeCallbacks(runnableInitialize);
                myHandler.removeMessages(INITIALIZE_FAIL);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, Content.initialize_fail));
            } else if ("false".equals(status.getData())) {
                Content.is_initialize_finished = 0;
                handlerInitialize.postDelayed(runnableInitialize, 1000);
                if (!myHandler.hasMessages(INITIALIZE_FAIL)) {
                    myHandler.sendEmptyMessageDelayed(INITIALIZE_FAIL, 1 * 60 * 1000);
                }
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, Content.initializing));
            } else {
                myHandler.removeMessages(INITIALIZE_FAIL);
                Content.is_initialize_finished = 2;
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, Content.initialize_fail));
            }
        } else if (messageEvent.getState() == BaseEvent.INITIALIZE_FAIL) {
            Log.d(TAG, "是否完成初始化error: " + (String) messageEvent.getT());
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, Content.initialize_fail));
            handlerInitialize.removeCallbacks(runnableInitialize);
            Content.is_initialize_finished = 2;
        } else if (messageEvent.getState() == BaseEvent.GETPOINTPOSITION) {//请求地图点列表
            //TaskManager.getInstances(mContext).getPosition(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.CANCEL_SCAN_MAP) {//取消扫描地图并保存
            TaskManager.getInstances(mContext).stopScanMap();
        } else if (messageEvent.getState() == BaseEvent.DEVELOP_MAP) {//拓展地图
            isDevelop = true;
            if (Content.hasLocalization) {
                TaskManager.getInstances(mContext).start_develop_map(use_mapName);
            } else {
                TaskManager.getInstances(mContext).initialize(use_mapName);
            }
        } else if (messageEvent.getState() == BaseEvent.DELETE_MAP) {//删除地图

//            use_mapName = SharedPrefUtil.getInstance(mContext).getSharedPrefMapName(Content.MAP_NAME_UUID);
            // Cursor cursor = mSqLiteOpenHelperUtils.searchMapName(Content.dbMapNameUuid, use_mapName);
//            while (cursor.moveToNext() && TextUtils.isEmpty(use_mapName)) {
//                current_mapname = cursor.getString(cursor.getColumnIndex(Content.dbMapName));
//            }
            Log.d(TAG, "之前的地图名字： " + use_mapName + ", 用户看到的地图名称： " + current_mapname);
            try {
                JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                if (jsonObject.has(Content.DELETE_MAP)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(Content.DELETE_MAP);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Cursor mapnameCursor = mSqLiteOpenHelperUtils.searchMapName(Content.dbMapNameUuid, jsonArray.getString(i));
                        Log.d(TAG, "current_mapname111 : " + use_mapName + ",   mapnameCursor : " + mapnameCursor.getCount());
                        while (mapnameCursor.moveToNext()) {
                            Log.d(TAG, "current_mapname222 : " + mapnameCursor.getString(mapnameCursor.getColumnIndex(Content.dbMapName)));
                            if (Content.TempMapName.equals(mapnameCursor.getString(mapnameCursor.getColumnIndex(Content.dbMapName)))) {
                                //删除临时地图
                                use_mapName = SharedPrefUtil.getInstance(mContext).getSharedPrefMapName(Content.MAP_NAME_UUID);
                                Cursor cursor = mSqLiteOpenHelperUtils.searchMapName(Content.dbMapNameUuid, use_mapName);
                                while (cursor.moveToNext()) {
                                    current_mapname = cursor.getString(cursor.getColumnIndex(Content.dbMapName));
                                }
                                Log.d(TAG, "应用之前的地图名字： " + use_mapName);
                                TaskManager.getInstances(mContext).use_map(use_mapName);
                                Message message = myHandler.obtainMessage();
                                message.arg1 = jsonArray.length();
                                message.obj = jsonArray.get(i);
                                message.what = DELETE_MAP;
                                myHandler.sendMessageDelayed(message, 2000);
                            } else if (jsonArray.getString(i).equals(use_mapName)) {
                                SharedPrefUtil.getInstance(mContext).delete(Content.MAP_NAME_UUID);
                                use_mapName = current_mapname = "";
                                gsonUtils.setMapNameUuid(use_mapName);
                                gsonUtils.setCurrent_mapname(current_mapname);
                                TaskManager.getInstances(mContext).deleteMap(jsonArray.getString(i), jsonArray.length());
                            } else {
                                TaskManager.getInstances(mContext).deleteMap(jsonArray.getString(i), jsonArray.length());
                            }
                            Log.d(TAG, "current_mapname111 : " + current_mapname);
                        }
                    }
                    sendRequest();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.DELETE_MAP_FAIL) {//删除地图失败
            ArrayList<String> deleteMapFail = (ArrayList<String>) messageEvent.getT();
            gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.DELETE_MAP_FAIL, deleteMapFail));
            gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.DELETE_MAP_FAIL, deleteMapFail));
        } else if (messageEvent.getState() == BaseEvent.DELETE_POSITION) {//删除点
            try {
                TaskManager.getInstances(mContext).deletePosition(
                        new JSONObject((String) messageEvent.getT()).getString(Content.MAP_NAME_UUID),
                        new JSONObject((String) messageEvent.getT()).getString(Content.POINT_NAME),
                        null,
                        0,
                        "deletePosition");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.BATTERY_DATA) {//电池电量
            byte[] bytes = (byte[]) messageEvent.getT();
            battery = bytes[23];
            gsonUtils.setBattery(battery + "%");
            if (battery <= Content.battery) {
                Content.robotState = 6;
                Content.time = 1000;
            } else if (Content.robotState == 6) {
                Content.robotState = 1;
                Content.time = 4000;
            }
        } else if (messageEvent.getState() == BaseEvent.CANCEL_SCAN_MAP_NO) {//取消扫描不保存地图
            TaskManager.getInstances(mContext).cancleScanMap();
        } else if (messageEvent.getState() == BaseEvent.ROBOT_HEALTHY) {//系统健康
            gsonUtils.setHealthyMsg((String) messageEvent.getT());
            gsonUtils.serverSendMsg(gsonUtils.putSocketHealthyMsg(Content.ROBOT_HEALTHY));
            gsonUtils.mqttSendMsg(gsonUtils.putSocketHealthyMsg(Content.ROBOT_HEALTHY));
        } else if (messageEvent.getState() == BaseEvent.ROBOT_TASK_STATE) {//任务状态
            Log.d(TAG, "ZDZD ROBOT_TASK_STATE : " + BaseEvent.ROBOT_TASK_STATE);
            gsonUtils.setMapNameUuid(use_mapName);
            gsonUtils.setCurrent_mapname(current_mapname);
            gsonUtils.setTaskName(Content.taskName);
            gsonUtils.setTaskState((PointStateBean) messageEvent.getT());
            gsonUtils.serverSendMsg(gsonUtils.putSocketTaskMsg(Content.ROBOT_TASK_STATE));
            gsonUtils.mqttSendMsg(gsonUtils.putSocketTaskMsg(Content.ROBOT_TASK_STATE));
        } else if (messageEvent.getState() == BaseEvent.ROBOT_TASK_HISTORY) {//任务历史
            try {
                JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                if (jsonObject.has(Content.ROBOT_TASK_DATE)) {
                    gsonUtils.serverSendMsg(gsonUtils.putSocketTaskHistory(Content.ROBOT_TASK_HISTORY, mContext, jsonObject.getString(Content.ROBOT_TASK_DATE)));
                    gsonUtils.mqttSendMsg(gsonUtils.putSocketTaskHistory(Content.ROBOT_TASK_HISTORY, mContext, jsonObject.getString(Content.ROBOT_TASK_DATE)));
                } else {
                    gsonUtils.mqttSendMsg(gsonUtils.putSocketTaskHistory(Content.ROBOT_TASK_HISTORY, mContext, ""));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (messageEvent.getState() == BaseEvent.SEND_MQTT_VIRTUAL) {//获取虚拟强数据
            VirtualObstacleBean virtualObstacleBean = (VirtualObstacleBean) messageEvent.getT();
            robotVirtualId++;
            if (robotVirtualId < robotMap.getData().size()) {
                TaskManager.getInstances(mContext).getVirtual_obstacles(robotMap.getData().get(robotVirtualId).getName(), robotMap.getData().get(robotVirtualId).getMapName(), "AllVirtual");
            }
            gsonUtils.setVirtualObstacleBean(virtualObstacleBean);
            gsonUtils.setMapNameUuid(virtualObstacleBean.getMapNameUuid());
            gsonUtils.setCurrent_mapname(virtualObstacleBean.getMapName());
            if (Content.mqttServer != null && virtualObstacleBean != null) {
                Content.mqttServer.broadcast(gsonUtils.putVirtualObstacle(Content.SEND_MQTT_VIRTUAL));
            }
        } else if (messageEvent.getState() == BaseEvent.SEND_VIRTUAL) {//返回虚拟强数据
            VirtualObstacleBean virtualObstacleBean = (VirtualObstacleBean) messageEvent.getT();
            gsonUtils.setVirtualObstacleBean(virtualObstacleBean);
            gsonUtils.setMapNameUuid(virtualObstacleBean.getMapNameUuid());
            gsonUtils.setCurrent_mapname(virtualObstacleBean.getMapName());
            if (virtualObstacleBean != null) {
                gsonUtils.serverSendMsg(gsonUtils.putVirtualObstacle(Content.SEND_VIRTUAL));
                gsonUtils.mqttSendMsg(gsonUtils.putVirtualObstacle(Content.SEND_VIRTUAL));
            }
        } else if (messageEvent.getState() == BaseEvent.UPDATA_VIRTUAL) {//更新虚拟强
            mVirtualBeanUtils.updateVirtual(4, use_mapName, current_mapname, "carpets", null);
            mVirtualBeanUtils.updateVirtual(6, use_mapName, current_mapname, "decelerations", null);
            mVirtualBeanUtils.updateVirtual(1, use_mapName, current_mapname, "slopes", null);
            mVirtualBeanUtils.updateVirtual(5, use_mapName, current_mapname, "displays", null);
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
            mVirtualBeanUtils.updateVirtual(0, use_mapName, current_mapname, "obstacles", polylinesEntities);
        } else if (messageEvent.getState() == BaseEvent.RENAME_MAP) {//重命名地图
            String message = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(message);
                use_mapName = jsonObject.getString(Content.MAP_NAME_UUID);
                current_mapname = jsonObject.getString(Content.NEW_MAP_NAME);
                SharedPrefUtil.getInstance(mContext).setSharedPrefMapName(Content.MAP_NAME_UUID, use_mapName);
                TaskManager.getInstances(mContext).renameMapName(jsonObject.getString(Content.MAP_NAME_UUID),
                        jsonObject.getString(Content.NEW_MAP_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.RENAME_POSITION) {//重命名点
            String message = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(message);
                TaskManager.getInstances(mContext).renamePosition(jsonObject.getString(Content.MAP_NAME_UUID),
                        jsonObject.getString(Content.OLD_POINT_NAME),
                        jsonObject.getString(Content.NEW_POINT_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.DEVICES_STATUS) {//返回设备信息
            RobotDeviceStatus robotDeviceStatus = (RobotDeviceStatus) messageEvent.getT();
            gsonUtils.setPlayPathSpeedLevel(robotDeviceStatus.getData().getPlayPathSpeedLevel());
            gsonUtils.setNavigationSpeedLevel(robotDeviceStatus.getData().getNavigationSpeedLevel());
            if (Content.speed != robotDeviceStatus.getData().getSpeed()
                    || Content.EMERGENCY != (robotDeviceStatus.getData().isEmergency()
                    || robotDeviceStatus.getData().isEmergencyStop())) {
                Content.speed = robotDeviceStatus.getData().getSpeed();
                Log.d(TAG, "devices speed : " + Content.speed);
                if (Content.server != null) {
                    Log.d(TAG, "紧急急停 : " + robotDeviceStatus.getData().isEmergency() + ",   " + robotDeviceStatus.getData().isEmergencyStop());
                    if (robotDeviceStatus.getData().isEmergency() || robotDeviceStatus.getData().isEmergencyStop()) {
                        Content.EMERGENCY = true;
                        Content.taskIsFinish = false;
                        toLightControlBtn = false;
                        onCheckedChanged(0);
                    } else {
                        Content.EMERGENCY = false;
                    }
                }
            }
        } else if (messageEvent.getState() == BaseEvent.ADD_POWER_POINT) {//添加充电点
            Log.d(TAG, "Add charging : " + Content.isCharging + ",   " + angle);
            TaskManager.getInstances(mContext).deletePosition(
                    use_mapName,
                    Content.CHARGING_POINT,
                    null,
                    0,
                    "addCharging");
        } else if (messageEvent.getState() == BaseEvent.SYSTEM_DATE) {//设置系统时间
            checkLztekLamp.setSystemTime((Long) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.GET_TASK_STATE) {//是否有任务正在执行
            Cursor aTrue = mSqLiteOpenHelperUtils.searchAlarm(Content.dbAlarmMapTaskName, use_mapName);
            Log.d(TAG, "是否有正在执行的任务" + aTrue.getCount() + ", use_mapName : " + use_mapName);
            String nextTaskaskName = "", nextTaskTime = "", nextTaskCycle = "", dbIsRun = "";
            ArrayList<RunningTaskBean> runningTaskBeanArrayList = new ArrayList<>();
            int flag = 0;
            while (aTrue.moveToNext()) {
                flag++;
                ArrayList<RunningTaskBean.PointBean> pointBeanArrayList = new ArrayList<>();
                String nextTaskCycle11 = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmCycle));
                if (nextTaskaskName.equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName)))) {
                    nextTaskCycle = nextTaskCycle + nextTaskCycle11 + ",";
                    Log.d(TAG, "ZDZD GET_TASK_STATE nextTaskCycle----: " + nextTaskCycle);
                    if (flag == aTrue.getCount()) {
                        Log.d(TAG, "ZDZD GET_TASK_STATE6666 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
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
                        runningTaskBean.setTaskName(nextTaskaskName.split(Content.dbSplit)[1]);
                        runningTaskBean.setTaskAlarm(nextTaskTime);
                        runningTaskBean.setAlarmCycle(nextTaskCycle);
                        runningTaskBean.setIsRunning(dbIsRun);
                        runningTaskBean.setMapNameUuid(nextTaskaskName.split(Content.dbSplit)[0]);
                        runningTaskBean.setMapName(aTrue.getString(aTrue.getColumnIndex(Content.dbMapName)));
                        if (!nextTaskTime.equals("FF:FF")) {
                            runningTaskBeanArrayList.add(runningTaskBean);
                        }
                        nextTaskaskName = "";
                        nextTaskCycle = "";
                    }
                } else if (TextUtils.isEmpty(nextTaskaskName)) {
                    nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName));
                    nextTaskCycle = nextTaskCycle + nextTaskCycle11 + ",";
                    nextTaskTime = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime));
                    dbIsRun = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmIsRun));
                    Log.d(TAG, "ZDZD GET_TASK_STATE 2222 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
                    if (flag == aTrue.getCount()) {
                        Log.d(TAG, "ZDZD GET_TASK_STATE 1111 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
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
                        runningTaskBean.setTaskName(nextTaskaskName.split(Content.dbSplit)[1]);
                        runningTaskBean.setTaskAlarm(nextTaskTime);
                        runningTaskBean.setAlarmCycle(nextTaskCycle);
                        runningTaskBean.setIsRunning(dbIsRun);
                        runningTaskBean.setMapNameUuid(nextTaskaskName.split(Content.dbSplit)[0]);
                        if (!nextTaskTime.equals("FF:FF")) {
                            runningTaskBeanArrayList.add(runningTaskBean);
                        }
                        nextTaskaskName = "";
                        nextTaskCycle = "";
                    }
                } else {
                    Log.d(TAG, "ZDZD GET_TASK_STATE 3333 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
                    if (flag == aTrue.getCount()) {
                        nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName));
                        nextTaskCycle = nextTaskCycle11;
                        nextTaskTime = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime));
                        dbIsRun = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmIsRun));
                        Log.d(TAG, "ZDZD GET_TASK_STATE 444 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);
                    }
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
                    runningTaskBean.setTaskName(nextTaskaskName.split(Content.dbSplit)[1]);
                    runningTaskBean.setTaskAlarm(nextTaskTime);
                    runningTaskBean.setAlarmCycle(nextTaskCycle);
                    runningTaskBean.setIsRunning(dbIsRun);
                    runningTaskBean.setMapNameUuid(nextTaskaskName.split(Content.dbSplit)[0]);
                    runningTaskBean.setMapName(aTrue.getString(aTrue.getColumnIndex(Content.dbMapName)));
                    if (!nextTaskTime.equals("FF:FF")) {
                        runningTaskBeanArrayList.add(runningTaskBean);
                    }
                    nextTaskaskName = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmMapTaskName));
                    nextTaskCycle = nextTaskCycle11 + ",";
                    nextTaskTime = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime));
                    dbIsRun = aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmIsRun));
                    Log.d(TAG, "ZDZD GET_TASK_STATE 555 ----: " + nextTaskaskName + ",  " + nextTaskCycle + ",   " + nextTaskTime);

                }
            }
            mSqLiteOpenHelperUtils.close();
            gsonUtils.serverSendMsg(gsonUtils.getTaskRun(Content.GET_TASK_STATE, runningTaskBeanArrayList));
            gsonUtils.mqttSendMsg(gsonUtils.getTaskRun(Content.GET_TASK_STATE, runningTaskBeanArrayList));
        } else if (messageEvent.getState() == BaseEvent.GET_ALL_TASK_STATE) {//是否有任务正在执行
            Cursor allTrue = mSqLiteOpenHelperUtils.searchAllAlarmTask();
            Log.d(TAG, "是否有正在执行的任务" + allTrue.getCount());
            String nextAllMapTaskName = "", nextAllMapTaskTime = "", nextAllMapTaskCycle = "", dbAllMapIsRun = "";
            ArrayList<RunningTaskBean> runningAllMapTaskBeanArrayList = new ArrayList<>();
            int allMapFlag = 0;
            while (allTrue.moveToNext()) {
                allMapFlag++;
                ArrayList<RunningTaskBean.PointBean> pointBeanArrayList = new ArrayList<>();
                String nextTaskCycle22 = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmCycle));
                if (nextAllMapTaskName.equals(allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmMapTaskName)))) {
                    nextAllMapTaskCycle = nextAllMapTaskCycle + nextTaskCycle22 + ",";
                    Log.d(TAG, "ZDZD GET_ALL_TASK_STATE nextAllMapTaskCycle----: " + nextAllMapTaskCycle);
                    if (allMapFlag == allTrue.getCount()) {
                        Log.d(TAG, "ZDZD GET_TASK_STATE6666 ----: " + nextAllMapTaskName + ",  " + nextAllMapTaskCycle + ",   " + nextAllMapTaskTime);
                        Cursor pointCuror = mSqLiteOpenHelperUtils.searchPointTask(
                                Content.dbPointTaskName,
                                allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmMapTaskName)));
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
                        runningTaskBean.setTaskName(nextAllMapTaskName.split(Content.dbSplit)[1]);
                        runningTaskBean.setTaskAlarm(nextAllMapTaskTime);
                        runningTaskBean.setAlarmCycle(nextAllMapTaskCycle);
                        runningTaskBean.setIsRunning(dbAllMapIsRun);
                        runningTaskBean.setMapNameUuid(nextAllMapTaskName.split(Content.dbSplit)[0]);
                        runningTaskBean.setMapName(allTrue.getString(allTrue.getColumnIndex(Content.dbMapName)));
                        if (!nextAllMapTaskTime.equals("FF:FF")) {
                            runningAllMapTaskBeanArrayList.add(runningTaskBean);
                        }
                        nextAllMapTaskName = "";
                        nextAllMapTaskCycle = "";
                    }
                } else if (TextUtils.isEmpty(nextAllMapTaskName)) {
                    nextAllMapTaskName = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmMapTaskName));
                    nextAllMapTaskCycle = nextAllMapTaskCycle + nextTaskCycle22 + ",";
                    nextAllMapTaskTime = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmTime));
                    dbAllMapIsRun = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmIsRun));
                    Log.d(TAG, "ZDZD GET_ALL_TASK_STATE 2222 ----: " + nextAllMapTaskName + ",  " + nextAllMapTaskCycle + ",   " + nextAllMapTaskTime);
                    if (allMapFlag == allTrue.getCount()) {
                        Log.d(TAG, "ZDZD GET_ALL_TASK_STATE 1111 ----: " + nextAllMapTaskName + ",  " + nextAllMapTaskCycle + ",   " + nextAllMapTaskTime);
                        Cursor pointCuror = mSqLiteOpenHelperUtils.searchPointTask(
                                Content.dbPointTaskName,
                                allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmMapTaskName)));
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
                        runningTaskBean.setTaskName(nextAllMapTaskName.split(Content.dbSplit)[1]);
                        runningTaskBean.setTaskAlarm(nextAllMapTaskTime);
                        runningTaskBean.setAlarmCycle(nextAllMapTaskCycle);
                        runningTaskBean.setIsRunning(dbAllMapIsRun);
                        runningTaskBean.setMapNameUuid(nextAllMapTaskName.split(Content.dbSplit)[0]);
                        runningTaskBean.setMapName(allTrue.getString(allTrue.getColumnIndex(Content.dbMapName)));
                        if (!nextAllMapTaskTime.equals("FF:FF")) {
                            runningAllMapTaskBeanArrayList.add(runningTaskBean);
                        }
                        nextAllMapTaskName = "";
                        nextAllMapTaskCycle = "";
                    }
                } else {
                    Log.d(TAG, "ZDZD GET_ALL_TASK_STATE 3333 ----: " + nextAllMapTaskName + ",  " + nextAllMapTaskCycle + ",   " + nextAllMapTaskTime);
                    if (allMapFlag == allTrue.getCount()) {
                        nextAllMapTaskName = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmMapTaskName));
                        nextAllMapTaskCycle = nextTaskCycle22;
                        nextAllMapTaskTime = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmTime));
                        dbAllMapIsRun = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmIsRun));
                        Log.d(TAG, "ZDZD GET_ALL_TASK_STATE 444 ----: " + nextAllMapTaskName + ",  " + nextAllMapTaskCycle + ",   " + nextAllMapTaskTime);
                    }
                    Cursor pointCuror = mSqLiteOpenHelperUtils.searchPointTask(
                            Content.dbPointTaskName,
                            allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmMapTaskName)));
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
                    runningTaskBean.setTaskName(nextAllMapTaskName.split(Content.dbSplit)[1]);
                    runningTaskBean.setTaskAlarm(nextAllMapTaskTime);
                    runningTaskBean.setAlarmCycle(nextAllMapTaskCycle);
                    runningTaskBean.setIsRunning(dbAllMapIsRun);
                    runningTaskBean.setMapNameUuid(nextAllMapTaskName.split(Content.dbSplit)[0]);
                    runningTaskBean.setMapName(allTrue.getString(allTrue.getColumnIndex(Content.dbMapName)));
                    if (!nextAllMapTaskTime.equals("FF:FF")) {
                        runningAllMapTaskBeanArrayList.add(runningTaskBean);
                    }
                    nextAllMapTaskName = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmMapTaskName));
                    nextAllMapTaskCycle = nextTaskCycle22 + ",";
                    nextAllMapTaskTime = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmTime));
                    dbAllMapIsRun = allTrue.getString(allTrue.getColumnIndex(Content.dbAlarmIsRun));
                    Log.d(TAG, "ZDZD GET_ALL_TASK_STATE 555 ----: " + nextAllMapTaskName + ",  " + nextAllMapTaskCycle + ",   " + nextAllMapTaskTime);

                }
            }
            mSqLiteOpenHelperUtils.close();
            gsonUtils.serverSendMsg(gsonUtils.getTaskRun(Content.GET_ALL_TASK_STATE, runningAllMapTaskBeanArrayList));
            gsonUtils.mqttSendMsg(gsonUtils.getTaskRun(Content.GET_ALL_TASK_STATE, runningAllMapTaskBeanArrayList));
        } else if (messageEvent.getState() == BaseEvent.RESET_ROBOT) {//重置设备
            mSqLiteOpenHelperUtils.reset_Db(Content.dbAlarmName);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbTaskHistory);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbPointTime);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbTaskState);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbTotalCount);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbCurrentCount);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbMapNameDeatabase);
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
            gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.GET_ULTRASONIC, ultrasonicPhitBean));
            gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.GET_ULTRASONIC, ultrasonicPhitBean));
        } else if (messageEvent.getState() == BaseEvent.ROBOTVERSIONCODE) {//获取下位机版本信息
            gsonUtils.setRobotVersion((String) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.Robot_Error) {
            gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.Robot_Error, (String) messageEvent.getT()));
            gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.Robot_Error, (String) messageEvent.getT()));
        } else if (messageEvent.getState() == BaseEvent.GET_SETTING_MODE) {//获取设置信息
            Log.d(TAG, "获取设置里的信息");
            //低电量
            int sharedPrefBattery = SharedPrefUtil.getInstance(mContext).getSharedPrefBattery(Content.GET_LOW_BATTERY);
            gsonUtils.setLow_battery(sharedPrefBattery);
            //音量
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
            int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            gsonUtils.setVoice(current);
            //工作模式
            int workingMode = SharedPrefUtil.getInstance(mContext).getSharedPrefWorkingMode(Content.GET_WORKING_MODE);
            Log.d(TAG, "working_mode get " + workingMode);
            //机器人名字
            gsonUtils.setRobotName(SharedPrefUtil.getInstance(mContext).getSharedPrefRobotName(Content.ROBOT_NAME));
            gsonUtils.setWorkingMode(workingMode);
            gsonUtils.setMapNameUuid(use_mapName);
            gsonUtils.setCurrent_mapname(current_mapname);
            gsonUtils.serverSendMsg(gsonUtils.putJsonMessage(Content.GET_SETTING_MODE));
            gsonUtils.mqttSendMsg(gsonUtils.putJsonMessage(Content.GET_SETTING_MODE));
        } else if (messageEvent.getState() == BaseEvent.SET_SETTING_MODE) {//获取设置信息
            Log.d(TAG, "getType message is " + (String) messageEvent.getT());
            try {
                //有无充电桩
                JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                if (jsonObject.has(Content.GET_CHARGING_MODE)) {
                    Content.have_charging_mode = jsonObject.getBoolean(Content.GET_CHARGING_MODE);
                    SharedPrefUtil.getInstance(mContext).setSharedPrefChargingMode(Content.GET_CHARGING_MODE, Content.have_charging_mode);
                }
                //工作模式
                if (jsonObject.has(Content.GET_WORKING_MODE)) {
                    Content.Working_mode = jsonObject.getInt(Content.GET_WORKING_MODE);
                    SharedPrefUtil.getInstance(mContext).setSharedPrefWorkingMode(Content.GET_WORKING_MODE, Content.Working_mode);
                }
                //音量
                if (jsonObject.has(Content.GET_VOICE_LEVEL)) {
                    int voice = jsonObject.getInt(Content.GET_VOICE_LEVEL);
                    AudioManager mAudioManager1 = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
                    mAudioManager1.setStreamVolume(AudioManager.STREAM_MUSIC,
                            voice,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                }
                //设置回冲电量
                if (jsonObject.has(Content.GET_LOW_BATTERY)) {
                    Content.battery = jsonObject.getInt(Content.GET_LOW_BATTERY);
                    SharedPrefUtil.getInstance(mContext).setSharedPrefBattery(Content.GET_LOW_BATTERY, Content.battery);
                }
                //机器人名字
                if (jsonObject.has(Content.ROBOT_NAME)) {
                    SharedPrefUtil.getInstance(mContext).setSharedPrefRobotName(Content.ROBOT_NAME, jsonObject.getString(Content.ROBOT_NAME));
                }
                //设置速度
                if (jsonObject.has(Content.GET_PLAYPATHSPEEDLEVEL)) {
                    int playPathSpeedLevel = jsonObject.getInt(Content.GET_PLAYPATHSPEEDLEVEL);
                    TaskManager.getInstances(mContext).setSpeedLevel(playPathSpeedLevel);
                    TaskManager.getInstances(mContext).setnavigationSpeedLevel(playPathSpeedLevel);
                    gsonUtils.setPlayPathSpeedLevel(playPathSpeedLevel);
                    gsonUtils.setNavigationSpeedLevel(playPathSpeedLevel);
                }
                if (jsonObject.has(Content.GET_NAVIGATIONSPEEDLEVEL)) {
                    int navigationspeedlevel = jsonObject.getInt(Content.GET_NAVIGATIONSPEEDLEVEL);
                    TaskManager.getInstances(mContext).setSpeedLevel(navigationspeedlevel);
                    TaskManager.getInstances(mContext).setnavigationSpeedLevel(navigationspeedlevel);
                    gsonUtils.setPlayPathSpeedLevel(navigationspeedlevel);
                    gsonUtils.setNavigationSpeedLevel(navigationspeedlevel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.INITIALIZE) {
            TaskManager.getInstances(mContext).initialize(use_mapName);
        } else if (messageEvent.getState() == BaseEvent.ROBOT_TASK_ERROR) {
            gsonUtils.serverSendMsg((String) messageEvent.getT());
            gsonUtils.mqttSendMsg((String) messageEvent.getT());
//        } else if (messageEvent.getState() == BaseEvent.DOWNLOAD_MAP) {
//            try {
//                JSONArray jsonArray = (JSONArray) messageEvent.getT();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    TaskManager.getInstances(mContext).download_map(jsonArray.getString(i), jsonArray.length());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } else if (messageEvent.getState() == BaseEvent.UPLOAD_MAP) {
            try {
                JSONArray jsonArray = (JSONArray) messageEvent.getT();
                for (int i = 0; i < jsonArray.length(); i++) {
                    TaskManager.getInstances(mContext).upload_map_syn(jsonArray.getString(i), jsonArray.length());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.UPLOADMAPSYN) {
            gsonUtils.mqttSendMsg((String) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.MQTT_ADD_POINT) {
            String s = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(s);
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName(jsonObject.getString(Content.POINT_NAME));
                positionListBean.setGridX(jsonObject.getInt(Content.POINT_X));
                positionListBean.setGridY(jsonObject.getInt(Content.POINT_Y));
                positionListBean.setAngle(jsonObject.getDouble(Content.ANGLE));
                positionListBean.setType(jsonObject.getInt(Content.POINT_TYPE));
                positionListBean.setMapName(jsonObject.getString(Content.MAP_NAME_UUID));
                TaskManager.getInstances(mContext).deletePosition(
                        jsonObject.getString(Content.MAP_NAME_UUID),
                        jsonObject.getString(Content.POINT_NAME),
                        positionListBean,
                        jsonObject.getInt(Content.POINT_TIME),
                        "addPosition");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == BaseEvent.MQTT_UPDATA_VIRTUAL) {//更新虚拟强
            String mapuuid = "";
            String message = (String) messageEvent.getT();
            //最外边「」
            List<List<UpdataVirtualObstacleBean.ObstaclesEntity.PolylinesEntity>> polylinesEntities = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(message);
                JSONArray jsonArray = jsonObject.getJSONArray(Content.MQTT_UPDATA_VIRTUAL);
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
                mVirtualBeanUtils.updateVirtual(4, jsonObject.getString(Content.MAP_NAME_UUID), current_mapname, "carpets", null);
                mVirtualBeanUtils.updateVirtual(6, jsonObject.getString(Content.MAP_NAME_UUID), current_mapname, "decelerations", null);
                mVirtualBeanUtils.updateVirtual(1, jsonObject.getString(Content.MAP_NAME_UUID), current_mapname, "slopes", null);
                mVirtualBeanUtils.updateVirtual(5, jsonObject.getString(Content.MAP_NAME_UUID), current_mapname, "displays", null);
                mVirtualBeanUtils.updateVirtual(0, jsonObject.getString(Content.MAP_NAME_UUID), current_mapname, "obstacles", polylinesEntities);

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
            gsonUtils.serverSendMsg(gsonUtils.putTestSensorCallBack(Content.TEST_SENSOR));
            gsonUtils.mqttSendMsg(gsonUtils.putTestSensorCallBack(Content.TEST_SENSOR));
        } else if (messageEvent.getState() == BaseEvent.TEST_WARNINGSTART) {
            uvcWarning.startWarning();
        } else if (messageEvent.getState() == BaseEvent.TEST_WARNINGSTOP) {
            uvcWarning.stopWarning();
        } else if (messageEvent.getState() == 30001) {
            Content.isUpdate = true;
            assestFile.writeBytesToFile((ByteBuffer) messageEvent.getT());
        } else if (messageEvent.getState() == BaseEvent.PING) {
            String address = "";
            try {
                address = new JSONObject((String) messageEvent.getT()).getString(Content.Address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "PING ADDRESS : " + address + ",   当前连接的address : " + Content.CONNECT_ADDRESS);
            if (TextUtils.isEmpty(Content.CONNECT_ADDRESS)) {
                gsonUtils.setMapNameUuid(use_mapName);
                gsonUtils.setCurrent_mapname(current_mapname);
                gsonUtils.serverSendMsg(gsonUtils.putConnMsg(Content.CONN_OK));
                gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.SCANNING_MAP, TaskManager.scanningFlag));
                Content.CONNECT_ADDRESS = address;
                myHandler.sendEmptyMessageDelayed(DISCONNECT, 2000);
                Log.d(TAG, "open connect：" + Content.CONNECT_ADDRESS);

                Log.d(TAG, "has update apk : " + assestFile.hasUpdateApk());
                if (assestFile.hasUpdateApk()) {
                    assestFile.writeBytesToFile(ByteBuffer.wrap("".getBytes()));
                } else {
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPDATE_FILE_LENGTH, 0));
                }
                TaskManager.getInstances(mContext).loadMapList();
            } else if (Content.CONNECT_ADDRESS.equals(address)) {
                gsonUtils.serverSendMsg(gsonUtils.putConnMsg(Content.CONNECTED));
                myHandler.removeMessages(DISCONNECT);
                myHandler.sendEmptyMessageDelayed(DISCONNECT, 2000);
            } else if (!Content.CONNECT_ADDRESS.equals(address)) {
                gsonUtils.setAddress(Content.CONNECT_ADDRESS);
                SimpleServer.getInstance(mContext).conn.send(gsonUtils.putConnMsg(Content.NO_CONN));
            }
        } else if (messageEvent.getState() == BaseEvent.ALARM_CODE) {
            Message message = new Message();
            message.arg1 = (int) messageEvent.getT();
            message.what = GET_ALARM_TASK;
            myHandler.sendMessage(message);
        } else if (messageEvent.getState() == BaseEvent.DELETE_FILE_ALARM_CODE) {
            LogcatHelper.getInstance(mContext).getFileLength();
        } else if (messageEvent.getState() == BaseEvent.DOWNLOAD_LOG) {
            Content.isDownloadCancel = true;
            if (!flag) {
                Log.d(TAG, "start download database");
                flag = true;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            assestFile.mCopyFile(AssestFile.ROBOT_DATABASES, AssestFile.ROBOTLOG_DATABASES);
                            JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                            ArrayList<String> arrayList = new ArrayList<>();
                            for (int i = 0; i < jsonObject.getJSONArray(Content.FILE_NAME).length(); i++) {
                                arrayList.add(AssestFile.ROBOT_LOG + "/" + jsonObject.getJSONArray(Content.FILE_NAME).get(i));
                            }
                            String zipName = "" + System.currentTimeMillis();
                            try {
                                ZipUtils.zipFiles(arrayList, AssestFile.ROBOTZIP_PATH + "/" + zipName + ".zip");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "zipFileOrDirectory 压缩完成");
                            File file = new File(AssestFile.ROBOTZIP_PATH + "/" + zipName + ".zip");
                            if (file.exists()) {
                                InputStream is = null;
                                is = new FileInputStream(file);
                                Log.d(TAG, "swapStream.FileInputStream() : " + is.available());
                                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                                byte[] buff = new byte[1024 * 500];
                                int rc = 0;
                                while ((rc = is.read(buff)) > 0) {
                                    if (Content.isDownloadCancel) {
                                        swapStream.write(buff, 0, rc);
                                        Log.d(TAG, "swapStream.toByteArray() : " + swapStream.toByteArray());
                                        gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.DOWNLOAD_LOG, zipName, is.available()));
                                        Content.server.broadcast(buff);
                                        Thread.sleep(200);
                                    } else {
                                        return;
                                    }
                                }
                                is.close();
                                flag = false;
                                TaskManager.getInstances(mContext).getMapPic(use_mapName);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        } else if (messageEvent.getState() == BaseEvent.MQTT_DOWNLOAD_LOG) {
            if (!flag) {
                Log.d(TAG, "start download database");
                flag = true;
                try {
                    assestFile.mCopyFile(AssestFile.ROBOT_DATABASES, AssestFile.ROBOTLOG_DATABASES);
                    JSONObject jsonObject = new JSONObject((String) messageEvent.getT());
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i = 0; i < jsonObject.getJSONArray(Content.FILE_NAME).length(); i++) {
                        //arrayList.add(AssestFile.ROBOT_LOG + "/" + jsonObject.getJSONArray(Content.FILE_NAME).get(i));
                        assestFile.zip(
                                AssestFile.ROBOT_LOG + "/" + jsonObject.getJSONArray(Content.FILE_NAME).get(i),
                                AssestFile.ROBOTZIP_PATH+ "/" + jsonObject.getJSONArray(Content.FILE_NAME).get(i) + ".zip");
                    }
//                    String zipName = "" + System.currentTimeMillis();
//                    try {
//                        ZipUtils.zipFiles(arrayList, AssestFile.ROBOTZIP_PATH + "/" + zipName + ".zip");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    Log.d(TAG, "zipFileOrDirectory 压缩完成");
                    gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.COMPRESSED,"success"));
                    flag = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (messageEvent.getState() == BaseEvent.MQTT_RENAME_MAP) {//重命名地图
            String message = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (use_mapName.equals(jsonObject.getString(Content.MAP_NAME_UUID))) {
                    current_mapname = jsonObject.getString(Content.NEW_MAP_NAME);
                }
                TaskManager.getInstances(mContext).renameMapName(jsonObject.getString(Content.MAP_NAME_UUID),
                        jsonObject.getString(Content.NEW_MAP_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (messageEvent.getState() == BaseEvent.GET_LOG_LIST) {
            Log.d(TAG, "getLogList : " + AssestFile.ROBOT_DATABASES + ",   " + AssestFile.ROBOTLOG_DATABASES);
            ArrayList<String> logList = assestFile.getLogList();
            gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.GET_LOG_LIST, logList));
            gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.GET_LOG_LIST, logList));
//        } else if (messageEvent.getState() == BaseEvent.COPY_FILE) {
//            int length = (Integer) messageEvent.getT();
//            gsonUtils.serverSendMsg(gsonUtils.sendCopyLength(Content.COPY_FILE_LENGTH, length));
//            if (length == 100) {
//                gsonUtils.serverSendMsg(gsonUtils.startFTPServer(Content.START_PTP_SERVER, "启动FPT服务"));
//                Log.d(TAG, "启动FPT服务");
//                BroadCastUtils.getInstance(mContext).sendFTPBroadcast();
//            }
        } else if (messageEvent.getState() == BaseEvent.GO_TO_LOG_URL) {
            gsonUtils.serverSendMsg(gsonUtils.startFTPServer(Content.START_PTP_SERVER, (String) messageEvent.getT()));
        } else if (messageEvent.getState() == BaseEvent.UPDATE_FILE_LENGTH) {
            //发送已经收到的文件的长度
            gsonUtils.serverSendMsg(gsonUtils.sendRobotMsg(Content.UPDATE_FILE_LENGTH, (int) messageEvent.getT()));
        } else if (messageEvent.getState() == 88888) {
            handlerServer.postDelayed(runableServer, 10 * 1000);
        }
    }

    Handler handlerServer = new Handler();
    Runnable runableServer = new Runnable() {
        @Override
        public void run() {
            Content.server = SimpleServer.getInstance(mContext);
            Content.mqttServer = MqttWebSocketServer.getMqttInstance(mContext);
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

    private void startDemoMode() {
        if (battery > Content.battery) {
            checkLztekLamp.setUvcModeForDemo(0);
        }
        myHandler.sendEmptyMessageDelayed(CHECK_DEMO_POWER, 1000);
    }

    public static void stopDemoMode() {
        toLightControlBtn = false;
        checkLztekLamp.setUvcModeForDemo(1);
        myHandler.removeMessages(CHECK_DEMO_POWER);
    }
}
