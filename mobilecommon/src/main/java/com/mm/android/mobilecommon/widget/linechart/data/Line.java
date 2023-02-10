package com.mm.android.mobilecommon.widget.linechart.data;

import android.graphics.Color;

import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class Line {

    private List<Entry> entries = new ArrayList<>();


    private double mYMax = Double.MIN_VALUE;
    private double mYMin = Double.MAX_VALUE;
    private double mXMax = Double.MIN_VALUE;
    private double mXMin = Double.MAX_VALUE;

    private int lineColor = Color.BLACK;
    private int lineWidth = (int) Utils.dp2px(1); //px
    private int circleColor = Color.RED;
    private int circleR = 2;

    private boolean isFilled = false;
    private int lineAlpha = 50;

    private boolean isEnable = true;
    private boolean isDrawCircle = true;
    private boolean isDrawLegend = false;
    private int legendWidth = 50;
    private int legendHeight = 17;
    private int legendTextSize = 8;
    private String name = "line";

    private CallBack_OnEntryClick onEntryClick;

    public Line() {
        this(null);
    }

    public Line(List<Entry> entries) {

        circleR = (int) Utils.dp2px(circleR);
        legendWidth = (int) Utils.dp2px(legendWidth);
        legendTextSize = (int) Utils.dp2px(legendTextSize);
        legendHeight = (int) Utils.dp2px(legendHeight);

        if (entries != null) {
            setEntries(entries);
        }
    }

    private void calMinMax(List<Entry> entries) {

        mYMax = -Double.MAX_VALUE;
        mYMin = Double.MAX_VALUE;
        mXMax = -Double.MAX_VALUE;
        mXMin = Double.MAX_VALUE;

        for (Entry entry : entries) {

            if (entry.getX() < mXMin) {
                mXMin = entry.getX();
            }
            if (entry.getX() > mXMax) {
                mXMax = entry.getX();
            }

            if (entry.getY() < mYMin) {
                mYMin = entry.getY();
            }
            if (entry.getY() > mYMax) {
                mYMax = entry.getY();
            }
        }
    }

    /**
     * Add data to the front of the array
     * @param entry
     *
     * 在数组的前面添加数据
     * @param entry
     */
    public void addEntryInHead(Entry entry) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(0, entry);

        // 计算最大最小
        if (entry.getX() < mXMin) {
            mXMin = entry.getX();
        }
        if (entry.getX() > mXMax) {
            mXMax = entry.getX();
        }

        if (entry.getY() < mYMin) {
            mYMin = entry.getY();
        }
        if (entry.getY() > mYMax) {
            mYMax = entry.getY();
        }
    }

    public void addEntry(Entry entry) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(entry);

        // 计算最大最小
        if (entry.getX() < mXMin) {
            mXMin = entry.getX();
        }
        if (entry.getX() > mXMax) {
            mXMax = entry.getX();
        }

        if (entry.getY() < mYMin) {
            mYMin = entry.getY();
        }
        if (entry.getY() > mYMax) {
            mYMax = entry.getY();
        }
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        // check
        if (entries == null) {
            throw new IllegalArgumentException("entries must be no null");
        }

        this.entries = entries;

        calMinMax(entries);
    }


    /**
     * Based on the value of x provided, find the nearest number in the list and return its index.
     * Considering the performance reasons, binary search method is used. (Very fast, not bad!)
     *
     * 根据提供的 x数值，找出list中离它最近的数，返回其下标。
     * 考虑到性能的原因，采用二分查找法。(速度很快，不错！)
     */
    public static int getEntryIndex(List<Entry> entries, double hitValue, Rounding rounding) {

        if (entries == null || entries.isEmpty())
            return -1;

        int low = 0;
        int high = entries.size() - 1;
        int closet = low;

        while (low < high) {
            int m = (low + high) / 2;

            double fm = entries.get(m).getX();// middle
            double fr = entries.get(m + 1).getX();// right

            if (hitValue >= fm && hitValue <= fr) {

                // 中_右
                double d1 = Math.abs(hitValue - fm);
                double d2 = Math.abs(hitValue - fr);

                if (d1 <= d2) {
                    closet = m;
                } else {
                    closet = m + 1;
                }

                low = high = closet;
                break;

            } else if (hitValue < fm) {

                if (m >= 1) {
                    double fl = entries.get(m - 1).getX();// left

                    if (hitValue >= fl && hitValue <= fm) {
                        // 中_左
                        double d0 = Math.abs(hitValue - fl);
                        double d1 = Math.abs(hitValue - fm);

                        if (d0 <= d1) {
                            closet = m - 1;
                        } else {
                            closet = m;
                        }

                        low = high = closet;
                        break;
                    }
                }

                high = m - 1;
                closet = high;
            } else if (hitValue > fr) {
                low = m + 1;
                closet = low;
            }
        }

        // trick
        high++;
        low--;
        closet = Math.min(Math.max(closet, 0), entries.size() - 1);

        int result = low;
        if (rounding == Rounding.UP) {
            result = Math.min(high, entries.size() - 1);//多一个
        } else if (rounding == Rounding.DOWN) {
            result = Math.max(low, 0);//少一个
        } else if (rounding == Rounding.CLOSEST) {
            result = closet;
        }
        return result;
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

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isDrawCircle() {
        return isDrawCircle;
    }

    public void setDrawCircle(boolean drawCircle) {
        isDrawCircle = drawCircle;
    }

    public int getLegendHeight() {
        return legendHeight;
    }

    public void setLegendHeight(int legendHeight) {
        this.legendHeight = legendHeight;
    }

    public int getLegendTextSize() {
        return legendTextSize;
    }

    public void setLegendTextSize(int legendTextSize) {
        this.legendTextSize = legendTextSize;
    }

    public int getLegendWidth() {
        return legendWidth;
    }

    public void setLegendWidth(int legendWidth) {
        this.legendWidth = legendWidth;
    }

    public boolean isDrawLegend() {
        return isDrawLegend;
    }

    public void setDrawLegend(boolean drawLegend) {
        isDrawLegend = drawLegend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCircleR() {
        return circleR;
    }

    public void setCircleR(int circleR) {
        this.circleR = circleR;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public CallBack_OnEntryClick getOnEntryClick() {
        return onEntryClick;
    }

    public void setOnEntryClick(CallBack_OnEntryClick onEntryClick) {
        this.onEntryClick = onEntryClick;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public int getLineAlpha() {
        return lineAlpha;
    }

    public void setLineAlpha(int lineAlpha) {
        this.lineAlpha = lineAlpha;
    }

    public enum Rounding {
        UP,
        DOWN,
        CLOSEST,
    }

    public interface CallBack_OnEntryClick {
        void onEntry(Line line, Entry entry);
    }

}
