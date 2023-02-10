package com.mm.android.mobilecommon.base.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class CommonRecyclerViewHolder extends RecyclerView.ViewHolder {
    RecyclerView mRecylerView;
    private SparseArray<View> mViewArray;       //控件集合

    View mConvertView;

    public CommonRecyclerViewHolder(RecyclerView recyclerView, final View itemView, final CommonRecyclerViewAdapter.RecyclerViewItemClickListener listener) {
        super(itemView);
        mRecylerView = recyclerView;
        mConvertView = itemView;
        mViewArray = new SparseArray<>();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == itemView.getId() && listener != null) {
                    listener.onRecylerViewItemClick(mRecylerView, itemView, getAdapterPosition());
                }
            }
        });
    }

    public <T extends View> T findViewById(int id) {
        View view = mViewArray.get(id);
        if (view == null) {
            view = mConvertView.findViewById(id);
            mViewArray.put(id, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }
}
