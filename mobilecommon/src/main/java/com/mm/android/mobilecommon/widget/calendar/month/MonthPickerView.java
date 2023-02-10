package com.mm.android.mobilecommon.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;


import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.widget.calendar.day.SimpleMonthAdapter;

import java.util.List;
import java.util.Map;

public class MonthPickerView extends RecyclerView
{
    protected Context mContext;
	protected MonthSelectAdapter mAdapter;
    protected int mCurrentScrollState = 0;
	protected long mPreviousScrollPosition;
	protected int mPreviousScrollState = 0;
    private TypedArray typedArray;
    private OnScrollListener onScrollListener;

    public MonthPickerView(Context context)
    {
        this(context, null);
    }

    public MonthPickerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MonthPickerView(Context context, AttributeSet attrs, int defStyle)
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
                final MonthSelectView child = (MonthSelectView) recyclerView.getChildAt(0);
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
			mAdapter = new MonthSelectAdapter(getContext(), typedArray);
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

    public Map<String, SelectedMonth> getSelectedMonthList()
    {
        return mAdapter.getSelectedMonthList();
    }

    public void setSelectedMonthList(List<SelectedMonth> selectedMonthList)
    {
        if(selectedMonthList == null || selectedMonthList.size() > SimpleMonthAdapter.NUM_CAN_SELECT) {
            return;
        }
        mAdapter.setSelectedMonthList(selectedMonthList);
    }

    protected TypedArray getTypedArray()
    {
        return typedArray;
    }
}