package com.mm.android.mobilecommon.widget.calendar.day;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.mm.android.mobilecommon.R;

import java.util.List;
import java.util.Map;

public class DayPickerView extends RecyclerView
{
    protected Context mContext;
	protected SimpleMonthAdapter mAdapter;
    protected int mCurrentScrollState = 0;
	protected long mPreviousScrollPosition;
	protected int mPreviousScrollState = 0;
    private TypedArray typedArray;
    private OnScrollListener onScrollListener;

    public DayPickerView(Context context)
    {
        this(context, null);
    }

    public DayPickerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DayPickerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        if (!isInEditMode())
        {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayPickerView);
//            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            init(context);
        }
    }

    public void setController()
    {
        setUpAdapter();
        setAdapter(mAdapter);
    }


	public void init(Context paramContext) {
        setLayoutManager(new LinearLayoutManager(paramContext));
		mContext = paramContext;
		setUpListView();

        onScrollListener = new OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                final SimpleMonthView child = (SimpleMonthView) recyclerView.getChildAt(0);
                if (child == null) {
                    return;
                }

                mPreviousScrollPosition = dy;
                mPreviousScrollState = mCurrentScrollState;
            }
        };

	}


	protected void setUpAdapter() {
		if (mAdapter == null) {
			mAdapter = new SimpleMonthAdapter(getContext(), typedArray);
        }

		mAdapter.notifyDataSetChanged();

        scrollToPosition(mAdapter.getItemCount() - 1);
	}

	protected void setUpListView() {
		setVerticalScrollBarEnabled(false);
		setOnScrollListener(onScrollListener);
		setFadingEdgeLength(0);
        setItemViewCacheSize(15);
	}

    public Map<String, CalendarDay> getSelectedDayList()
    {
        return mAdapter.getSelectedDayList();
    }

    public void setSelectedDayList(List<CalendarDay> selectedDayList)
    {
        if(selectedDayList == null || selectedDayList.size() > SimpleMonthAdapter.NUM_CAN_SELECT) {
            return;
        }
        mAdapter.setSelectedDayList(selectedDayList);
    }

    public Map<String, SelectedDays> getSelectedWeekList()
    {
        return mAdapter.getSelectedWeekList();
    }

    public void setSelectedWeekList(List<SelectedDays> selectedWeekList)
    {
        if(selectedWeekList == null || selectedWeekList.size() > SimpleMonthAdapter.NUM_CAN_SELECT) {
            return;
        }
        mAdapter.setSelectedWeekList(selectedWeekList);
    }

    public void setModeOfDay(boolean isModeOfDay) {
        mAdapter.setIsModeOfDay(isModeOfDay);
    }

    protected TypedArray getTypedArray()
    {
        return typedArray;
    }
}