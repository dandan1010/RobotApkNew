package com.retron.robotAgent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.retron.robotAgent.utils.EventBusMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseService extends Service {

    private Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsg(EventBusMessage messageEvent) {
        onBaseEventMessage(messageEvent);
    }


    protected void onBaseEventMessage(EventBusMessage messageEvent) {

    }

}
