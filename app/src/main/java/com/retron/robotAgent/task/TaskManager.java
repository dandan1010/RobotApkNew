package com.retron.robotAgent.task;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dcm360.controller.gs.GSRobotController;
import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.gs.controller.IGsRobotController;
import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.RecordingBean;
import com.dcm360.controller.gs.controller.bean.charge_bean.ModifyRobotParam;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotWorkStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.VersionBean;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.dcm360.controller.gs.controller.bean.vel_bean.RobotCmdVel;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.dcm360.controller.utils.WebSocketUtil;
import com.google.gson.Gson;
import com.retron.robotAgent.R;
import com.retron.robotAgent.bean.PointStateBean;
import com.retron.robotAgent.bean.SaveTaskBean;
import com.retron.robotAgent.bean.TaskBean;
import com.retron.robotAgent.content.BaseEvent;
import com.retron.robotAgent.service.NavigationService;
import com.retron.robotAgent.service.SocketServices;
import com.retron.robotAgent.sqlite.SqLiteOpenHelperUtils;
import com.retron.robotAgent.utils.AlarmUtils;
import com.retron.robotAgent.utils.AssestFile;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.utils.EventBusMessage;
import com.retron.robotAgent.controller.RobotManagerController;
import com.retron.robotAgent.utils.GsonUtils;
import com.retron.robotAgent.utils.Md5Utils;
import com.uslam.bean.MapPngBean;
import com.uslam.factory.Factory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class TaskManager implements Handler.Callback {
    private static final String TAG = "TaskManager";

    private static final int START_TASK = 1001;//任务循环
    private static final int ROBOT_SPEED = 1002;//机器人速度
    private static final int REINITIALIZE = 1003;//一分钟后重新初始化机器人
    private static final int REINITIALIZE_RESULT = 1004;//重新初始化的结果
    private static final int MOVE_TO_INITIALIZE = 1005;//移动到充电桩前
    private static final int ADD_INITIALIZE_POINT = 1006;// 添加初始化的点
    private static final int REBOOT_ROBOT = 1007;// 重启导航
    private static final int GETPOINTPOSITION = 1008;//获取机器人位置
    private static final int GET_BAG = 2001;//下载bag文件
    private static final int DELETE_BAG = 2002;//删除bag文件

    private static TaskManager mTaskManager;
    public static boolean scanningFlag = false;
    public ArrayList<TaskBean> mTaskArrayList = new ArrayList<>();
    private RobotPosition mRobotPosition = null;
    private Context mContext;
    private RobotTaskQueue robotTaskQueue;
    private List<String> pois;
    private RobotPositions mRobotPointPositions = null;
    private SqLiteOpenHelperUtils sqLiteOpenHelperUtils;
    public static PointStateBean pointStateBean;
    private AlarmUtils mAlarmUtils;
    private boolean isSendType = false;
    private boolean isAddInitialize = false;
    private AssestFile mAssestFile;
    private int currentTaskArea = 0;
    private long taskCount = 0, taskTime = 0, area = 0;
    private boolean isChecked = false;
    private long recordingTime = System.currentTimeMillis();
    private boolean hasFailedTask = false;
    private String pointState = "";
    private long pointTime = 0;
    private ArrayList<String> deleteMapFail = new ArrayList<>();
    public int deleteMapSuccessCount = 0;
    public int deleteMapFailCount = 0;
    private JSONObject bagJsonObject;
    private JSONArray bagJsonArray;
    private String currentMapName = "";
    private GsonUtils gsonUtils;
    private RobotMap mRobotMap;
    private HandlerThread handlerThread;
    private Handler childHandler;


    private TaskManager(Context mContext) {
        this.mContext = mContext;
        sqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(mContext);
        mAlarmUtils = new AlarmUtils(mContext);
        mAssestFile = new AssestFile(mContext);
        bagJsonObject = new JSONObject();
        bagJsonArray = new JSONArray();
        gsonUtils = new GsonUtils();
        handlerThread = new HandlerThread("download");
        handlerThread.start();
        //子线程Handler
        childHandler = new Handler(handlerThread.getLooper(), this);
    }

    public static TaskManager getInstances(Context mContext) {
        if (mTaskManager == null) {
            synchronized (TaskManager.class) {
                if (mTaskManager == null)
                    mTaskManager = new TaskManager(mContext);
            }
        }
        return mTaskManager;
    }

    public void initialize(String mapName) {
        if (Content.is_initialize_finished != 0) {
            use_map(mapName);

            getMapPic(mapName);
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ALL_TASK_STATE, ""));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_SETTING_MODE, ""));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, TaskManager.pointStateBean));
        }
    }

    /**
     * 通过地图名字获取地图图片
     */
    public void getMapPic(String mapName) {
        if (TextUtils.isEmpty(mapName)) {
            Log.d(TAG, "getMapPic mapName is null");
            return;
        }

        if (RobotManagerController.getInstance() == null) {
            Log.d(TAG, "getMapPic RobotManagerController is null");
            return;
        }
        if (RobotManagerController.getInstance().getRobotController() == null) {
            Log.d(TAG, "getMapPic getRobotController is null ");
            return;
        }
        MapPngBean mapPngBean = new MapPngBean();
        mapPngBean.setMapName(mapName);
        Factory.getInstance(mContext, Content.ipAddress).getMapPng(mapPngBean, new RobotStatus<byte[]>() {
            @Override
            public void success(byte[] bytes) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_mapIcon) + "successed"));
                Log.d(TAG, "getMapPic  ---- : " + bytes.length);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SENDMAPICON, bytes));
                mAssestFile.saveMapPic(mapName, bytes);
            }

            @Override
            public void error(Throwable error) {
                String msg = "getMapPic failed :" + error.getMessage();
                Log.d(TAG, msg);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_mapIcon) + error.getMessage()));
            }
        });
    }

    /**
     * 获取机器人的位置
     */
    public void getPositions(String mapName) {
        Log.d(TAG, "getPositions mapName : " + mapName);
        Factory.getInstance(mContext, Content.ipAddress).getPositions(mapName, new RobotStatus<RobotPosition>() {
            @Override
            public void success(RobotPosition robotPosition) {
                mRobotPosition = robotPosition;
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SENDGPSPOSITION, robotPosition));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "gps error :" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_robotPosition) + error.getMessage()));
            }
        });
    }
    /*
     * 机器人实时角速度和线速度
     * */

    public void cmdVel() {
        Factory.getInstance(mContext, Content.ipAddress).cmdVel(new RobotStatus<RobotCmdVel>() {
            @Override
            public void success(RobotCmdVel robotCmdVel) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.CMDVEL, robotCmdVel));
            }

            @Override
            public void error(Throwable error) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, "cmdVel " + error.getMessage()));

            }
        });
    }

    /**
     * 获取地图列表
     */
    @SuppressLint("CheckResult")
    public void loadMapList() {
        Factory.getInstance(mContext, Content.ipAddress).loadMapList(new RobotStatus<RobotMap>() {
            @Override
            public void success(RobotMap robotMap) {
                mRobotMap = robotMap;
                Log.d(TAG, "ZDZD ---- loadMapList SUCCESS : " + robotMap.getData().get(0).getMapName());
                Log.d(TAG, "ZDZD ---- loadMapList SUCCESS : " + robotMap.getData().size());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_mapList) + "successed"));

                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SENDMAPNAME, robotMap));
            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    /**
     * 移动到导航点
     */
    public void navigate_Position(String mapName, String positionName) {
        Factory.getInstance(mContext, Content.ipAddress).navigate_Position(mapName, positionName, null, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "navigate " + mapName + " to " + positionName + ",     " + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.navigate_position) + positionName + "successed"));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "navigate " + mapName + " to " + positionName + " : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.navigate_position) + error.getMessage()));
            }
        });

    }

    /*
     * 获取工作状态
     * */
    public void work_status() {
        Factory.getInstance(mContext, Content.ipAddress).work_status(new RobotStatus<RobotWorkStatus>() {
            @Override
            public void success(RobotWorkStatus robotWorkStatus) {
                currentMapName = robotWorkStatus.getData().getWork_status().getCurrent_map_name();
                Log.d(TAG, "work_status : " + robotWorkStatus.getData().getWork_status().getWork_type()
                        + ", currentMapName : " + currentMapName);
                if ("SCANNING_MAP".equals(robotWorkStatus.getData().getWork_status().getWork_type())) {
                    scanningFlag = true;
                    childHandler.sendEmptyMessage(1);
                }
            }

            @Override
            public void error(Throwable error) {

            }
        });

    }

    /**
     * 开始扫描地图
     */
    public void start_scan_map(String map_name) {
        Log.d(TAG, "开始扫描 ： " + map_name + "scanningFlag : " + scanningFlag);
        if (!scanningFlag) {
            currentMapName = map_name;
            Factory.getInstance(mContext, Content.ipAddress).startScanMap(map_name, 0, new RobotStatus<Status>() {
                @Override
                public void success(Status status) {
                    scanningFlag = true;
                    Message message = childHandler.obtainMessage();
                    message.obj = map_name;
                    message.what = 1;
                    childHandler.sendMessage(message);
                    gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.SCANNING_MAP, TaskManager.scanningFlag));
                }

                @Override
                public void error(Throwable error) {
                    Log.d(TAG, "start scan map failed :  " + error.getMessage());
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.start_scanMap) + error.getMessage()));
                }
            });
        }

    }

    /**
     * 扩展扫描地图
     */
    public void start_develop_map(String map_name) {
        Factory.getInstance(mContext, Content.ipAddress).startScanMap(map_name, 1, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                currentMapName = map_name;
                Log.d(TAG, "拓展扫描成功 :  " + status.toString() + "地图名字：" + map_name);
                if ("START_SCAN_MAP_FAILED".equals(status.getErrorCode())) {
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.develop_map_fail)));
                } else {
                    scanningFlag = true;
                    Message message = childHandler.obtainMessage();
                    message.obj = map_name;
                    message.what = 1;
                    childHandler.sendMessage(message);
                    gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.SCANNING_MAP, TaskManager.scanningFlag));

                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "开始扫描成功失败 :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.develop_map_fail)));
            }
        });
    }

    /**
     * 获取实时扫地图图片png
     */
    public void scanMapPng() {
        Factory.getInstance(mContext, Content.ipAddress).scanMapPng(new RobotStatus<byte[]>() {
            @Override
            public void success(byte[] bytes) {
                Log.d(TAG, "scanMapPng :  " + bytes.length);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SENDMAPICON, bytes));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "scanMapPng failed :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_scanMap) + error.getMessage()));
            }
        });
    }

    /**
     * 取消扫描地图——保存
     */
    public void stopScanMap() {
        Content.InitializePositionName = "End";
        isAddInitialize = false;
        Factory.getInstance(mContext, Content.ipAddress).stopScanMap(currentMapName, false, false, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_scanSaveMap) + status.getMsg()));
                scanningFlag = false;
                gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.SCANNING_MAP, TaskManager.scanningFlag));
                childHandler.removeMessages(1);
                Log.d(TAG, "stopScanMap success :  " + currentMapName);
                sqLiteOpenHelperUtils.saveMapName(currentMapName, Content.TempMapName,
                        Md5Utils.md5(currentMapName),
                        "",
                        "",
                        "");
                if (SocketServices.isDevelop) {
                    download_map(currentMapName);
                }
                currentMapName = "";
                SocketServices.isDevelop = false;
                if ("successed".equals(status.getMsg())) {
                    loadMapList();
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "stopScanMap failed :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_scanSaveMap) + error.getMessage()));
            }
        });
    }

    /**
     * 取消扫描地图——不保存
     */

    public void cancleScanMap() {
        Factory.getInstance(mContext, Content.ipAddress).cancelScanMap(currentMapName, false, false, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "cancleScanMap ：" + status.getMsg());
                scanningFlag = false;
                gsonUtils.mqttSendMsg(gsonUtils.sendRobotMsg(Content.SCANNING_MAP, TaskManager.scanningFlag));
                childHandler.removeMessages(1);
                use_map(SocketServices.use_mapName);
                getMapPic(SocketServices.use_mapName);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_scanMap) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "cancleScanMap failed ：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_scanMap) + error.getMessage()));

            }
        });
    }

    /**
     * 删除地图
     */
    public void deleteMap(String mapNameUuid, int mapCount) {
        Log.d(TAG, "deleteMap mapName  :  " + mapNameUuid);
        Factory.getInstance(mContext, Content.ipAddress).deleteMap(mapNameUuid, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "deleteMap :  " + status.getMsg() + ", mapname :" + mapNameUuid);
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.delete_map) + status.getMsg()));
                if ("successed".equals(status.getMsg())) {
                    sqLiteOpenHelperUtils.deleteAlarm(mapNameUuid);
                    sqLiteOpenHelperUtils.deletePoint(Content.dbPointTaskName, mapNameUuid);
                    sqLiteOpenHelperUtils.deleteMapName(mapNameUuid);
                    mAssestFile.deleteFile(AssestFile.ROBOT_MAP + "/" + mapNameUuid + ".tar.gz");
                } else if (deleteMapFailCount > 5) {
                    deleteMapFail.add(mapNameUuid);
                } else {
                    deleteMapFailCount++;
                    deleteMap(mapNameUuid, mapCount);
                }
                deleteMapSuccessCount++;
                if (deleteMapSuccessCount >= mapCount) {
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.DELETE_MAP_FAIL, deleteMapFail));
                    deleteMapFail.clear();
                    deleteMapSuccessCount = 0;
                    loadMapList();
                }
                Log.d(TAG, "deleteMap : END--- " + status.getMsg() + ", MAPnAME :" + mapNameUuid);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "deleteMap failed :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.delete_map) + error.getMessage()));
            }
        });
    }

    /**
     * 修改地图名称
     */
    public void renameMapName(String oldMapUuid, String newMap) {
//        GsController.INSTANCE.renameMap(oldMap, newMap, new RobotStatus<Status>() {
//            @Override
//            public void success(Status status) {
//                if ("successed".equals(status.getMsg())) {
        Cursor cursor = sqLiteOpenHelperUtils.searchAllPointTask();
        while (cursor.moveToNext()) {
            String mapTaskName = cursor.getString(cursor.getColumnIndex(Content.dbPointTaskName));
            Log.d(TAG, "renameMapName-pointTaskCursor :  " + mapTaskName);
            if (mapTaskName.startsWith(oldMapUuid) && oldMapUuid.equals(Content.TempMapName)) {
                sqLiteOpenHelperUtils.updatePointTask(Content.dbPointTaskName, mapTaskName,
                        newMap);
            } else if (mapTaskName.startsWith(oldMapUuid)) {
                sqLiteOpenHelperUtils.updatePointTask(Content.dbPointTaskName, mapTaskName,
                        newMap);
            }
        }
        Cursor searchAllAlarmTask = sqLiteOpenHelperUtils.searchAllAlarmTask();
        while (searchAllAlarmTask.moveToNext()) {
            String mapTaskName = searchAllAlarmTask.getString(searchAllAlarmTask.getColumnIndex(Content.dbAlarmMapTaskName));
            Log.d(TAG, "renameMapName-alarmCursor :  " + mapTaskName);
            if (mapTaskName.startsWith(oldMapUuid)) {
                sqLiteOpenHelperUtils.updateAlarmTask(mapTaskName,
                        newMap + Content.dbSplit + mapTaskName.split(Content.dbSplit)[1]);
            }
        }
        Cursor searchAllAlarmTask1 = sqLiteOpenHelperUtils.searchAllAlarmTask();
        while (searchAllAlarmTask1.moveToNext()) {
            String mapTaskName = searchAllAlarmTask1.getString(searchAllAlarmTask1.getColumnIndex(Content.dbAlarmMapTaskName));
            Log.d(TAG, "get all alarm task key :  " + mapTaskName);
        }
        sqLiteOpenHelperUtils.updateMapName(Content.dbMapNameUuid, oldMapUuid, newMap);

        sqLiteOpenHelperUtils.close();

        download_map(oldMapUuid);

        loadMapList();
//                }
//            }
//
//            @Override
//            public void error(Throwable error) {
//                Log.d(TAG, "renameMapName failed :  " + error.getMessage());
//                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.delete_map) + error.getMessage()));
//            }
//
//        });
    }

    /**
     * 保存任务队列
     */
    public void save_taskQueue(String mapNameUuid, String mapName, String taskName, List<SaveTaskBean> list) {
        ArrayList<TaskBean> taskPositionMsg = getTaskPositionMsg(mapNameUuid, mapName, taskName, list);

//        robotTaskQueue = exeTaskPoi(mapName, taskName, taskPositionMsg);
//
//        GsController.INSTANCE.saveTaskQueue(robotTaskQueue, new RobotStatus<Status>() {
//            @Override
//            public void success(Status status) {
//                Log.d(TAG, "saveTaskQueue : " + status);
//                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.save_task) + status.getMsg()));
//                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
//            }
//
//            @Override
//            public void error(Throwable error) {
//                Log.d(TAG, "saveTaskQueue failed : " + error.getMessage());
//                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.save_task) + error.getMessage()));
//            }
//        });

    }

    private ArrayList<TaskBean> getTaskPositionMsg(String mapNameUuid, String mapName, String taskName, List<SaveTaskBean> list) {
        ArrayList<TaskBean> arrayList = new ArrayList<>();
        if (mRobotPointPositions != null) {
            for (int j = 0; j < list.size(); j++) {
                for (int i = 0; i < mRobotPointPositions.getData().size(); i++) {
                    Log.d(TAG, "list mRobotPointPositions name : " + mRobotPointPositions.getData().get(i).getName() + ",   " + list.get(j).getPositionName());
                    if (list.get(j).getPositionName().equals(mRobotPointPositions.getData().get(i).getName())) {
                        TaskBean taskBean = new TaskBean(mRobotPointPositions.getData().get(i).getName(),
                                list.get(j).getTime(),
                                mRobotPointPositions.getData().get(i).getGridX(),
                                mRobotPointPositions.getData().get(i).getGridY());
                        arrayList.add(taskBean);
                        sqLiteOpenHelperUtils.savePointTask(mapNameUuid + Content.dbSplit + taskName,
                                mRobotPointPositions.getData().get(i).getName(),
                                "" + list.get(j).getTime(),
                                "" + mRobotPointPositions.getData().get(i).getGridX(),
                                "" + mRobotPointPositions.getData().get(i).getGridY(),
                                mapName);
                    }
                }
                sqLiteOpenHelperUtils.close();
            }
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ALL_TASK_STATE, ""));
        }
        return arrayList;
    }

