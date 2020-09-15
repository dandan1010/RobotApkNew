package com.dcm360.controller.robot_interface.status;

import com.dcm360.controller.gs.controller.bean.system_bean.HealthStatus;

/**
 * @author LiYan
 * @create 2019/5/15
 * @Describe
 */
public interface NavigationStatus {
    enum NoticeType {
        LOCALIZATION_FAILED, GOAL_NOT_SAFE, TOO_CLOSE_TO_OBSTACLES, UNREACHABLE, REACHED, HEADING,
        PLANNING, UNREACHED
    }

    void noticeType(String type, String destination);

    void statusCode(int code, String msg);
}
