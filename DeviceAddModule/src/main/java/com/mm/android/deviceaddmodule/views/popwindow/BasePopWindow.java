package com.mm.android.deviceaddmodule.views.popwindow;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Popup box base class
 *
 * 弹出框基类
 */
public abstract class BasePopWindow extends PopupWindow {
    BasePopWindow(View view, int width, int height){
        super(view,width,height);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * 绘制弹出框内容  Draw the content of the pop-up box
     * @param activity  The Activity object
     *
     * 绘制弹出框内容
     * @param activity  Activity对象
     */
    public abstract void drawContent(Activity activity);

    public void drawContent(Activity activity,boolean isPort){

    }


    /**
     * Update Pop-up Box
     * @param activity  The Activity object
     *
     * 更新弹出框
     * @param activity  Activity对象
     */
    public abstract void updateContent(Activity activity,boolean isPort);
}
