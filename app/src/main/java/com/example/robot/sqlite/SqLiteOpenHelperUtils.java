package com.example.robot.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public void saveTaskHistory(String taskName, String time, String data) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.tableName + "("+Content.dbTaskName+", "+Content.dbTime+", "+Content.dbData+") values ('" + Content.dbTaskName + "','" + Content.dbTime + "','" + Content.dbData + "')");
        sqLiteDatabase.close();
    }

    public Cursor searchTaskHistory(String taskName) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.tableName, new String[]{"_id", Content.dbTaskName, Content.dbTime, Content.dbData}, Content.dbTaskName+"=?", new String[]{Content.dbTaskName}, null, null, null);
        return cursor;
    }

    public void deleteTask(String taskName, int type) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.tableName, Content.dbTaskName+"=?", new String[]{Content.dbTaskName});
        sqLiteDatabase.close();
    }

}


