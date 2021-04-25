package com.example.robot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.example.robot.R;
import com.example.robot.controller.RobotManagerController;
import com.example.robot.task.TaskManager;
import com.example.robot.content.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.ServerConnoct;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author liyan
 */
public class NavigationService extends Service {
    private static final String TAG = "NavigationService";

    public static CompositeDisposable disposables;

    private static Context mContext;
    public static boolean isStartNavigationService = false;
    public static String positions = "";
    private boolean serverIsRun = false;
    private boolean threadIsRun = false;
    private CheckLztekLamp checkLztekLamp;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        disposables = new CompositeDisposable();
        startGaoXianSdk();
        checkLztekLamp = new CheckLztekLamp(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposables != null && !disposables.isDisposed())
            disposables.dispose();
    }

    public static void initGlobal(String mapName){
        GsController.INSTANCE.initGlobal(mapName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "initGlobal" + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(10027, status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "initGlobal" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10027, error.getMessage()));
            }
        });
    }

    public static void initialize_directly(String mapName) {//不转圈初始化
        RobotManagerController.getInstance().getRobotController().initialize_directly(mapName, Content.CHARGING_POINT, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "initialize_directly" + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(10027, status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "initialize_directly" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10027, error.getMessage()));
            }
        });

    }

    public static void initialize(String mapName, String initializePositionName) {//转圈初始化

        RobotManagerController.getInstance().getRobotController().initialize(mapName, initializePositionName, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                EventBus.getDefault().post(new EventBusMessage(10027, status.getMsg()));
                Log.d(TAG, "initialize" + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "initialize：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10027, error.getMessage()));
            }
        });
    }

    public static void is_initialize_finished() {
        RobotManagerController.getInstance().getRobotController().is_initialize_finished(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "is_initialize_finished：" + status.toString());
                EventBus.getDefault().post(new EventBusMessage(10034, status));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "is_initialize_finished：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10035, error.getMessage()));
            }
        });
    }

    public static void stopInitialize() {//停止初始化
        RobotManagerController.getInstance().getRobotController().stop_initialize(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "停止初始化");
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_initialize) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "停止初始化失败：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.stop_initialize) + error.getMessage()));
            }
        });
    }

    public static void move(float linearSpeed, float angularSpeed) {
        RobotManagerController.getInstance().getRobotController().move(linearSpeed, angularSpeed, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {

            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    public void startGaoXianSdk() {
        Log.d(TAG, "   导航服务启动");
        RobotManagerController.getInstance().getRobotController().connect_robot(Content.ROBOROT_INF);
        TaskManager.getInstances(mContext).getRobotHealthy();
        //TaskManager.getInstances(mContext).robotStatus();
        ping();
    }

    private void ping() {
        disposables.add(Observable.interval(2, TimeUnit.SECONDS).subscribe(aLong -> RobotManagerController.getInstance().getRobotController().ping(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "gxRobotStatus = " + status.toString());
                connectStatus(status != null && status.isSuccessed());
            }

            @Override
            public void error(Throwable error) {
                connectStatus(false);
            }
        }), throwable -> {
            Log.d(TAG, "导航:ping:" + throwable.getMessage());
        }));
    }

    private void connectStatus(boolean connect) {
        Log.d(TAG, "底盘连接状态：" + connect + "      , WorkingMode : " + Content.Working_mode);
        if (isStartNavigationService != connect) {
            Log.d(TAG, "底盘连接状态：" + connect);
            isStartNavigationService = connect;
            handler.postDelayed(runnable, 10 * 1000);
            TaskManager.getInstances(mContext).modifyRobotParam(0.7);
        }
        if (!connect) {
            Log.d(TAG, "重置底盘连接状态：" + connect);
            checkLztekLamp.openEth();
            checkLztekLamp.setEthAddress();
            try {
                Content.server.stop();
                Content.server = null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ServerConnoct.getInstance().connect(mContext);
        }
    };

}
