package com.example.robot.utils;

import android.content.Context;
import android.util.Log;

import com.example.robot.content.Content;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static final String TAG = "TimeUtils";
    private Context mContext;

    public TimeUtils(Context mContext) {
        this.mContext = mContext;
    }

    public String calculateDays(long date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(date);//获取当前时间
        String str = formatter.format(curDate);
        long days = 0, hours = 0, minutes = 0, second = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        try {
            d1 = df.parse(str);
            Date d2 = new Date(System.currentTimeMillis());
            long diff = d1.getTime() - d2.getTime();
            days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            second = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (hours <= 0 && minutes <= 0 && second <= 0) {
            Log.d(TAG, "TIME completeFlag  " + Content.completeFlag);
            Content.completeFlag = true;
            return "杀毒完成";
        } else {
            Log.d(TAG, "TIME completeFlag  " + Content.completeFlag);
            Content.completeFlag = false;
        }
        Log.d(TAG, "TIME completeFlag  : " + hours + "小时" + minutes + "分" + second + "秒");
        return hours + "小时" + minutes + "分" + second + "秒";
    }
}
