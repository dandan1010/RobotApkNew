package com.retron.robot.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.retron.robot.receiver.AlarmReceiver;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmUtils {

    private Context mContext;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    public AlarmUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void setAlarmTime(long time, long interval, String action) {
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action);
        intent.setClass(mContext, AlarmReceiver.class);
        intent.putExtra("week", getWeek(System.currentTimeMillis()));
        mPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播
        mAlarmManager.setRepeating(AlarmManager.RTC, time, interval, mPendingIntent);
//        am.set(AlarmManager.RTC, triggerAtMillis, sender);
    }

    /**
     * 获取指定时间对应的星期
     */
    public int getWeek(long time){
        Date date = new Date(time);
        //int week = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int wek = calendar.get(Calendar.DAY_OF_WEEK);
//        switch (wek){
//            case 1:
//                week = "星期日";
//                break;
//            case 2:
//                week = "星期一";
//                break;
//            case 3:
//                week = "星期二";
//                break;
//            case 4:
//                week = "星期三";
//                break;
//            case 5:
//                week = "星期四";
//                break;
//            case 6:
//                week = "星期五";
//                break;
//            case 7:
//                week = "星期六";
//                break;
//        }
//        return week;
        return wek;
    }

    /**
     * 获取指定时间对应的时间
     *
     * @param time "HH:mm:ss"
     * @return
     */
    public String getTimeMillis(long time) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(new Date(time));
    }

    public String getTimeYear(long time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(new Date(time));
    }
    public String getTimeMonth(long time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        return dateFormat.format(new Date(time));
    }

    //时间转为时间戳
    public long stringToTimestamp(String time){
        long times = 0;
        try {
            times = (long) ((Timestamp.valueOf(time).getTime())/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(times==0){
            System.out.println("String转10位时间戳失败");
        }
        return times;

    }

}
