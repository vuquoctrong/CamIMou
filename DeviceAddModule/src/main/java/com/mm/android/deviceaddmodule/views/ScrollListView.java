package com.mm.android.deviceaddmodule.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ScrollListView extends ListView {

	public ScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
			    MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
