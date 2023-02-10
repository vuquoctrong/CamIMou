package com.mm.android.deviceaddmodule.p_softap;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.contract.BaseSoftApTipConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.base.DefaultPermissionListener;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.location.FuseLocationUtil;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
/**
 * Soft AP adds the boot prompt page base class
 *
 * 软AP添加引导提示页基类
 */
public abstract class BaseTipSoftApFragment extends BaseTipFragment implements BaseSoftApTipConstract.View {
    BaseSoftApTipConstract.Presenter mPresenter;
    DeviceAddInfo deviceAddInfo;
    protected abstract void gotoNextSoftApTipPage();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.LAN.name())) {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
        } else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        }
    }

    @Override
    protected void nextAction() {

        if(mPresenter.isLastTipPage()){
            mPresenter.verifyWifiOrLocationPermission();
        } else {
            gotoNextSoftApTipPage();
        }
    }

    @Override
    protected void helpAction() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_RESET);
    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }

    protected void initView(View view) {
        super.initView(view);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mTipImg.getLayoutParams();
        params.height= /*RelativeLayout.LayoutParams.WRAP_CONTENT*/UIUtils.dp2px(getContextInfo(), 300);
        params.width=RelativeLayout.LayoutParams.MATCH_PARENT;
        params.setMargins(0,0,0,0);
        mTipImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    public void updateTipImage(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl))
             Glide.with(BaseTipSoftApFragment.this.getContext()).load(imageUrl).into(mTipImg);
    }

    @Override
    public void updateTipTxt(String tipInfo) {
        if (!TextUtils.isEmpty(tipInfo)) {
            mTipTxt.setText(tipInfo);
        } else {
            mTipTxt.setText(R.string.add_device_operation_by_instructions);
        }
    }

    @Override
    public void updateResetTxt(String resetTxt) {
        if (TextUtils.isEmpty(resetTxt)) {
            mHelpTxt.setVisibility(View.GONE);
        } else {
            mHelpTxt.setVisibility(View.VISIBLE);
            mHelpTxt.setText(resetTxt);
        }
    }


    @Override
    public void goErrorTipPage() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.SOFTAP_ERROR_CONNECT_HOT_FAILED);
    }

    @Override
    public void gotoSoftApTipConnectWifiPage() {
        PageNavigationHelper.gotoSoftApTipConnectWifiPage(this,false);
    }

    @Override
    public void applyLocationPermission() {
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
            gotoSoftApTipConnectWifiPage();
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
}
