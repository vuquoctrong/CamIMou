package com.lc.media.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {
    public static Date string2Date(String time) {
        try {
            String str = time;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date) sdf.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String dateFormatTiming(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getTimeHMS(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(time);
        String date1 = format1.format(date);
        return date1;
    }

    public static long parseMills(String dateTime) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String format(long time) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;
        int day = (int) (time / dd);
        int hour = (int) ((time - day * dd) / hh);
        int minute = (int) ((time - day * dd - hour * hh) / mi);
        int second = (int) ((time - day * dd - hour * hh - minute * mi) / ss);
        String strHour;
        if (hour < 10) {
            strHour = "0" + hour;
        } else {
            strHour = "" + hour;
        }
        String strMinute;
        if (minute < 10) {
            strMinute = "0" + minute;
        } else {
            strMinute = "" + minute;
        }
        String strSecond;
        if (second < 10) {
            strSecond = "0" + second;
        } else {
            strSecond = "" + second;;
        }
        return strHour + ":" + strMinute + ":" + strSecond;
    }
}
