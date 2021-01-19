package com.example.robot.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.LongDef;
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
import com.example.robot.R;
import com.example.robot.bean.PointStateBean;
import com.example.robot.bean.SaveTaskBean;
import com.example.robot.bean.TaskBean;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.AssestFile;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.utils.SharedPrefUtil;
import com.example.robot.utils.TimeUtils;
import com.example.robot.utils.VirtualBeanUtils;
import com.example.robot.uvclamp.CheckLztekLamp;
import com.example.robot.uvclamp.UvcWarning;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SocketServices extends Service {

    private static final String TAG = "SocketServices";
    private NavigationService navigationService;
    private Intent intentService;
    private Context mContext;
    private ImageView robot_Position;
    private UvcWarning uvcWarning;
    public static CheckLztekLamp checkLztekLamp;
    private GsonUtils gsonUtils;
    private String[] spinner;
    private boolean toLightControlBtn = false;
    private int ledtime = 0;
    private Long workTime;
    private String tvText;
    private TimeUtils mTimeUtils;
    public static MyHandler myHandler;
    private long pauseTime = 0;
    public static byte battery = 0;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mContext = this;
        spinner = mContext.getResources().getStringArray(R.array.spinner_time);
        initView();
        handler.sendEmptyMessage(1);

        robot_Position = new ImageView(mContext);

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
                    threadDestory = true;
                } else {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };

    private void initView() {
        uvcWarning = new UvcWarning(mContext);
        checkLztekLamp = new CheckLztekLamp(mContext);
        gsonUtils = new GsonUtils();
        myHandler = new MyHandler(SocketServices.this);
        checkLztekLamp.openBatteryPort();
        if (!checkLztekLamp.getEthEnable()) {
            Log.d(TAG, "网络设置失败");
        } else {
            Log.d(TAG, "网络设置成功");
        }
        mTimeUtils = new TimeUtils(this);
        mVirtualBeanUtils = new VirtualBeanUtils(this);
        mSqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(this);
        mAlarmUtils = new AlarmUtils(this);

        Content.robotState = 1;
        Content.time = 4000;
        checkLztekLamp.startCheckSensorAtTime();
        checkLztekLamp.startLedLamp();
        checkLztekLamp.openEth();
        checkLztekLamp.setEthAddress();
        checkLztekLamp.initUvcMode();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "server onDestroy");
        EventBus.getDefault().unregister(this);
        stopService(intentService);
        Intent intentServer = new Intent(mContext, SocketServices.class);
        mContext.startService(intentServer);
    }

    public void onCheckedChanged(int index) {
        Log.d(TAG,"onCheckedChanged : "+toLightControlBtn + ", spinner index: " + index);
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
                    Log.d(TAG, "case 2  " + Content.completeFlag + ", worktime : " + workTime + ",  countTime : " + Content.countTime );
                    int time = (int) ((workTime * 1000 - Content.countTime * Content.delayTime)/1000);
                    if (time <= 0) {
                        Content.completeFlag = true;
                    }
                    if (!Content.completeFlag) {
                        startUvcDetection();
                        //tvText = mTimeUtils.calculateDays(workTime);
                        tvText = time + "秒";
                        Log.d(TAG, "case 2  " + tvText + " , WORKTIME :" + workTime);
                        gsonUtils.setTvTime(tvText);
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                        }
                    } else {
                        gsonUtils.setTvTime("消毒完成");
                        Log.d(TAG, "case 2  " + "消毒完成");
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                        }
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
                    startUvcDetection();
                    break;
                case 4:
                    Log.d(TAG, "case 4  " + workTime);
                    gsonUtils.setTvTime("电量回充,消毒未完成");
                    if (Content.server != null) {
                        Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                    }
                    checkLztekLamp.setUvcMode(1);
                    uvcWarning.stopWarning();
                    toLightControlBtn = false;
                    onCheckedChanged(0);
                    Content.robotState = 6;
                    Content.time = 4000;
