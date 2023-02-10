package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

/**
 * Wheel scrolled listener interface.
 *
 * 滚轮滚动监听器接口
 */
public interface OnWheelScrollListener {
	/**
	 * Callback method to be invoked when scrolling started.
	 * @param wheel the spinnerwheel view whose state has changed.
	 *
	 * 在开始滚动时调用的回调方法。
	 * @param wheel 状态已经改变的滚轮视图。
	 */
	void onScrollingStarted(AbstractWheel wheel);
	
	/**
	 * Callback method to be invoked when scrolling ended.
	 * @param wheel the spinnerwheel view whose state has changed.
	 *
	 * 在滚动结束时调用的回调方法。
	 * @param wheel 状态已经改变的滚轮视图。
	 */
	void onScrollingFinished(AbstractWheel wheel);
}
