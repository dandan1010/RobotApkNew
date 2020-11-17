package com.example.robot.utils;

public final class Content {

    /**
     * robotState：
     * 0:关机
     * 1:静止
     * 2:
     * 3:移动
     * 4:充电
     * 5:uvc警告
     * 6:低电量
     */
    public static int robotState = 0;
    public static int time = 0;//灯带时间间隔
    /**
     * 0：任务结束
     * 1：执行任务，恢复任务
     * 2: 暂停任务
     * 3:没电暂停
    */
    public static int taskState = 0;//机器人执行任务的状态
    public static int taskIndex = 0;

    public static final String MAP_NAME = "map_Name";//地图名字
    public static final String TASK_NAME = "task_Name";//任务名字
    //type id
    public static final String STARTUP = "startUp";//前进
    public static final String STOPUP = "stopUp";//停止后退
    public static final String STARTDOWN = "startDown";//后退
    public static final String STOPDOWN = "stopDown";//停止后退
    public static final String STARTLEFT = "startLeft";//向左
    public static final String STOPLEFT = "stopLeft";//停止左转
    public static final String STARTRIGHT = "startRight";//向右
    public static final String STOPRIGHT = "stopRight";//停止右转
    public static final String STARTLIGHT = "startLight";//开灯
    public static final String STOPLIGHT = "stopLight";//关灯

    public static final String GETMAPLIST = "getMapList";//请求地图列表
    public static final String SENDMAPNAME = "sendMapName";//返回地图列表名称
    public static final String GETMAPPIC = "getMapPic";//请求地图图片
    public static final String SENDMAPICON = "sendMapIcon";//返回地图图片
    public static final String GETPOSITION = "getPosition";//请求机器人位置
    public static final String SENDGPSPOSITION = "sendGpsPosition";//返回机器人位置
    public static final String GETINITIALIZE = "getInitialize";//请求机器人转圈初始化
    public static final String SENDINITIALIZE = "sendInitialize";//返回机器人转圈初始化
    public static final String GETTASKQUEUE = "getTaskQueue";//请求机器人任务列表
    public static final String SENDTASKQUEUE = "sendTaskQueue";//返回任务列表
    public static final String GETPOINTPOSITION = "getPointPosition";//返回点数据
    public static final String SENDPOINTPOSITION = "sendPointPosition";//返回点数据
    public static final String ADD_POSITION = "add_position";//添加点
    public static final String SPINNERTIME = "spinnerTime";//请求的时间
    public static final String TV_TIME = "tv_time";//返回时间
    public static final String SAVETASKQUEUE = "saveTaskQueue";//存储任务
    public static final String DELETETASKQUEUE = "deleteTaskQueue";//删除任务队列
    public static final String STARTTASKQUEUE = "startTaskQueue";//开始任务队列
    public static final String STOPTASKQUEUE = "stopTaskQueue";//停止任务队列

    public static final String DATATIME = "dataTime";//地图名称的列表array的key


    //task key
    public static final String TASK_X = "x";//x坐标
    public static final String TASK_Y = "y";//y坐标
    public static final String TASK_DISINFECT_TIME = "disinfect_Time";//执行任务的点的时间
    public static final String TASK_ANGLE = "angle";//机器人角度
    public static final String POINT_NAME = "point_Name";//点的名字

}