//                    if (Content.taskState != 2) {
//                        TaskManager.getInstances(mContext).pauseTaskQueue();
//                    }
                    TaskManager.getInstances(mContext).navigate_Position(Content.mapName, Content.CHARGING_POINT);
                    myHandler.sendEmptyMessageDelayed(6, 5000);
                    break;
                case 5:
                    if (checkLztekLamp.getGpioSensorState()) {
                        checkLztekLamp.setUvcModeForDemo(1);
                    } else {
                        checkLztekLamp.setUvcModeForDemo(0);
                    }
                    myHandler.sendEmptyMessageDelayed(5, 1000);
                    break;
                case 6:
                    Log.d(TAG , "case 6 : "+"检测充电状态恢复任务battery : " + battery + ",  taskName: " + Content.taskName +",   taskState: "+ Content.taskState);
                    if (battery > Content.maxBattery && Content.taskName != null) {
                        //TaskManager.getInstances(mContext).resumeTaskQueue();
                        Content.taskIsFinish = false;
                        myHandler.removeMessages(6);
                    } else if (Content.taskName == null){
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
                    Log.d(TAG, "case 8  " + "回到充电点");
                    if (!Content.isCharging) {
                        TaskManager.getInstances(mContext).navigate_Position(Content.mapName, Content.CHARGING_POINT);
                    } else {
                        Content.IS_STOP_TASK = false;
                    }
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 10秒的sensor检查
     */
    private void startLoopDetection() {
        Log.d(TAG, "startLoopDetection: " + (10 * 1000 - ledtime * Content.delayTime) + "秒");
        tvText = (float)((10 * 1000 - ledtime * Content.delayTime)/1000) + "秒";
        gsonUtils.setTvTime(tvText);
        if (Content.server != null) {
            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
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
                checkLztekLamp.setUvcMode(0);
//                Date d2 = new Date(System.currentTimeMillis());
//                long diff = d2.getTime();
                workTime = Long.parseLong(spinnerItem.substring(0, spinnerItem.length() - 2)) * 60;
                Content.countTime = 0;
                Log.d(TAG, "onCheckedChanged：workTime : " + System.currentTimeMillis() + ",    " + workTime + ",    " + Long.parseLong(spinnerItem.substring(0, spinnerItem.length() - 2)));
                startUvcDetection();
                return;
            }
        }
    }

    /**
     * 开启uvc灯
     */
    private void startUvcDetection() {
        Log.d(TAG, "startUvcDetection" + battery);
        if (!toLightControlBtn) {
            return;
        }
        if (battery <= Content.battery) {//是否到达回冲电量
            myHandler.sendEmptyMessageDelayed(4, Content.delayTime);
        } else if (!checkLztekLamp.getGpioSensorState() && !isTaskFlag) {
            Log.d(TAG, "startUvcDetection" + "关led灯,开uvc灯");
            if (pauseTime != 0) {
                uvcWarning.stopWarning();
                checkLztekLamp.setUvcMode(0);
                //workTime = workTime + System.currentTimeMillis() - pauseTime;
                pauseTime = 0;
            }
            Content.robotState = 5;
            Content.time = 1000;
            Content.countTime ++;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsg(EventBusMessage messageEvent) {
        onBaseEventMessage(messageEvent);
    }


    protected void onBaseEventMessage(EventBusMessage messageEvent) {
        //phone 发送的命令
        if (messageEvent.getState() == 1007) {
            toLightControlBtn = true;
            Log.d(TAG, "spinner index" + (Integer) messageEvent.getT());
            onCheckedChanged((Integer) messageEvent.getT());
        } else if (messageEvent.getState() == 10000) {//callback信息的返回
            if (Content.server != null) {
                gsonUtils.setCallback((String) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putCallBackMsg(Content.REQUEST_MSG));
            }
        } else if (messageEvent.getState() == 10001) {//后退
            handler1.postDelayed(runnable1, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10002) {//前进
            handler2.postDelayed(runnable2, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10003) {//左转
            handler3.postDelayed(runnable3, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10004) {//右转
            handler4.postDelayed(runnable4, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10005) {//开始消毒检测
            toLightControlBtn = true;
            startDemoMode();
        } else if (messageEvent.getState() == 10006) {//停止消毒检测
            toLightControlBtn = false;
            stopDemoMode();
        } else if (messageEvent.getState() == 10007) {//停前
            handler2.removeCallbacks(runnable2);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10008) {//停退
            handler1.removeCallbacks(runnable1);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10009) {//停左
            handler3.removeCallbacks(runnable3);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10010) {//停右
            handler4.removeCallbacks(runnable4);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10011) {//地图列表
            TaskManager.getInstances(mContext).loadMapList();
        } else if (messageEvent.getState() == 10012) {//地图列表获取后发送
            RobotMap robotMap = (RobotMap) messageEvent.getT();
            if (!Content.is_reset_robot) {
                if (Content.server != null) {
                    Content.server.broadcast(gsonUtils.putMapListMessage(Content.SENDMAPNAME, robotMap));
                }
            }
            if (Content.is_reset_robot) {
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i =0 ;i < robotMap.getData().size(); i++) {
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
        } else if (messageEvent.getState() == 10013) {//存储任务队列
            String messageEventT = (String) messageEvent.getT();
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
        } else if (messageEvent.getState() == 10014) {//删除任务队列
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

        } else if (messageEvent.getState() == 10015) {//获取任务列表
            TaskManager.getInstances(mContext).getTaskQueues((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10016) {//返回任务列表
            RobotTaskQueueList robotTaskQueueList = (RobotTaskQueueList) messageEvent.getT();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < robotTaskQueueList.getData().size(); i++) {
                list.add(robotTaskQueueList.getData().get(i).getName());
            }
            gsonUtils.setData(list);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.SENDTASKQUEUE));
            }
        } else if (messageEvent.getState() == 10017) {//返回地图点数据
            RobotPositions robotPositions = (RobotPositions) messageEvent.getT();
            gsonUtils.setmRobotPositions(robotPositions);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.SENDPOINTPOSITION));
            }
        } else if (messageEvent.getState() == 10019) {//请求地图图片
            TaskManager.getInstances(mContext).getMapPic((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10020) {//返回地图图片
            byte[] bytes = (byte[]) messageEvent.getT();
            if (Content.server != null) {
                Content.server.broadcast(bytes);
            }
        } else if (messageEvent.getState() == 10021) {//添加点
            String s = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(s);
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName(jsonObject.getString(Content.POINT_NAME));
                positionListBean.setGridX((int) x);
                positionListBean.setGridY((int) y);
                positionListBean.setAngle(angle);
                positionListBean.setType(2);
                positionListBean.setMapName(Content.mapName);
                TaskManager.getInstances(mContext).add_Position(positionListBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10022) {//开始任务
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
                Log.d(TAG, "starttask: "+cursor.getString(cursor.getColumnIndex(Content.dbAlarmMapTaskName)) + "  ,  "
                + cursor.getString(cursor.getColumnIndex(Content.dbAlarmIsRun)));
            }
            mSqLiteOpenHelperUtils.close();
        } else if (messageEvent.getState() == 10023) {//停止任务
            Log.d(TAG,"stoptask taskName : " + Content.taskName);
            if (Content.taskName != null) {
                TaskManager.getInstances(mContext).stopTaskQueue(Content.mapName);
                toLightControlBtn = false;
                onCheckedChanged(0);
            }
            mSqLiteOpenHelperUtils.updateAllAlarmTask(Content.dbAlarmIsRun, "false");
            mSqLiteOpenHelperUtils.close();
            Content.is_initialize_finished = -1;
        } else if (messageEvent.getState() == 10024) {//返回机器人位置
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
        } else if (messageEvent.getState() == 10025) {//开始扫描地图
            TaskManager.getInstances(mContext).start_scan_map((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10026) {//选定地图
            TaskManager.getInstances(mContext).getMapPic(Content.mapName);
            TaskManager.getInstances(mContext).use_map(Content.mapName);
            myHandler.removeCallbacks(runnablePosition);
            myHandler.postDelayed(runnablePosition, 1000);
        } else if (messageEvent.getState() == 10027) {//转圈初始化结果
            Log.d(TAG, "Initialize result ： " + (String) messageEvent.getT() + ",     isDevelop :" + isDevelop);
            if ("successed".equals((String) messageEvent.getT())) {
                handlerInitialize.removeCallbacks(runnableInitialize);
                handlerInitialize.postDelayed(runnableInitialize, 1000);
            }
        } else if (messageEvent.getState() == 10028) {//请求地图点列表
            TaskManager.getInstances(mContext).getPosition((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10029) {//取消扫描地图并保存
            TaskManager.getInstances(mContext).stopScanMap();
            isDevelop = false;
        } else if (messageEvent.getState() == 10030) {//拓展地图
            isDevelop = true;
            NavigationService.initialize_directly(Content.mapName);
        } else if (messageEvent.getState() == 10031) {//删除地图
            TaskManager.getInstances(mContext).deleteMap((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10032) {//删除点
            try {
                TaskManager.getInstances(mContext).deletePosition(
                        new JSONObject((String) messageEvent.getT()).getString(Content.MAP_NAME),
                        new JSONObject((String) messageEvent.getT()).getString(Content.POINT_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10033) {//电池电量
            byte[] bytes = (byte[]) messageEvent.getT();
            battery = bytes[23];
            if (Content.server != null) {
                gsonUtils.setBattery(battery + "%");
                Content.server.broadcast(gsonUtils.putBattery(Content.BATTERY_DATA));
            }
        } else if (messageEvent.getState() == 10034) {
            Log.d(TAG, "是否完成初始化" + (Status) messageEvent.getT());
            Status status = (Status) messageEvent.getT();
            if ("true".equals(status.getData())) {
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.finish_initialize)));
                if (isDevelop) {
                    TaskManager.getInstances(mContext).start_develop_map(Content.mapName);
                }
                Content.is_initialize_finished = 1;
                Log.d(TAG, "是否完成初始化" + Content.is_initialize_finished);
            } else if ("failed".equals(status.getData())){
                Content.is_initialize_finished = 2;
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.fail_initialize)));
            } else if ("false".equals(status.getData()) && Content.is_initialize_finished == 0){
                Content.is_initialize_finished = 0;
                handlerInitialize.postDelayed(runnableInitialize, 1000);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.is_initialize)));
            } else {
                Content.is_initialize_finished = 2;
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(10000, status.getErrorCode()));
            }
        } else if (messageEvent.getState() == 10035) {
            Log.d(TAG, "是否完成初始化error: " + (String) messageEvent.getT());
            EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.fail_initialize)) + (String) messageEvent.getT());
            handlerInitialize.removeCallbacks(runnableInitialize);
            Content.is_initialize_finished = 2;
        } else if (messageEvent.getState() == 10036) {//取消扫描不保存地图
            TaskManager.getInstances(mContext).cancleScanMap();
        } else if (messageEvent.getState() == 10037) {//系统健康
            if (Content.server != null) {
                gsonUtils.setHealthyMsg((String) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putSocketHealthyMsg(Content.ROBOT_HEALTHY));
            }
        } else if (messageEvent.getState() == 10038) {//任务状态
            if (Content.server != null) {
                gsonUtils.setMapName(Content.mapName);
                gsonUtils.setTaskName(Content.taskName);
                gsonUtils.setTaskState((PointStateBean) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putSocketTaskMsg(Content.ROBOT_TASK_STATE));
            }
        } else if (messageEvent.getState() == 10039) {//任务历史
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putSocketTaskHistory(Content.ROBOT_TASK_HISTORY, mContext));
            }
        } else if (messageEvent.getState() == 10041) {//获取虚拟强数据
            TaskManager.getInstances(mContext).getVirtual_obstacles((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10042) {//返回虚拟强数据
            VirtualObstacleBean virtualObstacleBean = (VirtualObstacleBean) messageEvent.getT();
            gsonUtils.setVirtualObstacleBean(virtualObstacleBean);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putVirtualObstacle(Content.SEND_VIRTUAL));
            }
        } else if (messageEvent.getState() == 10043) {//更新虚拟强
            mVirtualBeanUtils.updateVirtual(4, Content.mapName, "carpets", null);
            mVirtualBeanUtils.updateVirtual(6, Content.mapName, "decelerations", null);
            mVirtualBeanUtils.updateVirtual(1, Content.mapName, "slopes", null);
            mVirtualBeanUtils.updateVirtual(5, Content.mapName, "displays", null);
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
            mVirtualBeanUtils.updateVirtual(0, Content.mapName, "obstacles", polylinesEntities);
        } else if (messageEvent.getState() == 10044) {//定时任务
            String message = (String) messageEvent.getT();
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
        } else if (messageEvent.getState() == 10045) {//重命名地图
            String message = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(message);
                TaskManager.getInstances(mContext).renameMapName(jsonObject.getString(Content.OLD_MAP_NAME),
                        jsonObject.getString(Content.NEW_MAP_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10046) {//设置跑线速度
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject((String) messageEvent.getT());
                int playPathSpeedLevel = jsonObject.getInt(Content.SET_PLAYPATHSPEEDLEVEL);
                TaskManager.getInstances(mContext).setSpeedLevel(playPathSpeedLevel);
                TaskManager.getInstances(mContext).setnavigationSpeedLevel(playPathSpeedLevel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10047) {//重命名点
            String message = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(message);
                TaskManager.getInstances(mContext).renamePosition(jsonObject.getString(Content.MAP_NAME),
                        jsonObject.getString(Content.OLD_POINT_NAME),
                        jsonObject.getString(Content.NEW_POINT_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10048) {//获取速度
            TaskManager.getInstances(mContext).deviceStatus();
        } else if (messageEvent.getState() == 10049) {//返回设备信息
            RobotDeviceStatus robotDeviceStatus = (RobotDeviceStatus) messageEvent.getT();
            Content.speed = robotDeviceStatus.getData().getSpeed();
            Log.d(TAG, "devices speed : " + Content.speed);
            if (Content.server != null) {
                gsonUtils.setPlayPathSpeedLevel(robotDeviceStatus.getData().getPlayPathSpeedLevel());
                gsonUtils.setNavigationSpeedLevel(robotDeviceStatus.getData().getNavigationSpeedLevel());
                Log.d(TAG, "紧急急停 : "+robotDeviceStatus.getData().isEmergency() +",   " + robotDeviceStatus.getData().isEmergencyStop());
                if (robotDeviceStatus.getData().isEmergency() || robotDeviceStatus.getData().isEmergencyStop()) {
                    gsonUtils.setEmergency(true);
                    Log.d(TAG, "紧急急停");
                    Content.EMERGENCY = true;
                    Content.taskIsFinish = false;
                    toLightControlBtn = false;
                    onCheckedChanged(0);
                } else {
                    if (Content.IS_STOP_TASK && Content.EMERGENCY) {
                        myHandler.sendEmptyMessage(8);
                    }
                    gsonUtils.setEmergency(false);
                    Content.EMERGENCY = false;
                }

                Content.server.broadcast(gsonUtils.putJsonMessage(Content.DEVICES_STATUS));
            }
        } else if (messageEvent.getState() == 10050) {//添加充电点
            Log.d(TAG , "Add charging : "+Content.isCharging + ",   " + angle);
            if (Content.isCharging) {
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName(Content.CHARGING_POINT);
                positionListBean.setGridX((int) x);
                positionListBean.setGridY((int) y);
                positionListBean.setAngle(angle);
                positionListBean.setType(1);
                positionListBean.setMapName(Content.mapName);
                TaskManager.getInstances(mContext).add_Position(positionListBean);
            }
        } else if (messageEvent.getState() == 10051) {//编辑任务
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
                    gsonUtils.setEditTaskType(list);
                    gsonUtils.setTaskBeans(taskBeans);
                    Content.server.broadcast(gsonUtils.putJsonMessage(Content.EDITTASKQUEUE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (messageEvent.getState() == 10052) {//设置系统时间
            checkLztekLamp.setSystemTime((Long) messageEvent.getT());
        } else if (messageEvent.getState() == 10053) {//是否有任务正在执行
            if (Content.server != null) {
                gsonUtils.setTask_state(Content.taskName);
                gsonUtils.setMapName(Content.mapName);
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_TASK_STATE));
            }
        } else if (messageEvent.getState() == 10054) {//设置led亮度

        } else if (messageEvent.getState() == 10055) {//获取led亮度
            int level = SharedPrefUtil.getInstance(mContext).getSharedPrefLed(Content.SET_LED_LEVEL);
            Log.d(TAG,"level get led" + level);
            if (Content.server != null) {
                gsonUtils.setLed_level(level);
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_LED_LEVEL));
            }
        } else if (messageEvent.getState() == 10056) {//获取低电量回充
            int sharedPrefBattery = SharedPrefUtil.getInstance(mContext).getSharedPrefBattery(Content.SET_LOW_BATTERY);
            Log.d(TAG,"level get battery " + sharedPrefBattery);
            if (Content.server != null) {
                gsonUtils.setLow_battery(sharedPrefBattery);
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_LOW_BATTERY));
            }
        } else if (messageEvent.getState() == 10057) {//重置设备
            //TaskManager.getInstances(mContext).robot_reset();
            mSqLiteOpenHelperUtils.reset_Db(Content.dbAlarmName);
            mSqLiteOpenHelperUtils.reset_Db(Content.tableName);
            mSqLiteOpenHelperUtils.reset_Db(Content.dbPointTime);
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
        } else if (messageEvent.getState() == 10058) {//声呐设备
            TaskManager.getInstances(mContext).getUltrasonicPhit();
        } else if (messageEvent.getState() == 10059) {//返回声呐设备信息
            UltrasonicPhitBean ultrasonicPhitBean = (UltrasonicPhitBean) messageEvent.getT();
            if (Content.server != null) {
                gsonUtils.setUltrasonicPhitBean(ultrasonicPhitBean);
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_ULTRASONIC));
            }
        } else if (messageEvent.getState() == 10060) {//版本
            TaskManager.getInstances(mContext).deviceRobotVersion();
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.VERSIONCODE));
            }
        } else if (messageEvent.getState() == 10061){//获取工作模式
            int workingMode = SharedPrefUtil.getInstance(mContext).getSharedPrefWorkingMode(Content.WORKING_MODE);
            Log.d(TAG,"working_mode get " + workingMode);
            if (Content.server != null) {
                gsonUtils.setWorkingMode(workingMode);
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.GET_WORKING_MODE));
            }
        } else if (messageEvent.getState() == 10062) {//获取下位机版本信息
            if (Content.server != null) {
                gsonUtils.setRobotVersion((String) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.ROBOTVERSIONCODE));
            }
        }

