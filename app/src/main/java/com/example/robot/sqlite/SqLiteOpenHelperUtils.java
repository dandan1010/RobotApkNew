package com.example.robot.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.robot.utils.Content;

import java.util.ArrayList;

public class SqLiteOpenHelperUtils {
    private Context mContext;
    private TaskSqLite taskSqLite;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<String> arrayList;


    public SqLiteOpenHelperUtils(Context mContext) {
        this.mContext = mContext;
        taskSqLite = new TaskSqLite(mContext);
    }

    //历史任务
    public void saveTaskHistory(String mapName, String taskName, String time, String data) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.tableName + "(" + Content.dbTaskMapName + ", " + Content.dbTaskName + ", " + Content.dbTime + ", " + Content.dbData + ") values ('" + mapName + "','" + taskName + "','" + time + "','" + data + "')");
    }

    public Cursor searchTaskHistory() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.tableName, new String[]{"_id", Content.dbTaskMapName, Content.dbTaskName, Content.dbTime, Content.dbData}, null, null, null, null, null);
        return cursor;
    }

    public void deleteTaskHistory(String taskName) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.tableName, Content.dbTaskName + "=?", new String[]{taskName});
        close();
    }

    //定时任务
    public void saveAlarmTask(String mapTaskName, String time, String data, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbAlarmName + "(" + Content.dbAlarmMapTaskName + ", " + Content.dbAlarmTime + ", " + Content.dbAlarmCycle + ", " + Content.dbAlarmIsRun + ") values ('" + mapTaskName + "','" + time + "','" + data + "','" + isRun + "')");
    }

    public Cursor searchAlarmTask(String type, String searchKey) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbAlarmName, new String[]{"_id", Content.dbAlarmMapTaskName, Content.dbAlarmTime, Content.dbAlarmCycle, Content.dbAlarmIsRun}, type + "=?", new String[]{searchKey}, null, null, null);
        return cursor;
    }

    public void updateAlarmTask(String mapTaskName,String type, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbAlarmName + " set " + type + "='" + isRun + "' where " + Content.dbAlarmMapTaskName + "='" + mapTaskName + "'");
    }

    public void updateAllAlarmTask(String type, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbAlarmName + " set " + type + "='" + isRun + "' where " + type + "='false'");
    }

    public void deleteAlarmTask(String mapTaskName) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.dbAlarmName, Content.dbAlarmMapTaskName + "=?", new String[]{mapTaskName});
        close();
    }

    public void close(){
        sqLiteDatabase.close();
    }

    public void reset_Db(String tab_name){
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + tab_name + "");
    }

}


