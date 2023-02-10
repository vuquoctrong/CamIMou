package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class AverageLinearLayout extends ViewGroup{

	public AverageLinearLayout(Context context) {
		super(context);
	}

	public AverageLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AverageLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = getMeasuredWidth();
		int hight = getMeasuredHeight();
		int childCount = getChildCount();
		int spaceCount = childCount - 1;
		int childTotalWidth = 0;
		for(int i = 0; i < childCount; i++){
			final View child = getChildAt(i);
			if(child.getVisibility() == View.VISIBLE){
				final int childWidth = child.getMeasuredWidth();
				childTotalWidth += childWidth;
			}else{
				spaceCount--;
			}

		}

		int space = (width - getPaddingLeft() - getPaddingRight() - childTotalWidth) / spaceCount;
		int childLeft = getPaddingLeft();

		for(int i = 0; i < childCount; i++){
			final View child = getChildAt(i);
			if(child.getVisibility() == View.VISIBLE){
				final int childWidth = child.getMeasuredWidth();
				final int childHeight = child.getMeasuredHeight();

				int childTop =(hight - getPaddingTop() - getPaddingBottom() - childHeight) / 2;

				if(i != childCount -1){
					child.layout(childLeft ,childTop , childLeft  + childWidth, childTop + childHeight);

					childLeft += space;

				}else{
					child.layout(r - getPaddingRight() - childWidth ,childTop , r - getPaddingRight(), childTop + childHeight);
				}

				childLeft += childWidth;
			}
		}


	}



}
