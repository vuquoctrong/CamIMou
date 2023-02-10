package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.adapters.WheelViewAdapter;

import java.util.LinkedList;
import java.util.List;


/**
 * Abstract spinner spinnerwheel view 
 * This class should be subclassed.
 *
 * 抽象滚轮视图
 * 这个类应该是子类
 */
public abstract class AbstractWheel extends View {

    private static int itemID = -1;

    @SuppressWarnings("unused")
    private final String LOG_TAG = AbstractWheel.class.getName() + " #" + (++itemID);

    //----------------------------------
    //  Default properties values
    //----------------------------------

    /** Default count of visible items */
    private static final int DEF_VISIBLE_ITEMS = 4;
    private static final boolean DEF_IS_CYCLIC = false;

    //----------------------------------
    //  Class properties
    //----------------------------------

    protected int mCurrentItemIdx = 0;

    // Count of visible items
    protected int mVisibleItems;
    // Should all items be visible
    protected boolean mIsAllVisible;

    protected boolean mIsCyclic;

    // Scrolling
    protected WheelScroller mScroller;
    protected boolean mIsScrollingPerformed;
    protected int mScrollingOffset;

    // Items layout
    protected LinearLayout mItemsLayout;

    // The number of first item in layout
    protected int mFirstItemIdx;

    // View adapter
    protected WheelViewAdapter mViewAdapter;
    
    protected int mLayoutHeight;
    protected int mLayoutWidth;

    // Recycle
    private WheelRecycler mRecycler = new WheelRecycler(this);

    // Listeners
    private List<OnWheelChangedListener> changingListeners = new LinkedList<>();
    private List<OnWheelScrollListener> scrollingListeners = new LinkedList<>();
    private List<OnWheelClickedListener> clickingListeners = new LinkedList<>();

    //XXX: I don't like listeners the way as they are now. -df

