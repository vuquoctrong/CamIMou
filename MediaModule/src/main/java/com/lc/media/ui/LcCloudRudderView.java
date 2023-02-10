package com.lc.media.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lc.media.R;
import com.lc.media.annotation.Direction;


/**
 * Cloud platform control panel
 *
 * 云台控制面板
 */
@SuppressLint("DrawAllocation")
public class LcCloudRudderView extends View {
    private Point mRockerPosition;
    private final Point mCtrlPoint = new Point(100, 100);
    private float mSolidRudderRadius = 60;
    private float mChangeRudderRadius = 60;
    private int mWheelRadius = 100;
    private RudderListener listener = null;
    private int mDownState;
    private int firstX, firstY;
    private int dCtrlPointx, dCtrlPointy;
    private static Bitmap mBigCircleBg;
    private static Bitmap mBigCircleBg_h;
    private static Bitmap mSmallCircleBg;
    private static Bitmap mBigCircleLandScapeBg;
    private static Bitmap mBigCircleLandScapeBg_h;
    private static Bitmap mSmallCircleLandScapeBg;
    private float mAngle;
    private boolean mShowDirectPic;
    private boolean mCanTouch = true;
    private boolean mLandScapeOnly = false;
    //支持四方向
    private boolean mSupportFourDirection=true;


    public LcCloudRudderView(Context context) {
        super(context);
        init();
    }

