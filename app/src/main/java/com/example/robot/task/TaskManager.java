package com.example.robot.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.example.robot.bean.SaveTaskBean;
import com.example.robot.bean.TaskBean;
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


    private TaskManager(Context mContext) {
        this.mContext = mContext;
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
    public void getMapPic(String mapMame) {
        Log.d(TAG, "获取地图" + mapMame);
        if (TextUtils.isEmpty(mapMame)) {
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
        RobotManagerController.getInstance().getRobotController().getMapPicture(mapMame, new RobotStatus<byte[]>() {
            @Override
            public void success(byte[] bytes) {
                Log.d(TAG, "获取地图图片成功" + bytes.length);
                EventBus.getDefault().post(new EventBusMessage(1002, bytes));
            }

            @Override
            public void error(Throwable error) {
                String msg = "获取地图图片失败";
                if (error != null) {
                    msg = "获取地图图片失败" + error.getMessage();
                }
                Log.d(TAG, msg);
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
                    Log.d(TAG, "机器人位置 :  " + robotPosition.getGridPosition().getX() + ",  " + robotPosition.getGridPosition().getY());
                    EventBus.getDefault().post(new EventBusMessage(1003, robotPosition));
                } else {
                    Log.d(TAG, "robotPosition == NULL");
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "gps error :" + error.getMessage());
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
                    }

                    @Override
                    public void onComplete() {

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
                //EventBus.getDefault().post(new EventBusMessage(1004, robotDeviceStatus));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "设备信息获取失败 " + error.getMessage());
            }
        });
    }

    /**
     * 获取充电点
     */
    public void charge_Position(String mapName) {
        RobotManagerController.getInstance().getRobotController().charge_Position(mapName, new RobotStatus<String>() {
            @Override
            public void success(String s) {
                Log.d(TAG, "地图charge_Position : " + mapName + ",   " + s);
                navigate_Position(mapName, s);
            }

            @Override
            public void error(Throwable error) {

                if (error == null) {
                    Log.d(TAG, "地图" + mapName + "充电异常 error = null");
                    return;
                }
                Log.d(TAG, "地图" + mapName + "充电异常" + error.getMessage());
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
                Log.d(TAG, "地图" + mapName + "移动到导航点 : " + positionName);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "地图" + mapName + "移动到导航点fail : " + error.getMessage());
            }
        });

    }

    public void scanMapThread(String newMapName) {
        GsController.INSTANCE.startScanMap(newMapName, 0, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "开始扫描成功 :  " + status.getData());
                scanMapPng();
                scanningFlag = true;
                if (myThread == null) {
                    Log.d(TAG, "启动thread ");
                    myThread = new MyThread(newMapName);
                    myThread.start();
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "开始扫描成功失败 :  " + error.getMessage());
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
            }
        });

    }

    public void start_develop_map(String map_name) {
        GsController.INSTANCE.startScanMap(map_name, 1, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "拓展扫描成功 :  " + status.getData());
                scanningFlag = true;
                if (myThread == null) {
                    Log.d(TAG, "启动拓展thread ");
                    myThread = new MyThread(map_name);
                    myThread.start();
                }
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "开始扫描成功失败 :  " + error.getMessage());
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
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "获取实时扫地图图片png失败 :  " + error.getMessage());
            }
        });
    }

    /**
     * 取消扫描地图
     */
    public void cancelScanMap() {
        GsController.INSTANCE.asyncStopScanMap(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "取消扫描成功 :  ");
                scanningFlag = false;
                if (myThread != null) {
                    myThread = null;
                }
                loadMapList();
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "取消扫描失败 :  " + error.getMessage());
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
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "删除地图失败 :  " + error.getMessage());
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
                Log.d(TAG, "请求图片");
