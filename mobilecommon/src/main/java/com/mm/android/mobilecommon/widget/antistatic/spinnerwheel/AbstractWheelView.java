package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import androidx.annotation.Keep;
import android.util.AttributeSet;
import com.mm.android.mobilecommon.R;


/**
 * Abstract spinner spinnerwheel view.
 * This class should be subclassed.
 *
 * 滚轮抽象视图。
 * 这个类应该是子类。
 */
public abstract class AbstractWheelView extends AbstractWheel {

    private static int itemID = -1;

    @SuppressWarnings("unused")
    private final String LOG_TAG = AbstractWheelView.class.getName() + " #" + (++itemID);

    //----------------------------------
    //  Default properties values
    //----------------------------------

    protected static final int DEF_ITEMS_DIMMED_ALPHA = 50; // 60 in ICS

    protected static final int DEF_SELECTION_DIVIDER_ACTIVE_ALPHA = 70;

    protected static final int DEF_SELECTION_DIVIDER_DIMMED_ALPHA = 255;

    protected static final int DEF_ITEM_OFFSET_PERCENT = 10;

    protected static final int DEF_ITEM_PADDING = 10;
    
    protected static final int DEF_SELECTION_DIVIDER_SIZE = 2;

    //----------------------------------
    //  Class properties
    //----------------------------------

    // configurable properties

    /**
     * The alpha of the selector spinnerwheel when it is dimmed.
     *
     * 当选择器滚轮变暗时的alpha值。
     */
    protected int mItemsDimmedAlpha;

    /**
     * The alpha of separators spinnerwheel when they are shown.
     *
     * 分离器的阿尔法滚轮时，他们显示。
     */
    protected int mSelectionDividerActiveAlpha;

    /**
     * The alpha of separators when they are is dimmed.
     *
     * 分离器的alpha是变暗的。
     */
    protected int mSelectionDividerDimmedAlpha;

    /**
     * Top and bottom items offset
     *
     * 顶部和底部的项目抵消
     */
    protected int mItemOffsetPercent;

    /**
     * Left and right padding value
     *
     * 左右填充值
     */
    protected int mItemsPadding;

    /**
     * Divider for showing item to be selected while scrolling
     *
     * 用于显示滚动时要选择的项目的分隔符
     */
    protected Drawable mSelectionDivider;

    // the rest

    /**
     * The {@link android.graphics.Paint} for drawing the selector.
     *
     * {@link android.graphics。Paint}用于绘制选择器。
     */
    protected Paint mSelectorWheelPaint;

    /**
     * The {@link android.graphics.Paint} for drawing the separators.
     *
     * {@link android.graphics。油漆}用于绘制分隔线。
     */
    protected Paint mSeparatorsPaint;

    protected Animator mDimSelectorWheelAnimator;

    protected Animator mDimSeparatorsAnimator;

    /**
     * The property for setting the selector paint.
     *
     * 用于设置选择器绘制的属性。
     */
    protected static final String PROPERTY_SELECTOR_PAINT_COEFF = "selectorPaintCoeff";

    /**
     * The property for setting the separators paint.
     *
     * 用于设置分隔符的属性。
     */
    protected static final String PROPERTY_SEPARATORS_PAINT_ALPHA = "separatorsPaintAlpha";


    protected Bitmap mSpinBitmap;
//    protected Bitmap mSeparatorsBitmap;


    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    public AbstractWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //--------------------------------------------------------------------------
    //
    //  Initiating assets and setters for paints
    //
    //--------------------------------------------------------------------------

