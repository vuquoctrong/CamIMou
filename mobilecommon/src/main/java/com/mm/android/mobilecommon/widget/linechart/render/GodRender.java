package com.mm.android.mobilecommon.widget.linechart.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mm.android.mobilecommon.widget.linechart.manager.MappingManager;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;


public class GodRender extends BaseRender {

    Paint _PaintRect;
    RectF _GodRect = new RectF(_rectMain);

    public GodRender(RectF _rectMain, MappingManager _MappingManager, RectF godRect) {
        super(_rectMain, _MappingManager);

        this._GodRect = godRect;

        _PaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        _PaintRect.setColor(Color.RED);
        _PaintRect.setStrokeWidth(Utils.dp2px(5));
        _PaintRect.setStyle(Paint.Style.STROKE);
    }


    public void render(Canvas canvas) {
        canvas.drawRect(_GodRect, _PaintRect);
    }


}
