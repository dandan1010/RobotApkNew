package com.example.robot.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ServerConnoct {

    private static ServerConnoct serverConnoct;
    private Context mContext;

    private ServerConnoct(Context context) {
        this.mContext = context;
    }

    /**
     * 获取唯一的instance
     */
    public static ServerConnoct getInstance(Context context) {
        if (serverConnoct == null) {
            synchronized (ServerConnoct.class) {
                if (serverConnoct == null) {
                    serverConnoct = new ServerConnoct(context);
                }
            }
        }
        return serverConnoct;
    }

}
