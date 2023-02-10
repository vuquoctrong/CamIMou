package com.mm.android.mobilecommon.widget.linechart.adapter;


public class DefaultHighLightValueAdapter implements IValueAdapter {

    public DefaultHighLightValueAdapter() {

    }

    @Override
    public String value2String(double value) {
        return (int)value + "";
    }

}
