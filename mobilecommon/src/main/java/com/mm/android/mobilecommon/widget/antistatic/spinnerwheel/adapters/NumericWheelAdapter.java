package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.adapters;

import android.content.Context;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	// Values
	private int minValue;
	private int maxValue;

	// format
	private String format;

	/**
	 * Constructor
	 * @param context  the current context
	 *
	 * 构造器
	 * @param context 当前上下文
	 */
	public NumericWheelAdapter(Context context) {
		this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	/**
	 * Constructor
	 * @param context  the current context
	 * @param minValue  the spinnerwheel min value
	 * @param maxValue  the spinnerwheel max value
	 *
	 * 构造器
	 * @param context  当前上下文
	 * @param minValue  滚轮的最小值
	 * @param maxValue  滚轮的最大值
	 */
	public NumericWheelAdapter(Context context, int minValue, int maxValue) {
		this(context, minValue, maxValue, null);
	}

	/**
	 * Constructor
	 * @param context  the current context
	 * @param minValue  the spinnerwheel min value
	 * @param maxValue  the spinnerwheel max value
	 * @param format  the format string
	 *
	 * 构造器
	 * @param context  当前上下文
	 * @param minValue  滚轮的最小值
	 * @param maxValue  滚轮的最大值
	 * @param format  字符串格式
	 */
	public NumericWheelAdapter(Context context, int minValue, int maxValue,
                               String format) {
		super(context);

		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
	}

	@Override
	public CharSequence getItemText(int index) {
		if (index >= 0 && index < getItemsCount()) {
			int value = minValue + index;
			return format != null ? String.format(format, value) : Integer
					.toString(value);
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return maxValue - minValue + 1;
	}
}
