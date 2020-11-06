package com.example.robotapk.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.example.robotapk.R;
import com.example.robotapk.adapter.MapManagerAdapter;
import com.example.robotapk.service.NavigationService;
import com.example.robotapk.task.TaskManager;
import com.example.robotapk.utils.Content;
import com.example.robotapk.utils.EventBusMessage;
import com.example.robotapk.uvclamp.CheckLztekLamp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapManagerActivity extends BaseActivity implements MapManagerAdapter.OnItemClickListener, View.OnTouchListener {

    private static final String TAG = "MapManagerActivity";
    @BindView(R.id.map_recycler)
    RecyclerView mapRecycler;
    @BindView(R.id.map_icon)
    ImageView mapIcon;
    @BindView(R.id.scanning_map)
    Button scanningMap;
    @BindView(R.id.scanning_map_icon)
    ImageView scanningMapIcon;
    @BindView(R.id.press_ok)
    Button pressOk;
    @BindView(R.id.forward_img)
    ImageView forwardImg;
    @BindView(R.id.to_left_img)
    ImageView toLeftImg;
    @BindView(R.id.to_right_img)
    ImageView toRightImg;
    @BindView(R.id.fallback_img)
    ImageView fallbackImg;

    private MapManagerAdapter mapManagerAdapter;
    private Context mContext;
    private List<RobotMap.DataBean> data;
    private String mapName;
    private NavigationService navigationService;
    private Intent intentService;
    private CheckLztekLamp checkLztekLamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);
        ButterKnife.bind(this);
        mContext = MapManagerActivity.this;
        initView();
        forwardImg.setOnTouchListener(this);
        toLeftImg.setOnTouchListener(this);
        toRightImg.setOnTouchListener(this);
        fallbackImg.setOnTouchListener(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (intentService != null) {
            stopService(intentService);
        }
        checkLztekLamp.stopUvc1Lamp();
        checkLztekLamp.stopUvc2Lamp();
        checkLztekLamp.stopUvc3Lamp();
        checkLztekLamp.stopLedLamp();

    }

    private void initView() {
        navigationService = new NavigationService();
        intentService = new Intent(this, NavigationService.class);
        startService(intentService);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mapRecycler.setLayoutManager(linearLayoutManager);
        mapRecycler.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mapManagerAdapter = new MapManagerAdapter(this, R.layout.item_recycler);
        mapManagerAdapter.setOnItemClickListener(this);

        checkLztekLamp = new CheckLztekLamp(mContext);
    }


    /**
     * 列表的点击事件
     */
    @Override
    public void OnItemClickListener(View view, int position) {
        mapName = data.get(position).getName();
        TaskManager.getInstances(mContext).getMapPic(mapName);
    }

    @Override
    public void OnItemLongClickListener(View view, int position) {
        mapName = data.get(position).getName();
        data.remove(position);
        mapManagerAdapter.refeshList(data);
        mapManagerAdapter.notifyDataSetChanged();
        TaskManager.getInstances(mContext).deleteMap(mapName);
    }

    @OnClick({R.id.scanning_map, R.id.map_icon, R.id.cancel_scanning_map, R.id.press_ok, R.id.develop_map})
    public void onClick(View view) {
        switch (view.getId()) {
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
                TaskManager.getInstances(mContext).cancelScanMap();
                break;
            case R.id.press_ok:
                Intent intent = new Intent(this, RobotDetailActivity.class);
                intent.putExtra("mapName", mapName);
                startActivity(intent);
                break;
            case R.id.develop_map:
                TaskManager.getInstances(mContext).start_develop_map(mapName);
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

    @Override
    protected void onBaseEventMessage(EventBusMessage messageEvent) {
        super.onBaseEventMessage(messageEvent);
        Log.d(TAG, "messageEvent : " + messageEvent.getState());
        if (messageEvent.getState() == 1001) {
            RobotMap robotMap = (RobotMap) messageEvent.getT();
            data = robotMap.getData();
            mapManagerAdapter.refeshList(data);
            mapRecycler.setAdapter(mapManagerAdapter);
            mapName = data.get(0).getName();
            TaskManager.getInstances(mContext).getMapPic(mapName);
            mapManagerAdapter.notifyDataSetChanged();
        } else if (messageEvent.getState() == 1002) {
            byte[] bytes = (byte[]) messageEvent.getT();
            Glide.with(mContext).load(bytes).into(mapIcon);
        } else if (messageEvent.getState() == 1005) {
            byte[] bytes = (byte[]) messageEvent.getT();
            Glide.with(mContext).load(bytes).into(scanningMapIcon);

        }
    }
}