//test request
        else if (messageEvent.getState() == 20001) {
            checkLztekLamp.test_uvc_start1();
        } else if (messageEvent.getState() == 20002) {
            checkLztekLamp.test_uvc_start2();
        } else if (messageEvent.getState() == 20003) {
            checkLztekLamp.test_uvc_start3();
        } else if (messageEvent.getState() == 20004) {
            checkLztekLamp.test_uvc_start4();
        } else if (messageEvent.getState() == 20005) {
            checkLztekLamp.test_uvc_stop1();
        } else if (messageEvent.getState() == 20006) {
            checkLztekLamp.test_uvc_stop2();
        } else if (messageEvent.getState() == 20007) {
            checkLztekLamp.test_uvc_stop3();
        } else if (messageEvent.getState() == 20008) {
            checkLztekLamp.test_uvc_stop4();
        } else if (messageEvent.getState() == 20009) {
            checkLztekLamp.test_uvc_startAll();
        } else if (messageEvent.getState() == 20010) {
            checkLztekLamp.test_uvc_stopAll();


        } else if (messageEvent.getState() == 20011) {
            checkLztekLamp.startLedLamp();
        } else if (messageEvent.getState() == 20012) {
            checkLztekLamp.stopLedLamp();
        } else if (messageEvent.getState() == 20013) {
            String s = checkLztekLamp.testGpioSensorState();
            gsonUtils.setTestCallBack(s);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putTestSensorCallBack(Content.TEST_SENSOR));
            }
        } else if (messageEvent.getState() == 20014) {
            uvcWarning.startWarning();
        } else if (messageEvent.getState() == 20015) {
            uvcWarning.stopWarning();
        } else if (messageEvent.getState() == 30001) {
            Content.isUpdate = true;
            AssestFile assestFile = new AssestFile(mContext);
            assestFile.writeBytesToFile((ByteBuffer) messageEvent.getT());
        } else if (messageEvent.getState() == 30002) {
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.UPDATE));
            }
        }
    }

    private Runnable runnablePosition = new Runnable() {
        @Override
        public void run() {
            TaskManager.getInstances(mContext).getPositions(Content.mapName);
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
        checkLztekLamp.setUvcModeForDemo(0);
        myHandler.sendEmptyMessageDelayed(5, 1000);
    }

    public static void stopDemoMode() {
        checkLztekLamp.setUvcModeForDemo(1);
        myHandler.removeMessages(5);
    }
}
