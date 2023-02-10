package com.mm.android.mobilecommon.widget.linechart;

import com.mm.android.mobilecommon.widget.linechart.adapter.DayHighLightValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.adapter.DefaultValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.adapter.MonthHighLightValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.adapter.ReportValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.adapter.WeekHighLightValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.adapter.YearHighLightValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.charts.LineChart;
import com.mm.android.mobilecommon.widget.linechart.data.Entry;
import com.mm.android.mobilecommon.widget.linechart.data.Line;
import com.mm.android.mobilecommon.widget.linechart.data.Lines;
import com.mm.android.mobilecommon.widget.linechart.model.Axis;
import com.mm.android.mobilecommon.widget.linechart.model.HighLight;
import com.mm.android.mobilecommon.widget.linechart.model.XAxis;
import com.mm.android.mobilecommon.widget.linechart.model.YAxis;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {
    private static final int dayXCount = 24;
    private static final int weekXCount = 7;
    private static final int monthXCount = 31;
    private static final int yearXCount = 12;

    private LineChart _lineChart;
    private int linesNumber=0;

    private String duration;

    private List<LineEntity> lineEntities;

    public ChartHelper(LineChart _lineChart) {
        this._lineChart = _lineChart;
    }

    public void displayChart() {

        _lineChart.setCanX_drag(true);// x 方向可以拖拽
        _lineChart.setCanY_drag(false);// y 方向不可拖拽
        _lineChart.setCanX_zoom(true);// x 方向可以缩放
        _lineChart.setCanY_zoom(false);// y 方向不可用缩放
        _lineChart.setZoom_alone(false);// 设置在 x，y方向上是否要独立缩放

        setAxis(_lineChart);
        setNumberData();
        setHighLight(_lineChart);
    }

    private void setHighLight(LineChart lineChart) {
        // 高亮
        HighLight highLight = lineChart.get_HighLight();
        highLight.setEnable(!(linesNumber > 1));// 启用高亮显示  默认为启用状态
        highLight.setDrawHint(false);
        switch (duration) {
            case "day":
                highLight.setxValueAdapter(new DayHighLightValueAdapter());
                break;
            case "week":
                highLight.setxValueAdapter(new WeekHighLightValueAdapter());
            break;
            case "month":
                highLight.setxValueAdapter(new MonthHighLightValueAdapter());
                break;
            case "year":
                highLight.setxValueAdapter(new YearHighLightValueAdapter());
                break;
        }
    }

    private void setAxis(LineChart lineChart) {
        duration = getDuration();

        if (duration == null) {
            return;
        }

//        // x,y轴上的单位
        XAxis xAxis = lineChart.get_XAxis();
        xAxis.set_labelCountAdvice(4);
        xAxis.set_ValueAdapter(new DefaultValueAdapter(0));
        xAxis.setLabelTextSize(Utils.sp2px(12));

        YAxis yAxis = lineChart.get_YAxis();
        yAxis.set_labelCountAdvice(6);
        yAxis.setCalWay(Axis.CalWay.perfect);
        yAxis.set_ValueAdapter(new ReportValueAdapter());
        yAxis.setLabelTextSize(Utils.sp2px(12));


        switch (duration) {
            case "day":
                xAxis.setCalWay(Axis.CalWay.lc_day);
                break;
            case "week":
                xAxis.setCalWay(Axis.CalWay.lc_week);
                break;
            case "month":
                xAxis.setCalWay(Axis.CalWay.lc_month);
                break;
            case "year":
                xAxis.setCalWay(Axis.CalWay.lc_year);
                break;
        }

        _lineChart.set_paddingLeft(0);
        _lineChart.set_paddingBottom(0);
    }

    private void setNumberData() {
        linesNumber = 0;
        Lines lines = new Lines();
        lines.setXCustomMaxMin(false);

        for(int i = 0; i < lineEntities.size(); i++) {
            LineEntity lineEntity = lineEntities.get(i);

            List<Entry> listEntriesNow = new ArrayList<>();
            if (lineEntity.isVisible()) {
                int count = dayXCount;
                switch (duration) {
                    case "day":
                        count = dayXCount;
                        break;
                    case "week":
                        count = weekXCount;
                        break;
                    case "month":
                        count = monthXCount;
                        break;
                    case "year":
                        count = yearXCount;
                        break;
                }

                long[] data = lineEntity.getData();
                for(int j = 0; j < count; j++) {
                    if (data.length > j) {
                        listEntriesNow.add(new Entry(j, data[j]));
                    }
                }

                Line line = new Line();
                line.setLineColor(lineEntity.getColor());
                line.setEntries(listEntriesNow);
                lines.addLine(line);
                linesNumber++;
                setHighLight(_lineChart);
            }
        }

        _lineChart.setLines(lines);

    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setLineEntities(List<LineEntity> lineEntities) {
        this.lineEntities = lineEntities;
    }
}
