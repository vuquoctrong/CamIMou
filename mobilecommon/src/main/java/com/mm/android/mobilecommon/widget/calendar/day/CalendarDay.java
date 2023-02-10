package com.mm.android.mobilecommon.widget.calendar.day;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Calendar;

public class CalendarDay implements Serializable
{
    private static final long serialVersionUID = -5456695978688356202L;
    private Calendar mCalendar;

    private int mDay;
    private int mMonth;
    private int mYear;
    private boolean mIsToday;
    private boolean mIsYesterday;
    private int mColor;

    public CalendarDay() {
        setTime(System.currentTimeMillis());
    }

    public CalendarDay(int year, int month, int day) {
        setDay(year, month, day);
    }

    private void setTime(long timeInMillis) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mCalendar.setTimeInMillis(timeInMillis);
        mMonth = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        generateValue();
    }

    public void setDay(int year, int month, int day) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mYear = year;
        mMonth = month;
        mDay = day;
        mCalendar.set(year, month, day);
        generateValue();
    }

    private void setIsToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mIsToday = (mMonth == month && mYear == year && mDay == day);
        if(mIsToday) {
            mColor = Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG_THIS);
        }
    }

    private void setIsYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mIsYesterday = (mMonth == month && mYear == year && mDay == day);
        if(mIsYesterday) {
            mColor = Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG_LAST);
        }
    }

    private void generateValue(){
        setIsToday();
        setIsYesterday();

    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        this.mDay = day;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        this.mMonth = month;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        this.mYear = year;
    }

    public boolean isToday() {
        return mIsToday;
    }

    public void setIsToday(boolean isToday) {
        this.mIsToday = isToday;
    }

    public boolean isYesterday() {
        return mIsYesterday;
    }

    public void setIsYesterday(boolean isYesterday) {
        this.mIsYesterday = isYesterday;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + mYear;
        result = 31 * result + mMonth;
        result = 31 * result + mDay;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof CalendarDay)) {
            return false;
        }
        CalendarDay calendarDay = (CalendarDay)o;
        return calendarDay.getDay() == mDay
                && calendarDay.getMonth() == mMonth
                && calendarDay.getYear() == mYear;
    }

    @Override
    public String toString()
    {
        String stringBuilder = "{ year: " +
                mYear +
                ", month: " +
                mMonth +
                ", day: " +
                mDay +
                " }";

        return stringBuilder;
    }
}
