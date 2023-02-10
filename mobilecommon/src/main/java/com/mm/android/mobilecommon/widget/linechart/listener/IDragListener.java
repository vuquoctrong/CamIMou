package com.mm.android.mobilecommon.widget.linechart.listener;

public interface IDragListener {
    /**
     * @param xMin The minimum value on the X-axis that is visible in the current atlas
     * @param xMax The maximum value on the X-axis visible in the current atlas
     *
     * @param xMin 当前图谱中可见的x轴上的最小值
     * @param xMax 当前图谱中可见的x轴上的最大值
     */
    void onDrag(double xMin, double xMax);
}
