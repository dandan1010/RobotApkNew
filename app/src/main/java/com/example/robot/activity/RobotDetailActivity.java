package com.example.robot.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotPosition;
import com.dcm360.controller.gs.controller.bean.paths_bean.RobotTaskQueueList;
import com.example.robot.R;
import com.example.robot.adapter.MapManagerAdapter;
import com.example.robot.adapter.TaskAdapter;
import com.example.robot.bean.SaveTaskBean;
import com.example.robot.receiver.AlarmReceiver;
import com.example.robot.service.NavigationService;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.uvclamp.CheckLztekLamp;
import com.example.robot.uvclamp.UvcWarning;
import com.lztek.toolkit.AddrInfo;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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

public class RobotDetailActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener, MapManagerAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.uvc_time)
    Spinner spinner;
    //    @BindView(R.id.scanning_map_icon)
//    ImageView scanningMapIcon;
    @BindView(R.id.map_recycler)
    RecyclerView mapRecycler;
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
    @BindView(R.id.robot_power)
    TextView robotPower;
    @BindView(R.id.robot_disinfection_time)
    TextView robotDisinfectionTime;
    @BindView(R.id.robot_task_title)
    TextView robotTaskTitle;
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
    //    @BindView(R.id.map_relative)
//    RelativeLayout mapRelative;
    @BindView(R.id.scanning_map)
    Button scanningMap;
    @BindView(R.id.develop_map)
    Button developMap;
    @BindView(R.id.cancel_scanning_map)
    Button cancelScanningMap;
    @BindView(R.id.add_position)
    Button addPosition;
    @BindView(R.id.delete_position)
    Button deletePosition;
    @BindView(R.id.pause_task_queue)
    Button pauseTaskQueue;
    @BindView(R.id.resume_task_queue)
    Button resumeTaskQueue;
    @BindView(R.id.alarm_btn)
    Button alarmBtn;
    @BindView(R.id.map_lin)
    RelativeLayout mapLin;
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
    private GsonUtils gsonUtils;
    private String tvText;
    private TaskAdapter taskAdapter;
    private List<String> taskList = new ArrayList<>();
    private byte battery = 0;

    private ImageView robot_Position;
    private int i = 0;
    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;
    private MapManagerAdapter mapManagerAdapter;
    private List<RobotMap.DataBean> data;
    private boolean isDevelop = false;
    private float x;
    private float y;
    private double angle;
    private ImageView imageView;
    private int deletePositions = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_robot);
        ButterKnife.bind(this);
        mContext = RobotDetailActivity.this;
        robot_Position = new ImageView(mContext);
