package com.dcm360.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcm360.com.example.robotapk.controller.R;
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
        if (id == R.id.tv_down) {
            gsRobotController.move_to(-1, 0.1f, new RobotStatus<Status>() {
                @Override
                public void success(Status status) {

                }

                @Override
                public void error(Throwable error) {

                }
            });
        } else if (id == R.id.tv_up) {
            gsRobotController.move_to(1, 0.1f, new RobotStatus<Status>() {
                @Override
                public void success(Status status) {

                }

                @Override
                public void error(Throwable error) {

                }
            });
        } else if (id == R.id.tv_stop) {
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
        } else if (id == R.id.tv_left) {
            gsRobotController.rotate(90, 0.2f, new RobotStatus<Status>() {
                @Override
                public void success(Status status) {

                }

                @Override
                public void error(Throwable error) {

                }
            });
        } else if (id == R.id.tv_right) {
            gsRobotController.rotate(-90, 0.2f, new RobotStatus<Status>() {
                @Override
                public void success(Status status) {

                }

                @Override
                public void error(Throwable error) {

                }
            });
        }
    }

    private void toast(int id) {
        String msg;
        if (id == R.id.tv_left) {
            msg = "向左";
        } else if (id == R.id.tv_right) {
            msg = "向右";
        } else if (id == R.id.tv_up) {
            msg = "前进";
        } else if (id == R.id.tv_down) {
            msg = "后退";
        } else if (id == R.id.tv_stop) {
            msg = "停止";
        } else {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
