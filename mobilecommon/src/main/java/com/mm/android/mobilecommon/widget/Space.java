package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class Space extends View {

    public Space(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (getVisibility() == VISIBLE) {
            setVisibility(INVISIBLE);
        }
    }

    public Space(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Space(Context context) {
        this(context, null);
    }

    /**
     * Compare to: {@link View#getDefaultSize(int, int)}
     * If mode is AT_MOST, return the child size instead of the parent size
     * (unless it is too big).
     *
     * 比较:{@link View#getDefaultSize(int, int)}
     * 如果mode为AT_MOST，则返回子大小而不是父大小(除非它太大)。
     */
    private static int getDefaultSize2(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                getDefaultSize2(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize2(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

}
