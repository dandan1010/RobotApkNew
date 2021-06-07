package com.retron.robot.utils;

import android.content.Context;
import android.util.Log;

import com.retron.robot.content.Content;
import com.retron.robot.service.SimpleServer;

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
        Log.d("ServerConnoct", "serverConnect connect");
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Log.d("ServerConnoct", "thread run");
                Content.server = SimpleServer.getInstance(mContext);
            }
        };
        thread.start();
    }

}
