package com.mm.android.mobilecommon.widget.linechart.adapter;

import java.text.DecimalFormat;

/**
 * 按周显示的报表，气泡X轴显示适配器
 *
 * Report by week, bubble X axis shows adapter
 */

public class WeekHighLightValueAdapter implements IValueAdapter {
    private DecimalFormat _formatter;

    public WeekHighLightValueAdapter() {
    }

    @Override
    public String value2String(double value) {
        String str = "";
        switch ((int)value){
            case 1:
                str = "周一";
                break;
            case 2:
                str = "周二";
                break;
            case 3:
                str = "周三";
                break;
            case 4:
                str = "周四";
                break;
            case 5:
                str = "周五";
                break;
            case 6:
                str = "周六";
                break;
            case 7:
                str = "周日";
               break;
        }
        return str;

    }
}
