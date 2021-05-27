package com.example.robot.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.robot.BuildConfig;
import com.example.robot.broadcast.BroadCastUtils;
import com.example.robot.content.BaseEvent;
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
    private RandomAccessFile randomAccessFile;

    public AssestFile(Context mContext) {
        this.mContext = mContext;
        stringBuffer = new StringBuffer();
        synchronizedObject = new Object();
    }

    public void writeBytesToFile(ByteBuffer byteBuffer) {

        OutputStream out = null;
        try {
            randomAccessFile = new RandomAccessFile(getUpdateFilePath(mContext), "rw");
            randomAccessFile.seek(getUpdateFilePath(mContext).length());
            randomAccessFile.write(byteBuffer.array());
            Content.randomFileCount++;
            Log.d(TAG, "file length： " + Content.randomFileCount + ",    " + byteBuffer.array().length);
            if (Content.randomFileCount >= 100) {
                Log.d(TAG, "broadcast install apk");
                EventBus.getDefault().post(new EventBusMessage(30002, "开始升级"));
                if (Content.server != null) {
                    try {
                        Content.server.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                BroadCastUtils.getInstance(mContext).sendUpdateBroadcast();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            out = new FileOutputStream(getCrashFilePath(mContext));
//            InputStream is = new ByteArrayInputStream(byteBuffer.array());
//            Log.d(TAG, "file length： " + is.available() + ",    " + byteBuffer.array().length);
//            fileLength = is.available();
//            byte[] buff = new byte[1024];
//            int len = 0;
//            int count = 0;
//            while ((len = is.read(buff)) != -1) {
//                out.write(buff, 0, len);
//                count += len;
//                int progress = (int) ((float) count / (float) fileLength * 100);
//                if (progress == 100) {
//                    Log.d(TAG, "broadcast install apk");
//                    EventBus.getDefault().post(new EventBusMessage(30002, "准备升级"));
//                    if (Content.server != null) {
//                        try {
//                            Content.server.stop();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    Intent intent = new Intent("com.android.robot.update");
//                    intent.setPackage("com.example.wireLessApk");
//                    mContext.sendBroadcast(intent);
//                }
//            }
//            out.flush();
//            is.close();
//            out.close();
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, e.getMessage());
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.d(TAG, e.getMessage());
//            e.printStackTrace();
//        }
    }

    /**
     * 获取文件夹路径
     *
     * @param context
     * @return
     */
    private static String getUpdateFilePath(Context context) {
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

    public void deleteUpdateFile() {
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID + "/update.apk";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        Log.d(TAG, "删除 update.apk 文件成功");
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
        String path = "/sdcard/robotLog/robotHealthy";
        File file = new File(path);
        if (file.exists()) {
            return file.listFiles().length;
        }
        return 0;
    }

    public void deleteErrorCode() {
        String path = "/sdcard/robotLog/robotHealthy";
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
            String path = "/sdcard/robotLog/robotBag";
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

    public void mCopyFile(String fromPath, String toPath) {
        File fromFile = new File(fromPath);
        if (!fromFile.exists()) {
            return;
        }
        File toFile = new File("/sdcard/robotLog");
        if (!fromFile.exists()) {
            toFile.mkdirs();
        }
        toFile = new File(toPath + "/RobotDatabase");
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024 * 1024];
            int c;
            int length = 0;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
                length = length + c;
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.COPY_FILE, length / fosfrom.available() * 100));
            }
            fosfrom.close();
            fosto.close();
        } catch (FileNotFoundException e) {
            Log.i("复制文件异常", e.toString());
        } catch (IOException e) {
            Log.i("复制文件异常", e.toString());
        }
    }

    public static void zipFolder(String srcFilePath, String zipFilePath) {
        //创建Zip包
        java.util.zip.ZipOutputStream outZip =
                null;
        try {
            outZip = new java.util.zip.ZipOutputStream(new FileOutputStream(zipFilePath));


            //打开要输出的文件
            java.io.File file = new java.io.File(srcFilePath);

            //压缩
            zipFiles(file.getParent() + java.io.File.separator, file.getName(), outZip);

            //完成,关闭
            outZip.finish();
            outZip.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void zipFiles(String folderPath, String filePath,
                                 java.util.zip.ZipOutputStream zipOut) {
        if (zipOut == null) {
            return;
        }

        java.io.File file = new java.io.File(folderPath + filePath);

        //判断是不是文件
        if (file.isFile()) {
            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(filePath);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);

                zipOut.putNextEntry(zipEntry);

                int len;
                byte[] buffer = new byte[4096];

                while ((len = inputStream.read(buffer)) != -1) {
                    zipOut.write(buffer, 0, len);
                }

                zipOut.closeEntry();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                java.util.zip.ZipEntry zipEntry =
                        new java.util.zip.ZipEntry(filePath + java.io.File.separator);
                try {
                    zipOut.putNextEntry(zipEntry);
                    zipOut.closeEntry();
                    //如果有子文件, 遍历子文件
                    for (int i = 0; i < fileList.length; i++) {
                        zipFiles(folderPath, filePath + java.io.File.separator + fileList[i], zipOut);
                    }//end of for
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//end of if

        }//end of func

    }
}

