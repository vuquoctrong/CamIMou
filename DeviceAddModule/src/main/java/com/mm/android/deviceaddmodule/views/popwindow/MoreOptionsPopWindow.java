package com.mm.android.deviceaddmodule.views.popwindow;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
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
import com.mm.android.deviceaddmodule.p_inputsn.DeviceDispatchHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.mm.android.deviceaddmodule.helper.PageNavigationHelper.SOFT_AP_TIP_TAG;
import static com.mm.android.deviceaddmodule.helper.PageNavigationHelper.TIP_POWER_FRAGMENT_TAG;

public class MoreOptionsPopWindow extends BasePopWindow implements AdapterView.OnItemClickListener {
    ListView mOptionList;
    OptionsAdapter mAdapter;
    PopWindowFactory.PopWindowType mType;
    List<Integer> mData;
    FragmentActivity mParent;

    MoreOptionsPopWindow(View view, int width, int height) {
        super(view, width, height);
        mData = new ArrayList<>();
    }

    public void setType(PopWindowFactory.PopWindowType type) {
        this.mType = type;
        switch (type) {
            case OPTION1:
                mData.add(R.string.add_device_restart);
                mData.add(R.string.common_cancel);
                break;
            case OPTION2:
                mData.add(R.string.add_device_restart);
                mData.add(R.string.add_device_switch_to_wired_add);
                mData.add(R.string.common_cancel);
                break;
            case OPTION3:
                mData.add(R.string.add_device_restart);
                mData.add(R.string.add_device_switch_to_wireless_add);
                mData.add(R.string.common_cancel);
                break;
            case OPTION4:
                mData.add(R.string.add_device_restart);
                mData.add(R.string.add_device_switch_to_soft_ap_add);
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
            if (DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode()
                    || DeviceAddInfo.DeviceAddType.HUB.equals(DeviceAddModel.newInstance().getDeviceInfoCache().getCurDeviceAddType())) {
                mParent.finish();   //离线配网模式，重新开始直接退出
            } else {
                DeviceDispatchHelper.setReAdd(true);
                mParent.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } else {
            switch (mType) {
                case OPTION2:
                    if (position == 1) {
                        DeviceDispatchHelper.setReAdd(true);
                        if(DeviceAddModel.newInstance().getDeviceInfoCache().getCurDeviceAddType() == DeviceAddInfo.DeviceAddType.SOFTAP){
                            mParent.getSupportFragmentManager().popBackStackImmediate(SOFT_AP_TIP_TAG,1);
                        } else {
                            mParent.getSupportFragmentManager().popBackStackImmediate(TIP_POWER_FRAGMENT_TAG,1);
                        }
                        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_WIRED_ACTION));

                        DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.LAN);
                    }
                    break;
                case OPTION3:
                    if (position == 1) {
                        DeviceDispatchHelper.setReAdd(true);
                        mParent.getSupportFragmentManager().popBackStackImmediate(TIP_POWER_FRAGMENT_TAG,1);
                        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_WIRELESS_ACTION));
                        DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.WLAN);
                    }
                    break;
                case OPTION4:
                    if (position == 1) {
                        DeviceDispatchHelper.setReAdd(true);
                        mParent.getSupportFragmentManager().popBackStackImmediate(TIP_POWER_FRAGMENT_TAG,1);
                        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_SOFTAP_ACTION));
                        DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.SOFTAP);
                    }
                    break;
            }
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
            if(position==getCount()-1){
                optionName.setTextColor(mContext.getResources().getColor(R.color.c12));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)optionName.getLayoutParams();
                layoutParams.topMargin = 15;
                optionName.setLayoutParams(layoutParams);
            }else{
                optionName.setTextColor(mContext.getResources().getColor(R.color.c2));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)optionName.getLayoutParams();
                layoutParams.topMargin = 0;
                optionName.setLayoutParams(layoutParams);
            }
        }
    }
}
