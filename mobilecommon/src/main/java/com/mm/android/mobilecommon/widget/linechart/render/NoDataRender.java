package com.mm.android.mobilecommon.widget.linechart.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mm.android.mobilecommon.widget.linechart.manager.MappingManager;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

public class NoDataRender extends BaseRender {

    Paint _Paint;
    String txt = "无数据";
    int txtColor = Color.RED;
    int txtSize = 30;

    public NoDataRender(RectF _FrameManager, MappingManager _MappingManager) {
        super(_FrameManager, _MappingManager);

        _Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    
    public void render(Canvas canvas) {

        _Paint.setColor(txtColor);
        _Paint.setTextSize(txtSize);

        float x = _rectMain.centerX();
        float y = _rectMain.centerY();

        float halfW = Utils.textWidth(_Paint, txt) / 2f;
        float halfH = Utils.textHeightAsc(_Paint) / 2f;

        canvas.drawText(txt, x - halfW, y + halfH, _Paint);
    }
}
