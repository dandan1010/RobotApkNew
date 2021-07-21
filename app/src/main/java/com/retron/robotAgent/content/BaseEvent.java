package com.retron.robotAgent.content;

public class BaseEvent {
    public static final int PING = 30000;
    public static final int UPDATE = 30002;
    public static final int UPDATE_FILE_LENGTH = 30003;
    public static final int ALARM_CODE = 30004;
    public static final int DELETE_FILE_ALARM_CODE = 30005;

    public static final int START_UVC = 1007;//开始消毒
    public static final int STOP_UVC = 1009;//开始消毒
    //type id
    public static final int REQUEST_MSG = 10000;//信息返回
    public static final int DELETE_MAP_FAIL = 10001;//删除地图失败
    public static final int STARTMOVE = 10002;//机器人移动
    public static final int STARTLIGHT = 10005;//开灯
    public static final int STOPLIGHT = 10006;//关灯
    public static final int GETMAPLIST = 10011;//请求地图列表
    public static final int SENDMAPNAME = 10012;//返回地图列表名称
    public static final int SAVETASKQUEUE = 10013;//存储任务
    public static final int DELETETASKQUEUE = 10014;//删除任务队列
    public static final int GETTASKQUEUE = 10015;//请求机器人任务列表
    public static final int SENDTASKQUEUE = 10016;//返回任务列表
    public static final int SENDPOINTPOSITION = 10017;//返回点数据
    public static final int ADDPOINTPOSITION = 10018;//返回点数据
    public static final int GETMAPPIC = 10019;//请求地图图片
    public static final int SENDMAPICON = 10020;//返回地图图片
    public static final int ADD_POSITION = 10021;//添加点
    public static final int STARTTASKQUEUE = 10022;//开始任务队列
    public static final int STOPTASKQUEUE = 10023;//停止任务队列
    public static final int SENDGPSPOSITION = 10024;//返回机器人位置
    public static final int START_SCAN_MAP = 10025;//开始扫描地图
    public static final int USE_MAP = 10026;//选定地图
    public static final int INITIALIZE_RESULE = 10027;//初始化结果
    public static final int GETPOINTPOSITION = 10028;//请求点数据
    public static final int CANCEL_SCAN_MAP = 10029;//取消扫描地图并且保存
    public static final int DEVELOP_MAP = 10030;//扩展扫描地图
    public static final int DELETE_MAP = 10031;//删除地图
    public static final int DELETE_POSITION = 10032;//删除点
    public static final int BATTERY_DATA = 10033;//电池电量
    public static final int IS_INITIALIZE_FINISHED= 10034;//初始化
    public static final int INITIALIZE_FAIL= 10035;//初始化失败
    public static final int CANCEL_SCAN_MAP_NO = 10036;//取消扫描不保存
    public static final int ROBOT_HEALTHY = 10037;//机器人健康
    public static final int ROBOT_TASK_STATE = 10038;//机器人任务状态
    public static final int ROBOT_TASK_HISTORY = 10039;//机器人历史任务
    public static final int SEND_MQTT_VIRTUAL = 10041;//发送mqtt虚拟墙
    public static final int SEND_VIRTUAL = 10042;//返回虚拟墙数据
    public static final int UPDATA_VIRTUAL = 10043;//更新虚拟墙
    public static final int TASK_ALARM = 10044;//任务
    public static final int RENAME_MAP = 10045;//重命名地图
    public static final int SET_SETTING_MODE = 10046;//设置信息
    public static final int RENAME_POSITION = 10047;//重命名点
    public static final int GET_SETTING_MODE = 10048;//设置信息
    public static final int DEVICES_STATUS = 10049;//设备信息
    public static final int ADD_POWER_POINT = 10050;//添加充电点
    public static final int EDITTASKQUEUE = 10051;//编辑任务队列
    public static final int SYSTEM_DATE = 10052;//系统时间
    public static final int GET_TASK_STATE = 10053;//当前地图的任务
    public static final int GET_ALL_TASK_STATE = 10054;//全部地图的任务
    public static final int GET_LED_LEVEL = 10055;//获取led亮度
    public static final int GET_LOW_BATTERY = 10056;//获取低电量回充
    public static final int RESET_ROBOT = 10057;//重置设备
    public static final int GET_ULTRASONIC = 10058;//声呐设备
    public static final int SEND_ULTRASONIC = 10059;//声呐设备
    public static final int VERSIONCODE = 10060;//系统版本
    public static final int GET_WORKING_MODE = 10061;//工作模式
    public static final int ROBOTVERSIONCODE = 10062;//下位机版本
    public static final int CMDVEL= 10063;//实时速度
    public static final int GET_CHARGING_MODE = 10064;//有无充电桩模式
    public static final int dbTotalCount = 10065;//任务统计个数
    public static final int dbCurrentCount = 10066;//当月任务统计个数
    public static final int Robot_Error = 10067;//重启导航
    public static final int CANCELTASKQUEUE = 10068;//取消当前任务
    public static final int INITIALIZE = 10069;
    public static final int STOPALLTASKQUEUE = 10070;//取消所有任务
    public static final int GET_BAG = 10071;//下载bag文件
    public static final int DOWNLOAD_MAP = 10072;//下载地图
    public static final int UPLOAD_MAP = 10073;//上传地图
    public static final int ROBOT_TASK_ERROR = 10074;
    public static final int UPLOADMAPSYN = 10075;//上传地图成功
    public static final int MQTT_ADD_POINT = 10076;//添加点
    public static final int MQTT_UPDATA_VIRTUAL = 10077;//更新虚拟墙
    public static final int MQTT_RENAME_MAP = 10078;//重命名地图


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
    public static final int TEST_LIGHTSTART = 20011;
    public static final int TEST_LIGHTSTOP = 20012;
    public static final int TEST_SENSOR = 20013;
    public static final int TEST_WARNINGSTART = 20014;
    public static final int TEST_WARNINGSTOP = 20015;
    public static final int DOWNLOAD_LOG = 20016;//下载log
    public static final int COPY_FILE = 20017;//COPY 数据库
    public static final int GO_TO_LOG_URL = 20018;//下载log
    public static final int GET_LOG_LIST = 20019;//下载log列表
    public static final int MQTT_DOWNLOAD_LOG = 20020;//下载log

    
}
