package com.retron.robotAgent.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.retron.robotAgent.content.Content;

public class TaskSqLite extends SQLiteOpenHelper {

    public TaskSqLite(@Nullable Context context, int version) {
        super(context,
                Content.dbName,
                null,
                2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Content.dbTaskHistory + "(_id integer primary key autoincrement, " + Content.dbTaskMapName + " varchar(200), " + Content.dbMapName + " varchar(7000), " + Content.dbTaskName + " varchar(200), " + Content.dbTime + " varchar(200)," + Content.dbData + " varchar(200)," + Content.dbStartBattery + " varchar(50)," + Content.dbEndBattery + " varchar(50)," + Content.dbTaskIndex + " varchar(50)," + Content.dbTaskPointState + " varchar(7000))");
        db.execSQL("create table " + Content.dbAlarmName + "(_id integer primary key autoincrement, " + Content.dbAlarmMapTaskName + " varchar(200), " + Content.dbMapName + " varchar(7000), " + Content.dbAlarmTime + " varchar(200)," + Content.dbAlarmCycle + " varchar(200) ," + Content.dbAlarmIsRun + " varchar(200))");
        db.execSQL("create table " + Content.dbPointTime + "(_id integer primary key autoincrement, " + Content.dbPointTaskName + " varchar(200), " + Content.dbMapName + " varchar(7000), " + Content.dbPointName + " varchar(200), " + Content.dbSpinnerTime + " varchar(200)," + Content.dbPointX + " varchar(200)," + Content.dbPointY + " varchar(200))");
        db.execSQL("create table " + Content.dbTaskState + "(_id integer primary key autoincrement, " + Content.dbTaskStateMapName + " varchar(200), " + Content.dbMapName + " varchar(7000), " + Content.dbTaskStateTaskName + " varchar(200), " + Content.dbTaskStatePointState + " varchar(7000)," + Content.dbData + " varchar(200))");

        db.execSQL("create table " + Content.dbTotalCount + "(_id integer primary key autoincrement, " + Content.dbTaskTotalCount + " varchar(200), " + Content.dbTimeTotalCount + " varchar(200), " + Content.dbAreaTotalCount + " varchar(200))");
        db.execSQL("create table " + Content.dbCurrentCount + "(_id integer primary key autoincrement, " + Content.dbTaskCurrentCount + " varchar(200), " + Content.dbTimeCurrentCount + " varchar(200), " + Content.dbAreaCurrentCount + " varchar(7000)," + Content.dbCurrentDate + " varchar(200))");
        db.execSQL("create table " + Content.dbMapNameDeatabase + "(_id integer primary key autoincrement, " + Content.dbMapNameUuid + " varchar(7000), " + Content.dbMapName + " varchar(7000), " + Content.db_map_md5 + " varchar(2000) , " + Content.db_map_link + " varchar(2000), " + Content.db_dump_md5 + " varchar(2000)," + Content.db_dump_link + " varchar(2000))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion < 2) {
            //从版本1到版本2时，增加了一个字段 desc
            db.execSQL("alter table [" + Content.dbTaskHistory + "] add [desc] nvarchar(300)");
            db.execSQL("alter table [" + Content.dbAlarmName + "] add [desc] nvarchar(300)");
            db.execSQL("alter table [" + Content.dbPointTime + "] add [desc] nvarchar(300)");
            db.execSQL("alter table [" + Content.dbTaskState + "] add [desc] nvarchar(300)");
            db.execSQL("alter table [" + Content.dbTotalCount + "] add [desc] nvarchar(300)");
            db.execSQL("alter table [" + Content.dbCurrentCount + "] add [desc] nvarchar(300)");
            db.execSQL("alter table [" + Content.dbMapNameDeatabase + "] add [desc] nvarchar(300)");
        }
    }
}
