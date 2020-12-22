package com.example.robot.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.robot.BuildConfig;
import com.example.robot.R;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssestFile {

    private static final String TAG = "AssestFile";
    private FileInputStream is;
    private FileOutputStream fos;
    //    OutputStream fos;
    private Context mContext;
    private long fileLength = 0;
    private static final String ZIP_NAME = "update.apk";

    public AssestFile(Context mContext) {
        this.mContext = mContext;
    }

    public void writeBytesToFile(ByteBuffer byteBuffer) {

        OutputStream out = null;
        try {
            out = new FileOutputStream(getCrashFilePath(mContext));
            InputStream is = new ByteArrayInputStream(byteBuffer.array());
            Log.d("zdzd 文件： " , "" + is.available() + ",    " + byteBuffer.array().length);
            fileLength = is.available();
            byte[] buff = new byte[1024];
            int len = 0;
            int count = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff, 0, len);
                count += len;
                int progress = (int) ((float) count / (float) fileLength * 100);
                Log.d(TAG, "保存进度 ：" + progress);
                if (progress == 100) {
                    Log.d(TAG, "发广播安装apk");
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
            Log.d("zdzd111 ", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("zdzd222 ", e.getMessage());
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
                + "/" + BuildConfig.APPLICATION_ID +"/update.apk";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("TAG", "getCrashFilePath: " + path);
        return path;
    }
}