//                start_scan_map(newMapName);
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
                Toast.makeText(mContext, "存储任务成功" + status.getMsg(), Toast.LENGTH_SHORT).show();
                getTaskQueues(mapName);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "存储任务error : " + error.getMessage());
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
                    Log.d(TAG, "ZDZD : " + mRobotPositions.getData().get(i).getName() + "LISTnAME :" +list.get(j).getPositionName());
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
        pois.add("Origin");

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
                if ("successed".equals(status.getMsg())) {
                    Content.robotState = 3;
                    Content.time = 300;
                    Content.taskState = 1;
                    Content.taskIndex = 0;
                    myHandler.removeCallbacks(runnable);
                    myHandler.postDelayed(runnable, 1000);
                    is_task_queue_finished();
                } else {
                    Toast.makeText(mContext, "11111" + status.getMsg(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "11111任务：" + status.getMsg());
                }
            }

            @Override
            public void error(Throwable error) {

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
                myHandler.removeCallbacks(runnable);
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
                myHandler.removeCallbacks(runnable);
                Log.d(TAG, "恢复任务index : " + Content.taskIndex + ",  pois : " + pois.size());
                if (Content.taskIndex < pois.size() - 1) {
                    myHandler.postDelayed(runnable, 1000);
                }
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
    public void stopTaskQueue() {
        GsController.INSTANCE.stopTaskQueue(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "停止任务队列成功");
                Content.taskState = 0;
                Content.taskIndex = 0;
                myHandler.removeCallbacks(runnable);
                myHandler.removeCallbacks(runnable_is_finfish);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "ERROR 停止任务队列失败" + error.getMessage());
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
            }

            @Override
            public void error(Throwable error) {

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
                //SharedPrefUtil.getInstance(mContext, mapName).setPositionMsg(positionListBean);
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "addPosition fail : " + error.getMessage());

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
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "deletePosition fail : " + error.getMessage());

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
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "renamePosition fail : " + error.getMessage());

            }
        });
    }

    /**
     * 地图点数据，导航点列表
     */
    public void getPosition(String mapName) {
        GsController.INSTANCE.getPosition(mapName, 2, new RobotStatus<RobotPositions>() {
            @Override
            public void success(RobotPositions robotPositions) {
                Log.d(TAG, "getPosition success : " + robotPositions.getData().size());
                List<String> list = new ArrayList<>();
                for (int i = 0; i < robotPositions.getData().size(); i++) {
                    list.add(robotPositions.getData().get(i).getName());
                }
                mRobotPositions = robotPositions;
                EventBus.getDefault().post(new EventBusMessage(10017, list));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "renamePosition fail : " + error.getMessage());
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
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "ERROR取消导航失败:" + error.getMessage());
            }
        });
    }

    /**
     * 使用地图
     */
    public void use_map(String map_name) {
        Log.d(TAG, "use_map");
        RobotManagerController.getInstance().getRobotController().use_map(map_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "use_map success");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "use_map error" + error.getMessage());
            }
        });
    }

    /**
     * 任务是否完成
     */
    public void is_task_queue_finished() {
        GsController.INSTANCE.isTaskQueueFinished(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "is_task_queue_finished success" + status.getData());
                if ("true".equals(status.getData())) {
                    Content.taskState = 0;
                    Content.robotState = 1;
                    Content.time = 4000;
                    Toast.makeText(mContext, "任务完成", Toast.LENGTH_SHORT).show();
                    myHandler.removeCallbacks(runnable);
                    myHandler.removeCallbacks(runnable_is_finfish);
                } else {
                    myHandler.removeCallbacks(runnable_is_finfish);
                    myHandler.postDelayed(runnable_is_finfish, 1000);
                }
            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

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

            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "机器人X ：" + mRobotPosition.getGridPosition().getX() + ",  Y : " + mRobotPosition.getGridPosition().getY());
            Log.d(TAG, "机器人目标X ：" + mTaskArrayList.get(Content.taskIndex).getX() + ",  Y : " + mTaskArrayList.get(Content.taskIndex).getY());

            if (mRobotPosition != null && mRobotPosition.getGridPosition().getX() == mTaskArrayList.get(Content.taskIndex).getX()
                    && mRobotPosition.getGridPosition().getY() == mTaskArrayList.get(Content.taskIndex).getY()) {
                Log.d(TAG, "到达知道哪个位置，开始消毒");
                myHandler.removeMessages(1);
                myHandler.sendEmptyMessage(1);
            } else {
                Log.d(TAG, "没到位置，继续移动");
                myHandler.removeCallbacks(this::run);
                myHandler.postDelayed(this::run, 1000);
            }
        }
    };

    Runnable runnable_is_finfish = new Runnable() {
        @Override
        public void run() {
            is_task_queue_finished();
        }
    };

}
