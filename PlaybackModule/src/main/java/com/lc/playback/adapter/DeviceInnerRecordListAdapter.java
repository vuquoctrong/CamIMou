package com.lc.playback.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.lc.playback.R;
import com.lc.playback.entity.RecordsData;
import com.lc.playback.utils.DateHelper;
import com.mm.android.mobilecommon.utils.ImageHelper;

import java.util.List;

public class DeviceInnerRecordListAdapter extends RecyclerView.Adapter<DeviceInnerRecordListAdapter.DeviceInnerRecordListHolder> {
    private Context mContext;
    private List<RecordsData> recordsData;

    public DeviceInnerRecordListAdapter(Context mContext, List<RecordsData> recordsData) {
        this.mContext = mContext;
        this.recordsData = recordsData;
    }

    @NonNull
    @Override
    public DeviceInnerRecordListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false);
        return new DeviceInnerRecordListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceInnerRecordListHolder holder, @SuppressLint("RecyclerView") final int position) {
        long time = DateHelper.string2Date(recordsData.get(position).endTime).getTime() - DateHelper.string2Date(recordsData.get(position).beginTime).getTime();
        holder.tvTimeM.setText(recordsData.get(position).beginTime.substring(11));
        holder.tvTimeS.setText(DateHelper.format(time));
        if (recordsData.get(position).recordType == 0) {
            try {
                ImageHelper.loadCacheImage(recordsData.get(position).thumbUrl, recordsData.get(position).deviceId, recordsData.get(position).deviceId, position, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try {
                            if (recordsData.get(msg.arg1).thumbUrl.hashCode() == msg.what && msg.obj != null) {
                                holder.ivBg.setImageDrawable((Drawable) msg.obj);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.ivCheck.setVisibility(recordsData.get(position).check ? View.VISIBLE : View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editClickListener != null) {
                    editClickListener.edit(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordsData == null ? 0 : recordsData.size();
    }

    class DeviceInnerRecordListHolder extends RecyclerView.ViewHolder {

        ImageView ivBg;
        TextView tvTimeS;
        TextView tvTimeM;
        ImageView ivCheck;

        public DeviceInnerRecordListHolder(View itemView) {
            super(itemView);
            ivBg = itemView.findViewById(R.id.iv_bg);
            ivCheck = itemView.findViewById(R.id.iv_check);
            tvTimeS = itemView.findViewById(R.id.tv_time_s);
            tvTimeM = itemView.findViewById(R.id.tv_time_m);
        }
    }

    private EditClickListener editClickListener;

    public interface EditClickListener {
        void edit(int innerPosition);
    }

    public void setEditClickListener(EditClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }
}
