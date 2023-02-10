package com.mm.android.mobilecommon.widget.linechart.render;

import android.graphics.RectF;

import com.mm.android.mobilecommon.widget.linechart.manager.MappingManager;


public abstract class BaseRender {
    RectF _rectMain;
    MappingManager _MappingManager;

    public BaseRender(RectF _rectMain, MappingManager _MappingManager) {
        this._rectMain = _rectMain;
        this._MappingManager = _MappingManager;
    }
}
