package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.mm.android.mobilecommon.R;


/**
 * Spinner wheel vertical view.
 *
 * 转轮垂直视图。
 */
public class WheelVerticalView extends AbstractWheelView {

    private static int itemID = -1;

    @SuppressWarnings("unused")
    private final String LOG_TAG = WheelVerticalView.class.getName() + " #" + (++itemID);

    /**
     * The height of the selection divider.
     *
     * 选择分隔符的高度。
     */
    protected int mSelectionDividerHeight;

    // Cached item height
    private int mItemHeight = 0;

    //--------------------------------------------------------------------------
    //
    //  Constructors
    //
    //--------------------------------------------------------------------------

    /**
     * Create a new wheel vertical view.
     * @param context The application environment.
     *
     * 创建一个新的滚轮垂直视图。
     * @param context 应用程序环境
     */
    public WheelVerticalView(Context context) {
        this(context, null);
    }

    /**
     * Create a new wheel vertical view.
     * @param context The application environment.
     * @param attrs A collection of attributes.
     *
     * 创建一个新的滚轮垂直视图
     * @param context 应用程序环境
     * @param attrs 属性的集合
     */
    public WheelVerticalView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.abstractWheelViewStyle);
    }

    /**
     * Create a new wheel vertical view.
     * @param context the application environment.
     * @param attrs a collection of attributes.
     * @param defStyle The default style to apply to this view.
     *
     * 创建一个新的滚轮垂直视图。
     * @param context 应用程序环境
     * @param attrs 属性的集合。
     * @param defStyle 应用于此视图的默认样式。
     */
    public WheelVerticalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //--------------------------------------------------------------------------
    //
    //  Initiating assets and setter for selector paint
    //
    //--------------------------------------------------------------------------

    @Override
    protected void initAttributes(AttributeSet attrs, int defStyle) {
        super.initAttributes(attrs, defStyle);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MobileCommonWheelVerticalView, defStyle, 0);
        mSelectionDividerHeight = a.getDimensionPixelSize(R.styleable.MobileCommonWheelVerticalView_selectionDividerHeight, DEF_SELECTION_DIVIDER_SIZE);
        a.recycle();
    }

    @Override
    public void setSelectorPaintCoeff(float coeff) {
        LinearGradient shader;

        int h = getMeasuredHeight();
        int ih = getItemDimension();
        float p1 = (1 - ih/(float) h)/2;
        float p2 = (1 + ih/(float) h)/2;
        float z = mItemsDimmedAlpha * (1 - coeff);
        float c1f = z + 255 * coeff;

        if (mVisibleItems == 2) {
            int c1 = Math.round( c1f ) << 24;
            int c2 = Math.round( z ) << 24;
            int[] colors =      {c2, c1, 0xff000000, 0xff000000, c1, c2};
            float[] positions = { 0, p1,     p1,         p2,     p2,  1};
            shader = new LinearGradient(0, 0, 0, h, colors, positions, Shader.TileMode.CLAMP);
        } else {
            float p3 = (1 - ih*3/(float) h)/2;
            float p4 = (1 + ih*3/(float) h)/2;

            float s = 255 * p3/p1;
            float c3f = s * coeff ; // here goes some optimized stuff
            float c2f = z + c3f;

            int c1 = Math.round( c1f ) << 24;
            int c2 = Math.round( c2f ) << 24;
            int c3 = Math.round( c3f ) << 24;

            int[] colors =      {0, c3, c2, c1, 0xff000000, 0xff000000, c1, c2, c3, 0};
            float[] positions = {0, p3, p3, p1,     p1,         p2,     p2, p4, p4, 1};
            shader = new LinearGradient(0, 0, 0, h, colors, positions, Shader.TileMode.CLAMP);
        }
        mSelectorWheelPaint.setShader(shader);
        invalidate();
    }


    //--------------------------------------------------------------------------
    //
    //  Scroller-specific methods
    //
    //--------------------------------------------------------------------------

    @Override
    protected WheelScroller createScroller(WheelScroller.ScrollingListener scrollingListener) {
        return new WheelVerticalScroller(getContext(), scrollingListener);
    }

    @Override
    protected float getMotionEventPosition(MotionEvent event) {
        return event.getY();
    }

    //--------------------------------------------------------------------------
    //
    //  Base measurements
    //
    //--------------------------------------------------------------------------

    @Override
    protected int getBaseDimension() {
        return getHeight();
    }

    /**
     * Returns height of the spinnerwheel
     * @return the item height
     *
     * 返回滚轮的高度
     * @return 元素高度
     */
    @Override
    protected int getItemDimension() {
        if (mItemHeight != 0) {
            return mItemHeight;
        }

        if (mItemsLayout != null && mItemsLayout.getChildAt(0) != null) {
            mItemHeight = mItemsLayout.getChildAt(0).getMeasuredHeight();
            return mItemHeight;
        }

        return getBaseDimension() / mVisibleItems;
    }

    //--------------------------------------------------------------------------
    //
    //  Layout creation and measurement operations
    //
    //--------------------------------------------------------------------------

    /**
     * Creates item layout if necessary
     *
     * 如果需要，创建项目布局
     */
    @Override
    protected void createItemsLayout() {
        if (mItemsLayout == null) {
            mItemsLayout = new LinearLayout(getContext());
            mItemsLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    @Override
    protected void doItemsLayout() {
        mItemsLayout.layout(0, 0, getMeasuredWidth() - 2 * mItemsPadding, getMeasuredHeight());
    }


    @Override
    protected void measureLayout() {
        mItemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mItemsLayout.measure(
                View.MeasureSpec.makeMeasureSpec(getWidth() - 2 * mItemsPadding, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        rebuildItems(); // rebuilding before measuring

        int width = calculateLayoutWidth(widthSize, widthMode);

        int height;
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = Math.max(
                    getItemDimension() * (mVisibleItems - mItemOffsetPercent / 100),
                    getSuggestedMinimumHeight()
            );

            if (heightMode == View.MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    /**
     * Calculates control width
     * @param widthSize the input layout width
     * @param mode the layout mode
     * @return the calculated control width
     *
     * 计算控制宽度
     * @param widthSize 输入布局宽度
     * @param mode 布局模式
     * @return 计算控制宽度
     */
    private int calculateLayoutWidth(int widthSize, int mode) {
        mItemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mItemsLayout.measure(
                View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int width = mItemsLayout.getMeasuredWidth();

        if (mode == View.MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width += 2 * mItemsPadding;

            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == View.MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
            }
        }

        // forcing recalculating
        mItemsLayout.measure(
                View.MeasureSpec.makeMeasureSpec(width - 2 * mItemsPadding, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );

        return width;
    }


    //--------------------------------------------------------------------------
    //
    //  Drawing items
    //
    //--------------------------------------------------------------------------

    @Override
    protected void drawItems(Canvas canvas) {
        canvas.save();
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int ih = getItemDimension();

        // resetting intermediate bitmap and recreating canvases
        mSpinBitmap.eraseColor(0);
        Canvas c = new Canvas(mSpinBitmap);
        Canvas cSpin = new Canvas(mSpinBitmap);

        int top = (mCurrentItemIdx - mFirstItemIdx) * ih + (ih - getHeight()) / 2;
        c.translate(mItemsPadding, - top + mScrollingOffset);
        mItemsLayout.draw(c);

//        mSeparatorsBitmap.eraseColor(0);
        Canvas cSeparators = new Canvas(mSpinBitmap);

        if (mSelectionDivider != null) {
            // draw the top divider
            int topOfTopDivider = (getHeight() - ih - mSelectionDividerHeight) / 2;
            int bottomOfTopDivider = topOfTopDivider + mSelectionDividerHeight;
            mSelectionDivider.setBounds(0, topOfTopDivider, w, bottomOfTopDivider);
            mSelectionDivider.draw(cSeparators);

            // draw the bottom divider
            int topOfBottomDivider =  topOfTopDivider + ih;
            int bottomOfBottomDivider = bottomOfTopDivider + ih;
            mSelectionDivider.setBounds(0, topOfBottomDivider, w, bottomOfBottomDivider);
            mSelectionDivider.draw(cSeparators);
        }

        cSpin.drawRect(0, 0, w, h, mSelectorWheelPaint);
        cSeparators.drawRect(0, 0, w, h, mSeparatorsPaint);

        canvas.drawBitmap(mSpinBitmap, 0, 0, null);
        canvas.drawBitmap(mSpinBitmap, 0, 0, null);
        canvas.restore();
    }

}