//    public RobotTaskQueue exeTaskPoi(String mapName, String taskName, ArrayList<TaskBean> mTaskArrayList) {
//        pois = new ArrayList<>();
//        for (int i = 0; i < mTaskArrayList.size(); i++) {
//            pois.add(mTaskArrayList.get(i).getName());
//        }
//        //pois.add(Content.CHARGING_POINT);
//
//        RobotTaskQueue taskQueue = new RobotTaskQueue();
//        taskQueue.setName(taskName);
//        taskQueue.setMap_name(mapName);
//        taskQueue.setLoop(false);
//
//        if (pois != null && pois.size() > 0) {
//            List<RobotTaskQueue.TasksBean> tasksBeans = new ArrayList<>();
//            for (int i = 0; i < pois.size(); i++) {
//                RobotTaskQueue.TasksBean bean = new RobotTaskQueue.TasksBean();
//                RobotTaskQueue.TasksBean.StartParamBean paramBean = new RobotTaskQueue.TasksBean.StartParamBean();
//                paramBean.setMap_name(mapName);
//                paramBean.setPosition_name(pois.get(i));
//                bean.setName("NavigationTask");
//                bean.setStart_param(paramBean);
//                tasksBeans.add(bean);
//            }
//            taskQueue.setTasks(tasksBeans);
//        }
//        return taskQueue;
//    }

    /**
     * 开始执行任务队列
     */
    public void startTaskQueue(String mapNameUuid, String mapName, String taskName, int taskIndex) {
        //robotStatus();
        Content.isLastTask = false;
        Content.taskIsFinish = false;
        Content.taskIndex = taskIndex;
        Log.d(TAG, "START TASK index ： " + taskIndex);
        Content.pir_timeCount = 20;
        getTaskPositionMsg(mapNameUuid, taskName);
//        robotTaskQueue = exeTaskPoi(mapName, taskName, mTaskArrayList);

        if (mTaskArrayList.size() == 0) {
            Content.taskName = null;
            Log.d(TAG, "taskList is null");
            return;
        }
        Content.startTime = System.currentTimeMillis();
        currentTaskArea = 0;
        sqLiteOpenHelperUtils.saveTaskHistory(mapNameUuid, Content.taskName,
                "-1",
                "" + mAlarmUtils.getTime(Content.startTime),
                SocketServices.battery + "%",
                SocketServices.battery + "%",
                "" + Content.taskIndex,
                pointState,
                mapName);
        sqLiteOpenHelperUtils.close();
        EventBus.getDefault().post(new EventBusMessage(10038, pointStateBean));
        handler.sendEmptyMessageDelayed(START_TASK, 0);

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == START_TASK) {
                Log.d(TAG, "start task  taskIsFinish： " + Content.taskIsFinish + ",  taskIndex: " + Content.taskIndex + " , mTaskArrayList " + mTaskArrayList.size()
                        + ",   isCharging: " + Content.isCharging + ",   Content.EMERGENCY : " + Content.EMERGENCY + ",   Content.isHightTemp : " + Content.isHightTemp);
                if (!Content.taskIsFinish && !Content.EMERGENCY && !Content.isHightTemp) {
                    isSendType = false;
                    if (Content.taskIndex < mTaskArrayList.size()) {
                        recordingTime = System.currentTimeMillis();
                        recording(Content.startRecordingOpName);
                        if (Content.Working_mode == 1) {
                            EventBus.getDefault().post(new EventBusMessage(BaseEvent.STARTLIGHT, -1));
                        }
                        Content.robotStatus = "Walking to " + mTaskArrayList.get(Content.taskIndex).getName();
                        pointState = pointState + (System.currentTimeMillis() - pointTime) / 1000 / 60 + "min; ";
                        pointTime = System.currentTimeMillis();

                        navigate_Position(SocketServices.use_mapName, mTaskArrayList.get(Content.taskIndex).getName());
                        pointStateBean.getList().get(Content.taskIndex).setPointState(mContext.getResources().getString(R.string.ongoing));
                        EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
                        Content.taskIsFinish = true;
                        Content.taskState = 1;

                        handler.removeMessages(START_TASK);
                        handler.removeMessages(ROBOT_SPEED);
                        handler.removeMessages(REINITIALIZE);
                        handler.sendEmptyMessageDelayed(ROBOT_SPEED, 1000);
                        handler.sendEmptyMessageDelayed(START_TASK, 1000);
                    } else {
                        Log.d(TAG, "remove message ");
//                        recording(Content.endRecordingOpName);
//                        handler.sendEmptyMessageDelayed(GET_BAG, 5000);
//                        if (hasFailedTask) {
//                            mAssestFile.zipFolder(AssestFile.ROBOT_BAG_PATH, AssestFile.ROBOTZIP_BAG);
//                            mAssestFile.zipFolder(AssestFile.ROBOT_INTERPRENTER, AssestFile.ROBOTZIP_INTERPRENTER);
//                            mAssestFile.zipFolder(AssestFile.ROBOT_DATABASES, AssestFile.ROBOTZIP_DATABASES);
//                        }
//                        hasFailedTask = false;
                        Content.Sum_Time = 0;
                        SocketServices.myHandler.removeMessages(9);
                        refreshTask();
                        EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, "任务完成"));
                    }
                } else {
                    handler.sendEmptyMessageDelayed(START_TASK, 1000);
                }
            } else if (msg.what == ROBOT_SPEED) {
                Log.d(TAG, "timer-1002 速度 : " + Content.speed);
                if (Content.speed == 0 && !SocketServices.toLightControlBtn) {
                    handler.sendEmptyMessageDelayed(REINITIALIZE, 1 * 60 * 1000);
                } else {
                    handler.removeMessages(REINITIALIZE);
                }
                handler.removeMessages(ROBOT_SPEED);
                handler.sendEmptyMessageDelayed(ROBOT_SPEED, 1000);
            } else if (msg.what == REINITIALIZE) {
                Log.d(TAG, "timer-1003 速度 : " + Content.speed + ",   mapName : " + SocketServices.use_mapName);
                if (Content.taskIndex < mTaskArrayList.size()) {
                    pointState = pointState + " - " + "UNREACHED ";
                    pointStateBean.getList().get(Content.taskIndex).setPointState(mContext.getResources().getString(R.string.unreached));
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
                    if (Content.taskIndex == mTaskArrayList.size() - 1 && Content.isCharging) {
                        Content.taskIsFinish = false;
                        Content.taskIndex++;
                        sqLiteOpenHelperUtils.updateTaskIndex(Content.dbTaskIndex,
                                "" + Content.taskIndex,
                                "" + mAlarmUtils.getTime(Content.startTime));
                        sqLiteOpenHelperUtils.close();
                    } else {
                        isSendType = true;
                        Content.is_initialize_finished = 0;
                        NavigationService.initGlobal(SocketServices.use_mapName);
                        handler.removeMessages(REINITIALIZE_RESULT);
                        handler.sendEmptyMessageDelayed(REINITIALIZE_RESULT, 1000);
                    }

                } else if (!Content.isCharging) {
                    Content.taskIsFinish = false;
                    navigate_Position(SocketServices.use_mapName, Content.CHARGING_POINT);
                    pointStateBean.getList().get(Content.taskIndex).setPointState(mContext.getResources().getString(R.string.ongoing));
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
                }
                handler.removeMessages(REINITIALIZE);
            } else if (msg.what == REINITIALIZE_RESULT) {
                Log.d(TAG, "timer-1004 速度 : " + Content.is_initialize_finished);
                if (Content.is_initialize_finished == 0) {
                    handler.sendEmptyMessageDelayed(REINITIALIZE_RESULT, 1000);
                } else {
                    Content.taskIsFinish = false;
                    Content.taskIndex++;
                    sqLiteOpenHelperUtils.updateTaskIndex(Content.dbTaskIndex,
                            "" + Content.taskIndex,
                            "" + mAlarmUtils.getTime(Content.startTime));
                    sqLiteOpenHelperUtils.close();
                    handler.removeMessages(REINITIALIZE_RESULT);
                }
            } else if (msg.what == MOVE_TO_INITIALIZE) {
                NavigationService.move(0.2f, 0.0f);
                handler.sendEmptyMessageDelayed(MOVE_TO_INITIALIZE, 10);
            } else if (msg.what == ADD_INITIALIZE_POINT) {
                handler.removeMessages(MOVE_TO_INITIALIZE);
                handler.removeMessages(ADD_INITIALIZE_POINT);
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName(Content.InitializePositionName);
                positionListBean.setGridX((int) mRobotPosition.getGridPosition().getX());
                positionListBean.setGridY((int) mRobotPosition.getGridPosition().getY());
                positionListBean.setAngle(mRobotPosition.getAngle());
                positionListBean.setType(0);
                positionListBean.setMapName(SocketServices.use_mapName);
                add_Position(positionListBean, -1);
            } else if (msg.what == REBOOT_ROBOT) {
                isChecked = false;
                if (mAssestFile.getFileCount() == 3) {
                    mAssestFile.deepFile((String) msg.obj);
                    reboot();
                } else if (mAssestFile.getFileCount() < 3) {
                    mAssestFile.deepFile((String) msg.obj);
                } else {
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.Robot_Error, Content.Robot_Error));
                }
