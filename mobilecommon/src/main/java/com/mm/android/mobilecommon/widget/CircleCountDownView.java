package com.mm.android.mobilecommon.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.utils.UIUtils;
/**
 * Circular countdown control
 *
 * 圆形倒计时控件
 **/
public class CircleCountDownView extends View {
    private int mRingColor;         //圆轮颜色
    private int mCirbgColor;        //圆形背景色
    private float mRingWidth;      //圆轮宽度
    private int mRingProgessTextSize;           //圆轮进度值文本大小
    private int mWidth;                         //宽度
    private int mHeight;                        //高度
    private Paint mPaint;
    private RectF mRectF;                       //圆环的矩形区域
    private int mProgessTextColor;
    private int mCountdownTime;
    private int mTotalCountdownTime;
    private float mCurrentProgress;
    private OnCountDownFinishListener mListener;
    ValueAnimator mValueAnimator;
    private int mMiddleTime;            //设置中间提醒时间，即0-CountdownTime之间的时间
    private boolean mHasMiddleTimeUp=false;

    public CircleCountDownView(Context context) {
        super(context);
        init();
    }

    public CircleCountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
    }

    public CircleCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }
    Paint textPaint;
    private void init() {
        textPaint = new Paint();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mRingColor);                    //颜色
        mPaint.setStyle(Paint.Style.STROKE);            //空心
        mPaint.setStrokeWidth(mRingWidth);             //宽度
        mCirbgColor = Color.parseColor("#e0e0e0");
        this.setWillNotDraw(false);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleCountDownView);
        mRingColor = a.getColor(R.styleable.CircleCountDownView_ringColor, mCirbgColor);
        mCirbgColor=a.getColor(R.styleable.CircleCountDownView_ringbgColor,mCirbgColor);
        mRingWidth = a.getDimensionPixelSize(R.styleable.CircleCountDownView_ringWidth, UIUtils.dp2px(context, 10));
        mRingProgessTextSize = a.getDimensionPixelSize(R.styleable.CircleCountDownView_progressTextSize, UIUtils.sp2px(context, 25));
        mProgessTextColor = a.getColor(R.styleable.CircleCountDownView_progressTextColor, mCirbgColor);
        mCountdownTime = a.getInteger(R.styleable.CircleCountDownView_countdownTime, 60);
        mTotalCountdownTime=mCountdownTime;
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if(mRectF == null){
            mRectF =    new RectF(0 + mRingWidth / 2, 0 + mRingWidth / 2,
                    mWidth - mRingWidth / 2, mHeight - mRingWidth / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mCirbgColor);                    //颜色
        canvas.drawArc(mRectF, -90, -360, false, mPaint);
        mPaint.setColor(mRingColor);                    //颜色
        canvas.drawArc(mRectF, -90, mCurrentProgress - 360, false, mPaint);
        //绘制文本
        String text = mCountdownTime - (int) (mCurrentProgress / 360f * mCountdownTime)+"";

        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(mRingProgessTextSize);
        textPaint.setFakeBoldText(true);
        textPaint.setColor(mProgessTextColor);

        //文字居中显示
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (int) ((mRectF.bottom + mRectF.top ) / 2);
        canvas.drawText(text, mRectF.centerX(), baseline, textPaint);
        textPaint.setFakeBoldText(false);
        canvas.drawText("s",mRectF.centerX(), baseline-fontMetrics.bottom-fontMetrics.top+10, textPaint);
    }

    private ValueAnimator getValA(long countdownTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    public void setCountdownTime(int mCountdownTime) {
        this.mCountdownTime = mCountdownTime;
        this.mTotalCountdownTime=this.mCountdownTime;
    }

    public void setMiddleTime(int middleTime){
        this.mMiddleTime=middleTime;
    }

     Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {         // handle message
            switch (msg.what) {
                case 1:
                    mTotalCountdownTime--;
                    if (mTotalCountdownTime > 0) {
                        mCurrentProgress = (int) (360 * ((mCountdownTime-mTotalCountdownTime) * 1.0f / mCountdownTime));
                        int curTime = (int) (mCurrentProgress / 360f * mCountdownTime);
                        if (mMiddleTime > 0 && curTime >= mMiddleTime && !mHasMiddleTimeUp && mListener != null) {
                            mListener.middleTimeUp();
                            mHasMiddleTimeUp = true;
                        }
                        invalidate();
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        //倒计时结束回调
                        if (mListener != null) {
                            mListener.countDownFinished();
                        }
                        setClickable(true);
                    }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * start the countdown
     *
     * 开始倒计时
     */
    public void startCountDown() {
        mHasMiddleTimeUp=false;
        setClickable(false);
        mValueAnimator = getValA((long)mCountdownTime * 1000);
        Message message = handler.obtainMessage(1);
        handler.sendMessageDelayed(message, 1000);
        mValueAnimator.start();
    }

    public int getCurCountdownTime() {
        return mCountdownTime - (int) (mCurrentProgress / 360f * mCountdownTime);
    }

    public void stopCountDown(){
        mListener=null;
        if(mValueAnimator!=null) {
            mValueAnimator.cancel();
        }
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
    }

    public void setCountDownListener(OnCountDownFinishListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCountDownFinishListener {
        void countDownFinished();
        void middleTimeUp();
    }
}
