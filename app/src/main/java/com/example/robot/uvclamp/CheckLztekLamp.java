package com.example.robot.uvclamp;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import com.example.robot.R;
import com.example.robot.task.TaskManager;
import com.example.robot.utils.EventBusMessage;
import com.example.robot.utils.Content;
import com.lztek.toolkit.AddrInfo;
import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.OutputStream;

public class CheckLztekLamp {

    private static final String TAG = "CheckLztekLamp";

    private static final int ON = 0;
    private static final int OFF = 1;
    private Context mContext;
    private Lztek mLztek;
    private SerialPort serialPort;
    private SerialPort batterySerialPort;
    private int mUVCLightStatus = OFF;
    private int mLEDStatus = OFF;
    private int mSensorStatus = OFF;

    /**
     * 248:左边sensor
     * 249:前边sensor
     * 218:右边sensor
     * 250:uvc
     * 230:uvc灯
     * 228:uvc灯
     * 229:uvc灯
     * 251:led灯
     */
    private int[] port = new int[]{218, 248, 249, 250, 230, 228, 229, 251};
    private MoreSerialPortThread moreSerialPortThread;
    public boolean threadFlag = false;

    public CheckLztekLamp(Context mContext) {
        this.mContext = mContext;
        mLztek = Lztek.create(mContext);
    }

    private void setEnable() {
        for (int i = 0; i < port.length; i++) {
            mLztek.gpioEnable(port[i]);
        }
    }

    public void initUvcMode() {
        for (int i = 3; i < 7; i++) {
            mLztek.setGpioOutputMode(port[i]);
            mLztek.setGpioValue(port[i], 1);
        }
    }

