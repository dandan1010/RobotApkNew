package com.example.robot.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.example.robot.MyApplication;
import com.example.robot.R;
import com.example.robot.adapter.TaskAdapter;
import com.example.robot.service.NavigationService;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.uvclamp.CheckLztekLamp;
import com.example.robot.uvclamp.UvcWarning;

import org.java_websocket.server.WebSocketServer;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotDetailActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.uvc_time)
    Spinner spinner;
    @BindView(R.id.residual_time)
    TextView residualTime;
    @BindView(R.id.controller_btn)
    Switch toLightControlBtn;
    @BindView(R.id.forward_img)
    ImageView forwardImg;
    @BindView(R.id.to_left_img)
    ImageView toLeftImg;
    @BindView(R.id.to_right_img)
    ImageView toRightImg;
    @BindView(R.id.fallback_img)
    ImageView fallbackImg;
    @BindView(R.id.robot_map)
    ImageView robotMap;
    @BindView(R.id.start_initialize)
    Button startInitialize;
    @BindView(R.id.main_relative)
    RelativeLayout mainRelative;
    @BindView(R.id.robot_task_list)
    RecyclerView robotTaskList;
    @BindView(R.id.robot_state)
    TextView robotState;
    @BindView(R.id.robot_power)
    TextView robotPower;
    @BindView(R.id.robot_low_power)
    TextView robotLowPower;
    @BindView(R.id.robot_video)
    TextView robotVideo;
    @BindView(R.id.robot_auto_task)
    TextView robotAutoTask;
    @BindView(R.id.robot_disinfection_num)
    TextView robotDisinfectionNum;
    @BindView(R.id.robot_disinfection_time)
    TextView robotDisinfectionTime;
    @BindView(R.id.robot_task_title)
    TextView robotTaskTitle;
    @BindView(R.id.start_navigate)
    Button startNavigate;
    @BindView(R.id.stop_navigate)
    Button stopNavigate;
    //    @BindView(R.id.robot_position)
//    ImageView robot_Position;
    @BindView(R.id.stop_initialize)
    Button stopInitialize;
    @BindView(R.id.save_task_queue)
    Button saveTaskQueue;
    @BindView(R.id.stop_task_queue)
    Button stopTaskQueue;
    @BindView(R.id.start_task_queue)
    Button startTaskQueue;
    @BindView(R.id.delete_task_queue)
    Button deleteTaskQueue;
    private UvcWarning uvcWarning;
    private CheckLztekLamp checkLztekLamp;
    private Context mContext;
    public static MyHandler myHandler;
    private int ledtime = 0;
    private Long workTime;
    private boolean completeFlag = false;
    private boolean isTaskFlag = false;
    private long pauseTime = 0;
    private static byte[] bytes;
    private WebSocketServer server;
    private GsonUtils gsonUtils;
    private String tvText;
    private TaskAdapter taskAdapter;
    private List<String> taskList = new ArrayList<>();
    private String mapName;
    private byte battery = 0;
    private int deletePosition = -1;
    private ImageView robot_Position;
    private int i = 0;
    private String taskName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
        ButterKnife.bind(this);
        mContext = RobotDetailActivity.this;
        robot_Position = new ImageView(mContext);
        server = new MyApplication().getServer();
        //选取地图之后，展示地图数据
        mapName = getIntent().getStringExtra("mapName");
        TaskManager.getInstances(mContext).getMapPic(mapName);
        TaskManager.getInstances(mContext).use_map(mapName);
        TaskManager.getInstances(mContext).getTaskQueues(mapName);
        //获取设备的信息
