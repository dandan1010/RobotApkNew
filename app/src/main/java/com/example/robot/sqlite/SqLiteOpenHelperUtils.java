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

    public Cursor searchAlarmTask(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbAlarmName, new String[]{"_id", Content.dbAlarmMapTaskName, Content.dbAlarmTime, Content.dbAlarmCycle, Content.dbAlarmIsRun}, typeName + "=?", new String[]{typeString}, null, null, null);
        return cursor;
    }

    public void updateAlarmTask(String mapTaskName, String type, String isRun) {
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

    //点时间
    public void savePointTask(String mapTaskName, String pointName, String spinnerTime, String x, String y) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbPointTime + "(" + Content.dbPointTaskName + ", " + Content.dbPointName + ", " + Content.dbSpinnerTime + ", " + Content.dbPointX + ", " + Content.dbPointY + ") values ('" + mapTaskName + "','" + pointName + "','" + spinnerTime + "','" + x + "','" + y + "')");
    }

    public Cursor searchPointTask(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbPointTime, new String[]{"_id", Content.dbPointTaskName, Content.dbPointName, Content.dbSpinnerTime, Content.dbPointX, Content.dbPointY}, typeName + "=?", new String[]{typeString}, null, null, null);
        return cursor;
    }

    public Cursor searchAllPointTask() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbPointTime, new String[]{"_id", Content.dbPointTaskName, Content.dbPointName, Content.dbSpinnerTime, Content.dbPointX, Content.dbPointY}, null, null, null, null, null);
        return cursor;
    }

    public void deletePointTask(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.dbPointTime, typeName + "=?", new String[]{typeString});
        close();
    }

    public void updatePointTask(String typeName, String oldNameString, String newNameString) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbPointTime + " set " + typeName + "='" + newNameString + "' where " + typeName + "='" + oldNameString + "'");
    }


    public void close() {
        sqLiteDatabase.close();
    }

    public void reset_Db(String tab_name) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + tab_name + "");
    }

}


