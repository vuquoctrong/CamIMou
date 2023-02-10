package com.mm.android.mobilecommon.widget.linechart.utils;


/**
 * Data View
 * Constraint：
 * left must be less than right
 * bottom must be less than top
 *
 * 数据视图
 * 约定：
 * left 必须小于 right
 * bottom 必须小于 top
 */

public class RectD {

    public double left;
    public double top;
    public double right;
    public double bottom;

    public RectD() {
    }

    public RectD(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public RectD(RectD rectD) {
        setRectD(rectD);
    }

    public void setRectD(RectD rectD) {
        left = rectD.left;
        top = rectD.top;
        right = rectD.right;
        bottom = rectD.bottom;
    }

    public double width() {
        return right - left;
    }

    public double height() {
        return top - bottom;
    }


    public String toString() {
        return "RectD(" + left + ", " + top + ", "
                + right + ", " + bottom + ")";
    }
}
