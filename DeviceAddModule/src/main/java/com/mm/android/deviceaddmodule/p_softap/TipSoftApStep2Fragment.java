package com.mm.android.deviceaddmodule.p_softap;

import android.os.Bundle;

import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.presenter.BaseSoftApTipPresenter;
/**
 * Soft AP Add boot tip page 2
 *
 * 软AP添加引导提示页2
 */
public class TipSoftApStep2Fragment extends BaseTipSoftApFragment {

    public static TipSoftApStep2Fragment newInstance() {
        TipSoftApStep2Fragment fragment = new TipSoftApStep2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void gotoNextSoftApTipPage() {
        PageNavigationHelper.gotoSoftApTip3Page(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter=new BaseSoftApTipPresenter(this,1);
    }
}