    public void setUvcMode(int flag) {
        if (Content.Working_mode == 2) {
            if (mUVCLightStatus != flag) {
                mUVCLightStatus = flag;
                for (int i = 3; i < 7; i++) {
                    mLztek.setGpioValue(port[i], flag);
                    try {
                        if (flag == 0) {
                            Thread.sleep(2000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setUvcModeForDemo(int flag) {
        if (Content.Working_mode == 1) {
            if (mUVCLightStatus != flag) {
                mUVCLightStatus = flag;
                for (int i = 3; i < 7; i++) {
                    mLztek.setGpioValue(port[i], flag);
                    try {
                        if (flag == 0) {
                            Thread.sleep(2000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setMode() {
        for (int i = 0; i < port.length; i++) {
            mLztek.setGpioInputMode(port[i]);
        }
    }

    public boolean getGpioSensorState() {

        boolean k1 = mLztek.getGpioValue(port[0]) == 1 ? true : false;
        boolean k2 = mLztek.getGpioValue(port[1]) == 1 ? true : false;
        boolean k3 = mLztek.getGpioValue(port[2]) == 1 ? true : false;

        Log.d("zdzd : ", "zdzd : " + k1 + ",  " + k2 + ", " + k3);
        if (k1 || k2 || k3) {
            return true;
        } else {
            return false;
        }
//        return false;
    }

    public void startCheckSensorAtTime() {
        setEnable();
        setMode();
    }

    public void startLedLamp() {
        mLztek.setGpioValue(port[7], 0);
        threadFlag = true;
        openSerialPort();
    }

    public void stopLedLamp() {
        mLztek.setGpioValue(port[7], 1);
        threadFlag = false;
    }

    public void startUvc1Lamp() {
        if (Content.Working_mode == 2) {
            mLztek.setGpioValue(port[5], 0);
        }
    }

    public void stopUvc1Lamp() {
        mLztek.setGpioValue(port[5], 1);
    }

    public void startUvc2Lamp() {
        if (Content.Working_mode == 2) {
            mLztek.setGpioValue(port[6], 0);
        }
    }

    public void stopUvc2Lamp() {
        mLztek.setGpioValue(port[6], 1);
    }

    public void startUvc3Lamp() {
        if (Content.Working_mode == 2) {
            mLztek.setGpioValue(port[4], 0);
        }
    }

    public void stopUvc3Lamp() {
        mLztek.setGpioValue(port[4], 1);
    }

    public void startUvc4Lamp() {
        if (Content.Working_mode == 2) {
            mLztek.setGpioValue(port[3], 0);
        }
    }

    public void stopUvc4Lamp() {
        mLztek.setGpioValue(port[3], 1);
    }

    public boolean getEthEnable() {
        return mLztek.getEthEnable();
    }

    public void openEth() {
        mLztek.setEthEnable(true);
    }

    public void setEthAddress() {
        mLztek.setEthIpAddress(Content.ip, Content.mask, Content.gateway, Content.dns);
    }

    public AddrInfo getAddrInfo() {
        return mLztek.getEthAddrInfo();
    }


    /**
     * 设置系统时间
     */
    public void setSystemTime(long milliseconds) {
        mLztek.setSystemTime(milliseconds);
    }

    /**
     * 请求led灯光
     */
    private void openSerialPort() {//控制Led灯
        serialPort = mLztek.openSerialPort("/dev/ttyS1", 115200, 8, 0, 1, 0);
        if (serialPort == null) {
            Log.d(TAG, "serialPort is null");
            return;
        }

        Log.d("ZDZD :", "robotstate : " + Content.robotState + "  thread = " + threadFlag);
        if (moreSerialPortThread == null) {
            Log.d(TAG, "myThread is null");
            moreSerialPortThread = new MoreSerialPortThread();
        }
        moreSerialPortThread.start();
    }

    /**
     * led线程
     */
    class MoreSerialPortThread extends Thread {
        int index = 0;
        String[] lightArray;
        OutputStream outputStream;

        @Override
        public void run() {
            super.run();
            Log.d("zdzd : ", "thread = " + threadFlag);
            while (threadFlag) {
                outputStream = serialPort.getOutputStream();
                try {
                    Log.d(TAG, "led robotstatus : " + Content.robotState);
                    switch (Content.robotState) {
                        case 0:
                            break;
                        case 1:
                            index = 0;
                            lightArray = mContext.getResources().getStringArray(R.array.static_light);
                            break;
                        case 2:
                            break;
                        case 3:
                            lightArray = mContext.getResources().getStringArray(R.array.move_light);
                            break;
                        case 4:
                            lightArray = mContext.getResources().getStringArray(R.array.charge_light);
                            break;
                        case 5:
                            lightArray = mContext.getResources().getStringArray(R.array.warning_light);
                            break;
                        case 6:
                            lightArray = mContext.getResources().getStringArray(R.array.low_power_light);
                            break;
                        default:
                            break;
                    }
                    if (index < lightArray.length - 1) {

                    } else {
                        index = 0;
                    }
                    byte[] colorLight = hexBytes(lightArray[index]);
                    if (null != colorLight) {
                        outputStream.write(colorLight);
                        Log.d(TAG, "serialPort write success index = " + index + "  , state = " + Content.robotState);
                    }
                    if (index < lightArray.length - 1) {
                        index++;
                    } else {
                        index = 0;
                    }
                    Thread.sleep(Content.time);
                    if (Content.robotState == 1 || Content.robotState == 5 || Content.robotState == 6 || Content.robotState == 4) {
                        outputStream.write(hexBytes(mContext.getResources().getString(R.string.no_color_light)));
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "#ERROR# : [COM]Write Faild: " + e.getMessage(), e);
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 转换16进制
     */
    private byte[] hexBytes(String hexString) {
        int length;
        byte h;
        byte l;
        byte[] byteArray;
        hexString = hexString.replaceAll(" ", "");
        length = null != hexString ? hexString.length() : 0;
        length = (length - (length % 2)) / 2;
        if (length < 1) {
            return null;
        }

        byteArray = new byte[length];
        for (int i = 0; i < length; i++) {
            h = (byte) hexString.charAt(i * 2);
            l = (byte) hexString.charAt(i * 2 + 1);

            l = (byte) ('0' <= l && l <= '9' ? l - '0' :
                    'A' <= l && l <= 'F' ? (l - 'A') + 10 :
                            'a' <= l && l <= 'f' ? (l - 'a') + 10 : 0);
            h = (byte) ('0' <= h && h <= '9' ? h - '0' :
                    'A' <= h && h <= 'F' ? (h - 'A') + 10 :
                            'a' <= h && h <= 'f' ? (h - 'a') + 10 : 3);
            byteArray[i] = (byte) (0x0FF & ((h << 4) | l));
        }
        return byteArray;
    }

    /**
     * 请求电池数据
     */
    public void openBatteryPort() {//获取电池
        batterySerialPort = mLztek.openSerialPort("/dev/ttyS0", 9600, 8, 0, 1, 0);
        if (batterySerialPort == null) {
            Log.d(TAG, "BatteryPort is null");
            return;
        }
        handler.postDelayed(runnable, 0);
    }

    Handler handler = new Handler();

    /**
     * 间隔5秒写入请求一次电池数据
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            OutputStream outputStream = null;
            byte[] battery = hexBytes("DDA50300FFFD77");
            try {
                if (null != battery) {
                    outputStream = batterySerialPort.getOutputStream();
                    outputStream.write(battery);
                    Log.d(TAG, "batteryPort write success");
                }
                handler.postDelayed(batteryRunnable, 0);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            handler.postDelayed(this::run, 1 * 1000);
        }
    };

    /**
     * 读取电池数据
     */
    Runnable batteryRunnable = new Runnable() {
        @Override
        public void run() {
            if (null == batterySerialPort) {
                return;
            }
            java.io.InputStream input = batterySerialPort.getInputStream();
            try {
                byte[] buffer = new byte[1024];
                int len = input.read(buffer);
                Log.d(TAG, "读取数据len ： " + len);
                if (len > 0) {
                    byte[] data = java.util.Arrays.copyOfRange(buffer, 0, len);
                    EditText editText = new EditText(mContext);
                    for (byte b : data) {
                        byte h = (byte) (0x0F & (b >> 4));
                        byte l = (byte) (0x0F & b);
                        editText.append("" + (char) (h > 9 ? 'A' + (h - 10) : '0' + h));
                        editText.append("" + (char) (l > 9 ? 'A' + (l - 10) : '0' + l));
                    }
                    Log.d(TAG, "读取数据 ： " + editText.getText().toString());
                    EventBus.getDefault().post(new EventBusMessage(1004, data));
                    EventBus.getDefault().post(new EventBusMessage(10033, data));
                    Log.d("zdzd ", "充点电：" + editText.getText().toString().substring(12, 14));
                    String msg = "";
                    if (!"FF".equals(editText.getText().toString().substring(12, 14))) {
                        Content.robotState = 4;
                        Content.time = 1000;
                        msg = "充电";
                        Content.isCharging = true;
                    } else {
                        Content.isCharging = false;
                        msg = "放电";
                        if (Content.robotState == 4) {
                            Content.robotState = 1;
                            Content.time = 4000;
                        }
                    }
                    EventBus.getDefault().post(new EventBusMessage(10000, msg));
                }
            } catch (Exception e) {
                android.util.Log.d(TAG, "[COM]Read Faild: " + e.getMessage(), e);
            } finally {
                if (null != input) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    //TEST
    public String testGpioSensorState() {
        boolean k1 = mLztek.getGpioValue(port[0]) == 1 ? true : false;
        boolean k2 = mLztek.getGpioValue(port[1]) == 1 ? true : false;
        boolean k3 = mLztek.getGpioValue(port[2]) == 1 ? true : false;
        return k1 + "," + k2 + "," + k3;
    }

    public void test_uvc_start1() {
        startUvc1Lamp();
    }

    public void test_uvc_start2() {
        startUvc2Lamp();
    }

    public void test_uvc_start3() {
        startUvc3Lamp();
    }

    public void test_uvc_start4() {
        startUvc4Lamp();
    }

    public void test_uvc_startAll() {
        setUvcMode(0);
//        startUvc1Lamp();
//        startUvc2Lamp();
//        startUvc3Lamp();
//        startUvc4Lamp();
    }

    public void test_uvc_stopAll() {
        stopUvc1Lamp();
        stopUvc2Lamp();
        stopUvc3Lamp();
        stopUvc4Lamp();
    }

    public void test_uvc_stop1() {
        stopUvc1Lamp();
    }

    public void test_uvc_stop2() {
        stopUvc2Lamp();
    }

    public void test_uvc_stop3() {
        stopUvc3Lamp();
    }

    public void test_uvc_stop4() {
        stopUvc4Lamp();
    }

    public int getUVCStatus() {
        return mUVCLightStatus;
    }
}
