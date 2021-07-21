package com.retron.robotAgent.log;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.retron.robotAgent.content.Content;
import com.retron.robotAgent.receiver.AlarmReceiver;
import com.retron.robotAgent.utils.AssestFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class LogcatHelper {
    private static LogcatHelper INSTANCE = null;
    private LogDumper mLogDumper = null;
    private int mPId;
    private File fileList;
    ArrayList<String> mFilesList = new ArrayList<String>();  //存放/myLog 下的所有文件
    private File file;
    private int count = 0;
    private AlarmManager mAlarmManager;

    /**
     * 初始化目录
     */
    private void init(Context context) {
        fileList = new File(AssestFile.ROBOT_INTERPRENTER);
        if (!fileList.exists()) {
            fileList.mkdirs();
        }
        Log.d("init : ", "PATH_LOGCAT");
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), AssestFile.ROBOT_INTERPRENTER);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    public void getFileLength() {
        stop();
        File file = new File(AssestFile.ROBOT_INTERPRENTER);
        if (file.listFiles().length > 10) {
            sortFileList(file.listFiles());
        }
        start();
    }

    private void sortFileList(File[] files) {
        ArrayList<File> fileInfoArrayList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            fileInfoArrayList.add(files[i]);
        }
        Collections.sort(fileInfoArrayList);
        for (int i = 0; i < (files.length - 10); i++) {
            fileInfoArrayList.get(i).delete();
        }
    }


    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private RandomAccessFile random = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;

            file = new File(AssestFile.ROBOT_INTERPRENTER, "log_"
                    + getFileName().replace(" ", "_").replace(":", "") + ".log");
            try {
                random = new RandomAccessFile(file,"rw");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
//            cmds = "logcat *:e *:i *:d *:v | grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (random != null && line.contains(mPID)) {
                        random.seek(file.length());
                        random.write((line + "\n").getBytes());
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (random != null) {
                    try {
                        random.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    random = null;
                }
            }
        }
    }

    public String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;// 2012年10月03日 23:41:31
    }

    public void createAlarmFile(Context mContext) {
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        Intent alarmIntent = new Intent(Content.DeleteFileAlarmAction);
        ComponentName componentName = new ComponentName(mContext, "com.retron.robotAgent.receiver.AlarmReceiver");
        alarmIntent.setComponent(componentName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, instance.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
    }
}
