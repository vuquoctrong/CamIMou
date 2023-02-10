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
public class TipWifiNameFragment extends BaseDevAddFragment {
    public static TipWifiNameFragment newInstance() {
        TipWifiNameFragment fragment = new TipWifiNameFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip_wifi_name, container, false);
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void initData() {
    }
}
