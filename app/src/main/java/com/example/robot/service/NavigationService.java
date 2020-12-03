package com.example.robot.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.dcm360.controller.RobotController;
import com.dcm360.controller.gs.GSRobotController;
import com.dcm360.controller.gs.controller.GsController;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.system_bean.HealthStatus;
import com.dcm360.controller.gs.controller.bean.system_bean.RobotDeviceStatus;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.NavigationStatus;
import com.dcm360.controller.robot_interface.status.RobotStatus;
import com.dcm360.controller.utils.WebSocketUtil;
import com.example.robot.R;
import com.example.robot.controller.RobotManagerController;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.MaybeSource;
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

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        disposables = new CompositeDisposable();
        startGaoXianSdk();
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

    public static void initialize_directly(String mapName) {//不转圈初始化
        RobotManagerController.getInstance().getRobotController().initialize_directly(mapName, "Charging", new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d(TAG, "不转圈初始化成功" + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "不转圈初始化成功" + error.getMessage());
            }
        });

    }

    public static void initialize(String mapName) {//转圈初始化

        RobotManagerController.getInstance().getRobotController().initialize(mapName, "Origin", new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                EventBus.getDefault().post(new EventBusMessage(10027, status.getMsg()));
                Log.d(TAG, "转圈地初始化成功" + status.getMsg());
            }

            @Override
            public void error(Throwable error) {
                Log.d(TAG, "转圈地初始化失败：" + error.getMessage());
                EventBus.getDefault().post(new EventBusMessage(10027, error.getMessage()));
            }
        });
    }

    public static void is_initialize_finished() {
        RobotManagerController.getInstance().getRobotController().is_initialize_finished(new RobotStatus<Status>() {
            @Override
            public void success(Status status) {
                Log.d("zdzd :", "初始化：" + status.toString());
                EventBus.getDefault().post(new EventBusMessage(10034, status.getData()));
            }

            @Override
            public void error(Throwable error) {
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

    public static void navigationUp() {
        Log.d(TAG, "执行命令 navigationUp=");
        //Tx2RosManager.getInstanse().chargeState(false);充电
        RobotManagerController.getInstance().getRobotController().move(0.2f, 0.0f, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {

            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    public static void navigationDown() {
        Log.d(TAG, "执行命令 navigationDown=");
        RobotManagerController.getInstance().getRobotController().move(-0.2f, 0.0f, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {

            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    public static void navigationLeft() {
        Log.d(TAG, "执行命令 navigationLeft=");
        RobotManagerController.getInstance().getRobotController().move(0.0f, 0.2f, new RobotStatus<Status>() {
            @Override
            public void success(Status status) {

            }

            @Override
            public void error(Throwable error) {

            }
        });
    }

    public static void navigationRight() {
        Log.d(TAG, "执行命令 navigationRight=");
        RobotManagerController.getInstance().getRobotController().move(0.0f, -0.2f, new RobotStatus<Status>() {
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
        RobotManagerController.getInstance().getRobotController().connect_robot("http://10.7.6.88:8080");
        TaskManager.getInstances(mContext).robotStatus();
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
        Log.d(TAG, "底盘连接状态：" + connect);
        if (isStartNavigationService != connect) {
            Log.d(TAG, "底盘连接状态：" + connect);
//            TaskQueueManager.getInstances().notifyMessage("底盘连接状态：" + connect, "01");
            isStartNavigationService = connect;
            TaskManager.getInstances(this).loadMapList();
        }
    }

}
