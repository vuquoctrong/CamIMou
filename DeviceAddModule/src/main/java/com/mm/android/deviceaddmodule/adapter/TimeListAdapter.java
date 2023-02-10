package com.mm.android.deviceaddmodule.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.device_wifi.Utils4DeviceManager;
import com.mm.android.mobilecommon.openapi.data.TimeSlice;
import com.mm.android.mobilecommon.common.ViewHolder;

import java.util.Calendar;
import java.util.List;


/**
 * Created by zhengcong on 2018/8/22.
 * Time list adapter
 *
 * Created by zhengcong on 2018/8/22.
 * 时间段列表适配器
 */

public class TimeListAdapter extends CommonSwipeAdapter<TimeSlice> {

    public TimeListAdapter(List<TimeSlice> list, Context context) {
        super(R.layout.item_time_list, list, context);
    }

    public TimeListAdapter(List<TimeSlice> list, Context context, OnMenuItemClickListener menuItemCllickLinstener) {
        super(R.layout.item_time_list, list, context, menuItemCllickLinstener);
    }

    @Override
    public boolean getSwipeLayoutEnable(int position) {
        return true;
    }

    @Override
    public void convert(ViewHolder viewHolder, TimeSlice item, int position, ViewGroup parent) {
        TextView time = (TextView) viewHolder.findViewById(R.id.time);
        View divider = viewHolder.findViewById(R.id.divider);

        if (item == null) {
            return;
        }

        Calendar beginCalendar = Utils4DeviceManager.resolveTime(item.getBeginTime());
        Calendar endCalendar = Utils4DeviceManager.resolveTime(item.getEndTime());

        String format = "%02d";
        String beginTime = String.format(format, beginCalendar.get(Calendar.HOUR_OF_DAY))
                + ":" + String.format(format, beginCalendar.get(Calendar.MINUTE));
        String endTime = String.format(format, endCalendar.get(Calendar.HOUR_OF_DAY))
                + ":" + String.format(format, endCalendar.get(Calendar.MINUTE));
        time.setText(beginTime + "-" + endTime);

        if (position == getCount() - 1)
            divider.setVisibility(View.GONE);
        else
            divider.setVisibility(View.VISIBLE);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
