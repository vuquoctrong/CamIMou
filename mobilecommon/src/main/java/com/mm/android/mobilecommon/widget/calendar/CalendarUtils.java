package com.mm.android.mobilecommon.widget.calendar;

import com.mm.android.mobilecommon.utils.TimeUtils;
import com.mm.android.mobilecommon.widget.calendar.day.CalendarDay;
import com.mm.android.mobilecommon.widget.calendar.month.SelectedMonth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils
{
    public static int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    /**
     * Get Monday of the week
     *
     * 获取本周周一
     */
    public static CalendarDay getMonday() {
	    Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == 1) {
            dayOfWeek += 7;
        }
        calendar.add(Calendar.DATE, 2 - dayOfWeek);
	    return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Get this Sunday
     *
     * 获取本周周日
     */
    public static CalendarDay getSunday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == 1) {
            dayOfWeek += 7;
        }
        calendar.add(Calendar.DATE, 2 - dayOfWeek);
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Accessed Monday of last week
     *
     * 获取上周周一
     */
    public static CalendarDay getLastWeekMonday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, -1 * 7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Accessed Sunday of last week
     *
     * 获取上周周日
     */
    public static CalendarDay getLastWeekSunday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, -1 * 7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Gets the Monday of the week of the given date
     *
     * 获取给定日期所在周的周一
     */
    public static CalendarDay getFixedMonday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayWeek == 1) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int y = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - y);


        return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Gets the Sunday of the week of the given date
     *
     * 获取给定日期所在周的周日
     */
    public static CalendarDay getFixedSunday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayWeek == 1) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int y = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - y);
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Determine if the given date is this week
     *
     * 判断给定日期是否是本周
     */
    public static boolean isThisWeek(CalendarDay calendarDay) {
        Calendar calendar = Calendar.getInstance();
        if(calendarDay.getYear() != calendar.get(Calendar.YEAR)) {
            return false;
        }

        int thisWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        calendar.set(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay());
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        return thisWeek == week;
    }

    /**
     * Determine if the given date is last week
     *
     * 判断给定日期是否是上周
     */
    public static boolean isLastWeek(CalendarDay calendarDay) {
        CalendarDay lastWeekMonday = getLastWeekMonday();
        Calendar lastWeekMondayCal = Calendar.getInstance();
        lastWeekMondayCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        lastWeekMondayCal.set(lastWeekMonday.getYear(), lastWeekMonday.getMonth(), lastWeekMonday.getDay());

        CalendarDay lastWeekSunday = getLastWeekSunday();
        Calendar lastWeekSundayCal = Calendar.getInstance();
        lastWeekSundayCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        lastWeekSundayCal.set(lastWeekSunday.getYear(), lastWeekSunday.getMonth(), lastWeekSunday.getDay());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay());

//        return !calendar.before(lastWeekMondayCal) && !calendar.after(lastWeekSundayCal);
        return isInTime(calendar.getTime(), lastWeekMondayCal.getTime(), lastWeekSundayCal.getTime(), "yyyy-MM-dd");
    }

    public static String getKeyByCalendarDay(CalendarDay calendarDay) {
        return String.valueOf(calendarDay.getYear()) + calendarDay.getMonth() + calendarDay.getDay();
    }

    public static String getKeyByYearMonthDay(int year, int month, int day) {
        return String.valueOf(year) + month + day;
    }

    public static String getKeyBySelectedMonth(SelectedMonth selectedMonth) {
        return String.valueOf(selectedMonth.getYear()) + selectedMonth.getMonth();
    }

    public static String getKeyByYearMonth(int year, int month) {
        return String.valueOf(year) + month;
    }

    /**
     * Gets the Monday of the week of the given date
     *
     * 获取给定日期所在周的周一
     */
    public static String getKeyByFixedMonday(int year, int month, int day) {
        CalendarDay calendarDay = getFixedMonday(year, month, day);

        return getKeyByYearMonthDay(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay());
    }

    /**
     * Determine if it is within a certain time frame
     *
     * 判断是否在某个时间范围之内
     */
    public static boolean isInTime(Date date, Date before, Date after, String pattern) {
        try {
            SimpleDateFormat sdf = TimeUtils.getDateFormatWithUS(pattern);
            String dateStr = sdf.format(date);
            date = sdf.parse(dateStr);
            String beforeStr = sdf.format(before);
            before = sdf.parse(beforeStr);
            String afterStr = sdf.format(after);
            after = sdf.parse(afterStr);
            return date.compareTo(before) >= 0 && date.compareTo(after) <= 0;
        }catch (Exception e) {
            return false;
        }
    }
}