//        TaskQueueManager.getInstances().deviceStatus();
        initView();
        initListener();

    }

    private void initView() {
        uvcWarning = new UvcWarning(mContext);
        checkLztekLamp = new CheckLztekLamp(mContext);

        gsonUtils = new GsonUtils();
        spinner.setSelection(3);
        myHandler = new MyHandler(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        robotTaskList.setLayoutManager(linearLayoutManager);
        robotTaskList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        taskAdapter = new TaskAdapter(this, R.layout.item_recycler);

        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void OnItemClickListener(View view, int position, String itemString) {
                deletePosition = position;
                taskName = itemString;
            }
        });
        TaskManager.getInstances(mContext).getTaskQueues(mapName);

        checkLztekLamp.openBatteryPort();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler1.removeCallbacks(runnable1);
        handler2.removeCallbacks(runnable2);
        handler3.removeCallbacks(runnable3);
        handler4.removeCallbacks(runnable4);
        myHandler.removeCallbacks(runnablePosition);

    }

    private void initListener() {
        toLightControlBtn.setOnCheckedChangeListener(this);
        forwardImg.setOnTouchListener(this);
        toLeftImg.setOnTouchListener(this);
        toRightImg.setOnTouchListener(this);
        fallbackImg.setOnTouchListener(this);
    }

    /**
     * 复选框的点击事件
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.controller_btn:
                if (b) {
                    completeFlag = false;
                    ledtime = 0;
                    uvcWarning.startWarning();
                    Content.robotState = 5;
                    Content.time = 1000;
                    spinner.setEnabled(false);
                    String spinnerItem = (String) spinner.getSelectedItem();
                    workTime = System.currentTimeMillis() +
                            Long.parseLong(spinnerItem.substring(0, spinnerItem.length() - 2)) * 60 * 1000 + 10 * 1000;
                    Log.d(TAG, "onCheckedChanged：workTime : " + workTime);
                } else {
                    ledtime = 0;
                    Content.robotState = 1;
                    Content.time = 4000;
                    uvcWarning.stopWarning();
                    checkLztekLamp.stopUvc1Lamp();
                    checkLztekLamp.stopUvc2Lamp();
                    checkLztekLamp.stopUvc3Lamp();
                    spinner.setEnabled(true);
                    tvText = calculateDays(System.currentTimeMillis());
                    residualTime.setText(tvText);
                    gsonUtils.setTvTime(tvText);
                    if (server != null) {
                        server.broadcast(gsonUtils.putJsonMessage(Content.TV_TIME));
                    }
                }
                myHandler.sendEmptyMessageDelayed(1, 0);
                break;
        }
    }

    /**
     * 初始化转圈圈点击事件
     */
    @OnClick({R.id.start_initialize, R.id.stop_initialize,
            R.id.start_navigate, R.id.pause_task_queue, R.id.stop_navigate,
            R.id.save_task_queue, R.id.stop_task_queue, R.id.start_task_queue,
            R.id.delete_task_queue, R.id.resume_task_queue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_initialize:
                if (NavigationService.isStartNavigationService) {
                    Log.d(TAG, "初始化转圈");
                    NavigationService.initialize();
                } else {
                    Toast.makeText(mContext, "底盘还没有链接成功，请稍后重试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop_initialize:
                if (NavigationService.isStartNavigationService) {
                    Log.d(TAG, "停止初始化转圈");
                    NavigationService.stopInitialize();
                } else {
                    Toast.makeText(mContext, "底盘还没有链接成功，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            case R.id.start_navigate://获取充电点,导航到导航点
                TaskManager.getInstances(mContext).charge_Position();
                break;
            case R.id.stop_navigate:
                TaskManager.getInstances(mContext).cancel_navigate();
                break;
            case R.id.pause_task_queue:
                isTaskFlag = true;
                TaskManager.getInstances(mContext).pauseTaskQueue();
                break;
            case R.id.resume_task_queue:
                isTaskFlag = false;
                TaskManager.getInstances(mContext).resumeTaskQueue();
                break;
            case R.id.save_task_queue:
                TaskManager.getInstances(mContext).save_taskQueue("task" + i);
                i++;
                break;
            case R.id.stop_task_queue:
                TaskManager.getInstances(mContext).stopTaskQueue();
                break;
            case R.id.start_task_queue:
                TaskManager.getInstances(mContext).startTaskQueue(taskName);
                break;
            case R.id.delete_task_queue:
                TaskManager.getInstances(mContext).deleteTaskQueue(mapName, taskName);
                break;
            default:
                break;
        }
    }

    //持续移动 下
    Handler handler1 = new Handler();
    Runnable runnable1 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.navigationDown();
            handler1.postDelayed(runnable1, 10);
        }
    };

    //持续移动 上
    Handler handler2 = new Handler();
    Runnable runnable2 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.navigationUp();
            handler2.postDelayed(runnable2, 10);
        }
    };

    //持续移动 左
    Handler handler3 = new Handler();
    Runnable runnable3 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.navigationLeft();
            handler3.postDelayed(runnable3, 10);
        }
    };

    //持续移动 右
    Handler handler4 = new Handler();
    Runnable runnable4 = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.navigationRight();
            handler4.postDelayed(runnable4, 10);
        }
    };

    /**
     * 移动按钮的touch事件
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.forward_img:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    handler2.postDelayed(runnable2, 10);
                    Log.d("zdzd", "前进");
                    Content.robotState = 3;
                    Content.time = 300;

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler2.removeCallbacks(runnable2);
                    Content.robotState = 1;
                    Content.time = 4000;
                    Log.d(TAG, "抬起事件:上");
                }
                break;
            case R.id.to_left_img:
                Toast.makeText(mContext, "向左", Toast.LENGTH_SHORT).show();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    handler3.postDelayed(runnable3, 10);
                    Content.time = 300;
                    Content.robotState = 3;

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler3.removeCallbacks(runnable3);
                    Content.robotState = 1;
                    Content.time = 4000;
                    Log.d(TAG, "抬起事件:左");
                }
                break;
            case R.id.to_right_img:
                Toast.makeText(mContext, "向右", Toast.LENGTH_SHORT).show();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    handler4.postDelayed(runnable4, 10);
                    Content.time = 300;
                    Content.robotState = 3;

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler4.removeCallbacks(runnable4);
                    Content.robotState = 1;
                    Content.time = 4000;
                    Log.d(TAG, "抬起事件:右");
                }
                break;
            case R.id.fallback_img:
                Toast.makeText(mContext, "后退", Toast.LENGTH_SHORT).show();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    handler1.postDelayed(runnable1, 10);
                    Content.time = 300;
                    Content.robotState = 3;

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler1.removeCallbacks(runnable1);
                    Content.robotState = 1;
                    Content.time = 4000;
                    Log.d(TAG, "抬起事件:下");
                }
                break;
        }

        return true;
    }

    public class MyHandler extends Handler {//防止内存泄漏
        //持有弱引用MainActivity,GC回收时会被回收掉.
        private final WeakReference<RobotDetailActivity> mAct;

        private MyHandler(RobotDetailActivity mainActivity) {
            mAct = new WeakReference<RobotDetailActivity>(mainActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "case 1  " + workTime);
                    startLoopDetection();
                    break;
                case 2:
                    Log.d(TAG, "case 2  " + completeFlag);

                    if (!completeFlag) {
                        startUvcDetection();
                        tvText = calculateDays(workTime);
                        residualTime.setText(tvText);
                        gsonUtils.setTvTime(tvText);
                        if (server != null) {
                            server.broadcast(gsonUtils.putJsonMessage(Content.TV_TIME));
                        }

                    } else {
                        residualTime.setText("消毒完成");
                        gsonUtils.setTvTime("消毒完成");
                        if (server != null) {
                            server.broadcast(gsonUtils.putJsonMessage(Content.TV_TIME));
                        }
                        toLightControlBtn.setChecked(false);
                        checkLztekLamp.stopUvc1Lamp();
                        checkLztekLamp.stopUvc2Lamp();
                        checkLztekLamp.stopUvc3Lamp();
                        Content.robotState = 1;
                        Content.time = 4000;
                        uvcWarning.startCompletePrompt();
                        completeFlag = false;
                        Log.d(TAG, "恢复任务" + Content.taskState);
                        if (Content.taskState == 2) {
                            TaskManager.getInstances(mContext).resumeTaskQueue();
                        }
                    }
                    break;
                case 3:
                    Log.d(TAG, "case 3  " + workTime);
                    startUvcDetection();
                    break;
                case 4:
                    Log.d(TAG, "case 4  " + workTime);
                    residualTime.setText("电量回充,消毒未完成");
                    gsonUtils.setTvTime("电量回充,消毒未完成");
                    if (server != null) {
                        server.broadcast(gsonUtils.putJsonMessage(Content.TV_TIME));
                    }
                    checkLztekLamp.stopUvc1Lamp();
                    checkLztekLamp.stopUvc2Lamp();
                    checkLztekLamp.stopUvc3Lamp();

                    Content.robotState = 6;
                    Content.time = 4000;
                    uvcWarning.startCompletePrompt();
                    if (Content.taskState == 1) {
                        TaskManager.getInstances(mContext).pauseTaskQueue();
                    }
                    break;
                case 5:
                    break;

                default:
                    break;
            }

        }
    }

    /**
     * 10秒的sensor检查
     */
    private void startLoopDetection() {
        Log.d(TAG, "startLoopDetection: " + (10 - ledtime) + "秒");
        tvText = (10 - ledtime) + "秒";
        residualTime.setText(tvText);
        gsonUtils.setTvTime(tvText);
        if (server != null) {
            server.broadcast(gsonUtils.putJsonMessage(Content.TV_TIME));
        }
        ledtime++;
        if (!toLightControlBtn.isChecked()) {
            ledtime = 0;
            return;
        }
        if (ledtime <= 10) {
            Log.d(TAG, "正在警告");
            if (checkLztekLamp.getGpioSensorState()) {
                //有人靠近
                Log.v(TAG, "10秒重置");
                ledtime = 0;
            }
        } else {
            Log.d(TAG, "警告结束，关闭警告和led，开启uvc灯");
            ledtime = 0;
            uvcWarning.stopWarning();
            Content.robotState = 5;
            Content.time = 1000;
            startUvcDetection();
            return;
        }
        myHandler.sendEmptyMessageDelayed(1, 1000);
    }

    /**
     * 开启uvc灯
     */
    private void startUvcDetection() {
        Log.d(TAG, "startUvcDetection" + battery);
        if (!toLightControlBtn.isChecked()) {
            return;
        }
        if (battery < 30) {//是否到达回冲电量
            myHandler.sendEmptyMessageDelayed(4, 1000);
        } else if (!checkLztekLamp.getGpioSensorState() && !isTaskFlag) {
            Log.d(TAG, "startUvcDetection" + "关led灯,开uvc灯");
            if (pauseTime != 0) {
                workTime = workTime + System.currentTimeMillis() - pauseTime;
            }
            pauseTime = 0;
            uvcWarning.stopWarning();
            Content.robotState = 5;
            Content.time = 1000;
            checkLztekLamp.setUvcMode();
            checkLztekLamp.startUvc1Lamp();
            checkLztekLamp.startUvc2Lamp();
            checkLztekLamp.startUvc3Lamp();
            myHandler.sendEmptyMessageDelayed(2, 1000);
        } else {
            if (pauseTime == 0) {
                pauseTime = System.currentTimeMillis();
            }
            Log.d(TAG, "startUvcDetection" + "开led灯,关uvc灯");
            uvcWarning.startWarning();
            Content.robotState = 5;
            Content.time = 1000;
            checkLztekLamp.stopUvc1Lamp();
            checkLztekLamp.stopUvc2Lamp();
            checkLztekLamp.stopUvc3Lamp();
            myHandler.sendEmptyMessageDelayed(3, 1000);
        }
    }

    public String calculateDays(long date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(date);//获取当前时间
        String str = formatter.format(curDate);
        long days = 0, hours = 0, minutes = 0, second = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        try {
            d1 = df.parse(str);
            Date d2 = new Date(System.currentTimeMillis());//你也可以获取当前时间
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            second = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (hours == 0 && minutes == 0 && second == 0) {
            Log.d(TAG, "TIME completeFlag  " + completeFlag);
            completeFlag = true;

            return "杀毒完成";
        }
        return hours + "小时" + minutes + "分" + second + "秒";
    }

    private Runnable runnablePosition = new Runnable() {
        @Override
        public void run() {
            TaskManager.getInstances(mContext).getPositions();
            myHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onBaseEventMessage(EventBusMessage messageEvent) {
        super.onBaseEventMessage(messageEvent);
        Log.d(TAG, "messageEvent.getState() :  " + messageEvent.getState());
//上位机本身发送的消息
        if (messageEvent.getState() == 1002) {
            bytes = (byte[]) messageEvent.getT();
            Glide.with(mContext).load(bytes).into(robotMap);

            myHandler.postDelayed(runnablePosition, 1000);
            if (server != null) {
                server.broadcast(bytes);
            }
        } else if (messageEvent.getState() == 1003) {
            RobotPosition robotPosition = (RobotPosition) messageEvent.getT();
            float x = (float) robotPosition.getGridPosition().getX();
            float y = (float) robotPosition.getGridPosition().getY();
            float rotate = (float) robotPosition.getAngle();

            Log.d(TAG, "地图：X" + robotPosition.getMapInfo().getGridWidth() + "Y :" + robotPosition.getMapInfo().getGridHeight());
            Log.d(TAG, "地图rotate：X" + x + "Y :" + y);
            Log.d(TAG, "地图originX：" + robotPosition.getMapInfo().getOriginX() + "originY :" + robotPosition.getMapInfo().getOriginY());
            Log.d(TAG, "地图图片rotate：X" + robotMap.getWidth() + "  Y :" + robotMap.getHeight());
            mainRelative.removeView(robot_Position);
            robot_Position.setImageResource(R.drawable.ic_robot_position);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins((int) (robotMap.getWidth() / robotPosition.getMapInfo().getGridWidth() * x + robotMap.getLeft() + 11.2f),
                    (int) (robotMap.getHeight() / robotPosition.getMapInfo().getGridHeight() * y + robotMap.getTop() + 11.2f), 0, 0);

            robot_Position.setLayoutParams(layoutParams);
            mainRelative.addView(robot_Position);
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x));
            list.add(String.valueOf(y));
            if (server != null) {
                server.broadcast(gsonUtils.putJsonMessage(Content.GETPOSITION));
            }
        } else if (messageEvent.getState() == 1004) {
            byte[] bytes = (byte[]) messageEvent.getT();
            battery = bytes[23];
            robotPower.setText("电池容量：" + battery + "%");
        } else if (messageEvent.getState() == 1006) {
            RobotTaskQueueList robotTaskQueueList = (RobotTaskQueueList) messageEvent.getT();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < robotTaskQueueList.getData().size(); i++){
                list.add(robotTaskQueueList.getData().get(i).getName());
            }
            taskAdapter.refeshList(list);
            robotTaskList.setAdapter(taskAdapter);
            taskAdapter.notifyDataSetChanged();
        } else if (messageEvent.getState() == 1007) {
            Toast.makeText(mContext, "到达指定位置开始杀毒", Toast.LENGTH_SHORT).show();
            spinner.setSelection((Integer) messageEvent.getT());
            toLightControlBtn.setChecked(true);


//phone 发送的命令
        } else if (messageEvent.getState() == 10001) {//后退
            handler1.postDelayed(runnable1, 10);
        } else if (messageEvent.getState() == 10002) {//前进
            handler2.postDelayed(runnable2, 10);
        } else if (messageEvent.getState() == 10003) {//左转
            handler3.postDelayed(runnable3, 10);
        } else if (messageEvent.getState() == 10004) {//右转
            handler4.postDelayed(runnable4, 10);
        } else if (messageEvent.getState() == 10005) {//开始消毒检测
            spinner.setSelection((Integer) messageEvent.getT());
            toLightControlBtn.setChecked(true);
        } else if (messageEvent.getState() == 10006) {//停止消毒检测
            toLightControlBtn.setChecked(false);
        } else if (messageEvent.getState() == 10007) {//停前
            handler2.removeCallbacks(runnable2);
        } else if (messageEvent.getState() == 10008) {//停退
            handler1.removeCallbacks(runnable1);
        } else if (messageEvent.getState() == 10009) {//停左
            handler3.removeCallbacks(runnable3);
        } else if (messageEvent.getState() == 10010) {//停右
            handler4.removeCallbacks(runnable4);
        } else if (messageEvent.getState() == 10011) {//地图列表
            TaskManager.getInstances(mContext).loadMapList();
        } else if (messageEvent.getState() == 10012) {//地图列表获取后发送
            RobotMap robotMap = (RobotMap) messageEvent.getT();
            List<RobotMap.DataBean> data = robotMap.getData();
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                stringList.add(data.get(i).getName());
            }
            gsonUtils.setData(stringList);
            if (server != null) {
                server.broadcast(gsonUtils.putJsonMessage(Content.GETMAPNAME));
            }
        } else if (messageEvent.getState() == 10013) {//存储和执行任务队列
//            List<String> list = (List<String>) messageEvent.getT();
//            TaskManager.getInstances(mContext).save_taskQueue(list, list.get(0));
        } else if (messageEvent.getState() == 10014) {//删除任务队列
//            String taskName = (String) messageEvent.getT();
//            TaskManager.getInstances(mContext).deleteTaskQueue(taskName);
        } else if (messageEvent.getState() == 10015) {//获取任务列表
//            TaskManager.getInstances(mContext).getTaskQueues();
        }
    }

}