package com.dcm360.controller.gs.controller.gaoxian;

/**
 * @author LiYan
 * @create 2018/11/8
 * @Describe 获取机器人状态Data
 */
public class DataResponse {

    private Double battery;               //45.435484000000002,  //电池电量：45.43%
    private Double batteryVoltage;        //21.815000000000001,  //电池电压：21.815V
    private Boolean charge;              // false,  //充电状态：未充电（已停用）
    private Integer charger;             // 0,  //充电状态，0：未充电，3：自动充电，4：手动充电，5：自动充电下电池充满
    private Double chargerCurrent;       // 0.604688,  //充电电流，实际电流检测功能需要硬件线路支持
    private Boolean chargerStatus;       //false,  //充电状态：未充电
    private Double chargerVoltage;       // 0.021000000000000001,  //充电接触条检测到的电压：0.02V
    private Boolean emergency;           //false,  //急停状态：未按下
    private Boolean emergencyStop;       //false,  //急停状态：未按下,与上一个参数兼容使用，或的关系
    private Integer navigationSpeedLevel;// 1,  //导航任务速度等级 0：低速，1：中速，2：高速
    private Integer playPathSpeedLevel;  // 2,  //跟线任务速度等级 0：低速，1：中速，2：高速
    private Double speed;                // 0,  //机器人实时速度，单位m/s
    private Long statusUpdatedAt;        // 1515896014,  //状态更新时间戳
    private Double totalMileage;        //0,  //机器人总里程，出厂后的累积里程（暂时未开放）
    private Long uptime;                 //5709  //运行时间，单位s

    public Double getBattery() {
        return battery;
    }

    public void setBattery(Double battery) {
        this.battery = battery;
    }

    public Double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public Boolean getCharge() {
        return charge;
    }

    public void setCharge(Boolean charge) {
        this.charge = charge;
    }

    public Integer getCharger() {
        return charger;
    }

    public void setCharger(Integer charger) {
        this.charger = charger;
    }

    public Double getChargerCurrent() {
        return chargerCurrent;
    }

    public void setChargerCurrent(Double chargerCurrent) {
        this.chargerCurrent = chargerCurrent;
    }

    public Boolean getChargerStatus() {
        return chargerStatus;
    }

    public void setChargerStatus(Boolean chargerStatus) {
        this.chargerStatus = chargerStatus;
    }

    public Double getChargerVoltage() {
        return chargerVoltage;
    }

    public void setChargerVoltage(Double chargerVoltage) {
        this.chargerVoltage = chargerVoltage;
    }

    public Boolean getEmergency() {
        return emergency;
    }

    public void setEmergency(Boolean emergency) {
        this.emergency = emergency;
    }

    public Boolean getEmergencyStop() {
        return emergencyStop;
    }

    public void setEmergencyStop(Boolean emergencyStop) {
        this.emergencyStop = emergencyStop;
    }

    public Integer getNavigationSpeedLevel() {
        return navigationSpeedLevel;
    }

    public void setNavigationSpeedLevel(Integer navigationSpeedLevel) {
        this.navigationSpeedLevel = navigationSpeedLevel;
    }

    public Integer getPlayPathSpeedLevel() {
        return playPathSpeedLevel;
    }

    public void setPlayPathSpeedLevel(Integer playPathSpeedLevel) {
        this.playPathSpeedLevel = playPathSpeedLevel;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Long getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(Long statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public Double getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(Double totalMileage) {
        this.totalMileage = totalMileage;
    }

    public Long getUptime() {
        return uptime;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }

    @Override
    public String toString() {
        return "DataResponse{" +
                "battery=" + battery +
                ", batteryVoltage=" + batteryVoltage +
                ", charge=" + charge +
                ", charger=" + charger +
                ", chargerCurrent=" + chargerCurrent +
                ", chargerStatus=" + chargerStatus +
                ", chargerVoltage=" + chargerVoltage +
                ", emergency=" + emergency +
                ", emergencyStop=" + emergencyStop +
                ", navigationSpeedLevel=" + navigationSpeedLevel +
                ", playPathSpeedLevel=" + playPathSpeedLevel +
                ", speed=" + speed +
                ", statusUpdatedAt=" + statusUpdatedAt +
                ", totalMileage=" + totalMileage +
                ", uptime=" + uptime +
                '}';
    }
}
