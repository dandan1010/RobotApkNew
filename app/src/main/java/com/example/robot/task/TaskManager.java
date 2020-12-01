package com.example.robot.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.dcm360.controller.gs.controller.bean.paths_bean.VirtualObstacleBean;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.dcm360.controller.utils.WebSocketUtil;
import com.example.robot.R;
import com.example.robot.bean.SaveTaskBean;
import com.example.robot.bean.TaskBean;
import com.example.robot.service.NavigationService;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.controller.RobotManagerController;
import com.example.robot.utils.GsonUtils;
import com.example.robot.utils.SharedPrefUtil;

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
    private ArrayList<TaskBean> mTaskArrayList = new ArrayList<>();
    private boolean navigateSuccess = false;
    private RobotPosition mRobotPosition = null;
    private Context mContext;
    private RobotTaskQueue robotTaskQueue;
    private List<String> pois;
    private RobotTaskQueueList mRobotTaskQueueList = null;
    private RobotPositions mRobotPositions = null;
    private SqLiteOpenHelperUtils sqLiteOpenHelperUtils;
    private GsonUtils gsonUtils;


    private TaskManager(Context mContext) {
        this.mContext = mContext;
        sqLiteOpenHelperUtils = new SqLiteOpenHelperUtils(mContext);
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
                    Log.d("zdzd", "为空:" + robotPosition.toString());
                    Log.d("zdzd", "为空:" + String.valueOf(robotPosition.getGridPosition()));
                    if (null == robotPosition.getGridPosition()) {
                        Log.d("zdzd", "为空");
                        EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.position_fail)));
                    } else {
                        Log.d(TAG, "机器人位置 :  " + robotPosition.getGridPosition().getX() + ",  " + robotPosition.getGridPosition().getY());
                        Log.d(TAG, "机器人位置 :  " + robotPosition.getMapInfo().getGridWidth() + ",  " + robotPosition.getMapInfo().getGridHeight());

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
                loadMapList();
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
     * */

    public void cancleScanMap(){
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
                getTaskQueues(mapName);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "存储任务error : " + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.save_task) + error.getMessage()));
            }
        });

    }

    private ArrayList<TaskBean> getTaskPositionMsg(String mapName, String taskName, List<SaveTaskBean> list) {
        Log.d(TAG, "ZDZD :mRobotPositions  ===" + list.size());
        Log.d(TAG, "ZDZD :mRobotPositions  ===" + (mRobotPositions != null));
        ArrayList<TaskBean> arrayList = new ArrayList<>();
        if (mRobotPositions != null) {
            for (int j = 0; j < list.size(); j++) {
                Log.d(TAG, "ZDZD :mRobotPositions  ===" + mRobotPositions.getData().size());
                for (int i = 0; i < mRobotPositions.getData().size(); i++) {
                    Log.d(TAG, "ZDZD : " + mRobotPositions.getData().get(i).getName() + "    LISTnAME :" +list.get(j).getPositionName());
                    if (list.get(j).getPositionName().equals(mRobotPositions.getData().get(i).getName())) {
                        TaskBean taskBean = new TaskBean(mRobotPositions.getData().get(i).getName(),
                                mRobotPositions.getData().get(i).getGridX(),
                                mRobotPositions.getData().get(i).getGridY(),
                                list.get(j).getTime(),
                                mRobotPositions.getData().get(i).getAngle());
                        arrayList.add(taskBean);
                    }
                }
            }
            SharedPrefUtil.getInstance(mContext, mapName).setPositionMsg(taskName, arrayList);
        }
        return arrayList;
    }

    public RobotTaskQueue exeTaskPoi(String mapName, String taskName, ArrayList<TaskBean> mTaskArrayList) {
        pois = new ArrayList<>();
        for (int i = 0; i < mTaskArrayList.size(); i++) {
            pois.add(mTaskArrayList.get(i).getName());
        }
        //pois.add("Origin");

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
        Log.d(TAG, "startTaskQueue 开始执行任务" + taskName);
        getTaskPositionMsg(mapName, taskName);
        robotTaskQueue = exeTaskPoi(mapName, taskName, mTaskArrayList);

        if (mTaskArrayList.size() == 0) {
            Log.d(TAG, "暂时没有任务队列");
            return;
        }
        if (robotTaskQueue == null) {
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
//                    myHandler.removeCallbacks(runnable);
//                    myHandler.postDelayed(runnable, 1000);
//                    is_task_queue_finished();
                } else {
                    Toast.makeText(mContext, "11111" + status.getMsg(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "11111任务：" + status.getMsg());
                }
            }

            @Override
            public void error(Throwable error) {
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.start_task) + error.getMessage()));
            }
        });
    }

    private ArrayList<TaskBean> getTaskPositionMsg(String mapName, String taskName) {
        mTaskArrayList.clear();
        String taskMsg = SharedPrefUtil.getInstance(mContext, mapName).getPositionMsg(taskName);
        Log.d(TAG, "获取地图数据startTaskQueue ：" + taskMsg);
        if (taskMsg != null) {
            try {
                JSONObject jsonObject = new JSONObject(taskMsg);
                JSONArray jsonArray = jsonObject.getJSONArray(taskName);
                for (int i =0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1= (JSONObject) jsonArray.get(i);
                    TaskBean taskBean = new TaskBean(jsonObject1.getString(Content.TASK_NAME),
                            jsonObject1.getInt(Content.TASK_X),
                            jsonObject1.getInt(Content.TASK_Y),
                            jsonObject1.getInt(Content.TASK_DISINFECT_TIME),
                            jsonObject1.getDouble(Content.TASK_ANGLE));
                    mTaskArrayList.add(taskBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                //myHandler.removeCallbacks(runnable);
                Log.d(TAG, "恢复任务index : " + Content.taskIndex + ",  pois : " + pois.size());
//                if (Content.taskIndex < pois.size() - 1) {
//                    myHandler.postDelayed(runnable, 1000);
//                }
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
                Content.taskName = null;
//                myHandler.removeCallbacks(runnable);
//                myHandler.removeCallbacks(runnable_is_finfish);
                navigate_Position(mapName, "Origin");
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
                SharedPrefUtil.getInstance(mContext, mapName).deleteTaskQueue(task_name);
                getTaskQueues(mapName);
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
                //SharedPrefUtil.getInstance(mContext, mapName).setPositionMsg(positionListBean);
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
        Log.d(TAG, "use_map： " + map_name);
        RobotManagerController.getInstance().getRobotController().use_map(map_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "use_map success");
                NavigationService.initialize(Content.mapName);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.use_map) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "use_map error" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.use_map) + error.getMessage()));
            }
        });
    }

    /**
     * 任务是否完成
     */
