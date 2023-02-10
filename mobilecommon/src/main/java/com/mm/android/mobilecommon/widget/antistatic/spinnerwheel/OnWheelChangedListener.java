package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

/**
 * Wheel changed listener interface.
 * <p>The onChanged() method is called whenever current spinnerwheel positions is changed:
 * <li> New Wheel position is set
 * <li> Wheel view is scrolled
 *
 * 滚轮改变监听器接口
 * <p>onChanged()方法在当前滚轮位置改变时被调用:
 * <li> 设置的新滚轮的位置
 * <li> 滚轮视图是滚动的
 */
public interface OnWheelChangedListener {
	/**
	 * Callback method to be invoked when current item changed
	 * @param wheel the spinnerwheel view whose state has changed
	 * @param oldValue the old value of current item
	 * @param newValue the new value of current item
	 *
	 *当当前项更改时调用的回调方法
	 * @param wheel 状态已经改变的滚轮视图
	 * @param oldValue 当前元素的旧值
	 * @param newValue 当前元素的新值
	 */
	void onChanged(AbstractWheel wheel, int oldValue, int newValue);
}
