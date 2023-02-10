package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.AttributeSet;

public class DrawableTextView extends AppCompatTextView {
    Rect mRect = new Rect();
    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Drawable[] drawables = getCompoundDrawables();
        if(drawables != null){
            Drawable drawable = drawables[0];
            if(drawable != null){
                float textwidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = drawable.getIntrinsicWidth();
                float bodyWidth = textwidth + drawablePadding + drawableWidth;
                canvas.translate((getWidth() - bodyWidth) / 2 , 0);
            } else if((drawable = drawables[1]) != null){
                 Rect rect = mRect;
                getPaint().getTextBounds(getText().toString(), 0, getText().toString().length(), rect);
                float textHeight = rect.height();
                int drawablePadding = getCompoundDrawablePadding();
                int drawableHeght = drawable.getIntrinsicHeight();
                float bodxinzaieight = textHeight + drawablePadding + drawableHeght;
                canvas.translate(0, (getHeight() - bodxinzaieight) / 2);
            }
        }

        super.onDraw(canvas);
    }
}
