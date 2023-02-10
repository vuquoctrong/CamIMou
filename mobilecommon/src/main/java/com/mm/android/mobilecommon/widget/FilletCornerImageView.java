package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.mm.android.mobilecommon.R;


/**
 * Custom ImageView controls with rounded corners and borders, as well as pressed color change
 * Modify the selection of colors and press colors, and remove the Java code dependency through the system API call, and rely on XML and attr configurations
 *
 * 自定义 ImageView 控件，实现了圆角和边框，以及按下变色
 * 修改设置选中 颜色和 按下颜色 的方法 ，通过系统api调用， 移除java代码依赖， 只需要依赖xml和attr配置
 */
public class FilletCornerImageView extends AppCompatImageView {
    // 图片的宽高
    private int width;
    private int height;
    // 圆角半径
    private int radius;
    private Path mPath;
    public FilletCornerImageView(Context context) {
        super(context);
        init(context, null);
    }

    public FilletCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FilletCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //初始化默认值
        mPath = new Path();
        radius = 16;
        // 获取控件的属性值
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FilletCornerImageView);
            radius = array.getDimensionPixelOffset(R.styleable.FilletCornerImageView_fillet_radius, radius);
            array.recycle();
        }

        setClickable(true);
        setDrawingCacheEnabled(true);
        setWillNotDraw(false);
    }
    Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {

        if(width<radius || height<radius){
            super.onDraw(canvas);
            return;
        }
        // 颜色设置
        paint.setColor(0xffffffff);
        // 抗锯齿
        paint.setAntiAlias(true);
        mPath.moveTo(radius,0);
        mPath.lineTo(width-radius,0);
        mPath.quadTo(width,0,width,radius);
        mPath.lineTo(width,height-radius);
        mPath.quadTo(width,height,width-radius,height);
        mPath.lineTo(radius,height);
        mPath.quadTo(0,height,0,height-radius);
        mPath.lineTo(0,radius);
        mPath.quadTo(0,0,radius,0);
        canvas.clipPath(mPath);
        super.onDraw(canvas);
    }

    /**
     * Overrides the onSizeChanged method of the parent class to detect change in the width and height of the control
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     *
     * 重写父类的 onSizeChanged 方法，检测控件宽高的变化
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

}