    @Override
    protected void initAttributes(AttributeSet attrs, int defStyle) {
        super.initAttributes(attrs, defStyle);
        
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MobileCommonAbstractWheelView, defStyle, 0);
        mItemsDimmedAlpha = a.getInt(R.styleable.MobileCommonAbstractWheelView_itemsDimmedAlpha, DEF_ITEMS_DIMMED_ALPHA);
        mSelectionDividerActiveAlpha = a.getInt(R.styleable.MobileCommonAbstractWheelView_selectionDividerActiveAlpha, DEF_SELECTION_DIVIDER_ACTIVE_ALPHA);
        mSelectionDividerDimmedAlpha = a.getInt(R.styleable.MobileCommonAbstractWheelView_selectionDividerDimmedAlpha, DEF_SELECTION_DIVIDER_DIMMED_ALPHA);
        mItemOffsetPercent = a.getInt(R.styleable.MobileCommonAbstractWheelView_itemOffsetPercent, DEF_ITEM_OFFSET_PERCENT);
        mItemsPadding = a.getDimensionPixelSize(R.styleable.MobileCommonAbstractWheelView_itemsPadding, DEF_ITEM_PADDING);
        mSelectionDivider = a.getDrawable(R.styleable.MobileCommonAbstractWheelView_selectionDivider);
        a.recycle();
    }

    @Override
    protected void initData(Context context) {
        super.initData(context);

        // creating animators
        mDimSelectorWheelAnimator = ObjectAnimator.ofFloat(this, PROPERTY_SELECTOR_PAINT_COEFF, 1, 0);

        mDimSeparatorsAnimator = ObjectAnimator.ofInt(this, PROPERTY_SEPARATORS_PAINT_ALPHA,
                mSelectionDividerActiveAlpha, mSelectionDividerDimmedAlpha
        );

        // creating paints
        mSeparatorsPaint = new Paint();
        mSeparatorsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mSeparatorsPaint.setAlpha(mSelectionDividerDimmedAlpha);

        mSelectorWheelPaint = new Paint();
        mSelectorWheelPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    /**
     * Recreates assets (like bitmaps) when layout size has been changed
     * @param width New spinnerwheel width
     * @param height New spinnerwheel height
     *
     * 当布局大小改变时，重新创建资产(如位图)
     * @param width 新滚轮宽度
     * @param height 新滚轮高度
     */
    @Override
    protected void recreateAssets(int width, int height) {
    	if(mSpinBitmap == null){
    	    try {
                mSpinBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }catch (IllegalArgumentException e){
    	        e.printStackTrace();
            }

    	}
    	
//    	if(mSeparatorsBitmap == null){
//    		mSeparatorsBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//    	}
        setSelectorPaintCoeff(0);
    }

    /**
     * Sets the <code>alpha</code> of the {@link Paint} for drawing separators
     * spinnerwheel.
     * @param alpha alpha value from 0 to 255
     *
     * 设置绘制分隔符{@link Paint}的<code>alpha</code>
     * 滚轮
     * @param alpha Alpha值从0到255
     */
    @SuppressWarnings("unused")  // Called via reflection
    @Keep
    public void setSeparatorsPaintAlpha(int alpha) {
        mSeparatorsPaint.setAlpha(alpha);
        invalidate();
    }
    
    @Override
    public void removeBitmap(){
    	if(mSpinBitmap != null && !mSpinBitmap.isRecycled()){
    		mSpinBitmap.recycle();
    		mSpinBitmap = null;
    	}
//    	
//    	if(mSeparatorsBitmap != null && !mSeparatorsBitmap.isRecycled()){
//    		mSeparatorsBitmap.recycle();
//    		mSeparatorsBitmap = null;
//    	}
    }

    /**
     * Sets the <code>coeff</code> of the {@link Paint} for drawing
     * the selector spinnerwheel.
     * @param coeff Coefficient from 0 (selector is passive) to 1 (selector is active)
     *
     * 设置用于绘图的{@link Paint}的<code>系数</code>
     * 滚轮选择器
     * @param coeff 系数从0(选择器为被动)到1(选择器为主动)
     */
    @Keep
    abstract public void setSelectorPaintCoeff(float coeff);


    //--------------------------------------------------------------------------
    //
    //  Processing scroller events
    //
    //--------------------------------------------------------------------------

    @Override
    protected void onScrollTouched() {
        mDimSelectorWheelAnimator.cancel();
        mDimSeparatorsAnimator.cancel();
        setSelectorPaintCoeff(1);
        setSeparatorsPaintAlpha(mSelectionDividerActiveAlpha);
    }

    @Override
    protected void onScrollTouchedUp() {
        super.onScrollTouchedUp();
        fadeSelectorWheel(750);
        lightSeparators(750);
    }

    @Override
    protected void onScrollFinished() {
        fadeSelectorWheel(500);
        lightSeparators(500);
    }

    //----------------------------------
    //  Animating components
    //----------------------------------

    /**
     * Fade the selector spinnerwheel via an animation.
     * @param animationDuration The duration of the animation.
     *
     * 通过动画淡出选择器滚轮。
     * @param animationDuration 动画的持续时间。
     */
    private void fadeSelectorWheel(long animationDuration) {
        mDimSelectorWheelAnimator.setDuration(animationDuration);
        mDimSelectorWheelAnimator.start();
    }

    /**
     * Fade the selector spinnerwheel via an animation.
     * @param animationDuration  The duration of the animation.
     *
     * 通过动画淡出选择器滚轮。
     * @param animationDuration  动画的持续时间。
     */
    private void lightSeparators(long animationDuration) {
        mDimSeparatorsAnimator.setDuration(animationDuration);
        mDimSeparatorsAnimator.start();
    }


    //--------------------------------------------------------------------------
    //
    //  Layout measuring
    //
    //--------------------------------------------------------------------------

    /**
     * Perform layout measurements
     *
     * 执行布局测量
     */
    abstract protected void measureLayout();


    //--------------------------------------------------------------------------
    //
    //  Drawing stuff
    //
    //--------------------------------------------------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewAdapter != null && mViewAdapter.getItemsCount() > 0) {
            if (rebuildItems()) {
                measureLayout();
            }
            doItemsLayout();
            drawItems(canvas);
        }
    }

    /**
     * Draws items on specified canvas
     * @param canvas the canvas for drawing
     *
     * 在指定的画布上绘制项目
     * @param canvas 绘画用的画布
     */
    abstract protected void drawItems(Canvas canvas);
}
