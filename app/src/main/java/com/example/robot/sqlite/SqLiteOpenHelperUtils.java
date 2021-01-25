package com.example.robot.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

import com.example.robot.R;
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
        Log.d("save history : ", "insert into " + Content.tableName + "(" + Content.dbTaskMapName + ", " + Content.dbTaskName + ", " + Content.dbTime + ", " + Content.dbData + ") values ('" + mapName + "','" + taskName + "','" + time + "','" + data + "')");
    }

    public Cursor searchTaskHistory() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.tableName, new String[]{"_id", Content.dbTaskMapName, Content.dbTaskName, Content.dbTime, Content.dbData}, null, null, null, null, null);
        return cursor;
    }

    public void updateHistory(String type, String typeString, String date) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.tableName + " set " + type + "='" + typeString + "' where " + Content.dbData + "='" + date + "'");
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
        sqLiteDatabase.execSQL("update " + Content.dbTaskState + " set " + Content.dbTaskStatePointState + "='" + state + "' where " + Content.dbTaskStateMapName + "='" + mapName + "' AND " + Content.dbTaskStateTaskName + "='" + taskName +"'");
    Log.d("ZDZD : " , "update " + Content.dbTaskState + " set " + Content.dbTaskStatePointState + "='" + state + "' where " + Content.dbTaskStateMapName + "='" + mapName + "' AND " + Content.dbTaskStateTaskName + "='" + taskName +"'");
        //        ContentValues values1 = new ContentValues();
//        values1.put(Content.dbTaskStatePointState, state);
//        String[] whereArgs1 = {"#100", b.getStorage_id()};
//        String whereClause1 = DatabaseSchema.TABLE_TALKS.COLUMN_TID + "=? AND " + DatabaseSchema.TABLE_TALKS.COLUMN_STORAGEID + "=?";
//        db.update(DatabaseSchema.TABLE_TALKS.NAME, values1, whereClause1, whereArgs1);
//        db.close();

    }


    public void close() {
        sqLiteDatabase.close();
    }

    public void reset_Db(String tab_name) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + tab_name + "");
    }

}


