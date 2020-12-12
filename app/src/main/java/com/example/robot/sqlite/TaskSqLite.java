package com.example.robot.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.robot.utils.Content;

public class TaskSqLite extends SQLiteOpenHelper {

    public TaskSqLite(@Nullable Context context) {
        super(context,
                Content.dbName,
                null,
                1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Content.tableName + "(_id integer primary key autoincrement, " + Content.dbTaskMapName + " varchar(50), " + Content.dbTaskName + " varchar(50), " + Content.dbTime + " varchar(50)," + Content.dbData + " varchar(50))");
        db.execSQL("create table " + Content.dbAlarmName + "(_id integer primary key autoincrement, " + Content.dbAlarmMapTaskName + " varchar(50), " + Content.dbAlarmTime + " varchar(100)," + Content.dbAlarmCycle + " varchar(200) ," + Content.dbAlarmIsRun + " varchar(200))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
