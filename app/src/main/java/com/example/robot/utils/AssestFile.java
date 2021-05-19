package com.example.robot.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.robot.BuildConfig;
import com.example.robot.content.Content;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class AssestFile {

    private static final String TAG = "AssestFile";
    private FileInputStream is;
    private FileOutputStream fos;
    //    OutputStream fos;
    private Context mContext;
    private long fileLength = 0;
    private static final String ZIP_NAME = "update.apk";
    private RandomAccessFile raf;
    private StringBuffer stringBuffer;
    private Object synchronizedObject;

    public AssestFile(Context mContext) {
        this.mContext = mContext;
        stringBuffer = new StringBuffer();
        synchronizedObject = new Object();
    }

    public void writeBytesToFile(ByteBuffer byteBuffer) {

        OutputStream out = null;
        try {
            out = new FileOutputStream(getCrashFilePath(mContext));
            InputStream is = new ByteArrayInputStream(byteBuffer.array());
            Log.d(TAG, "file length： " + is.available() + ",    " + byteBuffer.array().length);
            fileLength = is.available();
            byte[] buff = new byte[1024];
            int len = 0;
            int count = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff, 0, len);
                count += len;
                int progress = (int) ((float) count / (float) fileLength * 100);
                if (progress == 100) {
                    Log.d(TAG, "broadcast install apk");
                    EventBus.getDefault().post(new EventBusMessage(30002, "准备升级"));
                    if (Content.server != null) {
                        try {
                            Content.server.stop();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent("com.android.robot.update");
                    intent.setPackage("com.example.wireLessApk");
                    mContext.sendBroadcast(intent);
                }
            }
            out.flush();
            is.close();
            out.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取文件夹路径
     *
     * @param context
     * @return
     */
    private static String getCrashFilePath(Context context) {
        String path = null;
        path = Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        path = Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID + "/update.apk";
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public void deepFile(String healthyString) {
        stringBuffer.append(healthyString).toString();
        runnable.run();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
                try {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/robotHealthy";
                    File file1 = new File(path);
                    if (!file1.exists()) {
                        file1.mkdir();
                    }
                    path = path + "/" + System.currentTimeMillis() + ".txt";
                    File file = new File(path);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(file.length());
                    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    raf.write((dateFormat1.format(new Date(System.currentTimeMillis())) + " : " + stringBuffer.toString()).getBytes());
                    raf.write("\n".getBytes());
                    Log.d(TAG, "write log file success");
                    stringBuffer.delete(0, stringBuffer.length());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "write log file error : " + e.getMessage());
                } finally {
                    if (raf != null) {
                        try {
                            raf.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
    };

    public int getFileCount(){
        String path = Environment.getExternalStorageDirectory().getPath() + "/robotHealthy";
        File file = new File(path);
        if (file.exists()) {
            return file.listFiles().length;
        }
        return 0;
    }

    public void deleteErrorCode() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/robotHealthy";
        File file = new File(path);
        Log.d(TAG, "deleteErrorCode");
        if (file.exists()) {
            Log.d(TAG, "deleteErrorCode存在");
            for (int i = 0;i<file.listFiles().length;i++){
                file.listFiles()[i].delete();
            }
        }
    }
    public void writeBagFiles(byte[] bytes) {
        Log.d(TAG, "ZDZD : writeBagFiles ");
        OutputStream out = null;
        try {
            String path = "/sdcard/robotBag";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            path = path + "/"+ dateFormat1.format(new Date(System.currentTimeMillis())).replace(" ","_").replace(":","-") + ".bag";
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            out = new FileOutputStream(path);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG,  " 下载bag File : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, " 下载bag  IO : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

