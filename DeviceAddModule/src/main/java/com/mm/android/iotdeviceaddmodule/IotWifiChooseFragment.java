package com.mm.android.iotdeviceaddmodule;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.LCDeviceEngine;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.views.ChooseNetDialog;
import com.mm.android.deviceaddmodule.views.NotSupport5GTipDialog;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.IotWifiChooseConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.base.DefaultPermissionListener;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.location.FuseLocationUtil;
import com.mm.android.mobilecommon.utils.CommonHelper;
import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.IotWifiChoosePresenter;

import org.greenrobot.eventbus.EventBus;

public class IotWifiChooseFragment extends BaseDevAddFragment implements IotWifiChooseConstract.View, View.OnClickListener {
    private EditText ssidEt;
    private ClearPasswordEditText wifi_pwd;
    private TextView nextTv;
    private TextView tv_5g_tip;
    private TextView wifi_pwd_check;
    private ImageView switch_wifi;
    private IotWifiChoosePresenter presenter;
    private String WIFI_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_ADD_";
    private String WIFI_CHECKBOX_SAVE_PREFIX = TokenHelper.getInstance().userId + "_WIFI_CHECKBOX_ADD_";

    public static IotWifiChooseFragment newInstance() {
        IotWifiChooseFragment fragment = new IotWifiChooseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        ssidEt = (EditText) view.findViewById(R.id.ssid);
        wifi_pwd = (ClearPasswordEditText) view.findViewById(R.id.wifi_pwd);
        nextTv = view.findViewById(R.id.next);
        wifi_pwd_check = view.findViewById(R.id.wifi_pwd_check);
        tv_5g_tip = view.findViewById(R.id.tv_5g_tip);
        switch_wifi = view.findViewById(R.id.switch_wifi);
        ssidEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nextTv.setEnabled(!TextUtils.isEmpty(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        wifi_pwd.openEyeMode(false);
        wifi_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String str = StringUtils.wifiPwdFilter(editable.toString());
//                if (str != editable.toString()) {
//                    wifi_pwd.setText(str);
//                    wifi_pwd.setSelection(str.length());
//                }
            }
        });

        tv_5g_tip.setOnClickListener(this);
        switch_wifi.setOnClickListener(this);
        nextTv.setOnClickListener(this);
        wifi_pwd_check.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setWifi();
    }

    private void goNext() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(nextTv.getWindowToken(), 0);
        updateCheckPwd();
        PageNavigationHelper.gotoIotGuidePage(getActivity());
    }

    private void updateCheckPwd(){

        DeviceAddInfo.WifiInfo wifiInfo = DeviceAddModel.newInstance().getDeviceInfoCache().getWifiInfo();
        wifiInfo.setSsid(ssidEt.getText().toString());
        wifiInfo.setPwd(wifi_pwd.getText().toString());
        boolean isCheck = wifi_pwd_check.isSelected();
        if(isCheck){
            PreferencesHelper.getInstance(getActivity()).set(WIFI_SAVE_PREFIX + ssidEt.getText().toString(), wifi_pwd.getText().toString());
            PreferencesHelper.getInstance(getActivity()).set(WIFI_CHECKBOX_SAVE_PREFIX + ssidEt.getText().toString(), true);
        }else{
            PreferencesHelper.getInstance(getActivity()).set(WIFI_SAVE_PREFIX + ssidEt.getText().toString(), "");
            PreferencesHelper.getInstance(getActivity()).set(WIFI_CHECKBOX_SAVE_PREFIX + ssidEt.getText().toString(), false);
        }
    }

    private void setWifi() {
        String wifiName = TextUtils.isEmpty(presenter.getCurWifiName()) ? "" : presenter.getCurWifiName();
        ssidEt.setText(wifiName);
        boolean isChecked = PreferencesHelper.getInstance(getActivity()).getBoolean(WIFI_CHECKBOX_SAVE_PREFIX + wifiName);
        wifi_pwd_check.setSelected(isChecked);
        if(isChecked){
            String savePwd = PreferencesHelper.getInstance(getActivity()).getString(WIFI_SAVE_PREFIX + wifiName);
            wifi_pwd.setText(savePwd);
        }
    }

    private void dealWithUnknownSsid() {
        //1.判断是否该应用有地理位置权限  2.判断是否开启定位服务
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
                    return false;
                }
            });
        }
    }

    private void verifyLocationService() {
        if (FuseLocationUtil.isGpsEnabled(getActivity())) {
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

    @Override
    protected void initData() {
        presenter = new IotWifiChoosePresenter(this);
        if (TextUtils.isEmpty(ssidEt.getText())) {
            dealWithUnknownSsid();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.iotadd_wifi_choose, container, false);
    }

    private void initView() {
        presenter = new IotWifiChoosePresenter(this);
    }

    /**
     * Does not support 5 g
     *
     * 不支持5G
     */
    private void showNotSupport5GTipDialog() {
        NotSupport5GTipDialog chooseNetDialog = new NotSupport5GTipDialog(getContext());
        chooseNetDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_5g_tip) {
            showNotSupport5GTipDialog();
        } else if (id == R.id.switch_wifi) {
            CommonHelper.gotoWifiSetting(IotWifiChooseFragment.this.getActivity());
        } else if (id == R.id.next) {
            goNext();
        } else if(id == R.id.wifi_pwd_check){
            saveCheckPwd();
        }
    }

    private void saveCheckPwd() {
        wifi_pwd_check.setSelected(!wifi_pwd_check.isSelected());
    }
}
