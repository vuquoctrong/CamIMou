package com.mm.android.deviceaddmodule.p_errortip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;

/**
 * The account is bound
 *
 * 帐号被绑定提示
 */
public class TipUserBindFragment extends BaseDevAddFragment {
    public static TipUserBindFragment newInstance() {
        TipUserBindFragment fragment = new TipUserBindFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip_user_bind, container, false);
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void initData() {
    }
}
