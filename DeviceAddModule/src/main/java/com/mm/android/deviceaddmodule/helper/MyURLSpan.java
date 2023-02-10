package com.mm.android.deviceaddmodule.helper;

import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Custom underline and click event text related function class
 *
 * 自定义加下划线及点击事件的文字相关功能类
 */
class MyURLSpan extends URLSpan {
    private OnClickListener mListener;

    public MyURLSpan(String url) {
        super(url);
    }

    public void setOnClickListener(OnClickListener listenr) {
        // TODO Auto-generated method stub
        mListener = listenr;
    }

    @Override
    public void onClick(View widget) {
        if (mListener != null) {
            mListener.onClick(widget);
        }
    }
}