    public LcCloudRudderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(context, attrs);
        init();
    }

    private void setAttribute(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LcCloudRudderView);
            mLandScapeOnly = array.getBoolean(R.styleable.LcCloudRudderView_landscape_only, false);
            array.recycle();
        }
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        this.setKeepScreenOn(true);
        mCanTouch = true;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);
        mRockerPosition = new Point(mCtrlPoint);
        setFocusable(true);
        setFocusableInTouchMode(true);

        if (mBigCircleBg == null) {
            mBigCircleBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_cloudstage_direction_default);
        }

        if (mBigCircleBg_h == null) {
            mBigCircleBg_h = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_cloudstage_direction_up);
        }

        if (mSmallCircleBg == null) {
            mSmallCircleBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_cloudstage_direction_button);
        }
        if (mBigCircleLandScapeBg == null) {
            mBigCircleLandScapeBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_video_cloudstage_direction_default);
        }

        if (mBigCircleLandScapeBg_h == null) {
            mBigCircleLandScapeBg_h = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_video_cloudstage_direction_up);
        }

        if (mSmallCircleLandScapeBg == null) {
            mSmallCircleLandScapeBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_video_cloudstage_direction_button);
        }
        mSolidRudderRadius = (float) (mSmallCircleBg.getHeight() / 2.0);
    }

    /**
     * Reset the cloud platform background image to distinguish direction 4 from direction 8
     *
     * 重置云台背景图，为了区分4方向和8方向
     */
    public void resetCircleBg() {
        if (mSupportFourDirection) {
            if (mBigCircleBg != null) {
                mBigCircleBg.recycle();
                mBigCircleBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_cloudstage_direction_default_four);
            }
            if (mBigCircleLandScapeBg != null) {
                mBigCircleLandScapeBg.recycle();
                mBigCircleLandScapeBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_video_cloudstage_direction_default_four);
            }
        } else {
            if (mBigCircleBg != null) {
                mBigCircleBg.recycle();
                mBigCircleBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_cloudstage_direction_default);
            }
            if (mBigCircleLandScapeBg != null) {
                mBigCircleLandScapeBg.recycle();
                mBigCircleLandScapeBg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_module_video_cloudstage_direction_default);
            }
        }
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bigCirclebg = mLandScapeOnly ? mBigCircleLandScapeBg : mBigCircleBg;
        if (bigCirclebg == null || bigCirclebg.isRecycled()) {
            return;
        }

        try {
            canvas.save();
            canvas.translate(mCtrlPoint.x, mCtrlPoint.y);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Rect srcRect = new Rect(0, 0, bigCirclebg.getWidth(), bigCirclebg.getHeight());
            RectF dstRcctF = new RectF(-mWheelRadius, -mWheelRadius, mWheelRadius, mWheelRadius);
            canvas.drawBitmap(bigCirclebg, srcRect, dstRcctF, paint);
            canvas.restore();

            if (mShowDirectPic) {

                Bitmap bigCircleBg_h = mLandScapeOnly ? mBigCircleLandScapeBg_h : mBigCircleBg_h;
                canvas.save();
                canvas.translate(mCtrlPoint.x, mCtrlPoint.y);
                canvas.rotate(mAngle);
                Rect srcRect2 = new Rect(0, 0, bigCircleBg_h.getWidth(), bigCircleBg_h.getHeight());
                RectF dstRcctF2 = new RectF(-mWheelRadius, -mWheelRadius, mWheelRadius, mWheelRadius);
                canvas.drawBitmap(bigCircleBg_h, srcRect2, dstRcctF2, paint);
                canvas.restore();
            }

            Bitmap smallCircleBg = mLandScapeOnly ? mSmallCircleLandScapeBg : mSmallCircleBg;
            Rect srcRect3 = new Rect(0, 0, smallCircleBg.getWidth(), smallCircleBg.getHeight());
            RectF dstRcctF3 = new RectF(mRockerPosition.x - mChangeRudderRadius, mRockerPosition.y - mChangeRudderRadius, mRockerPosition.x + mChangeRudderRadius, mRockerPosition.y + mChangeRudderRadius);
            canvas.drawBitmap(smallCircleBg, srcRect3, dstRcctF3, paint);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    public void setRudderListener(RudderListener rockerListener) {
        listener = rockerListener;
    }

    public boolean isSupportFourDirection() {
        return mSupportFourDirection;
    }

    /**
     * Set whether to support the four-direction cloud platform
     * @param supportFourDirection  Whether support four directions
     *
     * 设置是否支持四方向云台
     * @param supportFourDirection  是否支持四个方向
     */
    public void setSupportFourDirection(boolean supportFourDirection) {
        this.mSupportFourDirection = supportFourDirection;
        resetCircleBg();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mCanTouch) {
            return true;
        }
        int len = (int) Math.sqrt(Math.pow(mCtrlPoint.x - event.getX(), 2) + Math.pow(mCtrlPoint.y - event.getY(), 2));
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (len < mWheelRadius / 2) {
                    if (listener != null) {
                        listener.onSteeringWheelChangedBegin();
                    }
                    mDownState = 1;
                    firstX = (int) event.getX();
                    firstY = (int) event.getY();
                    dCtrlPointx = firstX - mCtrlPoint.x;
                    dCtrlPointy = firstY - mCtrlPoint.y;
                } else if (len >= (mWheelRadius / 2) && len <= mWheelRadius) {
                    if (listener != null) {
                        listener.onSteeringWheelChangedBegin();
                    }
                    mDownState = 2;
                    mShowDirectPic = true;
                    firstX = (int) event.getX();
                    firstY = (int) event.getY();
                    dCtrlPointx = firstX - mCtrlPoint.x;
                    dCtrlPointy = firstY - mCtrlPoint.y;
                    float radian = getRadian(mCtrlPoint, new Point((int) event.getX(), (int) event.getY()));
                    int angle = LcCloudRudderView.this.getAngleConvert(radian);
                    Direction direction = directConvert(angle);
                    if (listener != null) {
                        listener.onSteeringWheelChangedSingle(direction);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDownState == 1) {
                    int dragLen = (int) Math.sqrt(Math.pow(firstX - event.getX(), 2) + Math.pow(firstY - event.getY(), 2));
                    if (dragLen <= mWheelRadius - mChangeRudderRadius / 2) {
                        mRockerPosition.set((int) event.getX() - dCtrlPointx, (int) event.getY() - dCtrlPointy);
                    } else {
                        int cutRadius = (int) (mWheelRadius - mChangeRudderRadius / 2);
                        float radian = getRadian(mCtrlPoint, new Point((int) event.getX() - (firstX - mCtrlPoint.x), (int) event.getY() - (firstY - mCtrlPoint.y)));
                        mRockerPosition = new Point(mCtrlPoint.x + (int) (cutRadius * Math.cos(radian)), mCtrlPoint.y + (int) (cutRadius * Math.sin(radian)));

                    }
                    float radian = getRadian(mCtrlPoint, new Point((int) event.getX(), (int) event.getY()));
                    int angle = LcCloudRudderView.this.getAngleConvert(radian);
                    Direction direction = directConvert(angle);
                    if (dragLen >= (mWheelRadius / 2)) {//超过 边界再发送命令
                        mShowDirectPic = true;
                        if (listener != null) {
                            listener.onSteeringWheelChangedContinue(direction);
                        }
                    } else {//未超过边界，发送停止命令，图标不显示按下
                        mShowDirectPic = false;
                        if (listener != null) {
                            listener.onSteeringWheelChangedContinue(null);
                        }
                    }
                } else if (mDownState == 2) {
                    if (listener != null) {
                        if (len >= (mWheelRadius / 2) && len <= mWheelRadius) {
                            mShowDirectPic = true;
                            float radian = getRadian(mCtrlPoint, new Point((int) event.getX(), (int) event.getY()));
                            int angle = LcCloudRudderView.this.getAngleConvert(radian);
                            directConvert(angle);
                        } else {
                            mShowDirectPic = false;
                        }
                    }

                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mDownState == 1) {
                    mShowDirectPic = false;
                    mRockerPosition = new Point(mCtrlPoint);
                    if (listener != null) {
                        listener.onSteeringWheelChangedContinue(null);
                        listener.onSteeringWheelChangedEnd();
                    }

                } else if (mDownState == 2) {
                    mShowDirectPic = false;
                    float radian = getRadian(mCtrlPoint, new Point((int) event.getX(), (int) event.getY()));
                    int angle = LcCloudRudderView.this.getAngleConvert(radian);
                    Direction direction = directConvert(angle);
                    mRockerPosition = new Point(mCtrlPoint);
                    if (listener != null) {
                        //listener.onSteeringWheelChangedSingle(direction);
                        listener.onSteeringWheelChangedEnd();
                    }
                }
                firstX = 0;
                firstY = 0;
                mDownState = 0;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private static float getRadian(Point a, Point b) {
        float lenA = b.x - a.x;
        float lenB = b.y - a.y;
        float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
        float ang = (float) Math.acos(lenA / lenC);
        ang = ang * (b.y < a.y ? -1 : 1);
        return ang;
    }

    private Direction directConvert(int angle) {
        Direction direction = Direction.Left;
        if (mSupportFourDirection) {
            if (angle <= 45 || angle > 315) {
                direction = Direction.Right;
                mAngle = 90;
            } else if (angle > 45 && angle <= 135) {
                direction = Direction.Up;
                mAngle = 0;
            } else if (angle > 135 && angle <= 225) {
                direction = Direction.Left;
                mAngle = 270;
            } else if (angle > 225 && angle <= 315) {
                direction = Direction.Down;
                mAngle = 180;
            }

        } else {

            if (angle <= 22.5 || angle > 337.5) {
                direction = Direction.Right;
                mAngle = 90;
            } else if (angle > 22.5 && angle <= 67.5) {
                direction = Direction.Right_up;
                mAngle = 45;
            } else if (angle > 67.5 && angle <= 112.5) {
                direction = Direction.Up;
                mAngle = 0;
            } else if (angle > 112.5 && angle <= 157.5) {
                direction = Direction.Left_up;
                mAngle = 315;
            } else if (angle > 157.5 && angle <= 202.5) {
                direction = Direction.Left;
                mAngle = 270;
            } else if (angle > 202.5 && angle <= 247.5) {
                direction = Direction.Left_down;
                mAngle = 225;
            } else if (angle > 247.5 && angle <= 292.5) {
                direction = Direction.Down;
                mAngle = 180;
            } else if (angle > 292.5 && angle <= 337.5) {
                direction = Direction.Right_down;
                mAngle = 135;
            }
        }

        return direction;
    }

    private int getAngleConvert(float radian) {
        int tmp = (int) Math.round(radian / Math.PI * 180);
        if (tmp < 0) {
            return -tmp;
        } else {
            return 180 + (180 - tmp);
        }
    }

    public interface RudderListener {
        void onSteeringWheelChangedBegin();

        void onSteeringWheelChangedContinue(Direction direction);

        void onSteeringWheelChangedSingle(Direction direction);

        void onSteeringWheelChangedEnd();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int len = Math.min(w, h) / 2;
        mWheelRadius = (int) (len - mSolidRudderRadius / 2);
        mChangeRudderRadius = mWheelRadius * 17 / 40;

        mCtrlPoint.set(w / 2, h / 2);
        mRockerPosition.set(w / 2, h / 2);
        postInvalidate();
    }

    public void enable(boolean enabled) {
        if (mCanTouch && !enabled) {
            mShowDirectPic = false;
            mRockerPosition = new Point(mCtrlPoint);
            invalidate();
            if (listener != null) {
                listener.onSteeringWheelChangedContinue(null);
                listener.onSteeringWheelChangedEnd();
            }
        }
        setEnabled(enabled);
        setAlpha(enabled ? 1f : 0.5f);
        mCanTouch = enabled;
    }

    public void unit() {
        Bitmap bigCirclebg = mLandScapeOnly ? mBigCircleLandScapeBg : mBigCircleBg;
        Bitmap bigCircleBg_h = mLandScapeOnly ? mBigCircleLandScapeBg_h : mBigCircleBg_h;
        Bitmap smallCircleBg = mLandScapeOnly ? mSmallCircleLandScapeBg : mSmallCircleBg;
        if (bigCirclebg != null) {
            bigCirclebg.recycle();
            if (mLandScapeOnly) {
                mBigCircleLandScapeBg = null;
            } else {
                mBigCircleBg = null;
            }
        }
        if (bigCircleBg_h != null) {
            bigCircleBg_h.recycle();
            if (mLandScapeOnly) {
                mBigCircleLandScapeBg_h = null;
            } else {
                mBigCircleBg_h = null;
            }
        }
        if (smallCircleBg != null) {
            smallCircleBg.recycle();
            if (mLandScapeOnly) {
                mSmallCircleLandScapeBg = null;
            } else {
                mSmallCircleBg = null;
            }
        }
        listener = null;
    }
}
