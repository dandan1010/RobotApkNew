package com.uslam.factory;

import android.content.Context;
import android.content.Intent;

import com.dcm360.controller.gs.GSRobotController;
import com.retron.robotAgent.controller.RobotManagerController;
import com.retron.robotAgent.service.NavigationService;
import com.retron.robotAgent.utils.SharedPrefUtil;

public class Factory {

    public static WorkFactory factory;
    public static NavigationService navigationService;
    private static Context mContext;
    private static Intent intentService;

    public static WorkFactory getInstance(Context context, String ipAddress) {
        mContext = context;
        if (factory == null) {
            synchronized (Factory.class) {
                if (factory == null)
                    factory = createFactory(context, ipAddress);
            }
        }
        return factory;
    }

    private static WorkFactory createFactory(Context context, String ipAddress){
        switch (ipAddress) {
            case "A":
                factory = new GxFactory();
                navigationService = new NavigationService();
                intentService = new Intent(context, NavigationService.class);
                context.startService(intentService);
                break;
            case "B":
                factory = new UsLamFactory("url");
                break;
            default:
                break;
        }
        return factory;
    }

    public static void stopServer(){
        mContext.stopService(intentService);
    }
}
