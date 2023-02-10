package com.mm.android.deviceaddmodule.p_wiredwireless;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.WifiPwdConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.base.DefaultPermissionListener;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.location.FuseLocationUtil;
import com.mm.android.mobilecommon.utils.CommonHelper;
import com.mm.android.mobilecommon.utils.UIHelper;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.deviceaddmodule.presenter.WifiPwdPresenter;

/**
 * Enter wifi password page
 *
 * 输入wifi密码页
 */
public class WifiPwdFragment extends BaseDevAddFragment implements WifiPwdConstract.View, View.OnClickListener {
    private ImageView mWifiIv;
    private TextView mNextTv;
    private TextView mWifiSsidTv;
    private ClearPasswordEditText mWifiPwdEt;
    private TextView m5GWifiTipTv;
    private TextView mSaveWifiPwdChebox;
    WifiPwdConstract.Presenter mPresenter;
    private ImageView mSwitchWifiIv;

    public static WifiPwdFragment newInstance() {
        WifiPwdFragment fragment = new WifiPwdFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            mWifiPwdEt.removeTextChangedListener(mTextWatcher);
            String str = Utils4AddDevice.wifiPwdFilter(s.toString());
            if (!str.equals(s.toString())) {
                mWifiPwdEt.setText(str);
                mWifiPwdEt.setSelection(str.length());
            }
            mWifiPwdEt.addTextChangedListener(mTextWatcher);
            setConnectButtonState();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_pwd, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWifiSsidTv.setText(mPresenter.getCurWifiName());
        setWifiPwd();

    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.updateWifiCache();//更新wifi信息到缓存
    }

    protected void initView(View view) {
        mWifiIv = view.findViewById(R.id.iv_wifi);
        mSwitchWifiIv = view.findViewById(R.id.switch_wifi);
        mNextTv = view.findViewById(R.id.next);
        m5GWifiTipTv = view.findViewById(R.id.tv_5g_tip);
        mWifiSsidTv = view.findViewById(R.id.ssid);
        mWifiPwdEt = view.findViewById(R.id.wifi_pwd);
        mWifiPwdEt.openEyeMode(ProviderManager.getAppProvider().getAppType() != LCConfiguration.APP_LECHANGE_OVERSEA);
        mWifiPwdEt.addTextChangedListener(mTextWatcher);
        mNextTv.setOnClickListener(this);
        mSaveWifiPwdChebox = view.findViewById(R.id.wifi_pwd_check);
        m5GWifiTipTv.setOnClickListener(this);
        mSaveWifiPwdChebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
            }
        });

        mSwitchWifiIv.setOnClickListener(this);
    }

    protected void initData() {
        mPresenter = new WifiPwdPresenter(this);
        String configMode = mPresenter.getConfigMode();
        if (DeviceAddInfo.ConfigMode.LAN.name().equalsIgnoreCase(configMode)
                || !configMode.contains(DeviceAddInfo.ConfigMode.LAN.name()))
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
        }
        check5GWifiTip();
        setConnectButtonState();
        setSSIDTextView(true);
    }

    private void setSSIDTextView(boolean needCheckPermission) {
        if (null != getActivity() && !Utils4AddDevice.isWifi(getActivity())) {
            mWifiSsidTv.setText("");
            mWifiPwdEt.setText("");
            showToastInfo(R.string.add_device_con_wifi);
            return;
        }

        String curWifiName = mPresenter.getCurWifiName();
        if (TextUtils.isEmpty(curWifiName) && needCheckPermission) {
            dealWithUnknownSsid();
            return;
        }

        mWifiSsidTv.setText(curWifiName);
        setWifiPwd();
    }

    private void dealWithUnknownSsid() {
        if (getActivity() == null) return;
        //1.判断是否该应用有地理位置权限  2.判断是否开启定位服务
        PermissionHelper permissionHelper = new PermissionHelper(this);
        if (permissionHelper.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            verifyLocationService();
        } else {
            permissionHelper.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new DefaultPermissionListener() {
                @Override
                public void onGranted() {
                    verifyLocationService();
                }

                @Override
                public boolean onDenied() {
                    return true;
                }
            });
        }


    }

    private void verifyLocationService() {
        if (FuseLocationUtil.isGpsEnabled(getActivity())) {
            mWifiSsidTv.setText(mPresenter.getCurWifiName());
            setWifiPwd();
        } else {
            showOpenLocationServiceDialog();
        }

    }

    private void showOpenLocationServiceDialog() {
        LCAlertDialog dialog = new LCAlertDialog.Builder(getActivity())
                .setTitle(R.string.add_device_goto_open_location_service)
                .setCancelButton(R.string.common_cancel, null)
                .setConfirmButton(R.string.common_confirm, new LCAlertDialog.OnClickListener() {
                    @Override
                    public void onClick(LCAlertDialog dialog, int which, boolean isChecked) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show(getFragmentManager(), null);
    }

    private void setWifiPwd() {
        String wifiPwd = mPresenter.getSavedWifiPwd();
        boolean wifiCheckBoxStatus = mPresenter.getSavedWifiCheckBoxStatus();

        if (!TextUtils.isEmpty(wifiPwd)) {
            mWifiPwdEt.setText(wifiPwd);
            mSaveWifiPwdChebox.setSelected(wifiCheckBoxStatus);
        } else {
            mWifiPwdEt.setText("");
            mSaveWifiPwdChebox.setSelected(wifiCheckBoxStatus);
        }
    }

    private void check5GWifiTip() {
        if (!mPresenter.isDevSupport5G()) {
            m5GWifiTipTv.setVisibility(View.VISIBLE);
            mWifiIv.setImageResource(R.drawable.adddevice_icon_wifipassword_nosupport5g_layer);
        } else {
            m5GWifiTipTv.setVisibility(View.GONE);
            mWifiIv.setImageResource(R.drawable.adddevice_icon_wifipassword);
        }
    }

    private void setConnectButtonState() {
        if (null != getActivity() && !Utils4AddDevice.isWifi(getActivity())) {
            UIHelper.setEnabledEX(false, mNextTv);
        } else {
            UIHelper.setEnabledEX(true, mNextTv);
        }
    }

    @Override
    protected IntentFilter createBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LCConfiguration.CONNECTIVITY_CHAGET_ACTION);
        return intentFilter;
    }

    @Override
    protected void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (LCConfiguration.CONNECTIVITY_CHAGET_ACTION.equals(intent.getAction())) {
            setConnectButtonState();
            setSSIDTextView(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != getActivity() && mWifiPwdEt != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mWifiPwdEt.getWindowToken(), 0);

        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (getActivity() == null) return;
        if (R.id.next == id) {
            if (!Utils4AddDevice.isWifi(getActivity())) {
                toastInCenter(R.string.add_device_con_wifi);
                return;
            }

            if (TextUtils.isEmpty(mWifiSsidTv.getText().toString())) {
                toast(R.string.mobile_common_permission_explain_access_location_usage);
                return;
            }

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mWifiPwdEt.getWindowToken(), 0);

            PageNavigationHelper.gotoLightTipPage(this);
        } else if (R.id.tv_5g_tip == id) {
            PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_5G);
        } else if (R.id.switch_wifi == id) {
            CommonHelper.gotoWifiSetting(getActivity());
        }
    }

    @Override
    public String getWifiPwd() {
        return mWifiPwdEt.getText().toString();
    }

    @Override
    public boolean isSavePwdChecked() {
        return mSaveWifiPwdChebox.isSelected();
    }

}
