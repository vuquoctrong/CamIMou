package com.mm.android.deviceaddmodule.p_errortip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;

/**
 * Does not support 5 g
 *
 * 不支持5G
 */
public class TipNotSupport5GFragment extends BaseDevAddFragment {
    public static TipNotSupport5GFragment newInstance() {
        TipNotSupport5GFragment fragment = new TipNotSupport5GFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip_not_support_5g, container, false);
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void initData() {
    }
}
