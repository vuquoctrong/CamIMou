package com.mm.android.deviceaddmodule.p_offlineconfig;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.OfflineConfigConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.deviceaddmodule.presenter.OfflineConfigPresenter;

/**
 * The device is reconnected offline
 *
 * 设备离线重连
 **/
public class OfflineConfigFragment extends BaseDevAddFragment implements OfflineConfigConstract.View {
    protected ImageView mTipImg;
    protected TextView mTipTxt, mNextBtn;
    OfflineConfigConstract.Presenter mPresenter;
    Handler mHandler=new Handler();

    public static OfflineConfigFragment newInstance(String sn,String devModelName, String imei) {
        OfflineConfigFragment fragment = new OfflineConfigFragment();
        Bundle args = new Bundle();
        args.putString(LCConfiguration.DEVICESN_PARAM,sn);
        args.putString(LCConfiguration.DEVICE_MODEL_NAME_PARAM,devModelName);
        args.putString(LCConfiguration.DEVICE_IMEI_PARAM, imei);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_tip, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.BLANK);
    }

    @Override
    protected void initView(View view) {
        mTipImg = view.findViewById(R.id.tip_img);
        mTipTxt = view.findViewById(R.id.tip_txt);
        mNextBtn = view.findViewById(R.id.tv_next);
        mTipImg.setVisibility(View.GONE);
        mTipTxt.setVisibility(View.GONE);
        mNextBtn.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        mPresenter=new OfflineConfigPresenter(this);
        mPresenter.resetCache();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isViewActive())
                    mPresenter.getDeviceInfo(getArguments().getString(LCConfiguration.DEVICESN_PARAM),
                            getArguments().getString(LCConfiguration.DEVICE_MODEL_NAME_PARAM));
            }
        }, 100);
    }

    @Override
    public void onGetDeviceInfoError() {
        getActivity().finish();
    }
}
