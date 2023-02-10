package com.mm.android.deviceaddmodule.p_softap;

import android.os.Bundle;

import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.presenter.BaseSoftApTipPresenter;

/**
 * Soft AP Add boot tip page 1
 *
 * 软AP添加引导提示页1
 */
public class TipSoftApStep1Fragment extends BaseTipSoftApFragment {

    public static TipSoftApStep1Fragment newInstance() {
        TipSoftApStep1Fragment fragment = new TipSoftApStep1Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void gotoNextSoftApTipPage() {
        PageNavigationHelper.gotoSoftApTip2Page(this);
    }


    @Override
    protected void initData() {
        super.initData();
        mPresenter=new BaseSoftApTipPresenter(this,0);
    }
}
