package com.mm.android.mobilecommon.widget.linechart.model;

import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

public class YAxis extends Axis {

    public static final float AREA_UNIT = 14;// unit 区域的高
    public static final float AREA_LABEL = 14;// label 区域的高


    public YAxis() {
        super();

        labelDimen = Utils.dp2px(AREA_LABEL);
        unitDimen = Utils.dp2px(AREA_UNIT);
    }

}
