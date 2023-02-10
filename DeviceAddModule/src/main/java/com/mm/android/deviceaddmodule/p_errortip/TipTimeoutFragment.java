package com.mm.android.deviceaddmodule.p_errortip;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.TimeoutConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.TimeoutPresenter;

/**
 * Connection timeout prompt page
 *
 * 连接超时提示页
 */
public class TipTimeoutFragment extends BaseDevAddFragment implements TimeoutConstract.View, View.OnClickListener {
    TimeoutConstract.Presenter mPresenter;
    public static String ERROR_PARAMS = "error_params";
    public static String DEV_TYPE_PARAMS = "dev_type_params";
    TextView mActionTxt1;

    public static TipTimeoutFragment newInstance(int errorCode, String timeoutDevTypeModel) {
        TipTimeoutFragment fragment = new TipTimeoutFragment();
        Bundle args = new Bundle();
        args.putInt(ERROR_PARAMS, errorCode);
        args.putString(DEV_TYPE_PARAMS, timeoutDevTypeModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tip_timeout, container, false);
    }

    protected void initView(View view) {
        mActionTxt1 = view.findViewById(R.id.tv_action1);
        mActionTxt1.setOnClickListener(this);
    }

    protected void initData() {
        mPresenter = new TimeoutPresenter(this);
        if (getArguments() != null) {
            int errorcode = getArguments().getInt(ERROR_PARAMS);
            String devtypeModel = getArguments().getString(DEV_TYPE_PARAMS);
            mPresenter.setErrorData(errorcode, devtypeModel);
            if (errorcode == DeviceAddHelper.ErrorCode.WIRED_WIRELESS_ERROR_CONFIG_TIMEOUT || errorcode == DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_ALWAYS || errorcode == DeviceAddHelper.ErrorCode.COMMON_ERROR_RED_FLASH) {
                DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
                if (DeviceAddInfo.ConfigMode.LAN.name().equalsIgnoreCase(deviceAddInfo.getConfigMode()) || !deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.LAN.name()))
                    DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
                else {
                    DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
                }
            } else {
                DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_action1) {
            mPresenter.dispatchAction1();
        }
    }

    @Override
    public void showAView() {
        mActionTxt1.setVisibility(View.VISIBLE);
        mActionTxt1.setText(R.string.add_device_yellow_light_twinkle);
    }

    @Override
    public void goScanPage() {
        getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}