//            } else if (msg.what == GETPOINTPOSITION) {
//                getPositions((String) msg.obj);
//                handler.removeMessages(GETPOINTPOSITION);
//                Message message = handler.obtainMessage();
//                message.obj = msg.obj;
//                message.what = GETPOINTPOSITION;
//                handler.sendMessageDelayed(message, 1000);
            } else if (msg.what == GET_BAG) {
                getBag(mAlarmUtils.getTime(recordingTime).replace(" ", "_") + "_0.bag");
            } else if (msg.what == DELETE_BAG) {
                deleteBag(mAlarmUtils.getTime(recordingTime).replace(" ", "_") + "_0.bag");

            }
        }
    };

    public void reboot() {
        Factory.getInstance(mContext, Content.ipAddress).reboot(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "REBOOT ----" + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "REBOOT ----" + error.getMessage());
            }
        });
    }

    public ArrayList<TaskBean> getTaskPositionMsg(String mapName, String taskName) {
        mTaskArrayList.clear();
        pointStateBean = new PointStateBean();
        pointStateBean.setMapName(mapName);
        pointStateBean.setTaskName(taskName);
        List<PointStateBean.PointState> pointStates = new ArrayList<>();
        Cursor cursor = sqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, mapName + Content.dbSplit + taskName);
        Log.d(TAG, "get task msg ：" + cursor.getCount() + "   ,mapName : " + mapName + " , taskName : " + taskName);
        while (cursor.moveToNext()) {
            TaskBean taskBean = new TaskBean(cursor.getString(cursor.getColumnIndex(Content.dbPointName)),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbSpinnerTime))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbPointX))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbPointY))));
            mTaskArrayList.add(taskBean);
            PointStateBean.PointState pointState = new PointStateBean.PointState();
            pointState.setPointName(cursor.getString(cursor.getColumnIndex(Content.dbPointName)));
            pointState.setPointState(mContext.getResources().getString(R.string.not_work));
            String[] spinnerItem = mContext.getResources().getStringArray(R.array.spinner_time);
            pointState.setTimeCount(Integer.parseInt(spinnerItem[Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbSpinnerTime)))]) * 60 + "");
            pointStates.add(pointState);
            Content.Sum_Time = Content.Sum_Time
                    + (Integer.parseInt(spinnerItem[Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbSpinnerTime)))]) * 60)
                    + 60;
            Log.d("zdzd : ", "时间 ： " + spinnerItem[Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbSpinnerTime)))] + ",   " + Content.Sum_Time);
        }
        String point_Name = "";
        if (Content.have_charging_mode) {
            point_Name = Content.CHARGING_POINT;
        } else {
            point_Name = Content.InitializePositionName;
        }
        TaskBean taskBean = new TaskBean(point_Name,
                0,
                0,
                0);
        PointStateBean.PointState pointState = new PointStateBean.PointState();
        pointState.setPointName(point_Name);
        pointState.setPointState(mContext.getResources().getString(R.string.not_work));
        pointState.setTimeCount("0");
        pointStates.add(pointState);
        mTaskArrayList.add(taskBean);
        pointStateBean.setList(pointStates);
        sqLiteOpenHelperUtils.close();
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
        return mTaskArrayList;
    }

    /**
     * 结束任务
     */
    public void stopTaskQueue(String mapName) {
        //NavigationService.stopInitialize();
        Log.d(TAG, "stopTaskQueue taskName : " + Content.taskName + " , charging : " + Content.isCharging
                + "time：" + (System.currentTimeMillis() - Content.startTime) / 1000 / 60 + ",  mapName : " + mapName);
        if (!Content.isCharging) {
            Content.IS_STOP_TASK = true;
            if (Content.have_charging_mode) {
                navigate_Position(mapName, Content.CHARGING_POINT);
            } else {
                navigate_Position(mapName, Content.InitializePositionName);
            }
        }
        if (Content.taskIndex == 0) {
            for (int i = Content.taskIndex; i < mTaskArrayList.size(); i++) {
                pointState = pointState + mTaskArrayList.get(i).getName() + "-" + "Unexecute; ";
            }
        } else if (Content.taskIndex > 0) {
            pointState = pointState + (System.currentTimeMillis() - pointTime) / 1000 / 60 + "min; ";
            for (int i = Content.taskIndex; i < mTaskArrayList.size(); i++) {
                pointState = pointState + mTaskArrayList.get(i).getName() + "-" + "Unexecute; ";
            }
        }
        refreshTask();
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_task) + "successed"));
    }

    public void refreshTask() {
        handler.removeMessages(START_TASK);
        handler.removeMessages(ROBOT_SPEED);
        handler.removeMessages(REINITIALIZE);

        Pattern p = Pattern.compile("\\d+");
        Matcher matcher = p.matcher(pointState);
        if (matcher.find()) {
            sqLiteOpenHelperUtils.updateHistory(Content.dbTime,
                    "" + ((System.currentTimeMillis() - Content.startTime) / 1000 / 60),
                    mAlarmUtils.getTime(Content.startTime),
                    SocketServices.battery + "%",
                    pointState.substring(matcher.group().length() + 6, pointState.length() - 1));
        }
        sqLiteOpenHelperUtils.saveTaskState(SocketServices.use_mapName,
                Content.taskName,
                "" + pointStateBean.toString().replace("'", ""),
                mAlarmUtils.getTime(Content.startTime),
                SocketServices.current_mapname);
        //更新总任务个数统计
        Cursor totalCursor = sqLiteOpenHelperUtils.searchTaskTotalCount();
        taskCount = 0;
        taskTime = 0;
        area = 0;
        while (totalCursor.moveToNext()) {
            taskCount = Long.parseLong(totalCursor.getString(totalCursor.getColumnIndex(Content.dbTaskTotalCount)));
            taskTime = Long.parseLong(totalCursor.getString(totalCursor.getColumnIndex(Content.dbTimeTotalCount)));
            area = Long.parseLong(totalCursor.getString(totalCursor.getColumnIndex(Content.dbAreaTotalCount)));
        }
        sqLiteOpenHelperUtils.reset_Db(Content.dbTotalCount);
        sqLiteOpenHelperUtils.saveTaskTotalCount((taskCount + 1) + "", (taskTime + (System.currentTimeMillis() - Content.startTime)) + "", (area + currentTaskArea) + "");
        //当月任务
        Cursor currentCursor = sqLiteOpenHelperUtils.searchTaskCurrentCount(mAlarmUtils.getTimeMonth(System.currentTimeMillis()));
        taskCount = 0;
        taskTime = 0;
        area = 0;
        while (currentCursor.moveToNext()) {
            taskCount = Long.parseLong(currentCursor.getString(currentCursor.getColumnIndex(Content.dbTaskCurrentCount)));
            taskTime = Long.parseLong(currentCursor.getString(currentCursor.getColumnIndex(Content.dbTimeCurrentCount)));
            area = Long.parseLong(currentCursor.getString(currentCursor.getColumnIndex(Content.dbAreaCurrentCount)));
        }
        sqLiteOpenHelperUtils.reset_Db(Content.dbCurrentCount);
        sqLiteOpenHelperUtils.saveTaskCurrentCount((taskCount + 1) + "", (taskTime + (System.currentTimeMillis() - Content.startTime)) + "", (area + currentTaskArea) + "", mAlarmUtils.getTimeMonth(System.currentTimeMillis()));

        //删除立即执行的任务
        Cursor aTrue = sqLiteOpenHelperUtils.searchAllAlarmTask();
        while (aTrue.moveToNext()) {
            if ("FF:FF".equals(aTrue.getString(aTrue.getColumnIndex(Content.dbAlarmTime)))) {
                sqLiteOpenHelperUtils.deleteAlarmTask(SocketServices.use_mapName + Content.dbSplit + Content.taskName);
                deleteTaskQueue(SocketServices.use_mapName, SocketServices.current_mapname, Content.taskName, false, null);
            }
        }
        pointState = "";
        Content.taskIndex = -1;
        pointStateBean.getList().clear();
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ALL_TASK_STATE, ""));
        //EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_HISTORY, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_HISTORY,
                "{\"type\":\"robot_task_history\",\"robot_task_date\":\"" + mAlarmUtils.getTimeYear(Content.startTime) + "\"}"));
        sqLiteOpenHelperUtils.close();
        currentTaskArea = 0;
        Content.taskName = null;
        mTaskArrayList.clear();
        Content.startTime = System.currentTimeMillis();

        Content.taskState = 0;
        isSendType = false;
        Content.taskIndex = -1;
        Content.taskIsFinish = false;
        Content.robotState = 1;
        Content.time = 4000;
        Content.taskName = null;
        mTaskArrayList.clear();
        SocketServices.checkLztekLamp.setUvcMode(1);
        if (Content.Working_mode == 1) {
            SocketServices.stopDemoMode();
        }
        try {
            bagJsonObject.put(GsonUtils.TYPE, Content.ROBOT_TASK_ERROR);
            bagJsonObject.put(Content.REPORT_TIME, mAlarmUtils.getTime(System.currentTimeMillis()));
            bagJsonObject.put(Content.ROBOT_TASK_ERROR, bagJsonArray);
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_ERROR, bagJsonObject.toString()));
            bagJsonArray = null;
            bagJsonArray = new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除任务队列
     */
    public void deleteTaskQueue(String mapNameUuid, String mapName, String task_name, boolean needAddTask, String message) {
        sqLiteOpenHelperUtils.deleteAlarmTask(mapNameUuid + Content.dbSplit + task_name);
        sqLiteOpenHelperUtils.deletePointTask(Content.dbPointTaskName, mapNameUuid + Content.dbSplit + task_name);

        if (needAddTask) {
            //存储任务队列
            try {
                JSONObject jsonObject = new JSONObject(message);
                List<SaveTaskBean> points = new ArrayList<>();
                for (int i = 0; i < jsonObject.getJSONArray(Content.SAVETASKQUEUE).length(); i++) {
                    SaveTaskBean saveTaskBean = new SaveTaskBean();
                    JSONObject jsonObject1 = (JSONObject) jsonObject.getJSONArray(Content.SAVETASKQUEUE).get(i);
                    saveTaskBean.setPositionName(jsonObject1.getString(Content.POINT_NAME));
                    saveTaskBean.setTime(jsonObject1.getInt(Content.POINT_TIME));
                    points.add(saveTaskBean);
                }
                TaskManager.getInstances(mContext).save_taskQueue(mapNameUuid, mapName, task_name, points);
                JSONArray jsonArray = jsonObject.getJSONArray(Content.dbAlarmCycle);
                String isRun = "";
                if (jsonObject.has(Content.dbAlarmIsRun)) {
                    isRun = jsonObject.getString(Content.dbAlarmIsRun);
                } else {
                    isRun = "true";
                }
                if (jsonArray.length() == 0) {
                    sqLiteOpenHelperUtils.saveAlarmTask(
                            jsonObject.getString(Content.MAP_NAME_UUID) + Content.dbSplit + jsonObject.getString(Content.TASK_NAME),
                            jsonObject.getString(Content.dbAlarmTime),
                            "",
                            isRun,
                            mapName);
                    Intent intent = new Intent(Content.AlarmAction);
                    mContext.sendBroadcast(intent);
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        sqLiteOpenHelperUtils.saveAlarmTask(
                                jsonObject.getString(Content.MAP_NAME_UUID) + Content.dbSplit + jsonObject.getString(Content.TASK_NAME),
                                jsonObject.getString(Content.dbAlarmTime),
                                jsonArray.getString(i),
                                isRun,
                                mapName);
                    }
                }
                sqLiteOpenHelperUtils.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_TASK_STATE, ""));
        EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_ALL_TASK_STATE, ""));
    }

    /**
     * 添加点
     */
    public void add_Position(PositionListBean positionListBean, int add_point_time) {
        Factory.getInstance(mContext, Content.ipAddress).add_Position(positionListBean, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "addPosition success : " + status.getMsg());
                if ("successed".equals(status.getMsg())) {
                    Log.d(TAG, "addPosition success : " + positionListBean.getName() + ",    " + Content.isCharging);
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.add_position) + status.getMsg()));
                    if (Content.isCharging) {
                        handler.sendEmptyMessageDelayed(MOVE_TO_INITIALIZE, 0);
                        handler.sendEmptyMessageDelayed(ADD_INITIALIZE_POINT, 2000);
                    }
                    sqLiteOpenHelperUtils.savePointTask(SocketServices.use_mapName,
                            positionListBean.getName(),
                            "" + add_point_time,
                            "" + positionListBean.getGridX(),
                            "" + positionListBean.getGridY(),
                            SocketServices.current_mapname);
                    sqLiteOpenHelperUtils.close();
                    getPosition(SocketServices.use_mapName, "point");
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "addPosition failed : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.add_position) + error.getMessage()));
            }
        });
    }

    /**
     * 删除点
     */
    public void deletePosition(String mapName, String positionName, PositionListBean positionBean, int add_point_time, String type) {
        Log.d(TAG, "deletePosition : " + mapName + ",  positionName : " + positionName);
        Factory.getInstance(mContext, Content.ipAddress).deletePosition(mapName, positionName, positionBean, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "deletePosition success");
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.delete_position) + status.getMsg()));
                sqLiteOpenHelperUtils.deletePointTask(Content.dbPointName, positionName);
                if (type.equals("addPosition")) {
                    add_Position(positionBean, add_point_time);
                } else if (type.equals("addCharging")) {
                    if (Content.isCharging && positionName.equals(Content.CHARGING_POINT) && mRobotPosition != null) {
                        PositionListBean positionListBean = new PositionListBean();
                        positionListBean.setName(Content.CHARGING_POINT);
                        positionListBean.setGridX((int) mRobotPosition.getGridPosition().getX());
                        positionListBean.setGridY((int) mRobotPosition.getGridPosition().getY());
                        positionListBean.setAngle(mRobotPosition.getAngle());
                        positionListBean.setType(1);
                        positionListBean.setMapName(mapName);
                        TaskManager.getInstances(mContext).add_Position(positionListBean, -1);
                    }
                }
                getPosition(SocketServices.use_mapName, "point");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "deletePosition fail : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.delete_position) + error.getMessage()));

            }
        });
    }

    /**
     * 重命名点
     */
    public void renamePosition(String mapName, String originName, String newName) {
        Factory.getInstance(mContext, Content.ipAddress).editPosition(mapName, originName, newName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                if ("successed".equals(status.getMsg())) {
                    Log.d(TAG, "renamePosition success");
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.rename_position) + status.getMsg()));
                    sqLiteOpenHelperUtils.updatePointTask(Content.dbPointName, originName, newName);
                    sqLiteOpenHelperUtils.close();
                    getPosition(SocketServices.use_mapName, "point");
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "renamePosition failed : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.rename_position) + error.getMessage()));

            }
        });
    }

    /**
     * 地图点数据，导航点列表
     */
    public void getPosition(String mapName, String type) {
        Factory.getInstance(mContext, Content.ipAddress).getMapPositions(mapName, new RobotStatus<RobotPositions>() {
            @Override
            public void success(RobotPositions robotPositions) {
                Log.d(TAG, "getPosition success : " + robotPositions.getData().size() + ", map: " + mapName + ", point : " + type);
                mRobotPointPositions = robotPositions;
                if (type.equals("point")) {
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.ADDPOINTPOSITION, robotPositions));
                } else if (type.equals("mapList")) {
                    EventBus.getDefault().post(new EventBusMessage(BaseEvent.SENDPOINTPOSITION, robotPositions));
                }
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_mapPositionList) + "successed"));
                for (int i = 0; i < robotPositions.getData().size(); i++) {
                    Log.d(TAG, "getPosition Initialize : " + Content.InitializePositionName + ",   点名字：" + robotPositions.getData().get(i).getName());
                    if (Content.CHARGING_POINT.equals(robotPositions.getData().get(i).getName())) {
                        Content.InitializePositionName = "Initialize";
                        break;
                    } else {
                        Content.InitializePositionName = "End";
                    }
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "renamePosition failed : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_mapPositionList) + error.getMessage()));
            }
        });
    }

    /**
     * 取消导航
     */
    public void cancel_navigate() {
        Factory.getInstance(mContext, Content.ipAddress).cancelNavigate(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "cancel_navigate " + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_navigate) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "cancel_navigate failed :" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_navigate) + error.getMessage()));
            }
        });
    }

    /**
     * 使用地图
     */
    public void use_map(String map_name) {
        Content.is_initialize_finished = 0;
        Log.d(TAG, "TaskManager-use_map： " + map_name);
        Content.noChargingCount = 5;
        NavigationService.stopInitialize();
        Factory.getInstance(mContext, Content.ipAddress).use_map(map_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "use_map success : " + ",   " + map_name + ",   " + Content.taskIndex);
                if (Content.is_initialize_finished == 0) {
                    if (Content.isCharging) {
                        NavigationService.initialize_directly(map_name);
                    } else if (Content.taskIndex == -1 || Content.taskIndex == 0) {
                        NavigationService.initialize(map_name, Content.InitializePositionName);
                    } else {
                        NavigationService.initGlobal(map_name);
                    }
                }
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.use_map) + "success"));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "use_map failed " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.use_map) + error.getMessage()));
            }
        });
    }

    //获取虚拟墙
    public void getVirtual_obstacles(String mapNameUuid, String mapName, String type) {
        Factory.getInstance(mContext, Content.ipAddress).getRecordStatus(new RobotStatus<RecordStatusBean>() {
            @Override
            public void success(RecordStatusBean status) {
                Log.d(TAG, "lines : getRecordStatus " + status.getMsg());
                Factory.getInstance(mContext, Content.ipAddress).getVirtualObstacleData(mapNameUuid, new RobotStatus<VirtualObstacleBean>() {
                    @Override
                    public void success(VirtualObstacleBean virtualObstacleBean) {
                        if ("successed".equals(virtualObstacleBean.getMsg())) {
                            virtualObstacleBean.setMapNameUuid(mapNameUuid);
                            virtualObstacleBean.setMapName(mapName);
                            if (type.equals("AllVirtual")) {
                                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SEND_MQTT_VIRTUAL, virtualObstacleBean));
                            } else if (type.equals("oneMapVirtual")) {
                                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SEND_VIRTUAL, virtualObstacleBean));
                            }
                            EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.virtual_obstacle) + "successed"));
                        }
                    }

                    @Override
                    public void error(Throwable error) {
                        Log.d(TAG, "lines : virtualObstacleBean error : " + error.getMessage());
                        EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.virtual_obstacle) + error.getMessage()));
                    }
                });
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "lines : getRecordStatus error " + error.getMessage());
            }
        });


    }

    //添加虚拟墙
    public void update_virtual_obstacles(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapNameUuid, String mapName, String obstacle_name) {
        Log.d(TAG, "update_virtual111 " + updataVirtualObstacleBean.toString());
        Factory.getInstance(mContext, Content.ipAddress).updateVirtualObstacleData(updataVirtualObstacleBean, mapNameUuid, obstacle_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "update_virtual " + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.add_virtual_obstacle) + status.getMsg()));
                getVirtual_obstacles(mapNameUuid, mapName, "oneMapVirtual");
                getVirtual_obstacles(mapNameUuid, mapName, "AllVirtual");

            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "update_virtual " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.add_virtual_obstacle) + error.getMessage()));
            }
        });
    }

    //跑路径速度
    public void setSpeedLevel(int level) {
        Factory.getInstance(mContext, Content.ipAddress).setSpeedLevel(String.valueOf(level), new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "setSpeedLevel " + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.set_speed_level) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "setSpeedLevel failed " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.set_speed_level) + error.getMessage()));

            }
        });
    }

    //导航速度
    public void setnavigationSpeedLevel(int level) {
        Factory.getInstance(mContext, Content.ipAddress).setnavigationSpeedLevel(String.valueOf(level), new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "setnavigationSpeedLevel " + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_SETTING_MODE, ""));
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.set_speed_level) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "setnavigationSpeedLevel " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.set_speed_level) + error.getMessage()));

            }
        });
    }

    /**
     * 获取设备信息
     */
    public void deviceStatus() {
        Factory.getInstance(mContext, Content.ipAddress).deviceStatus(new RobotStatus<RobotDeviceStatus>() {
            @Override
            public void success(RobotDeviceStatus robotDeviceStatus) {
                Log.d(TAG, "deviceStatus： ： " + robotDeviceStatus.getData());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.DEVICES_STATUS, robotDeviceStatus));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "deviceStatus failed :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.device_status) + error.getMessage()));
            }
        });
    }

    /**
     * 获取版本信息
     */
    public void deviceRobotVersion() {
        Factory.getInstance(mContext, Content.ipAddress).deviceRobotVersion(new RobotStatus<VersionBean>() {
            @Override
            public void success(VersionBean versionBean) {
                Log.d(TAG, "VersionBean： ： " + versionBean.getData().getVersion());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOTVERSIONCODE, versionBean.getData().getVersion()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "VersionBean failed :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.device_status) + error.getMessage()));
            }
        });
    }

    private void navigation() {
        try {
            NavigationService.disposables.add(WebSocketUtil.getWebSocket(Content.ROBOROT_INF_TWO + "/gs-robot/notice/navigation_status")
                    .subscribe(data -> {
                        Log.d("zdzd111 : ", "NavigationStatus : " + data);
                        if (TextUtils.isEmpty(data)) {
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(data);
                        String noticeType = jsonObject.getString("noticeType");
                        navigationStatus(noticeType);
                    }, throwable -> {
                        Log.d(TAG, "NavigationStatus throw ：" + throwable.getMessage());
                        navigation();
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "NavigationStatus Exception ：" + e.getMessage());
        }

    }

    private void notice_status() {
        try {
            NavigationService.disposables.add(WebSocketUtil.getWebSocket(Content.ROBOROT_INF_TWO + "/gs-robot/notice/status")
                    .subscribe(data -> {
                        Log.d("zdzd222 : ", "Navigationnotice : " + data);
                        if (TextUtils.isEmpty(data)) {
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(data);
                        int statusCode = jsonObject.getInt("statusCode");
                        switch (statusCode) {
                            case 701:
                                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, jsonObject.getString("statusMsg")));
                                break;
                            case 401:
                                Content.hasLocalization = false;
                                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, jsonObject.getString("statusMsg")));
                                break;
                            case 702:
                                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, jsonObject.getString("statusMsg")));
                                break;
                            case 1006:
                                Content.hasLocalization = false;
                                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, jsonObject.getString("statusMsg")));
                                break;
                        }
                    }, throwable -> {
                        Log.d(TAG, "Navigationnotice throw ：" + throwable.getMessage());
                        getRobotHealthy();
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Navigationnotice Exception ：" + e.getMessage());
        }
    }

    private void system_health_status() {
        try {
            NavigationService.disposables.add(WebSocketUtil.getWebSocket(Content.ROBOROT_INF_TWO + "/gs-robot/notice/system_health_status")
                    .subscribe(data -> {

                        if (TextUtils.isEmpty(data)) {
                            return;
                        }
                        Log.d(TAG, "system_health_status : " + data);
                        EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_HEALTHY, data));
                        if (!isChecked) {
                            String[] strings = data.split(Content.dbSplit);
                            for (int i = 0; i < strings.length; i++) {
                                if (!strings[i].contains("cannotRotate") && !strings[i].contains("localizationLost")) {
                                    if (strings[i].endsWith("false")) {
                                        Message message = handler.obtainMessage();
                                        message.what = REBOOT_ROBOT;
                                        message.obj = data;
                                        handler.sendMessageDelayed(message, 5 * 60 * 1000);
                                        isChecked = true;
                                        break;
                                    }
                                }
                                if (i == strings.length - 1) {
                                    handler.removeMessages(REBOOT_ROBOT);
                                    mAssestFile.deleteErrorCode();
                                }
                            }
                        }

                    }, throwable -> {
                        Log.d(TAG, "system_health_status ：" + throwable.getMessage());
                        system_health_status();
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "system_health_status Exception ：" + e.getMessage());
        }
    }


    public void getRobotHealthy() {
        system_health_status();
        navigation();
        notice_status();
    }

    public void navigationStatus(String type) throws JSONException {
        Log.d(TAG, "navigationStatus ： " + type + " , isSendType : " + isSendType);
        if ("REACHED".equals(type) && Content.robotState != 6 && !isSendType) {//已经到达目的地
            Log.d(TAG, "REACHED");
            pointState = pointState + mTaskArrayList.get(Content.taskIndex).getName() + "-" + "REACHED" + "-";
            Content.robotStatus = "Working at " + mTaskArrayList.get(Content.taskIndex).getName();
            recording(Content.endRecordingOpName);
            isSendType = true;
            handler.removeMessages(ROBOT_SPEED);
            handler.removeMessages(REINITIALIZE);
            pointStateBean.getList().get(Content.taskIndex).setPointState(mContext.getResources().getString(R.string.working));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
            Log.d(TAG, "Content.taskIndex" + Content.taskIndex + " ,   " + mTaskArrayList.size());
            if (Content.taskIndex < mTaskArrayList.size() - 1) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.START_UVC, mTaskArrayList.get(Content.taskIndex).getDisinfectTime()));
                Content.robotState = 5;
                Content.time = 1000;
            } else {
                Content.taskIsFinish = false;
            }
            currentTaskArea = currentTaskArea + 9;
            Content.taskIndex++;
            sqLiteOpenHelperUtils.updateTaskIndex(Content.dbTaskIndex,
                    "" + Content.taskIndex,
                    "" + mAlarmUtils.getTime(Content.startTime));
            handler.sendEmptyMessageDelayed(DELETE_BAG, 3000);
        } else if (type.equals("UNREACHABLE") && Content.robotState != 6 && !isSendType) {
            hasFailedTask = true;
            pointState = pointState + mTaskArrayList.get(Content.taskIndex).getName() + "-" + "UNREACHABLE" + "-";
            recording(Content.endRecordingOpName);
            isSendType = true;
            handler.removeMessages(ROBOT_SPEED);
            handler.removeMessages(REINITIALIZE);
            pointStateBean.getList().get(Content.taskIndex).setPointState(mContext.getResources().getString(R.string.unreached));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
            //点任务异常记录
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Content.MAP_NAME_UUID, SocketServices.use_mapName);
            jsonObject.put(Content.MAP_NAME, SocketServices.current_mapname);
            jsonObject.put(Content.POINT_NAME, mTaskArrayList.get(Content.taskIndex).getName());
            jsonObject.put(Content.ERROR_MSG, "UNREACHED");
            jsonObject.put(Content.ROS_BAG, mAlarmUtils.getTime(recordingTime).replace(" ", "_") + "_0.bag");
            bagJsonArray.put(jsonObject);

            Content.taskIndex++;
            Content.taskIsFinish = false;
            Log.d(TAG, "UNREACHABLE");
            sqLiteOpenHelperUtils.updateTaskIndex(Content.dbTaskIndex,
                    "" + Content.taskIndex,
                    "" + mAlarmUtils.getTime(Content.startTime));
            handler.sendEmptyMessageDelayed(GET_BAG, 5000);
