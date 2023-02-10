package com.lechange.demo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.common.openapi.entity.DeviceDetailListData;
import com.common.openapi.entity.DeviceLocalCacheData;
import com.lc.media.utils.MediaPlayHelper;
import com.lechange.demo.R;
import com.lechange.demo.tools.RoundedCornersTransform;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.utils.TimeUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.google.android.material.card.MaterialCardView;



public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ChannelHolder> {
    private final String deviceId;
    private Context mContext;
    private List<DeviceDetailListData.ResponseData.DeviceListBean.ChannelsBean> datas;
    private int parentViewWight = 0;
    private RoundedCornersTransform transform;
    private RequestOptions options;
    private int offset = 0;

    public ChannelListAdapter(Context mContext, List<DeviceDetailListData.ResponseData.DeviceListBean.ChannelsBean> datas, String deviceId) {
        this.mContext = mContext;
        this.datas = datas;
        this.deviceId = deviceId;
        transform = new RoundedCornersTransform(mContext, mContext.getResources().getDimensionPixelOffset(R.dimen.px_26));
        transform.setNeedCorner(false, false, true, true);
        options = new RequestOptions().placeholder(R.color.c10).transform(transform);
        offset = mContext.getResources().getDimensionPixelOffset(R.dimen.px_20);
    }

    public void setParentViewWight(int parentViewWight) {
        if (parentViewWight > 0) {
            this.parentViewWight = parentViewWight;
        }
    }

    @NonNull
    @Override
    public ChannelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_channel_list, parent, false);
        return new ChannelListAdapter.ChannelHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull final ChannelHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.rlDetail.getLayoutParams().width = parentViewWight / 2 - offset;
        if ("online".equals(datas.get(position).channelStatus)) {
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.rlOffline.setVisibility(View.GONE);
        } else {
            holder.ivPlay.setVisibility(View.GONE);
            holder.rlOffline.setVisibility(View.VISIBLE);
            if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) { // 海外   显示0时区时间
                holder.offlineTime.setText(TimeUtils.changeTimeFormat2StandardMinByDateFormat(datas.get(position).lastOffLineTime,"MM-dd HH:mm:ss"));
            }else
                holder.offlineTime.setText(TimeUtils.changeTimeFormat2StandardNoYear(datas.get(position).lastOffLineTime)); //国内
        }
        holder.tvName.setText(datas.get(position).channelName);
        //获取设备缓存信息
        DeviceLocalCacheData deviceLocalCacheData = new DeviceLocalCacheData();
        deviceLocalCacheData.setDeviceId(datas.get(position).deviceId);
        deviceLocalCacheData.setChannelId(datas.get(position).channelId);

        String captureFilePath = null;
        String channelName = null;
        channelName =deviceId + "&" + datas.get(position).channelId;
        // 去除通道中在目录中的非法字符
        channelName = channelName.replace("-", "");
        captureFilePath = com.lc.media.utils.MediaPlayHelper.getDevicesListImageCachePath(MediaPlayHelper.LCFilesType.LCImageCache, channelName);
        if (TextUtils.isEmpty(captureFilePath)) {
            holder.ivBg.setImageResource(R.mipmap.lc_demo_default_bg);
        } else {
            holder.ivBg.setImageURI(Uri.fromFile(new File(captureFilePath)));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onChannelClickListener != null) {
                onChannelClickListener.onChannelClick(position);
            }
        });

        holder.to_msg.setOnClickListener((View v) -> {
            if (onChannelClickListener != null) {
                onChannelClickListener.onMessageClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    static class ChannelHolder extends RecyclerView.ViewHolder {
        ImageView ivBg;
        RelativeLayout rlOffline;
        TextView tvOffline;
        ImageView ivPlay;
        LinearLayout rlDetail;
        TextView tvName;
        ImageView to_msg;
        TextView offlineTime;

        public ChannelHolder(View itemView) {
            super(itemView);
            ivBg = itemView.findViewById(R.id.iv_bg);
            rlOffline = itemView.findViewById(R.id.rl_offline);
            tvOffline = itemView.findViewById(R.id.tv_offline);
            ivPlay = itemView.findViewById(R.id.iv_play);
            rlDetail = itemView.findViewById(R.id.rl_detail);
            tvName = itemView.findViewById(R.id.tv_name);
            to_msg = itemView.findViewById(R.id.to_msg);
            offlineTime = itemView.findViewById(R.id.tv_offline_time);

        }
    }

    private OnChannelClickListener onChannelClickListener;

    public interface OnChannelClickListener {
        void onChannelClick(int position);

        void onMessageClick(int position);
    }

    public void setOnItemClickListener(OnChannelClickListener onChannelClickListener) {
        this.onChannelClickListener = onChannelClickListener;
    }
}
