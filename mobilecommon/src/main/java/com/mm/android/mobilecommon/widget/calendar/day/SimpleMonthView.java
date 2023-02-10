package com.mm.android.mobilecommon.widget.calendar.day;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.mobilecommon.widget.calendar.CalendarUtils;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SimpleMonthView extends View
{
    public static final String COLOR_NORMAL_DAY = "#ff999999";
    public static final String COLOR_ENABLE_DAY = "#ff999999";
    public static final String COLOR_SELECTED_BG_THIS = "#f18d00";
    public static final String COLOR_SELECTED_BG_LAST = "#7396f7";
    public static final String COLOR_SELECTED_BG1 = "#f76163";
    public static final String COLOR_SELECTED_BG2 = "#2e9b95";
    public static final String COLOR_SELECTED_BG3 = "#4ea7f2";

    public static final String TYPE_FACE_SANS_SERIF = "Arial Regular";

    public static final int TEXT_SIZE_DAY = 16;
    public static final int TEXT_SIZE_MONTH = 16;
    public static final int TEXT_SIZE_NAME = 10;
    public static final int HEIGHT_HEADER_MONTH = 50;
    public static final int DIMEN_SELECTED_DAY_RADIUS = 16;
    public static final int HEIGHT_CALENDAR = 270;


    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    private static final int SELECTED_CIRCLE_ALPHA = 128;
    private static int DEFAULT_HEIGHT = 32;
    private static final int DEFAULT_NUM_ROWS = 6;
    private static int DAY_SELECTED_CIRCLE_SIZE;
    private static int DAY_SEPARATOR_WIDTH = 1;
    private static int MINI_DAY_NUMBER_TEXT_SIZE;
    private static int MIN_HEIGHT = 10;
    private static int MONTH_DAY_LABEL_TEXT_SIZE;
    private static int MONTH_HEADER_SIZE;
    private static int MONTH_LABEL_TEXT_SIZE;

    private int mPadding = 0;

    private String mDayOfWeekTypeface;
    private String mMonthTitleTypeface;

    private Paint mMonthDayLabelPaint;
    private Paint mMonthNumPaint;
    private Paint mMonthTitlePaint;
    private Paint mSelectedCirclePaint;
    private Paint mLinePaint;
    private int mEnableDayTextColor;
    private int mMonthTextColor;
    private int mDayTextColor;
    private int mDayNumColor;

    private int mSelectedColorThis;
    private int mSelectedColorLast;


    private final StringBuilder mStringBuilder;

    private int mWeekStart = Calendar.SUNDAY;// 一周的第一天
    private int mNumDays = 7;
    private int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;// 一周中的第几天
    private int mMonth;
    private Boolean mDrawRect;
    private Boolean mIsModeOfDay;
    private int mRowHeight = DEFAULT_HEIGHT;
    private int mWidth;
    private int mYear;

    // 可以选择的最小时间
    private Calendar minCalendar;
    // 当前时间
    private Calendar currentCalendar;
    // 月标题使用的Calendar
	private final Calendar mTitleCalendar;

    private int mNumRows = DEFAULT_NUM_ROWS;

	private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();

    private OnDayClickListener mOnDayClickListener;

    private Map<String, CalendarDay> selectedDayList;
    private Map<String, SelectedDays> selectedWeekList;
    private Set<Integer> selectedColors;

    public SimpleMonthView(Context context, TypedArray typedArray)
    {
        super(context);

        mSelectedColorThis = typedArray.getColor(R.styleable.DayPickerView_colorSelectedBgThis, Color.parseColor(COLOR_SELECTED_BG_THIS));
        mSelectedColorLast = typedArray.getColor(R.styleable.DayPickerView_colorSelectedBgLast, Color.parseColor(COLOR_SELECTED_BG_LAST));


        mDrawRect = typedArray.getBoolean(R.styleable.DayPickerView_drawRoundRect, false);
        mTitleCalendar = Calendar.getInstance();

        mDayOfWeekTypeface = TYPE_FACE_SANS_SERIF;
        mMonthTitleTypeface = TYPE_FACE_SANS_SERIF;

        mEnableDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorEnableDay, Color.parseColor(COLOR_ENABLE_DAY));

        mMonthTextColor = typedArray.getColor(R.styleable.DayPickerView_colorMonthName, Color.parseColor(COLOR_NORMAL_DAY));
        mDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorDayName, Color.parseColor(COLOR_NORMAL_DAY));
        mDayNumColor = typedArray.getColor(R.styleable.DayPickerView_colorNormalDay, Color.parseColor(COLOR_NORMAL_DAY));

        mStringBuilder = new StringBuilder(50);

        MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDay, UIUtils.sp2px(context, TEXT_SIZE_DAY));
        MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeMonth, UIUtils.sp2px(context, TEXT_SIZE_MONTH));
        MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDayName, UIUtils.sp2px(context, TEXT_SIZE_NAME));
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight, UIUtils.dp2px(context, HEIGHT_HEADER_MONTH));
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius, UIUtils.dp2px(context, DIMEN_SELECTED_DAY_RADIUS));

        mRowHeight = ((typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight, UIUtils.dp2px(context, HEIGHT_CALENDAR)) - MONTH_HEADER_SIZE) / 6);

        initView();

    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
	}

	private void drawMonthDayLabels(Canvas canvas) {
        int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2) - 10;
        int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);
        Calendar dayLabelCalendar = Calendar.getInstance();

        for (int i = 0; i < mNumDays; i++) {
            int calendarDay = (i + mWeekStart) % mNumDays;
            int x = (2 * i + 1) * dayWidthHalf + mPadding;
            dayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
            canvas.drawText(mDateFormatSymbols.getShortWeekdays()[dayLabelCalendar.get(Calendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault()), x, y, mMonthDayLabelPaint);
        }

        canvas.drawLine(0, y - MONTH_DAY_LABEL_TEXT_SIZE - (MONTH_HEADER_SIZE - y), mWidth ,
                y - MONTH_DAY_LABEL_TEXT_SIZE - (MONTH_HEADER_SIZE - y), mLinePaint);
        canvas.drawLine(0, MONTH_HEADER_SIZE, mWidth , MONTH_HEADER_SIZE, mLinePaint);
	}

	private void drawMonthTitle(Canvas canvas) {
        int x = (mWidth + 2 * mPadding) / 2;
        int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE - 20) / 2 /*+ (MONTH_LABEL_TEXT_SIZE / 3)*/;
        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        canvas.drawText(stringBuilder.toString(), x, y, mMonthTitlePaint);
	}

	private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
	}

	private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mTitleCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
    }

	private void onDayClick(CalendarDay calendarDay) {
		if (mOnDayClickListener == null) {
            return;
        }

		if(mIsModeOfDay) {
            dayClick(calendarDay);
        } else {
            weekClick(calendarDay);
        }
	}

	private void dayClick(CalendarDay calendarDay){
        if (selectedDayList.containsKey(CalendarUtils.getKeyByCalendarDay(calendarDay))) {
            CalendarDay c = selectedDayList.get(CalendarUtils.getKeyByCalendarDay(calendarDay));
            if (calendarDay.equals(c)) {
                if (!calendarDay.isYesterday() && !calendarDay.isToday()) {
                    calendarDay.setColor(c.getColor());
                    selectedColors.add(calendarDay.getColor());
                }
            }
        } else {
            if (!calendarDay.isYesterday() && !calendarDay.isToday()) {
                Iterator<Integer> it = selectedColors.iterator();
                if (it.hasNext()) {
                    calendarDay.setColor(it.next());
                    it.remove();
                }
            }
        }
        mOnDayClickListener.onDayClick(this, calendarDay);
    }

	private void weekClick(CalendarDay calendarDay) {
        SelectedDays selectedDays ;
        if (selectedWeekList.containsKey(CalendarUtils.getKeyByCalendarDay(CalendarUtils.getFixedMonday(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay())))) {
            selectedDays = selectedWeekList.get(CalendarUtils.getKeyByCalendarDay(CalendarUtils.getFixedMonday(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay())));
            if (!selectedDays.isThisWeek() && !selectedDays.isLastWeek()) {
                selectedColors.add(selectedDays.getFirst().getColor());
            }
        } else {
            selectedDays = new SelectedDays(calendarDay);
            if(!selectedDays.isThisWeek() && !selectedDays.isLastWeek()) {
                Iterator<Integer> it = selectedColors.iterator();
                if (it.hasNext()) {
                    selectedDays.getFirst().setColor(it.next());
                    selectedDays.getLast().setColor(selectedDays.getFirst().getColor());
                    it.remove();
                }
            }
        }
        mOnDayClickListener.onDayClick(this, selectedDays);
    }

	protected void drawMonthNums(Canvas canvas) {
		int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        int lineY = mRowHeight + MONTH_HEADER_SIZE - DAY_SEPARATOR_WIDTH;
		int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
		int dayOffset = findDayOffset();
		int day = 1;

		while (day <= mNumCells) {
			int x = paddingDay * (1 + dayOffset * 2) + mPadding;

            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, day);

            if(mIsModeOfDay) {
                if (selectedDayList.containsKey(CalendarUtils.getKeyByYearMonthDay(mYear, mMonth, day))) {
                    setModeOfDaySelectedBgColor(day);
                    drawSelectedBg(canvas, x, y);
                }
            } else {
                if (selectedWeekList.containsKey(CalendarUtils.getKeyByFixedMonday(mYear, mMonth, day))
                        && CalendarUtils.isInTime(calendar.getTime(), minCalendar.getTime(), currentCalendar.getTime(), "yyyy-MM-dd")) {
                    setModeOfWeekSelectedBgColor(day);
                    drawSelectedBg(canvas, x, y);
                }
            }


            if ((selectedDayList.containsKey(CalendarUtils.getKeyByYearMonthDay(mYear, mMonth, day))
                    || selectedWeekList.containsKey(CalendarUtils.getKeyByFixedMonday(mYear, mMonth, day)))
                    && CalendarUtils.isInTime(calendar.getTime(), minCalendar.getTime(), currentCalendar.getTime(), "yyyy-MM-dd")){
                //选中的白色
                mMonthNumPaint.setColor(Color.WHITE);
            } else if(CalendarUtils.isInTime(calendar.getTime(), minCalendar.getTime(), currentCalendar.getTime(), "yyyy-MM-dd")) {
                //范围内的正常色
                mMonthNumPaint.setColor(mDayNumColor);
            } else {
                // 超过范围的日期置灰
                mMonthNumPaint.setColor(mEnableDayTextColor);
            }

			canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

			dayOffset++;
			if (dayOffset == mNumDays) {
                if (day != mNumCells){
                    //每月最后一天刚好是周六的不绘制线
                    canvas.drawLine(0, lineY, mWidth , lineY, mLinePaint);
                }
				dayOffset = 0;
				y += mRowHeight;
                lineY += mRowHeight;
			}
			day++;
		}
	}

    /**
     * Sets the background color of the selected number in daily mode
     *
     * 设置日模式下选中数字的背景颜色
     */
	private void setModeOfDaySelectedBgColor(int day) {
        CalendarDay c = selectedDayList.get(CalendarUtils.getKeyByYearMonthDay(mYear, mMonth, day));
        if (c.isToday()) {
            c.setColor(mSelectedColorThis);
        } else if (c.isYesterday()) {
            c.setColor(mSelectedColorLast);
        }
        mSelectedCirclePaint.setColor(c.getColor());
    }

    /**
     * Select the digital background color in Set Week mode
     *
     * 设置周模式下选中数字背景颜色
     */
	private void setModeOfWeekSelectedBgColor(int day) {
        SelectedDays s = selectedWeekList.get(CalendarUtils.getKeyByFixedMonday(mYear, mMonth, day));
        if (s.isThisWeek()) {
            s.getFirst().setColor(mSelectedColorThis);
            s.getLast().setColor(mSelectedColorThis);
        } else if (s.isLastWeek()) {
            s.getFirst().setColor(mSelectedColorLast);
            s.getLast().setColor(mSelectedColorLast);
        }
        mSelectedCirclePaint.setColor(s.getFirst().getColor());
    }

    /**
     * Draw a digital background
     *
     * 画数字背景
     */
	private void drawSelectedBg(Canvas canvas, int x, int y) {
        if (mDrawRect) {
            RectF rectF = new RectF(x - DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE, x + DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE);
            canvas.drawRoundRect(rectF, 10.0f, 10.0f, mSelectedCirclePaint);
        } else {
            canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaint);
        }
    }

	public CalendarDay getDayFromLocation(float x, float y) {
		int padding = mPadding;
		if ((x < padding) || (x > mWidth - mPadding)) {
			return null;
		}

		int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
		int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1)
            return null;

		return new CalendarDay(mYear, mMonth, day);
	}

	protected void initView() {
        mMonthTitlePaint = new Paint();
//        mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setTypeface(Typeface.create(mMonthTitleTypeface, Typeface.NORMAL));
        mMonthTitlePaint.setColor(mMonthTextColor);
        mMonthTitlePaint.setTextAlign(Align.CENTER);
        mMonthTitlePaint.setStyle(Style.FILL);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setTextAlign(Align.CENTER);
        mSelectedCirclePaint.setStyle(Style.FILL);
        mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mDayTextColor);
        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setStyle(Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Align.CENTER);
