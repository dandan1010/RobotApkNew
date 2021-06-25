package com.retron.robotAgent.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.retron.robotAgent.content.Content;

import java.util.ArrayList;

public class SqLiteOpenHelperUtils {
    private Context mContext;
    private TaskSqLite taskSqLite;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<String> arrayList;


    public SqLiteOpenHelperUtils(Context mContext) {
        this.mContext = mContext;
        taskSqLite = new TaskSqLite(mContext, 1);
    }

    //历史任务
    public void saveTaskHistory(String mapName, String taskName, String time, String data, String startBattery, String endBattery, String index, String pointState) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.tableName + "(" + Content.dbTaskMapName + ", " + Content.dbTaskName + ", " + Content.dbTime + ", " + Content.dbData + ", " + Content.dbStartBattery + ", " + Content.dbEndBattery + ", " + Content.dbTaskIndex + ", " + Content.dbTaskPointState + ") values ('" + mapName + "','" + taskName + "','" + time + "','" + data + "','" + startBattery + "','" + endBattery + "','" + index + "','"+ pointState +"')");
    }

    public Cursor searchTaskHistory() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.tableName, new String[]{"_id", Content.dbTaskMapName, Content.dbTaskName, Content.dbTime, Content.dbData, Content.dbStartBattery, Content.dbEndBattery, Content.dbTaskIndex, Content.dbTaskPointState}, null, null, null, null, null);
        return cursor;
    }

    public Cursor searchTaskIndex() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.tableName, new String[]{"_id", Content.dbTaskMapName, Content.dbTaskName, Content.dbTaskIndex}, Content.dbTime+"='-1'", null, null, null, null);
        return cursor;
    }

    public void updateTaskIndex(String type, String typeString, String date) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.tableName + " set " + type + "='" + typeString + "' where " + Content.dbData + "='" + date + "'");
    }

    public void updateHistory(String type, String typeString, String date, String endBattery, String pointState) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.tableName + " set " + type + "='" + typeString + "'," + Content.dbEndBattery + "='" + endBattery + "'," + Content.dbTaskPointState + "='" + pointState + "' where " + Content.dbData + "='" + date + "'");
    }

    public void deleteHistory(int _id) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.tableName,"_id=?",new String[]{""+_id});
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

    public Cursor searchAlarm(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbAlarmName, new String[]{"_id", Content.dbAlarmMapTaskName, Content.dbAlarmTime, Content.dbAlarmCycle, Content.dbAlarmIsRun}, typeName + " like ?", new String[]{"%" + typeString + "%"}, null, null, null);
        return cursor;
    }

    public void updateAlarmTask(String mapTaskName, String type, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbAlarmName + " set " + type + "='" + isRun + "' where " + Content.dbAlarmMapTaskName + "='" + mapTaskName + "'");
    }

    public void updateAllAlarmTask(String type, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbAlarmName + " set " + type + "='" + isRun + "' where " + type + "='true'");
    }

    public Cursor searchAllAlarmTask() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbAlarmName, new String[]{"_id", Content.dbAlarmMapTaskName, Content.dbAlarmTime, Content.dbAlarmCycle, Content.dbAlarmIsRun}, null, null, null, null, null);
        return cursor;
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
        Cursor cursor = sqLiteDatabase.query(Content.dbPointTime,
                new String[]{"_id", Content.dbPointTaskName, Content.dbPointName, Content.dbSpinnerTime, Content.dbPointX, Content.dbPointY},
                typeName + "=?",
                new String[]{typeString},
                null,
                null,
                null);
        return cursor;
    }

    public Cursor searchAllPointTask() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbPointTime,
                new String[]{"_id", Content.dbPointTaskName, Content.dbPointName, Content.dbSpinnerTime, Content.dbPointX, Content.dbPointY},
                null,
                null,
                null,
                null,
                null);
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

    //任务点状态
    public void saveTaskState(String mapName, String taskName, String pointState, String date) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbTaskState + "(" + Content.dbTaskStateMapName + ", " + Content.dbTaskStateTaskName + ", " + Content.dbTaskStatePointState + " ," + Content.dbData + ") values ('" + mapName + "','" + taskName + "','" + pointState + "','" + date + "')");
    }

    public Cursor searchTaskState() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbTaskState, new String[]{"_id", Content.dbTaskStateMapName, Content.dbTaskStateTaskName, Content.dbTaskStatePointState}, null, null, null, null, null);
        return cursor;
    }

    public void updateState(String state, String mapName, String taskName) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbTaskState + " set " + Content.dbTaskStatePointState + "='" + state + "' where " + Content.dbTaskStateMapName + "='" + mapName + "' AND " + Content.dbTaskStateTaskName + "='" + taskName + "'");
        Log.d("ZDZD : ", "update " + Content.dbTaskState + " set " + Content.dbTaskStatePointState + "='" + state + "' where " + Content.dbTaskStateMapName + "='" + mapName + "' AND " + Content.dbTaskStateTaskName + "='" + taskName + "'");
    }

    //总任务统计
    public void saveTaskTotalCount(String taskCount, String taskTime, String area) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbTotalCount + "(" + Content.dbTaskTotalCount + ", " + Content.dbTimeTotalCount + ", " + Content.dbAreaTotalCount + ") values ('" + taskCount + "','" + taskTime + "','" + area + "')");
    }

    public Cursor searchTaskTotalCount() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbTotalCount, new String[]{"_id", Content.dbTaskTotalCount, Content.dbTimeTotalCount, Content.dbAreaTotalCount}, null, null, null, null, null);
        return cursor;
    }

    //当月任务统计
    public void saveTaskCurrentCount(String taskCount, String taskTime, String area, String date) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbCurrentCount + "(" + Content.dbTaskCurrentCount + ", " + Content.dbTimeCurrentCount + ", " + Content.dbAreaCurrentCount + ", " + Content.dbCurrentDate + ") values ('" + taskCount + "','" + taskTime + "','" + area + "','" + date + "')");
    }

    public Cursor searchTaskCurrentCount(String date) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbCurrentCount,
                new String[]{"_id", Content.dbTaskCurrentCount, Content.dbTimeCurrentCount, Content.dbAreaCurrentCount, Content.dbCurrentDate},
                Content.dbCurrentDate + "=?",
                new String[]{date},
                null,
                null,
                null);
        return cursor;
    }

    public void close() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

    public void reset_Db(String tab_name) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + tab_name + "");
    }

}


