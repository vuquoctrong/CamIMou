package com.mm.android.mobilecommon.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mm.android.mobilecommon.common.ViewHolder;

import java.util.List;

/**
 * The base class for the adapter
 *
 * 适配器的基类
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> list;

    protected int layout;

    protected Context mContext;

    private int firstPosition;// 从头部插入的位置

    public CommonAdapter(int layout, List<T> list, Context mContext) {
        this.list = list;
        this.layout = layout;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public T getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public void addFirstData(List<T> list) {
        this.list.addAll(firstPosition, list);
        firstPosition = firstPosition + list.size();
    }

    public void addData(List<T> list) {
        if (list != null) {
            this.list.addAll(list);
        }
    }

    public void removeData(List<T> list) {
        this.list.removeAll(list);
    }

    public void clearData() {
        this.list.clear();
        firstPosition = 0;
    }

    public List<T> getData() {
        return list;
    }

    public void replaceData(List<T> list2) {
        if (list2 != list) {
            clearData();
            addData(list2);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(layout, convertView, mContext, parent);
        convert(viewHolder, getItem(position), position, parent);
        return viewHolder.getView();
    }

    public abstract void convert(ViewHolder viewHolder, T item, int position, ViewGroup parent);

}
