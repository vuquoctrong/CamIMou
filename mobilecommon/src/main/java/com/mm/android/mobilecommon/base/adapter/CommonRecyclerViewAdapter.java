package com.mm.android.mobilecommon.base.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewHolder> {
    protected RecyclerView mRecyclerView;
    protected List<T> mList;      //数据源集合
    protected Context mContext;
    protected int mLayout;
    protected RecyclerViewItemClickListener mRecylerViewItemClickListener;

    public CommonRecyclerViewAdapter(RecyclerView recyclerView, int layout) {
        mRecyclerView = recyclerView;
        mContext = mRecyclerView.getContext();
        mLayout = layout;
        mList = new ArrayList<>();
    }

    /**
     * Setting up a new data source
     * @param data  Data source
     *
     * 设置全新数据源
     * @param data  数据源
     */
    public void setData(List<T> data) {
        if (data != null && !data.isEmpty()) {
            mList = data;
        } else {//传入的数据为空，则清空数据
            mList.clear();
        }
        notifyDataSetChanged();
    }

    public void addData(List<T> list) {
        if (list != null) {
            this.mList.addAll(list);
        }
    }

    public void clearData() {
        this.mList.clear();
    }

    public void replaceData(List<T> list2) {
        if (list2 != mList) {
            clearData();
            addData(list2);
        }
        notifyDataSetChanged();
    }

    public void setRecylerViewItemClickListener(RecyclerViewItemClickListener listener) {
        mRecylerViewItemClickListener = listener;
    }

    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(viewType, parent, false);
        CommonRecyclerViewHolder viewHolder = new CommonRecyclerViewHolder(mRecyclerView, itemView, mRecylerViewItemClickListener);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (mLayout == 0) {
            throw new RuntimeException("no layout id");
        }
        return mLayout;
    }

    @Override
    public void onBindViewHolder(CommonRecyclerViewHolder holder, int position) {
        convert(holder, getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    /**
     * Padding Data
     *
     * 填充数据
     */
    public abstract void convert(CommonRecyclerViewHolder viewHolder, T item, int pos);

    public interface RecyclerViewItemClickListener {
        void onRecylerViewItemClick(ViewGroup parent, View itemView, int position);
    }

    public T getItem(int pos) {
        return mList.get(pos);
    }

    /**
     * Getting the data source
     * @return List  Data list
     *
     * 获取数据源
     * @return List  数据集合
     */
    public List<T> getData() {
        return mList;
    }

}
