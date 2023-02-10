package com.mm.android.mobilecommon.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.mobilecommon.widget.calendar.CalendarUtils;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MonthSelectView extends View {
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
    public static final int DIMEN_SELECTED_DAY_RADIUS = 22;
    public static final int HEIGHT_CALENDAR = 270;


    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_YEAR = "year";

    private static final int SELECTED_CIRCLE_ALPHA = 128;
    private static int DEFAULT_HEIGHT = 32;
    private static final int DEFAULT_NUM_ROWS = 4;
    private static int DAY_SELECTED_CIRCLE_SIZE;
    private static int DAY_SEPARATOR_WIDTH = 1;
    private static int MINI_DAY_NUMBER_TEXT_SIZE;
    private static int MIN_HEIGHT = 10;
    private static int MONTH_HEADER_SIZE;
    private static int MONTH_LABEL_TEXT_SIZE;

    private int mPadding = 0;

    private String mDayOfWeekTypeface;
    private String mMonthTitleTypeface;

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

    private int mNumDays = 3;
    private int mNumCells = mNumDays;
    private Boolean mDrawRect;
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

    private OnDayClickListener mOnDayClickListener;

    private Map<String, SelectedMonth> selectedMonthList;
    private Set<Integer> selectedColors;

    public MonthSelectView(Context context, TypedArray typedArray) {
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
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight, UIUtils.dp2px(context, HEIGHT_HEADER_MONTH));
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius, UIUtils.dp2px(context, DIMEN_SELECTED_DAY_RADIUS));

        mRowHeight = ((typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight, UIUtils.dp2px(context, HEIGHT_CALENDAR)) - MONTH_HEADER_SIZE) / 6);

        initView();

    }

    private void drawMonthTitle(Canvas canvas) {
        int x = (mWidth + 2 * mPadding) / 2;
        int y = MONTH_HEADER_SIZE / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
        canvas.drawText(getYearString(), x, y, mMonthTitlePaint);
        canvas.drawLine(0, MONTH_HEADER_SIZE, mWidth , MONTH_HEADER_SIZE, mLinePaint);
    }


    private String getYearString() {
        int year = mTitleCalendar.get(Calendar.YEAR);
        return getContext().getResources().getString(R.string.device_manager_report_month_select_year, year);
    }

    private void onDayClick(SelectedMonth selectedMonth) {
        if (mOnDayClickListener == null) {
            return;
        }

        dayClick(selectedMonth);

    }

    private void dayClick(SelectedMonth selectedMonth) {
        if (selectedMonthList.containsKey(CalendarUtils.getKeyBySelectedMonth(selectedMonth))) {
            SelectedMonth c = selectedMonthList.get(CalendarUtils.getKeyBySelectedMonth(selectedMonth));
            if (selectedMonth.equals(c)) {
                if (!selectedMonth.isLastMonth() && !selectedMonth.isThisMonth()) {
                    selectedMonth.setColor(c.getColor());
                    selectedColors.add(selectedMonth.getColor());
                }
            }
        } else {
            if (!selectedMonth.isLastMonth() && !selectedMonth.isThisMonth()) {
                Iterator<Integer> it = selectedColors.iterator();
                if (it.hasNext()) {
                    selectedMonth.setColor(it.next());
                    it.remove();
                }
            }
        }
        mOnDayClickListener.onDayClick(this, selectedMonth);
    }


    protected void drawMonthNums(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        int lineY = mRowHeight + MONTH_HEADER_SIZE - DAY_SEPARATOR_WIDTH;
        int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
        int dayOffset = 0;
        int month = 0;

        //取12月的文字长度（这里偷懒不再遍历最长的文字，直接取12月份的）
        float textWidth  = mMonthNumPaint.measureText(getContext().getResources().getString(R.string.device_manager_report_month_select_month, 12)) - 10;

        while (month < mNumCells) {
            int x = paddingDay * (1 + dayOffset * 2) + mPadding;

            if (selectedMonthList.containsKey(CalendarUtils.getKeyByYearMonth(mYear, month))) {
                setModeOfDaySelectedBgColor(month);
                drawSelectedBg(canvas, x, y, textWidth);
            }


            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, month, 1);

            if (selectedMonthList.containsKey(CalendarUtils.getKeyByYearMonth(mYear, month))){
                //选中的白色
                mMonthNumPaint.setColor(Color.WHITE);
            } else if (CalendarUtils.isInTime(calendar.getTime(), minCalendar.getTime(), currentCalendar.getTime(), "yyyy-MM")) {
                //范围内的正常色
                mMonthNumPaint.setColor(mDayNumColor);
            } else {
                // 超过范围的日期置灰
                mMonthNumPaint.setColor(mEnableDayTextColor);
            }

            canvas.drawText(getContext().getResources().getString(R.string.device_manager_report_month_select_month, month + 1), x, y, mMonthNumPaint);

            dayOffset++;
            if (dayOffset == mNumDays) {
                canvas.drawLine(0, lineY, mWidth , lineY, mLinePaint);
                dayOffset = 0;
                y +=  mRowHeight;
                lineY += mRowHeight;
            }
            month++;
        }
    }

    /**
     * Sets the background color of the selected number
     *
     * 设置选中数字的背景颜色
     */
    private void setModeOfDaySelectedBgColor(int month) {
        SelectedMonth c = selectedMonthList.get(CalendarUtils.getKeyByYearMonth(mYear, month));
        if (c.isThisMonth()) {
            c.setColor(mSelectedColorThis);
        } else if (c.isLastMonth()) {
            c.setColor(mSelectedColorLast);
        }
        mSelectedCirclePaint.setColor(c.getColor());
    }


    /**
     * Draw a digital background
     *
     * 画数字背景
     */
    private void drawSelectedBg(Canvas canvas, int x, int y, float textWidth) {
        if (mDrawRect) {
            RectF rectF = new RectF(x - DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE, x + DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE);
            canvas.drawRoundRect(rectF, 10.0f, 10.0f, mSelectedCirclePaint);
        } else {
            int centerX = x;    //文字中心X
            int centerY = y - MINI_DAY_NUMBER_TEXT_SIZE / 3;    //文字中心Y
            int offset = 5;     //由于设置了抗锯齿，防止部分手机上出现拼接出现间隙。重叠绘制一小部分

            RectF center = new RectF(centerX - textWidth/2, centerY - DAY_SELECTED_CIRCLE_SIZE,
                    centerX + textWidth/2, centerY + DAY_SELECTED_CIRCLE_SIZE);
            canvas.drawRect(center, mSelectedCirclePaint);

            //左半边圆弧
            RectF ovalLeft = new RectF(centerX + offset - textWidth/2 - DAY_SELECTED_CIRCLE_SIZE, centerY - DAY_SELECTED_CIRCLE_SIZE,
                    centerX + offset - textWidth/2 + DAY_SELECTED_CIRCLE_SIZE, centerY + DAY_SELECTED_CIRCLE_SIZE);
            canvas.drawArc(ovalLeft, 90, 180, true, mSelectedCirclePaint);

            //右半边圆弧
            RectF ovalRight = new RectF(centerX - offset + textWidth/2 - DAY_SELECTED_CIRCLE_SIZE , centerY - DAY_SELECTED_CIRCLE_SIZE,
                    centerX - offset + textWidth/2 + DAY_SELECTED_CIRCLE_SIZE, centerY + DAY_SELECTED_CIRCLE_SIZE);
            canvas.drawArc(ovalRight, -90, 180, true, mSelectedCirclePaint);
        }
    }

    public SelectedMonth getDayFromLocation(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }

        int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
        int month = ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding))) + yDay * mNumDays;

        return new SelectedMonth(mYear, month);
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
            SelectedMonth selectedMonth = getDayFromLocation(event.getX(), event.getY());
            if (selectedMonth != null) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(selectedMonth.getYear(), selectedMonth.getMonth(), 1);
                // 超过范围不能点击
                if (!calendar.before(minCalendar) && !calendar.after(currentCalendar)) {

                    onDayClick(selectedMonth);
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
        if (!params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify year for this view");
        }
        setTag(params);

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }

        mYear = params.get(VIEW_PARAMS_YEAR);

        mTitleCalendar.set(Calendar.YEAR, mYear);

        mNumCells = 12;
        mNumRows = 4;
    }

    public void setSelectedMonthList(Map<String, SelectedMonth> selectedMonthList) {
        this.selectedMonthList = selectedMonthList;
    }


    public void setSelectedColors(Set<Integer> selectedColors) {
        this.selectedColors = selectedColors;
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
        void onDayClick(MonthSelectView simpleMonthView, SelectedMonth selectedMonth);
    }
}