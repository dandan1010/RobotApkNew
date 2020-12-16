package com.example.robot.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.robot.service.SimpleServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerConnoct {

    private static ServerConnoct serverConnoct;
    private Thread thread;

    private ServerConnoct() {
    }

    /**
     * 获取唯一的instance
     */
    public static ServerConnoct getInstance() {
        if (serverConnoct == null) {
            synchronized (ServerConnoct.class) {
                if (serverConnoct == null) {
                    serverConnoct = new ServerConnoct();
                }
            }
        }
        return serverConnoct;
    }

    public void connect(Context mContext) {
        Log.d("zdzd --- ", "serverConnect connect");
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Log.d("zdzd --- ", "thread run");
                Content.server = SimpleServer.getInstance(mContext);
                Content.server.run();
            }
        };
        thread.start();
    }

}
