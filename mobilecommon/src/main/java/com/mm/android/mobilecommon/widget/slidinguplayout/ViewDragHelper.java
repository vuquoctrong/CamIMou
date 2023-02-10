package com.mm.android.mobilecommon.widget.slidinguplayout;

import android.content.Context;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.VelocityTrackerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ScrollerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import java.util.Arrays;

/**
 * ViewDragHelper is a utility class for writing custom ViewGroups. It offers a number
 * of useful operations and state tracking for allowing a user to drag and reposition
 * views within their parent ViewGroup.
 *
 * ViewDragHelper是一个用于编写自定义viewgroup的实用类。它提供了许多有用的操作和状态跟踪，允许用户在其父ViewGroup中拖动和重新定位视图。
 */
public class ViewDragHelper {
    private static final String TAG = "ViewDragHelper";

    /**
     * A null/invalid pointer ID.
     *
     * 空/无效的指针ID。
     */
    public static final int INVALID_POINTER = -1;

    /**
     * A view is not currently being dragged or animating as a result of a fling/snap.
     *
     * 一个视图目前没有被拖动或动画作为一个抛/snap的结果。
     */
    public static final int STATE_IDLE = 0;

    /**
     * A view is currently being dragged. The position is currently changing as a result
     * of user input or simulated user input.
     *
     * 视图当前正在被拖动。由于用户输入或模拟用户输入，当前位置正在发生变化。
     */
    public static final int STATE_DRAGGING = 1;

    /**
     * A view is currently settling into place as a result of a fling or
     * predefined non-interactive motion.
     *
     * 视图当前正由于抛动或预定义的非交互运动而固定在适当的位置。
     */
    public static final int STATE_SETTLING = 2;

    /**
     * Edge flag indicating that the left edge should be affected.
     *
     * 边缘标志，指示应该影响左边缘。
     */
    public static final int EDGE_LEFT = 1 << 0;

    /**
     * Edge flag indicating that the right edge should be affected.
     *
     * 边缘标志，指示应该影响右边缘。
     */
    public static final int EDGE_RIGHT = 1 << 1;

    /**
     * Edge flag indicating that the top edge should be affected.
     *
     * 指示应受影响的上边缘的边缘标志。
     */
    public static final int EDGE_TOP = 1 << 2;

    /**
     * Edge flag indicating that the bottom edge should be affected.
     *
     * 指示应受影响的下边缘的边缘标志。
     */
    public static final int EDGE_BOTTOM = 1 << 3;

    /**
     * Edge flag set indicating all edges should be affected.
     *
     * 边标志设置，指示应影响所有边。
     */
    public static final int EDGE_ALL = EDGE_LEFT | EDGE_TOP | EDGE_RIGHT | EDGE_BOTTOM;

    /**
     * Indicates that a check should occur along the horizontal axis
     *
     * 指示应沿水平轴进行检查
     */
    public static final int DIRECTION_HORIZONTAL = 1 << 0;

    /**
     * Indicates that a check should occur along the vertical axis
     *
     * 指示应沿垂直轴进行检查
     */
    public static final int DIRECTION_VERTICAL = 1 << 1;

    /**
     * Indicates that a check should occur along all axes
     *
     * 指示应沿所有轴进行检查
     */
    public static final int DIRECTION_ALL = DIRECTION_HORIZONTAL | DIRECTION_VERTICAL;

    private static final int EDGE_SIZE = 20; // dp

    private static final int BASE_SETTLE_DURATION = 256; // ms
    private static final int MAX_SETTLE_DURATION = 600; // ms

    // Current drag state; idle, dragging or settling
    private int mDragState;

    // Distance to travel before a drag may begin
    private int mTouchSlop;

    // Last known position/pointer tracking
    private int mActivePointerId = INVALID_POINTER;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private int[] mInitialEdgesTouched;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mPointersDown;

    private VelocityTracker mVelocityTracker;
    private float mMaxVelocity;
    private float mMinVelocity;

    private int mEdgeSize;
    private int mTrackingEdges;

    private ScrollerCompat mScroller;

    private final Callback mCallback;

    private View mCapturedView;
    private boolean mReleaseInProgress;

    private final ViewGroup mParentView;

    /**
     * A Callback is used as a communication channel with the ViewDragHelper back to the
     * parent view using it. <code>on*</code>methods are invoked on siginficant events and several
     * accessor methods are expected to provide the ViewDragHelper with more information
     * about the state of the parent view upon request. The callback also makes decisions
     * governing the range and draggability of child views.
     *
     * Callback被用作与ViewDragHelper的通信通道，使用它返回父视图。<code>on*</code>方法在重要事件时被调用，
     * 一些访问器方法被期望根据请求为ViewDragHelper提供关于父视图状态的更多信息。回调还决定控制子视图的范围和可拖动性。
     */
    public static abstract class Callback {
        /**
         * Called when the drag state changes. See the <code>STATE_*</code> constants for more information.
         * @param state The new drag state
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         *
         * 在拖动状态改变时调用。更多信息请参见<code>STATE *</code>常量。
         * @param state The new drag state
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         */
        public void onViewDragStateChanged(int state) {}

        /**
         * Called when the captured view's position changes as the result of a drag or settle.
         * @param changedView View whose position changed
         * @param left New X coordinate of the left edge of the view
         * @param top New Y coordinate of the top edge of the view
         * @param dx Change in X position from the last call
         * @param dy Change in Y position from the last call
         *
         * 当被捕获视图的位置因拖拽或调整而改变时调用。
         * @param changedView 位置发生变化的视图
         * @param left 视图左边缘的新X坐标
         * @param top 视图上边缘的新Y坐标
         * @param dx 上次呼叫后X位置的变化
         * @param dy 上次调用的Y坐标变化
         */
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {}

        /**
         * Called when a child view is captured for dragging or settling. The ID of the pointer
         * currently dragging the captured view is supplied. If activePointerId is
         * identified as {@link #INVALID_POINTER} the capture is programmatic instead of
         * pointer-initiated.
         * @param capturedChild Child view that was captured
         * @param activePointerId Pointer id tracking the child capture
         *
         * 在捕获子视图以进行拖拽或沉降时调用。提供当前拖动捕获视图的指针的ID。
         * 如果activePointerId标识为{@link #INVALID_POINTER}，则捕获是程序化的，而不是指针初始化的。
         * @param capturedChild 捕获的子视图
         * @param activePointerId 跟踪子捕获的指针id
         */
        public void onViewCaptured(View capturedChild, int activePointerId) {}

        /**
         * Called when the child view is no longer being actively dragged.
         * The fling velocity is also supplied, if relevant. The velocity values may
         * be clamped to system minimums or maximums.
         *
         * <p>Calling code may decide to fling or otherwise release the view to let it
         * settle into place. It should do so using {@link #settleCapturedViewAt(int, int)}
         * or {@link #flingCapturedView(int, int, int, int)}. If the Callback invokes
         * one of these methods, the ViewDragHelper will enter {@link #STATE_SETTLING}
         * and the view capture will not fully end until it comes to a complete stop.
         * If neither of these methods is invoked before <code>onViewReleased</code> returns,
         * the view will stop in place and the ViewDragHelper will return to
         * {@link #STATE_IDLE}.</p>
         * @param releasedChild The captured child view now being released
         * @param xvel X velocity of the pointer as it left the screen in pixels per second.
         * @param yvel Y velocity of the pointer as it left the screen in pixels per second.
         *
         *
         * 当子视图不再被主动拖动时调用。如果相关，还提供了抛出速度。速度值可以固定在系统的最小值或最大值上。
         *
         * 调用代码可以决定抛出视图或以其他方式释放视图，让它就位。它应该使用{@link #settleCapturedViewAt(int, int)}
         * 或{@link #flingCapturedView(int, int, int, int)}来实现。如果Callback调用这些方法中的一个，ViewDragHelper将进入{@link #STATE_SETTLING}，视图捕获直到完全停止才会完全结束。
         * 如果在<code>onViewReleased</code>返回之前这两个方法都没有被调用，视图将原地停止，ViewDragHelper将返回{@link #STATE_IDLE}
         *
         * @param releasedChild 他捕捉到的孩子的视角现在被释放了
         * @param xvel X指针离开屏幕时的速度，单位为像素/秒。
         * @param yvel 指针离开屏幕时的Y速度，单位为像素/秒。
         */
        public void onViewReleased(View releasedChild, float xvel, float yvel) {}

