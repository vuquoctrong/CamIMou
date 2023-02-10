package com.mm.android.mobilecommon.widget.linechart.charts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.UiThread;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.widget.linechart.adapter.IValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.animate.LAnimator;
import com.mm.android.mobilecommon.widget.linechart.data.Entry;
import com.mm.android.mobilecommon.widget.linechart.data.Line;
import com.mm.android.mobilecommon.widget.linechart.data.Lines;
import com.mm.android.mobilecommon.widget.linechart.listener.IDragListener;
import com.mm.android.mobilecommon.widget.linechart.manager.MappingManager;
import com.mm.android.mobilecommon.widget.linechart.model.Axis;
import com.mm.android.mobilecommon.widget.linechart.model.HighLight;
import com.mm.android.mobilecommon.widget.linechart.model.XAxis;
import com.mm.android.mobilecommon.widget.linechart.model.YAxis;
import com.mm.android.mobilecommon.widget.linechart.render.GodRender;
import com.mm.android.mobilecommon.widget.linechart.render.HighLightRender;
import com.mm.android.mobilecommon.widget.linechart.render.LineRender;
import com.mm.android.mobilecommon.widget.linechart.render.NoDataRender;
import com.mm.android.mobilecommon.widget.linechart.render.XAxisRender;
import com.mm.android.mobilecommon.widget.linechart.render.YAxisRender;
import com.mm.android.mobilecommon.widget.linechart.touch.GodTouchListener;
import com.mm.android.mobilecommon.widget.linechart.touch.TouchListener;
import com.mm.android.mobilecommon.widget.linechart.utils.RectD;
import com.mm.android.mobilecommon.widget.linechart.utils.SingleD_XY;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

import java.util.List;


public class LineChart extends Chart {

    MappingManager _MappingManager;

    Lines _lines;

    ////////////////////////////  listener  ///////////////////////////
    IDragListener _dragListener;

    ////////////////////////// function //////////////////////////
    boolean isHighLightEnabled = true;
    boolean isTouchEnabled = true;
    boolean isDragable = true;
    boolean isScaleable = true;
    ChartMode _ChartMode;

    ///////////////////////////////// parts ////////////////////////////////
    XAxis _XAxis;
    YAxis _YAxis;
    HighLight _HighLight;

    //////////////////////////////////  render  /////////////////////////////
    NoDataRender _NoDataRender;
    XAxisRender _XAxisRender;
    YAxisRender _YAxisRender;
    LineRender _LineRender;
    HighLightRender _HighLightRender;
    GodRender _GodRender;


    ////////////////////////////// touch  /////////////////////////////
    TouchListener _TouchListener;
    GodTouchListener _GodTouchListener;

    //////////////////////////// 区域 ///////////////////////////
    RectF _MainPlotRect;// 主要的绘图区域

    float _paddingLeft = 40;
    float _paddingRight = 5;
//    float _paddingTop = 17;
    float _paddingTop = 37;
//    float _paddingBottom = 15;
    float _paddingBottom = 25;

    RectF _GodRect;//

