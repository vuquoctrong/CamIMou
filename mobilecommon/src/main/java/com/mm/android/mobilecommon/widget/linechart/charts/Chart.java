package com.mm.android.mobilecommon.widget.linechart.charts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public abstract class Chart extends View {

    public Chart(Context context) {
        this(context, null, 0);
    }

    public Chart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    protected void init(Context context, AttributeSet attrs) {
        // 采用硬件加速
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = 100;
        int width = resolveSize(size, widthMeasureSpec);
        int height = resolveSize(size, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