        /**
         * Called when one of the subscribed edges in the parent view has been touched
         * by the user while no child view is currently captured.
         * @param edgeFlags A combination of edge flags describing the edge(s) currently touched
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see #EDGE_LEFT
         * @see #EDGE_TOP
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         *
         * 当用户触摸了父视图中的订阅边之一，而当前没有捕获子视图时调用。
         * @param edgeFlags 描述当前触碰的边的边标志的组合
         * @param pointerId 触碰到描述边缘的指针的ID。
         * @see #EDGE_LEFT
         * @see #EDGE_TOP
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         */
        public void onEdgeTouched(int edgeFlags, int pointerId) {}

        /**
         * Called when the given edge may become locked. This can happen if an edge drag
         * was preliminarily rejected before beginning, but after {@link #onEdgeTouched(int, int)}
         * was called. This method should return true to lock this edge or false to leave it
         * unlocked. The default behavior is to leave edges unlocked.
         * @param edgeFlags A combination of edge flags describing the edge(s) locked
         * @return true to lock the edge, false to leave it unlocked
         *
         * 当给定的边可能被锁定时调用。如果边缘拖动在开始之前被初步拒绝，但在{@link #onEdgeTouched(int, int)}被调用之后，
         * 就会发生这种情况。这个方法应该返回true以锁定这条边，或者返回false以不锁定它。默认行为是保持边缘未锁定。
         * @param edgeFlags 描述锁定的边的边标志的组合
         * @return True表示锁定边缘，false表示不锁定边缘
         */
        public boolean onEdgeLock(int edgeFlags) {
            return false;
        }

        /**
         * Called when the user has started a deliberate drag away from one
         * of the subscribed edges in the parent view while no child view is currently captured.
         * @param edgeFlags A combination of edge flags describing the edge(s) dragged
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see #EDGE_LEFT
         * @see #EDGE_TOP
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         *
         *当用户开始有意地从父视图中的订阅边中拖出，而当前没有捕获子视图时调用。
         * @param edgeFlags 描述被拖动的边的边标志的组合
         * @param pointerId 触碰到描述边缘的指针的ID。
         * @see #EDGE_LEFT
         * @see #EDGE_TOP
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         */
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {}

        /**
         * Called to determine the Z-order of child views.
         * @param index the ordered position to query for
         * @return index of the view that should be ordered at position <code>index</code>
         *
         * 调用以确定子视图的z轴顺序。
         * @param index 要查询的有序位置
         * @return 应该在position <code> Index </code>处排序的视图的索引
         */
        public int getOrderedChildIndex(int index) {
            return index;
        }

        /**
         * Return the magnitude of a draggable child view's horizontal range of motion in pixels.
         * This method should return 0 for views that cannot move horizontally.
         * @param child Child view to check
         * @return range of horizontal motion in pixels
         *
         * 返回可拖动子视图的水平移动范围的大小(以像素为单位)。
         * 对于不能水平移动的视图，该方法应该返回0。
         * @param child child子视图要检查
         * @return 水平运动范围(以像素为单位)
         */
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        /**
         * Return the magnitude of a draggable child view's vertical range of motion in pixels.
         * This method should return 0 for views that cannot move vertically.
         * @param child Child view to check
         * @return range of vertical motion in pixels
         *
         * 返回可拖动子视图的垂直移动范围的大小(以像素为单位)。
         * 对于不能垂直移动的视图，该方法应该返回0。
         * @param child 子视图要检查
         * @return 以像素为单位的垂直运动范围
         */
        public int getViewVerticalDragRange(View child) {
            return 0;
        }

        /**
         * Called when the user's input indicates that they want to capture the given child view
         * with the pointer indicated by pointerId. The callback should return true if the user
         * is permitted to drag the given view with the indicated pointer.
         *
         * <p>ViewDragHelper may call this method multiple times for the same view even if
         * the view is already captured; this indicates that a new pointer is trying to take
         * control of the view.</p>
         *
         * <p>If this method returns true, a call to {@link #onViewCaptured(View, int)}
         * will follow if the capture is successful.</p>
         *
         * @param child Child the user is attempting to capture
         * @param pointerId ID of the pointer attempting the capture
         * @return true if capture should be allowed, false otherwise
         *
         *
         * 当用户的输入指示他们想用由pointerId指示的指针捕获给定的子视图时调用。如果允许用户用指定的指针拖动给定的视图，则回调函数应该返回true。
         *
         * <p>ViewDragHelper可以为同一个视图多次调用此方法，即使视图已经被捕获;这表示有一个新的指针试图控制视图。 </p>
         * <p>will follow if the capture is successful.</p>
         *
         * @param child 用户试图捕获的子对象
         * @param pointerId 试图捕获的指针的ID
         * @return 如果允许捕获则为True，否则为false
         */
        public abstract boolean tryCaptureView(View child, int pointerId);

