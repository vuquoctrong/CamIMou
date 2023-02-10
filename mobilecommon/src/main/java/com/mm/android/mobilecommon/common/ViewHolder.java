package com.mm.android.mobilecommon.common;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder {
    private View mContent;

    private SparseArray<View> sparseArray;

    public static ViewHolder getViewHolder(int layout, View mContent, Context mContext, ViewGroup parent) {
        ViewHolder viewHolder;
        if (mContent == null) {
            viewHolder = new ViewHolder();
            mContent = LayoutInflater.from(mContext).inflate(layout, parent, false);
            mContent.setTag(viewHolder);
            viewHolder.mContent = mContent;
            viewHolder.sparseArray = new SparseArray<>();
        } else {
            viewHolder = (ViewHolder) mContent.getTag();
        }
        return viewHolder;
    }

    public <T extends View> T findViewById(int id) {
        T view = (T) sparseArray.get(id);
        if (view == null) {
            view = mContent.findViewById(id);
            sparseArray.put(id, view);
        }
        return view;
    }

    public View getView() {
        return mContent;
    }
}
