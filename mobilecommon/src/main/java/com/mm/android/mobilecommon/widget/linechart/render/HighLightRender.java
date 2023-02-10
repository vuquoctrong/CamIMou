package com.mm.android.mobilecommon.widget.linechart.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.mm.android.mobilecommon.widget.linechart.data.Entry;
import com.mm.android.mobilecommon.widget.linechart.data.Line;
import com.mm.android.mobilecommon.widget.linechart.data.Lines;
import com.mm.android.mobilecommon.widget.linechart.manager.MappingManager;
import com.mm.android.mobilecommon.widget.linechart.model.HighLight;
import com.mm.android.mobilecommon.widget.linechart.utils.SingleF_XY;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

public class HighLightRender extends BaseRender {

    private final static double UN_CHOOSE = Double.MIN_VALUE;

    Lines _lines;
    HighLight _highLight;

    Paint paintHighLight;
    Paint paintCircle;
    Paint paintHint;
    Context context;

    private enum DIRECTION{TOP, LEFT, BOTTOM, RIGHT}

    boolean TAG = false;
    public HighLightRender(Context context, RectF rectMain, MappingManager _MappingManager, Lines lines, HighLight highLight) {
        super(rectMain, _MappingManager);

        this._lines = lines;
        this._highLight = highLight;
        this.context = context;

        paintHighLight = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintHint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    double highValueX = UN_CHOOSE;
    double hightValueY = UN_CHOOSE;

    int record_LineIndex = -1;// 记录哪条线
    int record_EntryIndex = -1;// 记录哪条entry
    boolean isClick = false;

    public void highLight_ValueXY(double x, double y) {
        highValueX = x;
        hightValueY = y;
        isClick = true;
    }

    public void highLightLeft() {
        record_EntryIndex--;
    }

    public void highLightRight() {
        record_EntryIndex++;
    }


    public void render(Canvas canvas) {

        if (!_highLight.isEnable()) {
            return;
        }

        if (_lines == null) {
            return;
        }

        if (_lines.getLines().size() == 0) {
            return;
        }

        if (highValueX == UN_CHOOSE || hightValueY == UN_CHOOSE) {
            return;
        }

//        if (TAG) {
//            canvas.save();
//            canvas.drawText("", 0, 0, new Paint());
//            canvas.restore();
//            TAG = false;
//        } else {
            canvas.save();
//            canvas.clipRect(_rectMain); // 限制绘制区域

        RectF rectF = new RectF(_rectMain);
        rectF.top = _rectMain.top - 100;
        rectF.bottom = _rectMain.bottom + 100;
        canvas.clipRect(rectF); // 限制绘制区域

            drawHighLight_Hint(canvas);

            canvas.restore();

//        }

    }


    private void drawHighLight_Hint(Canvas canvas) {

        double closeX = Double.MAX_VALUE;
        double closeY = Double.MAX_VALUE;
        Entry hitEntry = null;


        if (isClick) {
            // 点击光标进来的
            int tempIndexEntry = -1;
            int tempLineIndex = -1;

            for (int i = 0; i < _lines.getLines().size(); i++) {
                Line line = _lines.getLines().get(i);

                if (!line.isEnable()) {
                    continue;
                }
                int indexEntry = Line.getEntryIndex(line.getEntries(), highValueX, Line.Rounding.CLOSEST);

                if (indexEntry < 0 || indexEntry >= line.getEntries().size()) {
                    continue;
                }

                Entry entry = line.getEntries().get(indexEntry);

                // 到点击的距离
                double dx = Math.abs(entry.getX() - highValueX);
                double dy = Math.abs(entry.getY() - hightValueY);

                // 先考虑 x
                if (dx <= closeX) {
                    closeX = dx;
                    // 再考虑 y
                    if (dy <= closeY) {
                        closeY = dy;
                        hitEntry = entry;
                        tempIndexEntry = indexEntry;
                        tempLineIndex = i;
                    }
                }
            }

            if (record_EntryIndex == tempIndexEntry && record_LineIndex == tempLineIndex){
                //点击的是同一个点，重置掉
                record_LineIndex = -1;
                record_EntryIndex = -1;
                isClick = false;
                return;
            }else {
                record_EntryIndex = tempIndexEntry;
                record_LineIndex = tempLineIndex;
            }

        } else {
            // 左右移动进来的
            try {
                Line line = _lines.getLines().get(record_LineIndex);
                if (line.isEnable()) {
                    hitEntry = line.getEntries().get(record_EntryIndex);
                }
            } catch (Exception e) {
                hitEntry = null;
            }
        }


        if (hitEntry == null) {
            return;
        }

        // 考虑断掉的点
        if (hitEntry.isNull_Y()) {
            return;
        }

        highValueX = hitEntry.getX();// real indexX

        SingleF_XY xy = _MappingManager.getPxByEntry(hitEntry);


        // draw high line
        if (_highLight.isDrawHighLine()) {
            //获取当前line的颜色，作为highLine的颜色
            int lineSize = _lines.getLines().size();
            Line line = lineSize > 0 && record_LineIndex < lineSize ? _lines.getLines().get(record_LineIndex) : null;

            int highLineColor = _highLight.getHighLightColor();
            if (highLineColor == 0 && line != null)
            {
                highLineColor = line.getLineColor();
            }

            //DRAW ROUND RING PIC
                //mid white ring
            float hightLightwidth = _highLight.getHighLightWidth();
            paintCircle.setStrokeWidth( (int) Utils.dp2px(0.8f) );  //the radius of inner ball is 3dp. white ring width is 2dp
            paintCircle.setColor(Color.WHITE);
            paintCircle.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(xy.getX(), xy.getY(), (int)Utils.dp2px(2+0.8f), paintCircle);

                //outside color ring
            paintCircle.setStrokeWidth((int)Utils.dp2px(1)); //color ring width is 2dp
            paintCircle.setColor(highLineColor);
            paintCircle.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(xy.getX(), xy.getY(), (int)Utils.dp2px(2+0.8f+1), paintCircle);


            //DRAW HIGH VERTICAL LINE
            paintHighLight.setStrokeWidth(_highLight.getHighLightWidth());

            //气泡颜色暂时默认都设置成灰色，隐藏横竖定位线
//            paintHighLight.setColor(highLineColor);

//            canvas.drawLine(_rectMain.left, xy.getY(), _rectMain.right, xy.getY(), paintHighLight);  //绘制highLight横线
//            canvas.drawLine(xy.getX(), _rectMain.top, xy.getX(), _rectMain.bottom, paintHighLight);


            //DRAW LC HINT


            //绘制气泡

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(Utils.sp2px(14));
            paint.setAntiAlias(true);
            String xStr = _highLight.getxValueAdapter().value2String(hitEntry.getX()+1);  //文字内容
            String yStr = _highLight.getyValueAdapter().value2String(hitEntry.getY()) + "人";

            //文字长度
            float textLen = Math.max(paint.measureText(xStr), paint.measureText(yStr));
            Paint.FontMetrics fm = paint.getFontMetrics();
            float textHeight1 = Utils.textHeight(paint);
            paint.setTextSize(Utils.sp2px(12));
            float textHeight2 = Utils.textHeight(paint);

            float triangleWidth = Utils.dp2px(8);      //三角宽高
            float trianglePadding = 0;    //三角距离数据原点的间距
            float rectWidth = Math.min(Utils.dp2px(100), textLen + Utils.dp2px(5)*2);        //矩形宽度
            float rectHeight = Utils.dp2px(4) * 3 + textHeight1 + textHeight2;        //矩形高度


            //判断气泡画在哪个方向，必须先判断Y轴方向
            DIRECTION direction = DIRECTION.LEFT;
            if (xy.getY() - rectHeight/2 < 0){
                direction = DIRECTION.BOTTOM;
            }else if (xy.getY() + rectHeight/2 > _rectMain.bottom){
                direction = DIRECTION.TOP;
            }else if (xy.getX() - triangleWidth - trianglePadding - rectWidth > _rectMain.left){
                direction = DIRECTION.LEFT;
            }else {
                direction = DIRECTION.RIGHT;
            }

            //画三角
            Path path = new Path();
            float triangleX;    //三角指向圆圈的角的X
            float triangleY;    //三角指向圆圈的角的Y
            float rectRight = 0;    //矩形右边
            float rectLeft = 0;     //矩形左边
            float rectTop = 0;      //矩形上边
            float rectBottom = 0;   //矩形下边
            float text1X = 0;
            float text1Y = 0;
            float text2X = 0;
            float text2Y = 0;

            switch(direction){
                case TOP :
                    //三角
                    triangleX = xy.getX();
                    triangleY = xy.getY() - triangleWidth - trianglePadding;
                    path.moveTo(triangleX, triangleY);
                    path.lineTo(triangleX - triangleWidth/2, triangleY - triangleWidth);
                    path.lineTo(triangleX + triangleWidth/2, triangleY - triangleWidth);
                    path.close();

                    //矩形
                    rectLeft = triangleX - rectWidth/2;
                    rectRight = triangleX + rectWidth/2;
                    rectTop = triangleY - triangleWidth - rectHeight;
                    rectBottom = triangleY - triangleWidth;

                    //文字
                    text1X = rectLeft + Utils.dp2px(5);
                    text1Y = rectTop + rectHeight/2 - 14;
                    text2X = rectLeft + Utils.dp2px(5);
                    text2Y = rectTop + rectHeight/2 - 6;

            	break;
                case BOTTOM :
                    triangleX = xy.getX();
                    triangleY = xy.getY() + triangleWidth + trianglePadding;
                    path.moveTo(triangleX, triangleY);
                    path.lineTo(triangleX + triangleWidth/2, triangleY + triangleWidth);
                    path.lineTo(triangleX - triangleWidth/2, triangleY + triangleWidth);
                    path.close();

                    //矩形
                    rectLeft = triangleX - rectWidth/2;
                    rectRight = triangleX + rectWidth/2;
                    rectTop = triangleY + triangleWidth;
                    rectBottom = triangleY + triangleWidth + rectHeight;

                    //文字
                    text1X = rectLeft + Utils.dp2px(5);
                    text1Y = rectTop + rectHeight/2 - 14;
                    text2X = rectLeft + Utils.dp2px(5);
                    text2Y = rectTop + rectHeight/2 - 6;
                    break;
                case LEFT :
                    triangleX = xy.getX() - triangleWidth - trianglePadding;
                    triangleY = xy.getY();
                    path.moveTo(triangleX, triangleY);
                    path.lineTo(triangleX - triangleWidth, triangleY + triangleWidth/2);
                    path.lineTo(triangleX - triangleWidth, triangleY - triangleWidth/2);
                    path.close();

                    //矩形
                    rectLeft = triangleX - triangleWidth - rectWidth;
                    rectRight = triangleX - triangleWidth;
                    rectTop = triangleY - rectHeight/2;
                    rectBottom = triangleY + rectHeight/2;

                    //文字
                    text1X = rectLeft + Utils.dp2px(5);
                    text1Y = triangleY - 14;
                    text2X = rectLeft + Utils.dp2px(5);
                    text2Y = triangleY - 6;
                    break;
                case RIGHT :
                    triangleX = xy.getX() + triangleWidth + trianglePadding;
                    triangleY = xy.getY();
                    path.moveTo(triangleX, triangleY);
                    path.lineTo(triangleX + triangleWidth, triangleY + triangleWidth/2);
                    path.lineTo(triangleX + triangleWidth, triangleY - triangleWidth/2);
                    path.close();

                    //矩形
                    rectLeft = triangleX + triangleWidth;
                    rectRight = triangleX + triangleWidth + rectWidth;
                    rectTop = triangleY - rectHeight/2;
                    rectBottom = triangleY + rectHeight/2;

                    //文字
                    text1X = rectLeft + Utils.dp2px(5);
                    text1Y = triangleY - 14;
                    text2X = rectLeft + Utils.dp2px(5);
                    text2Y = triangleY - 6;
                    break;
            	default:
            	break;
            }


            //气泡全部设置成灰色，外发光
            paintHighLight.setARGB(229,73,73,73);
//            paintHighLight.setShadowLayer(20, 2, 2, Color.argb(229,98,98,98));
//            paintHighLight.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));

            canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paintHighLight);
            canvas.drawPath(path, paintHighLight);

            canvas.drawText(xStr, text1X, text1Y, paint);
            canvas.drawText(yStr, text2X, text2Y + textHeight1, paint);
//            paintHighLight.clearShadowLayer();
//            paintHighLight.setMaskFilter(null);
        }




        // draw hint
        if (_highLight.isDrawHint()) {
            paintHint.setColor(_highLight.getHintColor());
            paintHint.setTextSize(_highLight.getHintTextSize());

            String xStr = _highLight.getxValueAdapter().value2String(hitEntry.getX());
            String yStr = _highLight.getyValueAdapter().value2String(hitEntry.getY());
            float txtHeight = Utils.textHeight(paintHint);
            float txtWidth = Math.max(Utils.textWidth(paintHint, xStr), Utils.textWidth(paintHint, yStr));

            float x = _rectMain.right - txtWidth - 10;
            float y = _rectMain.top + Utils.dp2px(20);

            canvas.drawText(xStr, x, y, paintHint);
            canvas.drawText(yStr, x, y + txtHeight, paintHint);
        }

        // callback
        if (isClick) {
            isClick = false;

            Line line = _lines.getLines().get(record_LineIndex);
            hitEntry = line.getEntries().get(record_EntryIndex);

            Line.CallBack_OnEntryClick cb = line.getOnEntryClick();
            if (cb != null) {
                cb.onEntry(line, hitEntry);
            }
        }

    }

    public void onDataChanged(Lines lines) {
        _lines = lines;
    }
}
