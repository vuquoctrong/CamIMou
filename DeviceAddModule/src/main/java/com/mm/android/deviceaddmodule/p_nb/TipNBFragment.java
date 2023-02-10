package com.mm.android.deviceaddmodule.p_nb;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

public class TipNBFragment extends BaseTipFragment {

    public static TipNBFragment newInstance() {
        TipNBFragment fragment = new TipNBFragment();
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
        tipImageMatch();
        mConfirmCheck.setVisibility(View.GONE);
        mTipTxt.setText(R.string.add_device_nb_tip1);
        mTipTxt2.setVisibility(View.VISIBLE);
        mTipTxt2.setText(R.string.add_device_nb_tip2);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceAddInfo deviceAddInfo= DeviceAddModel.newInstance().getDeviceInfoCache();
        DeviceIntroductionInfo deviceIntroductionInfo = deviceAddInfo.getDevIntroductionInfo();
        if (deviceIntroductionInfo != null) {
            String tipImage = deviceIntroductionInfo.getImageInfos().get(DeviceAddHelper.OMSKey.THIRD_PARTY_PLATFORM_MODE_GUIDING_LIGHT_IMAGE);
            if (!TextUtils.isEmpty(tipImage)) {
                Glide.with(TipNBFragment.this.getContext()).load(tipImage).into(mTipImg);
            }
        }
    }

    @Override
    protected void nextAction() {
        PageNavigationHelper.gotoCloudConnectPage(this);
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
