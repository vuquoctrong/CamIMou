package com.mm.android.deviceaddmodule.p_devicelocal;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Local network allocation
 *
 * 设备本地配网
 */
public class TipDeviceLocalFragment extends BaseTipFragment {

    public static TipDeviceLocalFragment newInstance() {
        TipDeviceLocalFragment fragment = new TipDeviceLocalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);

        mConfirmCheck.setVisibility(View.GONE);
        mNextBtn.setVisibility(View.VISIBLE);
        mNextBtn.setText(R.string.common_confirm);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceAddInfo deviceAddInfo= DeviceAddModel.newInstance().getDeviceInfoCache();
        DeviceIntroductionInfo deviceIntroductionInfo = deviceAddInfo.getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String tipImage = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.LOCATION_MODE_OPERATION_IMAGE);
            String tipTxt = deviceIntroductionInfo.getStrInfos().get(DeviceAddHelper.OMSKey.LOCATION_MODE_OPERATION_INTRODUCTION);
            if (!TextUtils.isEmpty(tipImage)) {
                Glide.with(TipDeviceLocalFragment.this.getContext()).load(tipImage).into(mTipImg);
            }
            if (!TextUtils.isEmpty(tipTxt)) {
                mTipTxt.setText(tipTxt);

            }
        }
    }

    @Override
    protected void nextAction() {
        if (getActivity() != null) {
            if (DeviceAddModel.newInstance().getDeviceInfoCache().isWifiOfflineMode()
                    || DeviceAddInfo.DeviceAddType.HUB.equals(DeviceAddModel.newInstance().getDeviceInfoCache().getCurDeviceAddType())) {
                ProviderManager.getDeviceAddCustomProvider().goHomePage(getActivity());//离线配网模式，跳转到主页
            } else {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DESTROY_ACTION));
        }
    }

    @Override
    protected void helpAction() {

    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }
}
