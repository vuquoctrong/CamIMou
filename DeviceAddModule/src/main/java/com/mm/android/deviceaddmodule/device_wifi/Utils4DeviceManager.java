package com.mm.android.deviceaddmodule.device_wifi;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Tools related to device management
 *
 * 设备管理相关工具类
 */
public class Utils4DeviceManager {
    public static String wifiPwdFilter(String str) {

        if (TextUtils.isEmpty(str)) {
            return str;
        }

        String chinese1 = "[\u2E80-\uA4CF]";
        String chinese2 = "[\uF900-\uFAFF]";
        String chinese3 = "[\uFE30-\uFE4F]";

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(chinese1) || temp.matches(chinese2) || temp.matches(chinese3)) {
                str = str.replace(temp, "");
                return wifiPwdFilter(str);
            }
        }
        return str;
    }

    public static String replaceSpecial(String source,
                                        List<String> filterStringList) {
        for (String s : filterStringList) {
            if (source.contains(s)) {
                return source.replaceAll(s, "");
            }
        }
        return source;
    }

    // 转换从服务获取的timeInfo
    public static long convertTimeInfo(String time){
        // 服务时间格式："THHMMSS"  现转换时的格式"HHMM"
        if(time.length() == 4){
            long hour =  Long.valueOf(time.substring(0, 2));
            long min = Long.valueOf(time.substring(2, time.length()));
            return hour * 3600 + min * 60;
        }
        return  0;
    }

    public static String getServerBeginTime(int hour, int minute) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.set(Calendar.SECOND, 0);
        beginCalendar.set(Calendar.HOUR_OF_DAY, hour);
        beginCalendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        Date date = beginCalendar.getTime();
        String time = format.format(date);
        return "T" + time;
    }

    public static String getServerEndTime(int hour, int minute) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.set(Calendar.SECOND, 59);
        beginCalendar.set(Calendar.HOUR_OF_DAY, hour);
        beginCalendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        Date date = beginCalendar.getTime();
        String time = format.format(date);
        return "T" + time;
    }

    public static Calendar resolveTime(String time){
        time = time.substring(1);
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }

        return calendar;
    }
}