//        mapLin.removeView(robot_Position);
//        robot_Position.setImageResource(R.drawable.ic_baseline_brightness_1_24);
//        robot_Position.setPaddingRelative(50,50,0,0);
//        mapLin.addView(robot_Position);
        initView();
        initListener();

    }

    private void initView() {
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mContext);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        mapRecycler.setLayoutManager(linearLayoutManager1);
        mapRecycler.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mapManagerAdapter = new MapManagerAdapter(this, R.layout.item_recycler);
        mapManagerAdapter.setOnItemClickListener(this);

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
                deletePositions = position;
                Content.taskName = itemString;
            }
        });
        TaskManager.getInstances(mContext).getTaskQueues(Content.mapName);

        checkLztekLamp.openBatteryPort();
        if (!checkLztekLamp.getEthEnable()) {
            Log.d(TAG, "网络设置失败");
        } else {
            Log.d(TAG, "网络设置成功");
        }

        AddrInfo addrInfo = checkLztekLamp.getAddrInfo();
        Log.d("zdzd : ", "" + addrInfo.getIpAddress());
        Log.d("zdzd : ", "" + addrInfo.getGateway());
        Log.d("zdzd : ", "" + addrInfo.getNetmask());
        Log.d("zdzd : ", "" + addrInfo.getIpMode());
        Log.d("zdzd : ", "" + addrInfo.getDns());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("zdzd ", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("zdzd ", "onDestroy");
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
                    if (Content.server != null) {
                        Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
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
            R.id.pause_task_queue, R.id.stop_navigate, R.id.delete_position,
            R.id.save_task_queue, R.id.stop_task_queue, R.id.start_task_queue,
            R.id.delete_task_queue, R.id.resume_task_queue, R.id.add_position,
            R.id.alarm_btn, R.id.scanning_map, R.id.cancel_scanning_map, R.id.develop_map})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_initialize:
                if (NavigationService.isStartNavigationService) {
                    Log.d(TAG, "初始化转圈");
                    NavigationService.initialize(Content.mapName);
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
                break;
            case R.id.stop_navigate:
                TaskManager.getInstances(mContext).cancel_navigate();
                break;
            case R.id.scanning_map:
                EditText editText = new EditText(this);
                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setTitle("输入地图的名字：")
                        .setView(editText)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TaskManager.getInstances(mContext).start_scan_map(editText.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.cancel_scanning_map:
                isDevelop = false;
                TaskManager.getInstances(mContext).stopScanMap();
                break;
            case R.id.develop_map:
                isDevelop = true;
                NavigationService.initialize(Content.mapName);
                break;
            case R.id.pause_task_queue:
                isTaskFlag = true;
                if (Content.taskState != 2) {
                    TaskManager.getInstances(mContext).pauseTaskQueue();
                }
                break;
            case R.id.resume_task_queue:
                isTaskFlag = false;
                if (!toLightControlBtn.isChecked()) {
                    TaskManager.getInstances(mContext).resumeTaskQueue();
                }
                break;
            case R.id.save_task_queue:

                List<SaveTaskBean> list = new ArrayList<>();
                SaveTaskBean saveTaskBean = new SaveTaskBean();
                saveTaskBean.setPositionName("aaa");
                saveTaskBean.setTime(0);
                list.add(saveTaskBean);

                SaveTaskBean saveTaskBean1 = new SaveTaskBean();
                saveTaskBean1.setPositionName("ggg");
                saveTaskBean1.setTime(0);
                list.add(saveTaskBean1);

                TaskManager.getInstances(mContext).save_taskQueue(Content.mapName, "task" + i, list);
                i++;
                break;
            case R.id.stop_task_queue:
                TaskManager.getInstances(mContext).stopTaskQueue(Content.mapName);
                toLightControlBtn.setChecked(false);
                break;
            case R.id.start_task_queue:
                if (Content.taskName != null) {
                    TaskManager.getInstances(mContext).startTaskQueue(Content.mapName, Content.taskName);
                }
                break;
            case R.id.delete_task_queue:
                TaskManager.getInstances(mContext).deleteTaskQueue(Content.mapName, Content.taskName);
                break;
            case R.id.add_position:
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName("ddd");
                positionListBean.setGridX(105);
                positionListBean.setGridY(180);
                positionListBean.setAngle(0);
                positionListBean.setType(2);
                positionListBean.setMapName(Content.mapName);
                TaskManager.getInstances(mContext).add_Position(positionListBean);
                break;
            case R.id.delete_position:
                TaskManager.getInstances(mContext).deletePosition(Content.mapName, "bbb");
                break;
            case R.id.alarm_btn:
                setAlarmTime("task0", System.currentTimeMillis() + 2 * 60 * 1000);
                break;
            default:
                break;
        }
    }

    //初始化结果
    Handler handlerInitialize = new Handler();
    Runnable runnableInitialize = new Runnable() {
        @Override
        public synchronized void run() {
            NavigationService.is_initialize_finished();
        }
    };

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

    @Override
    public void OnItemClickListener(View view, int position) {
        Content.mapName = data.get(position).getName();
        TaskManager.getInstances(mContext).getMapPic(Content.mapName);
        TaskManager.getInstances(mContext).use_map(Content.mapName);
        TaskManager.getInstances(mContext).getTaskQueues(Content.mapName);
        TaskManager.getInstances(mContext).getPosition(Content.mapName);
    }

    @Override
    public void OnItemLongClickListener(View view, int position) {
        String mapName = data.get(position).getName();
        data.remove(position);
        mapManagerAdapter.refeshList(data);
        mapManagerAdapter.notifyDataSetChanged();
        TaskManager.getInstances(mContext).deleteMap(mapName);
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
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                        }

                    } else {
                        residualTime.setText("消毒完成");
                        gsonUtils.setTvTime("消毒完成");
                        if (Content.server != null) {
                            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
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
                    if (Content.server != null) {
                        Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
                    }
                    checkLztekLamp.stopUvc1Lamp();
                    checkLztekLamp.stopUvc2Lamp();
                    checkLztekLamp.stopUvc3Lamp();

                    Content.robotState = 6;
                    Content.time = 4000;
                    uvcWarning.startCompletePrompt();
                    if (Content.taskState == 1) {
                        Content.taskState = 3;
                        TaskManager.getInstances(mContext).pauseTaskQueue();
                    }
                    TaskManager.getInstances(mContext).navigate_Position(Content.mapName, "Origin");
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
        if (Content.server != null) {
            Content.server.broadcast(gsonUtils.putTVTime(Content.TV_TIME));
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
        if (battery > 80 && Content.taskState == 3) {
            if (completeFlag) {
                TaskManager.getInstances(mContext).resumeTaskQueue();
            }
        } else if (battery < 30) {//是否到达回冲电量
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
            TaskManager.getInstances(mContext).getPositions(Content.mapName);
            myHandler.postDelayed(this, 1000);
        }
    };

    private void setAlarmTime(String taskName, long triggerAtMillis) {
        Log.d("AlarmReceiver", "开启定时任务 ：" + triggerAtMillis);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("android.alarm.task.action");
        intent.putExtra("mapName", Content.mapName);
        intent.putExtra("taskName", taskName);
        intent.setClass(this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播
        int interval = 24 * 60 * 60 * 1000;
        mAlarmManager.setRepeating(AlarmManager.RTC, triggerAtMillis, interval, mPendingIntent);
//        am.set(AlarmManager.RTC, triggerAtMillis, sender);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onBaseEventMessage(EventBusMessage messageEvent) {
        super.onBaseEventMessage(messageEvent);
        Log.d(TAG, "messageEvent.getState() :  " + messageEvent.getState());
//上位机本身发送的消息
        if (messageEvent.getState() == 1001) {
            RobotMap robotMap = (RobotMap) messageEvent.getT();
            data = robotMap.getData();
            mapManagerAdapter.refeshList(data);
            mapRecycler.setAdapter(mapManagerAdapter);
            mapManagerAdapter.notifyDataSetChanged();
        } else if (messageEvent.getState() == 1002) {
            bytes = (byte[]) messageEvent.getT();
            Glide.with(mContext).load(bytes).into(robotMap);
        } else if (messageEvent.getState() == 1003) {
            RobotPosition robotPosition = (RobotPosition) messageEvent.getT();
            x = (float) robotPosition.getGridPosition().getX();
            y = (float) robotPosition.getGridPosition().getY();
            angle = (float) robotPosition.getAngle();
            Log.d(TAG, "地图：X===========" + robotMap.getWidth() + "Y :" + robotMap.getHeight());
            Log.d(TAG, "地图：X===" + robotPosition.getMapInfo().getGridWidth() + "Y :" + robotPosition.getMapInfo().getGridHeight());
            Log.d(TAG, "地图rotate：X===" + x + "Y :" + y);
            Log.d(TAG, "地图originX：===" + robotPosition.getMapInfo().getOriginX() + "originY :" + robotPosition.getMapInfo().getOriginY());
            Log.d(TAG, "地图图片rotate：X===" + robotMap.getWidth() + "  Y :" + robotMap.getHeight());
            Log.d(TAG, "地图angle：===" + angle + "cosy :" + Math.cos(angle) + "sinx :" + Math.sin(angle));
            Log.d(TAG, "ZDZD === " + (int) (robotMap.getWidth() / robotPosition.getMapInfo().getGridWidth() * x
                            + robotPosition.getMapInfo().getOriginX() - (Content.ROBOT_SIZE / robotPosition.getMapInfo().getResolution() * Math.cos(angle))));
            Log.d(TAG, "ZDZD ==="+ (int) ((robotMap.getHeight()) - ((robotMap.getHeight() / robotPosition.getMapInfo().getGridHeight() * y
                            + robotPosition.getMapInfo().getOriginY()) - (Content.ROBOT_SIZE / robotPosition.getMapInfo().getResolution() * Math.sin(angle)))));
            mapLin.removeView(robot_Position);
            robot_Position.setImageResource(R.drawable.ic_baseline_brightness_1_24);
            robot_Position.setPaddingRelative(
                    (int) (robotMap.getWidth() / robotPosition.getMapInfo().getGridWidth() * x
                            + robotPosition.getMapInfo().getOriginX() - (Content.ROBOT_SIZE / robotPosition.getMapInfo().getResolution() * Math.cos(angle))),
                    (int) ((robotMap.getHeight()) - ((robotMap.getHeight() / robotPosition.getMapInfo().getGridHeight() * y
                            + robotPosition.getMapInfo().getOriginY()) - (Content.ROBOT_SIZE / robotPosition.getMapInfo().getResolution() * Math.sin(angle)))),
                    0, 0);
            mapLin.addView(robot_Position);
        } else if (messageEvent.getState() == 1004) {
            byte[] bytes = (byte[]) messageEvent.getT();
            battery = bytes[23];
            robotPower.setText("电池容量：" + battery + "%");
        } else if (messageEvent.getState() == 1005) {
//            byte[] bytes = (byte[]) messageEvent.getT();
//            Glide.with(mContext).load(bytes).into(scanningMapIcon);
        } else if (messageEvent.getState() == 1006) {
            RobotTaskQueueList robotTaskQueueList = (RobotTaskQueueList) messageEvent.getT();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < robotTaskQueueList.getData().size(); i++) {
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
        } else if (messageEvent.getState() == 10000) {//callback信息的返回
            if (Content.server != null) {
                gsonUtils.setCallback((String) messageEvent.getT());
                Content.server.broadcast(gsonUtils.putCallBackMsg(Content.REQUEST_MSG));
            }
        } else if (messageEvent.getState() == 10001) {//后退
            handler1.postDelayed(runnable1, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10002) {//前进
            handler2.postDelayed(runnable2, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10003) {//左转
            handler3.postDelayed(runnable3, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10004) {//右转
            handler4.postDelayed(runnable4, 10);
            Content.time = 300;
            Content.robotState = 3;
        } else if (messageEvent.getState() == 10005) {//开始消毒检测
            spinner.setSelection((Integer) messageEvent.getT());
            toLightControlBtn.setChecked(true);
        } else if (messageEvent.getState() == 10006) {//停止消毒检测
            toLightControlBtn.setChecked(false);
        } else if (messageEvent.getState() == 10007) {//停前
            handler2.removeCallbacks(runnable2);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10008) {//停退
            handler1.removeCallbacks(runnable1);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10009) {//停左
            handler3.removeCallbacks(runnable3);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10010) {//停右
            handler4.removeCallbacks(runnable4);
            Content.robotState = 1;
            Content.time = 4000;
        } else if (messageEvent.getState() == 10011) {//地图列表
            TaskManager.getInstances(mContext).loadMapList();
        } else if (messageEvent.getState() == 10012) {//地图列表获取后发送
            RobotMap robotMap = (RobotMap) messageEvent.getT();
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putMapListMessage(Content.SENDMAPNAME, robotMap));
            }
        } else if (messageEvent.getState() == 10013) {//存储任务队列
            String messageEventT = (String) messageEvent.getT();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(messageEventT);
                String taskName = jsonObject.getString(Content.TASK_NAME);
                List<SaveTaskBean> points = new ArrayList<>();
                for (int i = 0; i < jsonObject.getJSONArray(Content.SAVETASKQUEUE).length(); i++) {
                    SaveTaskBean saveTaskBean = new SaveTaskBean();
                    JSONObject jsonObject1 = (JSONObject) jsonObject.getJSONArray(Content.SAVETASKQUEUE).get(i);
                    saveTaskBean.setPositionName(jsonObject1.getString(Content.POINT_NAME));
                    saveTaskBean.setTime(jsonObject1.getInt(Content.SPINNERTIME));
                    points.add(saveTaskBean);
                }
                TaskManager.getInstances(mContext).save_taskQueue(Content.mapName, taskName, points);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10014) {//删除任务队列
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject((String) messageEvent.getT());
                String taskName = jsonObject.getString(Content.TASK_NAME);
                TaskManager.getInstances(mContext).deleteTaskQueue(Content.mapName, taskName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10015) {//获取任务列表
            TaskManager.getInstances(mContext).getTaskQueues(Content.mapName);
        } else if (messageEvent.getState() == 10016) {//返回任务列表
            RobotTaskQueueList robotTaskQueueList = (RobotTaskQueueList) messageEvent.getT();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < robotTaskQueueList.getData().size(); i++) {
                list.add(robotTaskQueueList.getData().get(i).getName());
            }
            gsonUtils.setData(list);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.SENDTASKQUEUE));
            }
        } else if (messageEvent.getState() == 10017) {//返回地图点数据
            RobotPositions robotPositions = (RobotPositions) messageEvent.getT();
            gsonUtils.setmRobotPositions(robotPositions);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putJsonMessage(Content.SENDPOINTPOSITION));
            }
        } else if (messageEvent.getState() == 10019) {//请求地图图片
            TaskManager.getInstances(mContext).getMapPic(Content.mapName);
        } else if (messageEvent.getState() == 10020) {//返回地图图片
            byte[] bytes = (byte[]) messageEvent.getT();
            if (Content.server != null) {
                Content.server.broadcast(bytes);
            }
        } else if (messageEvent.getState() == 10021) {//添加点
            String s = (String) messageEvent.getT();
            try {
                JSONObject jsonObject = new JSONObject(s);
                PositionListBean positionListBean = new PositionListBean();
                positionListBean.setName(jsonObject.getString(Content.POINT_NAME));
                positionListBean.setGridX((int) x);
                positionListBean.setGridY((int) y);
                positionListBean.setAngle(angle);
                positionListBean.setType(2);
                positionListBean.setMapName(Content.mapName);
                TaskManager.getInstances(mContext).add_Position(positionListBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getState() == 10022) {//开始任务
            TaskManager.getInstances(mContext).startTaskQueue(Content.mapName, Content.taskName);
        } else if (messageEvent.getState() == 10023) {//停止任务
            TaskManager.getInstances(mContext).stopTaskQueue(Content.mapName);
            toLightControlBtn.setChecked(false);
        } else if (messageEvent.getState() == 10024) {//返回机器人位置
            RobotPosition robotPosition = (RobotPosition) messageEvent.getT();
            gsonUtils.setX((double) robotPosition.getGridPosition().getX());
            gsonUtils.setY((double) robotPosition.getGridPosition().getY());
            gsonUtils.setGridHeight((int) robotPosition.getMapInfo().getGridHeight());
            gsonUtils.setGridWidth((int) robotPosition.getMapInfo().getGridWidth());
            gsonUtils.setOriginX((double) robotPosition.getMapInfo().getOriginX());
            gsonUtils.setOriginY((double) robotPosition.getMapInfo().getOriginY());
            gsonUtils.setResolution((double) robotPosition.getMapInfo().getResolution());
            gsonUtils.setAngle((double) robotPosition.getAngle());
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putRobotPosition(Content.SENDGPSPOSITION));
            }
        } else if (messageEvent.getState() == 10025) {//开始扫描地图
            TaskManager.getInstances(mContext).start_scan_map((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10026) {//选定地图
            TaskManager.getInstances(mContext).getMapPic(Content.mapName);
            TaskManager.getInstances(mContext).use_map(Content.mapName);
            myHandler.removeCallbacks(runnablePosition);
            myHandler.postDelayed(runnablePosition, 1000);
        } else if (messageEvent.getState() == 10027) {//转圈初始化结果
            Log.d("zdzd ", "初始化结果： " + (String) messageEvent.getT() + ",     isDevelop :" + isDevelop);
            if ("successed".equals((String) messageEvent.getT()) && isDevelop) {
                handlerInitialize.postDelayed(runnableInitialize, 1000);
            }
            if (Content.server != null) {
                Content.server.broadcast((String) messageEvent.getT());
            }
        } else if (messageEvent.getState() == 10028) {//请求地图点列表
            TaskManager.getInstances(mContext).getPosition(Content.mapName);
        } else if (messageEvent.getState() == 10029) {//取消扫描地图并保存
            TaskManager.getInstances(mContext).stopScanMap();
            isDevelop = false;
        } else if (messageEvent.getState() == 10030) {//拓展地图
            isDevelop = true;
            NavigationService.initialize(Content.mapName);
        } else if (messageEvent.getState() == 10031) {//删除地图
            TaskManager.getInstances(mContext).deleteMap((String) messageEvent.getT());
        } else if (messageEvent.getState() == 10032) {//删除点
            TaskManager.getInstances(mContext).deletePosition(Content.mapName, (String) messageEvent.getT());
        } else if (messageEvent.getState() == 10033) {//电池电量
            byte[] bytes = (byte[]) messageEvent.getT();
            battery = bytes[23];
            robotPower.setText("电池容量：" + battery + "%");
            if (Content.server != null) {
                gsonUtils.setBattery(battery + "%");
                Content.server.broadcast(gsonUtils.putBattery(Content.BATTERY_DATA));
            }
        } else if (messageEvent.getState() == 10034) {
            Log.d("zdzd :", "是否完成初始化" + (String) messageEvent.getT());
            if ("true".equals((String) messageEvent.getT())) {
                handlerInitialize.removeCallbacks(runnableInitialize);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.finish_initialize)));
                TaskManager.getInstances(mContext).start_develop_map(Content.mapName);
            } else {
                handler1.postDelayed(runnableInitialize, 1000);
                EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.is_initialize)));
            }
        } else if (messageEvent.getState() == 10035) {
            Log.d("zdzd :", "是否完成初始化error: " + (String) messageEvent.getT());
            EventBus.getDefault().post(new EventBusMessage(10000, mContext.getResources().getString(R.string.fail_initialize)) + (String) messageEvent.getT());
            handlerInitialize.removeCallbacks(runnableInitialize);
        } else if (messageEvent.getState() == 10036) {//取消扫描不保存地图
            TaskManager.getInstances(mContext).cancleScanMap();
        }


//test request
        else if (messageEvent.getState() == 20001) {
            checkLztekLamp.setUvcMode();
            checkLztekLamp.startUvc1Lamp();
            checkLztekLamp.startUvc2Lamp();
            checkLztekLamp.startUvc3Lamp();
        } else if (messageEvent.getState() == 20002) {
            checkLztekLamp.stopUvc1Lamp();
            checkLztekLamp.stopUvc2Lamp();
            checkLztekLamp.stopUvc3Lamp();
        } else if (messageEvent.getState() == 20003) {
            checkLztekLamp.startLedLamp();
        } else if (messageEvent.getState() == 20004) {
            checkLztekLamp.stopLedLamp();
        } else if (messageEvent.getState() == 20005) {
            String s = checkLztekLamp.testGpioSensorState();
            Log.d("zdzd : ", "sensor返回值： " + s);
            gsonUtils.setTestCallBack(s);
            if (Content.server != null) {
                Content.server.broadcast(gsonUtils.putTestSensorCallBack(Content.TEST_SENSOR_CALLBACK));
            }
        } else if (messageEvent.getState() == 20006) {
            uvcWarning.startWarning();
        } else if (messageEvent.getState() == 20007) {
            uvcWarning.stopWarning();
        }
    }


}