package com.lc.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.media.R;
import com.lc.media.entity.DeviceDetailListData;
import com.lc.media.ui.LcPopupWindow;

import java.util.List;


public class PixelListAdapter extends RecyclerView.Adapter<PixelListAdapter.PixelHolder> implements View.OnClickListener {


    private Context  mContext;
    private List<com.lc.media.entity.DeviceDetailListData.ResponseData.DeviceListBean.ChannelsBean.ResolutionBean> mPixelList;
    private LcPopupWindow.onRecyclerViewItemClickListener mItemClickListener;
    private RecyclerView mRecycleView;


    public PixelListAdapter(Context context, List<DeviceDetailListData.ResponseData.DeviceListBean.ChannelsBean.ResolutionBean> pixelList) {
        this.mContext = context;
        this.mPixelList = pixelList;
    }


    public void setOnItemClickListener(LcPopupWindow.onRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.mItemClickListener = onRecyclerViewItemClickListener;
    }


    @NonNull
    @Override
    public PixelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mRecycleView = (RecyclerView) parent;
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_pixel_list, parent, false);
        inflate.setOnClickListener(this);
        return new PixelHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull PixelHolder holder, int position) {
        holder.tvPixel.setText(mPixelList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return mPixelList!=null?mPixelList.size():0;
    }

    @Override
    public void onClick(View view) {
        int childAdapterPosition = mRecycleView.getChildAdapterPosition(view);
        if(mItemClickListener!=null){
            mItemClickListener.onItemClick(mRecycleView,view,childAdapterPosition,
                    mPixelList.get(childAdapterPosition).name,mPixelList.get(childAdapterPosition).imageSize,mPixelList.get(childAdapterPosition).streamType);
        }
    }

    class PixelHolder extends  RecyclerView.ViewHolder{

        public TextView tvPixel;

        public PixelHolder(View itemView) {
            super(itemView);
            tvPixel = itemView.findViewById(R.id.tv_pixel);
        }
    }

}
