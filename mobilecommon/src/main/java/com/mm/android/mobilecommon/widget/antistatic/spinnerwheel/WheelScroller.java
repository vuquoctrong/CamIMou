package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Scroller class handles scrolling events and updates the spinnerwheel
 */
public abstract class WheelScroller {
    /**
     * Scrolling listener interface
     *
     * 滚动侦听器接口
     */
    public interface ScrollingListener {
        /**
         * Scrolling callback called when scrolling is performed.
         * @param distance the distance to scroll
         *
         * 在执行滚动时调用滚动回调。
         * @param distance  滚动的距离
         */
        void onScroll(int distance);

        /**
         * This callback is invoked when scroller has been touched
         *
         * 当触摸滚轮时调用此回调
         */
        void onTouch();

        /**
         * This callback is invoked when touch is up
         *
         * 这个回调在touch打开时被调用
         */
        void onTouchUp();

        /**
         * Starting callback called when scrolling is started
         *
         * 开始滚动时调用回调
         */
        void onStarted();
        
        /**
         * Finishing callback called after justifying
         *
         * 在调整后完成回调
         */
        void onFinished();
        
        /**
         * Justifying callback called to justify a view when scrolling is ended
         *
         * 当滚动结束时，调用调整回调来调整视图
         */
        void onJustify();
    }
    
    /** Scrolling duration */
    private static final int SCROLLING_DURATION = 400;

    /** Minimum delta for scrolling */
    public static final int MIN_DELTA_FOR_SCROLLING = 1;

    // Listener
    private ScrollingListener listener;
    
    // Context
    private Context context;
    
    // Scrolling
    private GestureDetector gestureDetector;
    protected Scroller scroller;
    private int lastScrollPosition;
    private float lastTouchedPosition;
    private boolean isScrollingPerformed;

    /**
     * Constructor
     * @param context the current context
     * @param listener the scrolling listener
     *
     * 构造器
     * @param context 当前上下文呢
     * @param listener 滚动监听器
     */
    public WheelScroller(Context context, ScrollingListener listener) {
        gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Do scrolling in onTouchEvent() since onScroll() are not call immediately
                //  when user touch and move the spinnerwheel
                return true;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                lastScrollPosition = 0;
                scrollerFling(lastScrollPosition, (int) velocityX, (int) velocityY);
                setNextMessage(MESSAGE_SCROLL);
                return true;
            }

            // public boolean onDown(MotionEvent motionEvent);

        });
        gestureDetector.setIsLongpressEnabled(false);
        
        scroller = new Scroller(context);

        this.listener = listener;
        this.context = context;
    }
    
    /**
     * Set the the specified scrolling interpolator
     * @param interpolator the interpolator
     *
     * 设置指定的滚动插值器
     * @param interpolator 插入器
     */
    public void setInterpolator(Interpolator interpolator) {
        scroller.forceFinished(true);
        scroller = new Scroller(context, interpolator);
    }
    
    /**
     * Scroll the spinnerwheel
     * @param distance the scrolling distance
     * @param time the scrolling duration
     *
     * 滚动滚轮
     * @param distance 滚动的距离
     * @param time 滚动的持续时间
     */
    public void scroll(int distance, int time) {
        scroller.forceFinished(true);
        lastScrollPosition = 0;
        scrollerStartScroll(distance, time != 0 ? time : SCROLLING_DURATION);
        setNextMessage(MESSAGE_SCROLL);
        startScrolling();
    }
   
    /**
     * Stops scrolling
     *
     * 停止滚动
     */
    public void stopScrolling() {
        scroller.forceFinished(true);
    }
    
    /**
     * Handles Touch event 
     * @param event the motion event
     * @return boolean Boolean
     *
     * 处理触摸事件
     * @param event 移动事件
     * @return boolean 布尔值
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastTouchedPosition = getMotionEventPosition(event);
                scroller.forceFinished(true);
                clearMessages();
                listener.onTouch();
                break;

            case MotionEvent.ACTION_UP:
                if (scroller.isFinished())
                    listener.onTouchUp();
                break;


            case MotionEvent.ACTION_MOVE:
                // perform scrolling
                int distance = (int)(getMotionEventPosition(event) - lastTouchedPosition);
                if (distance != 0) {
                    startScrolling();
                    listener.onScroll(distance);
                    lastTouchedPosition = getMotionEventPosition(event);
                }
                break;
        }

        if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }

        return true;
    }


    // Messages
    private static final int MESSAGE_SCROLL = 0;
    private static final int MESSAGE_JUSTIFY = 1;
    
    /**
     * Set next message to queue. Clears queue before.
     * @param message the message to set
     *
     * 将下一条消息设置为队列。清除队列之前。
     * @param message 要设置的消息
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * Clears messages from queue
     *
     * 从队列中清除消息
     */
    private void clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.removeMessages(MESSAGE_JUSTIFY);
    }
    
    // animation handler
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currPosition = getCurrentScrollerPosition();
            int delta = lastScrollPosition - currPosition;
            lastScrollPosition = currPosition;
            if (delta != 0) {
                listener.onScroll(delta);
            }
            
            // scrolling is not finished when it comes to final Y
            // so, finish it manually 
            if (Math.abs(currPosition - getFinalScrollerPosition()) < MIN_DELTA_FOR_SCROLLING) {
                // currPosition = getFinalScrollerPosition();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };
    
    /**
     * Justifies spinnerwheel
     *
     * 证明滚轮
     */
    private void justify() {
        listener.onJustify();
        setNextMessage(MESSAGE_JUSTIFY);
    }

    /**
     * Starts scrolling
     *
     * 开始滚动
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            listener.onStarted();
        }
    }

    /**
     * Finishes scrolling
     *
     * 完成滚动
     */
    protected void finishScrolling() {
        if (isScrollingPerformed) {
            listener.onFinished();
            isScrollingPerformed = false;
        }
    }

    protected abstract int getCurrentScrollerPosition();

    protected abstract int getFinalScrollerPosition();

    protected abstract float getMotionEventPosition(MotionEvent event);

    protected abstract void scrollerStartScroll(int distance, int time);

    protected abstract void scrollerFling(int position, int velocityX, int velocityY);
}
