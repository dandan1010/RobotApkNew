package com.example.robot.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SqLiteOpenHelperUtils {

    private static final String TAG = "SqLiteOpenHelperUtils";
    private static final String table = TaskSqLite.tableName;
    private Context mContext;
    private TaskSqLite taskSqLite;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<String> arrayList;


    public SqLiteOpenHelperUtils(Context mContext) {
        this.mContext = mContext;
        taskSqLite = new TaskSqLite(mContext);
    }

    public void saveTask(String taskName, String time, String data) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + table + "(taskName, time, data) values ('" + taskName + "','" + time + "','" + data + "')");
        sqLiteDatabase.close();
    }

    public Cursor searchTask(String taskName) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(table, new String[]{"_id", "taskName", "time", "data"}, "taskName=?", new String[]{taskName}, null, null, null);
        return cursor;
    }

    public void deleteTask(String taskName, int type) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(table, "taskName=?", new String[]{taskName});
        sqLiteDatabase.close();
    }

}


