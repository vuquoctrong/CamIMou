package com.mm.android.deviceaddmodule.presenter;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.fragment.app.Fragment;

import com.dahua.mobile.utility.network.DHNetworkUtil;
import com.dahua.mobile.utility.network.DHWifiUtil;
import com.lechange.opensdk.bluetooth.LCOpenSDK_BluetoothConfig;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.helper.IotSoftApHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.base.DefaultPermissionListener;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.location.FuseLocationUtil;
import com.mm.android.mobilecommon.utils.StringUtils;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;
import com.mm.android.iotdeviceaddmodule.data.IotAddDeviceInfo;

public class IotConnectPresenter {
    private static final String TAG = "LCOpenSDK_IotConnectPresenter";
    Fragment fragment;
    private DeviceAddInfo deviceAddInfo;

    private boolean isHeartBeatBreak = false;
    private int heartCount = 0;
    private static final int ACTION_HEARTBEAT = 0x10001;


    public IotConnectPresenter(Fragment fragment) {
        this.fragment = fragment;

        deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();

    }

    public void verifyWifiOrLocationPermission() {
        IotSoftApHelper.getInstance().openWifi();//预先打开wifi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applyLocationPermission();
        } else {
            gotoSoftApTipConnectWifiPage();
        }
    }


    public void applyLocationPermission() {
        if (fragment.getActivity() == null) return;
        //1.判断是否该应用有地理位置权限  2.判断是否开启定位服务
        PermissionHelper permissionHelper = new PermissionHelper(fragment);
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
        if (FuseLocationUtil.isGpsEnabled(fragment.getContext())) {
            gotoSoftApTipConnectWifiPage();
        } else {
            showOpenLocationServiceDialog();
        }
    }

    public void gotoSoftApTipConnectWifiPage() {
        PageNavigationHelper.gotoSoftApTipConnectWifiPage(fragment, true);
    }

    private void showOpenLocationServiceDialog() {
        LCAlertDialog dialog = new LCAlertDialog.Builder(fragment.getContext())
                .setTitle(R.string.add_device_goto_open_location_service)
                .setCancelButton(R.string.common_cancel, null)
                .setConfirmButton(R.string.common_confirm, new LCAlertDialog.OnClickListener() {
                    @Override
                    public void onClick(LCAlertDialog dialog, int which, boolean isChecked) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        fragment.startActivity(intent);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show(fragment.getFragmentManager(), null);
    }


    private void onFailure() {

    }

    public void startScan( Handler resultHandler) {
        LCOpenSDK_BluetoothConfig.startBleConfig(fragment.getContext(), deviceAddInfo.getDeviceSn(),deviceAddInfo.getProductId(),deviceAddInfo.getWifiInfo().getSsid(),
                deviceAddInfo.getWifiInfo().getPwd(),resultHandler);
    }

    public void destroy() {
    }
}
