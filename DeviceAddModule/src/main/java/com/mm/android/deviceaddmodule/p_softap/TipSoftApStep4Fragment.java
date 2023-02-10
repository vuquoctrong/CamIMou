package com.mm.android.deviceaddmodule.p_softap;

import android.os.Bundle;

import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.presenter.BaseSoftApTipPresenter;
/**
 * Soft AP Add boot tip page 4
 *
 * 软AP添加引导提示页4
 */
public class TipSoftApStep4Fragment extends BaseTipSoftApFragment {

    public static TipSoftApStep4Fragment newInstance() {
        TipSoftApStep4Fragment fragment = new TipSoftApStep4Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void gotoNextSoftApTipPage() {
        PageNavigationHelper.gotoSoftApTipConnectWifiPage(this,false);
    }


    @Override
    protected void initData() {
        super.initData();
        mPresenter=new BaseSoftApTipPresenter(this,3);
    }
}
