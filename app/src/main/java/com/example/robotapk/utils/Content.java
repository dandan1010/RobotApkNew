package com.example.robotapk.utils;

public final class Content {

    /**
     * robotState：
     * 0:关机
     * 1:开机连接下位机成功
     * 2:静止
     * 3:移动
     * 4:充电
     * 5:uvc警告
     * 6:低电量
     */
    public static int robotState = 0;
    public static int time = 0;//灯带时间间隔

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
    public static final String TV_TIME = "tv_time";//时间

    public static final String GETMAPLIST = "getMapList";//请求地图列表
    public static final String GETMAPNAME = "getMapName";//返回地图列表名称
    public static final String GETMAPPIC = "getMapPic";//请求地图图片
    public static final String GETMAPICON = "getMapIcon";//返回地图图片
    public static final String GETPOSITION = "getPosition";//请求机器人位置
    public static final String SENDPOSITION = "sendPosition";//返回机器人位置
    public static final String SENDINITIALIZE = "sendPosition";//返回机器人转圈初始化

    //json key
    public static final String DATATIME = "dataTime";//地图名称的列表array的key
    public static final String SPINNERTIME = "spinnerTime";//text显示的倒计时
    public static final String SaveTASKQUEUE = "saveTaskQueue";//存储，开始任务队列
    public static final String DELETETASKQUEUE = "deleteTaskQueue";//删除任务队列
    public static final String GETTASKQUEUE = "getTaskQueue";//获取任务列表



}
