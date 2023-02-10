package com.lechange.demo.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.common.openapi.entity.DeviceDetailListData;
import com.common.openapi.entity.DeviceLocalCacheData;
import com.lc.media.utils.MediaPlayHelper;
import com.lechange.demo.R;
import com.lechange.demo.tools.GridItemDecoration;
import com.lechange.demo.tools.RoundedCornersTransform;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.utils.TimeUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceListHolder> {

    public Context mContext;
    private List<DeviceDetailListData.ResponseData.DeviceListBean> datas;
    private RecyclerView.ItemDecoration decoration;
    private RoundedCornersTransform transform;
    private RequestOptions options;
    private int listOffset = 0;

    public DeviceListAdapter(Context mContext, List<DeviceDetailListData.ResponseData.DeviceListBean> datas) {
        this.mContext = mContext;
        this.datas = datas;
        transform = new RoundedCornersTransform(mContext, mContext.getResources().getDimensionPixelOffset(R.dimen.px_26));
        transform.setNeedCorner(false, false, true, true);
        options = new RequestOptions().placeholder(R.color.c10).transform(transform);
        listOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.px_10);
        decoration = new GridItemDecoration(listOffset, true);
    }

    @NonNull
    @Override
    public DeviceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == -1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_device_list, parent, false);
        }
        return new DeviceListHolder(mContext, view,viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (datas == null || datas.size() == 0) {
            return -1;
        }
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceListHolder holder, final int position) {
        if (datas == null ||datas.size()==0) {
            return;
        }
        holder.tvName.setText(datas.get(position).deviceName);
        holder.ivDetail.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onSettingClick(position);
            }
        });
        holder.rlOffline.setVisibility(View.VISIBLE);
        if (datas.get(position).channelList != null && datas.get(position).channelList.size() > 1) {
            //多通道
            holder.ivDetail.setVisibility(View.VISIBLE);
            holder.rlDetail.setVisibility(View.GONE);
            holder.to_msg.setVisibility(View.GONE);
            holder.rcvChannel.setVisibility(View.VISIBLE);
            holder.rlOffline.setVisibility(View.GONE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
            gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            holder.rcvChannel.setLayoutManager(gridLayoutManager);
            holder.ivDetail.post(() -> {
                ChannelListAdapter channelListAdapter = new ChannelListAdapter(mContext, datas.get(position).channelList, datas.get(position).deviceId);
                channelListAdapter.setParentViewWight(holder.itemView.getMeasuredWidth());
                holder.rcvChannel.setAdapter(channelListAdapter);
                holder.rcvChannel.removeItemDecoration(decoration);
                holder.rcvChannel.addItemDecoration(decoration);
                channelListAdapter.setOnItemClickListener(new ChannelListAdapter.OnChannelClickListener() {
                    @Override
                    public void onChannelClick(int channelPosition) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onChannelClick(position, channelPosition);
                        }
                    }

                    @Override
                    public void onMessageClick(int channelPosition) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onMessageClick(position, channelPosition);
                        }
                    }
                });
            });
        } else if (datas.get(position).channelList != null && datas.get(position).channelList.size() == 0) {
            //多通道NVR,但是没有通道数
            holder.ivDetail.setVisibility(View.VISIBLE);
            holder.ivPlay.setVisibility(View.GONE);
            holder.to_msg.setVisibility(View.GONE);
            holder.rcvChannel.setVisibility(View.GONE);
            holder.rlDetail.setVisibility(View.VISIBLE);
            holder.ivBg.setImageResource(R.mipmap.lc_demo_default_bg);

            holder.rlOffline.setVisibility(View.VISIBLE);
            holder.rlOffline.setBackground(mContext.getDrawable(R.color.transparent));
            holder.tvOffline.setText(R.string.lc_demo_device_nvr_no_channel);
            holder.rlDetail.setOnClickListener(null);
        } else {
            //单通道
            holder.to_msg.setVisibility(View.VISIBLE);
            holder.ivDetail.setVisibility(View.VISIBLE);
            holder.rlDetail.setVisibility(View.VISIBLE);
            holder.rcvChannel.setVisibility(View.GONE);
            holder.to_msg.setOnClickListener((view) -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onMessageClick(position, datas.get(position).checkedChannel);
                }
            });
            if ("online".equals(datas.get(position).getDeviceStatus()) && datas.get(position).channelList != null && datas.get(position).channelList.size() > 0) {
                holder.ivPlay.setVisibility(View.VISIBLE);
                holder.rlOffline.setVisibility(View.GONE);
                holder.rlOffline.setBackground(mContext.getDrawable(R.color.transparent));
                holder.rlDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onDetailClick(position);
                        }
                    }
                });
            } else {
                holder.ivPlay.setVisibility(View.GONE);
                holder.rlOffline.setVisibility(View.VISIBLE);
                holder.rlOffline.setBackground(mContext.getDrawable(R.drawable.lc_demo_soild_20r_c66000000_shape));
                holder.tvOffline.setText(R.string.lc_demo_main_offline);
                if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) { // 海外   显示0时区时间
                    holder.offlineTime.setText(TimeUtils.changeTimeFormat2StandardMinByDateFormat(datas.get(position).lastOffLineTime, "MM-dd HH:mm:ss"));
                } else {
                    holder.offlineTime.setText(TimeUtils.changeTimeFormat2StandardNoYear(datas.get(position).lastOffLineTime)); //国内
                }
                holder.rlDetail.setOnClickListener(null);
            }
            //获取设备缓存信息
            DeviceLocalCacheData deviceLocalCacheData = new DeviceLocalCacheData();
            deviceLocalCacheData.setDeviceId(datas.get(position).deviceId);
            if (datas.get(position).channelList != null && datas.get(position).channelList.size() > 0) {
                deviceLocalCacheData.setChannelId(datas.get(position).channelList.get(datas.get(position).checkedChannel).channelId);
            }

            String captureFilePath = null;
            String channelName = null;
            if (datas.get(position).channelList != null && datas.get(position).channelList.size() > 0) {
                channelName = datas.get(position).deviceId + "&" + datas.get(position).channelList.get(datas.get(position).checkedChannel).channelId;
            } else {
                channelName = datas.get(position).deviceId;
            }
            // 去除通道中在目录中的非法字符
            channelName = channelName.replace("-", "");
            captureFilePath =
                    com.lc.media.utils.MediaPlayHelper.getDevicesListImageCachePath(MediaPlayHelper.LCFilesType.LCImageCache, channelName);
            if (TextUtils.isEmpty(captureFilePath)) {
                holder.ivBg.setImageResource(R.mipmap.lc_demo_default_bg);
            } else {
                holder.ivBg.setImageURI(Uri.fromFile(new File(captureFilePath)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return (datas == null || datas.size() == 0) ? 1 : datas.size();
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {

        void onMessageClick(int outPosition, int innerPosition);

        void onSettingClick(int position);

        void onDetailClick(int position);

        void onChannelClick(int outPosition, int innerPosition);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class DeviceListHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivDetail;
        ImageView ivBg;
        RelativeLayout rlOffline;
        TextView tvOffline;
        TextView offlineTime;
        ImageView ivPlay;
        RelativeLayout rlDetail;
        RecyclerView rcvChannel;
        FrameLayout frDetail;
        ImageView to_msg;
        int viewType;

        public DeviceListHolder(Context context, View itemView,int viewType) {
            super(itemView);
            this.viewType=viewType;
            if(viewType==-1)return;
            tvName = itemView.findViewById(R.id.tv_name);
            ivDetail = itemView.findViewById(R.id.iv_detail);
            ivBg = itemView.findViewById(R.id.iv_bg);
            rlDetail = itemView.findViewById(R.id.rl_detail);
            frDetail = itemView.findViewById(R.id.fr_detail);
            rlOffline = itemView.findViewById(R.id.rl_offline);
            tvOffline = itemView.findViewById(R.id.tv_offline);
            ivPlay = itemView.findViewById(R.id.iv_play);
            rcvChannel = itemView.findViewById(R.id.rcv_channel);
            offlineTime = itemView.findViewById(R.id.tv_offline_time);
            to_msg = itemView.findViewById(R.id.to_msg);
            ivBg.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) ivBg.getLayoutParams();
                mLayoutParams.height = mLayoutParams.width * 9 / 16;
                mLayoutParams.width = frDetail.getWidth(); // 屏幕宽度（像素）
                mLayoutParams.setMargins(0, 0, 0, 0);
                ivBg.setLayoutParams(mLayoutParams);
            });
            rlOffline.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) rlOffline.getLayoutParams();
                mLayoutParams.height = ivBg.getHeight();
                mLayoutParams.width = ivBg.getWidth();
                mLayoutParams.setMargins(0, 0, 0, 0);
                rlOffline.setLayoutParams(mLayoutParams);
            });
        }
    }
}
