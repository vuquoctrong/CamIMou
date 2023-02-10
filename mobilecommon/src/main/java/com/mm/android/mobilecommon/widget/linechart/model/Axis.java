package com.mm.android.mobilecommon.widget.linechart.model;

import android.graphics.Color;

import com.mm.android.mobilecommon.widget.linechart.adapter.DefaultValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.adapter.IValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.data.Line;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

import java.util.List;

/**
 * Axis related
 * Area prior agreement, better than calculation!
 *
 * 轴线相关
 * 区域事先约定，优于计算！
 */

public abstract class Axis {

    public static final float D_AXIS_WIDTH = 2;// d 代表default
//    public static final float D_LEG_WIDTH = 5;
    public static final float D_LEG_WIDTH = 0;
    public static final float D_LABEL_TXT = 7;
    public static final float D_UNIT_TXT = 12;

    public static final int D_LABEL_COUNT = 0;// 建议label的数量,决定从外部传


    //////////////////////////  label 相关  /////////////////////////
    double[] labelValues = new double[]{};
    int labelCount = 6;
    int _labelCountAdvice = D_LABEL_COUNT;
    float labelDimen;
    int labelColor = Color.BLACK;
    float labelTextSize;



    String duration;
    CalWay calWay = CalWay.perfect;

    ///////////////////////////////  unit 相关  ////////////////////////////
    boolean _enableUnit = true;
    String _unit = "";
    float unitDimen;
    float unitTxtSize;
    int unitColor = Color.RED;

    /////////////////////////////////  轴线相关  //////////////////////////////////
    int axisColor = Color.BLACK;
    float axisWidth;
    float leg;// 轴线上的小腿（多出来的小不点：叫他小腿吧)

    /////////////////////////////////  预警线相关  ////////////////////////////////
    List<WarnLine> listWarnLins;


    boolean enable = true;// axis is enable?
    IValueAdapter _ValueAdapter;

    public Axis() {

        axisWidth = Utils.dp2px(D_AXIS_WIDTH);
        labelTextSize = Utils.dp2px(D_LABEL_TXT);
        leg = Utils.dp2px(D_LEG_WIDTH);
        unitTxtSize = Utils.dp2px(D_UNIT_TXT);

        // value adapter
        _ValueAdapter = new DefaultValueAdapter(2);
    }

    /**
     * Calculate and store: The value of each step in the visible area
     * Note: Visible area!
     *
     * 计算与存储：可见区域内的每一步的数值
     * 注意：可见区域！
     */
    public void calValues(double min, double max, Line line) {

        double range;
        range = max - min;

        if (Math.abs(max - min) == 0) {
            return;
        }

        if (calWay == CalWay.perfect) {
            // 漂亮：展现的更合理

            double rawInterval = range / (_labelCountAdvice - 1);
            // 1.以最大数值为量程
            double interval = Utils.roundNumber2One(rawInterval);//314->300
            // 2. 量程>5，则以10为单位
            double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));//100
            int intervalSigDigit = (int) (interval / intervalMagnitude);
            if (intervalSigDigit > 5) {
                interval = Math.floor(10 * intervalMagnitude);// 以10位单位 //
            }

            double first = Math.floor(min / interval) * interval;//有几个interval
            double last = Math.ceil(max / interval) * interval;

            double f;
            int n = 0;
            if (interval != 0.0) {
                for (f = first; f <= last; f += interval) {
                    ++n;
                }
            }
            labelCount = n;

            if (labelValues.length < labelCount) {
                labelValues = new double[labelCount];
            }

