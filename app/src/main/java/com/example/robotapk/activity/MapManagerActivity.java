package com.example.robotapk.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.example.robotapk.R;
import com.example.robotapk.adapter.MapManagerAdapter;
import com.example.robotapk.utils.EventBusMessage;
import com.example.robotapk.service.NavigationService;
import com.example.robotapk.task.TaskManager;
import com.example.robotapk.uvclamp.CheckLztekLamp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapManagerActivity extends BaseActivity implements MapManagerAdapter.OnItemClickListener {

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
     * */
    @Override
    public void OnItemClickListener(View view, int position) {
        mapName = data.get(position).getName();
        TaskManager.getInstances().getMapPic(mapName);
    }

    @Override
    public void OnItemLongClickListener(View view, int position) {
        mapName = data.get(position).getName();
        data.remove(position);
        mapManagerAdapter.refeshList(data);
        mapManagerAdapter.notifyDataSetChanged();
        TaskManager.getInstances().deleteMap(mapName);
    }

    @OnClick({R.id.scanning_map, R.id.map_icon, R.id.cancel_scanning_map, R.id.press_ok})
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
                                TaskManager.getInstances().start_scan_map(editText.getText().toString());
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
                TaskManager.getInstances().cancelScanMap();
                break;
            case R.id.press_ok:
                Intent intent = new Intent(this, RobotDetailActivity.class);
                intent.putExtra("mapName", mapName);
                startActivity(intent);
                break;
            default:
                break;
        }
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
            TaskManager.getInstances().getMapPic(mapName);
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