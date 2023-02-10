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
public class TipConnectFailFragment extends BaseDevAddFragment {
    public static TipConnectFailFragment newInstance() {
        TipConnectFailFragment fragment = new TipConnectFailFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip_connect_fail, container, false);
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void initData() {
    }
}
