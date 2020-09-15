package com.dcm360.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dcm360.controller.gs.GSRobotController;
import com.dcm360.controller.robot_interface.bean.Status;
import com.dcm360.controller.robot_interface.status.RobotStatus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ROBOROT_INF = "http://192.168.216.187:7002"; //机器人底盘
    private GSRobotController gsRobotController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_down).setOnClickListener(this);
        findViewById(R.id.tv_left).setOnClickListener(this);
        findViewById(R.id.tv_right).setOnClickListener(this);
        findViewById(R.id.tv_stop).setOnClickListener(this);
        findViewById(R.id.tv_up).setOnClickListener(this);

        gsRobotController = new GSRobotController();
        gsRobotController.connect_robot(ROBOROT_INF);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        toast(id);
        switch (id) {
            case R.id.tv_down:
                gsRobotController.move_to(-1, 0.1f, new RobotStatus<Status>() {
                    @Override
                    public void success(Status status) {

                    }

                    @Override
                    public void error(Throwable error) {

                    }
                });
                break;
            case R.id.tv_up:
                gsRobotController.move_to(1, 0.1f, new RobotStatus<Status>() {
                    @Override
                    public void success(Status status) {

                    }

                    @Override
                    public void error(Throwable error) {

                    }
                });
                break;
            case R.id.tv_stop:
                gsRobotController.stop_move_to(new RobotStatus<Status>() {
                    @Override
                    public void success(Status status) {

                    }

                    @Override
                    public void error(Throwable error) {

                    }
                });
                gsRobotController.stop_rotate(new RobotStatus<Status>() {
                    @Override
                    public void success(Status status) {

                    }

                    @Override
                    public void error(Throwable error) {

                    }
                });
                break;
            case R.id.tv_left:
                gsRobotController.rotate(90, 0.2f, new RobotStatus<Status>() {
                    @Override
                    public void success(Status status) {

                    }

                    @Override
                    public void error(Throwable error) {

                    }
                });
                break;
            case R.id.tv_right:
                gsRobotController.rotate(-90, 0.2f, new RobotStatus<Status>() {
                    @Override
                    public void success(Status status) {

                    }

                    @Override
                    public void error(Throwable error) {

                    }
                });
                break;
        }
    }

    private void toast(int id) {
        String msg;
        switch (id) {
            case R.id.tv_left:
                msg = "向左";
                break;
            case R.id.tv_right:
                msg = "向右";
                break;
            case R.id.tv_up:
                msg = "前进";
                break;
            case R.id.tv_down:
                msg = "后退";
                break;
            case R.id.tv_stop:
                msg = "停止";
                break;
            default:
                return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
