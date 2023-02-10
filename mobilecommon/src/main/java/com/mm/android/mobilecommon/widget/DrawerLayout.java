package com.mm.android.mobilecommon.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import androidx.customview.view.AbsSavedState;
import androidx.customview.widget.ViewDragHelper;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;


public class DrawerLayout extends ViewGroup {
    private static final String TAG = "DrawerLayout";

    private static final int[] THEME_ATTRS = {
            android.R.attr.colorPrimaryDark
    };

    @IntDef({STATE_IDLE, STATE_DRAGGING, STATE_SETTLING})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {}

    /**
     * Indicates that any drawers are in an idle, settled state. No animation is in progress.
     *
     * 指示所有抽屉处于空闲、已解决的状态。没有正在制作的动画。
     */
    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;

    /**
     * Indicates that a drawer is currently being dragged by the user.
     *
     * 指示用户当前正在拖动一个抽屉。
     */
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;

    /**
     * Indicates that a drawer is in the process of settling to a final position.
     *
     * 指示出票人正在确定最终位置的过程中。
     */
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;

    @IntDef({LOCK_MODE_UNLOCKED, LOCK_MODE_LOCKED_CLOSED, LOCK_MODE_LOCKED_OPEN,
            LOCK_MODE_UNDEFINED})
    @Retention(RetentionPolicy.SOURCE)
    private @interface LockMode {}

    /**
     * The drawer is unlocked.
     *
     * drawer没锁。
     */
    public static final int LOCK_MODE_UNLOCKED = 0;

    /**
     * The drawer is locked closed. The user may not open it, though
     * the app may open it programmatically.
     *
     * drawer被锁上了。用户可能不会打开它，尽管应用程序可能以编程方式打开它。
     */
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;

    /**
     * The drawer is locked open. The user may not close it, though the app
     * may close it programmatically.
     *
     * drawer被锁着。用户可能不会关闭它，但应用程序可能会以编程方式关闭它。
     */
    public static final int LOCK_MODE_LOCKED_OPEN = 2;

    /**
     * The drawer's lock state is reset to default.
     *
     * 抽屉的锁状态重置为默认。
     */
    public static final int LOCK_MODE_UNDEFINED = 3;

    @IntDef(value = {Gravity.LEFT, Gravity.RIGHT, GravityCompat.START, GravityCompat.END},
            flag = true)
    @Retention(RetentionPolicy.SOURCE)
    private @interface EdgeGravity {}


    private static final int MIN_DRAWER_MARGIN = 0; // dp
    private static final int DRAWER_ELEVATION = 10; //dp

    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;

    /**
     * Length of time to delay before peeking the drawer.
     *
     * 偷看drawer前的延迟时间长度。
     */
    private static final int PEEK_DELAY = 160; // ms

    /**
     * Minimum velocity that will be detected as a fling
     *
     * 被检测到的最小速度
     */
    private static final int MIN_FLING_VELOCITY = 400; // dips per second

    /**
     * Experimental feature.
     *
     * 实验功能。
     */
    private static final boolean ALLOW_EDGE_LOCK = false;

    private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;

    private static final float TOUCH_SLOP_SENSITIVITY = 1.f;

