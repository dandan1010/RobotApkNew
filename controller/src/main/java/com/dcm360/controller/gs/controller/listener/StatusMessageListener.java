package com.dcm360.controller.gs.controller.listener;

/**
 * @author LiYan
 * @create 2019/5/15
 * @Describe
 */
public interface StatusMessageListener {
//    void connectStatus(boolean isConnect);

    void onMessage(String data);
}
