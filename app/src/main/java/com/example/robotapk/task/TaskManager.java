package com.example.robotapk.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dcm360.controller.RobotController;
import com.dcm360.controller.gs.GSRobotController;
import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.navigate_bean.RobotNavigatePosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotSaveTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.dcm360.controller.gs.controller.service.GsControllerService;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.example.robotapk.bean.TaskBean;
import com.example.robotapk.utils.Content;
import com.example.robotapk.utils.EventBusMessage;
import com.example.robotapk.controller.RobotManagerController;
import com.example.robotapk.utils.GsonUtils;
import com.example.robotapk.utils.SharedPrefUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    public String mapName = "";
    private static TaskManager mTaskManager;
    private MyThread myThread;
    private boolean scanningFlag = false;
    private ArrayList<TaskBean> mTaskArrayList = new ArrayList<>();
    private int taskIndex = 0;
    private boolean navigateSuccess = false;
    private RobotPosition mRobotPosition = null;
    private Context mContext;


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
        this.mapName = mapMame;
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
    public void getPositions() {
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
    public void charge_Position() {
        RobotManagerController.getInstance().getRobotController().charge_Position(mapName, new RobotStatus<String>() {
            @Override
            public void success(String s) {
                Log.d(TAG, "地图charge_Position : " + mapName + ",   " + s);
                navigate_Position(s);
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
    public void navigate_Position(String positionName) {
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
        GsController.INSTANCE.stopScanMap(new RobotStatus<Status>() {
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
    public void save_taskQueue() {
        Log.d(TAG, "保存任务队列");
        mTaskArrayList.clear();
        TaskBean taskBean1 = new TaskBean();
        taskBean1.setName("A任务点执行任务1分钟");
        taskBean1.setX(180);
        taskBean1.setY(260);
        taskBean1.setAngle(100);
        taskBean1.setDisinfectTime(0);
        mTaskArrayList.add(taskBean1);

        TaskBean taskBean2 = new TaskBean();
        taskBean2.setName("B任务点执行任务5分钟");
        taskBean2.setX(180);
        taskBean2.setY(200);
        taskBean2.setAngle(100);
        taskBean2.setDisinfectTime(1);
        mTaskArrayList.add(taskBean2);

        GsonUtils gsonUtils = new GsonUtils();
        gsonUtils.setmTaskList(mTaskArrayList);
        SharedPrefUtil.getInstance(mContext).setTaskQueue(gsonUtils.putJsonTaskMessage(Content.DATA_TASK));

        getTaskQueues(Content.DATA_TASK);
    }

    /**
     * 开始执行任务队列
     */
    public void startTaskQueue() {
        if (mTaskArrayList.size() == 0) {
            Log.d(TAG, "暂时没有任务队列");
            return;
        }
        Log.d(TAG, "开始任务队列： " + taskIndex + ",队列任务数量： " + mTaskArrayList.size());
        if (taskIndex < mTaskArrayList.size()) {
            RobotNavigatePosition robotNavigatePosition = new RobotNavigatePosition();
            RobotNavigatePosition.DestinationBean destinationBean = new RobotNavigatePosition.DestinationBean();
            destinationBean.setAngle(mTaskArrayList.get(taskIndex).getAngle());
            RobotNavigatePosition.DestinationBean.GridPositionBean gridPositionBean = new RobotNavigatePosition.DestinationBean.GridPositionBean();
            gridPositionBean.setX(mTaskArrayList.get(taskIndex).getX());
            gridPositionBean.setY(mTaskArrayList.get(taskIndex).getY());
            destinationBean.setGridPosition(gridPositionBean);
            robotNavigatePosition.setDestination(destinationBean);
            GsController.INSTANCE.navigate(robotNavigatePosition, new RobotStatus<Status>() {
                @Override
                public void success(Status status) {
                    Log.d(TAG, "导航成功 : " + status.getMsg());
                    if ("successed".equals(status.getMsg())) {
                        navigateSuccess = true;
                        moveToTaskLocation();
                    }
                }

                @Override
                public void error(Throwable error) {

                }
            });
        }
    }

    public void moveToTaskLocation() {
        Content.robotState = 3;
        Log.d(TAG, "机器人X :" + mRobotPosition.getGridPosition().getX() + "  Y :" + mRobotPosition.getGridPosition().getY());
        Log.d(TAG, "机器人目标X :" + mTaskArrayList.get(taskIndex).getX() + "  Y :" + mTaskArrayList.get(taskIndex).getY());
        if (mRobotPosition != null && mRobotPosition.getGridPosition().getX() == mTaskArrayList.get(taskIndex).getX()
                && mRobotPosition.getGridPosition().getY() == mTaskArrayList.get(taskIndex).getY()) {
            Log.d(TAG, "到达知道哪个位置，开始消毒");
            EventBus.getDefault().post(new EventBusMessage(1007, mTaskArrayList.get(taskIndex).getDisinfectTime()));
            navigateSuccess = false;
            taskIndex++;
        } else if (taskIndex < mTaskArrayList.size() && navigateSuccess) {
            Log.d(TAG, "移动");
            moveToTaskLocation();
        }
    }

    /**
     * 结束任务
     */
    public void stopTaskQueue() {
        taskIndex = 0;
        navigateSuccess = false;
    }

    /**
     * 删除任务队列
     */
    public void deleteTaskQueue(String type) {
        mTaskArrayList.clear();
        SharedPrefUtil.getInstance(mContext).deleteTaskQueue(type);
    }

    /**
     * 获取任务队列
     */
    public void getTaskQueues(String type) {
        mTaskArrayList.clear();
        String gsonUtils = SharedPrefUtil.getInstance(mContext).getTaskQueue(Content.DATA_TASK);
        Log.d(TAG, "get taskQueues : " + gsonUtils);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gsonUtils);
            JSONArray jsonArray = jsonObject.getJSONArray(type);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                TaskBean taskBean = new TaskBean(object.getString(Content.TASK_NAME), object.getInt(Content.TASK_X),
                        object.getInt(Content.TASK_Y), object.getInt(Content.TASK_DISINFECT_TIME), object.getInt(Content.TASK_ANGLE));
                mTaskArrayList.add(taskBean);
            }
            EventBus.getDefault().post(new EventBusMessage(1006, mTaskArrayList));
        } catch (JSONException e) {
            Log.d(TAG, "ERROR任务列表object : " + e.getMessage());
        }


    }

    /**
     * 记录点
     */
    public void addPosition(String positionName) {
        GsController.INSTANCE.addPosition(positionName, 2, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "addPosition success");
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
    public void deletePosition(String positionName) {
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
    public void renamePosition(String originName, String newName) {
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
    public void getPosition() {
        GsController.INSTANCE.getPosition(TaskManager.getInstances(mContext).mapName, 2, new RobotStatus<RobotPositions>() {
            @Override
            public void success(RobotPositions robotPositions) {
                Log.d(TAG, "getPosition success : " + robotPositions.getData().size());
//                List<String> list = new ArrayList<>();
//                for (int i = 0 ;i<robotPositions.getData().size();i++){
//                    list.add(robotPositions.getData().get(i).getName());
//                }
//                TaskManager.getInstances().save_taskQueue(list,"task1");

            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "renamePosition fail : " + error.getMessage());
            }
        });


    }

    /**
     * 获取路径
     */
    public void getPath() {
        GsController.INSTANCE.getPath(mapName, new RobotStatus<RobotPath>() {
            @Override
            public void success(RobotPath robotPath) {
                Log.d(TAG, "GET path success " + robotPath.getData().size());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "GET path fail " + error.getMessage());
            }
        });
    }

    /**
     * 取消导航
     */
    public void cancel_navigate() {
        Log.d(TAG, "开始请求取消导航");
        Content.robotState = 1;
        navigateSuccess = false;
        taskIndex = 0;
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

}
