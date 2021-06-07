package com.retron.robot.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.retron.robot.content.Content;

public class TaskSqLite extends SQLiteOpenHelper {

    public TaskSqLite(@Nullable Context context,int version) {
        super(context,
                Content.dbName,
                null,
                version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Content.tableName + "(_id integer primary key autoincrement, " + Content.dbTaskMapName + " varchar(200), " + Content.dbTaskName + " varchar(200), " + Content.dbTime + " varchar(200)," + Content.dbData + " varchar(200)," + Content.dbStartBattery +" varchar(50)," + Content.dbEndBattery + " varchar(50)," + Content.dbTaskIndex + " varchar(50)," + Content.dbTaskPointState + " varchar(7000))");
        db.execSQL("create table " + Content.dbAlarmName + "(_id integer primary key autoincrement, " + Content.dbAlarmMapTaskName + " varchar(200), " + Content.dbAlarmTime + " varchar(200)," + Content.dbAlarmCycle + " varchar(200) ," + Content.dbAlarmIsRun + " varchar(200))");
        db.execSQL("create table " + Content.dbPointTime + "(_id integer primary key autoincrement, " + Content.dbPointTaskName + " varchar(200), " + Content.dbPointName + " varchar(200), " + Content.dbSpinnerTime + " varchar(200)," + Content.dbPointX + " varchar(200)," + Content.dbPointY + " varchar(200))");
        db.execSQL("create table " + Content.dbTaskState + "(_id integer primary key autoincrement, " + Content.dbTaskStateMapName + " varchar(200), " + Content.dbTaskStateTaskName + " varchar(200), " + Content.dbTaskStatePointState + " varchar(7000)," + Content.dbData + " varchar(200))");
        db.execSQL("create table " + Content.dbTotalCount + "(_id integer primary key autoincrement, " + Content.dbTaskTotalCount + " varchar(200), " + Content.dbTimeTotalCount + " varchar(200), " + Content.dbAreaTotalCount + " varchar(200))");
        db.execSQL("create table " + Content.dbCurrentCount + "(_id integer primary key autoincrement, " + Content.dbTaskCurrentCount + " varchar(200), " + Content.dbTimeCurrentCount + " varchar(200), " + Content.dbAreaCurrentCount + " varchar(7000)," + Content.dbCurrentDate + " varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
