package com.mm.android.mobilecommon.widget.slidinguplayout;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Helper class for determining the current scroll positions for scrollable views. Currently works
 * for ListView, ScrollView and RecyclerView, but the library users can override it to add support
 * for other views.
 *
 * Helper类，用于确定可滚动视图的当前滚动位置。目前适用于ListView, ScrollView和RecyclerView，但库用户可以覆盖它来添加对其他视图的支持。
 */
public class ScrollableViewHelper {
    /**
     * Returns the current scroll position of the scrollable view. If this method returns zero or
     * less, it means at the scrollable view is in a position such as the panel should handle
     * scrolling. If the method returns anything above zero, then the panel will let the scrollable
     * view handle the scrolling
     * @param scrollableView the scrollable view
     * @param isSlidingUp whether or not the panel is sliding up or down
     * @return the scroll position
     *
     * 返回可滚动视图的当前滚动位置。如果此方法返回0或更少，
     * 则意味着可滚动视图处于面板应该处理滚动的位置。如果该方法返回大于零的值，那么面板将让可滚动视图处理滚动
     * @param scrollableView 可滚动视图
     * @param isSlidingUp 面板是否向上或向下滑动
     * @return 滚动位置
     */
    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (scrollableView == null) return 0;
        if (scrollableView instanceof ScrollView) {
            if (isSlidingUp) {
                return scrollableView.getScrollY();
            } else {
                ScrollView sv = ((ScrollView) scrollableView);
                View child = sv.getChildAt(0);
                return (child.getBottom() - (sv.getHeight() + sv.getScrollY()));
            }
        } else if (scrollableView instanceof ListView && ((ListView) scrollableView).getChildCount() > 0) {
            ListView lv = ((ListView) scrollableView);
            if (lv.getAdapter() == null) return 0;
            if (isSlidingUp) {
                View firstChild = lv.getChildAt(0);
                // Approximate the scroll position based on the top child and the first visible item
                return lv.getFirstVisiblePosition() * firstChild.getHeight() - firstChild.getTop();
            } else {
                View lastChild = lv.getChildAt(lv.getChildCount() - 1);
                // Approximate the scroll position based on the bottom child and the last visible item
                return (lv.getAdapter().getCount() - lv.getLastVisiblePosition() - 1) * lastChild.getHeight() + lastChild.getBottom() - lv.getBottom();
            }
        } else if (scrollableView instanceof RecyclerView && ((RecyclerView) scrollableView).getChildCount() > 0) {
            RecyclerView rv = ((RecyclerView) scrollableView);
            RecyclerView.LayoutManager lm = rv.getLayoutManager();
            if (rv.getAdapter() == null) return 0;
            if (isSlidingUp) {
                View firstChild = rv.getChildAt(0);
                // Approximate the scroll position based on the top child and the first visible item
                return rv.getChildLayoutPosition(firstChild) * lm.getDecoratedMeasuredHeight(firstChild) - lm.getDecoratedTop(firstChild);
            } else {
                View lastChild = rv.getChildAt(rv.getChildCount() - 1);
                // Approximate the scroll position based on the bottom child and the last visible item
                return (rv.getAdapter().getItemCount() - 1) * lm.getDecoratedMeasuredHeight(lastChild) + lm.getDecoratedBottom(lastChild) - rv.getBottom();
            }
        } else {
            return 0;
        }
    }
}
