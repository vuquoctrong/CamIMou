package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.adapters;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * Wheel items adapter interface
 *
 * 车轮物品适配器接口
 */
public interface WheelViewAdapter {
    /**
     * Gets items count
     * @return the count of spinnerwheel items
     *
     * 得到元素个数
     * @return  纺车项的个数
     */
    public int getItemsCount();

    /**
     * Get a View that displays the data at the specified position in the data set
     * @param index the item index
     * @param convertView the old view to reuse if possible
     * @param parent the parent that this view will eventually be attached to
     * @return the spinnerwheel item View
     *
     * 获取一个视图，该视图显示数据集中指定位置的数据
     * @param index 元素小标
     * @param convertView 旧的视图要尽可能重用
     * @param parent 该视图最终将附加到的父对象
     * @return 纺车项视图
     */
    public View getItem(int index, View convertView, ViewGroup parent);

    /**
     * Get a View that displays an empty spinnerwheel item placed before the first or after
     * the last spinnerwheel item.
     * @param convertView the old view to reuse if possible
     * @param parent the parent that this view will eventually be attached to
     * @return the empty item View
     *
     * 获取一个视图，该视图显示位于第一个或之后的空纺车项
     * 最后一个纺车项
     * @param convertView 旧的视图要尽可能重用
     * @param parent 该视图最终将附加到的父对象
     * @return 空项视图
     */
    public View getEmptyItem(View convertView, ViewGroup parent);

    /**
     * Register an observer that is called when changes happen to the data used by this adapter.
     * @param observer the observer to be registered
     *
     * 注册一个观察器，当此适配器使用的数据发生更改时调用该观察器。
     * @param observer 要注册的观察员
     */
    public void registerDataSetObserver(DataSetObserver observer);

    /**
     * Unregister an observer that has previously been registered
     * @param observer the observer to be unregistered
     *
     * 取消先前已注册的观察员的注册
     * @param observer 观察员未注册
     */
    void unregisterDataSetObserver (DataSetObserver observer);
}
