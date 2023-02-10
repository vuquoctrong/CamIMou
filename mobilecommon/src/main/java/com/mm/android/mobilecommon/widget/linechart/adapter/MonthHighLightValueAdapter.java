package com.mm.android.mobilecommon.widget.linechart.adapter;

import java.text.DecimalFormat;

/**
 * 按月显示的报表，气泡X轴显示适配器
 *
 * Report by month, bubble X axis shows adapter
 */

public class MonthHighLightValueAdapter implements IValueAdapter {
    private DecimalFormat _formatter;

    public MonthHighLightValueAdapter() {
    }

    @Override
    public String value2String(double value) {
        return (int)value + "日";

    }
}
