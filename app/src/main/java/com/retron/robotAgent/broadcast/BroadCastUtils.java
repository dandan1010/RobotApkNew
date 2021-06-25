package com.retron.robotAgent.broadcast;

import android.content.Context;
import android.content.Intent;

public class BroadCastUtils {

    private static BroadCastUtils broadCastUtils;
    private static Context mContext;

    public static BroadCastUtils getInstance(Context context) {
        mContext = context;
        if (broadCastUtils == null) {
            synchronized (BroadCastUtils.class) {
                if (broadCastUtils == null) {
                    broadCastUtils = new BroadCastUtils();
                }
            }
        }
        return broadCastUtils;
    }

    public void sendFTPBroadcast(){
        Intent intent = new Intent("com.android.robot.ftpserver.start");
        intent.setPackage("net.micode.fileexplorer");
        mContext.sendBroadcast(intent);
    }

    public void sendUpdateBroadcast(){
        Intent intent = new Intent("com.android.robot.update");
        intent.setPackage("com.retron.wireLessApk");
        mContext.sendBroadcast(intent);
    }

}
