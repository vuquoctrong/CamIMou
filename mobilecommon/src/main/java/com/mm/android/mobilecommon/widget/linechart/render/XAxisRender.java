package com.mm.android.mobilecommon.widget.linechart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.mm.android.mobilecommon.widget.linechart.adapter.IValueAdapter;
import com.mm.android.mobilecommon.widget.linechart.manager.MappingManager;
import com.mm.android.mobilecommon.widget.linechart.model.Axis;
import com.mm.android.mobilecommon.widget.linechart.model.WarnLine;
import com.mm.android.mobilecommon.widget.linechart.utils.SingleF_XY;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

import java.util.List;

public class XAxisRender extends AxisRender {

    public XAxisRender(RectF _FrameManager, MappingManager _MappingManager, Axis axis) {
        super(_FrameManager, _MappingManager, axis);
    }

    @Override
    public void renderAxisLine(Canvas canvas) {
        super.renderAxisLine(canvas);

        float startX = _rectMain.left;
        float startY = _rectMain.bottom;
        float stopX = _rectMain.right;
        float stopY = _rectMain.bottom;

        canvas.drawLine(startX, startY, stopX, stopY, _PaintAxis);
    }


    @Override
    public void renderGridline(Canvas canvas) {
        super.renderGridline(canvas);

        canvas.save();
        canvas.clipRect(_rectMain);// 限制绘制区域

        double[] values = _Axis.getLabelValues();
        int labelCount = _Axis.getLabelCount();
        float x = 0;

        float top = _rectMain.top;
        float bottom = _rectMain.bottom;

        _PathGrid.reset();

        for (int i = 0; i < labelCount; i++) {
            if (values.length < (i + 1)) {
                break;
            }

            double value = values[i];

            SingleF_XY xy = _MappingManager.getPxByValue(value, 0);
            x = xy.getX();

            _PathGrid.moveTo(x, bottom);
            _PathGrid.lineTo(x, top);
        }

        // grid line
        canvas.drawPath(_PathGrid, _PaintGridline);

        canvas.restore();
    }

    @Override
    public void renderLabels(Canvas canvas) {
        super.renderLabels(canvas);

        String duration = _Axis.getDuration();
        double[] values = _Axis.getLabelValues();
        int labelCount = _Axis.getLabelCount();

        IValueAdapter adapter = _Axis.get_ValueAdapter();
        float indicator = _Axis.getLeg();

        float x = 0;

        float bottom = _rectMain.bottom;

        for (int i = 0; i < labelCount; i++) {
            if (values.length < (i + 1)) {
                break;
            }

            double value = values[i];
            String label = "";
//                    = adapter.value2String(value);

            if (duration .equals( "DAY")) {
                if (value == 0) {
                    label = "01:00";
                } else if (value == 4) {
                    label = "05:00";
                } else if (value == 8) {
                    label = "09:00";
                }  else if (value == 12) {
                    label = "13:00";
                }  else if (value == 16) {
                    label = "17:00";
                }  else if (value == 20) {
                    label = "21:00";
                }  else if (value == 23) {
                    label = "24:00";
                }
                else {
                    label = adapter.value2String(value);
                }

            } else if (duration .equals( "WEEK")) {
                if (value == 0) {
                    label = "周一";
                } else if (value == 3) {
                    label = "周四";
                } else if (value == 6) {
                    label = "周日";
                } else {
                    label = adapter.value2String(value);
                }

            } else if (duration .equals( "MONTH")) {
                if (value == 0) {
                    label = "01";
                } else if (value == 5) {
                    label = "06";
                } else if (value == 10) {
                    label = "11";
                }else if (value == 15) {
                    label = "16";
                }else if (value == 20) {
                    label = "21";
                }else if (value == 25) {
                    label = "26";
                }else if (value == 30) {
                    label = "31";
                } else {
                    label = adapter.value2String(value);
                }
            } else if (duration .equals( "YEAR")) {
                if (value == 0) {
                    label = "";
                } else if(value == 1) {
                    label = "二月";
                } else if (value == 5) {
                    label = "六月";
                } else if (value == 9) {
                    label = "十月";
                } else {
                    label = adapter.value2String(value);
                }
            }




            SingleF_XY xy = _MappingManager.getPxByValue(value, 0);
            x = xy.getX();

            // check
            if (x < _rectMain.left || x > _rectMain.right) {
                continue;
            }

            if (label == null) {
                continue;
            }

            // indicator
            canvas.drawLine(x, bottom, x, bottom + indicator, _PaintLittle);

            // label
            float labelX = x - Utils.textWidth(_PaintLabel, label) / 2;
            float labelY = bottom + _Axis.getLeg() + _Axis.getLabelDimen() + 5;
            canvas.drawText(label, labelX, labelY, _PaintLabel);
        }
    }


    @Override
    public void renderUnit(Canvas canvas) {
        super.renderUnit(canvas);

        String unit = _Axis.get_unit();
        Paint paintUnit = _PaintUnit;

        float bottom = _rectMain.bottom;
        float labelX = _rectMain.centerX() - Utils.textWidth(paintUnit, unit) / 2;
        float labelY = bottom + _Axis.getLeg() + _Axis.getLabelDimen() + _Axis.getUnitDimen();

        canvas.drawText(unit, labelX, labelY, _PaintUnit);
    }


    @Override
    public void renderWarnLine(Canvas canvas) {
        super.renderWarnLine(canvas);

        List<WarnLine> warnLines = _Axis.getListWarnLins();
        if (warnLines == null) {
            return;
        }

        canvas.save();
        canvas.clipRect(_rectMain);

        for (WarnLine warnLine : warnLines) {
            if (warnLine.isEnable()) {
                double value = warnLine.getValue();

                SingleF_XY xy = _MappingManager.getPxByValue(value, 0);
                float x = xy.getX();

                if (x < _rectMain.left || x > _rectMain.right) {
                    continue;
                }

                _PaintWarnText.setColor(warnLine.getWarnColor());
                _PaintWarnText.setStrokeWidth(warnLine.getWarnLineWidth());
                _PaintWarnText.setTextSize(warnLine.getTxtSize());

                _PaintWarnPath.setColor(warnLine.getWarnColor());
                _PaintWarnPath.setStrokeWidth(warnLine.getWarnLineWidth());

                _PathWarn.reset();
                _PathWarn.moveTo(x, _rectMain.bottom);
                _PathWarn.lineTo(x, _rectMain.top);

                canvas.drawPath(_PathWarn, _PaintWarnPath);

                float txtHeight = Utils.textHeight(_PaintWarnText);
                float txtWidth = Utils.textWidth(_PaintWarnText, "" + value);

                canvas.drawText(value + "", x - txtWidth * 1.5f, _rectMain.bottom - txtHeight * 1.5f, _PaintWarnText);
            }
        }

        canvas.restore();
    }

}
