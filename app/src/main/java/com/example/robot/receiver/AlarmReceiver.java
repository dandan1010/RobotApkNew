package com.example.robot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.robot.R;
import com.example.robot.service.NavigationService;
import com.example.robot.service.SocketServices;
import com.example.robot.sqlite.SqLiteOpenHelperUtils;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.AlarmUtils;
import com.example.robot.utils.AssestFile;
import com.example.robot.content.Content;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.GsonUtils;
import com.example.robot.uvclamp.CheckLztekLamp;

import org.greenrobot.eventbus.EventBus;

public class AlarmReceiver extends BroadcastReceiver {
    private int week = 0;

    public void onReceive(Context context, Intent intent) {
        if (Content.AlarmAction.equals(intent.getAction())) {
            week = intent.getIntExtra("week", -1);
//            handler.sendEmptyMessage(1000);
            Message message = new Message();
            message.arg1 = week;
            message.what = 11;
            SocketServices.myHandler.sendMessage(message);
        }
    }
}
