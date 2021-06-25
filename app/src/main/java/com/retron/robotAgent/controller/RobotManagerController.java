package com.retron.robotAgent.controller;


import com.dcm360.controller.gs.GSRobotController;

public class RobotManagerController {
    private GSRobotController robotController;

    private volatile static RobotManagerController INSTANCE;

    private RobotManagerController() {
        if (robotController == null) {
            synchronized (GSRobotController.class) {
                if (robotController == null)
                    robotController = new GSRobotController();
            }
        }
    }

    public static RobotManagerController getInstance() {
        if (INSTANCE == null) {
            synchronized (RobotManagerController.class) {
                if (INSTANCE == null)
                    INSTANCE = new RobotManagerController();
            }
        }
        return INSTANCE;
    }

    public GSRobotController getRobotController() {
        return this.robotController;
    }

//    public boolean isConnect() {
//        return MrApp.configModel != null && MrApp.isStartNavigationService && !NavigationService.isLocationError;
//    }
}