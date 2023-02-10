package com.mm.android.mobilecommon.widget.linechart.utils;

/**
 * For performance reasons, the singleton pattern is used
 * Allocate as little memory as possible
 *
 * 考虑到性能的原因，采用单例模式
 * 尽量少的分配内存
 */

public class SingleD_XY {
    private double x;
    private double y;

    private static SingleD_XY value;

    private SingleD_XY() {

    }

    public synchronized static SingleD_XY getInstance() {
        if (value == null) {
            value = new SingleD_XY();
        }
        return value;
    }

    public double getX() {
        return x;
    }

    public SingleD_XY setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public SingleD_XY setY(double y) {
        this.y = y;
        return this;
    }

    @Override
    public String toString() {
        return "U_XY  x: " + x + " y:" + y;
    }
}