    static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.layout_gravity
    };

    /** Whether we can use NO_HIDE_DESCENDANTS accessibility importance. */
    static final boolean CAN_HIDE_DESCENDANTS = Build.VERSION.SDK_INT >= 19;

    /** Whether the drawer shadow comes from setting elevation on the drawer. */
    private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION =
            Build.VERSION.SDK_INT >= 21;

    private final ChildAccessibilityDelegate mChildAccessibilityDelegate =
            new ChildAccessibilityDelegate();
    private float mDrawerElevation;

    private int mMinDrawerMargin;

    private int mScrimColor = DEFAULT_SCRIM_COLOR;
    private float mScrimOpacity;
    private Paint mScrimPaint = new Paint();

    private final ViewDragHelper mLeftDragger;
    private final ViewDragHelper mRightDragger;
    private final ViewDragCallback mLeftCallback;
    private final ViewDragCallback mRightCallback;
    private int mDrawerState;
    private boolean mInLayout;
    private boolean mFirstLayout = true;

    private @LockMode int mLockModeLeft = LOCK_MODE_UNDEFINED;
    private @LockMode int mLockModeRight = LOCK_MODE_UNDEFINED;
    private @LockMode int mLockModeStart = LOCK_MODE_UNDEFINED;
    private @LockMode int mLockModeEnd = LOCK_MODE_UNDEFINED;

    private boolean mDisallowInterceptRequested;
    private boolean mChildrenCanceledTouch;

    private @Nullable DrawerListener mListener;
    private List<DrawerListener> mListeners;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private Drawable mStatusBarBackground;
    private Drawable mShadowLeftResolved;
    private Drawable mShadowRightResolved;

    private CharSequence mTitleLeft;
    private CharSequence mTitleRight;

    private Object mLastInsets;
    private boolean mDrawStatusBarBackground;

    /** Shadow drawables for different gravity */
    private Drawable mShadowStart = null;
    private Drawable mShadowEnd = null;
    private Drawable mShadowLeft = null;
    private Drawable mShadowRight = null;

    private final ArrayList<View> mNonDrawerViews;

    /**
     * Listener for monitoring events about drawers.
     *
     * 监听器用于监视关于drawers的事件。
     */
    public interface DrawerListener {
        /**
         * Called when a drawer's position changes.
         * @param drawerView The child view that was moved
         * @param slideOffset The new offset of this drawer within its range, from 0-1
         *
         * 当drawer的位置改变时调用。
         * @param drawerView 被移动的子视图
         * @param slideOffset 这个drawer在其范围内的新偏移量，从0-1
         */
        void onDrawerSlide(@NonNull View drawerView, float slideOffset);

        /**
         * Called when a drawer has settled in a completely open state.
         * The drawer is interactive at this point.
         * @param drawerView Drawer view that is now open
         *
         * 当drawer处于完全打开状态时调用。
         * 此时drawer是交互式的。
         * @param drawerView Drawer视图，现在已打开
         */
        void onDrawerOpened(@NonNull View drawerView);

        /**
         * Called when a drawer has settled in a completely closed state.
         * @param drawerView Drawer view that is now closed
         *
         * 当Drawer处于完全关闭状态时调用。
         * @param drawerView Drawer视图，现在已关闭
         */
        void onDrawerClosed(@NonNull View drawerView);

        /**
         * Called when the drawer motion state changes. The new state will
         * be one of {@link #STATE_IDLE}, {@link #STATE_DRAGGING} or {@link #STATE_SETTLING}.
         * @param newState The new drawer motion state
         *
         * 当drawer运动状态改变时调用。新状态将是{@link #STATE_IDLE}, {@link #STATE_DRAGGING} 或 {@link #STATE_SETTLING}中的一个。
         * @param newState The new drawer motion state
         */
        void onDrawerStateChanged(@State int newState);
    }

    /**
     * Stub/no-op implementations of all methods of {@link DrawerListener}.
     * Override this if you only care about a few of the available callback methods.
     *
     * {@link DrawerListener}的所有方法的Stub/no-op实现。
     * 如果您只关心一些可用的回调方法，那么可以重写它。
     */
    public abstract static class SimpleDrawerListener implements DrawerListener {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    }

    public DrawerLayout(@NonNull Context context) {
        this(context, null);
    }

    public DrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        final float density = getResources().getDisplayMetrics().density;
        mMinDrawerMargin = (int) (MIN_DRAWER_MARGIN * density + 0.5f);
        final float minVel = MIN_FLING_VELOCITY * density;

        mLeftCallback = new ViewDragCallback(Gravity.LEFT);
        mRightCallback = new ViewDragCallback(Gravity.RIGHT);

        mLeftDragger = ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY, mLeftCallback);
        mLeftDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mLeftDragger.setMinVelocity(minVel);
        mLeftCallback.setDragger(mLeftDragger);

        mRightDragger = ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY, mRightCallback);
        mRightDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
        mRightDragger.setMinVelocity(minVel);
        mRightCallback.setDragger(mRightDragger);

        // So that we can catch the back button
        setFocusableInTouchMode(true);

        ViewCompat.setImportantForAccessibility(this,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
        setMotionEventSplittingEnabled(false);
        if (ViewCompat.getFitsSystemWindows(this)) {
            if (Build.VERSION.SDK_INT >= 21) {
                setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @TargetApi(21)
                    @Override
                    public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                        final DrawerLayout drawerLayout = (DrawerLayout) view;
                        drawerLayout.setChildInsets(insets, insets.getSystemWindowInsetTop() > 0);
                        return insets.consumeSystemWindowInsets();
                    }
                });
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                final TypedArray a = context.obtainStyledAttributes(THEME_ATTRS);
                try {
                    mStatusBarBackground = a.getDrawable(0);
                } finally {
                    a.recycle();
                }
            } else {
                mStatusBarBackground = null;
            }
        }

        mDrawerElevation = DRAWER_ELEVATION * density;

        mNonDrawerViews = new ArrayList<>();
    }

    /**
     * Sets the base elevation of the drawer(s) relative to the parent, in pixels. Note that the
     * elevation change is only supported in API 21 and above.
     * @param elevation The base depth position of the view, in pixels.
     *
     * 设置drawer相对于父元素的基本高度(以像素为单位)。注意，高度更改仅在API 21及以上版本中支持。
     * @param elevation 视图的基本深度位置，以像素为单位。
     */
    public void setDrawerElevation(float elevation) {
        mDrawerElevation = elevation;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (isDrawerView(child)) {
                ViewCompat.setElevation(child, mDrawerElevation);
            }
        }
    }

    /**
     * The base elevation of the drawer(s) relative to the parent, in pixels. Note that the
     * elevation change is only supported in API 21 and above. For unsupported API levels, 0 will
     * be returned as the elevation.
     * @return The base depth position of the view, in pixels.
     *
     * drawer相对于父元素的基本高度(以像素为单位)。注意，高度更改仅在API 21及以上版本中支持。对于不支持的API级别，将返回0作为海拔。
     * @return 视图的基本深度位置，以像素为单位。
     */
    public float getDrawerElevation() {
        if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
            return mDrawerElevation;
        }
        return 0f;
    }

    /**
     * @hide Internal use only; called to apply window insets when configured
     * with fitsSystemWindows="true"
     *
     * @hide 仅内部使用;调用fitsSystemWindows="true"时应用窗口嵌入
     */
    @RestrictTo(LIBRARY_GROUP)
    public void setChildInsets(Object insets, boolean draw) {
        mLastInsets = insets;
        mDrawStatusBarBackground = draw;
        setWillNotDraw(!draw && getBackground() == null);
        requestLayout();
    }

    /**
     * Set a simple drawable used for the left or right shadow. The drawable provided must have a
     * nonzero intrinsic width. For API 21 and above, an elevation will be set on the drawer
     * instead of using the provided shadow drawable.
     *
     * <p>Note that for better support for both left-to-right and right-to-left layout
     * directions, a drawable for RTL layout (in additional to the one in LTR layout) can be
     * defined with a resource qualifier "ldrtl" for API 17 and above with the gravity
     * {@link GravityCompat#START}. Alternatively, for API 23 and above, the drawable can
     * auto-mirrored such that the drawable will be mirrored in RTL layout.</p>
     *
     * @param shadowDrawable Shadow drawable to use at the edge of a drawer
     * @param gravity Which drawer the shadow should apply to
     *
     *
     *
     * 设置一个用于左侧或右侧阴影的简单绘图。所提供的可绘制对象必须具有非零的固有宽度。
     *                对于API 21及以上，将在drawer上设置一个仰角，而不是使用所提供的阴影绘图。
     * <p>注意，为了更好地支持从左到右和从右到左的布局方向，可以使用API 17及以上版本的gravity {@link GravityCompat#START}
     *                使用资源限定符“ldrtl”定义RTL布局的绘图(除了LTR布局中的绘图)。
     *                另外，对于API 23及以上版本，可绘制对象可以自动镜像，这样可绘制对象将在RTL布局中镜像。</p>
     *
     * @param shadowDrawable 可以画在drawer边缘的阴影
     * @param gravity 影子应该放在哪个drawer
     */
    public void setDrawerShadow(Drawable shadowDrawable, @EdgeGravity int gravity) {
        /*
         * TODO Someone someday might want to set more complex drawables here.
         * They're probably nuts, but we might want to consider registering callbacks,
         * setting states, etc. properly.
         */
        if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
            // No op. Drawer shadow will come from setting an elevation on the drawer.
            return;
        }
        if ((gravity & GravityCompat.START) == GravityCompat.START) {
            mShadowStart = shadowDrawable;
        } else if ((gravity & GravityCompat.END) == GravityCompat.END) {
            mShadowEnd = shadowDrawable;
        } else if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            mShadowLeft = shadowDrawable;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            mShadowRight = shadowDrawable;
        } else {
            return;
        }
        resolveShadowDrawables();
        invalidate();
    }

    /**
     * Set a simple drawable used for the left or right shadow. The drawable provided must have a
     * nonzero intrinsic width. For API 21 and above, an elevation will be set on the drawer
     * instead of using the provided shadow drawable.
     *
     * <p>Note that for better support for both left-to-right and right-to-left layout
     * directions, a drawable for RTL layout (in additional to the one in LTR layout) can be
     * defined with a resource qualifier "ldrtl" for API 17 and above with the gravity
     * {@link GravityCompat#START}. Alternatively, for API 23 and above, the drawable can
     * auto-mirrored such that the drawable will be mirrored in RTL layout.</p>
     *
     * @param resId Resource id of a shadow drawable to use at the edge of a drawer
     * @param gravity Which drawer the shadow should apply to
     *
     *
     * 设置一个用于左侧或右侧阴影的简单绘图。所提供的可绘制对象必须具有非零的固有宽度。
     *                对于API 21及以上，将在drawer上设置一个仰角，而不是使用所提供的阴影绘图。
     *
     * <p>注意，为了更好地支持从左到右和从右到左的布局方向，
     *                可以使用API 17及以上版本的gravity {@link GravityCompat#START}使用资源限定符“ldrtl”定义RTL布局的绘图(除了LTR布局中的绘图)。
     *                另外，对于API 23及以上版本，可绘制对象可以自动镜像，这样可绘制对象将在RTL布局中镜像</p>
     *
     * @param resId 可绘制在drawer边缘使用的阴影的资源id
     * @param gravity 影子应该放在哪个drawer
     */
    public void setDrawerShadow(@DrawableRes int resId, @EdgeGravity int gravity) {
        setDrawerShadow(ContextCompat.getDrawable(getContext(), resId), gravity);
    }

    /**
     * Set a color to use for the scrim that obscures primary content while a drawer is open.
     * @param color Color to use in 0xAARRGGBB format.
     *
     * 设置一种颜色用于在抽屉打开时模糊主要内容的棉布。
     * @param color 颜色以0xAARRGGBB格式使用。
     */
    public void setScrimColor(@ColorInt int color) {
        mScrimColor = color;
        invalidate();
    }

    /**
     * Set a listener to be notified of drawer events. Note that this method is deprecated
     * and you should use {@link #addDrawerListener(DrawerListener)} to add a listener and
     * {@link #removeDrawerListener(DrawerListener)} to remove a registered listener.
     * @param listener Listener to notify when drawer events occur
     * @deprecated Use {@link #addDrawerListener(DrawerListener)}
     * @see DrawerListener
     * @see #addDrawerListener(DrawerListener)
     * @see #removeDrawerListener(DrawerListener)
     *
     *
     * 设置一个监听器，以通知drawer事件。注意，此方法已被弃用，您应该使用{@link #addDrawerListener(DrawerListener)}添加一个侦听器，
     *       使用{@link #removeDrawerListener(DrawerListener)}删除已注册的监听器。
     * @param listener 监听器，用于在drawer事件发生时通知
     * @deprecated 使用 {@link #addDrawerListener(DrawerListener)}
     * @see DrawerListener
     * @see #addDrawerListener(DrawerListener)
     * @see #removeDrawerListener(DrawerListener)
     */
    @Deprecated
    public void setDrawerListener(DrawerListener listener) {
        // The logic in this method emulates what we had before support for multiple
        // registered listeners.
        if (mListener != null) {
            removeDrawerListener(mListener);
        }
        if (listener != null) {
            addDrawerListener(listener);
        }
        // Update the deprecated field so that we can remove the passed listener the next
        // time we're called
        mListener = listener;
    }

    /**
     * Adds the specified listener to the list of listeners that will be notified of drawer events.
     * @param listener Listener to notify when drawer events occur.
     * @see #removeDrawerListener(DrawerListener)
     *
     * 将指定的监听器添加到将收到drawer事件通知的侦听器列表中。
     * @param listener 监听器，用于在drawer事件发生时通知。
     * @see #removeDrawerListener(DrawerListener)
     */
    public void addDrawerListener(@NonNull DrawerListener listener) {
        if (listener == null) {
            return;
        }
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }

    /**
     * Removes the specified listener from the list of listeners that will be notified of drawer
     * events.
     * @param listener Listener to remove from being notified of drawer events
     * @see #addDrawerListener(DrawerListener)
     *
     * 从将收到drawer事件通知的监听器列表中删除指定的监听器。
     * @param listener 监听器以从drawer事件通知中删除
     * @see #addDrawerListener(DrawerListener)
     */
    public void removeDrawerListener(@NonNull DrawerListener listener) {
        if (listener == null) {
            return;
        }
        if (mListeners == null) {
            // This can happen if this method is called before the first call to addDrawerListener
            return;
        }
        mListeners.remove(listener);
    }

    /**
     * Enable or disable interaction with all drawers.
     *
     * <p>This allows the application to restrict the user's ability to open or close
     * any drawer within this layout. DrawerLayout will still respond to calls to
     * {@link #openDrawer(int)}, {@link #closeDrawer(int)} and friends if a drawer is locked.</p>
     *
     * <p>Locking drawers open or closed will implicitly open or close
     * any drawers as appropriate.</p>
     *
     * @param lockMode The new lock mode for the given drawer. One of {@link #LOCK_MODE_UNLOCKED},
     *                 {@link #LOCK_MODE_LOCKED_CLOSED} or {@link #LOCK_MODE_LOCKED_OPEN}.
     *
     *
     * 启用或禁用与所有的交互drawers.
     *
     * <p>这允许应用程序限制用户打开或关闭该布局中的任何抽屉的能力。
     *                 如果drawer被锁定，DrawerLayout仍然会响应对{@link #openDrawer(int)}， {@link #closeDrawer(int)}和朋友的调用。</p>
     *
     * <p>锁定drawer的打开或关闭将隐式地打开或关闭任何适当的drawer。</p>
     *
     * @param lockMode 给定drawer的新锁定模式。r. 包括：{@link #LOCK_MODE_UNLOCKED},
     *                 {@link #LOCK_MODE_LOCKED_CLOSED} 和 {@link #LOCK_MODE_LOCKED_OPEN}.
     */
    public void setDrawerLockMode(@LockMode int lockMode) {
        setDrawerLockMode(lockMode, Gravity.LEFT);
        setDrawerLockMode(lockMode, Gravity.RIGHT);
    }

    /**
     * Enable or disable interaction with the given drawer.
     *
     * <p>This allows the application to restrict the user's ability to open or close
     * the given drawer. DrawerLayout will still respond to calls to {@link #openDrawer(int)},
     * {@link #closeDrawer(int)} and friends if a drawer is locked.</p>
     *
     * <p>Locking a drawer open or closed will implicitly open or close
     * that drawer as appropriate.</p>
     *
     * @param lockMode The new lock mode for the given drawer. One of {@link #LOCK_MODE_UNLOCKED},
     *                 {@link #LOCK_MODE_LOCKED_CLOSED} or {@link #LOCK_MODE_LOCKED_OPEN}.
     * @param edgeGravity Gravity.LEFT, RIGHT, START or END.
     *                    Expresses which drawer to change the mode for.
     *
     * @see #LOCK_MODE_UNLOCKED
     * @see #LOCK_MODE_LOCKED_CLOSED
     * @see #LOCK_MODE_LOCKED_OPEN
     *
     *
     * 启用或禁用与给定drawer的交互。
     *
     * <p>这允许应用程序限制用户打开或关闭给定抽屉的能力。
     * 如果drawer被锁定，DrawerLayout仍然会响应对{@link #openDrawer(int)}，{@link #closeDrawer(int)}和朋友的调用。</p>
     *
     * <p>把一个drawer打开或关上，就意味着适当地打开或关闭那个抽屉。</p>
     *
     * @param lockMode 给定drawer的新锁定模式. 包括： {@link #LOCK_MODE_UNLOCKED},
     *                 {@link #LOCK_MODE_LOCKED_CLOSED} , {@link #LOCK_MODE_LOCKED_OPEN}.
     * @param edgeGravity Gravity.LEFT, RIGHT, START or END.
     *                    Expresses which drawer to change the mode for.
     *
     * @see #LOCK_MODE_UNLOCKED
     * @see #LOCK_MODE_LOCKED_CLOSED
     * @see #LOCK_MODE_LOCKED_OPEN
     */
    public void setDrawerLockMode(@LockMode int lockMode, @EdgeGravity int edgeGravity) {
        final int absGravity = GravityCompat.getAbsoluteGravity(edgeGravity,
                ViewCompat.getLayoutDirection(this));

        switch (edgeGravity) {
            case Gravity.LEFT:
                mLockModeLeft = lockMode;
                break;
            case Gravity.RIGHT:
                mLockModeRight = lockMode;
                break;
            case GravityCompat.START:
                mLockModeStart = lockMode;
                break;
            case GravityCompat.END:
                mLockModeEnd = lockMode;
                break;
        }

        if (lockMode != LOCK_MODE_UNLOCKED) {
            // Cancel interaction in progress
            final ViewDragHelper helper = absGravity == Gravity.LEFT ? mLeftDragger : mRightDragger;
            helper.cancel();
        }
        switch (lockMode) {
            case LOCK_MODE_LOCKED_OPEN:
                final View toOpen = findDrawerWithGravity(absGravity);
                if (toOpen != null) {
                    openDrawer(toOpen);
                }
                break;
            case LOCK_MODE_LOCKED_CLOSED:
                final View toClose = findDrawerWithGravity(absGravity);
                if (toClose != null) {
                    closeDrawer(toClose);
                }
                break;
            // default: do nothing
        }
    }

    /**
     * Enables or disables interaction with a given drawer.
     *
     * <p>This allows the application to restrict the user's ability to open or close a given drawer. If the drawer is locked,
     * DrawerLayout will still respond to calls to {@link #openDrawer(int)}, {@link #closeDrawer(int)} and a friend.</p>
     *
     * <p>Opening or closing a drawer means opening or closing that drawer properly.</p>
     *
     * @param lockMode A new locking mode for the given drawer. One of {@link #LOCK_MODE_UNLOCKED},
     *                 {@link #LOCK_MODE_LOCKED_CLOSED} or {@link #LOCK_MODE_LOCKED_OPEN}.
     * @param drawerView The drawer view whose locking mode is to be changed
     *
     * @see #LOCK_MODE_UNLOCKED
     * @see #LOCK_MODE_LOCKED_CLOSED
     * @see #LOCK_MODE_LOCKED_OPEN
     *
     *
     * 启用或禁用与给定drawer的交互。
     *
     * <p>这允许应用程序限制用户打开或关闭给定drawer的能力。
     * 如果drawer被锁定，DrawerLayout仍然会响应对{@link #openDrawer(int)}， {@link #closeDrawer(int)}和朋友的调用。</p>
     *
     * <p>把一个drawer打开或关上，就意味着适当地打开或关闭那个drawer。</p>
     *
     * @param lockMode 给定drawer的新锁定模式。 包括： {@link #LOCK_MODE_UNLOCKED},
     *                 {@link #LOCK_MODE_LOCKED_CLOSED} , {@link #LOCK_MODE_LOCKED_OPEN}.
     * @param drawerView 要更改其锁定模式的drawer视图
     *
     * @see #LOCK_MODE_UNLOCKED
     * @see #LOCK_MODE_LOCKED_CLOSED
     * @see #LOCK_MODE_LOCKED_OPEN
     */
    @SuppressLint("WrongConstant")
    public void setDrawerLockMode(@LockMode int lockMode, @NonNull View drawerView) {
        if (!isDrawerView(drawerView)) {
            throw new IllegalArgumentException("View " + drawerView + " is not a "
                    + "drawer with appropriate layout_gravity");
        }
        final int gravity = ((LayoutParams) drawerView.getLayoutParams()).gravity;
        setDrawerLockMode(lockMode, gravity);
    }

    /**
     * Check the lock mode of the drawer with the given gravity.
     * @param edgeGravity Gravity of the drawer to check
     * @return one of {@link #LOCK_MODE_UNLOCKED}, {@link #LOCK_MODE_LOCKED_CLOSED} or
     *         {@link #LOCK_MODE_LOCKED_OPEN}.
     *
     * 检查给定重力下drawer的锁紧方式。
     * @param edgeGravity 检查drawer的重力
     * @return {@link #LOCK_MODE_UNLOCKED}/{@link #LOCK_MODE_LOCKED_CLOSED}/{@link #LOCK_MODE_LOCKED_OPEN}
     */
    @LockMode
    public int getDrawerLockMode(@EdgeGravity int edgeGravity) {
        int layoutDirection = ViewCompat.getLayoutDirection(this);

        switch (edgeGravity) {
            case Gravity.LEFT:
                if (mLockModeLeft != LOCK_MODE_UNDEFINED) {
                    return mLockModeLeft;
                }
                int leftLockMode = (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR)
                        ? mLockModeStart : mLockModeEnd;
                if (leftLockMode != LOCK_MODE_UNDEFINED) {
                    return leftLockMode;
                }
                break;
            case Gravity.RIGHT:
                if (mLockModeRight != LOCK_MODE_UNDEFINED) {
                    return mLockModeRight;
                }
                int rightLockMode = (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR)
                        ? mLockModeEnd : mLockModeStart;
                if (rightLockMode != LOCK_MODE_UNDEFINED) {
                    return rightLockMode;
                }
                break;
            case GravityCompat.START:
                if (mLockModeStart != LOCK_MODE_UNDEFINED) {
                    return mLockModeStart;
                }
                int startLockMode = (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR)
                        ? mLockModeLeft : mLockModeRight;
                if (startLockMode != LOCK_MODE_UNDEFINED) {
                    return startLockMode;
                }
                break;
            case GravityCompat.END:
                if (mLockModeEnd != LOCK_MODE_UNDEFINED) {
                    return mLockModeEnd;
                }
                int endLockMode = (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR)
                        ? mLockModeRight : mLockModeLeft;
                if (endLockMode != LOCK_MODE_UNDEFINED) {
                    return endLockMode;
                }
                break;
        }

        return LOCK_MODE_UNLOCKED;
    }

    /**
     * Check the lock mode of the given drawer view.
     * @param drawerView Drawer view to check lock mode
     * @return one of {@link #LOCK_MODE_UNLOCKED}, {@link #LOCK_MODE_LOCKED_CLOSED} or
     *         {@link #LOCK_MODE_LOCKED_OPEN}.
     *
     * 检查给定drawer视图的锁定模式
     * @param drawerView drawer视图以检查锁定模式
     * @return {@link #LOCK_MODE_UNLOCKED}/{@link #LOCK_MODE_LOCKED_CLOSED}/{@link #LOCK_MODE_LOCKED_OPEN}
     */
    @SuppressLint("WrongConstant")
    @LockMode
    public int getDrawerLockMode(@NonNull View drawerView) {
        if (!isDrawerView(drawerView)) {
            throw new IllegalArgumentException("View " + drawerView + " is not a drawer");
        }
        final int drawerGravity = ((LayoutParams) drawerView.getLayoutParams()).gravity;
        return getDrawerLockMode(drawerGravity);
    }

    /**
     * Sets the title of the drawer with the given gravity.
     * When accessibility is turned on, this is the title that will be used to
     * identify the drawer to the active accessibility service.
     * @param edgeGravity Gravity.LEFT, RIGHT, START or END. Expresses which
     *            drawer to set the title for.
     * @param title The title for the drawer.
     *
     * 设置具有给定重力的drawer的标题。
     * 当可访问性被打开时drawer，这个标题将用于标识活动可访问性服务的drawer。
     * @param edgeGravity 重力。左，右，开始或结束。表示要为哪个drawer设置标题。
     * @param title drawer的标题。
     */
    public void setDrawerTitle(@EdgeGravity int edgeGravity, @Nullable CharSequence title) {
        final int absGravity = GravityCompat.getAbsoluteGravity(
                edgeGravity, ViewCompat.getLayoutDirection(this));
        if (absGravity == Gravity.LEFT) {
            mTitleLeft = title;
        } else if (absGravity == Gravity.RIGHT) {
            mTitleRight = title;
        }
    }

    /**
     * Returns the title of the drawer with the given gravity.
     * @param edgeGravity Gravity.LEFT, RIGHT, START or END. Expresses which
     *            drawer to return the title for.
     * @return The title of the drawer, or null if none set.
     * @see #setDrawerTitle(int, CharSequence)
     *
     * 返回具有给定重力的drawer的标题。
     * @param edgeGravity 重力。左，右，开始或结束。表示要为哪个drawer返回标题。
     * @return drawer的标题，如果没有设置则为空。
     * @see #setDrawerTitle(int, CharSequence)
     */
    @Nullable
    public CharSequence getDrawerTitle(@EdgeGravity int edgeGravity) {
        final int absGravity = GravityCompat.getAbsoluteGravity(
                edgeGravity, ViewCompat.getLayoutDirection(this));
        if (absGravity == Gravity.LEFT) {
            return mTitleLeft;
        } else if (absGravity == Gravity.RIGHT) {
            return mTitleRight;
        }
        return null;
    }

    /**
     * Resolve the shared state of all drawers from the component ViewDragHelpers.
     * Should be called whenever a ViewDragHelper's state changes.
     *
     * 解析组件ViewDragHelpers中所有drawers的共享状态。
     * 应该在ViewDragHelper的状态改变时调用。
     */
    void updateDrawerState(int forGravity, @State int activeState, View activeDrawer) {
        final int leftState = mLeftDragger.getViewDragState();
        final int rightState = mRightDragger.getViewDragState();

        final int state;
        if (leftState == STATE_DRAGGING || rightState == STATE_DRAGGING) {
            state = STATE_DRAGGING;
        } else if (leftState == STATE_SETTLING || rightState == STATE_SETTLING) {
            state = STATE_SETTLING;
        } else {
            state = STATE_IDLE;
        }

        if (activeDrawer != null && activeState == STATE_IDLE) {
            final LayoutParams lp = (LayoutParams) activeDrawer.getLayoutParams();
            if (lp.onScreen == 0) {
                dispatchOnDrawerClosed(activeDrawer);
            } else if (lp.onScreen == 1) {
                dispatchOnDrawerOpened(activeDrawer);
            }
        }

        if (state != mDrawerState) {
            mDrawerState = state;

            if (mListeners != null) {
                // Notify the listeners. Do that from the end of the list so that if a listener
                // removes itself as the result of being called, it won't mess up with our iteration
                int listenerCount = mListeners.size();
                for (int i = listenerCount - 1; i >= 0; i--) {
                    mListeners.get(i).onDrawerStateChanged(state);
                }
            }
        }
    }

    void dispatchOnDrawerClosed(View drawerView) {
        final LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();
        if ((lp.openState & LayoutParams.FLAG_IS_OPENED) == 1) {
            lp.openState = 0;

            if (mListeners != null) {
                // Notify the listeners. Do that from the end of the list so that if a listener
                // removes itself as the result of being called, it won't mess up with our iteration
                int listenerCount = mListeners.size();
                for (int i = listenerCount - 1; i >= 0; i--) {
                    mListeners.get(i).onDrawerClosed(drawerView);
                }
            }

            updateChildrenImportantForAccessibility(drawerView, false);

            // Only send WINDOW_STATE_CHANGE if the host has window focus. This
            // may change if support for multiple foreground windows (e.g. IME)
            // improves.
            if (hasWindowFocus()) {
                final View rootView = getRootView();
                if (rootView != null) {
                    rootView.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                }
            }
        }
    }

    void dispatchOnDrawerOpened(View drawerView) {
        final LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();
        if ((lp.openState & LayoutParams.FLAG_IS_OPENED) == 0) {
            lp.openState = LayoutParams.FLAG_IS_OPENED;
            if (mListeners != null) {
                // Notify the listeners. Do that from the end of the list so that if a listener
                // removes itself as the result of being called, it won't mess up with our iteration
                int listenerCount = mListeners.size();
                for (int i = listenerCount - 1; i >= 0; i--) {
                    mListeners.get(i).onDrawerOpened(drawerView);
                }
            }

            updateChildrenImportantForAccessibility(drawerView, true);

            // Only send WINDOW_STATE_CHANGE if the host has window focus.
            if (hasWindowFocus()) {
                sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            }
        }
    }

    private void updateChildrenImportantForAccessibility(View drawerView, boolean isDrawerOpen) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if ((!isDrawerOpen && !isDrawerView(child)) || (isDrawerOpen && child == drawerView)) {
                // Drawer is closed and this is a content view or this is an
                // open drawer view, so it should be visible.
                ViewCompat.setImportantForAccessibility(child,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
            } else {
                ViewCompat.setImportantForAccessibility(child,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            }
        }
    }

    void dispatchOnDrawerSlide(View drawerView, float slideOffset) {
        if (mListeners != null) {
            // Notify the listeners. Do that from the end of the list so that if a listener
            // removes itself as the result of being called, it won't mess up with our iteration
            int listenerCount = mListeners.size();
            for (int i = listenerCount - 1; i >= 0; i--) {
                mListeners.get(i).onDrawerSlide(drawerView, slideOffset);
            }
        }
    }

    void setDrawerViewOffset(View drawerView, float slideOffset) {
        final LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();
        if (slideOffset == lp.onScreen) {
            return;
        }

        lp.onScreen = slideOffset;
        dispatchOnDrawerSlide(drawerView, slideOffset);
    }

    float getDrawerViewOffset(View drawerView) {
        return ((LayoutParams) drawerView.getLayoutParams()).onScreen;
    }

    /**
     * @return the absolute gravity of the child drawerView, resolved according
     *         to the current layout direction
     *
     * @return 子drawerView的绝对重力，根据当前布局方向解析
     */
    int getDrawerViewAbsoluteGravity(View drawerView) {
        final int gravity = ((LayoutParams) drawerView.getLayoutParams()).gravity;
        return GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this));
    }

    boolean checkDrawerViewAbsoluteGravity(View drawerView, int checkFor) {
        final int absGravity = getDrawerViewAbsoluteGravity(drawerView);
        return (absGravity & checkFor) == checkFor;
    }

    View findOpenDrawer() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams childLp = (LayoutParams) child.getLayoutParams();
            if ((childLp.openState & LayoutParams.FLAG_IS_OPENED) == 1) {
                return child;
            }
        }
        return null;
    }

    void moveDrawerToOffset(View drawerView, float slideOffset) {
        final float oldOffset = getDrawerViewOffset(drawerView);
        final int width = drawerView.getWidth();
        final int oldPos = (int) (width * oldOffset);
        final int newPos = (int) (width * slideOffset);
        final int dx = newPos - oldPos;

        drawerView.offsetLeftAndRight(
                checkDrawerViewAbsoluteGravity(drawerView, Gravity.LEFT) ? dx : -dx);
        setDrawerViewOffset(drawerView, slideOffset);
    }

    /**
     * @param gravity the gravity of the child to return. If specified as a
     *            relative value, it will be resolved according to the current
     *            layout direction.
     * @return the drawer with the specified gravity
     *
     * @param gravity 孩子的重心要回归。如果指定为相对值，则将根据当前布局方向解析。
     * @return 有指定重力的drawer
     */
    View findDrawerWithGravity(int gravity) {
        final int absHorizGravity = GravityCompat.getAbsoluteGravity(
                gravity, ViewCompat.getLayoutDirection(this)) & Gravity.HORIZONTAL_GRAVITY_MASK;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final int childAbsGravity = getDrawerViewAbsoluteGravity(child);
            if ((childAbsGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == absHorizGravity) {
                return child;
            }
        }
        return null;
    }

    /**
     * Simple gravity to string - only supports LEFT and RIGHT for debugging output.
     * @param gravity Absolute gravity value
     * @return LEFT or RIGHT as appropriate, or a hex string
     *
     * 简单的重力到字符串-只支持左和右调试输出。
     * @param gravity 绝对重力值
     * @return 左或右(视情况而定)，或十六进制字符串
     */
    static String gravityToString(@EdgeGravity int gravity) {
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            return "LEFT";
        }
        if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            return "RIGHT";
        }
        return Integer.toHexString(gravity);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            if (isInEditMode()) {
                // Don't crash the layout editor. Consume all of the space if specified
                // or pick a magic number from thin air otherwise.
                // TODO Better communication with tools of this bogus state.
                // It will crash on a real device.
                if (widthMode == MeasureSpec.AT_MOST) {
                    widthMode = MeasureSpec.EXACTLY;
                } else if (widthMode == MeasureSpec.UNSPECIFIED) {
                    widthMode = MeasureSpec.EXACTLY;
                    widthSize = 300;
                }
                if (heightMode == MeasureSpec.AT_MOST) {
                    heightMode = MeasureSpec.EXACTLY;
                } else if (heightMode == MeasureSpec.UNSPECIFIED) {
                    heightMode = MeasureSpec.EXACTLY;
                    heightSize = 300;
                }
            } else {
                throw new IllegalArgumentException(
                        "DrawerLayout must be measured with MeasureSpec.EXACTLY.");
            }
        }

        setMeasuredDimension(widthSize, heightSize);

        final boolean applyInsets = mLastInsets != null && ViewCompat.getFitsSystemWindows(this);
        final int layoutDirection = ViewCompat.getLayoutDirection(this);

        // Only one drawer is permitted along each vertical edge (left / right). These two booleans
        // are tracking the presence of the edge drawers.
        boolean hasDrawerOnLeftEdge = false;
        boolean hasDrawerOnRightEdge = false;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (applyInsets) {
                final int cgrav = GravityCompat.getAbsoluteGravity(lp.gravity, layoutDirection);
                if (ViewCompat.getFitsSystemWindows(child)) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        WindowInsets wi = (WindowInsets) mLastInsets;
                        if (cgrav == Gravity.LEFT) {
                            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(),
                                    wi.getSystemWindowInsetTop(), 0,
                                    wi.getSystemWindowInsetBottom());
                        } else if (cgrav == Gravity.RIGHT) {
                            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(),
                                    wi.getSystemWindowInsetRight(),
                                    wi.getSystemWindowInsetBottom());
                        }
                        child.dispatchApplyWindowInsets(wi);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        WindowInsets wi = (WindowInsets) mLastInsets;
                        if (cgrav == Gravity.LEFT) {
                            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(),
                                    wi.getSystemWindowInsetTop(), 0,
                                    wi.getSystemWindowInsetBottom());
                        } else if (cgrav == Gravity.RIGHT) {
                            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(),
                                    wi.getSystemWindowInsetRight(),
                                    wi.getSystemWindowInsetBottom());
                        }
                        lp.leftMargin = wi.getSystemWindowInsetLeft();
                        lp.topMargin = wi.getSystemWindowInsetTop();
                        lp.rightMargin = wi.getSystemWindowInsetRight();
                        lp.bottomMargin = wi.getSystemWindowInsetBottom();
                    }
                }
            }

            if (isContentView(child)) {
                // Content views get measured at exactly the layout's size.
                final int contentWidthSpec = MeasureSpec.makeMeasureSpec(
                        widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
                final int contentHeightSpec = MeasureSpec.makeMeasureSpec(
                        heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
                child.measure(contentWidthSpec, contentHeightSpec);
            } else if (isDrawerView(child)) {
                if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
                    if (ViewCompat.getElevation(child) != mDrawerElevation) {
                        ViewCompat.setElevation(child, mDrawerElevation);
                    }
                }
                final @EdgeGravity int childGravity =
                        getDrawerViewAbsoluteGravity(child) & Gravity.HORIZONTAL_GRAVITY_MASK;
                // Note that the isDrawerView check guarantees that childGravity here is either
                // LEFT or RIGHT
                boolean isLeftEdgeDrawer = (childGravity == Gravity.LEFT);
                if ((isLeftEdgeDrawer && hasDrawerOnLeftEdge)
                        || (!isLeftEdgeDrawer && hasDrawerOnRightEdge)) {
                    throw new IllegalStateException("Child drawer has absolute gravity "
                            + gravityToString(childGravity) + " but this " + TAG + " already has a "
                            + "drawer view along that edge");
                }
                if (isLeftEdgeDrawer) {
                    hasDrawerOnLeftEdge = true;
                } else {
                    hasDrawerOnRightEdge = true;
                }
                final int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                        mMinDrawerMargin + lp.leftMargin + lp.rightMargin,
                        lp.width);
                final int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                        lp.topMargin + lp.bottomMargin,
                        lp.height);
                child.measure(drawerWidthSpec, drawerHeightSpec);
            } else {
                throw new IllegalStateException("Child " + child + " at index " + i
                        + " does not have a valid layout_gravity - must be Gravity.LEFT, "
                        + "Gravity.RIGHT or Gravity.NO_GRAVITY");
            }
        }
    }

    private void resolveShadowDrawables() {
        if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
            return;
        }
        mShadowLeftResolved = resolveLeftShadow();
        mShadowRightResolved = resolveRightShadow();
    }

    private Drawable resolveLeftShadow() {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        // Prefer shadows defined with start/end gravity over left and right.
        if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
            if (mShadowStart != null) {
                // Correct drawable layout direction, if needed.
                mirror(mShadowStart, layoutDirection);
                return mShadowStart;
            }
        } else {
            if (mShadowEnd != null) {
                // Correct drawable layout direction, if needed.
                mirror(mShadowEnd, layoutDirection);
                return mShadowEnd;
            }
        }
        return mShadowLeft;
    }

    private Drawable resolveRightShadow() {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
            if (mShadowEnd != null) {
                // Correct drawable layout direction, if needed.
                mirror(mShadowEnd, layoutDirection);
                return mShadowEnd;
            }
        } else {
            if (mShadowStart != null) {
                // Correct drawable layout direction, if needed.
                mirror(mShadowStart, layoutDirection);
                return mShadowStart;
            }
        }
        return mShadowRight;
    }

    /**
     * Change the layout direction of the given drawable.
     * Return true if auto-mirror is supported and drawable's layout direction can be changed.
     * Otherwise, return false.
     *
     * 改变给定绘图对象的布局方向。
     * 如果支持自动镜像且可绘制对象的布局方向可以更改，则返回true。 否则,返回false。
     */
    private boolean mirror(Drawable drawable, int layoutDirection) {
        if (drawable == null || !DrawableCompat.isAutoMirrored(drawable)) {
            return false;
        }

        DrawableCompat.setLayoutDirection(drawable, layoutDirection);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        final int width = r - l;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (isContentView(child)) {
                child.layout(lp.leftMargin, lp.topMargin,
                        lp.leftMargin + child.getMeasuredWidth(),
                        lp.topMargin + child.getMeasuredHeight());
            } else { // Drawer, if it wasn't onMeasure would have thrown an exception.
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                int childLeft;

                final float newOffset;
                if (checkDrawerViewAbsoluteGravity(child, Gravity.LEFT)) {
                    childLeft = -childWidth + (int) (childWidth * lp.onScreen);
                    newOffset = (float) (childWidth + childLeft) / childWidth;
                } else { // Right; onMeasure checked for us.
                    childLeft = width - (int) (childWidth * lp.onScreen);
                    newOffset = (float) (width - childLeft) / childWidth;
                }

                final boolean changeOffset = newOffset != lp.onScreen;

                final int vgrav = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (vgrav) {
                    default:
                    case Gravity.TOP: {
                        child.layout(childLeft, lp.topMargin, childLeft + childWidth,
                                lp.topMargin + childHeight);
                        break;
                    }

                    case Gravity.BOTTOM: {
                        final int height = b - t;
                        child.layout(childLeft,
                                height - lp.bottomMargin - child.getMeasuredHeight(),
                                childLeft + childWidth,
                                height - lp.bottomMargin);
                        break;
                    }

                    case Gravity.CENTER_VERTICAL: {
                        final int height = b - t;
                        int childTop = (height - childHeight) / 2;

                        // Offset for margins. If things don't fit right because of
                        // bad measurement before, oh well.
                        if (childTop < lp.topMargin) {
                            childTop = lp.topMargin;
                        } else if (childTop + childHeight > height - lp.bottomMargin) {
                            childTop = height - lp.bottomMargin - childHeight;
                        }
                        child.layout(childLeft, childTop, childLeft + childWidth,
                                childTop + childHeight);
                        break;
                    }
                }

                if (changeOffset) {
                    setDrawerViewOffset(child, newOffset);
                }

                final int newVisibility = lp.onScreen > 0 ? VISIBLE : INVISIBLE;
                if (child.getVisibility() != newVisibility) {
                    child.setVisibility(newVisibility);
                }
            }
        }
        mInLayout = false;
        mFirstLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    public void computeScroll() {
        final int childCount = getChildCount();
        float scrimOpacity = 0;
        for (int i = 0; i < childCount; i++) {
            final float onscreen = ((LayoutParams) getChildAt(i).getLayoutParams()).onScreen;
            scrimOpacity = Math.max(scrimOpacity, onscreen);
        }
        mScrimOpacity = scrimOpacity;

        boolean leftDraggerSettling = mLeftDragger.continueSettling(true);
        boolean rightDraggerSettling = mRightDragger.continueSettling(true);
        if (leftDraggerSettling || rightDraggerSettling) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private static boolean hasOpaqueBackground(View v) {
        final Drawable bg = v.getBackground();
        if (bg != null) {
            return bg.getOpacity() == PixelFormat.OPAQUE;
        }
        return false;
    }

    /**
     * Set a drawable to draw in the insets area for the status bar.
     * Note that this will only be activated if this DrawerLayout fitsSystemWindows.
     * @param bg Background drawable to draw behind the status bar
     *
     * 设置一个可绘制对象，以便在状态栏的insets区域绘制。
     * 注意，只有当这个DrawerLayout fitsSystemWindows时才会被激活。
     * @param bg 背景绘制可绘制在状态栏后面
     */
    public void setStatusBarBackground(@Nullable Drawable bg) {
        mStatusBarBackground = bg;
        invalidate();
    }

    /**
     * Gets the drawable used to draw in the insets area for the status bar.
     * @return The status bar background drawable, or null if none set
     *
     * 获取用于在状态栏的插图区域中绘制的可绘制对象。
     * @return 状态栏背景可绘制，如果没有设置则为空
     */
    @Nullable
    public Drawable getStatusBarBackgroundDrawable() {
        return mStatusBarBackground;
    }

    /**
     * Set a drawable to draw in the insets area for the status bar.
     * Note that this will only be activated if this DrawerLayout fitsSystemWindows.
     * @param resId Resource id of a background drawable to draw behind the status bar
     *
     * 设置一个可绘制对象，以便在状态栏的insets区域绘制。
     * 注意，只有当这个DrawerLayout fitsSystemWindows时才会被激活。
     * @param resId 可以在状态栏后面绘制的背景绘制的资源id
     */
    public void setStatusBarBackground(int resId) {
        mStatusBarBackground = resId != 0 ? ContextCompat.getDrawable(getContext(), resId) : null;
        invalidate();
    }

    /**
     * Set a drawable to draw in the insets area for the status bar.
     * Note that this will only be activated if this DrawerLayout fitsSystemWindows.
     * @param color Color to use as a background drawable to draw behind the status bar
     *              in 0xAARRGGBB format.
     *
     * 设置一个可绘制对象，以便在状态栏的insets区域绘制。
     * 注意，只有当这个DrawerLayout fitsSystemWindows时才会被激活。
     * @param color 颜色用作背景绘图，可在状态栏后面以0xAARRGGBB格式绘制。
     */
    public void setStatusBarBackgroundColor(@ColorInt int color) {
        mStatusBarBackground = new ColorDrawable(color);
        invalidate();
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        resolveShadowDrawables();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (mDrawStatusBarBackground && mStatusBarBackground != null) {
            final int inset;
            if (Build.VERSION.SDK_INT >= 21) {
                inset = mLastInsets != null
                        ? ((WindowInsets) mLastInsets).getSystemWindowInsetTop() : 0;
            } else {
                inset = 0;
            }
            if (inset > 0) {
                mStatusBarBackground.setBounds(0, 0, getWidth(), inset);
                mStatusBarBackground.draw(c);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final int height = getHeight();
        final boolean drawingContent = isContentView(child);
        int clipLeft = 0, clipRight = getWidth();

        final int restoreCount = canvas.save();
        if (drawingContent) {
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View v = getChildAt(i);
                if (v == child || v.getVisibility() != VISIBLE
                        || !hasOpaqueBackground(v) || !isDrawerView(v)
                        || v.getHeight() < height) {
                    continue;
                }

                if (checkDrawerViewAbsoluteGravity(v, Gravity.LEFT)) {
                    final int vright = v.getRight();
                    if (vright > clipLeft) clipLeft = vright;
                } else {
                    final int vleft = v.getLeft();
                    if (vleft < clipRight) clipRight = vleft;
                }
            }
            canvas.clipRect(clipLeft, 0, clipRight, getHeight());
        }
        final boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);

        if (mScrimOpacity > 0 && drawingContent) {
            final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
            final int imag = (int) (baseAlpha * mScrimOpacity);
            final int color = imag << 24 | (mScrimColor & 0xffffff);
            mScrimPaint.setColor(color);

            canvas.drawRect(clipLeft, 0, clipRight, getHeight(), mScrimPaint);
        } else if (mShadowLeftResolved != null
                &&  checkDrawerViewAbsoluteGravity(child, Gravity.LEFT)) {
            final int shadowWidth = mShadowLeftResolved.getIntrinsicWidth();
            final int childRight = child.getRight();
            final int drawerPeekDistance = mLeftDragger.getEdgeSize();
            final float alpha =
                    Math.max(0, Math.min((float) childRight / drawerPeekDistance, 1.f));
            mShadowLeftResolved.setBounds(childRight, child.getTop(),
                    childRight + shadowWidth, child.getBottom());
            mShadowLeftResolved.setAlpha((int) (0xff * alpha));
            mShadowLeftResolved.draw(canvas);
        } else if (mShadowRightResolved != null
                &&  checkDrawerViewAbsoluteGravity(child, Gravity.RIGHT)) {
            final int shadowWidth = mShadowRightResolved.getIntrinsicWidth();
            final int childLeft = child.getLeft();
            final int showing = getWidth() - childLeft;
            final int drawerPeekDistance = mRightDragger.getEdgeSize();
            final float alpha =
                    Math.max(0, Math.min((float) showing / drawerPeekDistance, 1.f));
            mShadowRightResolved.setBounds(childLeft - shadowWidth, child.getTop(),
                    childLeft, child.getBottom());
            mShadowRightResolved.setAlpha((int) (0xff * alpha));
            mShadowRightResolved.draw(canvas);
        }
        return result;
    }

    boolean isContentView(View child) {
        return ((LayoutParams) child.getLayoutParams()).gravity == Gravity.NO_GRAVITY;
    }

    boolean isDrawerView(View child) {
        final int gravity = ((LayoutParams) child.getLayoutParams()).gravity;
        final int absGravity = GravityCompat.getAbsoluteGravity(gravity,
                ViewCompat.getLayoutDirection(child));
        if ((absGravity & Gravity.LEFT) != 0) {
            // This child is a left-edge drawer
            return true;
        }
        if ((absGravity & Gravity.RIGHT) != 0) {
            // This child is a right-edge drawer
            return true;
        }
        return false;
    }

    @SuppressWarnings("ShortCircuitBoolean")
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            final int action = ev.getActionMasked();

            // "|" used deliberately here; both methods should be invoked.
            final boolean interceptForDrag = mLeftDragger.shouldInterceptTouchEvent(ev)
                    | mRightDragger.shouldInterceptTouchEvent(ev);

            boolean interceptForTap = false;

            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    final float x = ev.getX();
                    final float y = ev.getY();
                    mInitialMotionX = x;
                    mInitialMotionY = y;
                    if (mScrimOpacity > 0) {
                        final View child = mLeftDragger.findTopChildUnder((int) x, (int) y);
                        if (child != null && isContentView(child)) {
                            interceptForTap = true;
                        }
                    }
                    mDisallowInterceptRequested = false;
                    mChildrenCanceledTouch = false;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    // If we cross the touch slop, don't perform the delayed peek for an edge touch.
                    if (mLeftDragger.checkTouchSlop(ViewDragHelper.DIRECTION_ALL)) {
                        mLeftCallback.removeCallbacks();
                        mRightCallback.removeCallbacks();
                    }
                    break;
                }

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    closeDrawers(true);
                    mDisallowInterceptRequested = false;
                    mChildrenCanceledTouch = false;
                }
            }

            return interceptForDrag || interceptForTap || hasPeekingDrawer() || mChildrenCanceledTouch;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            mLeftDragger.processTouchEvent(ev);
            mRightDragger.processTouchEvent(ev);

            final int action = ev.getAction();
            boolean wantTouchEvents = true;

            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    final float x = ev.getX();
                    final float y = ev.getY();
                    mInitialMotionX = x;
                    mInitialMotionY = y;
                    mDisallowInterceptRequested = false;
                    mChildrenCanceledTouch = false;
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    final float x = ev.getX();
                    final float y = ev.getY();
                    boolean peekingOnly = true;
                    final View touchedView = mLeftDragger.findTopChildUnder((int) x, (int) y);
                    if (touchedView != null && isContentView(touchedView)) {
                        final float dx = x - mInitialMotionX;
                        final float dy = y - mInitialMotionY;
                        final int slop = mLeftDragger.getTouchSlop();
                        if (dx * dx + dy * dy < slop * slop) {
                            // Taps close a dimmed open drawer but only if it isn't locked open.
                            final View openDrawer = findOpenDrawer();
                            if (openDrawer != null) {
                                peekingOnly = getDrawerLockMode(openDrawer) == LOCK_MODE_LOCKED_OPEN;
                            }
                        }
                    }
                    closeDrawers(peekingOnly);
                    mDisallowInterceptRequested = false;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    closeDrawers(true);
                    mDisallowInterceptRequested = false;
                    mChildrenCanceledTouch = false;
                    break;
                }
            }

            return wantTouchEvents;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (CHILDREN_DISALLOW_INTERCEPT
                || (!mLeftDragger.isEdgeTouched(ViewDragHelper.EDGE_LEFT)
                && !mRightDragger.isEdgeTouched(ViewDragHelper.EDGE_RIGHT))) {
            // If we have an edge touch we want to skip this and track it for later instead.
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
        mDisallowInterceptRequested = disallowIntercept;
        if (disallowIntercept) {
            closeDrawers(true);
        }
    }

    /**
     * Close all currently open drawer views by animating them out of view.
     *
     * 关闭所有当前打开的抽屉视图，将它们动画移出视图。
     */
    public void closeDrawers() {
        closeDrawers(false);
    }

    void closeDrawers(boolean peekingOnly) {
        boolean needsInvalidate = false;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (!isDrawerView(child) || (peekingOnly && !lp.isPeeking)) {
                continue;
            }

            final int childWidth = child.getWidth();

            if (checkDrawerViewAbsoluteGravity(child, Gravity.LEFT)) {
                needsInvalidate |= mLeftDragger.smoothSlideViewTo(child,
                        -childWidth, child.getTop());
            } else {
                needsInvalidate |= mRightDragger.smoothSlideViewTo(child,
                        getWidth(), child.getTop());
            }

            lp.isPeeking = false;
        }

        mLeftCallback.removeCallbacks();
        mRightCallback.removeCallbacks();

        if (needsInvalidate) {
            invalidate();
        }
    }

    /**
     * Open the specified drawer view by animating it into view.
     * @param drawerView Drawer view to open
     *
     * 通过在视图中设置动画打开指定的drawer视图。
     * @param drawerView 要打开的drawer视图
     */
    public void openDrawer(@NonNull View drawerView) {
        openDrawer(drawerView, true);
    }

    /**
     * Open the specified drawer view.
     * @param drawerView Drawer view to open
     * @param animate Whether opening of the drawer should be animated.
     *
     * 打开指定的drawer视图。
     * @param drawerView 要打开的drawer视图
     * @param animate drawer的打开是否应该动画。
     */
    public void openDrawer(@NonNull View drawerView, boolean animate) {
        if (!isDrawerView(drawerView)) {
            throw new IllegalArgumentException("View " + drawerView + " is not a sliding drawer");
        }

        final LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();
        if (mFirstLayout) {
            lp.onScreen = 1.f;
            lp.openState = LayoutParams.FLAG_IS_OPENED;

            updateChildrenImportantForAccessibility(drawerView, true);
        } else if (animate) {
            lp.openState |= LayoutParams.FLAG_IS_OPENING;

            if (checkDrawerViewAbsoluteGravity(drawerView, Gravity.LEFT)) {
                mLeftDragger.smoothSlideViewTo(drawerView, 0, drawerView.getTop());
            } else {
                mRightDragger.smoothSlideViewTo(drawerView, getWidth() - drawerView.getWidth(),
                        drawerView.getTop());
            }
        } else {
            moveDrawerToOffset(drawerView, 1.f);
            updateDrawerState(lp.gravity, STATE_IDLE, drawerView);
            drawerView.setVisibility(VISIBLE);
        }
        invalidate();
    }

    /**
     * Open the specified drawer by animating it out of view.
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     *
     * 通过将指定的drawer移出视图来打开它。
     * @param gravity 重力。向左移动左边drawer或重力。RIGHT for RIGHT .GravityCompat. start或GravityCompat。也可以使用END。
     */
    public void openDrawer(@EdgeGravity int gravity) {
        openDrawer(gravity, true);
    }

    /**
     * Open the specified drawer.
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     * @param animate Whether opening of the drawer should be animated.
     *
     * 打开指定的drawer。
     * @param gravity 重力。向左移动左边抽屉或重力。RIGHT for RIGHT .GravityCompat. start或GravityCompat。也可以使用END。
     * @param animate 抽屉的打开是否应该动画。
     */
    public void openDrawer(@EdgeGravity int gravity, boolean animate) {
        final View drawerView = findDrawerWithGravity(gravity);
        if (drawerView == null) {
            throw new IllegalArgumentException("No drawer view found with gravity "
                    + gravityToString(gravity));
        }
        openDrawer(drawerView, animate);
    }

    /**
     * Close the specified drawer view by animating it into view.
     * @param drawerView Drawer view to close
     *
     * 通过在视图中设置动画来关闭指定的drawer视图。
     * @param drawerView 要关闭的drawer视图
     */
    public void closeDrawer(@NonNull View drawerView) {
        closeDrawer(drawerView, true);
    }

    /**
     * Close the specified drawer view.
     * @param drawerView Drawer view to close
     * @param animate Whether closing of the drawer should be animated.
     *
     * 关闭指定的drawer视图。
     * @param drawerView 要关闭的drawer视图
     * @param animate 是否应该动画关闭drawer。
     */
    public void closeDrawer(@NonNull View drawerView, boolean animate) {
        if (!isDrawerView(drawerView)) {
            throw new IllegalArgumentException("View " + drawerView + " is not a sliding drawer");
        }

        final LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();
        if (mFirstLayout) {
            lp.onScreen = 0.f;
            lp.openState = 0;
        } else if (animate) {
            lp.openState |= LayoutParams.FLAG_IS_CLOSING;

            if (checkDrawerViewAbsoluteGravity(drawerView, Gravity.LEFT)) {
                mLeftDragger.smoothSlideViewTo(drawerView, -drawerView.getWidth(),
                        drawerView.getTop());
            } else {
                mRightDragger.smoothSlideViewTo(drawerView, getWidth(), drawerView.getTop());
            }
        } else {
            moveDrawerToOffset(drawerView, 0.f);
            updateDrawerState(lp.gravity, STATE_IDLE, drawerView);
            drawerView.setVisibility(INVISIBLE);
        }
        invalidate();
    }

    /**
     * Close the specified drawer by animating it out of view.
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     *
     * 通过将指定的drawer移出视图来关闭它。
     * @param gravity 重力。向左移动左边drawer或重力。对对对。GravityCompat。启动或GravityCompat。也可以使用END。
     */
    public void closeDrawer(@EdgeGravity int gravity) {
        closeDrawer(gravity, true);
    }

    /**
     * Close the specified drawer.
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     * @param animate Whether closing of the drawer should be animated.
     *
     * 关闭指定的drawer。
     * @param gravity 重力。向左移动左边drawer或重力。GravityCompat。启动或GravityCompat。也可以使用END。
     * @param animate 是否应该动画关闭drawer。
     */
    public void closeDrawer(@EdgeGravity int gravity, boolean animate) {
        final View drawerView = findDrawerWithGravity(gravity);
        if (drawerView == null) {
            throw new IllegalArgumentException("No drawer view found with gravity "
                    + gravityToString(gravity));
        }
        closeDrawer(drawerView, animate);
    }

    /**
     * Check if the given drawer view is currently in an open state.
     * To be considered "open" the drawer must have settled into its fully
     * visible state. To check for partial visibility use
     * {@link #isDrawerVisible(android.view.View)}.
     *
     * @param drawer Drawer view to check
     * @return true if the given drawer view is in an open state
     * @see #isDrawerVisible(android.view.View)
     *
     *
     * 检查给定的drawer视图当前是否处于打开状态。
     * 要被认为是“打开”，drawer必须进入完全可见的状态。检查部分可见性使用
     * {@link #isDrawerVisible(android.view.View)}.
     *
     * @param drawer 要检查的drawer视图
     * @return 如果给定的drawer视图处于打开状态，则为
     * @see #isDrawerVisible(android.view.View)
     */
    public boolean isDrawerOpen(@NonNull View drawer) {
        if (!isDrawerView(drawer)) {
            throw new IllegalArgumentException("View " + drawer + " is not a drawer");
        }
        LayoutParams drawerLp = (LayoutParams) drawer.getLayoutParams();
        return (drawerLp.openState & LayoutParams.FLAG_IS_OPENED) == 1;
    }

    /**
     * Check if the given drawer view is currently in an open state.
     * To be considered "open" the drawer must have settled into its fully
     * visible state. If there is no drawer with the given gravity this method
     * will return false.
     *
     * @param drawerGravity Gravity of the drawer to check
     * @return true if the given drawer view is in an open state
     *
     *
     * 检查给定的drawer视图当前是否处于打开状态。
     * 要被认为是“打开”，drawer必须进入完全可见的状态。如果没有具有给定重力的drawer，该方法将返回false。
     *
     * @param drawerGravity 检查drawer的重力
     * @return 如果给定的drawer视图处于打开状态，则为
     */
    public boolean isDrawerOpen(@EdgeGravity int drawerGravity) {
        final View drawerView = findDrawerWithGravity(drawerGravity);
        if (drawerView != null) {
            return isDrawerOpen(drawerView);
        }
        return false;
    }

    /**
     * Check if a given drawer view is currently visible on-screen. The drawer
     * may be only peeking onto the screen, fully extended, or anywhere inbetween.
     *
     * @param drawer Drawer view to check
     * @return true if the given drawer is visible on-screen
     * @see #isDrawerOpen(android.view.View)
     *
     *
     * 检查给定的drawer视图当前是否在屏幕上可见。抽屉可能只是在窥视屏幕，完全展开，或介于两者之间的任何地方。
     * @param drawer 要检查的drawer视图
     * @return 如果给定的drawer在屏幕上可见，则为
     * @see #isDrawerOpen(android.view.View)
     */
    public boolean isDrawerVisible(@NonNull View drawer) {
        if (!isDrawerView(drawer)) {
            throw new IllegalArgumentException("View " + drawer + " is not a drawer");
        }
        return ((LayoutParams) drawer.getLayoutParams()).onScreen > 0;
    }

    /**
     * Check if a given drawer view is currently visible on-screen. The drawer
     * may be only peeking onto the screen, fully extended, or anywhere in between.
     * If there is no drawer with the given gravity this method will return false.
     *
     * @param drawerGravity Gravity of the drawer to check
     * @return true if the given drawer is visible on-screen
     *
     *
     * 检查给定的drawer视图当前是否在屏幕上可见。drawer可能只是在窥视屏幕，完全展开，或介于两者之间的任何地方。如果没有具有给定重力的抽屉，该方法将返回false。
     * @param drawerGravity 检查drawer的重力
     * @return 如果给定的drawer在屏幕上可见，则为
     */
    public boolean isDrawerVisible(@EdgeGravity int drawerGravity) {
        final View drawerView = findDrawerWithGravity(drawerGravity);
        if (drawerView != null) {
            return isDrawerVisible(drawerView);
        }
        return false;
    }

    private boolean hasPeekingDrawer() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            if (lp.isPeeking) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams
                ? new LayoutParams((LayoutParams) p)
                : p instanceof ViewGroup.MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (getDescendantFocusability() == FOCUS_BLOCK_DESCENDANTS) {
            return;
        }

        // Only the views in the open drawers are focusables. Add normal child views when
        // no drawers are opened.
        final int childCount = getChildCount();
        boolean isDrawerOpen = false;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (isDrawerView(child)) {
                if (isDrawerOpen(child)) {
                    isDrawerOpen = true;
                    child.addFocusables(views, direction, focusableMode);
                }
            } else {
                mNonDrawerViews.add(child);
            }
        }

        if (!isDrawerOpen) {
            final int nonDrawerViewsCount = mNonDrawerViews.size();
            for (int i = 0; i < nonDrawerViewsCount; ++i) {
                final View child = mNonDrawerViews.get(i);
                if (child.getVisibility() == View.VISIBLE) {
                    child.addFocusables(views, direction, focusableMode);
                }
            }
        }

        mNonDrawerViews.clear();
    }

    private boolean hasVisibleDrawer() {
        return findVisibleDrawer() != null;
    }

    View findVisibleDrawer() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (isDrawerView(child) && isDrawerVisible(child)) {
                return child;
            }
        }
        return null;
    }

    void cancelChildViewTouch() {
        // Cancel child touches
        if (!mChildrenCanceledTouch) {
            final long now = SystemClock.uptimeMillis();
            final MotionEvent cancelEvent = MotionEvent.obtain(now, now,
                    MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).dispatchTouchEvent(cancelEvent);
            }
            cancelEvent.recycle();
            mChildrenCanceledTouch = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && hasVisibleDrawer()) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final View visibleDrawer = findVisibleDrawer();
            if (visibleDrawer != null && getDrawerLockMode(visibleDrawer) == LOCK_MODE_UNLOCKED) {
                closeDrawers();
            }
            return visibleDrawer != null;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.openDrawerGravity != Gravity.NO_GRAVITY) {
            final View toOpen = findDrawerWithGravity(ss.openDrawerGravity);
            if (toOpen != null) {
                openDrawer(toOpen);
            }
        }

        if (ss.lockModeLeft != LOCK_MODE_UNDEFINED) {
            setDrawerLockMode(ss.lockModeLeft, Gravity.LEFT);
        }
        if (ss.lockModeRight != LOCK_MODE_UNDEFINED) {
            setDrawerLockMode(ss.lockModeRight, Gravity.RIGHT);
        }
        if (ss.lockModeStart != LOCK_MODE_UNDEFINED) {
            setDrawerLockMode(ss.lockModeStart, GravityCompat.START);
        }
        if (ss.lockModeEnd != LOCK_MODE_UNDEFINED) {
            setDrawerLockMode(ss.lockModeEnd, GravityCompat.END);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState ss = new SavedState(superState);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            // Is the current child fully opened (that is, not closing)?
            boolean isOpenedAndNotClosing = (lp.openState == LayoutParams.FLAG_IS_OPENED);
            // Is the current child opening?
            boolean isClosedAndOpening = (lp.openState == LayoutParams.FLAG_IS_OPENING);
            if (isOpenedAndNotClosing || isClosedAndOpening) {
                // If one of the conditions above holds, save the child's gravity
                // so that we open that child during state restore.
                ss.openDrawerGravity = lp.gravity;
                break;
            }
        }

        ss.lockModeLeft = mLockModeLeft;
        ss.lockModeRight = mLockModeRight;
        ss.lockModeStart = mLockModeStart;
        ss.lockModeEnd = mLockModeEnd;

        return ss;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        final View openDrawer = findOpenDrawer();
        if (openDrawer != null || isDrawerView(child)) {
            // A drawer is already open or the new view is a drawer, so the
            // new view should start out hidden.
            ViewCompat.setImportantForAccessibility(child,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        } else {
            // Otherwise this is a content view and no drawer is open, so the
            // new view should start out visible.
            ViewCompat.setImportantForAccessibility(child,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }

        // We only need a delegate here if the framework doesn't understand
        // NO_HIDE_DESCENDANTS importance.
        if (!CAN_HIDE_DESCENDANTS) {
            ViewCompat.setAccessibilityDelegate(child, mChildAccessibilityDelegate);
        }
    }

    static boolean includeChildForAccessibility(View child) {
        // If the child is not important for accessibility we make
        // sure this hides the entire subtree rooted at it as the
        // IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDATS is not
        // supported on older platforms but we want to hide the entire
        // content and not opened drawers if a drawer is opened.
        return ViewCompat.getImportantForAccessibility(child)
                != ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                && ViewCompat.getImportantForAccessibility(child)
                != ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO;
    }

    /**
     * State persisted across instances
     */
    protected static class SavedState extends AbsSavedState {
        int openDrawerGravity = Gravity.NO_GRAVITY;
        @LockMode int lockModeLeft;
        @LockMode int lockModeRight;
        @LockMode int lockModeStart;
        @LockMode int lockModeEnd;

        public SavedState(@NonNull Parcel in, @Nullable ClassLoader loader) {
            super(in, loader);
            openDrawerGravity = in.readInt();
            lockModeLeft = in.readInt();
            lockModeRight = in.readInt();
            lockModeStart = in.readInt();
            lockModeEnd = in.readInt();
        }

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(openDrawerGravity);
            dest.writeInt(lockModeLeft);
            dest.writeInt(lockModeRight);
            dest.writeInt(lockModeStart);
            dest.writeInt(lockModeEnd);
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
        private final int mAbsGravity;
        private ViewDragHelper mDragger;

        private final Runnable mPeekRunnable = new Runnable() {
            @Override public void run() {
                peekDrawer();
            }
        };

        ViewDragCallback(int gravity) {
            mAbsGravity = gravity;
        }

        public void setDragger(ViewDragHelper dragger) {
            mDragger = dragger;
        }

        public void removeCallbacks() {
            DrawerLayout.this.removeCallbacks(mPeekRunnable);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // Only capture views where the gravity matches what we're looking for.
            // This lets us use two ViewDragHelpers, one for each side drawer.
            return isDrawerView(child) && checkDrawerViewAbsoluteGravity(child, mAbsGravity)
                    && getDrawerLockMode(child) == LOCK_MODE_UNLOCKED;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            updateDrawerState(mAbsGravity, state, mDragger.getCapturedView());
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            float offset;
            final int childWidth = changedView.getWidth();

            // This reverses the positioning shown in onLayout.
            if (checkDrawerViewAbsoluteGravity(changedView, Gravity.LEFT)) {
                offset = (float) (childWidth + left) / childWidth;
            } else {
                final int width = getWidth();
                offset = (float) (width - left) / childWidth;
            }
            setDrawerViewOffset(changedView, offset);
            changedView.setVisibility(offset == 0 ? INVISIBLE : VISIBLE);
            invalidate();
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            final LayoutParams lp = (LayoutParams) capturedChild.getLayoutParams();
            lp.isPeeking = false;

            closeOtherDrawer();
        }

        private void closeOtherDrawer() {
            final int otherGrav = mAbsGravity == Gravity.LEFT ? Gravity.RIGHT : Gravity.LEFT;
            final View toClose = findDrawerWithGravity(otherGrav);
            if (toClose != null) {
                closeDrawer(toClose);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // Offset is how open the drawer is, therefore left/right values
            // are reversed from one another.
            final float offset = getDrawerViewOffset(releasedChild);
            final int childWidth = releasedChild.getWidth();

            int left;
            if (checkDrawerViewAbsoluteGravity(releasedChild, Gravity.LEFT)) {
                left = xvel > 0 || (xvel == 0 && offset > 0.5f) ? 0 : -childWidth;
            } else {
                final int width = getWidth();
                left = xvel < 0 || (xvel == 0 && offset > 0.5f) ? width - childWidth : width;
            }

            mDragger.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            postDelayed(mPeekRunnable, PEEK_DELAY);
        }

        void peekDrawer() {
            final View toCapture;
            final int childLeft;
            final int peekDistance = mDragger.getEdgeSize();
            final boolean leftEdge = mAbsGravity == Gravity.LEFT;
            if (leftEdge) {
                toCapture = findDrawerWithGravity(Gravity.LEFT);
                childLeft = (toCapture != null ? -toCapture.getWidth() : 0) + peekDistance;
            } else {
                toCapture = findDrawerWithGravity(Gravity.RIGHT);
                childLeft = getWidth() - peekDistance;
            }
            // Only peek if it would mean making the drawer more visible and the drawer isn't locked
            if (toCapture != null && ((leftEdge && toCapture.getLeft() < childLeft)
                    || (!leftEdge && toCapture.getLeft() > childLeft))
                    && getDrawerLockMode(toCapture) == LOCK_MODE_UNLOCKED) {
                final LayoutParams lp = (LayoutParams) toCapture.getLayoutParams();
                mDragger.smoothSlideViewTo(toCapture, childLeft, toCapture.getTop());
                lp.isPeeking = true;
                invalidate();

                closeOtherDrawer();

                cancelChildViewTouch();
            }
        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            if (ALLOW_EDGE_LOCK) {
                final View drawer = findDrawerWithGravity(mAbsGravity);
                if (drawer != null && !isDrawerOpen(drawer)) {
                    closeDrawer(drawer);
                }
                return true;
            }
            return false;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            final View toCapture;
            if ((edgeFlags & ViewDragHelper.EDGE_LEFT) == ViewDragHelper.EDGE_LEFT) {
                toCapture = findDrawerWithGravity(Gravity.LEFT);
            } else {
                toCapture = findDrawerWithGravity(Gravity.RIGHT);
            }

            if (toCapture != null && getDrawerLockMode(toCapture) == LOCK_MODE_UNLOCKED) {
                mDragger.captureChildView(toCapture, pointerId);
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return isDrawerView(child) ? child.getWidth() : 0;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (checkDrawerViewAbsoluteGravity(child, Gravity.LEFT)) {
                return Math.max(-child.getWidth(), Math.min(left, 0));
            } else {
                final int width = getWidth();
                return Math.max(width - child.getWidth(), Math.min(left, width));
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return child.getTop();
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int FLAG_IS_OPENED = 0x1;
        private static final int FLAG_IS_OPENING = 0x2;
        private static final int FLAG_IS_CLOSING = 0x4;

        public int gravity = Gravity.NO_GRAVITY;
        float onScreen;
        boolean isPeeking;
        int openState;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            this.gravity = a.getInt(0, Gravity.NO_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            this(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(@NonNull LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }

    class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final Rect mTmpRect = new Rect();

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            if (CAN_HIDE_DESCENDANTS) {
                super.onInitializeAccessibilityNodeInfo(host, info);
            } else {
                // Obtain a node for the host, then manually generate the list
                // of children to only include non-obscured views.
                final AccessibilityNodeInfoCompat superNode =
                        AccessibilityNodeInfoCompat.obtain(info);
                super.onInitializeAccessibilityNodeInfo(host, superNode);

                info.setSource(host);
                final ViewParent parent = ViewCompat.getParentForAccessibility(host);
                if (parent instanceof View) {
                    info.setParent((View) parent);
                }
                copyNodeInfoNoChildren(info, superNode);
                superNode.recycle();

                addChildrenForAccessibility(info, (ViewGroup) host);
            }

            info.setClassName(DrawerLayout.class.getName());

            // This view reports itself as focusable so that it can intercept
            // the back button, but we should prevent this view from reporting
            // itself as focusable to accessibility services.
            info.setFocusable(false);
            info.setFocused(false);
            info.removeAction(AccessibilityActionCompat.ACTION_FOCUS);
            info.removeAction(AccessibilityActionCompat.ACTION_CLEAR_FOCUS);
        }

        @Override
        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);

            event.setClassName(DrawerLayout.class.getName());
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            // Special case to handle window state change events. As far as
            // accessibility services are concerned, state changes from
            // DrawerLayout invalidate the entire contents of the screen (like
            // an Activity or Dialog) and they should announce the title of the
            // new content.
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                final List<CharSequence> eventText = event.getText();
                final View visibleDrawer = findVisibleDrawer();
                if (visibleDrawer != null) {
                    final int edgeGravity = getDrawerViewAbsoluteGravity(visibleDrawer);
                    final CharSequence title = getDrawerTitle(edgeGravity);
                    if (title != null) {
                        eventText.add(title);
                    }
                }

                return true;
            }

            return super.dispatchPopulateAccessibilityEvent(host, event);
        }

        @Override
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child,
                                                       AccessibilityEvent event) {
            if (CAN_HIDE_DESCENDANTS || includeChildForAccessibility(child)) {
                return super.onRequestSendAccessibilityEvent(host, child, event);
            }
            return false;
        }

        private void addChildrenForAccessibility(AccessibilityNodeInfoCompat info, ViewGroup v) {
            final int childCount = v.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = v.getChildAt(i);
                if (includeChildForAccessibility(child)) {
                    info.addChild(child);
                }
            }
        }

        /**
         * This should really be in AccessibilityNodeInfoCompat, but there unfortunately
         * seem to be a few elements that are not easily cloneable using the underlying API.
         * Leave it private here as it's not general-purpose useful.
         *
         * 这实际上应该在AccessibilityNodeInfoCompat中，
         * 但不幸的是，似乎有一些元素无法使用底层API轻松克隆。在这里保留它的私密性，因为它不是通用的有用的。
         */
        private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat dest,
                                            AccessibilityNodeInfoCompat src) {
            final Rect rect = mTmpRect;

            src.getBoundsInParent(rect);
            dest.setBoundsInParent(rect);

            src.getBoundsInScreen(rect);
            dest.setBoundsInScreen(rect);

            dest.setVisibleToUser(src.isVisibleToUser());
            dest.setPackageName(src.getPackageName());
            dest.setClassName(src.getClassName());
            dest.setContentDescription(src.getContentDescription());

            dest.setEnabled(src.isEnabled());
            dest.setClickable(src.isClickable());
            dest.setFocusable(src.isFocusable());
            dest.setFocused(src.isFocused());
            dest.setAccessibilityFocused(src.isAccessibilityFocused());
            dest.setSelected(src.isSelected());
            dest.setLongClickable(src.isLongClickable());

            dest.addAction(src.getActions());
        }
    }

    static final class ChildAccessibilityDelegate extends AccessibilityDelegateCompat {
        @Override
        public void onInitializeAccessibilityNodeInfo(View child,
                                                      AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(child, info);

            if (!includeChildForAccessibility(child)) {
                // If we are ignoring the sub-tree rooted at the child,
                // break the connection to the rest of the node tree.
                // For details refer to includeChildForAccessibility.
                info.setParent(null);
            }
        }
    }
}
