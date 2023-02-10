package com.mm.android.deviceaddmodule.views.popwindow;

import android.app.Activity;
import android.view.View;


public class LoadingPopWindow extends BasePopWindow {
    LoadingPopWindow(View view, int width, int height) {
        super(view, width, height);
        setOutsideTouchable(false);
        setFocusable(false);
    }

    @Override
    public void drawContent(Activity activity) {
    }

    @Override
    public void updateContent(Activity activity, boolean isPort) {

    }
}
