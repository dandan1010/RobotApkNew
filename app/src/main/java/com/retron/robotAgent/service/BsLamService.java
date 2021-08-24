package com.retron.robotAgent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.task.TaskManager;
import com.retron.robotAgent.utils.EventBusMessage;
import com.retron.robotAgent.uvclamp.CheckLztekLamp;
import com.uslam.factory.Factory;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author liyan
 */
public class BsLamService extends Service {
    private static final String TAG = "BsLamService";

    public static CompositeDisposable disposables;

    private static Context mContext;
    public static boolean isStartNavigationService = false;
    private CheckLztekLamp checkLztekLamp;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
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

    public void startGaoXianSdk() {
        Log.d(TAG, "   导航服务启动");
        Factory.getInstance(mContext, Content.ipAddress).connect_robot(Content.ROBOROT_INF, "uuid");
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
