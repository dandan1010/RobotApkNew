package com.example.robot.log;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.robot.utils.Content;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class LogcatHelper {
    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;
    private File fileList;
    ArrayList<String> mFilesList = new ArrayList<String>();  //存放/myLog 下的所有文件
    private File file;
    private int count = 0;

    /**
     * 初始化目录
     */
    private void init(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "interprenter";
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + "interprenter";
        }
        fileList = new File(PATH_LOGCAT);
        if (!fileList.exists()) {
            fileList.mkdirs();
            Log.e("init : ", "创建文件夹");
        }
        Log.e("init : ", "PATH_LOGCAT");
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
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private static long getFileSizes(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
                Log.e("获取文件大小", "文件不存在!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileNotFoundError : ", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException : ", e.getMessage());
        }
        return size;
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;

            file = new File(PATH_LOGCAT, "log_"
                    + getFileName().replace(" ", "_").replace(":", "")+count + ".log");

//            if (getFileSizes(fileList) > 5 * 1024 * 1024) {
//                Log.d("zdzd : ", "log大于5000");
//                if (fileList.isDirectory()) {
//                    File[] AllFiles = fileList.listFiles(); //列出目录下的所有文件
//                    mFilesList.clear();
//                    for (int i = 0; i < AllFiles.length; i++) {
//                        File mFile = AllFiles[i]; //得到文件
//                        String Name = mFile.getName(); //得到文件的名字
//                        if (Name == null || Name.length() < 1) {
//                            break;
//                        }
//                        if (Name.startsWith("log_") && Name.endsWith(".log")) {  //筛选出log
//                            mFilesList.add(Name); //把文件名添加到链表里
//                        }
//                    }
//                }
//
//                Collections.sort(mFilesList);
//                for (int i = 0; i < 1; i++) {
//                    String Name = mFilesList.get(i); //得到链表最早的文件名
//                    File mFile = new File(PATH_LOGCAT, Name); //得到最早的文件
//                    Log.d("delete file : ", mFile.getPath());
//                    mFile.delete(); //删除
//                }
//            }
            try {
                out = new FileOutputStream(file);
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
                    if (out != null && line.contains(mPID)) {
                        out.write((line + "\n").getBytes());
                    }
//                    if (getFileSize(file) > 10 * 1024 * 1024) {
//                        Log.d("zdzd : ", "log大于100");
//                        LogcatHelper.this.stop();
//                        count ++;
//                        LogcatHelper.this.start();
//
//                    }
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
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }

    public String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;// 2012年10月03日 23:41:31
    }
}