    //////////////////////////////  animator  ////////////////////////////
    LAnimator _LAnimator;


    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attributeSet) {
        super.init(context, attributeSet);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,  R.styleable.LineChart);
            boolean isGodMode = typedArray.getBoolean(R.styleable.LineChart_god_mode, false);
            _ChartMode = isGodMode ? ChartMode.God : ChartMode.Normal;
            typedArray.recycle();

        // animator
        _LAnimator = new LAnimator(this);

        // init v
        _MainPlotRect = new RectF();
        _MappingManager = new MappingManager(_MainPlotRect);
        _GodRect = new RectF();

        // models
        _XAxis = new XAxis();
        _YAxis = new YAxis();
        _HighLight = new HighLight();

        // render
        _NoDataRender = new NoDataRender(_MainPlotRect, _MappingManager);
        _XAxisRender = new XAxisRender(_MainPlotRect, _MappingManager, _XAxis);
        _YAxisRender = new YAxisRender(_MainPlotRect, _MappingManager, _YAxis);
        _LineRender = new LineRender(_MainPlotRect, _MappingManager, _lines, this);
        _HighLightRender = new HighLightRender(context,_MainPlotRect, _MappingManager, _lines, _HighLight);
        _GodRender = new GodRender(_MainPlotRect, _MappingManager, _GodRect);

        // touch listener
        _TouchListener = new TouchListener(this);
        _GodTouchListener = new GodTouchListener(this);

        // other
        _paddingLeft = Utils.dp2px(_paddingLeft);
        _paddingRight = Utils.dp2px(_paddingRight);
        _paddingTop = Utils.dp2px(_paddingTop);
        _paddingBottom = Utils.dp2px(_paddingBottom);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();

        if (_ChartMode == ChartMode.Normal) {
            _TouchListener.computeScroll();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (_lines == null) {
            return false;
        }
        if (!isTouchEnabled) {
            return false;
        }

        if (_ChartMode == ChartMode.Normal) {
            return _TouchListener.onTouch(this, event);
        } else if (_ChartMode == ChartMode.God) {
            return _GodTouchListener.onTouch(this, event);
        }

        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. render no data
        if (_lines == null || _lines.getLines() == null || _lines.getLines().size() == 0) {
//            _NoDataRender.render(canvas);


            _XAxis.calValues(getVisiableMinX(), getVisiableMaxX(), null);
            _YAxis.calValues(getVisiableMinY(), getVisiableMaxY(), null);

            _YAxisRender.renderGridline(canvas);

            // render labels
            _XAxisRender.renderLabels(canvas);
            _YAxisRender.renderLabels(canvas);

            // render unit
            _XAxisRender.renderUnit(canvas);
            _YAxisRender.renderUnit(canvas);


            return;
        }

        // 计算轴线上的数值
        Line line = _lines.getLines().get(0);
        _XAxis.calValues(getVisiableMinX(), getVisiableMaxX(), line);
        _YAxis.calValues(getVisiableMinY(), getVisiableMaxY(), line);


        // render grid line
//        _XAxisRender.renderGridline(canvas);
        _YAxisRender.renderGridline(canvas);

        // render line
        _LineRender.render(canvas);

        // render god
        if (_ChartMode == ChartMode.God) {
            _GodRender.render(canvas);
        }


        // render high light,放到刻度虚线后面画，防止被Gridline遮挡
        _HighLightRender.render(canvas);


        // render warn line
        _XAxisRender.renderWarnLine(canvas);
        _YAxisRender.renderWarnLine(canvas);

        // render Axis
//        _XAxisRender.renderAxisLine(canvas);
//        _YAxisRender.renderAxisLine(canvas);

        // render labels
        _XAxisRender.renderLabels(canvas);
        _YAxisRender.renderLabels(canvas);

        // render unit
        _XAxisRender.renderUnit(canvas);
        _YAxisRender.renderUnit(canvas);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        _LineRender.onChartSizeChanged(w, h);
        notifyDataChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        _lines = null;
    }

    /**
     * Notification of data changes
     *
     * 通知数据改变
     */
    public void notifyDataChanged() {

        limitMainPlotArea();

        if (_lines == null) {
            return;
        }
        _lines.calMinMax();

        prepareMap();

        // 2. onDataChanged
        _LineRender.onDataChanged(_lines);
        _HighLightRender.onDataChanged(_lines);
    }

    /**
     * Limit the boundaries of the main drawing area
     *
     * 限制 主绘图区域的边界
     */
    private void limitMainPlotArea() {

        _MainPlotRect.setEmpty();
        _MainPlotRect.right += _MainPlotRect.left + getWidth();
        _MainPlotRect.bottom += _MainPlotRect.top + getHeight();

        // 0. padding
        offsetPadding();
        // 1. 计算label,unit的宽高
        offsetArea();

        if (_ChartMode == ChartMode.God) {
            _GodRect.set(_MainPlotRect);
            _GodRect.right = _GodRect.right / 3;
        }
    }

    private void offsetPadding() {

        // 考虑图例
        if (_lines != null) {
            int h = 0;
            for (Line line : _lines.getLines()) {
                if (line.getLegendHeight() > h) {
                    h = line.getLegendHeight();
                }
            }

            if (h > 0) {
                _paddingTop = h;
            }
        }

        _MainPlotRect.left += _paddingLeft;
        _MainPlotRect.top += _paddingTop;
        _MainPlotRect.right -= _paddingRight;
        _MainPlotRect.bottom -= _paddingBottom;
    }

    private void offsetArea() {

        _YAxisRender.paint_label();
        _YAxisRender.paint_unit();
        Paint paintLabel = _YAxisRender.get_PaintLabel();
        Paint paintUnit = _YAxisRender.get_PaintUnit();

        /*******************************  取一堆label中的head middle tail 中的最大值  ***********************************/
        float labelDimen = _YAxis.getLabelDimen();

        if (_lines != null && _lines.getLines().size() > 0) {
            Line line = _lines.getLines().get(0);

            if (line.getEntries().size() > 0) {
                List<Entry> entryList = line.getEntries();

                Entry head = entryList.get(0);
                Entry tail = entryList.get(entryList.size() - 1);
                Entry middle = entryList.get(( entryList.size() - 1) / 2);

                IValueAdapter yAdapter = _YAxis.get_ValueAdapter();
                String s1 = yAdapter.value2String(head.getY());
                float w1 = Utils.textWidth(paintLabel, s1);
                if (labelDimen < w1) {
                    labelDimen = w1;
                }

                String s2 = yAdapter.value2String(middle.getY());
                float w2 = Utils.textWidth(paintLabel, s2);
                if (labelDimen < w2) {
                    labelDimen = w2;
                }

                String s3 = yAdapter.value2String(tail.getY());
                float w3 = Utils.textWidth(paintLabel, s3);
                if (labelDimen < w3) {
                    labelDimen = w3;
                }
            }

        }

        // 考虑y label和unit
        _MainPlotRect.left += _YAxis.offsetLeft(labelDimen, Utils.textHeight(paintUnit));
        // 考虑x label和unit
        _MainPlotRect.bottom -= _XAxis.offsetBottom(Utils.textHeight(paintLabel), Utils.textHeight(paintUnit));
    }


    /**
     * Preparing the Mapping
     *
     * 准备映射关系
     */
    private void prepareMap() {
        double xMin = _lines.getmXMin();
        double xMax = _lines.getmXMax();
        double yMin = _lines.getmYMin();
        double yMax = _lines.getmYMax();

        if (yMin == 0 && yMax == 0){
            yMax = 10;
            yMin = 0;
        }

        if (_lines.getLines().size() == 0){
            yMax = 10;
            yMin = 0;

            Axis.CalWay calWay = get_XAxis().getCalWay();
            if (calWay == Axis.CalWay.lc_day){
                xMin = - 23.d/30;
                xMax = 23.d*31/30;
            }else if (calWay == Axis.CalWay.lc_week){
                xMin = - 6.d/30;
                xMax = 6.d*31/30;
            }else if (calWay == Axis.CalWay.lc_month){
                xMin = - 30.d/30;
                xMax = 30.d*31/30;
            }else if (calWay == Axis.CalWay.lc_year){
                xMin = - 11.d/30;
                xMax = 11.d*31/30;
            }

        }

        _MappingManager.prepareRelation(xMin, xMax, yMin, yMax);
    }

    /**
     * Setting up the data source
     * @param lines
     *
     * 设置数据源
     * @param lines
     */
    public void setLines(Lines lines) {
        _lines = lines;

        notifyDataChanged();
        postInvalidate();
    }

    public Lines getlines() {
        return _lines;
    }

    /**
     * Clear data
     *
     * 清空数据
     */
    public void clearData() {
        _lines = null;
        invalidate();
    }


    private void A______________________________________________() {

    }

    /**
     * I'm going to animate it in the Y direction
     *
     * Y方向进行动画
     */
    @UiThread
    public void animateY() {
        _LAnimator.animateY(1000);
    }

    /**
     * I'm going to animate it in the Y direction
     *
     * Y方向进行动画
     */
    @UiThread
    public void animateY(long duration) {
        _LAnimator.animateY(duration);
    }

    /**
     * I'm going to animate in the X direction
     *
     * X方向进行动画
     */
    @UiThread
    public void animateX() {
        _LAnimator.animateX(1000);
    }

    /**
     * I'm going to animate in the X direction
     *
     * X方向进行动画
     */
    @UiThread
    public void animateX(long duration) {
        _LAnimator.animateX(duration);
    }

    /**
     * I'm going to animate in the X and Y direction
     *
     * X,Y方向进行动画
     */
    @UiThread
    public void animateXY() {
        _LAnimator.animateXY(1000);
    }

    /**
     * I'm going to animate in the X and Y direction
     *
     * X,Y方向进行动画
     */
    @UiThread
    public void animateXY(long duration) {
        _LAnimator.animateXY(duration);
    }

    public LAnimator get_LAnimator() {
        return _LAnimator;
    }

    public void set_LAnimator(LAnimator _LAnimator) {
        this._LAnimator = _LAnimator;
    }


    private void B_________________________________________________() {

    }

    private void a______________________________________________() {

    }

    public boolean isCanX_drag() {
        return _TouchListener.isCanX_drag();
    }

    /**
     * Sets whether the x direction is dragable
     * @param canX_drag
     *
     * 设置x方向是否可拖动
     * @param canX_drag
     */
    public void setCanX_drag(boolean canX_drag) {
        _TouchListener.setCanX_drag(canX_drag);
    }

    public boolean isCanY_drag() {
        return _TouchListener.isCanY_drag();
    }

    /**
     * Sets whether the y direction can be dragged
     * @param canY_drag
     *
     * 设置y方向是否可拖动
     * @param canY_drag
     */
    public void setCanY_drag(boolean canY_drag) {
        _TouchListener.setCanY_drag(canY_drag);
    }

    public boolean isZoom_alone() {
        return _TouchListener.isZoom_alone();
    }

    /**
     * Sets whether the x and y directions can be scaled independently
     * @param zoom_independent
     *
     * 设置x,y方向是否可以独立缩放
     * @param zoom_independent
     */
    public void setZoom_alone(boolean zoom_independent) {
        _TouchListener.setZoom_alone(zoom_independent);
    }

    public boolean isCanX_zoom() {
        return _TouchListener.isCanX_zoom();
    }

    /**
     * Sets whether the x direction can be scaled
     * @param canX_zoom
     *
     * 设置x方向是否可以缩放
     * @param canX_zoom
     */
    public void setCanX_zoom(boolean canX_zoom) {
        _TouchListener.setCanX_zoom(canX_zoom);
    }

    public boolean isCanY_zoom() {
        return _TouchListener.isCanY_zoom();
    }

    /**
     * Sets whether the y direction can be scaled
     * @param canY_zoom
     *
     * 设置y方向是否可以缩放
     * @param canY_zoom
     */
    public void setCanY_zoom(boolean canY_zoom) {
        _TouchListener.setCanY_zoom(canY_zoom);
    }

    public boolean isDragable() {
        return isDragable;
    }

    public void setDragable(boolean dragable) {
        isDragable = dragable;
    }

    public boolean isScaleable() {
        return isScaleable;
    }

    public void setScaleable(boolean scaleable) {
        isScaleable = scaleable;
    }

    public IDragListener get_dragListener() {
        return _dragListener;
    }

    public void set_dragListener(IDragListener _dragListener) {
        this._dragListener = _dragListener;
    }

    private void b______________________________________________() {

    }

    public float get_paddingLeft() {
        return _paddingLeft;
    }

    public void set_paddingLeft(float _paddingLeft) {
        this._paddingLeft = _paddingLeft;
    }

    public float get_paddingRight() {
        return _paddingRight;
    }

    public void set_paddingRight(float _paddingRight) {
        this._paddingRight = _paddingRight;
    }

    public float get_paddingTop() {
        return _paddingTop;
    }

    public void set_paddingTop(float _paddingTop) {
        this._paddingTop = _paddingTop;
    }

    public float get_paddingBottom() {
        return _paddingBottom;
    }

    public void set_paddingBottom(float _paddingBottom) {
        this._paddingBottom = _paddingBottom;
    }

    private void c______________________________________________() {

    }

    public double getVisiableMinX() {
        float px = _MainPlotRect.left;
        SingleD_XY xy = _MappingManager.getValueByPx(px, 0);
        return xy.getX();
    }

    public double getVisiableMaxX() {
        float px = _MainPlotRect.right;
        SingleD_XY xy = _MappingManager.getValueByPx(px, 0);
        return xy.getX();
    }

    public double getVisiableMinY() {
        float py = _MainPlotRect.bottom;
        SingleD_XY xy = _MappingManager.getValueByPx(0, py);
        return xy.getY();
    }

    public double getVisiableMaxY() {
        float py = _MainPlotRect.top;
        SingleD_XY xy = _MappingManager.getValueByPx(0, py);
        return xy.getY();
    }

    public RectD get_currentViewPort() {
        return _MappingManager.get_currentViewPort();
    }

    public void set_currentViewPort(RectD currentViewPort) {
        _MappingManager.set_currentViewPort(currentViewPort);
    }

    /**
     * Set the Y-axis range
     * @param yMin Y the minimum
     * @param yMax Y the maximum
     *
     * 设置y轴的范围
     * @param yMin y最小值
     * @param yMax y最大值
     */
    public void setYMax_Min(double yMin, double yMax) {
        if (_lines != null) {
            _lines.setYCustomMaxMin(true);
            _lines.setmYMin(yMin);
            _lines.setmYMax(yMax);
            postInvalidate();
        } else {
            LogUtil.errorLog("setAxisMaxMin:", " 请在lines设置后，调用此方法！");
        }
    }

    /**
     * Sets the scope of the axis
     * @param xMin X minimum
     * @param xMax Y maximum
     *
     * 设置轴线的范围
     * @param xMin x最小值
     * @param xMax x最大值
     */
    public void setXAix_MaxMin(double xMin, double xMax) {
        if (_lines != null) {
            _lines.setXCustomMaxMin(true);
            _lines.setmXMin(xMin);
            _lines.setmXMax(xMax);
            postInvalidate();
        } else {
            LogUtil.errorLog("setAxisMaxMin:"," 请在lines设置后，调用此方法！");
        }
    }

    private void d______________________________________________() {

    }

    /**
     * Getting the mapping manager
     *
     * 获取映射管家
     */
    public MappingManager get_MappingManager() {
        return _MappingManager;
    }


    ////////////////////////////////  便捷的方法  //////////////////////////////////

    public void highLight_PixXY(float px, float py) {
        SingleD_XY xy = _MappingManager.getValueByPx(px, py);
        highLight_ValueXY(xy.getX(), xy.getY());
    }

    public void highLight_ValueXY(double x, double y) {
        _HighLightRender.highLight_ValueXY(x, y);
        invalidate();
    }

    public void highLightLeft() {
        _HighLightRender.highLightLeft();
        invalidate();
    }

    public void highLightRight() {
        _HighLightRender.highLightRight();
        invalidate();
    }

    public HighLight get_HighLight() {
        return _HighLight;
    }

    public void set_HighLight(HighLight _HighLight) {
        this._HighLight = _HighLight;
    }

    public XAxis get_XAxis() {
        return _XAxis;
    }

    public YAxis get_YAxis() {
        return _YAxis;
    }

    public RectF get_GodRect() {
        return _GodRect;
    }

    public void set_GodRect(RectF _GodRect) {
        this._GodRect = _GodRect;
    }

    public RectF get_MainPlotRect() {
        return _MainPlotRect;
    }

    public void set_MainPlotRect(RectF _MainPlotRect) {
        this._MainPlotRect = _MainPlotRect;
    }

    public ChartMode get_ChartMode() {
        return _ChartMode;
    }

    public LineChart set_ChartMode(ChartMode _ChartMode) {
        this._ChartMode = _ChartMode;
        return this;
    }

    LineChart _ob_linechart;

    public void registObserver(LineChart lineChartOb) {
        this._ob_linechart = lineChartOb;

        notifyDataChanged_FromOb(lineChartOb);
    }

    public void notifyDataChanged_FromOb(LineChart lineChartOb) {

        // x,y轴上的单位
        XAxis xAxis = lineChartOb.get_XAxis();
        this.get_XAxis().set_unit(xAxis.get_unit());

        YAxis yAxis = lineChartOb.get_YAxis();
        this.get_YAxis().set_unit(yAxis.get_unit());

        this.setLines(lineChartOb.getlines());
    }

    public void notifyOB_ViewportChanged(RectD _currentViewPort) {
        _ob_linechart.set_CurrentViewPort(_currentViewPort);
        _ob_linechart.invalidate();
    }

    public LineChart set_CurrentViewPort(RectD _currentViewPort) {
        _MappingManager.set_currentViewPort(_currentViewPort);
        return this;
    }


    public enum ChartMode {
        Normal, God
    }


}
