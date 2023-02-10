package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

import android.content.Context;
import android.view.MotionEvent;

public class WheelHorizontalScroller extends WheelScroller {

	/**
	 * Constructor
	 * @param context  the current context
	 * @param listener  the scrolling listener
	 *
	 * 构造器
	 * @param context  当前上下文
	 * @param listener  滚动监听器
	 */
	public WheelHorizontalScroller(Context context, ScrollingListener listener) {
		super(context, listener);
	}

	@Override
	protected int getCurrentScrollerPosition() {
		return scroller.getCurrX();
	}

	@Override
	protected int getFinalScrollerPosition() {
		return scroller.getFinalX();
	}

	@Override
	protected float getMotionEventPosition(MotionEvent event) {
		// should be overriden
		return event.getX();
	}

	@Override
	protected void scrollerStartScroll(int distance, int time) {
		scroller.startScroll(0, 0, distance, 0, time);
	}

	@Override
	protected void scrollerFling(int position, int velocityX, int velocityY) {
		final int maxPosition = 0x7FFFFFFF;
		final int minPosition = -maxPosition;
		scroller.fling(position, 0, -velocityX, 0, minPosition, maxPosition, 0,
				0);
	}
}
