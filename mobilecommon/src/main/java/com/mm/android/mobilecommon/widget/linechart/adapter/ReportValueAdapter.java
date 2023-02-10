package com.mm.android.mobilecommon.widget.linechart.adapter;

import java.text.DecimalFormat;

/**
 * 报表Y轴单位文案适配器
 * 大于1000的按照1.0k显示
 *
 * Report Y axis unit copy adapter
 * Those larger than 1000 are displayed at 1.0k
 */

public class ReportValueAdapter implements IValueAdapter {
    private DecimalFormat _formatter;

    public ReportValueAdapter() {
        _formatter = new DecimalFormat("###,###,###,###.0" + "k");
    }

    @Override
    public String value2String(double value) {
        //这里文案的长度会用来计算，负值需要处理
        value = Math.abs(value);
        if (value >= 1000){
            return _formatter.format(value / 1000);
        }else if (value < 10){
            return new DecimalFormat("0.#").format(value);
        }else {
            return new DecimalFormat("###").format(value);
        }

    }
}
