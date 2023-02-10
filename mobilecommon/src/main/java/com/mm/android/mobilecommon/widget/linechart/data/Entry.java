package com.mm.android.mobilecommon.widget.linechart.data;

public class Entry {

    double x;
    double y;

    boolean isNull_Y = false;

    public Entry(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public double getX() {
        return x;
    }

    public Entry setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public Entry setY(double y) {
        this.y = y;
        return this;
    }

    public boolean isNull_Y() {
        return isNull_Y;
    }

    public Entry setNull_Y() {
        isNull_Y = true;
        return this;
    }

    @Override
    public String toString() {
        return "entry x:" + x + " y:" + y;
    }
}
