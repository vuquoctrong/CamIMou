package com.mm.android.deviceaddmodule.views.popwindow;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.mobilecommon.base.adapter.CommonAdapter;
import com.mm.android.mobilecommon.common.ViewHolder;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ChoseTypePopWindow extends BasePopWindow implements AdapterView.OnItemClickListener {
    ListView mOptionList;
    OptionsAdapter mAdapter;
    PopWindowFactory.PopWindowType mType;
    List<Integer> mData;
    FragmentActivity mParent;

    ChoseTypePopWindow(View view, int width, int height) {
        super(view, width, height);
        mData = new ArrayList<>();
    }

    public void setType(PopWindowFactory.PopWindowType type) {
        this.mType = type;
        switch (type) {
            case CHOSETYPE:
                mData.add(R.string.add_device_add_by_wired);
                mData.add(R.string.add_device_add_by_sound_wave);
                mData.add(R.string.add_device_add_by_soft_ap);
                mData.add(R.string.common_cancel);
                break;
        }
    }

    @Override
    public void drawContent(Activity activity) {
        mParent = (FragmentActivity) activity;
        View view = getContentView();
        mOptionList = view.findViewById(R.id.option_list);

        mAdapter = new OptionsAdapter(R.layout.option_item, mData, activity);
        mOptionList.setAdapter(mAdapter);
        mOptionList.setOnItemClickListener(this);
    }

    @Override
    public void updateContent(Activity activity, boolean isPort) {
}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_WIRED_ACTION));
            DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.LAN);
        } else if (position == 1) {
            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_WIRELESS_ACTION));
            DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.WLAN);
        } else if (position == 2) {
            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_SOFTAP_ACTION));
            DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.SOFTAP);
        }
        dismiss();
}

    class OptionsAdapter extends CommonAdapter<Integer> {

        public OptionsAdapter(int layout, List list, Context mContext) {
            super(layout, list, mContext);
        }

        @Override
        public void convert(ViewHolder viewHolder, Integer item, int position, ViewGroup parent) {
            TextView optionName = (TextView) viewHolder.findViewById(R.id.option_name);
            optionName.setText(item);
            if (position == getCount() - 1) {
                optionName.setTextColor(mContext.getResources().getColor(R.color.c12));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)optionName.getLayoutParams();
                layoutParams.topMargin = 15;
                optionName.setLayoutParams(layoutParams);
            } else {
                optionName.setTextColor(mContext.getResources().getColor(R.color.c2));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)optionName.getLayoutParams();
                layoutParams.topMargin = 0;
                optionName.setLayoutParams(layoutParams);
            }
        }
    }
}
