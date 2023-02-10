package com.mm.android.deviceaddmodule.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.mobilecommon.base.adapter.CommonAdapter;
import com.mm.android.mobilecommon.common.ViewHolder;

import java.util.List;


public class WifiListAdapter extends CommonAdapter<WlanInfo> {
    public WifiListAdapter(int layout, List<WlanInfo> list, Context mContext) {
        super(layout, list, mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(layout, parent, false);
        }
        WlanInfo entity = getItem(position);
        TextView wlanName = (TextView) convertView.findViewById(R.id.wlan_name);
        ImageView wlanStrength = (ImageView) convertView.findViewById(R.id.wlan_len);
        ImageView wlanAuthMode = (ImageView) convertView.findViewById(R.id.wlan_mode);

        wlanName.setText(entity.getWlanSSID());  //设置wifi名

        int quality = entity.getWlanQuality();  //获取wifi信号

        if (quality < 15) {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_nosingal:R.drawable.devicedetail_wifi_nosingal_lock;
            wlanStrength.setImageResource(singalDrawable);
        } else if (quality < 45) {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_1singal:R.drawable.devicedetail_wifi_1singal_lock;
            wlanStrength.setImageResource(singalDrawable);
        } else if (quality < 75) {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_2singal:R.drawable.devicedetail_wifi_2singal_lock;
            wlanStrength.setImageResource(singalDrawable);
        } else {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_3singal:R.drawable.devicedetail_wifi_3singal_lock;
            wlanStrength.setImageResource(singalDrawable);
        }

        return convertView;
    }

    @Override
    public void convert(ViewHolder viewHolder, WlanInfo entity,
                        final int position, ViewGroup parent) {
        TextView wlanName = (TextView) viewHolder.findViewById(R.id.wlan_name);
        ImageView wlanStrength = (ImageView) viewHolder.findViewById(R.id.wlan_len);
        ImageView wlanAuthMode = (ImageView) viewHolder.findViewById(R.id.wlan_mode);

        wlanName.setText(entity.getWlanSSID());  //设置wifi名

        int quality = entity.getWlanQuality();  //获取wifi信号

        if (quality < 15) {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_nosingal:R.drawable.devicedetail_wifi_nosingal_lock;
            wlanStrength.setImageResource(singalDrawable);
        } else if (quality < 45) {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_1singal:R.drawable.devicedetail_wifi_1singal_lock;
            wlanStrength.setImageResource(singalDrawable);
        } else if (quality < 75) {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_2singal:R.drawable.devicedetail_wifi_2singal_lock;
            wlanStrength.setImageResource(singalDrawable);
        } else {
            int singalDrawable=entity.getWlanAuthMode() == 0 && entity.getWlanEncrAlgr() == 0?R.drawable.devicedetail_wifi_3singal:R.drawable.devicedetail_wifi_3singal_lock;
            wlanStrength.setImageResource(singalDrawable);
        }
    }

    public void refresh(){
        Log.i("refresh","list:"+list.size());
        this.notifyDataSetChanged();
    }
}
