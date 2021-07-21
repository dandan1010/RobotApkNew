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
    public void saveTaskHistory(String mapName, String taskName, String time, String data, String startBattery, String endBattery, String index, String pointState, String currentMap) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbTaskHistory + "(" + Content.dbTaskMapName + ", " + Content.dbTaskName + ", " + Content.dbTime + ", " + Content.dbData + ", " + Content.dbStartBattery + ", " + Content.dbEndBattery + ", " + Content.dbTaskIndex + ", " + Content.dbTaskPointState + ", " + Content.dbMapName + ") values ('" + mapName + "','" + taskName + "','" + time + "','" + data + "','" + startBattery + "','" + endBattery + "','" + index + "','" + pointState + "','" + currentMap + "')");
    }

    public Cursor searchTaskHistory() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbTaskHistory, new String[]{"_id", Content.dbTaskMapName, Content.dbTaskName, Content.dbTime, Content.dbData, Content.dbStartBattery, Content.dbEndBattery, Content.dbTaskIndex, Content.dbTaskPointState, Content.dbMapName}, null, null, null, null, null);
        return cursor;
    }

    public Cursor searchTaskIndex() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbTaskHistory, new String[]{"_id", Content.dbTaskMapName, Content.dbTaskName, Content.dbTaskIndex}, Content.dbTime + "='-1'", null, null, null, null);
        return cursor;
    }

    public void updateTaskIndex(String type, String typeString, String date) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbTaskHistory + " set " + type + "='" + typeString + "' where " + Content.dbData + "='" + date + "'");
    }

    public void updateHistory(String type, String typeString, String date, String endBattery, String pointState) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbTaskHistory + " set " + type + "='" + typeString + "'," + Content.dbEndBattery + "='" + endBattery + "'," + Content.dbTaskPointState + "='" + pointState + "' where " + Content.dbData + "='" + date + "'");
    }

    public void deleteHistory(int _id) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.dbTaskHistory, "_id=?", new String[]{"" + _id});
    }

    //定时任务
    public void saveAlarmTask(String mapTaskName, String time, String data, String isRun, String currentMap) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbAlarmName + "(" + Content.dbAlarmMapTaskName + ", " + Content.dbAlarmTime + ", " + Content.dbAlarmCycle + ", " + Content.dbAlarmIsRun + ", " + Content.dbMapName + ") values ('" + mapTaskName + "','" + time + "','" + data + "','" + isRun + "','" + currentMap + "')");
    }

    public Cursor searchAlarmTask(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbAlarmName, new String[]{"_id", Content.dbAlarmMapTaskName, Content.dbAlarmTime, Content.dbAlarmCycle, Content.dbAlarmIsRun, Content.dbMapName}, typeName + "=?", new String[]{typeString}, null, null, null);
        return cursor;
    }

    public Cursor searchAlarm(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbAlarmName, new String[]{"_id", Content.dbAlarmMapTaskName, Content.dbAlarmTime, Content.dbAlarmCycle, Content.dbAlarmIsRun, Content.dbMapName}, typeName + " like ?", new String[]{"%" + typeString + "%"}, null, null, null);
        return cursor;
    }

    public void updateAlarmTask(String mapTaskName, String type, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbAlarmName + " set " + type + "='" + isRun + "' where " + Content.dbAlarmMapTaskName + "='" + mapTaskName + "'");
    }

    public void updateAlarmTask(String mapTaskName, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbAlarmName + " set " + Content.dbMapName + "='" + isRun.split(Content.dbSplit)[0] + "' where " + Content.dbAlarmMapTaskName + "='" + mapTaskName + "'");

    }


    public void updateAllAlarmTask(String type, String isRun) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbAlarmName + " set " + type + "='" + isRun + "' where " + type + "='true'");
    }

    public Cursor searchAllAlarmTask() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbAlarmName, new String[]{"_id", Content.dbAlarmMapTaskName, Content.dbAlarmTime, Content.dbAlarmCycle, Content.dbAlarmIsRun, Content.dbMapName}, null, null, null, null, null);
        return cursor;
    }

    public void deleteAlarmTask(String mapTaskName) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.dbAlarmName, Content.dbAlarmMapTaskName + "=?", new String[]{mapTaskName});
        close();
    }

    public void deleteAlarm(String mapName) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.dbAlarmName, Content.dbAlarmMapTaskName + " like ? ", new String[]{"%" + mapName + "%"});
        close();
    }

    //点时间
    public void savePointTask(String mapTaskName, String pointName, String spinnerTime, String x, String y, String currentMap) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbPointTime + "(" + Content.dbPointTaskName + ", " + Content.dbPointName + ", " + Content.dbSpinnerTime + ", " + Content.dbPointX + ", " + Content.dbPointY + ", " + Content.dbMapName + ") values ('" + mapTaskName + "','" + pointName + "','" + spinnerTime + "','" + x + "','" + y + "','" + currentMap + "')");
    }

    public Cursor searchPointTask(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbPointTime,
                new String[]{"_id", Content.dbPointTaskName, Content.dbPointName, Content.dbSpinnerTime, Content.dbPointX, Content.dbPointY, Content.dbMapName},
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

    public void deletePoint(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.dbPointTime, typeName + " like ?", new String[]{"%" + typeString + "%"});
        close();
    }

    public void updatePointTask(String typeName, String oldNameString, String newNameString) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("update " + Content.dbPointTime + " set " + Content.dbMapName + "='" + newNameString.split(Content.dbSplit)[0] + "' where " + typeName + "='" + oldNameString + "'");
    }

    //任务点状态
    public void saveTaskState(String mapName, String taskName, String pointState, String date, String currentMap) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbTaskState + "(" + Content.dbTaskStateMapName + ", " + Content.dbTaskStateTaskName + ", " + Content.dbTaskStatePointState + " ," + Content.dbData + " ," + Content.dbMapName + ") values ('" + mapName + "','" + taskName + "','" + pointState + "','" + date + "','" + currentMap + "')");
    }

    public Cursor searchTaskState() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbTaskState, new String[]{"_id", Content.dbTaskStateMapName, Content.dbTaskStateTaskName, Content.dbTaskStatePointState, Content.dbMapName}, null, null, null, null, null);
        return cursor;
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

    //地图名字
    public void saveMapName(String mapNameUuid, String mapName, String db_map_md5, String db_map_link, String db_dump_md5, String db_dump_link) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into " + Content.dbMapNameDeatabase + "(" + Content.dbMapNameUuid + ", " + Content.dbMapName + ", " + Content.db_map_md5 + ", " + Content.db_map_link + ", " + Content.db_dump_md5 + ", " + Content.db_dump_link + ") values ('" + mapNameUuid + "','" + mapName + "','" + db_map_md5 + "','" + db_map_link + "','" + db_dump_md5 + "','" + db_dump_link + "')");
    }

    public Cursor searchMapName(String typeName, String typeString) {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbMapNameDeatabase, new String[]{"_id", Content.dbMapNameUuid, Content.dbMapName, Content.db_map_md5, Content.db_map_link, Content.db_dump_md5, Content.db_dump_link}, typeName + "=?", new String[]{typeString}, null, null, null);
        return cursor;
    }

    public void updateMapName(String type, String oldMapUuid, String newMapName) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        Log.d("updateMapName : ", "update " + Content.dbMapNameDeatabase + " set " + Content.dbMapName + "='" + newMapName + "' where " + type + "='" + oldMapUuid + "'");
        sqLiteDatabase.execSQL("update " + Content.dbMapNameDeatabase + " set " + Content.dbMapName + "='" + newMapName + "' where " + type + "='" + oldMapUuid + "'");
    }

    public void updateDumpMd5(String type, String oldMapUuid, String newdump) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        Log.d("updateDumpMd5 : ", "update " + Content.dbMapNameDeatabase + " set " + Content.db_dump_md5 + "='" + newdump + "' where " + type + "='" + oldMapUuid + "'");
        sqLiteDatabase.execSQL("update " + Content.dbMapNameDeatabase + " set " + Content.db_dump_md5 + "='" + newdump + "' where " + type + "='" + oldMapUuid + "'");
    }

    public Cursor searchAllMapName() {
        sqLiteDatabase = taskSqLite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Content.dbMapNameDeatabase, new String[]{"_id", Content.dbMapNameUuid, Content.dbMapName}, null, null, null, null, null);
        return cursor;
    }

    public void deleteMapName(String mapTaskName) {
        sqLiteDatabase = taskSqLite.getWritableDatabase();
        sqLiteDatabase.delete(Content.dbMapNameDeatabase, Content.dbMapNameUuid + "=?", new String[]{mapTaskName});
        close();
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


