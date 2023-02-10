package com.mm.android.mobilecommon.widget.calendar.day;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.widget.calendar.CalendarUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleMonthAdapter extends RecyclerView.Adapter<SimpleMonthAdapter.ViewHolder> implements SimpleMonthView.OnDayClickListener {
    public static final int MONTHS_IN_YEAR = 12;
    public static final int NUM_CAN_SELECT = 5; //可选数量
    public static final int RANGE_CAN_SELECT = 90;  // 可选日期范围

    private final TypedArray mTypedArray;
	private final Context mContext;
    private final Calendar mMinCalendar;
    private final Calendar mCurCalendar;
    private final Integer mFirstMonth;
    private final Integer mLastMonth;
    private final Map<String, CalendarDay> mSelectedDayList;
    private final Map<String, SelectedDays> mSelectedWeekList;
    private final Set<Integer> mSelectedColors;
    private boolean mIsModeOfDay;


	public SimpleMonthAdapter(Context context, TypedArray typedArray) {
        mTypedArray = typedArray;
        mContext = context;

        mMinCalendar = Calendar.getInstance();
        mMinCalendar.add(Calendar.DAY_OF_MONTH, -RANGE_CAN_SELECT);
        mCurCalendar = Calendar.getInstance();
        mFirstMonth = mMinCalendar.get(Calendar.MONTH);
        mLastMonth = (mCurCalendar.get(Calendar.MONTH)) % MONTHS_IN_YEAR;

        mSelectedDayList = new HashMap<>(NUM_CAN_SELECT);
        mSelectedWeekList = new HashMap<>(NUM_CAN_SELECT);
        mSelectedColors = new HashSet<>(NUM_CAN_SELECT - 2);
		init();
	}

    private void init() {
        mIsModeOfDay = mTypedArray.getBoolean(R.styleable.DayPickerView_modeOfDay, true);

        // 三种颜色
        int mSelectedColor1 = mTypedArray.getColor(R.styleable.DayPickerView_colorSelectedBg1, Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG1));
        int mSelectedColor2 = mTypedArray.getColor(R.styleable.DayPickerView_colorSelectedBg2, Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG2));
        int mSelectedColor3 = mTypedArray.getColor(R.styleable.DayPickerView_colorSelectedBg3, Color.parseColor(SimpleMonthView.COLOR_SELECTED_BG3));
        mSelectedColors.add(mSelectedColor1);
        mSelectedColors.add(mSelectedColor2);
        mSelectedColors.add(mSelectedColor3);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        final SimpleMonthView simpleMonthView = new SimpleMonthView(mContext, mTypedArray);
        return new ViewHolder(simpleMonthView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        final SimpleMonthView v = viewHolder.simpleMonthView;
        final HashMap<String, Integer> drawingParams = new HashMap<>();
        int month;
        int year;

        month = (mFirstMonth + (position % MONTHS_IN_YEAR)) % MONTHS_IN_YEAR;
        year = position / MONTHS_IN_YEAR + mMinCalendar.get(Calendar.YEAR) + ((mFirstMonth + (position % MONTHS_IN_YEAR)) / MONTHS_IN_YEAR);

        v.reuse();

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, mMinCalendar.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.setSelectedDayList(mSelectedDayList);
        v.setSelectedWeekList(mSelectedWeekList);
        v.setSelectedColors(mSelectedColors);
        v.setModeOfDay(mIsModeOfDay);
        v.setCurrentCalendar(mCurCalendar);
        v.setMinCalendar(mMinCalendar);

        v.invalidate();
    }

    public long getItemId(int position) {
		return position;
	}

    @Override
    public int getItemCount()
    {
        int itemCount = (((mCurCalendar.get(Calendar.YEAR) - mMinCalendar.get(Calendar.YEAR)) + 1) * MONTHS_IN_YEAR);

        itemCount -= mFirstMonth;

        itemCount -= (MONTHS_IN_YEAR - mLastMonth) - 1;

        return itemCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final SimpleMonthView simpleMonthView;

        public ViewHolder(View itemView, SimpleMonthView.OnDayClickListener onDayClickListener)
        {
            super(itemView);
            simpleMonthView = (SimpleMonthView) itemView;
            simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            simpleMonthView.setClickable(true);
            simpleMonthView.setOnDayClickListener(onDayClickListener);
        }
    }

	public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
		if (calendarDay != null) {
            setSelectedDay(calendarDay);
        }
	}
    public void onDayClick(SimpleMonthView simpleMonthView, SelectedDays selectedDays) {
        if (selectedDays != null) {
            setSelectedDay(selectedDays);
        }
    }

	private void setSelectedDay(CalendarDay calendarDay) {

            if (calendarDay.isYesterday() || calendarDay.isToday()) {
                return;
            }

            if (mSelectedDayList.containsKey(CalendarUtils.getKeyByCalendarDay(calendarDay))) {
                mSelectedDayList.remove(CalendarUtils.getKeyByCalendarDay(calendarDay));
            } else {
                if (mSelectedDayList.size() < NUM_CAN_SELECT) {
                    mSelectedDayList.put(CalendarUtils.getKeyByCalendarDay(calendarDay), calendarDay);
                }
            }

		notifyDataSetChanged();
	}

    private void setSelectedDay(SelectedDays selectedDays) {

            if(selectedDays.isThisWeek() || selectedDays.isLastWeek()) {
                return;
            }

            if (mSelectedWeekList.containsKey(CalendarUtils.getKeyByCalendarDay(selectedDays.getFirst()))) {
                mSelectedWeekList.remove(CalendarUtils.getKeyByCalendarDay(selectedDays.getFirst()));
            } else {
                if (mSelectedWeekList.size() < NUM_CAN_SELECT) {
                    mSelectedWeekList.put(CalendarUtils.getKeyByCalendarDay(selectedDays.getFirst()), selectedDays);
                }
            }

        notifyDataSetChanged();
    }

    public void setIsModeOfDay(boolean isModeOfDay) {
        this.mIsModeOfDay = isModeOfDay;
    }

    public Map<String, CalendarDay> getSelectedDayList()
    {
        return mSelectedDayList;
    }

    public void setSelectedDayList(List<CalendarDay> selectedDayList)
    {
        for(CalendarDay calendarDay : selectedDayList) {
            if(calendarDay != null && !mSelectedDayList.containsKey(CalendarUtils.getKeyByCalendarDay(calendarDay)) && mSelectedDayList.size() < NUM_CAN_SELECT) {

                if(!calendarDay.isToday() && !calendarDay.isYesterday()) {
                    Iterator<Integer> it = mSelectedColors.iterator();
                    if (it.hasNext()) {
                        calendarDay.setColor(it.next());
                        it.remove();
                    }
                }
                mSelectedDayList.put(CalendarUtils.getKeyByCalendarDay(calendarDay), calendarDay);
            }
        }
    }

    public Map<String, SelectedDays> getSelectedWeekList()
    {
        return mSelectedWeekList;
    }

    public void setSelectedWeekList(List<SelectedDays> selectedWeekList)
    {
        for(SelectedDays selectedDays : selectedWeekList) {
            if(selectedDays != null && !mSelectedWeekList.containsKey(CalendarUtils.getKeyByCalendarDay(selectedDays.getFirst())) && mSelectedWeekList.size() < NUM_CAN_SELECT) {
                if(!selectedDays.isThisWeek() && !selectedDays.isLastWeek()) {
                    Iterator<Integer> it = mSelectedColors.iterator();
                    if (it.hasNext()) {
                        selectedDays.getFirst().setColor(it.next());
                        selectedDays.getLast().setColor(selectedDays.getFirst().getColor());
                        it.remove();
                    }
                }
                mSelectedWeekList.put(CalendarUtils.getKeyByCalendarDay(selectedDays.getFirst()), selectedDays);
            }
        }
    }
}