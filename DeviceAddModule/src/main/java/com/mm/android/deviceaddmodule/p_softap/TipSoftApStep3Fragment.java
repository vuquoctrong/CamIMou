package com.mm.android.deviceaddmodule.p_softap;

import android.os.Bundle;

import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.presenter.BaseSoftApTipPresenter;
/**
 * Soft AP Add boot tip page 3
 *
 * 软AP添加引导提示页3
 */
public class TipSoftApStep3Fragment extends BaseTipSoftApFragment {

    public static TipSoftApStep3Fragment newInstance() {
        TipSoftApStep3Fragment fragment = new TipSoftApStep3Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void gotoNextSoftApTipPage() {
        PageNavigationHelper.gotoSoftApTip4Page(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter=new BaseSoftApTipPresenter(this,2);
    }
}
