package com.example.robotapk.task;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotDeviceStatus;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotPath;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotSaveTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueue;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.example.robotapk.utils.EventBusMessage;
import com.example.robotapk.controller.RobotManagerController;

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
    private static TaskManager taskQueueManager;
    private MyThread myThread;
    private boolean scanningFlag = false;


    private TaskManager() {
    }

    public static TaskManager getInstances() {
        if (taskQueueManager == null) {
            synchronized (TaskManager.class) {
                if (taskQueueManager == null)
                    taskQueueManager = new TaskManager();
            }
        }

        return taskQueueManager;
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
                /**
                 * 导航到点
                 * */
                RobotManagerController.getInstance().getRobotController().navigate_Position(mapName, s, new RobotStatus<Status>() {

                    @Override
                    public void success(Status status) {
                        if (status == null) {
                            Log.d(TAG, "地图" + mapName + "充电status空" + s);
                            return;
                        }
                        if (status.isSuccessed()) {
                            Log.d(TAG, "地图" + mapName + "充电命令成功");
                        } else {
                            Log.d(TAG, "地图" + mapName + "充电命令失败" + status.toString());
                        }
                    }

                    @Override
                    public void error(Throwable error) {
                        if (error == null) {
                            Log.d(TAG, "地图" + mapName + "充电命令异常 error = null");
                            return;
                        }
                        Log.d(TAG, "地图" + mapName + "充电命令异常" + error.getMessage());
                    }
                });
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
//               scanMapPng();

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
     * */
    public void deleteMap(String map_name){
        GsController.INSTANCE.deleteMap(map_name, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "删除地图成功 :  ");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "删除地图失败 :  "+ error.getMessage());
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
    * */
    public void save_taskQueue(List<String> pois,String taskName) {
        RobotTaskQueue robotTaskQueue = exeTaskPoi(pois,taskName);
        Log.d(TAG,"save taskName : " + taskName + ",  pois size : " + pois.size());
        RobotManagerController.getInstance().getRobotController().save_taskQueue(robotTaskQueue, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG,"save task success");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"save task fail :" + error.getMessage());
            }
        });
        //startTaskQueue(robotTaskQueue);
    }

    public RobotTaskQueue exeTaskPoi(List<String> pois, String taskName) {
        RobotTaskQueue taskQueue = new RobotTaskQueue();
        taskQueue.setName(taskName);
        taskQueue.setMap_name(mapName);
        taskQueue.setLoop(false);

        if (pois != null && pois.size() > 0) {
            List<RobotTaskQueue.TasksBean> tasksBeans = new ArrayList<>();
            for (int i= 0 ;i<pois.size();i++) {
                RobotTaskQueue.TasksBean bean = new RobotTaskQueue.TasksBean();
                RobotTaskQueue.TasksBean.StartParamBean paramBean = new RobotTaskQueue.TasksBean.StartParamBean();
                paramBean.setMap_name(mapName);
                Log.d("ZDZD:" , "pois name : " + pois.get(i));
                paramBean.setPath_name(pois.get(i));
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
     * */
    public void startTaskQueue(RobotTaskQueue robotTaskQueue){
        Log.d(TAG,"start task taskName : " + robotTaskQueue.getName() + ",   mapName : " + mapName);
        RobotManagerController.getInstance().getRobotController().start_taskQueue(robotTaskQueue, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG,"start task success");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"start task success : " + error.getMessage());
            }
        });
    }

    /**
     * 删除任务队列
     * */
    public void deleteTaskQueue(String taskName){
        Log.d(TAG,"delete taskName : " + taskName + ",   mapName : " + mapName);
        GsController.INSTANCE.deleteTaskQueue(mapName, taskName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG,"delete taskqueue success");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"delete taskqueue fail : " + error.getMessage());
            }
        });
    }

    /**
     * 获取任务队列
     * */
    public void getTaskQueues(){
        Log.d(TAG,"get taskQueues mapName : " + mapName);
        GsController.INSTANCE.taskQueues(mapName, new RobotStatus<RobotTaskQueueList>() {
            @Override
            public void success(RobotTaskQueueList robotTaskQueueList) {
                Log.d(TAG,"get taskQueues success" + robotTaskQueueList.getData().size());
                EventBus.getDefault().post(new EventBusMessage(1006, robotTaskQueueList));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"get taskQueues fail : " + error.getMessage());
            }
        });
    }

    /**
     * 记录点
     * */
    public void addPosition(String positionName) {
        GsController.INSTANCE.addPosition(positionName, 2, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG,"addPosition success");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"addPosition fail : " + error.getMessage());

            }
        });
    }

    /**
     * 删除点
     * */
    public void deletePosition(String positionName) {
        GsController.INSTANCE.deletePosition(mapName, positionName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG,"deletePosition success");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"deletePosition fail : " + error.getMessage());

            }
        });
    }
    /**
     * 重命名点
     * */
    public void renamePosition(String originName, String newName) {
        GsController.INSTANCE.renamePosition(mapName, originName, newName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG,"renamePosition success");
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"renamePosition fail : " + error.getMessage());

            }
        });
    }

    /**
     * 地图点数据
     * */
    public void getPosition() {
        GsController.INSTANCE.getPosition(TaskManager.getInstances().mapName, 2, new RobotStatus<RobotPositions>() {
            @Override
            public void success(RobotPositions robotPositions) {
                Log.d(TAG,"getPosition success : " + robotPositions.getData().size());
//                List<String> list = new ArrayList<>();
//                for (int i = 0 ;i<robotPositions.getData().size();i++){
//                    list.add(robotPositions.getData().get(i).getName());
//                }
//                TaskManager.getInstances().save_taskQueue(list,"task1");

            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG,"renamePosition fail : " + error.getMessage());
            }
        });


    }
    /**
     * 获取路径
     * */
    public void getPath(){
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

}
