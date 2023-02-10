package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.adapters;

import android.content.Context;

/**
 * The simple Array spinnerwheel adapter
 * @param <T>  the element type
 *
 * 简单的Array纺轮适配器
 * @param <T>  元素类型
 */
public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {

	// items
	private T items[];

	/**
	 * Constructor
	 * @param context  the current context
	 * @param items  the items
	 *
	 * 构造器
	 * @param context  当前上下文
	 * @param items  元素
	 */
	public ArrayWheelAdapter(Context context, T items[]) {
		super(context);

		// setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
		this.items = items;
	}

	public ArrayWheelAdapter(Context context, T items[], int itemResource, int itemTextResource){
		super(context, itemResource, itemTextResource);
		this.items = items;
	}

	@Override
	public CharSequence getItemText(int index) {
		if (index >= 0 && index < items.length) {
			T item = items[index];
			if (item instanceof CharSequence) {
				return (CharSequence) item;
			}
			return item.toString();
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return items.length;
	}
}
