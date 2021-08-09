package com.retron.robotAgent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.retron.robotAgent.R;
import com.retron.robotAgent.content.BaseEvent;
import com.retron.robotAgent.controller.RobotManagerController;
import com.retron.robotAgent.task.TaskManager;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.utils.EventBusMessage;
import com.retron.robotAgent.uvclamp.CheckLztekLamp;
import com.uslam.bean.MoveBean;
import com.uslam.factory.Factory;

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

    public static void initGlobal(String mapName) {
        Factory.getInstance(mContext, Content.ipAddress).initialize(mapName, "", 0, 0, 0, 3, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "initGlobal" + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE_RESULE, status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "initGlobal" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE_RESULE, error.getMessage()));
            }
        });
    }

    public static void initialize_directly(String mapName) {//不转圈初始化
        Factory.getInstance(mContext, Content.ipAddress).initialize(mapName, Content.InitializePositionName, 0, 0, 0, 1, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "initialize_directly" + status.getMsg());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE_RESULE, status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "initialize_directly" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE_RESULE, error.getMessage()));
            }
        });

    }

    public static void initialize(String mapName, String initializePositionName) {//转圈初始化

        Factory.getInstance(mContext, Content.ipAddress).initialize(mapName, initializePositionName, 0, 0, 0, 2, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE_RESULE, status.getMsg()));
                Log.d(TAG, "initialize" + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "initialize：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE_RESULE, error.getMessage()));
            }
        });
    }

    public static void is_initialize_finished() {
        Factory.getInstance(mContext, Content.ipAddress).is_initialize_finished(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "is_initialize_finished：" + status.toString());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.IS_INITIALIZE_FINISHED, status));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "is_initialize_finished：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.INITIALIZE_FAIL, error.getMessage()));
            }
        });
    }

    public static void stopInitialize() {//停止初始化
        Factory.getInstance(mContext, Content.ipAddress).stop_initialize(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "停止初始化");
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_initialize) + status.getMsg()));
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "停止初始化失败：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.REQUEST_MSG, mContext.getResources().getString(R.string.stop_initialize) + error.getMessage()));
            }
        });
    }

    static MoveBean moveBean = new MoveBean();

    public static void move(float linearSpeed, float angularSpeed) {
        moveBean.setLinearSpeed(linearSpeed);
        moveBean.setAngularSpeed(angularSpeed);
        Factory.getInstance(mContext, Content.ipAddress).move(moveBean, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "robot move：" + linearSpeed + ",   " + angularSpeed);
            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    public void startGaoXianSdk() {
        Log.d(TAG, "   导航服务启动");
        Factory.getInstance(mContext, Content.ipAddress).connect_robot(Content.ROBOROT_INF, null);
        TaskManager.getInstances(mContext).getRobotHealthy();
        //TaskManager.getInstances(mContext).robotStatus();
        ping();
    }

    private void ping() {
        disposables.add(Observable.interval(2, TimeUnit.SECONDS).subscribe(aLong -> Factory.getInstance(mContext, Content.ipAddress).ping(new RobotStatus<Status>() {
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
//            handler.postDelayed(runnable, 10 * 1000);
            EventBus.getDefault().post(new EventBusMessage(88888, ""));
            TaskManager.getInstances(mContext).modifyRobotParam(0.75);
            TaskManager.getInstances(mContext).deviceRobotVersion();
            TaskManager.getInstances(mContext).work_status();
            TaskManager.getInstances(mContext).loadMapList();
        }
        if (!connect) {
            Log.d(TAG, "重置底盘连接状态：" + connect);
            checkLztekLamp.openEth();
            checkLztekLamp.setEthAddress();
        }
    }

}
