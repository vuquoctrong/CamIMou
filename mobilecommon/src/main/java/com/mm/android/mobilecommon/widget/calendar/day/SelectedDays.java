package com.mm.android.mobilecommon.widget.calendar.day;

import android.graphics.Color;

import com.mm.android.mobilecommon.widget.calendar.CalendarUtils;

import java.io.Serializable;

public class SelectedDays implements Serializable
{
    private static final long serialVersionUID = 3942549765282708376L;
    private CalendarDay mFirst;
    private CalendarDay mLast;
    private boolean mIsThisWeek;
    private boolean mIsLastWeek;

    public SelectedDays(CalendarDay first, CalendarDay last) {
        mFirst = first;
        mLast = last;
        generateVaule();
    }

    public SelectedDays(CalendarDay calendarDay) {

        this(CalendarUtils.getFixedMonday(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay()),
                CalendarUtils.getFixedSunday(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay()));
    }

    private void generateVaule() {
        mIsThisWeek = CalendarUtils.isThisWeek(mFirst);
        mIsLastWeek = CalendarUtils.isLastWeek(mLast);
        if(mIsThisWeek) {
            mFirst.setColor(Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG_THIS));
            mLast.setColor(Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG_THIS));
        } else if(mIsLastWeek) {
            mFirst.setColor(Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG_LAST));
            mLast.setColor(Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG_LAST));
        }
    }

    public boolean isThisWeek() {
        return mIsThisWeek;
    }

    public void setThisWeek(boolean thisWeek) {
        mIsThisWeek = thisWeek;
    }

    public boolean isLastWeek() {
        return mIsLastWeek;
    }

    public void setLastWeek(boolean lastWeek) {
        mIsLastWeek = lastWeek;
    }

    public CalendarDay getFirst()
    {
        return mFirst;
    }

    public void setFirst(CalendarDay first)
    {
        mFirst = first;
    }

    public CalendarDay getLast()
    {
        return mLast;
    }

    public void setLast(CalendarDay last)
    {
        mLast = last;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + mFirst.hashCode();
        result = 31 * result + mLast.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof SelectedDays)) {
            return false;
        }
        SelectedDays selectedDays = (SelectedDays)o;
        return selectedDays.getFirst().equals(mFirst)
                && selectedDays.getLast().equals(mLast);
    }
}
