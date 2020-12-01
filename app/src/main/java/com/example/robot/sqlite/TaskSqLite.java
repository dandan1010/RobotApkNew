package com.example.robot.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TaskSqLite extends SQLiteOpenHelper {
    public static final String tableName = "taskHistory";
    public static final String dbName = "taskHistory";

    public TaskSqLite(@Nullable Context context) {
        super(context,
                dbName,
                null,
                1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ tableName +"(_id integer primary key autoincrement, taskName varchar(64), time varchar(100),data varchar(30))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
