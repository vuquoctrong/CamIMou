package com.mm.android.deviceaddmodule.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class DrawableCenterTextView extends TextView {
    public DrawableCenterTextView(Context context) {
        super(context);
    }

    public DrawableCenterTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables == null) {
            super.onDraw(canvas);
        }
        for (int i = 0; i < drawables.length; i++) {
            Drawable drawable = drawables[i];
            if (drawable != null) {
                if (i == 0 || i == 2) {
                    int drawableWidth = drawable.getIntrinsicWidth();
                    int drawablePadding = getCompoundDrawablePadding();
                    int textWidth = (int) getPaint().measureText(getText().toString().trim());
                    int bodyWidth = drawableWidth + drawablePadding + textWidth;
                    canvas.save();
                    canvas.translate((getWidth() - bodyWidth) / 2, 0);
                } else if (i == 1 || i == 3) {
                    int drawableHeight = drawable.getIntrinsicHeight();
                    int drawablePadding = getCompoundDrawablePadding();
                    int textHeight = (int) getPaint().measureText(getText().toString().trim());
                    int bodyHeight = textHeight + drawablePadding + drawableHeight;
                    canvas.save();
                    canvas.translate(0, (getHeight() - bodyHeight) / 2);
                }
            }
        }
        super.onDraw(canvas);
    }
}
