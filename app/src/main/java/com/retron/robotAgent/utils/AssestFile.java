package com.retron.robotAgent.utils;

import android.content.Context;
import android.nfc.Tag;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.retron.robotAgent.BuildConfig;
import com.retron.robotAgent.broadcast.BroadCastUtils;
import com.retron.robotAgent.content.BaseEvent;
import com.retron.robotAgent.content.Content;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AssestFile {

    private static final String TAG = "AssestFile";
    private Context mContext;
    private RandomAccessFile raf;
    private StringBuffer stringBuffer;
    private RandomAccessFile randomAccessFile;
    private String updatePackageName = "";
    private String updateFilePath = "";
    private File updateFile;
    private DateFormat dateFormat1;
    private String data = "";
    public static String ROBOT_HEALTHY_PATH;
    public static String ROBOT_BAG_PATH;
    public static String ROBOT_LOG;
    public static String ROBOT_INTERPRENTER;
    public static String ROBOT_DATABASES;
    public static String ROBOTLOG_DATABASES;

    public static String ROBOTZIP_PATH;
    public static String ROBOTZIP_BAG;
    public static String ROBOTZIP_INTERPRENTER;
    public static String ROBOTZIP_DATABASES;

    public static String ROBOT_MAP;

    public AssestFile(Context mContext) {
        this.mContext = mContext;
        stringBuffer = new StringBuffer();
        data = new AlarmUtils(mContext).getTimeDay(System.currentTimeMillis());
        ROBOT_HEALTHY_PATH = "/sdcard/robotLog/" + data + "/robotHealthy";
        ROBOT_BAG_PATH = "/sdcard/robotLog/" + data + "/robotBag";
        ROBOT_LOG = "/sdcard/robotLog";
        ROBOT_INTERPRENTER = "/sdcard/robotLog/" + data + "/interprenter";
        ROBOT_DATABASES = "/data/data/" + BuildConfig.APPLICATION_ID + "/databases/RobotDatabase";
        ROBOTLOG_DATABASES = "/sdcard/robotLog/" + data + "/databases";

        ROBOTZIP_PATH = "/sdcard/robotLogZip";
        ROBOTZIP_BAG = "/sdcard/robotLogZip/" + data + "/robotBag";
        ROBOTZIP_INTERPRENTER = "/sdcard/robotLogZip/" + data + "/interprenter";
        ROBOTZIP_DATABASES = "/sdcard/robotLogZip/" + data + "/databases";

        ROBOT_MAP = "/sdcard/robotMap";

        dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void writeBytesToFile(ByteBuffer byteBuffer) {
        try {
            updatePackageName = "/update" + Content.update_file_name + ".apk";
            updateFilePath = getUpdateFilePath(mContext);
            randomAccessFile = new RandomAccessFile(updateFilePath + updatePackageName, "rw");
            updateFile = new File(updateFilePath + updatePackageName);
            randomAccessFile.seek(updateFile.length());
            randomAccessFile.write(byteBuffer.array());
            Log.d(TAG, "file length： " + byteBuffer.array().length + ",    randomAccessFile : " + randomAccessFile.length());
            if (randomAccessFile.length() == Content.update_file_length) {
                Log.d(TAG, "broadcast install apk");
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPDATE_FILE_LENGTH, (int) (randomAccessFile.length() / 1024 / 1024 + 1)));
                FixFileName(updateFilePath + updatePackageName, updateFilePath + "/update.apk");
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                    randomAccessFile = null;
                }
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
            } else {
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPDATE_FILE_LENGTH, (int) (randomAccessFile.length() / 1024 / 1024)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改文件名称
     */
    public void FixFileName(String filePath, String newFileName) {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return;
        }
        newFileName = newFileName.trim();
        if (TextUtils.isEmpty(newFileName)) // 文件名不能为空
            return;
        Log.d(TAG, "FixFileName : " + filePath);
        File oldFile = new File(filePath);
        File newFile = new File(newFileName);
        boolean b = oldFile.renameTo(newFile);
        Log.d(TAG, "FixFileName : " + b);
    }

    /**
     * 获取文件夹路径
     *
     * @param context
     * @return
     */
    private String getUpdateFilePath(Context context) {
        String path = null;
        path = Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID;
        if (!new File(path + updatePackageName).exists()) {
            deleteFolder(path);
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path + updatePackageName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public boolean hasUpdateApk() {
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/" + BuildConfig.APPLICATION_ID + "/update.apk";
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    public void deepFile(String healthyString) {
        stringBuffer.append(healthyString).toString();
        runnable.run();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                File file1 = new File(ROBOT_HEALTHY_PATH);
                if (!file1.exists()) {
                    file1.mkdir();
                }
                String path = ROBOT_HEALTHY_PATH + "/" + System.currentTimeMillis() + ".txt";
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

    public int getFileCount() {
        File file = new File(ROBOT_HEALTHY_PATH);
        if (file.exists()) {
            return file.listFiles().length;
        }
        return 0;
    }

    public void deleteErrorCode() {
        File file = new File(ROBOT_HEALTHY_PATH);
        Log.d(TAG, "deleteErrorCode");
        if (file.exists()) {
            for (int i = 0; i < file.listFiles().length; i++) {
                file.listFiles()[i].delete();
            }
        }
    }

    public void writeBagFiles(byte[] bytes) {
        Log.d(TAG, "writeBagFiles ");
        OutputStream out = null;
        try {
            File file = new File(ROBOT_BAG_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = ROBOT_BAG_PATH + "/" + dateFormat1.format(new Date(System.currentTimeMillis())).replace(" ", "_").replace(":", "-") + ".bag";
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
            Log.d(TAG, " download bag File : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "download bag  IO : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mCopyFile(String fromPath, String toPath) {

        File fromFile = new File(fromPath);
        Log.d(TAG, "mCopyFile : " + fromPath + ",   " + toPath + ",   " + fromFile.exists());
        if (!fromFile.exists()) {
            return;
        }
        File toFile = new File(toPath);
        if (!toFile.exists()) {
            toFile.mkdirs();
        }
        File newFile = new File(toPath + "/RobotDatabase");
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(newFile);
            byte bt[] = new byte[1024 * 1024];
            int c;
            int length = 0;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
                length = length + c;
                //EventBus.getDefault().post(new EventBusMessage(BaseEvent.COPY_FILE, length / fosfrom.available() * 100));
            }
            fosfrom.close();
            fosto.close();
            Log.d(TAG, "copy RobotDatabase success");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "copy RobotDatabase fail : " + e.toString());
        } catch (IOException e) {
            Log.d(TAG, "copy RobotDatabase fail : " + e.toString());
        }
    }

    public static void zip(String src, String dest) {
        //定义压缩输出流
        ZipOutputStream out = null;
        try {
            //传入源文件
            File outFile = new File(ROBOTZIP_PATH);
            if (!outFile.exists()) {
                outFile.mkdirs();
            }
            File fileOrDirectory = new File(src);
            //传入压缩输出流
            out = new ZipOutputStream(new FileOutputStream(new File(dest)));
            //判断是否是一个文件或目录
            //如果是文件则压缩
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                //否则列出目录中的所有文件递归进行压缩
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    Log.d(TAG, "zipFile : " + entries[i].getName());
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath) throws IOException {
        FileInputStream in = null;
        try {
            //判断目录是否为null
            Log.d(TAG, "zipFileOrDirectory : " + fileOrDirectory.isDirectory());
            if (!fileOrDirectory.isDirectory()) {
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                //归档压缩目录
                ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
                //将压缩目录写到输出流中
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                //列出目录中的所有文件
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    //递归压缩
                    zipFileOrDirectory(out, entries[i], curPath + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ArrayList<String> getLogList() {
        ArrayList<String> arrayList = new ArrayList<>();
        File file = new File(ROBOT_LOG);
        Log.d(TAG, "getLogList111 : " + file.listFiles().length);
        if (file.exists()) {
            for (int i = 0; i < file.listFiles().length; i++) {
                Log.d("getLogList ", "getLogList Name : " + file.listFiles()[i].getName());
                arrayList.add(file.listFiles()[i].getName());
            }
            return arrayList;
        }
        return arrayList;
    }

    public void downloadMapPic(String mapName, byte[] bytes) {
        File robotMap = new File(ROBOT_MAP);
        if (!robotMap.exists()) {
            robotMap.mkdirs();
        }
        File file = new File(ROBOT_MAP + "/" + mapName + ".tar.gz");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMapPic(String mapName, byte[] bytes) {
        File robotMap = new File("/sdcard/robotMapPic");
        if (!robotMap.exists()) {
            robotMap.mkdirs();
        }
        File file = new File("/sdcard/robotMapPic/" + mapName + ".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

