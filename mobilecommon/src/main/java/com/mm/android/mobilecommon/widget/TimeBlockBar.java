package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.entity.ClipRect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class TimeBlockBar extends View {

	public static final int PORTRAIT = 0;                    //竖屏模式
	public static final int LANDSCAPE = 1;                    //横屏模式
	private static int TIME = 24;                    //时间段24小时
	public static int MAX = TIME * 3600;            //进度条最大值
	private static final int NONE = 0;                    //NONE模式
	private static final int DRAG = 1;                    //拖拽模式
	private static final int ZOOM = 2;                    //缩放模式
	private static final int SEEK_DELEY = 800;                    //seek延时触发时间

	private int mWinIndex;                                        //由于seek延时触发和一个进度条对应N个窗口的模式，seek结束时需要把窗口号回调出去处理
	private int mMode = NONE;                    //手势模式
	private int mOrientation = LANDSCAPE;            //横竖屏模式标志
	private Date mStartDate;                                        //开始时间
	private Date mEndDate;                                        //结束时间
	private float mWidth = 0;                    //控件宽度
	private float mHeight = 0;                    //控件高度
	private float mSourceWidth = 0;                    //控件可见范围宽度
	private float mEmptyWidth;                                    //进度条前后预留的控件可见范围的一半
	private float mDrawWidth = 0;                    //整个控件绘制范围宽度，不包含首尾空白
	private float mDrawStart = 0;                    //开始绘制X
	private float mDrawEnd = 0;                    //结束绘制Y
	private float mThumbX = 0;                    //Thumb X坐标
	private float mMidX = 0;                    //缩放基准点X坐标
	private float mMidY = 0;                    //缩放基准点Y坐标
	private float mStartX = 0;                    //一次DRAG的开始点X值
	private float mStartY = 0;                    //一次DRAG的开始点Y值
	private float mScaleTextSize = sp2px(12);            //刻度文字大小
	private int mScaleTextColor;                                //刻度文字颜色
	private float mDateTextSize = sp2px(14);            //日期文字(当前时间)大小
	private int mDateTextColor;                                    //日期文字颜色
	private int mThumbColor;                                    //Thumb颜色
	private int mBackGroundColor;                                //背景颜色
	private int mFillColor;                                        //填充时间的颜色
	private int mMaxScale = 50;                    //最大缩放倍数
	private float mCurPos = 0.f;                    //当前进度
	private float mCurrentScale = 1.0f;                    //当前缩放系数
	private float mCurrentStart = 0.f;                    //当前开始绘制X
	private float mCurrentEnd = 0.f;                    //当前结束绘制Y
	private float mZoomStartdis = 0.f;                    //一次Zoom开始时两个手指间距离（down）
	private float mZoomEnddis = 0.f;                    //一次Zoom结束时两个手指间距离（up/cancel)
	private boolean mCanTouch = true;                    //能否Touch
	private ArrayList<ClipRect> mClipRects;                                        //填充内容
	private Timer mSeekTimer = null;                //seek延时触发定时器
	private TimerTask mSeekTimerTask = null;                //seek触发任务
	public static final float DEFAULT_SCALE = 6.0f;


	public TimeBlockBar(Context context) {
		this(context, null);
	}

	public TimeBlockBar(Context context, AttributeSet attrs) {
		// 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, android.R.attr.seekBarStyle);
	}

	public TimeBlockBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	/**
	 * Initialize function
	 * @param context
	 * @param attrs
	 * @author feidan August 4, 2014 at 5:23:25 PM
	 *
	 * 初始化函数
	 * @param context
	 * @param attrs
	 * @author feidan 2014年8月4日 下午5:23:25
	 */
	private void init(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeBar);
		mBackGroundColor = typedArray.getColor(R.styleable.TimeBar_background_color, Color.WHITE);
		mFillColor = typedArray.getColor(R.styleable.TimeBar_fill_color, Color.WHITE);
		mScaleTextSize = sp2px(typedArray.getDimension(R.styleable.TimeBar_scale_text_size, 12));
		mScaleTextColor = typedArray.getColor(R.styleable.TimeBar_scale_text_color, Color.GRAY);
		mDateTextSize = sp2px(typedArray.getDimension(R.styleable.TimeBar_date_text_size, 14));
		mDateTextColor = typedArray.getColor(R.styleable.TimeBar_date_text_color, Color.GRAY);
		mThumbColor = typedArray.getColor(R.styleable.TimeBar_thumb_color, 0XFF1B81DC);
		TIME = typedArray.getInt(R.styleable.TimeBar_max_time, 24);
		typedArray.recycle();

		MAX = TIME * 3600;
		mCurPos = MAX / 2;

		setStartDate(new Date());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawBack(canvas);
		drawFill(canvas);
//        drawText(canvas);
//        drawThumb(canvas);
		//drawCurPos(canvas);
	}

	public void setBackGroundColor(int backGroundColor) {
		mBackGroundColor = backGroundColor;
	}

	/**
	 * Draw the background
	 * @param canvas
	 *
	 * 绘制背景
	 * @param canvas
	 */
	private void drawBack(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(mBackGroundColor);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			canvas.drawRoundRect(mDrawStart - mEmptyWidth, 0, mDrawEnd + mEmptyWidth, mHeight, 6f, 6f, paint);
		} else {
			canvas.drawRect(mDrawStart - mEmptyWidth, 0, mDrawEnd + mEmptyWidth, mHeight, paint);
		}
	}

	/**
	 * Draw fill time
	 * @param canvas
	 *
	 * 绘制填充时间
	 * @param canvas
	 */
	private void drawFill(Canvas canvas) {
		//绘制Fill
		if (mClipRects == null || mClipRects.size() <= 0) {
			return;
		}

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		for (ClipRect clipRect : mClipRects) {
			float xStart = mDrawWidth * (float) clipRect.start / MAX + mDrawStart;
			float xEnd = mDrawWidth * (float) clipRect.end / MAX + mDrawStart;
//			paint.setColor(clipRect.color == -1 ? mFillColor : clipRect.color);
			paint.setColor(mFillColor);

			//小于一个像素的会绘制不出来，自动填充成1个像素
			if (Math.abs(xEnd - xStart) < 1) {
				xEnd = xStart + 1;
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				canvas.drawRoundRect(xStart, 0, xEnd, mHeight, 6f, 6f, paint);
			} else {
				canvas.drawRect(xStart, 0, xEnd, mHeight, paint);
			}
		}
	}


	/**
	 * Rendering text
	 * @param canvas
	 *
	 * 绘制文字
	 * @param canvas
	 */
	private void drawText(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(mScaleTextColor);
		paint.setTextAlign(Align.LEFT);
		float cellWidth = mDrawWidth / (TIME * 60);
		paint.setTextSize(mScaleTextSize);
		float lineHeight = dip2px(8);

		//先计算是绘制
		for (int i = 0; i <= TIME * 60; i++) {
			float lineX = cellWidth * (i) + mDrawStart;                    //每个刻度的X坐标

			//屏幕时间跨度为24h，每刻度6h，每刻度为6小格，每个刻度显示一个时间点（最大刻度）；
			if (mCurrentScale >= 1 && mCurrentScale < 2.4) {
				if (i % (60 * 6) == 0) {
					//绘制每6小时一个的大刻度
					String text = String.format("%02d:%02d", i / 60, 0);
					drawCell(canvas, paint, lineHeight, lineX, text);
				} else if (i % 60 == 0) {
					//绘制每1小时一个的小刻度
					drawCell(canvas, paint, lineHeight / 2, lineX, null);
				}
			}

			//屏幕时间跨度为10h，每刻度1h，每刻度为6小格，每两个刻度显示一个时间点；
			if (mCurrentScale >= 2.4 && mCurrentScale < 4) {
				if (i % 60 == 0) {
					//绘制每1小时一个的大刻度，每两个大刻度绘制文字
					String text = null;
					if ((i / 60) % 2 == 0) {
						text = String.format("%02d:%02d", i / 60, 0);
					}
					drawCell(canvas, paint, lineHeight, lineX, text);
				} else if (i % 10 == 0) {
					//绘制10分钟一个的小刻度
					drawCell(canvas, paint, lineHeight / 2, lineX, null);
				}
			}

			//屏幕时间跨度为6h，每刻度1h，每刻度为6小格，每个刻度显示一个时间点；
			//屏幕时间跨度为4h，每刻度为1h，每刻度为6小格，每个刻度显示一个时间点（默认刻度）；
			if (mCurrentScale >= 4 && mCurrentScale < 12) {
				if (i % 60 == 0) {
					//绘制每1小时一个的大刻度
					String text = String.format("%02d:%02d", i / 60, 0);
					drawCell(canvas, paint, lineHeight, lineX, text);
				} else if (i % 10 == 0) {
					//绘制10分钟一个的小刻度
					drawCell(canvas, paint, lineHeight / 2, lineX, null);
				}
			}

			//屏幕时间跨度为2h，每刻度30min，每刻度6小格，每个刻度显示一个时间点；
			if (mCurrentScale >= 12 && mCurrentScale < 24) {
				if (i % 30 == 0) {
					//绘制每30分钟一个的大刻度
					String text = String.format("%02d:%02d", i / 60, ((i / 30) % 2) * 30);
					drawCell(canvas, paint, lineHeight, lineX, text);
				} else if (i % 5 == 0) {
					//绘制每5分钟一个的小刻度
					drawCell(canvas, paint, lineHeight / 2, lineX, null);
				}
			}

			//屏幕时间跨度为1h，每刻度10min，每刻度5小格，每个刻度显示一个时间点；
			if (mCurrentScale >= 24) {
				if (i % 10 == 0) {
					//绘制每10分钟一个的大刻度
					String text = String.format("%02d:%02d", i / 60, i % 60);
					drawCell(canvas, paint, lineHeight, lineX, text);
				} else if (i % 2 == 0) {
					//绘制每2分钟一个的小刻度
					drawCell(canvas, paint, lineHeight / 2, lineX, null);
				}
			}
		}
	}


	private void drawCell(Canvas canvas, Paint paint, float lineHeight,
						  float lineX, String textString) {
		if (!(lineX >= 0 && lineX <= mSourceWidth)) {
			//在可见范围之外，不绘制
			return;
		}

		float textLen = 0;  //文字长度
		int textHeight = 0; //文字高度
		if (!TextUtils.isEmpty(textString)) {
			textLen = paint.measureText(textString);
			FontMetrics fm = paint.getFontMetrics();
			textHeight = (int) Math.ceil(fm.descent - fm.ascent);
		}

		canvas.drawLine(lineX, 0, lineX, lineHeight, paint);                            //绘制上刻度线
		canvas.drawLine(lineX, mHeight, lineX, mHeight - lineHeight, paint);            //绘制下刻度线
		if (!TextUtils.isEmpty(textString)) {
			canvas.drawText(textString, lineX - textLen / 2, lineHeight + textHeight, paint);    //绘制时间文字
		}
	}

	/**
	 * Drawing Thumb Thumb does not need to swipe in the PAD version
	 * @param canvas
	 *
	 * 绘制Thumb 在PAD版本中Thumb不需要滑动
	 * @param canvas
	 */
	private void drawThumb(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(mThumbColor);
		paint.setAntiAlias(true);

		float width = dip2px(11);
		float height = dip2px(5);

		//画直线
		paint.setStrokeWidth(0);
		canvas.drawLine(mThumbX, height, mThumbX, mHeight - height, paint);

		//画上三角
		Path path1 = new Path();
		path1.moveTo(mThumbX - width / 2, 0);
		path1.lineTo(mThumbX + width / 2, 0);
		path1.lineTo(mThumbX, height);
		path1.close();
		canvas.drawPath(path1, paint);

		//画下三角
		path1.moveTo(mThumbX - width / 2, mHeight);
		path1.lineTo(mThumbX, mHeight - height);
		path1.lineTo(mThumbX + width / 2, mHeight);
		path1.close();
		canvas.drawPath(path1, paint);
	}

	/**
	 * Draws the current date and time
	 * @param canvas
	 * @author feidan July 3, 2014 at 8:57:53 PM
	 *
	 * 绘制当前日期和时间
	 * @param canvas
	 * @author feidan 2014年7月3日 下午8:57:53
	 */
	private void drawCurPos(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(mDateTextColor);
		paint.setTextSize(mDateTextSize);


		int paddingCenter = dip2px(14);                                //日期和当前时间中间的空间的一半
		String dateStr = getDateStr(mCurPos);                        //日期字符串
		FontMetrics fm = paint.getFontMetrics();
		int textHeight = (int) Math.ceil(fm.descent - fm.ascent);    //字体高度

		//绘制时间
		canvas.drawText(getTimeStr(mCurPos), mThumbX + paddingCenter, mHeight / 2 + textHeight / 2, paint);

		//绘制日期
		float dateStrLen = paint.measureText(dateStr);    //文字长度
		canvas.drawText(getDateStr(mCurPos), mThumbX - dateStrLen - paddingCenter, mHeight / 2 + textHeight / 2, paint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mHeight = h;
		mSourceWidth = w;

		if (mDrawWidth == 0) {
			mDrawWidth = w;
			mDrawStart = 0;
			mDrawEnd = mDrawWidth;
			mEmptyWidth = mDrawWidth / 2;
			mThumbX = mDrawWidth / 2;
		} else {
			mDrawWidth = mDrawWidth * w / oldw;
			mDrawStart = mDrawStart * w / oldw;
			mDrawEnd = mDrawEnd * w / oldw;
			mEmptyWidth = mEmptyWidth * w / oldw;
			mThumbX = mThumbX * w / oldw;
		}

		mCurrentStart = mDrawStart;
		mCurrentEnd = mDrawEnd;
		mWidth = mDrawWidth;
		mMidX = mWidth / 2;
		mMidY = 0.0f;
	}

	/**
	 * Gets the coordinates of the scaling reference point
	 * @param event
	 * @author feidan September 2, 2014 at 7:48:06 PM
	 *
	 * 获取缩放基准点坐标
	 * @param event
	 * @author feidan 2014年9月2日 下午7:48:06
	 */
	private void midPoint(MotionEvent event) {
		mMidX = mThumbX;
		mMidY = event.getY(0) + event.getY(1);
		mMidY /= 2;
	}

	/**
	 * zoom
	 * @param zoom How many times to zoom in/out
	 * @param x    Scale the reference point X
	 * @param y    Scale the reference point Y
	 * @author feidan September 2, 2014 at 7:08:54 PM
	 *
	 * 缩放
	 * @param zoom 要放大/缩小的倍数
	 * @param x    缩放基准点X
	 * @param y    缩放基准点Y
	 * @author feidan 2014年9月2日 下午7:08:54
	 */
	private void setZoom(float zoom, float x, float y) {
		if (mWidth * zoom < mSourceWidth) {
			//缩小的比控件原始大小还要小时，做边界限制。只能缩小到控件的原始大小。防止异常绘制，进度条会异常跳动。
			float scale = mSourceWidth / mWidth;
			mDrawWidth = mSourceWidth;
			mDrawStart = x - scale * (x - mCurrentStart);
			mDrawEnd = scale * (mCurrentEnd - x) + x;
		} else {
			mDrawWidth = mWidth * zoom;
			mDrawStart = x - zoom * (x - mCurrentStart);
			mDrawEnd = zoom * (mCurrentEnd - x) + x;
		}
	}

	/**
	 * slide
	 * @param x Where X is going to move to
	 * @param y Where Y is going to move to
	 * @author feidan July 7, 2014 at 2:34:38 PM
	 *
	 * 滑动
	 * @param x X要移动到的位置
	 * @param y Y要移动到的位置
	 * @author feidan 2014年7月7日 下午2:34:38
	 */
	private void setMove(float x, float y) {
		float tmpStart = mCurrentStart + x;
		float tmpEnd = mCurrentEnd + x;
		if (tmpStart > mEmptyWidth) {
			mDrawStart = mEmptyWidth;
			x = mDrawStart - mCurrentStart;

		} else if (tmpEnd < mSourceWidth - mEmptyWidth) {
			mDrawEnd = mSourceWidth - mEmptyWidth;
			x = mDrawEnd - mCurrentEnd;
		}

		mDrawStart = mCurrentStart + x;
		mDrawEnd = mCurrentEnd + x;
		controlSide();
		invalidate();
	}

	/**
	 * Boundary restrictions
	 * @author feidan September 2, 2014 at 7:47:33 PM
	 *
	 * 边界限制
	 * @author feidan 2014年9月2日 下午7:47:33
	 */
	private void controlSide() {
		if (mDrawWidth < mSourceWidth) {
			mDrawWidth = mSourceWidth;
			mDrawStart = 0;
			mDrawEnd = mDrawWidth - mDrawStart;
		}

		if (mDrawStart > mEmptyWidth) {
			mDrawStart = mEmptyWidth;
		}

		if (mDrawEnd < mSourceWidth - mEmptyWidth) {
			mDrawEnd = mSourceWidth - mEmptyWidth;
		}
	}

	//************************一些帮助方法***************************//

	/**
	 * Distance between two points
	 * @param event
	 * @return
	 * @author feidan September 2, 2014 at 11:00:01 AM
	 *
	 * 两点间距离
	 * @param event
	 * @return
	 * @author feidan 2014年9月2日 上午11:00:01
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
		//return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * More current progress to get the time string
	 * @param curPos
	 * @return
	 * @author feidan 8:16:32 PM on July 3, 2014
	 *
	 * 更具当前进度获取时间字符串
	 * @param curPos
	 * @return
	 * @author feidan 2014年7月3日 下午8:16:32
	 */
	@SuppressWarnings("deprecation")
	private String getTimeStr(float curPos) {
		long curTime = (mStartDate.getTime() / 1000L) + (long) curPos;
		Date date = new Date(curTime * 1000);
		String str = String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());
		return str;
	}

	/**
	 * More current progress to get the date string
	 * @param curPos
	 * @return
	 * @author feidan On July 3, 2014 at 8:21:49 PM
	 *
	 * 更具当前进度获取日期字符串
	 * @param curPos
	 * @return
	 * @author feidan 2014年7月3日 下午8:21:49
	 */
	private String getDateStr(float curPos) {
		Date date;
		float value = TIME / 24f;                //进度条表示的天数
		if (curPos < MAX / value) {
			//第一天
			date = mStartDate;
		} else {
			//后一天
			date = mEndDate;
		}

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	/**
	 * dip2px
	 * @param dpValue
	 * @return
	 * @author feidan September 2, 2014 at 10:52:35 AM
	 *
	 * dip2px
	 * @param dpValue
	 * @return
	 * @author feidan 2014年9月2日 上午10:52:35
	 */
	private int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * sp2px
	 * @param spValue
	 * @return
	 * @author feidan September 2, 2014 at 10:52:42 am
	 *
	 * sp2px
	 * @param spValue
	 * @return
	 * @author feidan 2014年9月2日 上午10:52:42
	 */
	public int sp2px(float spValue) {
		final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	//************************Public方法**************************//

	/**
	 * Set the start time of the progress bar to 48 hours
	 * @param date
	 * @author feidan July 3, 2014 at 8:43:06 PM
	 *
	 * 设置进度条开始时间，48小时的
	 * @param date
	 * @author feidan 2014年7月3日 下午8:43:06
	 */
	public void setStartDate(Date date) {
		this.mStartDate = date;
		int value = TIME / 24 - 1;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, value);
		Date endDate = calendar.getTime();
		this.mEndDate = endDate;
	}

	public Date getStartDate() {
		return mStartDate;
	}

	public long getStartTime() {
		return mStartDate.getTime();
	}

	public long getEndTime() {
		return mEndDate.getTime();
	}

	/**
	 * Set the scale text size
	 * @param size
	 * @author feidan September 1, 2014 at 3:48:28 PM
	 *
	 * 设置刻度文字大小
	 * @param size
	 * @author feidan 2014年9月1日 下午3:48:28
	 */
	public void setScaleTextSize(int size) {
		this.mScaleTextSize = size;
	}

	/**
	 * Set the scale text color
	 * @param color
	 * @author feidan September 1, 2014 at 3:49:26 PM
	 *
	 * 设置刻度文字颜色
	 * @param color
	 * @author feidan 2014年9月1日 下午3:49:26
	 */
	public void setScaleTextColrt(int color) {
		this.mScaleTextColor = color;
	}

	/**
	 * Set the date (current time) text size
	 * @param size
	 * @author feidan September 1, 2014 at 3:50:11 PM
	 *
	 * 设置日期（当前时间）文字大小
	 * @param size
	 * @author feidan 2014年9月1日 下午3:50:11
	 */
	public void setDateTextSize(int size) {
		this.mDateTextSize = size;
	}

	/**
	 * Set the date (current time) text color
	 * @param color
	 * @author feidan September 1, 2014 at 3:51:03 PM
	 *
	 * 设置日期（当前时间）文字颜色
	 * @param color
	 * @author feidan 2014年9月1日 下午3:51:03
	 */
	public void setDateTextColor(int color) {
		this.mDateTextColor = color;
	}

	/**
	 * Set the Angle of thumb
	 * @param color
	 * @author feidan September 1, 2014 at 3:53:15 PM
	 *
	 * 设置Thumb颜色
	 * @param color
	 * @author feidan 2014年9月1日 下午3:53:15
	 */
	public void setThumbColor(int color) {
		this.mThumbColor = color;
	}

	/**
	 * Set the fill color
	 * @param color
	 * @author feidan September 2, 2014 at 10:24:44 AM
	 *
	 * 设置填充颜色
	 * @param color
	 * @author feidan 2014年9月2日 上午10:24:44
	 */
	public void setFillColor(int color) {
		this.mFillColor = color;
	}

	public void setFillColor(String color) {
		this.mFillColor = Color.parseColor(color);
	}

	/**
	 * Set the horizontal and vertical mode
	 * @param orientation
	 * @author feidan 3:20:34 PM September 1, 2014
	 *
	 * 设置横竖屏模式
	 * @param orientation
	 * @author feidan 2014年9月1日 下午3:20:34
	 */
	public void setOrientation(int orientation) {
		this.mOrientation = orientation;
	}

	/**
	 * Set fill time
	 * @param clipRects
	 * @author feidan September 1, 2014 at 3:47:43 PM
	 *
	 * 设置填充时间
	 * @param clipRects
	 * @author feidan 2014年9月1日 下午3:47:43
	 */
	public void setClipRects(ArrayList<ClipRect> clipRects) {
		if (mClipRects == null) {
			mClipRects = new ArrayList<ClipRect>();
		}

		mClipRects.clear();// addby 32752 2017.07.29
		mClipRects.addAll(clipRects);

		invalidate();
	}

	public int getClipRectCounts() {
		if (mClipRects == null) {
			return 0;
		} else {
			return mClipRects.size();
		}
	}

	public void clearClipRects() {
		if (mClipRects != null && mClipRects.size() != 0) {
			mClipRects = null;
			invalidate();
		}

	}

	/**
	 * Gets the current progress, down to seconds
	 * @author feidan 8:11:13 PM on July 3, 2014
	 *
	 * 获取当前进度，精确到秒
	 * @author feidan 2014年7月3日 下午8:11:13
	 */
	public float getProgress() {
		return mCurPos;
	}

	/**
	 * Sets whether the Touch event is available
	 * @param touch
	 * @author feidan September 1, 2014 at 3:56:22 PM
	 *
	 * 设置Touch事件是否可用
	 * @param touch
	 * @author feidan 2014年9月1日 下午3:56:22
	 */
	public void setCanTouch(boolean touch) {
		mCanTouch = touch;
	}

	/**
	 * Setting the current progress
	 * @param progress
	 * @author feidan July 7, 2014 at 1:41:47 PM
	 *
	 * 设置当前进度
	 * @param progress
	 * @author feidan 2014年7月7日 下午1:41:47
	 */
	public void setProgress(float progress) {
		if (progress < 0) {
			progress = 0;
		}

		mCurPos = progress;
//        mDrawStart = mThumbX - progress / MAX * mWidth;
		mDrawStart = 0;
		mDrawEnd = mDrawStart + mDrawWidth;
		mCurrentStart = mDrawStart;
		mCurrentEnd = mDrawEnd;
		controlSide();
		invalidate();
	}

	/**
	 * Zoom to complete
	 * @param scale
	 *
	 * 缩放完成
	 * @param scale
	 */
	public void scaleEnd(float scale) {
		mCurrentScale *= scale;
		if (mCurrentScale > mMaxScale) {
			mCurrentScale = mMaxScale;
		} else if (mCurrentScale < 1.0f) {
			mCurrentScale = 1.0f;
		}
		if (mDrawStart > mEmptyWidth) {
			mDrawStart = mEmptyWidth;
		}
		if (mDrawEnd < mSourceWidth - mEmptyWidth) {
			mDrawEnd = mSourceWidth - mEmptyWidth;
		}
		mWidth = mDrawWidth;
		mCurrentStart = mDrawStart;
		mCurrentEnd = mDrawEnd;
		controlSide();
	}

	/**
	 * Set the scale
	 * @param scale
	 * @author feidan September 3, 2014 at 11:17:56 AM
	 *
	 * 设置缩放倍率
	 * @param scale
	 * @author feidan 2014年9月3日 上午11:17:56
	 */
	public void setScale(float scale) {
		if (scale < 1.0f) {
			scale = 1.0f;
		}

		if (scale > mMaxScale) {
			scale = mMaxScale;
		}

		float zoom = scale / mCurrentScale;
		setZoom(zoom, mMidX, 0);
		scaleEnd(zoom);
		invalidate();
	}

	/**
	 * Gets the current scaling ratio
	 * @author feidan September 2, 2014 at 10:58:29 am
	 *
	 * 获取当前缩放倍率
	 * @author feidan 2014年9月2日 上午10:58:29
	 */
	public float getCurScale() {
		return mCurrentScale;
	}

	/**
	 * Due to the pattern of delayed seek firing and a progress bar corresponding to N Windows,
	 *     the window number needs to be brought back to be processed when the seek ends
	 * @param index
	 * @author feidan September 15, 2014 at 3:50:05 PM
	 *
	 * 由于seek延时触发和一个进度条对应N个窗口的模式，seek结束时需要把窗口号回调出去处理
	 * @param index
	 * @author feidan 2014年9月15日 下午3:50:05
	 */
	public void setWinIndex(int index) {
		mWinIndex = index;
	}

	/**
	 * The progress bar is restored to its initial state
	 * @author feidan September 1, 2014 at 3:54:49 PM
	 *
	 * 进度条恢复初始状态
	 * @author feidan 2014年9月1日 下午3:54:49
	 */
	public void clear() {
		setScale(1);
		setProgress(MAX / 2);
		setCanTouch(true);
		mClipRects = null;
		mStartX = 0.f;
		mStartY = 0.f;
		mMidX = 0.f;
		mMidY = 0.f;
		mZoomStartdis = 0.f;
		mZoomEnddis = 0.f;
		mMode = NONE;
	}
}
