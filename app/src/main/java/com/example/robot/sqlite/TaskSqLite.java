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
        db.execSQL("create table " + Content.tableName + "(_id integer primary key autoincrement, " + Content.dbTaskName + " varchar(50), " + Content.dbTime + " varchar(50)," + Content.dbData + " varchar(50))");
        //  db.execSQL("create table "+ Content.tableTaskName +"(_id integer primary key autoincrement, taskName varchar(50), time varchar(50),data varchar(50))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