            f = first;
            for (int i = 0; i < n; f += interval, ++i) {

                if (f == 0.0) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0;

                labelValues[i] = (float) f;
            }


        } else if (calWay == CalWay.justAvg) {
            // 平均：达到共产主义
            labelCount = _labelCountAdvice;

            if (labelValues.length < labelCount) {
                labelValues = new double[labelCount];
            }

            double v = min;
            double interval = range / (labelCount - 1);

            labelValues[0] = min;
            for (int i = 1; i < labelCount - 1; i++) {
                v = v + interval;
                labelValues[i] = v;
            }
            labelValues[labelCount - 1] = max;
        } else if (calWay == CalWay.every) {
            // 每个：将可视范围内，这条线上的每个数据在x轴上的label都绘制出来

            if (line != null && line.getEntries().size() != 0) {
                int minIndex = Line.getEntryIndex(line.getEntries(), min, Line.Rounding.DOWN);
                int maxIndex = Line.getEntryIndex(line.getEntries(), max, Line.Rounding.UP);

                labelCount = (maxIndex - minIndex) + 1;
                if (labelValues.length < labelCount) {
                    labelValues = new double[labelCount];
                }

                int count = 0;
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (this instanceof XAxis) {
                        labelValues[count++] = line.getEntries().get(i).getX();
                    } else {
                        labelValues[count++] = line.getEntries().get(i).getY();
                    }
                }
            }

        } else if (calWay == CalWay.lc_day) {
            labelValues = new double[]{0, 4, 8,12,16,20,24};
            duration = "DAY";
        }else if (calWay == CalWay.lc_week) {
            labelValues = new double[]{0, 3, 6};
            duration = "WEEK";
        }else if (calWay == CalWay.lc_month) {
            labelValues = new double[]{0, 5, 10, 15, 20, 25, 30};
            duration = "MONTH";
        } else if (calWay == CalWay.lc_year) {
            labelValues = new double[]{0, 1, 5, 9};
            duration = "YEAR";
        }
    }

    /**
     * Distance between label and indicator to the left of the axis
     *
     * 轴线左边 label和indicator的距离
     */
    public float offsetLeft(float labelWidth, float unitHeight) {
        labelDimen = labelWidth;
        unitDimen = unitHeight;

        float sum = labelDimen;
        if (_enableUnit) {
            sum += unitDimen;
        }
        sum += leg;
        return sum;
    }

    /**
     * Distance between label and indicator at the bottom of the axis
     *
     * 轴线底部 label和indicator的距离
     */
    public float offsetBottom(float labelHeight, float unitHeight) {
        labelDimen = labelHeight;
        unitDimen = unitHeight;

        float sum;
        sum = labelDimen;
        if (_enableUnit) {
            sum += unitDimen;
        }
        sum += leg;
        return sum;
    }


    public float getLabelDimen() {
        return labelDimen;
    }

    public void setLabelDimen(float labelDimen) {
        this.labelDimen = labelDimen;
    }

    public float getUnitDimen() {
        return unitDimen;
    }

    public void setUnitDimen(float unitDimen) {
        this.unitDimen = unitDimen;
    }


    ///////////////////////////////  get set  //////////////////////////////////////


    public IValueAdapter get_ValueAdapter() {
        return _ValueAdapter;
    }

    public void set_ValueAdapter(IValueAdapter _ValueAdapter) {
        this._ValueAdapter = _ValueAdapter;
    }

    public boolean is_enableUnit() {
        return _enableUnit;
    }

    public void set_enableUnit(boolean _enableUnit) {
        this._enableUnit = _enableUnit;
    }

    public int get_labelCountAdvice() {
        return _labelCountAdvice;
    }

    public void set_labelCountAdvice(int _labelCountAdvice) {
        this._labelCountAdvice = _labelCountAdvice;
    }

    public String get_unit() {
        return _unit;
    }

    public void set_unit(String _unit) {
        this._unit = _unit;
    }

    public float getUnitTxtSize() {
        return unitTxtSize;
    }

    public void setUnitTxtSize(float unitTxtSize) {
        this.unitTxtSize = unitTxtSize;
    }

    public int getUnitColor() {
        return unitColor;
    }

    public void setUnitColor(int unitColor) {
        this.unitColor = unitColor;
    }

    public int getAxisColor() {
        return axisColor;
    }

    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    public float getAxisWidth() {
        return axisWidth;
    }

    public void setAxisWidth(float axisWidth) {
        this.axisWidth = axisWidth;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public float getLeg() {
        return leg;
    }

    public void setLeg(float leg) {
        this.leg = leg;
    }

    public float getLabelTextSize() {
        return labelTextSize;
    }

    public void setLabelTextSize(float labelTextSize) {
        this.labelTextSize = labelTextSize;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public int getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(int labelCount) {
        this.labelCount = labelCount;
    }

    public double[] getLabelValues() {
        return labelValues;
    }

    public void setLabelValues(double[] labelValues) {
        this.labelValues = labelValues;
    }

    public List<WarnLine> getListWarnLins() {
        return listWarnLins;
    }

    public void setListWarnLins(List<WarnLine> listWarnLins) {
        this.listWarnLins = listWarnLins;
    }

    public CalWay getCalWay() {
        return calWay;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    /**
     * The calculation of a bunch of labels on the axis
     * @param calWay Calculate way
     *
     * 轴线上一堆label的计算方式
     * @param calWay 计算方法
     */
    public void setCalWay(CalWay calWay) {
        this.calWay = calWay;
    }

    /**
     * The calculation of a bunch of data on an axis
     *
     * 轴线上的一堆数据的计算方式
     */
    public enum CalWay {
        /**
         * Beautiful: show more reasonable
         *
         * 漂亮：展现的更合理
         */
        perfect,
        /**
         * Average：
         *
         * 平均：
         */
        justAvg,
        /**
         * Each: Plots the label on the X-axis for each data line within visual range
         *
         * 每个：将可视范围内，这条线上的每个数据在x轴上的label都绘制出来
         */
        every,

        lc_day,
        lc_week,
        lc_month,
        lc_year,

    }
}
