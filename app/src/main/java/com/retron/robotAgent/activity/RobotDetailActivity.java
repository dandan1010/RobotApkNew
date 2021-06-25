package com.retron.robotAgent.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.retron.robotAgent.R;
import com.retron.robotAgent.service.SocketServices;

public class RobotDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        Intent intentServer = new Intent(this, SocketServices.class);
        startService(intentServer);
    }
}