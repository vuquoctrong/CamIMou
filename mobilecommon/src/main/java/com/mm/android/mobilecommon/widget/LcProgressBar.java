package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.mm.android.mobilecommon.R;


public class LcProgressBar extends ProgressBar {

    private final Paint progressPaint = new Paint();
    private Context context;
    private String content = getResources().getString(R.string.lc_demo_device_update);
    private int textColor = getResources().getColor(R.color.lc_demo_color_0B8C0D);

    public LcProgressBar(Context context) {
        this(context, null);
    }

    public LcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        progressPaint.setStrokeWidth(4);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setColor(textColor);
        progressPaint.setTextSize(dip2Px(context.getResources().getDimensionPixelSize(R.dimen.px_12)));
        progressPaint.setTextAlign(Paint.Align.CENTER);
        progressPaint.setAntiAlias(true);
    }

    private int dip2Px(int dip) {
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        progressPaint.getTextBounds(this.content, 0, this.content.length(), rect);
        int x = (getWidth() / 2) ;
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.content, x, y, this.progressPaint);
    }

    public void setText(String text) {
        this.content = text;
        postInvalidate();
    }

    public String  getText(){
        return content;
    }

    public void setTextColor(int color) {
        this.textColor = color;
        progressPaint.setColor(textColor);
        postInvalidate();
    }
}
