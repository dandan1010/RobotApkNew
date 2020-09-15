package com.dcm360.controller.robot_interface.bean;

public class StatusCode {
    public static String code_000 = "空闲中";
    public static String code_100 = "暂停状态";
    public static String code_300 = "路径无效";
    public static String code_301 = "导航去路径开始点";
    public static String code_302 = "跟线中";
    public static String code_303 = "避障等待中";
    public static String code_304 = "避障中";
    public static String code_305 = "路径终点到达不了";
    public static String code_306 = "跑路径结束";
    public static String code_401 = "定位异常";
    public static String code_402 = "目标点不安全";
    public static String code_403 = "前方有障碍物";
    public static String code_404 = "目标点不可达";
    public static String code_405 = "导航行走中";
    public static String code_406 = "规划路径中";
    public static String code_407 = "到达目标点";
    public static String code_408 = "未到达目标点";
    public static String code_409 = "寻找充电桩";
    public static String code_410 = "移动到充电桩前";
    public static String code_411 = "后退对接充电桩";
    public static String code_600 = "正在转圈";
    public static String code_601 = "不能转圈";
    public static String code_602 = "转圈结束";
    public static String code_603 = "转圈失败";
    public static String code_604 = "下桩中（桩指充电桩）";
    public static String code_605 = "不能下桩（转圈前准备）";
    public static String code_606 = "下桩结束（转圈前准备）";
    public static String code_607 = "下桩失败（转圈前准备）";
    public static String code_1000 = "定位程序正常";
    public static String code_1001 = "转圈定位中";
    public static String code_1002 = "计算中";
    public static String code_1003 = "定位程序初始化中";
    public static String code_1004 = "定位程序异常，需重启";
    public static String code_1005 = "激光不匹配";
    public static String code_1006 = "定位丢失";
    public static String code_1007 = "定位校正";
    public static String code_1008 = "检测到rfid";
    public static String code_1009 = "rfid验证后，定位有问题";
    public static String code_1010 = "加密狗有问题";
    public static String code_1100 = "迎宾空闲状态";
    public static String code_1101 = "远处迎宾";
    public static String code_1102 = "近处迎宾";
    public static String code_1103 = "机器人使用状态";
    public static String code_1104 = "机器人返回迎宾点";
    public static String code_1105 = "送宾状态";

    public static String getCodeMsg(int code) {
        switch (code) {
            case 000:
                return code_000;
            case 100:
                return code_100;
            case 300:
                return code_300;
            case 301:
                return code_301;
            case 302:
                return code_302;
            case 303:
                return code_303;
            case 304:
                return code_304;
            case 305:
                return code_305;
            case 306:
                return code_306;
            case 401:
                return code_401;
            case 402:
                return code_402;
            case 403:
                return code_403;
            case 404:
                return code_404;
            case 405:
                return code_405;
            case 406:
                return code_406;
            case 407:
                return code_407;
            case 408:
                return code_408;
            case 409:
                return code_409;
            case 410:
                return code_410;
            case 411:
                return code_411;
            case 600:
                return code_600;
            case 601:
                return code_601;
            case 602:
                return code_602;
            case 603:
                return code_603;
            case 604:
                return code_604;
            case 605:
                return code_605;
            case 606:
                return code_606;
            case 607:
                return code_607;
            case 1000:
                return code_1000;
            case 1001:
                return code_1001;
            case 1002:
                return code_1002;
            case 1003:
                return code_1003;
            case 1004:
                return  code_1004;
            case 1005:
                return code_1005;
            case 1006:
                return code_1006;
            case 1007:
                return code_1007;
            case 1008:
                return code_1008;
            case 1009:
                return code_1009;
            case 1010:
                return code_1010;
            case 1100:
                return code_1100;
            case 1101:
                return code_1101;
            case 1102:
                return code_1102;
            case 1103:
                return code_1103;
            case 1104:
                return code_1104;
            case 1105:
                return code_1105;
            default:
                return null;
        }
    }
}
