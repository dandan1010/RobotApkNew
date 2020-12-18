package com.example.robot.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.RecordStatusBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.gs.controller.bean.system_bean.UltrasonicPhitBean;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.dcm360.controller.utils.WebSocketUtil;
import com.example.robot.R;
import com.example.robot.bean.PointStateBean;
import com.example.robot.bean.SaveTaskBean;
import com.example.robot.bean.TaskBean;
import com.example.robot.service.NavigationService;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.controller.RobotManagerController;
import com.example.robot.utils.GsonUtils;
import com.example.robot.utils.SharedPrefUtil;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskManager {
    private static final String TAG = "TaskManager";
    private static TaskManager mTaskManager;
    private MyThread myThread;
    private boolean scanningFlag = false;
    public ArrayList<TaskBean> mTaskArrayList = new ArrayList<>();
    private boolean navigateSuccess = false;
    private RobotPosition mRobotPosition = null;
    private Context mContext;
    private RobotTaskQueue robotTaskQueue;
    private List<String> pois;
    private RobotTaskQueueList mRobotTaskQueueList = null;
    private RobotPositions mRobotPositions = null;
    private SqLiteOpenHelperUtils sqLiteOpenHelperUtils;
    private GsonUtils gsonUtils;
    private PointStateBean pointStateBean;
    private AlarmUtils mAlarmUtils;


    private TaskManager(Context mContext) {
        this.mContext = mContext;
        sqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(mContext);
        mAlarmUtils = new AlarmUtils(mContext);
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

    /**
     * 通过地图名字获取地图图片
     */
    public void getMapPic(String mapName) {
        Log.d(TAG, "获取地图" + mapName);
        if (TextUtils.isEmpty(mapName)) {
            Log.d(TAG, "地图名字不能为空");
            return;
        }

        if (RobotManagerController.getInstance() == null) {
            Log.d(TAG, "RobotManagerController() 不能为空");
            return;
        }
        if (RobotManagerController.getInstance().getRobotController() == null) {
            Log.d(TAG, "getRobotController() 不能为空");
            return;
        }
        RobotManagerController.getInstance().getRobotController().getMapPicture(mapName, new RobotStatus<byte[]>() {
            @Override
            public void success(byte[] bytes) {
                Log.d(TAG, "获取地图图片成功" + bytes.length);
                EventBus.getDefault().post(new EventBusMessage(1002, bytes));
                EventBus.getDefault().post(new EventBusMessage(10020, bytes));
            }

            @Override
            public void error(Throwable error) {
                String msg = "获取地图图片失败";
                if (error != null) {
                    msg = "获取地图图片失败" + error.getMessage();
                }
                Log.d(TAG, msg);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_mapIcon) + error.getMessage()));
            }
        });
    }

    /**
     * 获取机器人的位置
     */
    public void getPositions(String mapName) {
        Log.d(TAG, "getPositions" + mapName);
        GsController.INSTANCE.getPositions(mapName, new RobotStatus<RobotPosition>() {
            @Override
            public void success(RobotPosition robotPosition) {
                if (robotPosition != null) {
                    mRobotPosition = robotPosition;
                    if (null == robotPosition.getGridPosition()) {
                        EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.position_fail)));
                    } else {
                        Log.d(TAG, "机器人位置 :  " + robotPosition.getGridPosition().getX() + ",  " + robotPosition.getGridPosition().getY());
                        EventBus.getDefault().post(new EventBusMessage(1003, robotPosition));
                        EventBus.getDefault().post(new EventBusMessage(10024, robotPosition));
                    }

                } else {
                    Log.d(TAG, "robotPosition == NULL");
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "gps error :" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_robotPosition) + error.getMessage()));
            }
        });
    }

    /**
     * 获取地图列表
     */
    @SuppressLint("CheckResult")
    public void loadMapList() {
        GsController.INSTANCE.getGsControllerService().getMapList()
                .filter(robotMap -> robotMap != null && robotMap.getData() != null && robotMap.getData().size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RobotMap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RobotMap robotMap) {
                        Log.d(TAG, "数组长度 ： " + robotMap.getData().size());
                        EventBus.getDefault().post(new EventBusMessage(1001, robotMap));
                        EventBus.getDefault().post(new EventBusMessage(10012, robotMap));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "get map list is error ： " + e.getMessage());
                        EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_mapList) + e.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 移动到导航点
     */
    public void navigate_Position(String mapName, String positionName) {
        RobotManagerController.getInstance().getRobotController().navigate_Position(mapName, positionName, new RobotStatus<Status>() {

            @Override
            public void success(Status status) {
                Log.d(TAG, "地图" + mapName + "移动到导航点 : " + positionName + ",     " + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "地图" + mapName + "移动到导航点fail : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.navigate_position) + error.getMessage()));
            }
        });

    }

    /**
     * 开始扫描地图
     */
    public void start_scan_map(String map_name) {
        GsController.INSTANCE.startScanMap(map_name, 0, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "开始扫描成功 :  " + status.getData());
                scanningFlag = true;
                if (myThread == null) {
                    Log.d(TAG, "启动thread ");
                    myThread = new MyThread(map_name);
                    myThread.start();
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "开始扫描成功失败 :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.start_scanMap) + error.getMessage()));
            }
        });

    }

    /**
     * 扩展扫描地图
     */
    public void start_develop_map(String map_name) {
        GsController.INSTANCE.startScanMap(map_name, 1, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "拓展扫描成功 :  " + status.toString() + "地图名字：" + map_name);
                if ("START_SCAN_MAP_FAILED".equals(status.getErrorCode())) {
                    EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.develop_map) + status.getMsg()));
                } else {
                    scanningFlag = true;
                    if (myThread == null) {
                        Log.d(TAG, "启动拓展thread ");
                        myThread = new MyThread(map_name);
                        myThread.start();
                    }
                }

            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "开始扫描成功失败 :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.develop_map) + error.getMessage()));
            }
        });

    }

    /**
     * 获取实时扫地图图片png
     */
    public void scanMapPng() {
        RobotManagerController.getInstance().getRobotController().scanMapPng(new RobotStatus<byte[]>() {
            @Override
            public void success(byte[] bytes) {
                Log.d(TAG, "获取实时扫地图图片png :  " + bytes.length);
                EventBus.getDefault().post(new EventBusMessage(1005, bytes));
                EventBus.getDefault().post(new EventBusMessage(10020, bytes));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "获取实时扫地图图片png失败 :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_scanMap) + error.getMessage()));
            }
        });
    }

    /**
     * 取消扫描地图——保存
     */
    public void stopScanMap() {
        GsController.INSTANCE.stopScanMap(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "取消扫描成功 :  ");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_scanSaveMap) + status.getMsg()));
                scanningFlag = false;
                if (myThread != null) {
                    myThread = null;
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "取消扫描失败 :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_scanSaveMap) + error.getMessage()));
            }
        });
    }

    /**
     * 取消扫描地图——不保存
     */

    public void cancleScanMap() {
        GsController.INSTANCE.cancelScanMap(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "取消不保存地图 ：" + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_scanMap) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "取消不保存地图失败 ：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_scanMap) + error.getMessage()));

            }
        });
    }

    /**
     * 删除地图
     */
    public void deleteMap(String map_name) {
        GsController.INSTANCE.deleteMap(map_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "删除地图成功 :  ");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.delete_map) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "删除地图失败 :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.delete_map) + error.getMessage()));
            }
        });
    }

    public void renameMapName(String oldMap, String newMap) {
        GsController.INSTANCE.renameMap(oldMap, newMap, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "重命名地图成功 :  ");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.rename_map) + status.getMsg()));
                Cursor cursor = sqLiteOpenHelperUtils.searchAllPointTask();
                while (cursor.moveToNext()) {
                    String mapTaskName = cursor.getString(cursor.getColumnIndex(Content.dbPointTaskName));
                    Log.d(TAG, "重命名地图成功111 :  " + mapTaskName);
                    if (mapTaskName.startsWith(oldMap)) {
                        sqLiteOpenHelperUtils.updatePointTask(Content.dbPointTaskName, mapTaskName,
                                newMap + "," + mapTaskName.split(",")[1]);
                    }
                }
                Cursor cursor1 = sqLiteOpenHelperUtils.searchAllPointTask();
                while (cursor1.moveToNext()) {
                    String mapTaskName = cursor1.getString(cursor1.getColumnIndex(Content.dbPointTaskName));
                    Log.d(TAG, "重命名地图成功222 :  " + mapTaskName);
                }
                sqLiteOpenHelperUtils.close();

            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "重命名地图失败 :  " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.delete_map) + error.getMessage()));
            }
        });
    }

    class MyThread extends Thread {

        private String newMapName;

        public MyThread(String newMapName) {
            this.newMapName = newMapName;
        }

        @Override
        public void run() {
            super.run();
            while (scanningFlag) {
                scanMapPng();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存任务队列
     */
    public void save_taskQueue(String mapName, String taskName, List<SaveTaskBean> list) {
        ArrayList<TaskBean> taskPositionMsg = getTaskPositionMsg(mapName, taskName, list);

        robotTaskQueue = exeTaskPoi(mapName, taskName, taskPositionMsg);

        GsController.INSTANCE.saveTaskQueue(robotTaskQueue, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "存储任务成功");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.save_task) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "存储任务error : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.save_task) + error.getMessage()));
            }
        });

    }

    private ArrayList<TaskBean> getTaskPositionMsg(String mapName, String taskName, List<SaveTaskBean> list) {
        ArrayList<TaskBean> arrayList = new ArrayList<>();
        if (mRobotPositions != null) {
            for (int j = 0; j < list.size(); j++) {
                Log.d(TAG, "ZDZD :mRobotPositions  ===" + mRobotPositions.getData().size());
                for (int i = 0; i < mRobotPositions.getData().size(); i++) {
                    Log.d(TAG, "ZDZD : " + mRobotPositions.getData().get(i).getName() + "    LISTnAME :" + list.get(j).getPositionName());
                    if (list.get(j).getPositionName().equals(mRobotPositions.getData().get(i).getName())) {
                        TaskBean taskBean = new TaskBean(mRobotPositions.getData().get(i).getName(),
                                list.get(j).getTime(),
                                mRobotPositions.getData().get(i).getGridX(),
                                mRobotPositions.getData().get(i).getGridY());
                        arrayList.add(taskBean);
                        sqLiteOpenHelperUtils.savePointTask(mapName + "," + taskName,
                                mRobotPositions.getData().get(i).getName(),
                                "" + list.get(j).getTime(),
                                "" + mRobotPositions.getData().get(i).getGridX(),
                                "" + mRobotPositions.getData().get(i).getGridY());
                    }
                }
                sqLiteOpenHelperUtils.close();
            }
        }
        return arrayList;
    }

    public RobotTaskQueue exeTaskPoi(String mapName, String taskName, ArrayList<TaskBean> mTaskArrayList) {
        pois = new ArrayList<>();
        for (int i = 0; i < mTaskArrayList.size(); i++) {
            pois.add(mTaskArrayList.get(i).getName());
        }
        //pois.add(Content.CHARGING_POINT);

        RobotTaskQueue taskQueue = new RobotTaskQueue();
        taskQueue.setName(taskName);
        taskQueue.setMap_name(mapName);
        taskQueue.setLoop(false);

        if (pois != null && pois.size() > 0) {
            List<RobotTaskQueue.TasksBean> tasksBeans = new ArrayList<>();
            for (int i = 0; i < pois.size(); i++) {
                RobotTaskQueue.TasksBean bean = new RobotTaskQueue.TasksBean();
                RobotTaskQueue.TasksBean.StartParamBean paramBean = new RobotTaskQueue.TasksBean.StartParamBean();
                paramBean.setMap_name(mapName);
                Log.d("ZDZD:", "pois name : " + pois.get(i));
                paramBean.setPosition_name(pois.get(i));
                bean.setName("NavigationTask");
                bean.setStart_param(paramBean);
                tasksBeans.add(bean);
            }
            taskQueue.setTasks(tasksBeans);
        }
        return taskQueue;
    }

    /**
     * 开始执行任务队列
     */
    public void startTaskQueue(String mapName, String taskName) {
        Content.isLastTask = false;
        Log.d(TAG, "startTaskQueue 开始执行任务" + taskName + "  mapName : " + mapName);
        getTaskPositionMsg(mapName, taskName);
        robotTaskQueue = exeTaskPoi(mapName, taskName, mTaskArrayList);

        if (mTaskArrayList.size() == 0) {
            Content.taskName = null;
            Log.d(TAG, "暂时没有任务队列");
            return;
        }
        if (robotTaskQueue == null) {
            Content.taskName = null;
            Toast.makeText(mContext, "没有任务执行", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "任务请求：" + robotTaskQueue.toString());
        GsController.INSTANCE.startTaskQueue(robotTaskQueue, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.start_task) + status.getMsg()));
                if ("successed".equals(status.getMsg())) {
                    Content.robotState = 3;
                    Content.time = 300;
                    Content.taskState = 1;
                    Content.taskIndex = 0;
                    Content.startTime = System.currentTimeMillis();
                    Log.d("zdzd", "开始时间：" + Content.startTime);
                    if (Content.Working_mode == 1) {
                        EventBus.getDefault().post(new EventBusMessage(10005, -1));
                    }

                } else {
                    Content.taskName = null;
                    Log.d(TAG, "11111任务：" + status.getMsg());
                    EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.start_task) + status.getMsg()));
                }
            }

            @Override
            public void error(Throwable error) {
                Content.taskName = null;
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.start_task) + error.getMessage()));
            }
        });
    }

    public ArrayList<TaskBean> getTaskPositionMsg(String mapName, String taskName) {
        mTaskArrayList.clear();
        pointStateBean = new PointStateBean();
        pointStateBean.setTaskName(taskName);
        List<PointStateBean.PointState> pointStates = new ArrayList<>();
        Cursor cursor = sqLiteOpenHelperUtils.searchPointTask(Content.dbPointTaskName, mapName + "," + taskName);
        Log.d(TAG, "获取地图数据startTaskQueue ：" + cursor.getCount() + "   ,mapName : " + mapName + "," + taskName);
        while (cursor.moveToNext()) {
            TaskBean taskBean = new TaskBean(cursor.getString(cursor.getColumnIndex(Content.dbPointName)),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbSpinnerTime))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbPointX))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(Content.dbPointY))));
            mTaskArrayList.add(taskBean);
            PointStateBean.PointState pointState = new PointStateBean.PointState();
            pointState.setPointName(cursor.getString(cursor.getColumnIndex(Content.dbPointName)));
            pointState.setPointState("没有执行");
            pointStates.add(pointState);
        }
        sqLiteOpenHelperUtils.close();
        pointStateBean.setList(pointStates);
        return mTaskArrayList;
    }

    /**
     * 暂停任务
     */
    public void pauseTaskQueue() {
        GsController.INSTANCE.pauseTaskQueue(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "暂停任务成功");
                Content.taskState = 2;
                //myHandler.removeCallbacks(runnable);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "ERROR 暂停任务失败" + error.getMessage());
            }
        });
    }

    /**
     * 恢复任务
     */
    public void resumeTaskQueue() {
        GsController.INSTANCE.resumeTaskQueue(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "恢复任务成功" + status.getMsg());
                Content.taskState = 1;
                Content.robotState = 3;
                Content.time = 300;
                Log.d(TAG, "恢复任务index : " + Content.taskIndex + ",  pois : " + pois.size());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "ERROR 恢复任务失败" + error.getMessage());
            }
        });
    }

    /**
     * 结束任务
     */
    public void stopTaskQueue(String mapName) {
        GsController.INSTANCE.stopTaskQueue(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "停止任务队列成功");
                Content.taskState = 0;
                Content.taskIndex = 0;
                if (Content.taskName != null) {
                    navigate_Position(mapName, Content.CHARGING_POINT);
                    sqLiteOpenHelperUtils.saveTaskHistory(mapName, Content.taskName,
                            "" + ((System.currentTimeMillis() - Content.startTime) / 1000 / 60) + "分钟",
                            "" + mAlarmUtils.getTimeYear(System.currentTimeMillis()));
                    sqLiteOpenHelperUtils.close();
                }
                Content.taskName = null;
                Content.startTime = System.currentTimeMillis();
                if (Content.Working_mode == 1) {
                    EventBus.getDefault().post(new EventBusMessage(10006, -1));
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "ERROR 停止任务队列失败" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_task) + error.getMessage()));
            }
        });
    }

    /**
     * 删除任务队列
     */
    public void deleteTaskQueue(String mapName, String task_name) {
        GsController.INSTANCE.deleteTaskQueue(mapName, task_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                sqLiteOpenHelperUtils.deleteAlarmTask(mapName + "," + task_name);
                sqLiteOpenHelperUtils.deletePointTask(Content.dbPointTaskName, mapName + "," + task_name);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.delete_task) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.delete_task) + error.getMessage()));
            }
        });
    }

    /**
     * 获取任务队列
     */
    public void getTaskQueues(String map_name) {
        GsController.INSTANCE.taskQueues(map_name, new RobotStatus<RobotTaskQueueList>() {
            @Override
            public void success(RobotTaskQueueList robotTaskQueueList) {
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_taskList) + "成功"));
                Log.d(TAG, "获取任务队列 success" + robotTaskQueueList.getData().size());
                mRobotTaskQueueList = robotTaskQueueList;
                EventBus.getDefault().post(new EventBusMessage(1006, robotTaskQueueList));
                EventBus.getDefault().post(new EventBusMessage(10016, robotTaskQueueList));
            }

            @Override
            public void error(Throwable error) {
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_taskList) + error.getMessage()));
            }
        });

    }

    /**
     * 添加点
     */
    public void add_Position(PositionListBean positionListBean) {
        GsController.INSTANCE.add_Position(positionListBean, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "addPosition success");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.add_position) + status.getMsg()));
                getPosition(Content.mapName);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "addPosition fail : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.add_position) + error.getMessage()));
            }
        });
    }

    /**
     * 删除点
     */
    public void deletePosition(String mapName, String positionName) {
        GsController.INSTANCE.deletePosition(mapName, positionName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "deletePosition success");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.delete_position) + status.getMsg()));
                sqLiteOpenHelperUtils.deletePointTask(Content.dbPointName, positionName);
                getPosition(Content.mapName);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "deletePosition fail : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.delete_position) + error.getMessage()));

            }
        });
    }

    /**
     * 重命名点
     */
    public void renamePosition(String mapName, String originName, String newName) {
        GsController.INSTANCE.renamePosition(mapName, originName, newName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "renamePosition success");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.rename_position) + status.getMsg()));
                sqLiteOpenHelperUtils.updatePointTask(Content.dbPointName, originName, newName);
                getPosition(Content.mapName);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "renamePosition fail : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.rename_position) + error.getMessage()));

            }
        });
    }

    /**
     * 地图点数据，导航点列表
     */
    public void getPosition(String mapName) {
        GsController.INSTANCE.getMapPositions(mapName, new RobotStatus<RobotPositions>() {
            @Override
            public void success(RobotPositions robotPositions) {
                Log.d(TAG, "getPosition success : " + robotPositions.getData().size());
                mRobotPositions = robotPositions;
                EventBus.getDefault().post(new EventBusMessage(10017, robotPositions));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "renamePosition fail : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_mapPositionList) + error.getMessage()));
            }
        });


    }

    /**
     * 取消导航
     */
    public void cancel_navigate() {
        Log.d(TAG, "开始请求取消导航");
        GsController.INSTANCE.cancelNavigate(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "取消导航成功");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_navigate) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "ERROR取消导航失败:" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_navigate) + error.getMessage()));
            }
        });
    }

    /**
     * 使用地图
     */
    public void use_map(String map_name) {
        Content.is_initialize_finished = 0;
        Log.d(TAG, "use_map： " + map_name);
        RobotManagerController.getInstance().getRobotController().use_map(map_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "use_map success");
                if (Content.robotState == 4) {
                    NavigationService.initialize_directly(Content.mapName);
                } else {
                    NavigationService.initialize(Content.mapName);
                }
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.use_map) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "use_map error" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.use_map) + error.getMessage()));
            }
        });
    }

    //获取虚拟墙
    public void getVirtual_obstacles(String mapName) {
        RobotManagerController.getInstance().getRobotController().getRecordStatus(new RobotStatus<RecordStatusBean>() {
            @Override
            public void success(RecordStatusBean status) {
                Log.d("zdzd ", "lines : getRecordStatus " + status.getMsg());

                RobotManagerController.getInstance().getRobotController().getVirtualObstacleData(mapName, new RobotStatus<VirtualObstacleBean>() {
                    @Override
                    public void success(VirtualObstacleBean virtualObstacleBean) {
                        EventBus.getDefault().post(new EventBusMessage(10042, virtualObstacleBean));
                    }

                    @Override
                    public void error(Throwable error) {
                        Log.d("zdzd ", "lines : virtualObstacleBean error : " + error.getMessage());
                        EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.virtual_obstacle) + error.getMessage()));
                    }
                });
            }

            @Override
            public void error(Throwable error) {
                Log.d("zdzd ", "lines : getRecordStatus error " + error.getMessage());
            }
        });


    }

    //添加虚拟墙
    public void update_virtual_obstacles(UpdataVirtualObstacleBean updataVirtualObstacleBean, String mapName, String obstacle_name) {
        Log.d("update_virtual111 ", "" + updataVirtualObstacleBean.toString());
        RobotManagerController.getInstance().getRobotController().updateVirtualObstacleData(updataVirtualObstacleBean, mapName, obstacle_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d("update_virtual ", "" + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.add_virtual_obstacle) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d("update_virtual ", "" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.add_virtual_obstacle) + error.getMessage()));

            }
        });
    }

    //导航速度
    public void setSpeedLevel(int level) {
        RobotManagerController.getInstance().getRobotController().setSpeedLevel(String.valueOf(level), new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "设置导航速度成功");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.set_speed_level) + status.getMsg()));

            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "设置导航速度失败");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.set_speed_level) + error.getMessage()));

            }
        });
    }

    /**
     * 获取设备信息
     */
    public void deviceStatus() {
        Log.d(TAG, "设备信息： ：deviceStatus ");
        GsController.INSTANCE.deviceStatus(new RobotStatus<RobotDeviceStatus>() {
            @Override
            public void success(RobotDeviceStatus robotDeviceStatus) {
                Log.d(TAG, "设备信息： ： " + robotDeviceStatus.getData().toString());
                EventBus.getDefault().post(new EventBusMessage(10049, robotDeviceStatus));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "获取设备信息失败 " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.device_status) + error.getMessage()));
            }
        });
    }


    @SuppressLint("CheckResult")
    public void robotStatus() {
        NavigationService.disposables.add(WebSocketUtil.getWebSocket(Content.ROBOROT_INF_TWO + "/gs-robot/notice/system_health_status")
                .subscribe(data -> {

                    if (TextUtils.isEmpty(data)) {
                        return;
                    }
                    EventBus.getDefault().post(new EventBusMessage(10037, data));
                }, throwable -> {
                    Log.d("zdzd999", "导航：" + throwable.getMessage());
                }));

        RobotManagerController.getInstance()
                .getRobotController()
                .RobotStatus(
                        new NavigationStatus() {
                            @Override
                            public void noticeType(String type, String destination) {
                                Log.d("zdzdxxx", "statustype：" + type + ",  taskIndex : " + Content.taskIndex);
                                String msg = "";
                                if (Content.taskIndex < mTaskArrayList.size() && !type.equals("HEADING")) {
                                    Log.d("zdzdxxx", "statustype33：" + type + ",  taskIndex : " + Content.taskIndex);
                                    pointStateBean.getList().get(Content.taskIndex).setPointState(type);
                                    EventBus.getDefault().post(new EventBusMessage(10038, pointStateBean));
                                }
                                if ("REACHED".equals(type)) {//已经到达目的地
                                    Log.d("zdzdContent.taskIndex", "" + Content.taskIndex + " ,   " + mTaskArrayList.size());
                                    if (Content.taskIndex < mTaskArrayList.size()) {
                                        pauseTaskQueue();
                                        EventBus.getDefault().post(new EventBusMessage(1007, mTaskArrayList.get(Content.taskIndex).getDisinfectTime()));
                                        Log.d(TAG, "暂停任务");
                                        Content.robotState = 5;
                                        Content.time = 1000;
                                    }
                                    Content.taskIndex++;
                                    msg = "到达目的地";
                                } else if (type.equals("UNREACHABLE")) {//目的地无法到达
                                    msg = "目的地无法到达";
                                    Content.taskIndex++;
                                } else if (type.equals("LOCALIZATION_FAILED")) {
                                    msg = "定位失败";
                                    Content.taskIndex++;
                                } else if (type.equals("GOAL_NOT_SAFE")) {
                                    msg = "目的地有障碍物";
                                    Content.taskIndex++;
                                } else if (type.equals("TOO_CLOSE_TO_OBSTACLES")) {
                                    msg = "离障碍物太近";
                                    Content.taskIndex++;
                                } else if (type.equals("UNREACHED")) {
                                    msg = "到达目的地附近，目的地有障碍物";
                                    Content.taskIndex++;
                                } else if (type.equals("HEADING")) {
                                    msg = "正在前往目的地";
                                } else if (type.equals("PLANNING")) {
                                    msg = "正在规划路径";
                                }

                                if (Content.taskIndex == mTaskArrayList.size()) {
                                    Content.isLastTask = true;
                                    if (type.equals("UNREACHABLE") || type.equals("LOCALIZATION_FAILED") || type.equals("UNREACHED")
                                            || type.equals("GOAL_NOT_SAFE") || type.equals("TOO_CLOSE_TO_OBSTACLES")) {
                                        TaskManager.getInstances(mContext).navigate_Position(Content.mapName, Content.CHARGING_POINT);
                                        Log.d(TAG, "任务完成");
                                        sqLiteOpenHelperUtils.saveTaskHistory(Content.mapName, Content.taskName, "" + ((System.currentTimeMillis() - Content.startTime) / 1000 / 60) + "分钟",
                                                mAlarmUtils.getTimeYear(System.currentTimeMillis()));
                                        sqLiteOpenHelperUtils.close();
                                        Content.startTime = System.currentTimeMillis();
                                        Content.taskState = 0;
                                        Content.robotState = 1;
                                        Content.time = 4000;
                                        Content.taskName = null;
                                        if (Content.Working_mode == 1) {
                                            EventBus.getDefault().post(new EventBusMessage(10006, -1));
                                        }
                                    }
                                }

                                Log.d("zdzdxxx", "statustype2：" + type + ",  taskIndex : " + Content.taskIndex + "list : " + mTaskArrayList.size());
                                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.robot_state) + " ：" + type + ",  " + msg));
                            }

                            @Override
                            public void statusCode(int code, String msg) {
                                //String status = StatusCode.getCodeMsg(code);
                                //MyLogcat.showMessageLog(status + ">>>" + msg);
                                Log.d("zdzdxxx", "statusCode：" + code + "  , msg : " + msg);
                                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.robot_state) + " ：" + code + ",  " + msg));
                                switch (code) {

                                }
                            }
                        },
                        Content.ROBOROT_INF_TWO + "/gs-robot/notice/navigation_status",
                        Content.ROBOROT_INF_TWO + "/gs-robot/notice/status");
    }

    public void robot_reset() {
        GsController.INSTANCE.reset_robot(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "reset_robot : " + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.reset_robot) + " : " + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.reset_robot) + " : " + error.getMessage()));
            }
        });

    }

    public void getUltrasonicPhit() {
        GsController.INSTANCE.getUltrasonicPhit(new RobotStatus<UltrasonicPhitBean>() {
            @Override
            public void success(UltrasonicPhitBean ultrasonicPhitBean) {
                EventBus.getDefault().post(new EventBusMessage(10059, ultrasonicPhitBean));
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_ultrasonic_phit) + "成功"));

            }

            @Override
            public void error(Throwable error) {
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.get_ultrasonic_phit) + error.getMessage()));
            }
        });
    }
}
