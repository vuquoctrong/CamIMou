package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

import android.view.View;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Recycle stored spinnerwheel items to reuse.
 *
 * 回收储存的滚轮物品以重复使用。
 */
public class WheelRecycler {


    @SuppressWarnings("unused")
    private static final String LOG_TAG = WheelRecycler.class.getName();

    // Cached items
    private List<View> items;

    // Cached empty items
    private List<View> emptyItems;

    // Wheel view
    private AbstractWheel wheel;

    /**
     * Constructor
     * @param wheel the spinnerwheel view
     *
     * 构造器
     * @param wheel 滚轮视图
     */
    public WheelRecycler(AbstractWheel wheel) {
        this.wheel = wheel;
    }

    /**
     * Recycles items from specified layout.
     * There are saved only items not included to specified range.
     * All the cached items are removed from original layout.
     * @param layout the layout containing items to be cached
     * @param firstItem the number of first item in layout
     * @param range the range of current spinnerwheel items
     * @return the new value of first item number
     *
     * 从指定的布局回收项目。
     * 只保存未包含在指定范围内的元素。从原始布局中删除所有缓存项。
     * @param layout 包含要缓存的项的布局
     * @param firstItem 布局中第一项的数量
     * @param range 当前滚轮项目的范围
     * @return 第一项编号的新值
     */
    public int recycleItems(LinearLayout layout, int firstItem, ItemsRange range) {
        int index = firstItem;
        for (int i = 0; i < layout.getChildCount();) {
            if (!range.contains(index)) {
                recycleView(layout.getChildAt(i), index);
                layout.removeViewAt(i);
                if (i == 0) { // first item
                    firstItem++;
                }
            } else {
                i++; // go to next item
            }
            index++;
        }
        return firstItem;
    }

    /**
     * Gets item view
     * @return the cached view
     *
     * 获取元素视图
     * @return 已缓存的视图
     */
    public View getItem() {
        return getCachedView(items);
    }

    /**
     * 获取空项视图
     * @return 缓存的空视图
     */
    public View getEmptyItem() {
        return getCachedView(emptyItems);
    }

    /**
     * 清空所有视图
     */
    public void clearAll() {
        if (items != null) {
            items.clear();
        }
        if (emptyItems != null) {
            emptyItems.clear();
        }
    }

    /**
     * Adds view to specified cache. Creates a cache list if it is null.
     * @param view the view to be cached
     * @param cache the cache list
     * @return the cache list
     *
     * 向指定的缓存添加视图。如果缓存列表为空，则创建一个缓存列表。
     * @param view 要缓存的视图
     * @param cache 缓存列表
     * @return 缓存列表
     */
    private List<View> addView(View view, List<View> cache) {
        if (cache == null) {
            cache = new LinkedList<>();
        }

        cache.add(view);
        return cache;
    }

    /**
     * Adds view to cache. Determines view type (item view or empty one) by index.
     * @param view the view to be cached
     * @param index the index of view
     *
     * 添加视图到缓存。根据索引确定视图类型(项视图或空视图)。
     * @param view 要缓存的视图
     * @param index 视图索引
     */
    private void recycleView(View view, int index) {
        int count = wheel.getViewAdapter().getItemsCount();

        if ((index < 0 || index >= count) && !wheel.isCyclic()) {
            // empty view
            emptyItems = addView(view, emptyItems);
        } else {
            while (index < 0) {
                index = count + index;
            }
            index %= count;
            items = addView(view, items);
        }
    }

    /**
     * Gets view from specified cache.
     * @param cache the cache
     * @return the first view from cache.
     *
     * 从指定的缓存获取视图。
     * @param cache 缓存
     * @return 缓存中的第一个视图。
     */
    private View getCachedView(List<View> cache) {
        if (cache != null && cache.size() > 0) {
            View view = cache.get(0);
            cache.remove(0);
            return view;
        }
        return null;
    }

}
