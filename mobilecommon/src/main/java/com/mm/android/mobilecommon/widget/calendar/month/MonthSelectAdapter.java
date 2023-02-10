package com.mm.android.mobilecommon.widget.calendar.month;

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
import com.mm.android.mobilecommon.widget.calendar.day.SimpleMonthView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonthSelectAdapter extends RecyclerView.Adapter<MonthSelectAdapter.ViewHolder> implements MonthSelectView.OnDayClickListener {
    public static final int NUM_CAN_SELECT = 5; //可选数量
    public static final int RANGE_CAN_SELECT = 36;  // 可选月范围

    private final TypedArray mTypedArray;
	private final Context mContext;
    private final Calendar mMinCalendar;
    private final Calendar mCurCalendar;

    private final Map<String, SelectedMonth> mSelectedMonthList;
    private final Set<Integer> mSelectedColors;

	public MonthSelectAdapter(Context context, TypedArray typedArray) {
        mTypedArray = typedArray;
        mContext = context;

        mMinCalendar = Calendar.getInstance();
        mMinCalendar.add(Calendar.MONTH, - RANGE_CAN_SELECT);
        mCurCalendar = Calendar.getInstance();

        mSelectedMonthList = new HashMap<>(NUM_CAN_SELECT);
        mSelectedColors = new HashSet<>(NUM_CAN_SELECT - 2);
		init();
	}

    private void init() {
        // 三种颜色
        int mSelectedColor1 = mTypedArray.getColor(R.styleable.DayPickerView_colorSelectedBg1, Color.parseColor(MonthSelectView.COLOR_SELECTED_BG1));
        int mSelectedColor2 = mTypedArray.getColor(R.styleable.DayPickerView_colorSelectedBg2, Color.parseColor(MonthSelectView.COLOR_SELECTED_BG2));
        int mSelectedColor3 = mTypedArray.getColor(R.styleable.DayPickerView_colorSelectedBg3, Color.parseColor(MonthSelectView.COLOR_SELECTED_BG3));
        mSelectedColors.add(mSelectedColor1);
        mSelectedColors.add(mSelectedColor2);
        mSelectedColors.add(mSelectedColor3);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        final MonthSelectView simpleMonthView = new MonthSelectView(mContext, mTypedArray);
        return new ViewHolder(simpleMonthView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        final MonthSelectView v = viewHolder.simpleMonthView;
        final HashMap<String, Integer> drawingParams = new HashMap<>();

        int year = mMinCalendar.get(Calendar.YEAR) + position;

        v.reuse();

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, mMinCalendar.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.setSelectedMonthList(mSelectedMonthList);
        v.setSelectedColors(mSelectedColors);
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
        int itemCount = (mCurCalendar.get(Calendar.YEAR) - mMinCalendar.get(Calendar.YEAR)) + 1;

        return itemCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final MonthSelectView simpleMonthView;

        public ViewHolder(View itemView, MonthSelectView.OnDayClickListener onDayClickListener)
        {
            super(itemView);
            simpleMonthView = (MonthSelectView) itemView;
            simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            simpleMonthView.setClickable(true);
            simpleMonthView.setOnDayClickListener(onDayClickListener);
        }
    }

	public void onDayClick(MonthSelectView simpleMonthView, SelectedMonth selectedMonth) {
		if (selectedMonth != null) {
            setSelectedMonth(selectedMonth);
        }
	}


	private void setSelectedMonth(SelectedMonth selectedMonth) {

            if (selectedMonth.isThisMonth() || selectedMonth.isLastMonth()) {
                return;
            }

            if (mSelectedMonthList.containsKey(CalendarUtils.getKeyBySelectedMonth(selectedMonth))) {
                mSelectedMonthList.remove(CalendarUtils.getKeyBySelectedMonth(selectedMonth));
            } else {
                if (mSelectedMonthList.size() < NUM_CAN_SELECT) {
                    mSelectedMonthList.put(CalendarUtils.getKeyBySelectedMonth(selectedMonth), selectedMonth);
                }
            }

		notifyDataSetChanged();
	}



    public Map<String, SelectedMonth> getSelectedMonthList()
    {
        return mSelectedMonthList;
    }

    public void setSelectedMonthList(List<SelectedMonth> selectedMonthList)
    {
        for(SelectedMonth selectedMonth : selectedMonthList) {
            if(selectedMonth != null && !mSelectedMonthList.containsKey(CalendarUtils.getKeyBySelectedMonth(selectedMonth)) && mSelectedMonthList.size() < NUM_CAN_SELECT) {

                if(!selectedMonth.isThisMonth() && !selectedMonth.isLastMonth()) {
                    Iterator<Integer> it = mSelectedColors.iterator();
                    if (it.hasNext()) {
                        selectedMonth.setColor(it.next());
                        it.remove();
                    }
                }
                mSelectedMonthList.put(CalendarUtils.getKeyBySelectedMonth(selectedMonth), selectedMonth);
            }
        }
    }


}