package com.retron.robot.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.retron.robot.BuildConfig;
import com.retron.robot.R;
import com.retron.robot.broadcast.BroadCastUtils;
import com.retron.robot.content.BaseEvent;
import com.retron.robot.content.Content;

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
import java.util.Date;

public class AssestFile {

    private static final String TAG = "AssestFile";
    private Context mContext;
    private RandomAccessFile raf;
    private StringBuffer stringBuffer;
    private RandomAccessFile randomAccessFile;
    private String updatePackageName = "";
    private String updateFilePath = "";
    private File updateFile;

    public AssestFile(Context mContext) {
        this.mContext = mContext;
        stringBuffer = new StringBuffer();
    }

    public void writeBytesToFile(ByteBuffer byteBuffer) {
        try {
            updatePackageName = "/update" + Content.update_file_name + ".apk";
            updateFilePath = getUpdateFilePath(mContext);
            randomAccessFile = new RandomAccessFile(updateFilePath  + updatePackageName, "rw");
            updateFile = new File(updateFilePath  + updatePackageName);
            randomAccessFile.seek(updateFile.length());
            randomAccessFile.write(byteBuffer.array());
            Log.d(TAG, "file length： " + byteBuffer.array().length + ",    randomAccessFile : " + randomAccessFile.length());
            if (randomAccessFile.length() == Content.update_file_length) {
                Log.d(TAG, "broadcast install apk");
                EventBus.getDefault().post(new EventBusMessage(BaseEvent.UPDATE_FILE_LENGTH, (int) (randomAccessFile.length() / 1024 / 1024 + 1)));
                FixFileName(updateFilePath  + updatePackageName, updateFilePath + "/update.apk");
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

    public int getFileCount() {
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
            for (int i = 0; i < file.listFiles().length; i++) {
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
            path = path + "/" + dateFormat1.format(new Date(System.currentTimeMillis())).replace(" ", "_").replace(":", "-") + ".bag";
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
            Log.d(TAG, " 下载bag File : " + e.getMessage());
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

