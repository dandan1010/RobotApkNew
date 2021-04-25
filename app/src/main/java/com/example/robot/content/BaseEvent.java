package com.example.robot.content;

import com.example.robot.service.SimpleServer;

public class BaseEvent {
    public static boolean have_charging_mode = true;
    public static final int ALARM_CODE = 30004;
    public static final int DELETE_FILE_ALARM_CODE = 30005;
    public static final int GET_WORKING_MODE = 10061;//工作模式
    public static final int GET_CHARGING_MODE = 10064;//有无充电桩模式

    public static final int UPDATE = 30002;
    public static final int SYSTEM_DATE = 10052;//系统时间
    public static final int VERSIONCODE = 10060;//系统版本
    public static final int ROBOTVERSIONCODE = 10062;//下位机版本

    public static final int PING = 30000;

    //type id
    public static final int REQUEST_MSG = 10000;//信息返回
    public static final int STARTUP = 10002;//前进
    public static final int STOPUP = 10007;//停止后退
    public static final int STARTDOWN = 10001;//后退
    public static final int STOPDOWN = 10008;//停止后退
    public static final int STARTLEFT = 10003;//向左
    public static final int STOPLEFT = 10009;//停止左转
    public static final int STARTRIGHT = 10004;//向右
    public static final int STOPRIGHT = 10010;//停止右转
    public static final int STARTLIGHT = 10005;//开灯
    public static final int STOPLIGHT = 10006;//关灯

    public static final int GETMAPLIST = 10011;//请求地图列表
    public static final int SENDMAPNAME = 10012;//返回地图列表名称
    public static final int GETMAPPIC = 10019;//请求地图图片
    public static final int SENDMAPICON = 10020;//返回地图图片
    public static final int SENDGPSPOSITION = 10024;//返回机器人位置
    public static final int INITIALIZE_RESULE = 10027;//初始化结果
    public static final int GETTASKQUEUE = 10015;//请求机器人任务列表
    public static final int SENDTASKQUEUE = 10016;//返回任务列表
    public static final int GETPOINTPOSITION = 10028;//请求点数据
    public static final int SENDPOINTPOSITION = 10017;//返回点数据
    public static final int ADD_POSITION = 10021;//添加点
    public static final int DELETE_POSITION = 10032;//删除点
    public static final int RENAME_POSITION = 10047;//重命名点
    public static final int SAVETASKQUEUE = 10013;//存储任务
    public static final int DELETETASKQUEUE = 10014;//删除任务队列
    public static final int STARTTASKQUEUE = 10022;//开始任务队列
    public static final int STOPTASKQUEUE = 10023;//停止任务队列
    public static final int EDITTASKQUEUE = 10051;//编辑任务队列
    public static final int GET_TASK_STATE = 10053;//是否有正在执行的任务


    public static final int START_SCAN_MAP = 10025;//开始扫描地图
    public static final int CANCEL_SCAN_MAP = 10029;//取消扫描地图并且保存
    public static final int CANCEL_SCAN_MAP_NO = 10036;//取消扫描不保存
    public static final int DEVELOP_MAP = 10030;//扩展扫描地图
    public static final int DELETE_MAP = 10031;//删除地图
    public static final int USE_MAP = 10026;//选定地图
    public static final int RENAME_MAP = 10045;//重命名地图

    public static final int BATTERY_DATA = 10033;//电池电量
    public static final int GET_LOW_BATTERY = 10056;//获取低电量回充
    public static final int ADD_POWER_POINT = 10050;//添加充电点
    public static int battery = 30;//低电量回充
    public static int maxBattery = 70;//最高电量
    public static int fullBattery = 99;//充满电
    public static boolean isCharging = false;//充电窗台
    public static int isLimiting_flag = 0;//已经限流
    public static int limiting_flag = 0;//已经限流
    public static int limitint_init_flag = 0;//初始化限流

    /**
     *
     */
    public static int chargingState = 0;//充电状态
    public static int chargerVoltage = 0;//充满电
    public static int pir_timeCount = 20;//sensor
    public static final int GET_SPEED_LEVEL = 10048;//导航速度

    public static final int SET_PLAYPATHSPEEDLEVEL = 10046;//设置导航速度
    public static final int GET_LED_LEVEL = 10055;//获取led亮度

    public static final int RESET_ROBOT = 10057;//重置设备
    public static boolean is_reset_robot = false;//重置设备
    public static final int DEVICES_STATUS = 10049;//设备信息
    public static boolean EMERGENCY = false;//急停
    public static boolean IS_STOP_TASK = false;//停止任务
    public static double speed = 0;//停止任务
    public static int robotSpeed = 2;//机器人速度

    public static final int ROBOT_HEALTHY = 10037;//机器人健康
    public static final int ROBOT_TASK_STATE = 10038;//机器人任务状态
    public static final int ROBOT_TASK_HISTORY = 10039;//机器人历史任务

    public static final int GET_VIRTUAL = 10041;//获取虚拟墙数据
    public static final int UPDATA_VIRTUAL = 10043;//更新虚拟墙
    public static final int SEND_VIRTUAL = 10042;//返回虚拟墙数据

    public static boolean completeFlag = false;
    public static final int TASK_ALARM = 10044;//任务

    public static final int dbTotalCount = 10065;//任务统计个数

    public static final int dbCurrentCount = 10066;//当月任务统计个数
    public static final int Robot_Error = 10067;//重启导航

    //test
    public static final int TEST_UVCSTART_1 = 20001;
    public static final int TEST_UVCSTART_2 = 20002;
    public static final int TEST_UVCSTART_3 = 20003;
    public static final int TEST_UVCSTART_4 = 20004;
    public static final int TEST_UVCSTOP_1 = 20005;
    public static final int TEST_UVCSTOP_2 = 20006;
    public static final int TEST_UVCSTOP_3 = 20007;
    public static final int TEST_UVCSTOP_4 = 20008;
    public static final int TEST_UVCSTART_ALL = 20009;
    public static final int TEST_UVCSTOP_ALL = 20010;
    public static final int TEST_SENSOR = 20013;
    public static final int TEST_WARNINGSTART = 20014;
    public static final int TEST_WARNINGSTOP = 20015;
    public static final int TEST_LIGHTSTART = 20011;
    public static final int TEST_LIGHTSTOP = 20012;


    public static final int GET_ULTRASONIC = 10058;//声呐设备
    public static final int SEND_ULTRASONIC = 10059;//声呐设备
    
}