    // Adapter listener
    private DataSetObserver mDataObserver;


    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Create a new AbstractWheel instance
     * @param context the application environment.
     * @param attrs a collection of attributes.
     * @param defStyle The default style to apply to this view.
     *
     * 创建一个新的AbstractWheel实例
     * @param context 应用程序环境。
     * @param attrs 属性的集合。
     * @param defStyle 应用于此视图的默认样式。
     */
    public AbstractWheel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initAttributes(attrs, defStyle);
        initData(context);
    }

    //--------------------------------------------------------------------------
    //
    //  Initiating data and assets at start up
    //
    //--------------------------------------------------------------------------

    /**
     * Initiates data and parameters from styles
     * @param attrs a collection of attributes.
     * @param defStyle The default style to apply to this view.
     *
     * 从样式初始化数据和参数
     * @param attrs 属性的集合
     * @param defStyle 应用于此视图的默认样式
     */
    protected void initAttributes(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MobileCommonAbstractWheelView, defStyle, 0);
        mVisibleItems = a.getInt(R.styleable.MobileCommonAbstractWheelView_visibleItems, DEF_VISIBLE_ITEMS);
        mIsAllVisible = a.getBoolean(R.styleable.MobileCommonAbstractWheelView_isAllVisible, false);
        mIsCyclic = a.getBoolean(R.styleable.MobileCommonAbstractWheelView_isCyclic, DEF_IS_CYCLIC);

        a.recycle();
    }

    /**
     * Initiates data
     * @param context the context
     *
     * 初始化数据
     * @param context 上下文
     */
    protected void initData(Context context) {

        mDataObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                invalidateItemsLayout(false);
            }

            @Override
            public void onInvalidated() {
                invalidateItemsLayout(true);
            }
        };

        // creating new scroller
        mScroller = createScroller(new WheelScroller.ScrollingListener() {

            public void onStarted() {
                mIsScrollingPerformed = true;
                notifyScrollingListenersAboutStart();
                onScrollStarted();
            }

            public void onTouch() {
                onScrollTouched();
            }

            public void onTouchUp() {
                if (!mIsScrollingPerformed)
                    onScrollTouchedUp(); // if scrolling IS performed, whe should use onFinished instead
            }

            public void onScroll(int distance) {
                doScroll(distance);

                int dimension = getBaseDimension();
                if (mScrollingOffset >  dimension) {
                    mScrollingOffset =  dimension;
                    mScroller.stopScrolling();
                } else if (mScrollingOffset < - dimension) {
                    mScrollingOffset = - dimension;
                    mScroller.stopScrolling();
                }
            }

            public void onFinished() {
                if (mIsScrollingPerformed) {
                    notifyScrollingListenersAboutEnd();
                    mIsScrollingPerformed = false;
                    onScrollFinished();
                }

                mScrollingOffset = 0;
                invalidate();
            }

            public void onJustify() {
                if (Math.abs(mScrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                    mScroller.scroll(mScrollingOffset, 0);
                }
            }
        });
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        //end

        ss.currentItem = this.getCurrentItem();

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end

        mCurrentItemIdx = ss.currentItem;

        // dirty hack to re-draw child items correctly
        postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidateItemsLayout(false);
            }
        }, 100);
    }

    static class SavedState extends BaseSavedState {
        int currentItem;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentItem = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.currentItem);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    abstract protected void recreateAssets(int width, int height);

    abstract public void removeBitmap();

    //--------------------------------------------------------------------------
    //
    //  Scroller operations
    //
    //--------------------------------------------------------------------------

    /**
     * Creates scroller appropriate for specific wheel implementation.
     * @param scrollingListener listener to be passed to the scroller
     * @return Initialized scroller to be used
     *
     * 创建适合于特定车轮实现的滚动条
     * @param scrollingListener 要传递给滚动器的监听器
     * @return 初始化要使用的滚动器
     */
    abstract protected WheelScroller createScroller(WheelScroller.ScrollingListener scrollingListener);

    /* These methods are not abstract, as we may want to override only some of them */
    protected void onScrollStarted() {}
    protected void onScrollTouched() {}
    protected void onScrollTouchedUp() {}
    protected void onScrollFinished() {}

    /**
     * Stops scrolling
     *
     * 停止滚动
     */
    public void stopScrolling() {
        mScroller.stopScrolling();
    }

    /**
     * Set the the specified scrolling interpolator
     * @param interpolator the interpolator
     *
     * 设置指定的滚动插值器
     * @param interpolator 插入器
     */
    public void setInterpolator(Interpolator interpolator) {
        mScroller.setInterpolator(interpolator);
    }

    /**
     * Scroll the spinnerwheel
     * @param itemsToScroll items to scroll
     * @param time scrolling duration
     *
     * 滚动滚轮
     * @param itemsToScroll 滚动项
     * @param time 滚动的持续时间
     */
    public void scroll(int itemsToScroll, int time) {
        int distance = itemsToScroll * getItemDimension() - mScrollingOffset;
        onScrollTouched(); // we have to emulate touch when scrolling spinnerwheel programmatically to light up stuff
        mScroller.scroll(distance, time);
    }

    /**
     * Scrolls the spinnerwheel
     * @param delta the scrolling value
     *
     * 滚动滚轮
     * @param delta 滚动值
     */
    private void doScroll(int delta) {
        mScrollingOffset += delta;

        int itemDimension = getItemDimension();
        int count = mScrollingOffset / itemDimension;

        int pos = mCurrentItemIdx - count;
        int itemCount = mViewAdapter.getItemsCount();

        int fixPos = mScrollingOffset % itemDimension;
        if (Math.abs(fixPos) <= itemDimension / 2) {
            fixPos = 0;
        }
        if (mIsCyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--;
                count++;
            } else if (fixPos < 0) {
                pos++;
                count--;
            }
            // fix position by rotating
            while (pos < 0) {
                pos += itemCount;
            }
            pos %= itemCount;
        } else {
            if (pos < 0) {
                count = mCurrentItemIdx;
                pos = 0;
            } else if (pos >= itemCount) {
                count = mCurrentItemIdx - itemCount + 1;
                pos = itemCount - 1;
            } else if (pos > 0 && fixPos > 0) {
                pos--;
                count++;
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++;
                count--;
            }
        }

        int offset = mScrollingOffset;
        if (pos != mCurrentItemIdx) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }

        // update offset
        int baseDimension = getBaseDimension();
        mScrollingOffset = offset - count * itemDimension;
        if (mScrollingOffset > baseDimension) {
            mScrollingOffset = mScrollingOffset % baseDimension + baseDimension;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Base measurements
    //
    //--------------------------------------------------------------------------

    /**
     * Returns base dimension of the spinnerwheel — width for horizontal spinnerwheel, height for
     * vertical
     * @return width or height of the spinnerwheel
     *
     * 返回滚轮的基本尺寸——水平滚轮的宽度，水平滚轮的高度
     * @return 滚轮的宽度或者高度
     */
    abstract protected int getBaseDimension();

    /**
     * Returns base dimension of base item — width for horizontal spinnerwheel, height for vertical
     * @return width or height of base item
     *
     * 返回基本项的基本尺寸-水平滚轮的宽度，垂直纺车的高度
     * @return 基础项的宽度或高度
     */
    abstract protected int getItemDimension();

    /**
     * Processes MotionEvent and returns relevant position — x for horizontal spinnerwheel, y for
     * vertical
     * @param event MotionEvent to be processed
     * @return relevant position of the MotionEvent
     *
     * 处理MotionEvent并返回相关位置- x为水平滚轮，y为垂直滚轮
     * @param event 要处理的MotionEvent
     * @return MotionEvent的相关位置
     */
    abstract protected float getMotionEventPosition(MotionEvent event);


    //--------------------------------------------------------------------------
    //
    //  Layout creation and measurement operations
    //
    //--------------------------------------------------------------------------

    /**
     * Creates item layouts if necessary
     *
     * 如果需要，创建项目布局
     */
    abstract protected void createItemsLayout();

    /**
     * Sets layout width and height
     *
     * 设置布局宽度和高度
     */
    abstract protected void doItemsLayout();


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int w = r - l;
            int h = b - t;
            doItemsLayout();
            if (mLayoutWidth != w || mLayoutHeight != h) {
                recreateAssets(getMeasuredWidth(), getMeasuredHeight());
            }
            mLayoutWidth = w;
            mLayoutHeight = h;
        }
    }

    /**
     * Invalidates items layout
     * @param clearCaches if true then cached views will be cleared
     *
     * 无效的项目布局
     * @param clearCaches 如果为true，则缓存的视图将被清除
     */
    public void invalidateItemsLayout(boolean clearCaches) {
        if (clearCaches) {
            mRecycler.clearAll();
            if (mItemsLayout != null) {
                mItemsLayout.removeAllViews();
            }
            mScrollingOffset = 0;
        } else if (mItemsLayout != null) {
                // cache all items
                mRecycler.recycleItems(mItemsLayout, mFirstItemIdx, new ItemsRange());
        }
        invalidate();
    }


    //--------------------------------------------------------------------------
    //
    //  Getters and setters
    //
    //--------------------------------------------------------------------------

    /**
     * Gets count of visible items
     * @return the count of visible items
     *
     * 获取可见项的计数
     * @return 可见项的计数
     */
    public int getVisibleItems() {
        return mVisibleItems;
    }

    /**
     * Sets the desired count of visible items.
     * Actual amount of visible items depends on spinnerwheel layout parameters.
     * To apply changes and rebuild view call measure().
     * @param count the desired count for visible items
     *
     * 设置可见项的期望计数
     * 可见项的实际数量取决于滚轮布局参数。要应用更改并重新构建视图，请调用measure()。
     * @param count 可见项的期望计数
     */
    public void setVisibleItems(int count) {
        mVisibleItems = count;
    }

    /**
     * Sets all items to have no dim and makes them visible
     * @param isAllVisible  Whether all visible
     *
     * 设置所有项目没有昏暗，使他们可见
     * @param isAllVisible  是否所有可见
     */
    public void setAllItemsVisible(boolean isAllVisible){
        mIsAllVisible = isAllVisible;
        invalidateItemsLayout(false);
    }

    /**
     * Gets view adapter
     * @return the view adapter
     *
     * 获取视图适配器
     * @return 视图适配器
     */
    public WheelViewAdapter getViewAdapter() {
        return mViewAdapter;
    }


    /**
     * Sets view adapter. Usually new adapters contain different views, so
     * it needs to rebuild view by calling measure().
     * @param viewAdapter the view adapter
     *
     * 适配器设置视图。通常新的适配器包含不同的视图，因此需要通过调用measure()来重新构建视图。
     * @param viewAdapter 视图适配器
     */
    public void setViewAdapter(WheelViewAdapter viewAdapter) {
        if (this.mViewAdapter != null) {
            this.mViewAdapter.unregisterDataSetObserver(mDataObserver);
        }
        this.mViewAdapter = viewAdapter;
        if (this.mViewAdapter != null) {
            this.mViewAdapter.registerDataSetObserver(mDataObserver);
        }
        invalidateItemsLayout(true);
    }

    /**
     * Gets current value
     * @return the current value
     *
     * 获取当前值
     * @return 当前值
     */
    public int getCurrentItem() {
        return mCurrentItemIdx;
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     * @param index the item index
     * @param animated the animation flag
     *
     * 设置当前项。当索引错误时不执行任何操作。
     * @param index 当前项索引
     * @param animated 动画标志
     */
    public void setCurrentItem(int index, boolean animated) {
        if (mViewAdapter == null || mViewAdapter.getItemsCount() == 0) {
            return; // throw?
        }

        int itemCount = mViewAdapter.getItemsCount();
        if (index < 0 || index >= itemCount) {
            if (mIsCyclic) {
                while (index < 0) {
                    index += itemCount;
                }
                index %= itemCount;
            } else{
                return; // throw?
            }
        }
        if (index != mCurrentItemIdx) {
            if (animated) {
                int itemsToScroll = index - mCurrentItemIdx;
                if (mIsCyclic) {
                    int scroll = itemCount + Math.min(index, mCurrentItemIdx) - Math.max(index, mCurrentItemIdx);
                    if (scroll < Math.abs(itemsToScroll)) {
                        itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
                    }
                }
                scroll(itemsToScroll, 0);
            } else {
                mScrollingOffset = 0;
                final int old = mCurrentItemIdx;
                mCurrentItemIdx = index;
                notifyChangingListeners(old, mCurrentItemIdx);
                invalidate();
            }
        }
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     * @param index the item index
     *
     * 设置当前项目带/o动画。当索引错误时不执行任何操作。
     * @param index 当前项索引
     */
    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    /**
     * Tests if spinnerwheel is cyclic. That means before the 1st item there is shown the last one
     * @return true if spinnerwheel is cyclic
     *
     * 测试滚轮是否循环。这意味着在第一项之前有最后一项
     * @return true 滚轮是否循环
     */
    public boolean isCyclic() {
        return mIsCyclic;
    }

    /**
     * Set spinnerwheel cyclic flag
     * @param isCyclic the flag to set
     *
     * 设置滚轮循环标志
     * @param isCyclic  设置的标志
     */
    public void setCyclic(boolean isCyclic) {
        this.mIsCyclic = isCyclic;
        invalidateItemsLayout(false);
    }


    //--------------------------------------------------------------------------
    //
    //  Listener operations
    //
    //--------------------------------------------------------------------------

    /**
     * Adds spinnerwheel changing listener
     * @param listener the listener
     *
     * 添加滚轮改变时监听器
     * @param listener 监听器
     */
    public void addChangingListener(OnWheelChangedListener listener) {
        changingListeners.add(listener);
    }

    /**
     * Removes spinnerwheel changing listener
     * @param listener the listener
     *
     * 移除滚轮监听器
     * @param listener 监听器
     */
    public void removeChangingListener(OnWheelChangedListener listener) {
        changingListeners.remove(listener);
    }

    /**
     * Notifies changing listeners
     * @param oldValue the old spinnerwheel value
     * @param newValue the new spinnerwheel value
     *
     * 通知更改侦听器
     * @param oldValue 旧滚轮的值
     * @param newValue 新滚轮的值
     */
    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Adds spinnerwheel scrolling listener
     * @param listener the listener
     *
     * 添加滚轮滚动监听器
     * @param listener 监听器
     */
    public void addScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.add(listener);
    }

    /**
     * Removes spinnerwheel scrolling listener
     * @param listener the listener
     *
     * 移除滚轮滚动监听器
     * @param listener 监听器
     */
    public void removeScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    /**
     * Notifies listeners about starting scrolling
     *
     * 通知监听器开始滚动
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     *
     * 通知监听器结束滚动
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Adds spinnerwheel clicking listener
     * @param listener the listener
     *
     * 添加滚轮点击监听器
     * @param listener 监听器
     */
    public void addClickingListener(OnWheelClickedListener listener) {
        clickingListeners.add(listener);
    }

    /**
     * Removes spinnerwheel clicking listener
     * @param listener the listener
     *
     * 移除滚轮点击监听器
     * @param listener 监听器
     */
    public void removeClickingListener(OnWheelClickedListener listener) {
        clickingListeners.remove(listener);
    }

    /**
     * Notifies listeners about clicking
     * @param item clicked item
     *
     * 通知点击监听器
     * @param item 点击项
     */
    protected void notifyClickListenersAboutClick(int item) {
        for (OnWheelClickedListener listener : clickingListeners) {
            listener.onItemClicked(this, item);
        }
    }


    //--------------------------------------------------------------------------
    //
    //  Rebuilding items
    //
    //--------------------------------------------------------------------------

    /**
     * Rebuilds spinnerwheel items if necessary. Caches all unused items.
     * @return boolean  if items are rebuilt
     *
     * 如有必要，重建滚轮项。缓存所有未使用的项目。
     * @return boolean  该项是否重建
     */
    protected boolean rebuildItems() {
        boolean updated;
        ItemsRange range = getItemsRange();

        if (mItemsLayout != null) {
            int first = mRecycler.recycleItems(mItemsLayout, mFirstItemIdx, range);
            updated = mFirstItemIdx != first;
            mFirstItemIdx = first;
        } else {
            createItemsLayout();
            updated = true;
        }

        if (!updated) {
            updated = mFirstItemIdx != range.getFirst() || mItemsLayout.getChildCount() != range.getCount();
        }

        if (mFirstItemIdx > range.getFirst() && mFirstItemIdx <= range.getLast()) {
            for (int i = mFirstItemIdx - 1; i >= range.getFirst(); i--) {
                if (!addItemView(i, true)) {
                    break;
                }
                mFirstItemIdx = i;
            }
        } else {
            mFirstItemIdx = range.getFirst();
        }

        int first = mFirstItemIdx;
        for (int i = mItemsLayout.getChildCount(); i < range.getCount(); i++) {
            if (!addItemView(mFirstItemIdx + i, false) && mItemsLayout.getChildCount() == 0) {
                first++;
            }
        }
        mFirstItemIdx = first;

        return updated;
    }

    //----------------------------------
    //  ItemsRange operations
    //----------------------------------

    /**
     * Calculates range for spinnerwheel items
     * @return the items range
     *
     * 计算滚轮项的范围
     * @return 该项的范围
     */
    private ItemsRange getItemsRange() {
        if (mIsAllVisible) {
            int baseDimension = getBaseDimension();
            int itemDimension = getItemDimension();
            if (itemDimension != 0)
                mVisibleItems = baseDimension / itemDimension + 1;
        }

        int start = mCurrentItemIdx - mVisibleItems / 2;
        int end = start + mVisibleItems - (mVisibleItems % 2 == 0 ? 0 : 1);
        if (mScrollingOffset != 0) {
            if (mScrollingOffset > 0) {
                start--;
            } else {
                end++;
            }
        }
        if (!isCyclic()) {
            if (start < 0)
                start = 0;
            if (mViewAdapter != null && end > mViewAdapter.getItemsCount())
                end = mViewAdapter.getItemsCount();
        }
        return new ItemsRange(start, end - start + 1);
    }

    /**
     * Checks whether item index is valid
     * @param index the item index
     * @return true if item index is not out of bounds or the spinnerwheel is cyclic
     *
     * 检查项索引是否有效
     * @param index 该元素索引
     * @return true 如果元素索引没有超出边界或滚轮是循环的
     */
    protected boolean isValidItemIndex(int index) {
        return (mViewAdapter != null) && (mViewAdapter.getItemsCount() > 0) &&
                (mIsCyclic || (index >= 0 && index < mViewAdapter.getItemsCount()));
    }

    //----------------------------------
    //  Operations with item view
    //----------------------------------

    /**
     * Adds view for item to items layout
     * @param index the item index
     * @param first the flag indicates if view should be first
     * @return true if corresponding item exists and is added
     *
     * 为项目布局添加项的视图
     * @param index 元素索引
     * @param first 标志表示视图是否应该放在首位
     * @return true 如果对应项存在并已添加
     */
    private boolean addItemView(int index, boolean first) {
        View view = getItemView(index);
        if (view != null) {
            if (first) {
                mItemsLayout.addView(view, 0);
            } else {
                mItemsLayout.addView(view);
            }
            return true;
        }
        return false;
    }

    /**
     * 返回指定项的视图
     * @param index 元素索引
     * @return item 视图或空视图，如果索引超出边界
     */
    private View getItemView(int index) {
        if (mViewAdapter == null || mViewAdapter.getItemsCount() == 0) {
            return null;
        }
        int count = mViewAdapter.getItemsCount();
        if (!isValidItemIndex(index)) {
            return mViewAdapter.getEmptyItem( mRecycler.getEmptyItem(), mItemsLayout);
        } else {
            while (index < 0) {
                index = count + index;
            }
        }
        index %= count;
        return mViewAdapter.getItem(index, mRecycler.getItem(), mItemsLayout);
    }


    //--------------------------------------------------------------------------
    //
    //  Intercepting and processing touch event
    //
    //--------------------------------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || getViewAdapter() == null) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!mIsScrollingPerformed) {
                    int distance = (int) getMotionEventPosition(event) - getBaseDimension() / 2;
                    if (distance > 0) {
                        distance += getItemDimension() / 2;
                    } else {
                        distance -= getItemDimension() / 2;
                    }
                    int items = distance / getItemDimension();
                    if (items != 0 && isValidItemIndex(mCurrentItemIdx + items)) {
                        notifyClickListenersAboutClick(mCurrentItemIdx + items);
                    }
                }
                break;
        }
        return mScroller.onTouchEvent(event);
    }

}
