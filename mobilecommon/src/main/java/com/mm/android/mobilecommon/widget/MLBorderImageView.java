package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
public class MLBorderImageView extends AppCompatImageView {
    public static final int TYPE_SHAPE_CIRCLE = 1;
    public static final int TYPE_SHAPE_RECT = 2;
    // 图片的宽高
    private int width;
    private int height;

    // 定义 Bitmap 的默认配置
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.RGB_565;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    // 边框颜色
    private int borderColor, borderSelectedColor,borderUnSelectedColor;
    // 边框宽度
    private int borderWidth;

    // 圆角半径
    private int radius;
    // 图片类型（矩形，圆形）
    private int shapeType;


    public MLBorderImageView(Context context) {
        super(context);
        init(context, null);
    }

    public MLBorderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MLBorderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //初始化默认值
        borderWidth = 0;
        borderColor = 0xffffff;

        radius = 16;
        shapeType = 2;

        // 获取控件的属性值
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MLBorderImageView);
            borderColor = array.getColor(R.styleable.MLBorderImageView_border_color, borderColor);
            borderUnSelectedColor = array.getColor(R.styleable.MLBorderImageView_border_color_normal, borderColor);
            borderSelectedColor = array.getColor(R.styleable.MLBorderImageView_border_color_selected, borderColor);
            borderWidth = array.getDimensionPixelOffset(R.styleable.MLBorderImageView_border_width, borderWidth);

            radius = array.getDimensionPixelOffset(R.styleable.MLBorderImageView_radius, radius);
            shapeType = array.getInteger(R.styleable.MLBorderImageView_shape_type, shapeType);
            array.recycle();
        }

        setClickable(true);
        setDrawingCacheEnabled(true);
        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (shapeType == 0) {
            super.onDraw(canvas);
            return;
        }
        // 获取当前控件的 drawable
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        // 这里 get 回来的宽度和高度是当前控件相对应的宽度和高度（在 xml 设置）
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        // 获取 bitmap，即传入 imageView 的 bitmap
        // Bitmap bitmap = ((BitmapDrawable) ((SquaringDrawable)
        // drawable).getCurrent()).getBitmap();
        // 获取 bitmap 方式，因为上边的获取会导致 Glide 加载的drawable 强转为 BitmapDrawable 出错
        Bitmap bitmap = getBitmapFromDrawable(drawable);
        drawDrawable(canvas, bitmap);

        drawBorder(canvas);
    }

    /**
     * Realize the drawing of rounded corners
     * @param canvas
     * @param bitmap
     *
     * 实现圆角的绘制
     * @param canvas
     * @param bitmap
     */
    private void drawDrawable(Canvas canvas, Bitmap bitmap) {
        // 画笔
        Paint paint = new Paint();
        // 颜色设置
        paint.setColor(0xffffffff);
        // 抗锯齿
        paint.setAntiAlias(true);
        //Paint 的 Xfermode，PorterDuff.Mode.SRC_IN 取两层图像的交集部分, 只显示上层图像。
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(0, 0, width, height, null);
        }

        if (shapeType == 1) {
            // 画遮罩，画出来就是一个和空间大小相匹配的圆（这里在半径上 -1 是为了不让图片超出边框）
            canvas.drawCircle(width / 2, height / 2, width / 2 - 1, paint);
        } else if (shapeType == 2) {
            // 当ShapeType == 2 时 图片为圆角矩形 （这里在宽高上 -1 是为了不让图片超出边框）
            RectF rectf = new RectF(1, 1, getWidth() - 1, getHeight() - 1);
            canvas.drawRoundRect(rectf, radius + 1, radius + 1, paint);
        }

        paint.setXfermode(xfermode);

        // 空间的大小 / bitmap 的大小 = bitmap 缩放的倍数
        float scaleWidth = ((float) getWidth()) / bitmap.getWidth();
        float scaleHeight = ((float) getHeight()) / bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        //bitmap 缩放
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //draw 上去
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
        bitmap.recycle();
        bitmap = null;
    }

    /**
     * Draws a custom control border
     * @param canvas
     *
     * 绘制自定义控件边框
     * @param canvas
     */
    private void drawBorder(Canvas canvas) {
        if (borderWidth > 0) {
            Paint paint = new Paint();
            paint.setStrokeWidth(borderWidth);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(borderColor);
            paint.setAntiAlias(true);
            // 根据控件类型的属性去绘制圆形或者矩形
            if (shapeType == 1) {
                canvas.drawCircle(width / 2, height / 2, (width - borderWidth) / 2, paint);
            } else if (shapeType == 2) {
                // 当ShapeType = 1 时 图片为圆角矩形
                RectF rectf = new RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2,
                        getHeight() - borderWidth / 2);
                canvas.drawRoundRect(rectf, radius, radius, paint);
            }
        }
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


    /**
     * Here is a reference to other developers to get the Bitmap content, because the image loaded by Glide is not considered before
     * The drawable type is of the SquaringDrawable type, resulting in a drawable failure
     * This is a different type of drawable that gets bitmap
     * @param drawable
     *
     * 这里是参考其他开发者获取Bitmap内容的方法， 之前是因为没有考虑到 Glide 加载的图片
     * 导致drawable 类型是属于 SquaringDrawable 类型，导致强转失败
     * 这里是通过drawable不同的类型来进行获取Bitmap
     * @param drawable
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        try {
            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                        BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setBorderColor(selected);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if(isSelected()){
            return;
        }
        setBorderColor(pressed);
    }

    private void setBorderColor(boolean selected) {
        if(selected){
            borderColor = borderSelectedColor;
        }else{
            borderColor = borderUnSelectedColor;
        }
        invalidate();
    }


    public void setRadius(int radius){
        this.radius = radius;
        invalidate();
    }

    public void setShapeType(int type){
        this.shapeType = type;
    }


}
