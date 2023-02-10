package com.mm.android.mobilecommon.widget.calendar.month;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Calendar;

public class SelectedMonth implements Serializable
{
    private static final long serialVersionUID = -5456695978688356205L;
    private Calendar mCalendar;

    private int mMonth;
    private int mYear;
    private boolean mIsThisMonth;
    private boolean mIsLastMonth;
    private int mColor;

    public SelectedMonth() {
        setTime(System.currentTimeMillis());
    }

    public SelectedMonth(int year, int month) {
        setDay(year, month);
    }

    private void setTime(long timeInMillis) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mCalendar.setTimeInMillis(timeInMillis);
        mMonth = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);
        generateValue();
    }

    public void setDay(int year, int month) {
        mYear = year;
        mMonth = month;
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mCalendar.set(year, month, 1);
        generateValue();
    }

    private void setIsThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        mIsThisMonth = (mMonth == month && mYear == year);
        if(mIsThisMonth) {
            mColor = Color.parseColor(MonthSelectView.COLOR_SELECTED_BG_THIS);
        }
    }

    private void setIsLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH, -1);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        mIsLastMonth = (mMonth == month && mYear == year);
        if(mIsLastMonth) {
            mColor = Color.parseColor(MonthSelectView.COLOR_SELECTED_BG_LAST);
        }
    }

    private void generateValue(){
        setIsThisMonth();
        setIsLastMonth();
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

    public boolean isThisMonth() {
        return mIsThisMonth;
    }

    public void setIsThisMonth(boolean isThisMonth) {
        this.mIsThisMonth = isThisMonth;
    }

    public boolean isLastMonth() {
        return mIsLastMonth;
    }

    public void setIsLastMonth(boolean isLastMonth) {
        this.mIsLastMonth = isLastMonth;
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
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof SelectedMonth)) {
            return false;
        }
        SelectedMonth calendarDay = (SelectedMonth)o;
        return calendarDay.getMonth() == mMonth
                && calendarDay.getYear() == mYear;
    }

    @Override
    public String toString()
    {
        String stringBuilder = "{ year: " +
                mYear +
                ", month: " +
                mMonth +
                " }";

        return stringBuilder;
    }
}
