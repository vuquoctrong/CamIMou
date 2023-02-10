package com.lc.playback.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.playback.R;
import com.lc.playback.entity.RecordListData;

import java.util.List;

public class DeviceRecordListAdapter extends RecyclerView.Adapter<DeviceRecordListAdapter.DeviceListHolder> {

    private Context mContext;
    private List<RecordListData> recordListDataList;

    public DeviceRecordListAdapter(Context mContext, List<RecordListData> recordListDataList) {
        this.mContext = mContext;
        this.recordListDataList = recordListDataList;
    }

    @NonNull
    @Override
    public DeviceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_record_list, parent, false);
        return new DeviceListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvTime.setText(recordListDataList.get(position).period);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        holder.rcvRecord.setLayoutManager(gridLayoutManager);
        DeviceInnerRecordListAdapter deviceInnerRecordListAdapter = new DeviceInnerRecordListAdapter(mContext, recordListDataList.get(position).recordsData);
        deviceInnerRecordListAdapter.setEditClickListener(new DeviceInnerRecordListAdapter.EditClickListener() {
            @Override
            public void edit(int innerPosition) {
                if (editClickListener != null) {
                    editClickListener.edit(position,innerPosition);
                }
            }
        });
        holder.rcvRecord.setAdapter(deviceInnerRecordListAdapter);
    }

    @Override
    public int getItemCount() {
        return recordListDataList == null ? 0 : recordListDataList.size();
    }

    class DeviceListHolder extends RecyclerView.ViewHolder {

        RecyclerView rcvRecord;
        TextView tvTime;

        public DeviceListHolder(View itemView) {
            super(itemView);
            rcvRecord = itemView.findViewById(R.id.rcv_record);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    private EditClickListener editClickListener;

    public interface EditClickListener {
        void edit(int outPosition, int innerPosition);
    }

    public void setEditClickListener(EditClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }
}
