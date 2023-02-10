package com.mm.android.mobilecommon.widget.linechart.data;

import java.util.ArrayList;
import java.util.List;


public class Lines {

    private List<Line> lines = new ArrayList<>();

    /************************  x,y的范围  **********************/
    private double mYMax;
    private double mYMin;
    private double mXMax;
    private double mXMin;

    /**
     * Whether to use the minimum maximum value set
     *
     * 是否使用 设置的最小最大值
     */
    private boolean isYCustomMaxMin = false;
    /**
     * Whether to use the minimum maximum value set
     *
     * 是否使用 设置的最小最大值
     */
    private boolean isXCustomMaxMin = false;

    public Lines() {
    }

    public Lines(List<Line> lines) {
        setLines(lines);
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
        calMinMax(lines);
    }

    public void addLine(Line line) {
        lines.add(line);
        calMinMax(lines);
    }

    private void calMinMax(List<Line> lines) {

        if (!isXCustomMaxMin) {
            mXMax = Double.MIN_VALUE;
            mXMin = Double.MAX_VALUE;
        }

        if (!isYCustomMaxMin) {
//            mYMax = Double.MIN_VALUE;
            mYMax = 0;
            mYMin = Double.MAX_VALUE;
        }

        for (Line line : lines) {

            if (!isXCustomMaxMin) {
                if (line.getmXMin() < mXMin) {
                    mXMin = - line.getmXMax()/30;

                }
                if (line.getmXMax() > mXMax) {
                    mXMax = line.getmXMax()*31/30;
                }
            }

            if (!isYCustomMaxMin) {
                if (line.getmYMin() < mYMin) {
                    mYMin = line.getmYMin();

                }
                if (line.getmYMax() > mYMax) {
                    mYMax = line.getmYMax();
                }
            }
        }

        // 考虑到只有一个点的问题
        if (mXMax == mXMin) {
            double half = Math.abs(mXMax) / 2;
            mXMax += half;
            mXMin -= half;
        }

        if (mYMax == mYMin) {
            double half = Math.abs(mYMax) / 2;
            mYMax += half;
            mYMin -= half;
        }
    }

    public void calMinMax() {

        if (lines != null) {
            calMinMax(lines);
        }
    }


    public double getmYMax() {
        return mYMax;
    }

    public void setmYMax(double mYMax) {
        this.mYMax = mYMax;
    }

    public double getmYMin() {
        return mYMin;
    }

    public void setmYMin(double mYMin) {
        this.mYMin = mYMin;
    }

    public double getmXMax() {
        return mXMax;
    }

    public void setmXMax(double mXMax) {
        this.mXMax = mXMax;
    }

    public double getmXMin() {
        return mXMin;
    }

    public void setmXMin(double mXMin) {
        this.mXMin = mXMin;
    }

    public boolean isYCustomMaxMin() {
        return isYCustomMaxMin;
    }

    public void setYCustomMaxMin(boolean customMaxMin) {
        isYCustomMaxMin = customMaxMin;
    }

    public boolean isXCustomMaxMin() {
        return isXCustomMaxMin;
    }

    public void setXCustomMaxMin(boolean XCustomMaxMin) {
        isXCustomMaxMin = XCustomMaxMin;
    }
}
