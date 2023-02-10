package com.mm.android.mobilecommon.widget.linechart.utils;

import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextUtils;


public class Utils {

    public static float dp2px(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }
 public static int sp2px(float spValue)
    {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }    
    public static float textWidth(Paint paint, String txt) {
        return paint.measureText(txt);
    }

    private static Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();

    public static int textHeightAsc(Paint paint) {
        paint.getFontMetrics(mFontMetrics);
        return (int) (Math.abs(mFontMetrics.ascent));
    }

    public static int textHeight(Paint paint) {
        paint.getFontMetrics(mFontMetrics);
        return (int) (Math.abs(mFontMetrics.ascent) + Math.abs(mFontMetrics.descent));
    }

    /**
     * Turn messy numbers into milestones。
     * @param number
     *
     * 将杂乱的数字变成里程碑。
     * @param number
     */
    public static float roundNumber2One(double number) {
        if (Double.isInfinite(number) ||
                Double.isNaN(number) ||
                number == 0.0)
            return 0;

        final float d = (float) Math.ceil((float) Math.log10(number < 0 ? -number : number));//有几位？
        final int pw = 1 - (int) d;
        final float magnitude = (float) Math.pow(10, pw);
        final long shifted = Math.round(number * magnitude);
        return shifted / magnitude;
    }

    /**
     * Check whether the URL starts with http:// or https://. If not, add http://
     *
     * 检测URL是否以http://或者https://开头，不是的话加上http://
     */
    public static String checkURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }


}