//    public void is_task_queue_finished() {
//        GsController.INSTANCE.isTaskQueueFinished(new RobotStatus<Status>() {
//            @Override
//            public void success(Status status) {
//                Log.d(TAG, "is_task_queue_finished success" + status.getData());
//                if ("true".equals(status.getData())) {
//                    Content.taskState = 0;
//                    Content.robotState = 1;
//                    Content.time = 4000;
//                    Toast.makeText(mContext, "任务完成", Toast.LENGTH_SHORT).show();
//                    Content.taskName = null;
//                    myHandler.removeCallbacks(runnable);
//                    myHandler.removeCallbacks(runnable_is_finfish);
//                    EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.task_finish)));
//                } else {
//                    myHandler.removeCallbacks(runnable_is_finfish);
//                    myHandler.postDelayed(runnable_is_finfish, 1000);
//                }
//            }
//
//            @Override
//            public void error(Throwable error) {
//                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.task_finish) + error.getMessage()));
//            }
//        });
//    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                EventBus.getDefault().post(new EventBusMessage(1007, mTaskArrayList.get(Content.taskIndex).getDisinfectTime()));
                Log.d(TAG, "暂停任务");
                Content.robotState = 5;
                Content.time = 1000;
                pauseTaskQueue();
                Content.taskIndex ++;
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.arrive_location)));

            }
        }
    };

//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            Log.d(TAG, "机器人X ：" + mRobotPosition.getGridPosition().getX() + ",  Y : " + mRobotPosition.getGridPosition().getY());
//            Log.d(TAG, "机器人目标X ：" + mTaskArrayList.get(Content.taskIndex).getX() + ",  Y : " + mTaskArrayList.get(Content.taskIndex).getY());
//
//            if (mRobotPosition != null && mRobotPosition.getGridPosition().getX() == mTaskArrayList.get(Content.taskIndex).getX()
//                    && mRobotPosition.getGridPosition().getY() == mTaskArrayList.get(Content.taskIndex).getY()) {
//                Log.d(TAG, "到达知道哪个位置，开始消毒");
//                Log.d(TAG, "到达知道哪个位置，开始消毒");
//                myHandler.removeMessages(1);
//                myHandler.sendEmptyMessage(1);
//            } else {
//                Log.d(TAG, "没到位置，继续移动");
//                myHandler.removeCallbacks(this::run);
//                myHandler.postDelayed(this::run, 1000);
//            }
//        }
//    };