//        mMonthDayLabelPaint.setFakeBoldText(true);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTextAlign(Align.CENTER);
        mMonthNumPaint.setFakeBoldText(false);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Style.FILL);
        mLinePaint.setStrokeWidth(UIUtils.dip2px(getContext(), 0.5f));
        mLinePaint.setColor(Color.parseColor("#eeeeee"));
	}

	protected void onDraw(Canvas canvas) {
		drawMonthTitle(canvas);
		drawMonthDayLabels(canvas);
		drawMonthNums(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows + MONTH_HEADER_SIZE);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
	}

	public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay());
                // 超过范围不能点击
                if(!calendar.before(minCalendar) && !calendar.after(currentCalendar)) {

                    onDayClick(calendarDay);
                }
            }
        }
        return true;
	}

	public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
		requestLayout();
	}

	public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
		setTag(params);

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

		mTitleCalendar.set(Calendar.MONTH, mMonth);
		mTitleCalendar.set(Calendar.YEAR, mYear);
		mTitleCalendar.set(Calendar.DAY_OF_MONTH, 1);// 一个月中的第几天
		mDayOfWeekStart = mTitleCalendar.get(Calendar.DAY_OF_WEEK);// 一周中的第几天

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mTitleCalendar.getFirstDayOfWeek();// 一周的第一天
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
        mNumRows = calculateNumRows();
	}

	public void setSelectedDayList(Map<String, CalendarDay> selectedDayList) {
        this.selectedDayList = selectedDayList;
    }

    public void setSelectedWeekList(Map<String, SelectedDays> selectedWeekList) {
        this.selectedWeekList = selectedWeekList;
    }

    public void setSelectedColors(Set<Integer> selectedColors) {
        this.selectedColors = selectedColors;
    }

    public void setModeOfDay(boolean isModeOfDay) {
        this.mIsModeOfDay = isModeOfDay;
    }

    public void setCurrentCalendar(Calendar calendar) {
	    this.currentCalendar = calendar;
    }

    public void setMinCalendar(Calendar calendar) {
        this.minCalendar = calendar;
    }

	public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
		mOnDayClickListener = onDayClickListener;
	}

	public static abstract interface OnDayClickListener {
		void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay);
        void onDayClick(SimpleMonthView simpleMonthView, SelectedDays selectedDays);
	}
}