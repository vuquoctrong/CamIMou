package com.mm.android.deviceaddmodule.device_wifi;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.base.adapter.CommonAdapter;
import com.mm.android.mobilecommon.common.ViewHolder;

import java.util.List;

public class DeviceWifiListAdapter extends CommonAdapter<WifiInfo> {

    public DeviceWifiListAdapter(int layout, List<WifiInfo> list, Context context) {
        super(layout, list, context);
    }

    @Override
    public void convert(ViewHolder viewHolder, WifiInfo entity,
                        final int position, ViewGroup parent) {
        TextView wifiSSID = (TextView) viewHolder.findViewById(R.id.wifi_ssid);
        ImageView wifiQualityIcon = (ImageView) viewHolder.findViewById(R.id.wifi_quality_icon);

        wifiSSID.setText(entity.getSsid());  //设置wifi名
        int quality = entity.getIntensity();  //获取wifi信号

        if (quality < 2) {
            wifiQualityIcon.setImageResource("OPEN".equalsIgnoreCase(entity.getAuth())
                    ? R.drawable.devicedetail_wifi_nosingal : R.drawable.devicedetail_wifi_nosingal_lock);
        } else if (quality < 3) {
            wifiQualityIcon.setImageResource("OPEN".equalsIgnoreCase(entity.getAuth())
                    ? R.drawable.devicedetail_wifi_1singal : R.drawable.devicedetail_wifi_1singal_lock);
        } else if (quality < 4) {
            wifiQualityIcon.setImageResource("OPEN".equalsIgnoreCase(entity.getAuth())
                    ? R.drawable.devicedetail_wifi_2singal : R.drawable.devicedetail_wifi_2singal_lock);
        } else {
            wifiQualityIcon.setImageResource("OPEN".equalsIgnoreCase(entity.getAuth())
                    ? R.drawable.devicedetail_wifi_3singal : R.drawable.devicedetail_wifi_3singal_lock);
        }
    }
}
