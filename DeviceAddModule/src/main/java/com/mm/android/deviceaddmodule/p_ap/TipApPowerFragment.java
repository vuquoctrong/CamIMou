package com.mm.android.deviceaddmodule.p_ap;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;

public class TipApPowerFragment extends BaseTipFragment {

    public static TipApPowerFragment newInstance() {
        TipApPowerFragment fragment = new TipApPowerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tipImageMatch();
        mTipImg.setImageResource(R.drawable.adddevice_netsetting_battery);
        mTipImg.setScaleType(ImageView.ScaleType.FIT_XY);
        mTipTxt.setText(R.string.add_device_ap_install_battery);
    }

    @Override
    protected void nextAction() {
        PageNavigationHelper.gotoApLightPage(this);
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
