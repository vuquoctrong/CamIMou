package com.mm.android.mobilecommon.widget.linechart.adapter;

import java.text.DecimalFormat;

public class DefaultValueAdapter implements IValueAdapter {


    private DecimalFormat _formatter;
    protected int _digits = 0;

    public DefaultValueAdapter(int digits) {
        _digits = digits;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < _digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        _formatter = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public String value2String(double value) {
        return _formatter.format(value);
    }

}
