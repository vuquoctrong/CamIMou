package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

/**
 * Wheel clicked listener interface.
 * <p>The onItemClicked() method is called whenever a spinnerwheel item is clicked
 * <li> New Wheel position is set
 * <li> Wheel view is scrolled
 *
 * 滚轮点击监听器接口
 * <p>onItemClicked()方法在单击纺车项时被调用
 * <li> 新滚轮位置已设置
 * <li> 滚轮视图是滚动的
 */
public interface OnWheelClickedListener {
    /**
     * Callback method to be invoked when current item clicked
     * @param wheel the spinnerwheel view
     * @param itemIndex the index of clicked item
     *
     *在单击当前项时调用的回调方法
     * @param wheel 滚轮视图
     * @param itemIndex 点击元素下标
     */
    void onItemClicked(AbstractWheel wheel, int itemIndex);
}