        /**
         * Restrict the motion of the dragged child view along the horizontal axis.
         * The default implementation does not allow horizontal motion; the extending
         * class must override this method and provide the desired clamping.
         * @param child Child view being dragged
         * @param left Attempted motion along the X axis
         * @param dx Proposed change in position for left
         * @return The new clamped position for left
         *
         *
         * 限制沿水平轴拖动子视图的运动。默认实现不允许水平移动;扩展类必须覆盖此方法并提供所需的夹紧。
         * @param child 正在拖动子视图
         * @param left 沿着X轴尝试运动
         * @param dx 建议改变左边的位置
         * @return 新的夹紧位置为左
         */
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        /**
         * Restrict the motion of the dragged child view along the vertical axis.
         * The default implementation does not allow vertical motion; the extending
         * class must override this method and provide the desired clamping.
         * @param child Child view being dragged
         * @param top Attempted motion along the Y axis
         * @param dy Proposed change in position for top
         * @return The new clamped position for top
         *
         * 限制沿垂直轴拖动子视图的运动。
         * 默认实现不允许垂直运动;扩展类必须覆盖此方法并提供所需的夹紧。
         * @param child 正在拖动子视图
         * @param top 沿Y轴尝试运动
         * @param dy 建议改变顶部的位置
         * @return 顶部的新夹紧位置
         */
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }
    }

    /**
     * Interpolator defining the animation curve for mScroller
     *
     * 为mScroller定义动画曲线的插值器
     */
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private final Runnable mSetIdleRunnable = new Runnable() {
        public void run() {
            setDragState(STATE_IDLE);
        }
    };

    /**
     * Factory method to create a new ViewDragHelper.
     * @param forParent Parent view to monitor
     * @param cb Callback to provide information and receive events
     * @return a new ViewDragHelper instance
     *
     * Factory方法来创建一个新的ViewDragHelper。
     * @param forParent 要监视的父视图
     * @param cb 回调函数提供信息并接收事件
     * @return 一个新的ViewDragHelper实例
     */
    public static ViewDragHelper create(ViewGroup forParent, Callback cb) {
        return new ViewDragHelper(forParent.getContext(), forParent, null, cb);
    }

    /**
     * Factory method to create a new ViewDragHelper with the specified interpolator.
     * @param forParent Parent view to monitor
     * @param interpolator interpolator for scroller
     * @param cb Callback to provide information and receive events
     * @return a new ViewDragHelper instance
     *
     * Factory方法来使用指定的插值器创建一个新的ViewDragHelper。
     * @param forParent 要监视的父视图
     * @param interpolator 插入器的照片卷轴
     * @param cb 回调函数提供信息并接收事件
     * @return 一个新的ViewDragHelper实例
     */
    public static ViewDragHelper create(ViewGroup forParent, Interpolator interpolator, Callback cb) {
        return new ViewDragHelper(forParent.getContext(), forParent, interpolator, cb);
    }

    /**
     * Factory method to create a new ViewDragHelper.
     * @param forParent Parent view to monitor
     * @param sensitivity Multiplier for how sensitive the helper should be about detecting
     *                    the start of a drag. Larger values are more sensitive. 1.0f is normal.
     * @param cb Callback to provide information and receive events
     * @return a new ViewDragHelper instance
     *
     * Factory方法来创建一个新的ViewDragHelper。
     * @param forParent 要监视的父视图
     * @param sensitivity 用于helper对检测拖动开始的敏感性的倍增器。数值越大越敏感。1.0 f是正常的。
     * @param cb 回调函数提供信息并接收事件
     * @return 一个新的ViewDragHelper实例
     */
    public static ViewDragHelper create(ViewGroup forParent, float sensitivity, Callback cb) {
        final ViewDragHelper helper = create(forParent, cb);
        helper.mTouchSlop = (int) (helper.mTouchSlop * (1 / sensitivity));
        return helper;
    }

    /**
     * Factory method to create a new ViewDragHelper with the specified interpolator.
     * @param forParent Parent view to monitor
     * @param sensitivity Multiplier for how sensitive the helper should be about detecting
     *                    the start of a drag. Larger values are more sensitive. 1.0f is normal.
     * @param interpolator interpolator for scroller
     * @param cb Callback to provide information and receive events
     * @return a new ViewDragHelper instance
     *
     * Factory方法来使用指定的插值器创建一个新的ViewDragHelper。
     * @param forParent 要监视的父视图
     * @param sensitivity 用于helper对检测拖动开始的敏感性的倍增器。数值越大越敏感。1.0 f是正常的。
     * @param interpolator 插入器的照片卷轴
     * @param cb 回调函数提供信息并接收事件
     * @return 一个新的ViewDragHelper实例
     */
    public static ViewDragHelper create(ViewGroup forParent, float sensitivity, Interpolator interpolator, Callback cb) {
        final ViewDragHelper helper = create(forParent, interpolator, cb);
        helper.mTouchSlop = (int) (helper.mTouchSlop * (1 / sensitivity));
        return helper;
    }

    /**
     * Apps should use ViewDragHelper.create() to get a new instance.
     * This will allow VDH to use internal compatibility implementations for different
     * platform versions.
     * If the interpolator is null, the default interpolator will be used.
     * @param context Context to initialize config-dependent params from
     * @param forParent Parent view to monitor
     * @param interpolator interpolator for scroller
     *
     * 应用程序应该使用ViewDragHelper.create()来获取一个新实例。
     * 这将允许VDH对不同的平台版本使用内部兼容性实现。
     * 如果插值器为空，将使用默认的插值器。
     * @param context 上下文来初始化依赖于配置的参数
     * @param forParent 要监视的父视图
     * @param interpolator 插入器的照片卷轴
     */
    private ViewDragHelper(Context context, ViewGroup forParent, Interpolator interpolator, Callback cb) {
        if (forParent == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        }
        if (cb == null) {
            throw new IllegalArgumentException("Callback may not be null");
        }

        mParentView = forParent;
        mCallback = cb;

        final ViewConfiguration vc = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;
        mEdgeSize = (int) (EDGE_SIZE * density + 0.5f);

        mTouchSlop = vc.getScaledTouchSlop();
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
        mScroller = ScrollerCompat.create(context, interpolator != null ? interpolator : sInterpolator);
    }

    /**
     * Set the minimum velocity that will be detected as having a magnitude greater than zero
     * in pixels per second. Callback methods accepting a velocity will be clamped appropriately.
     * @param minVel Minimum velocity to detect
     *
     * 将检测到的最小速度设置为每秒像素大小大于零。接受速度的回调方法将被适当地夹紧。
     * @param minVel 可检测的最小速度
     */
    public void setMinVelocity(float minVel) {
        mMinVelocity = minVel;
    }

    /**
     * Return the currently configured minimum velocity. Any flings with a magnitude less
     * than this value in pixels per second. Callback methods accepting a velocity will receive
     * zero as a velocity value if the real detected velocity was below this threshold.
     * @return the minimum velocity that will be detected
     *
     * 返回当前配置的最小速度。任何大小小于此值(以像素/秒为单位)的投掷。
     * 如果实际检测到的速度低于此阈值，则接受速度的回调方法将接收零作为速度值。
     * @return 被检测到的最小速度
     */
    public float getMinVelocity() {
        return mMinVelocity;
    }

    /**
     * Retrieve the current drag state of this helper. This will return one of
     * {@link #STATE_IDLE}, {@link #STATE_DRAGGING} or {@link #STATE_SETTLING}.
     * @return The current drag state
     *
     * 检索此帮助器的当前拖动状态。这将返回{@link #STATE IDLE}、{@link #STATE拖动}或{@link #STATE settlement}中的一个。
     * @return 当前拖动状态
     */
    public int getViewDragState() {
        return mDragState;
    }

    /**
     * Enable edge tracking for the selected edges of the parent view.
     * The callback's {@link Callback#onEdgeTouched(int, int)} and
     * {@link Callback#onEdgeDragStarted(int, int)} methods will only be invoked
     * for edges for which edge tracking has been enabled.
     * @param edgeFlags Combination of edge flags describing the edges to watch
     * @see #EDGE_LEFT
     * @see #EDGE_TOP
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     *
     * 为父视图的选定边启用边缘跟踪。
     * 回调的{@link Callback #onEdgeTouched(int, int)}和{@link Callback #onEdgeDragStarted(int, int)}方法
     * 只会在边缘跟踪已经启用的边缘上被调用。
     * @param edgeFlags 描述要观察的边缘的边缘标志的组合
     * @see #EDGE_LEFT
     * @see #EDGE_TOP
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        mTrackingEdges = edgeFlags;
    }

    /**
     * Return the size of an edge. This is the range in pixels along the edges of this view
     * that will actively detect edge touches or drags if edge tracking is enabled.
     * @return The size of an edge in pixels
     * @see #setEdgeTrackingEnabled(int)
     *
     * 返回边缘的大小。这是沿视图边缘的像素范围，如果启用了边缘跟踪，它将主动检测边缘触摸或拖动。
     * @return 边的大小，以像素为单位
     * @see #setEdgeTrackingEnabled(int)
     */
    public int getEdgeSize() {
        return mEdgeSize;
    }

    /**
     * Capture a specific child view for dragging within the parent. The callback will be notified
     * but {@link Callback#tryCaptureView(View, int)} will not be asked permission to
     * capture this view.
     * @param childView Child view to capture
     * @param activePointerId ID of the pointer that is dragging the captured child view
     *
     * 捕获特定的子视图，以便在父视图中进行拖动。回调将被通知，但是{@link Callback #tryCaptureView(View, int)}将不会被请求获取该视图的权限。
     * @param childView 要捕获的子视图
     * @param activePointerId 拖动捕获的子视图的指针的ID
     */
    public void captureChildView(View childView, int activePointerId) {
        if (childView.getParent() != mParentView) {
            throw new IllegalArgumentException("captureChildView: parameter must be a descendant " +
                    "of the ViewDragHelper's tracked parent view (" + mParentView + ")");
        }

        mCapturedView = childView;
        mActivePointerId = activePointerId;
        mCallback.onViewCaptured(childView, activePointerId);
        setDragState(STATE_DRAGGING);
    }

    /**
     * @return The currently captured view, or null if no view has been captured.
     *
     * @return 当前捕获的视图，如果没有捕获视图则为空。
     */
    public View getCapturedView() {
        return mCapturedView;
    }

    /**
     * @return The ID of the pointer currently dragging the captured view,or {@link #INVALID_POINTER}.
     *
     * @return 当前拖动捕获视图的指针的ID，或者{@link #INVALID_POINTER}。
     */
    public int getActivePointerId() {
        return mActivePointerId;
    }

    /**
     * @return The minimum distance in pixels that the user must travel to initiate a drag
     *
     * @return 用户发起拖动所需的最小距离(以像素为单位)
     */
    public int getTouchSlop() {
        return mTouchSlop;
    }

    /**
     * The result of a call to this method is equivalent to {@link #processTouchEvent(MotionEvent)} receiving an ACTION_CANCEL event.
     *
     * 调用此方法的结果等价于{@link #processTouchEvent(MotionEvent)}接收一个ACTION CANCEL事件。
     */
    public void cancel() {
        mActivePointerId = INVALID_POINTER;
        clearMotionHistory();

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * {@link #cancel()}, but also abort all motion in progress and snap to the end of any animation.
     *
     * {@link #cancel()}，但也会中止正在进行的所有动作，并捕捉到任何动画的结尾。
     */
    public void abort() {
        cancel();
        if (mDragState == STATE_SETTLING) {
            final int oldX = mScroller.getCurrX();
            final int oldY = mScroller.getCurrY();
            mScroller.abortAnimation();
            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();
            mCallback.onViewPositionChanged(mCapturedView, newX, newY, newX - oldX, newY - oldY);
        }
        setDragState(STATE_IDLE);
    }

    /**
     * Animate the view <code>child</code> to the given (left, top) position.
     * If this method returns true, the caller should invoke {@link #continueSettling(boolean)}
     * on each subsequent frame to continue the motion until it returns false. If this method
     * returns false there is no further work to do to complete the movement.
     *
     * <p>This operation does not count as a capture event, though {@link #getCapturedView()}
     * will still report the sliding view while the slide is in progress.</p>
     *
     * @param child Child view to capture and animate
     * @param finalLeft Final left position of child
     * @param finalTop Final top position of child
     * @return true if animation should continue through {@link #continueSettling(boolean)} calls
     *
     * 动画视图<code>child</code>到给定的(左，上)位置。
     * 如果这个方法返回true，调用者应该在每个后续帧上调用{@link #continueSettling(boolean)}来继续运动，
     * 直到它返回false。如果此方法返回false，则不需要做进一步的工作来完成移动。
     *
     * <p>虽然{@link #getCapturedView()}仍然会在幻灯片进行时报告滑动视图，但该操作不被视为捕获事件</p>
     * @param child 子视图来捕获和动画
     * @param finalLeft 最后孩子的左侧位置
     * @param finalTop 孩子最后的顶端位置
     * @return 如果动画应该通过{@link #continueSettling(boolean)}调用继续，则为true
     */
    public boolean smoothSlideViewTo(View child, int finalLeft, int finalTop) {
        mCapturedView = child;
        mActivePointerId = INVALID_POINTER;

        return forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0);
    }

    /**
     * Settle the captured view at the given (left, top) position.
     * The appropriate velocity from prior motion will be taken into account.
     * If this method returns true, the caller should invoke {@link #continueSettling(boolean)}
     * on each subsequent frame to continue the motion until it returns false. If this method
     * returns false there is no further work to do to complete the movement.
     *
     * @param finalLeft Settled left edge position for the captured view
     * @param finalTop Settled top edge position for the captured view
     * @return true if animation should continue through {@link #continueSettling(boolean)} calls
     *
     *
     * 将捕获的视图固定在给定的位置(左、上)。
     * 将考虑到先前运动产生的适当速度。
     * 如果这个方法返回true，调用者应该在每个后续帧上调用{@link #continueSettling(boolean)}来继续运动，
     * 直到它返回false。如果此方法返回false，则不需要做进一步的工作来完成移动。
     *
     * @param finalLeft 为捕获的视图确定左边缘位置
     * @param finalTop 为捕获的视图设置上边缘位置，
     * @return 如果动画应该通过{@link #continueSettling(boolean)}调用继续，则为true
     */
    public boolean settleCapturedViewAt(int finalLeft, int finalTop) {
        if (!mReleaseInProgress) {
            throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to " +
                    "Callback#onViewReleased");
        }

        return forceSettleCapturedViewAt(finalLeft, finalTop,
                (int) VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                (int) VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId));
    }

    /**
     * Settle the captured view at the given (left, top) position.
     * @param finalLeft Target left position for the captured view
     * @param finalTop Target top position for the captured view
     * @param xvel Horizontal velocity
     * @param yvel Vertical velocity
     * @return true if animation should continue through {@link #continueSettling(boolean)} calls
     *
     * 将捕获的视图固定在给定的位置(左、上)。
     * @param finalLeft 捕获视图的目标左侧位置
     * @param finalTop 捕获视图的目标顶部位置
     * @param xvel 水平速度
     * @param yvel 垂直速度
     * @return 如果动画应该通过{@link #continueSettling(boolean)}调用继续，则为true
     */
    private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
        final int startLeft = mCapturedView.getLeft();
        final int startTop = mCapturedView.getTop();
        final int dx = finalLeft - startLeft;
        final int dy = finalTop - startTop;

        if (dx == 0 && dy == 0) {
            // Nothing to do. Send callbacks, be done.
            mScroller.abortAnimation();
            setDragState(STATE_IDLE);
            return false;
        }

        final int duration = computeSettleDuration(mCapturedView, dx, dy, xvel, yvel);
        mScroller.startScroll(startLeft, startTop, dx, dy, duration);

        setDragState(STATE_SETTLING);
        return true;
    }

    private int computeSettleDuration(View child, int dx, int dy, int xvel, int yvel) {
        xvel = clampMag(xvel, (int) mMinVelocity, (int) mMaxVelocity);
        yvel = clampMag(yvel, (int) mMinVelocity, (int) mMaxVelocity);
        final int absDx = Math.abs(dx);
        final int absDy = Math.abs(dy);
        final int absXVel = Math.abs(xvel);
        final int absYVel = Math.abs(yvel);
        final int addedVel = absXVel + absYVel;
        final int addedDistance = absDx + absDy;

        final float xweight = xvel != 0 ? (float) absXVel / addedVel :
                (float) absDx / addedDistance;
        final float yweight = yvel != 0 ? (float) absYVel / addedVel :
                (float) absDy / addedDistance;

        int xduration = computeAxisDuration(dx, xvel, mCallback.getViewHorizontalDragRange(child));
        int yduration = computeAxisDuration(dy, yvel, mCallback.getViewVerticalDragRange(child));

        return (int) (xduration * xweight + yduration * yweight);
    }

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        if (delta == 0) {
            return 0;
        }

        final int width = mParentView.getWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, (float) Math.abs(delta) / width);
        final float distance = halfWidth + halfWidth *
                distanceInfluenceForSnapDuration(distanceRatio);

        int duration;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            if(motionRange == 0){
                return 0;
            }
            final float range = (float) Math.abs(delta) / motionRange;
            duration = (int) ((range + 1) * BASE_SETTLE_DURATION);
        }
        return Math.min(duration, MAX_SETTLE_DURATION);
    }

    /**
     * Clamp the magnitude of value for absMin and absMax.
     * If the value is below the minimum, it will be clamped to zero.
     * If the value is above the maximum, it will be clamped to the maximum.
     * @param value Value to clamp
     * @param absMin Absolute value of the minimum significant value to return
     * @param absMax Absolute value of the maximum value to return
     * @return The clamped value with the same sign as <code>value</code>
     *
     * 钳取absMin和absMax值的大小。
     * 如果值低于最小值，它将被固定为零。
     * 如果值高于最大值，它将被夹紧到最大值。
     * @param value 价值夹
     * @param absMin 要返回的最小有效值的绝对值
     * @param absMax 返回最大值的绝对值
     * @return 与<code>value</code>符号相同的夹紧值
     */
    private int clampMag(int value, int absMin, int absMax) {
        final int absValue = Math.abs(value);
        if (absValue < absMin) return 0;
        if (absValue > absMax) return value > 0 ? absMax : -absMax;
        return value;
    }

    /**
     * Clamp the magnitude of value for absMin and absMax.
     * If the value is below the minimum, it will be clamped to zero.
     * If the value is above the maximum, it will be clamped to the maximum.
     * @param value Value to clamp
     * @param absMin Absolute value of the minimum significant value to return
     * @param absMax Absolute value of the maximum value to return
     * @return The clamped value with the same sign as <code>value</code>
     *
     * 钳取absMin和absMax值的大小。
     * 如果值低于最小值，它将被固定为零。
     * 如果值高于最大值，它将被夹紧到最大值。
     * @param value 价值夹
     * @param absMin 返回的最小有效值的绝对值
     * @param absMax 返回最大值的绝对值
     * @return 与<code>value</code>符号相同的夹紧值
     */
    private float clampMag(float value, float absMin, float absMax) {
        final float absValue = Math.abs(value);
        if (absValue < absMin) return 0;
        if (absValue > absMax) return value > 0 ? absMax : -absMax;
        return value;
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    /**
     * Settle the captured view based on standard free-moving fling behavior.
     * The caller should invoke {@link #continueSettling(boolean)} on each subsequent frame
     * to continue the motion until it returns false.
     * @param minLeft Minimum X position for the view's left edge
     * @param minTop Minimum Y position for the view's top edge
     * @param maxLeft Maximum X position for the view's left edge
     * @param maxTop Maximum Y position for the view's top edge
     *
     * 基于标准的自由移动抛动行为来确定捕获的视图。
     * 调用者应该在每个后续帧上调用{@link #continueSettling(boolean)}来继续运动，直到它返回false。
     * @param minLeft 视图左边缘的最小X位置
     * @param minTop 视图上边缘的最小Y位置
     * @param maxLeft 视图左边缘的最大X位置
     * @param maxTop 视图上边缘的最大Y位置
     */
    public void flingCapturedView(int minLeft, int minTop, int maxLeft, int maxTop) {
        if (!mReleaseInProgress) {
            throw new IllegalStateException("Cannot flingCapturedView outside of a call to " +
                    "Callback#onViewReleased");
        }

        mScroller.fling(mCapturedView.getLeft(), mCapturedView.getTop(),
                (int) VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                (int) VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId),
                minLeft, maxLeft, minTop, maxTop);

        setDragState(STATE_SETTLING);
    }

    /**
     * Move the captured settling view by the appropriate amount for the current time.
     * If <code>continueSettling</code> returns true, the caller should call it again
     * on the next frame to continue.
     * @param deferCallbacks true if state callbacks should be deferred via posted message.
     *                       Set this to true if you are calling this method from
     *                       {@link View#computeScroll()} or similar methods
     *                       invoked as part of layout or drawing.
     * @return true if settle is still in progress
     *
     * 根据当前时间移动捕获的结算视图的适当数量。
     * 如果<code>continueSettling</code>返回true，调用者应该在下一帧再次调用它以继续。
     * @param deferCallbacks 如果状态回调应该通过发布的消息延迟，则为True。
     *                       如果您从{@link View#computeScroll()}调用此方法或作为布局或绘图的一部分调用的类似方法，则将此设置为true。
     * @return 如果settle仍在进行中，则为
     */
    public boolean continueSettling(boolean deferCallbacks) {
        // Make sure, there is a captured view
        if (mCapturedView == null) {
            return false;
        }
        if (mDragState == STATE_SETTLING) {
            boolean keepGoing = mScroller.computeScrollOffset();
            final int x = mScroller.getCurrX();
            final int y = mScroller.getCurrY();
            final int dx = x - mCapturedView.getLeft();
            final int dy = y - mCapturedView.getTop();

            if(!keepGoing && dy != 0) { //fix #525
                //Invalid drag state
                mCapturedView.setTop(0);
                return true;
            }

            if (dx != 0) {
                mCapturedView.offsetLeftAndRight(dx);
            }
            if (dy != 0) {
                mCapturedView.offsetTopAndBottom(dy);
            }

            if (dx != 0 || dy != 0) {
                mCallback.onViewPositionChanged(mCapturedView, x, y, dx, dy);
            }

            if (keepGoing && x == mScroller.getFinalX() && y == mScroller.getFinalY()) {
                // Close enough. The interpolator/scroller might think we're still moving
                // but the user sure doesn't.
                mScroller.abortAnimation();
                keepGoing = mScroller.isFinished();
            }

            if (!keepGoing) {
                if (deferCallbacks) {
                    mParentView.post(mSetIdleRunnable);
                } else {
                    setDragState(STATE_IDLE);
                }
            }
        }

        return mDragState == STATE_SETTLING;
    }

    /**
     * Like all callback events this must happen on the UI thread, but release
     * involves some extra semantics. During a release (mReleaseInProgress)
     * is the only time it is valid to call {@link #settleCapturedViewAt(int, int)}
     * or {@link #flingCapturedView(int, int, int, int)}.
     *
     * 和所有回调事件一样，这必须发生在UI线程上，但是release涉及到一些额外的语义。
     * 在发布期间(mReleaseInProgress)是唯一调用{@link #settleCapturedViewAt(int, int)}或{@link #flingCapturedView(int, int, int, int)}有效的时间。
     */
    private void dispatchViewReleased(float xvel, float yvel) {
        mReleaseInProgress = true;
        mCallback.onViewReleased(mCapturedView, xvel, yvel);
        mReleaseInProgress = false;

        if (mDragState == STATE_DRAGGING) {
            // onViewReleased didn't call a method that would have changed this. Go idle.
            setDragState(STATE_IDLE);
        }
    }

    private void clearMotionHistory() {
        if (mInitialMotionX == null) {
            return;
        }
        Arrays.fill(mInitialMotionX, 0);
        Arrays.fill(mInitialMotionY, 0);
        Arrays.fill(mLastMotionX, 0);
        Arrays.fill(mLastMotionY, 0);
        Arrays.fill(mInitialEdgesTouched, 0);
        Arrays.fill(mEdgeDragsInProgress, 0);
        Arrays.fill(mEdgeDragsLocked, 0);
        mPointersDown = 0;
    }

    private void clearMotionHistory(int pointerId) {
        if (mInitialMotionX == null || mInitialMotionX.length <= pointerId) {
            return;
        }
        mInitialMotionX[pointerId] = 0;
        mInitialMotionY[pointerId] = 0;
        mLastMotionX[pointerId] = 0;
        mLastMotionY[pointerId] = 0;
        mInitialEdgesTouched[pointerId] = 0;
        mEdgeDragsInProgress[pointerId] = 0;
        mEdgeDragsLocked[pointerId] = 0;
        mPointersDown &= ~(1 << pointerId);
    }

    private void ensureMotionHistorySizeForId(int pointerId) {
        if (mInitialMotionX == null || mInitialMotionX.length <= pointerId) {
            float[] imx = new float[pointerId + 1];
            float[] imy = new float[pointerId + 1];
            float[] lmx = new float[pointerId + 1];
            float[] lmy = new float[pointerId + 1];
            int[] iit = new int[pointerId + 1];
            int[] edip = new int[pointerId + 1];
            int[] edl = new int[pointerId + 1];

            if (mInitialMotionX != null) {
                System.arraycopy(mInitialMotionX, 0, imx, 0, mInitialMotionX.length);
                System.arraycopy(mInitialMotionY, 0, imy, 0, mInitialMotionY.length);
                System.arraycopy(mLastMotionX, 0, lmx, 0, mLastMotionX.length);
                System.arraycopy(mLastMotionY, 0, lmy, 0, mLastMotionY.length);
                System.arraycopy(mInitialEdgesTouched, 0, iit, 0, mInitialEdgesTouched.length);
                System.arraycopy(mEdgeDragsInProgress, 0, edip, 0, mEdgeDragsInProgress.length);
                System.arraycopy(mEdgeDragsLocked, 0, edl, 0, mEdgeDragsLocked.length);
            }

            mInitialMotionX = imx;
            mInitialMotionY = imy;
            mLastMotionX = lmx;
            mLastMotionY = lmy;
            mInitialEdgesTouched = iit;
            mEdgeDragsInProgress = edip;
            mEdgeDragsLocked = edl;
        }
    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        ensureMotionHistorySizeForId(pointerId);
        mInitialMotionX[pointerId] = mLastMotionX[pointerId] = x;
        mInitialMotionY[pointerId] = mLastMotionY[pointerId] = y;
        mInitialEdgesTouched[pointerId] = getEdgesTouched((int) x, (int) y);
        mPointersDown |= 1 << pointerId;
    }

    private void saveLastMotion(MotionEvent ev) {
        final int pointerCount = MotionEventCompat.getPointerCount(ev);
        for (int i = 0; i < pointerCount; i++) {
            final int pointerId = MotionEventCompat.getPointerId(ev, i);
            final float x = MotionEventCompat.getX(ev, i);
            final float y = MotionEventCompat.getY(ev, i);
            // Sometimes we can try and save last motion for a pointer never recorded in initial motion. In this case we just discard it.
            if (mLastMotionX != null && mLastMotionY != null
                    && mLastMotionX.length > pointerId && mLastMotionY.length > pointerId) {
                mLastMotionX[pointerId] = x;
                mLastMotionY[pointerId] = y;
            }
        }
    }

    /**
     * Check if the given pointer ID represents a pointer that is currently down (to the best
     * of the ViewDragHelper's knowledge).
     *
     * <p>The state used to report this information is populated by the methods
     * {@link #shouldInterceptTouchEvent(MotionEvent)} or
     * {@link #processTouchEvent(MotionEvent)}. If one of these methods has not
     * been called for all relevant MotionEvents to track, the information reported
     * by this method may be stale or incorrect.</p>
     *
     * @param pointerId pointer ID to check; corresponds to IDs provided by MotionEvent
     * @return true if the pointer with the given ID is still down
     *
     *
     * 检查给定的指针ID是否代表当前down的指针(根据ViewDragHelper的知识)。
     *
     * <p>用于报告此信息的状态由方法{@link #shouldInterceptTouchEvent(MotionEvent)}
     * 或{@link #processTouchEvent(MotionEvent)}填充。如果没有调用其中一个方法来跟踪所有相关的motionevent，
     * 则该方法报告的信息可能是过时的或不正确的。</p>
     *
     * @param pointerId 要检查的指针ID;对应于MotionEvent提供的id
     * @return 如果具有给定ID的指针仍然为down，则为true
     */
    public boolean isPointerDown(int pointerId) {
        return (mPointersDown & 1 << pointerId) != 0;
    }

    void setDragState(int state) {
        if (mDragState != state) {
            mDragState = state;
            mCallback.onViewDragStateChanged(state);
            if (mDragState == STATE_IDLE) {
                mCapturedView = null;
            }
        }
    }

    /**
     * Attempt to capture the view with the given pointer ID. The callback will be involved.
     * This will put us into the "dragging" state. If we've already captured this view with
     * this pointer this method will immediately return true without consulting the callback.
     * @param toCapture View to capture
     * @param pointerId Pointer to capture with
     * @return true if capture was successful
     *
     * 尝试用给定的指针ID捕获视图。回调会涉及到。
     * 这将使我们进入“拖拽”状态。如果我们已经用这个指针捕获了这个视图，这个方法将立即返回true，而不咨询回调函数。
     * @param toCapture 为了捕获
     * @param pointerId 要捕获的指针
     * @return 如果捕获成功，则为true
     */
    boolean tryCaptureViewForDrag(View toCapture, int pointerId) {
        if (toCapture == mCapturedView && mActivePointerId == pointerId) {
            // Already done!
            return true;
        }
        if (toCapture != null && mCallback.tryCaptureView(toCapture, pointerId)) {
            mActivePointerId = pointerId;
            captureChildView(toCapture, pointerId);
            return true;
        }
        return false;
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     * @param v View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx Delta scrolled in pixels along the X axis
     * @param dy Delta scrolled in pixels along the Y axis
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     *
     * 在给定dx的情况下，测试v的子视图的可滚动性。
     * @param v 视图以测试水平可滚动性
     * @param checkV 是否应该检查传递的视图v本身的可滚动性(true)，或者只是它的子结点(假)。
     * @param dx 沿X轴以像素为单位滚动
     * @param dy 沿Y轴以像素为单位滚动
     * @param x 主动触点的X坐标
     * @param y 主动触点的Y坐标
     * @return 如果v的子视图可以被(dx)滚动，则为真。
     */
    protected boolean canScroll(View v, boolean checkV, int dx, int dy, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, dy, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && (ViewCompat.canScrollHorizontally(v, -dx) ||
                ViewCompat.canScrollVertically(v, -dy));
    }

    /**
     * Check if this event as provided to the parent view's onInterceptTouchEvent should
     * cause the parent to intercept the touch event stream.
     * @param ev MotionEvent provided to onInterceptTouchEvent
     * @return true if the parent view should return true from onInterceptTouchEvent
     *
     * 检查提供给父视图的onInterceptTouchEvent事件是否会导致父视图拦截触摸事件流。
     * @param ev 提供给onInterceptTouchEvent的MotionEvent
     * @return 如果父视图从onInterceptTouchEvent返回true则为true
     */
    public boolean shouldInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int actionIndex = MotionEventCompat.getActionIndex(ev);

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                final int pointerId = MotionEventCompat.getPointerId(ev, 0);
                saveInitialMotion(x, y, pointerId);

                final View toCapture = findTopChildUnder((int) x, (int) y);

                // Catch a settling view if possible.
                if (toCapture == mCapturedView && mDragState == STATE_SETTLING) {
                    tryCaptureViewForDrag(toCapture, pointerId);
                }

                final int edgesTouched = mInitialEdgesTouched[pointerId];
                if ((edgesTouched & mTrackingEdges) != 0) {
                    mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                final float x = MotionEventCompat.getX(ev, actionIndex);
                final float y = MotionEventCompat.getY(ev, actionIndex);

                saveInitialMotion(x, y, pointerId);

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    final int edgesTouched = mInitialEdgesTouched[pointerId];
                    if ((edgesTouched & mTrackingEdges) != 0) {
                        mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                    }
                } else if (mDragState == STATE_SETTLING) {
                    // Catch a settling view if possible.
                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    if (toCapture == mCapturedView) {
                        tryCaptureViewForDrag(toCapture, pointerId);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // First to cross a touch slop over a draggable view wins. Also report edge drags.
                final int pointerCount = MotionEventCompat.getPointerCount(ev);
                for (int i = 0; i < pointerCount && mInitialMotionX != null && mInitialMotionY != null; i++) {
                    final int pointerId = MotionEventCompat.getPointerId(ev, i);
                    if (pointerId >= mInitialMotionX.length || pointerId >= mInitialMotionY.length) {
                        continue;
                    }
                    final float x = MotionEventCompat.getX(ev, i);
                    final float y = MotionEventCompat.getY(ev, i);
                    final float dx = x - mInitialMotionX[pointerId];
                    final float dy = y - mInitialMotionY[pointerId];

                    reportNewEdgeDrags(dx, dy, pointerId);
                    if (mDragState == STATE_DRAGGING) {
                        // Callback might have started an edge drag
                        break;
                    }

                    final View toCapture = findTopChildUnder((int)mInitialMotionX[pointerId], (int)mInitialMotionY[pointerId]);
                    if (toCapture != null && checkTouchSlop(toCapture, dx, dy) &&
                            tryCaptureViewForDrag(toCapture, pointerId)) {
                        break;
                    }
                }
                saveLastMotion(ev);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                clearMotionHistory(pointerId);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                cancel();
                break;
            }
        }

        return mDragState == STATE_DRAGGING;
    }

    /**
     * Process a touch event received by the parent view. This method will dispatch callback events
     * as needed before returning. The parent view's onTouchEvent implementation should call this.
     * @param ev The touch event received by the parent view
     *
     * 处理父视图接收到的触摸事件。此方法将在返回之前根据需要分派回调事件。父视图的onTouchEvent实现应该调用这个。
     * @param ev 父视图接收到的触摸事件
     */
    public void processTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int actionIndex = MotionEventCompat.getActionIndex(ev);

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                final int pointerId = MotionEventCompat.getPointerId(ev, 0);
                final View toCapture = findTopChildUnder((int) x, (int) y);

                saveInitialMotion(x, y, pointerId);

                // Since the parent is already directly processing this touch event,
                // there is no reason to delay for a slop before dragging.
                // Start immediately if possible.
                tryCaptureViewForDrag(toCapture, pointerId);

                final int edgesTouched = mInitialEdgesTouched[pointerId];
                if ((edgesTouched & mTrackingEdges) != 0) {
                    mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                final float x = MotionEventCompat.getX(ev, actionIndex);
                final float y = MotionEventCompat.getY(ev, actionIndex);

                saveInitialMotion(x, y, pointerId);

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    // If we're idle we can do anything! Treat it like a normal down event.

                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    tryCaptureViewForDrag(toCapture, pointerId);

                    final int edgesTouched = mInitialEdgesTouched[pointerId];
                    if ((edgesTouched & mTrackingEdges) != 0) {
                        mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                    }
                } else if (isCapturedViewUnder((int) x, (int) y)) {
                    // We're still tracking a captured view. If the same view is under this
                    // point, we'll swap to controlling it with this pointer instead.
                    // (This will still work if we're "catching" a settling view.)

                    tryCaptureViewForDrag(mCapturedView, pointerId);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mDragState == STATE_DRAGGING) {
                    final int index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, index);
                    final float y = MotionEventCompat.getY(ev, index);
                    final int idx = (int) (x - mLastMotionX[mActivePointerId]);
                    final int idy = (int) (y - mLastMotionY[mActivePointerId]);

                    dragTo(mCapturedView.getLeft() + idx, mCapturedView.getTop() + idy, idx, idy);

                    saveLastMotion(ev);
                } else {
                    // Check to see if any pointer is now over a draggable view.
                    final int pointerCount = MotionEventCompat.getPointerCount(ev);
                    for (int i = 0; i < pointerCount; i++) {
                        final int pointerId = MotionEventCompat.getPointerId(ev, i)
                                ;
                        final float x = MotionEventCompat.getX(ev, i);
                        final float y = MotionEventCompat.getY(ev, i);
                        final float dx = x - mInitialMotionX[pointerId];
                        final float dy = y - mInitialMotionY[pointerId];

                        reportNewEdgeDrags(dx, dy, pointerId);
                        if (mDragState == STATE_DRAGGING) {
                            // Callback might have started an edge drag.
                            break;
                        }

                        final View toCapture = findTopChildUnder((int) mInitialMotionX[pointerId], (int) mInitialMotionY[pointerId]);
                        if (checkTouchSlop(toCapture, dx, dy) &&
                                tryCaptureViewForDrag(toCapture, pointerId)) {
                            break;
                        }
                    }
                    saveLastMotion(ev);
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                if (mDragState == STATE_DRAGGING && pointerId == mActivePointerId) {
                    // Try to find another pointer that's still holding on to the captured view.
                    int newActivePointer = INVALID_POINTER;
                    final int pointerCount = MotionEventCompat.getPointerCount(ev);
                    for (int i = 0; i < pointerCount; i++) {
                        final int id = MotionEventCompat.getPointerId(ev, i);
                        if (id == mActivePointerId) {
                            // This one's going away, skip.
                            continue;
                        }

                        final float x = MotionEventCompat.getX(ev, i);
                        final float y = MotionEventCompat.getY(ev, i);
                        if (findTopChildUnder((int) x, (int) y) == mCapturedView &&
                                tryCaptureViewForDrag(mCapturedView, id)) {
                            newActivePointer = mActivePointerId;
                            break;
                        }
                    }

                    if (newActivePointer == INVALID_POINTER) {
                        // We didn't find another pointer still touching the view, release it.
                        releaseViewForPointerUp();
                    }
                }
                clearMotionHistory(pointerId);
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mDragState == STATE_DRAGGING) {
                    releaseViewForPointerUp();
                }
                cancel();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (mDragState == STATE_DRAGGING) {
                    dispatchViewReleased(0, 0);
                }
                cancel();
                break;
            }
        }
    }

    private void reportNewEdgeDrags(float dx, float dy, int pointerId) {
        int dragsStarted = 0;
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_LEFT)) {
            dragsStarted |= EDGE_LEFT;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_TOP)) {
            dragsStarted |= EDGE_TOP;
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_RIGHT)) {
            dragsStarted |= EDGE_RIGHT;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_BOTTOM)) {
            dragsStarted |= EDGE_BOTTOM;
        }

        if (dragsStarted != 0) {
            mEdgeDragsInProgress[pointerId] |= dragsStarted;
            mCallback.onEdgeDragStarted(dragsStarted, pointerId);
        }
    }

    private boolean checkNewEdgeDrag(float delta, float odelta, int pointerId, int edge) {
        final float absDelta = Math.abs(delta);
        final float absODelta = Math.abs(odelta);

        if ((mInitialEdgesTouched[pointerId] & edge) != edge  || (mTrackingEdges & edge) == 0 ||
                (mEdgeDragsLocked[pointerId] & edge) == edge ||
                (mEdgeDragsInProgress[pointerId] & edge) == edge ||
                (absDelta <= mTouchSlop && absODelta <= mTouchSlop)) {
            return false;
        }
        if (absDelta < absODelta * 0.5f && mCallback.onEdgeLock(edge)) {
            mEdgeDragsLocked[pointerId] |= edge;
            return false;
        }
        return (mEdgeDragsInProgress[pointerId] & edge) == 0 && absDelta > mTouchSlop;
    }

    /**
     * Check if we've crossed a reasonable touch slop for the given child view.
     * If the child cannot be dragged along the horizontal or vertical axis, motion
     * along that axis will not count toward the slop check.
     * @param child Child to check
     * @param dx Motion since initial position along X axis
     * @param dy Motion since initial position along Y axis
     * @return true if the touch slop has been crossed
     *
     * 检查我们是否为给定的子视图跨越了合理的触摸斜坡。
     * 如果不能沿着水平或垂直轴拖动孩子，沿着该轴的运动将不计入坡度检查。
     * @param child 孩子检查
     * @param dx 自初始位置起沿X轴的运动
     * @param dy 自初始位置起沿Y轴的运动
     * @return 如果触摸斜面已经交叉，则为True
     */
    private boolean checkTouchSlop(View child, float dx, float dy) {
        if (child == null) {
            return false;
        }
        final boolean checkHorizontal = mCallback.getViewHorizontalDragRange(child) > 0;
        final boolean checkVertical = mCallback.getViewVerticalDragRange(child) > 0;

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop;
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop;
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop;
        }
        return false;
    }

    /**
     * Check if any pointer tracked in the current gesture has crossed
     * the required slop threshold.
     *
     * <p>This depends on internal state populated by
     * {@link #shouldInterceptTouchEvent(MotionEvent)} or
     * {@link #processTouchEvent(MotionEvent)}. You should only rely on
     * the results of this method after all currently available touch data
     * has been provided to one of these two methods.</p>
     *
     * @param directions Combination of direction flags, see {@link #DIRECTION_HORIZONTAL},
     *                   {@link #DIRECTION_VERTICAL}, {@link #DIRECTION_ALL}
     * @return true if the slop threshold has been crossed, false otherwise
     *
     *
     * 检查当前手势中跟踪的指针是否超过了所需的slop阈值。
     *
     * <p>这取决于由{@link #shouldInterceptTouchEvent(MotionEvent)}或{@link #processTouchEvent(MotionEvent)}填充的内部状态。
     * 在所有当前可用的触摸数据都提供给这两种方法之一后，你应该只依赖于这个方法的结果。</p>
     *
     * @param directions 方向标志的组合，请参见{@link #DIRECTION_HORIZONTAL}，{@link #DIRECTION_VERTICAL}，{@link #DIRECTION_ALL}
     * @return 如果超过了斜率阈值，则为True，否则为false
     */
    public boolean checkTouchSlop(int directions) {
        final int count = mInitialMotionX.length;
        for (int i = 0; i < count; i++) {
            if (checkTouchSlop(directions, i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the specified pointer tracked in the current gesture has crossed
     * the required slop threshold.
     *
     * <p>This depends on internal state populated by
     * {@link #shouldInterceptTouchEvent(MotionEvent)} or
     * {@link #processTouchEvent(MotionEvent)}. You should only rely on
     * the results of this method after all currently available touch data
     * has been provided to one of these two methods.</p>
     *
     * @param directions Combination of direction flags, see {@link #DIRECTION_HORIZONTAL},
     *                   {@link #DIRECTION_VERTICAL}, {@link #DIRECTION_ALL}
     * @param pointerId ID of the pointer to slop check as specified by MotionEvent
     * @return true if the slop threshold has been crossed, false otherwise
     *
     *
     * 检查当前手势中跟踪的指定指针是否超过了所需的坡度阈值。
     *
     * <p>这取决于由{@link #shouldInterceptTouchEvent(MotionEvent)}或{@link #processTouchEvent(MotionEvent)}填充的内部状态。
     * 在所有当前可用的触摸数据都提供给这两种方法之一后，你应该只依赖于这个方法的结果。</p>
     *
     * @param directions 方向标志的组合，请参见{@link #DIRECTION_HORIZONTAL}，{@link #DIRECTION_VERTICAL}，{@link #DIRECTION_ALL}
     * @param pointerId 由MotionEvent指定的斜坡检查指针的ID
     * @return 如果超过了斜率阈值，则为True，否则为false
     */
    public boolean checkTouchSlop(int directions, int pointerId) {
        if (!isPointerDown(pointerId)) {
            return false;
        }

        final boolean checkHorizontal = (directions & DIRECTION_HORIZONTAL) == DIRECTION_HORIZONTAL;
        final boolean checkVertical = (directions & DIRECTION_VERTICAL) == DIRECTION_VERTICAL;

        final float dx = mLastMotionX[pointerId] - mInitialMotionX[pointerId];
        final float dy = mLastMotionY[pointerId] - mInitialMotionY[pointerId];

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop;
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop;
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop;
        }
        return false;
    }

    /**
     * Check if any of the edges specified were initially touched in the currently active gesture.
     * If there is no currently active gesture this method will return false.
     * @param edges Edges to check for an initial edge touch. See {@link #EDGE_LEFT},
     *              {@link #EDGE_TOP}, {@link #EDGE_RIGHT}, {@link #EDGE_BOTTOM} and
     *              {@link #EDGE_ALL}
     * @return true if any of the edges specified were initially touched in the current gesture
     *
     * 检查在当前活动的手势中，指定的任何边最初是否被触摸过。
     * 如果当前没有活动的手势，此方法将返回false。
     * @param edges 检查初始边缘触摸的边缘。参见{@link #EDGE_LEFT}， {@link #EDGE_TOP}， {@link #EDGE_RIGHT}， {@link #EDGE_BOTTOM}和{@link #EDGE_ALL}
     * @return 如果指定的任何边最初在当前手势中被触摸，则为True
     */
    public boolean isEdgeTouched(int edges) {
        final int count = mInitialEdgesTouched.length;
        for (int i = 0; i < count; i++) {
            if (isEdgeTouched(edges, i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if any of the edges specified were initially touched by the pointer with
     * the specified ID. If there is no currently active gesture or if there is no pointer with
     * the given ID currently down this method will return false.
     * @param edges Edges to check for an initial edge touch. See {@link #EDGE_LEFT},
     *              {@link #EDGE_TOP}, {@link #EDGE_RIGHT}, {@link #EDGE_BOTTOM} and
     *              {@link #EDGE_ALL}
     * @return true if any of the edges specified were initially touched in the current gesture
     *
     * 检查带有指定ID的指针最初是否接触过指定的任何边。如果当前没有活动的手势，或者当前没有指定ID的指针，此方法将返回false。
     * @param edges 检查初始边缘触摸的边缘。参见{@link #EDGE_LEFT}， {@link #EDGE_TOP}， {@link #EDGE_RIGHT}， {@link #EDGE_BOTTOM}和{@link #EDGE_ALL}
     * @return 如果指定的任何边最初在当前手势中被触摸，则为True
     */
    public boolean isEdgeTouched(int edges, int pointerId) {
        return isPointerDown(pointerId) && (mInitialEdgesTouched[pointerId] & edges) != 0;
    }

    public boolean isDragging() {
        return mDragState == STATE_DRAGGING;
    }

    private void releaseViewForPointerUp() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
        final float xvel = clampMag(
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                mMinVelocity, mMaxVelocity);
        final float yvel = clampMag(
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId),
                mMinVelocity, mMaxVelocity);
        dispatchViewReleased(xvel, yvel);
    }

    private void dragTo(int left, int top, int dx, int dy) {
        int clampedX = left;
        int clampedY = top;
        final int oldLeft = mCapturedView.getLeft();
        final int oldTop = mCapturedView.getTop();
        if (dx != 0) {
            clampedX = mCallback.clampViewPositionHorizontal(mCapturedView, left, dx);
            mCapturedView.offsetLeftAndRight(clampedX - oldLeft);
        }
        if (dy != 0) {
            clampedY = mCallback.clampViewPositionVertical(mCapturedView, top, dy);
            mCapturedView.offsetTopAndBottom(clampedY - oldTop);
        }

        if (dx != 0 || dy != 0) {
            final int clampedDx = clampedX - oldLeft;
            final int clampedDy = clampedY - oldTop;
            mCallback.onViewPositionChanged(mCapturedView, clampedX, clampedY,
                    clampedDx, clampedDy);
        }
    }

    /**
     * Determine if the currently captured view is under the given point in the
     * parent view's coordinate system. If there is no captured view this method
     * will return false.
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return true if the captured view is under the given point, false otherwise
     *
     * 确定当前捕获的视图是否在父视图坐标系中的给定点之下。如果没有捕获视图，该方法将返回false。
     * @param x 要在父坐标系统中测试的X位置
     * @param y 在父坐标系统中要测试的Y位置
     * @return 如果捕获的视图位于给定点下方，则为True，否则为false
     */
    public boolean isCapturedViewUnder(int x, int y) {
        return isViewUnder(mCapturedView, x, y);
    }

    /**
     * Determine if the supplied view is under the given point in the
     * parent view's coordinate system.
     * @param view Child view of the parent to hit test
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return true if the supplied view is under the given point, false otherwise
     *
     * 确定所提供的视图是否位于父视图坐标系中的给定点之下。
     * @param view 要命中test的父级的子视图
     * @param x 要在父坐标系统中测试的X位置
     * @param y 在父坐标系统中要测试的Y位置
     * @return 如果提供的视图位于给定点下方，则为True，否则为false
     */
    public boolean isViewUnder(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        return x >= view.getLeft() &&
                x < view.getRight() &&
                y >= view.getTop() &&
                y < view.getBottom();
    }

    /**
     * Find the topmost child under the given point within the parent view's coordinate system.
     * The child order is determined using {@link Callback#getOrderedChildIndex(int)}.
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return The topmost child view under (x, y) or null if none found.
     *
     * 找到父视图坐标系中给定点下的最上面的子视图。
     * 使用{@link Callback#getOrderedChildIndex(int)}确定子顺序。
     * @param x 要在父坐标系统中测试的X位置
     * @param y 在父坐标系统中要测试的Y位置
     * @return (x, y)下最上面的子视图，如果没有找到则为空。
     */
    public View findTopChildUnder(int x, int y) {
        final int childCount = mParentView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = mParentView.getChildAt(mCallback.getOrderedChildIndex(i));
            if (x >= child.getLeft() && x < child.getRight() &&
                    y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    private int getEdgesTouched(int x, int y) {
        int result = 0;

        if (x < mParentView.getLeft() + mEdgeSize) result |= EDGE_LEFT;
        if (y < mParentView.getTop() + mEdgeSize) result |= EDGE_TOP;
        if (x > mParentView.getRight() - mEdgeSize) result |= EDGE_RIGHT;
        if (y > mParentView.getBottom() - mEdgeSize) result |= EDGE_BOTTOM;

        return result;
    }
}