//    Runnable runnable_is_finfish = new Runnable() {
//        @Override
//        public void run() {
//            is_task_queue_finished();
//        }
//    };

    //获取虚拟墙
    public void getVirtual_obstacles(String mapName){
        RobotManagerController.getInstance().getRobotController().getRecordStatus(new RobotStatus<RecordStatusBean>() {
            @Override
            public void success(RecordStatusBean status) {
                Log.d("zdzd " , "lines : getRecordStatus " + status.getMsg());

                RobotManagerController.getInstance().getRobotController().getVirtualObstacleData(mapName, new RobotStatus<VirtualObstacleBean>() {
                    @Override
                    public void success(VirtualObstacleBean virtualObstacleBean) {
                        Log.d("zdzd " , "lines : virtualObstacleBean " + virtualObstacleBean.toString());
                        VirtualObstacleBean.ObstaclesEntity obstaclesEntity = virtualObstacleBean.getObstacles();
                        List<VirtualObstacleBean.ObstaclesEntity.LinesEntity> linesEntity = obstaclesEntity.getLines();
                        for (int i = 0; i < linesEntity.size(); i++) {
                            Log.d("zdzd " , "lines : start " + linesEntity.get(i).getStart().getX());
                            Log.d("zdzd " , "lines : end " + linesEntity.get(i).getEnd().getX());
                        }

                    }

                    @Override
                    public void error(Throwable error) {
                        Log.d("zdzd " , "lines : virtualObstacleBean " + error.getMessage());
                    }
                });
            }

            @Override
            public void error(Throwable error) {
                Log.d("zdzd " , "lines : getRecordStatus error " + error.getMessage());
            }
        });


    }

    //添加虚拟墙



    @SuppressLint("CheckResult")
    public void robotStatus() {
        NavigationService.disposables.add(WebSocketUtil.getWebSocket(Content.ROBOROT_INF_TWO + "/gs-robot/notice/system_health_status")
                .subscribe(data -> {

                    if (TextUtils.isEmpty(data)) {
                        return;
                    }
                    Log.d("zdzd888", "websocket3:" + data);
                    EventBus.getDefault().post(new EventBusMessage(10037, mContext.getResources().getString(R.string.arrive_location)));
                }, throwable -> {
                    Log.d("zdzd999","导航：" + throwable.getMessage());
                }));

        RobotManagerController.getInstance()
                .getRobotController()
                .RobotStatus(new NavigationStatus() {
                                 @Override
                                 public void noticeType(String type, String destination) {
                                     Log.d("zdzdxxx", "statustype：" + type );
                                     String msg = "";
                                     if ("REACHED".equals(type)) {//已经到达目的地
                                         Log.d("zdzdContent.taskIndex", "" + Content.taskIndex);
                                         if (Content.taskIndex < mTaskArrayList.size() - 1) {
                                             pauseTaskQueue();
                                             EventBus.getDefault().post(new EventBusMessage(1007, mTaskArrayList.get(Content.taskIndex).getDisinfectTime()));
                                             Log.d(TAG, "暂停任务");
                                             Content.robotState = 5;
                                             Content.time = 1000;
                                             Content.taskIndex ++;
                                         }
                                     } else if (type.equals("UNREACHABLE")) {//目的地无法到达

                                     } else if (type.equals("LOCALIZATION_FAILED")) {
                                         msg = "定位失败";
                                     } else if (type.equals("GOAL_NOT_SAFE")) {

                                         msg = "目的地有障碍物";
                                     } else if (type.equals("TOO_CLOSE_TO_OBSTACLES")) {
                                         msg = "离障碍物太近";
                                     } else if (type.equals("HEADING")) {
                                         msg = "正在前往目的地";
                                     } else if (type.equals("PLANNING")) {
                                         msg = "正在规划路径";
                                     }
                                 }

                                 @Override
                                 public void statusCode(int code, String msg) {
                                     //String status = StatusCode.getCodeMsg(code);
                                     //MyLogcat.showMessageLog(status + ">>>" + msg);
                                    Log.d("zdzdxxx", "statusCode：" + code + "  , msg : " + msg);
                                     switch (code) {
                                         case 403:
                                         case 304:
                                             break;
                                         case 408:
                                             break;
                                         case 409:
                                             msg = "正在寻找充电桩";
                                             break;
                                         case 410:
                                             msg = "正在移动到充电桩前面";
                                             break;
                                         case 411:
                                             msg = "正在尝试对桩充电";
                                             break;
                                         case 702:
                                             msg = "触发虚拟墙,请人工移动";
                                             break;
                                         case 1004:
                                         case 1005:
                                         case 1006:
                                         case 1009:
                                             break;
                                         case 1008:

                                             break;
                                         default:

                                             break;
                                     }
                                 }
                             },
                        Content.ROBOROT_INF_TWO + "/gs-robot/notice/navigation_status",
                        Content.ROBOROT_INF_TWO + "/gs-robot/notice/status");

    }



}