//        } else if (type.equals("GOAL_NOT_SAFE") && Content.robotState != 6 && !isSendType) {
//            hasFailedTask = true;
//            pointState = pointState + mTaskArrayList.get(Content.taskIndex).getName() + "-" + "GOAL_NOT_SAFE" + "-";
//            recording(Content.endRecordingOpName);
//            isSendType = true;
//            handler.removeMessages(ROBOT_SPEED);
//            handler.removeMessages(REINITIALIZE);
//            pointStateBean.getList().get(Content.taskIndex).setPointState(mContext.getResources().getString(R.string.done));
//            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
//            Content.taskIndex++;
//            Content.taskIsFinish = false;
//            Log.d(TAG, "GOAL_NOT_SAFE");
//            sqLiteOpenHelperUtils.updateTaskIndex(Content.dbTaskIndex,
//                    "" + Content.taskIndex,
//                    "" + mAlarmUtils.getTimeYear(Content.startTime));
//            handler.sendEmptyMessageDelayed(GET_BAG, 5000);
        } else if (type.equals("UNREACHED") && Content.robotState != 6 && !isSendType) {
            hasFailedTask = true;
            pointState = pointState + mTaskArrayList.get(Content.taskIndex).getName() + "-" + "UNREACHED" + "-";
            recording(Content.endRecordingOpName);
            isSendType = true;
            handler.removeMessages(ROBOT_SPEED);
            handler.removeMessages(REINITIALIZE);
            pointStateBean.getList().get(Content.taskIndex).setPointState(mContext.getResources().getString(R.string.unreached));
            EventBus.getDefault().post(new EventBusMessage(BaseEvent.ROBOT_TASK_STATE, pointStateBean));
            //点任务异常记录
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Content.MAP_NAME_UUID, SocketServices.use_mapName);
            jsonObject.put(Content.MAP_NAME, SocketServices.current_mapname);
            jsonObject.put(Content.POINT_NAME, mTaskArrayList.get(Content.taskIndex).getName());
            jsonObject.put(Content.ERROR_MSG, "UNREACHED");
            jsonObject.put(Content.ROS_BAG, mAlarmUtils.getTime(recordingTime).replace(" ", "_") + "_0.bag");
            bagJsonArray.put(jsonObject);

            Content.taskIsFinish = false;
            Content.taskIndex++;
            Log.d(TAG, "UNREACHED");
            sqLiteOpenHelperUtils.updateTaskIndex(Content.dbTaskIndex,
                    "" + Content.taskIndex,
                    "" + mAlarmUtils.getTime(Content.startTime));
            handler.sendEmptyMessageDelayed(GET_BAG, 5000);
        } else if (type.equals("HEADING") || type.equals("PLANNING")) {

        }
        sqLiteOpenHelperUtils.close();
    }

    public void recording(String op_name) {
        RecordingBean recordingBean = new RecordingBean();
        recordingBean.setNode_name("record-bag");
        recordingBean.setOp_name(op_name);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.clear();
        if (Content.startRecordingOpName.equals(op_name)) {
            arrayList.add("/root/GAUSSIAN_RUNTIME_DIR/bag/" + mAlarmUtils.getTime(recordingTime).replace(" ", "_") + ".bag");
        }
        recordingBean.setArgs(arrayList);
        Factory.getInstance(mContext, Content.ipAddress).recording(recordingBean, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "recording-- op_name : " + op_name + ",  status : " + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "recording error-- op_name : " + op_name + ",  status : " + error.getMessage());
            }
        });
    }

    public void getBag(String bagName) {
        Log.d(TAG, "getBag ： " + bagName);
        Factory.getInstance(mContext, Content.ipAddress).getBag(bagName, new RobotStatus<byte[]>() {
            @Override
            public void success(byte[] bytes) {
                Log.d(TAG, "下载bag文件 ： " + bagName + ",   " + bytes.length);
                mAssestFile.writeBagFiles(bytes);
                //EventBus.getDefault().post(new EventBusMessage(BaseEvent.GET_BAG, bagName));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "下载bag文件 error ： " + bagName + ",   " + error.getMessage());
            }
        });
    }

    public void deleteBag(String bagName) {
        Factory.getInstance(mContext, Content.ipAddress).deleteBag("bag/" + bagName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "删除bag文件 : " + bagName + ",   " + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "删除bag文件失败 : " + bagName + ",   " + error.getMessage());
            }
        });
    }

    public void robot_reset() {
        Factory.getInstance(mContext, Content.ipAddress).reset_robot(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "reset_robot : " + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.reset_robot) + " : " + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "reset_robot failed : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.reset_robot) + " : " + error.getMessage()));
            }
        });

    }

    public void getUltrasonicPhit() {
        Factory.getInstance(mContext, Content.ipAddress).getUltrasonicPhit(new RobotStatus<UltrasonicPhitBean>() {
            @Override
            public void success(UltrasonicPhitBean ultrasonicPhitBean) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.SEND_ULTRASONIC, ultrasonicPhitBean));
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_ultrasonic_phit) + "成功"));
            }

            @Override
            public void error(Throwable error) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.get_ultrasonic_phit) + error.getMessage()));
            }
        });
    }

    public void modifyRobotParam(double value) {
        ModifyRobotParam.RobotParam robotParam = new ModifyRobotParam.RobotParam();
        robotParam.setNamespace("/strategy/charger_base/backward_dis");
        robotParam.setType("double");
        robotParam.setValue(value + "");
        ModifyRobotParam.RobotParam[] params = new ModifyRobotParam.RobotParam[]{robotParam};

        Factory.getInstance(mContext, Content.ipAddress).modifyRobotParam(params, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "设置回充电桩的距离 ： " + status + ",  value : " + value);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "设置回充电桩的距离error ： " + error.getMessage() + ",  value : " + value);
            }
        });
    }

    //下载地图
    public void download_map(String mapUuid) {
        Message message = childHandler.obtainMessage();
        message.what = 2;
        message.obj = mapUuid;
        childHandler.sendMessage(message);
    }

    //上传地图
    public void upload_map_syn(String mapName, int count) {
        Message message = childHandler.obtainMessage();
        message.what = 3;
        message.obj = mapName;
        message.arg1 = count;
        childHandler.sendMessage(message);

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case 1:
                scanMapPng();
                childHandler.removeMessages(1);
                childHandler.sendEmptyMessageDelayed(1, 1000);
                break;
            case 2:
                try {
                    Response<ResponseBody> responseBodyResponse = Factory.getInstance(mContext, Content.ipAddress).downloadMap((String) msg.obj);
                    byte[] downloadMap = responseBodyResponse.body().bytes();
                    Log.d(TAG, "download_map SIZE : " + downloadMap.length);
                    if (downloadMap != null && downloadMap.length != 0) {
                        mAssestFile.downloadMapPic((String) msg.obj, downloadMap);
                        File file = new File("/sdcard/robotMap/" + (String) msg.obj + ".tar.gz");
                        String md5ByFile = Md5Utils.getMd5ByFile(file);
                        sqLiteOpenHelperUtils.updateDumpMd5(Content.dbMapNameUuid, (String) msg.obj, md5ByFile);
                        sqLiteOpenHelperUtils.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                Log.d("upload_map_syn : ", "文件路径： " + "/sdcard/robotMap/" + (String) msg.obj);
                String mapName = (String) ((String) msg.obj).replace(".tar.gz", "");
                Factory.getInstance(mContext, Content.ipAddress).uploadMap(mapName, "/sdcard/robotMap/" + (String) msg.obj, new RobotStatus<Status>() {
                    @Override
                    public void success(Status status) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("type", Content.UPLOADMAPSYN);
                            jsonObject.put(Content.MAP_NAME_UUID, mapName);
                            jsonObject.put("status", status.getMsg());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPLOADMAPSYN, jsonObject.toString()));
                        Log.d(TAG, "upload_map_syn : " + status.getMsg() + ",   " + status.getData());
                        sqLiteOpenHelperUtils.saveMapName(currentMapName, Content.TempMapName,
                                "",
                                "",
                                "",
                                "");
                        loadMapList();
                    }

                    @Override
                    public void error(Throwable error) {

                    }
                });
                break;
            default:
                break;
        }

        return false;
    }

}
