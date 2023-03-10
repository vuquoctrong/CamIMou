package com.opensdk.devicedetail.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.mm.android.deviceaddmodule.LCDeviceEngine;
import com.mm.android.deviceaddmodule.device_wifi.CurWifiInfo;
import com.mm.android.deviceaddmodule.device_wifi.DeviceConstant;
import com.mm.android.mobilecommon.entity.device.LCDevice;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.utils.DialogUtils;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.opensdk.devicedetail.dialog.CommonDialog;
import com.opensdk.devicedetail.entity.DevSdStoreData;
import com.opensdk.devicedetail.entity.DeviceDetailListData;
import com.opensdk.devicedetail.R;
import com.opensdk.devicedetail.callback.IGetDeviceInfoCallBack;
import com.opensdk.devicedetail.constance.MethodConst;
import com.opensdk.devicedetail.entity.DeviceUnBindData;
import com.opensdk.devicedetail.manager.DetailInstanceManager;
import com.opensdk.devicedetail.net.DeviceDetailService;
import com.opensdk.devicedetail.tools.GsonUtils;

import java.text.DecimalFormat;

import static android.app.Activity.RESULT_OK;
import static com.mm.android.deviceaddmodule.device_wifi.DeviceConstant.IntentCode.DEVICE_SETTING_WIFI_LIST;

public class DeviceDetailMainFragment extends Fragment implements View.OnClickListener, IGetDeviceInfoCallBack.IUnbindDeviceCallBack, IGetDeviceInfoCallBack.IModifyDeviceName  {
    private static final String TAG = DeviceDetailMainFragment.class.getSimpleName();
    private static final float OFFLINE_ALPHA = 0.5f;//?????????????????????

    private RelativeLayout rlDeviceDetail;
    private RelativeLayout rlDetailVersion;
    private RelativeLayout rlDeployment;
    private RelativeLayout rlDetele;
    private RelativeLayout rlSdcard;
    private RelativeLayout rlIvNext;
    private RelativeLayout rlCloud;

    private TextView tvDeviceName;
    private ImageView ivDevicePic;
    private TextView tvDeviceVersion;
    private Bundle arguments;
    private DeviceDetailListData.ResponseData.DeviceListBean deviceListBean;
    private DeviceDetailActivity deviceDetailActivity;
    private DeviceDetailService deviceDetailService;
    private CurWifiInfo wifiInfo;
    private TextView tvCurrentWifi;
    private RelativeLayout rlCurWifi;
    private IGetDeviceInfoCallBack.IModifyDeviceName modifyNameListener;
    private String fromWhere;
    private TextView tvDeploymentTip;
    private TextView tvStatus;
    private TextView tvUseSpace;
    private TextView tvSdCard;
    private TextView tvStoreTip;
    private TextView tvDeviceSettingTip;
    private ProgressBar pbStore;
    private ImageView ivBtnCloud;


    public static DeviceDetailMainFragment newInstance() {
        DeviceDetailMainFragment fragment = new DeviceDetailMainFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceDetailActivity = (DeviceDetailActivity) getActivity();
        deviceDetailActivity.llOperate.setVisibility(View.GONE);
        arguments = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_detail_main_new, container, false);
    }

    public void setModifyNameListener(IGetDeviceInfoCallBack.IModifyDeviceName modifyNameListener) {
        this.modifyNameListener = modifyNameListener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rlSdcard = view.findViewById(R.id.rl_sdcard);
        rlDeviceDetail = view.findViewById(R.id.rl_device_detail);
        rlDetailVersion = view.findViewById(R.id.rl_detail_version);
        rlDeployment = view.findViewById(R.id.rl_deployment);
        tvDeploymentTip = view.findViewById(R.id.tv_deployment_tip);
        rlDetele = view.findViewById(R.id.rl_detele);
        tvDeviceName = view.findViewById(R.id.tv_device_name);
        ivDevicePic = view.findViewById(R.id.iv_device_pic);
        tvDeviceVersion = view.findViewById(R.id.tv_device_version);
        tvCurrentWifi = view.findViewById(R.id.tv_current_wifi);
        rlCurWifi = view.findViewById(R.id.rl_cur_wifi);
        rlCloud = view.findViewById(R.id.rl_cloud);

        rlIvNext = view.findViewById(R.id.rl_iv_next);
        tvStatus = view.findViewById(R.id.tv_status);
        tvUseSpace = view.findViewById(R.id.tv_use_space);
        pbStore = view.findViewById(R.id.pb_store);
        tvSdCard = view.findViewById(R.id.tv_sd_card);
        ivBtnCloud = view.findViewById(R.id.iv_btn_cloud);
        tvStoreTip = view.findViewById(R.id.tv_store_tip);
        tvDeviceSettingTip = view.findViewById(R.id.tv_device_setting_tip);

        rlSdcard.setOnClickListener(this);
        rlDeployment.setOnClickListener(this);
        rlDetele.setOnClickListener(this);
        rlDeviceDetail.setOnClickListener(this);
        rlCurWifi.setOnClickListener(this);
        ivBtnCloud.setOnClickListener(this);
        rlDetailVersion.setOnClickListener(this);

        DeviceDetailActivity deviceDetailActivity = (DeviceDetailActivity) getActivity();
        deviceDetailActivity.tvTitle.setText(getResources().getString(R.string.lc_demo_device_detail_title));
        if (arguments == null) {
            return;
        }
        String deviceListStr = arguments.getString(MethodConst.ParamConst.deviceDetail);
        if (TextUtils.isEmpty(deviceListStr)) {
            return;
        }
        deviceListBean = GsonUtils.fromJson(deviceListStr, DeviceDetailListData.ResponseData.DeviceListBean.class);
        //????????? ???????????????
        fromWhere = arguments.getString(MethodConst.ParamConst.fromList);
        if (deviceListBean == null) {
            return;
        }
        deviceDetailService = DetailInstanceManager.newInstance().getDeviceDetailService();
        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 1) {
            //????????????????????????  ???????????????????????????????????????????????????
            rlDeployment.setVisibility(View.GONE);
            tvDeploymentTip.setVisibility(View.GONE);
            rlCloud.setVisibility(View.GONE);
            rlCurWifi.setVisibility(View.GONE);
            tvDeviceSettingTip.setVisibility(View.GONE);
            if (MethodConst.ParamConst.fromList.equals(fromWhere)) {
                //????????????
                tvDeviceName.setText(deviceListBean.deviceName);
                tvDeviceVersion.setText(deviceListBean.deviceVersion);
            } else {
                //????????????   ??????????????????????????????
                tvDeviceName.setText(deviceListBean.deviceName);
                tvDeviceVersion.setText(deviceListBean.deviceVersion);
            }
        } else if (deviceListBean.channelList != null && deviceListBean.channelList.size() == 1) {
            //?????????
            tvDeviceName.setText(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName);
            getDeviceLocalCache();
            tvDeviceVersion.setText(deviceListBean.deviceVersion);
            if (deviceListBean.deviceSource == 2) {
                rlDetele.setVisibility(View.GONE);
            }
        } else {
            //????????????????????????????????? ???????????????????????????????????????????????????
            tvDeviceName.setText(deviceListBean.deviceName);
            tvDeviceVersion.setText(deviceListBean.deviceVersion);
            rlDeployment.setVisibility(View.GONE);
            tvDeploymentTip.setVisibility(View.GONE);
            rlCloud.setVisibility(View.GONE);
            rlCurWifi.setVisibility(View.GONE);
            tvDeviceSettingTip.setVisibility(View.GONE);
        }

    }

    /**
     * Obtain device cache information
     *
     * ????????????????????????
     */
    private void getDeviceLocalCache() {
//        DeviceLocalCacheData deviceLocalCacheData = new DeviceLocalCacheData();
//        deviceLocalCacheData.setDeviceId(deviceListBean.deviceId);
//        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 0) {
//            deviceLocalCacheData.setChannelId(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId);
//        }
//        DeviceLocalCacheService deviceLocalCacheService = ClassInstanceManager.newInstance().getDeviceLocalCacheService();
//        deviceLocalCacheService.findLocalCache(deviceLocalCacheData, this);
    }

    private void getCurrentWifiInfo() {
        deviceDetailService.currentDeviceWifi(deviceListBean.deviceId, new IGetDeviceInfoCallBack.IDeviceCurrentWifiInfoCallBack() {
            @Override
            public void deviceCurrentWifiInfo(CurWifiInfo curWifiInfo) {
                if (!isAdded() || curWifiInfo == null) {
                    return;
                }
                rlCurWifi.setVisibility(View.VISIBLE);
                if (curWifiInfo.isLinkEnable()) {
                    wifiInfo = curWifiInfo;
                    tvCurrentWifi.setText(wifiInfo.getSsid());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                DeviceDetailMainFragment.this.onError(throwable);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void gotoModifyNamePage(FragmentActivity fragmentActivity) {
        if (fragmentActivity == null || fragmentActivity.getSupportFragmentManager() == null) {
            return;
        }
        DeviceDetailNameFragment fragment = DeviceDetailNameFragment.newInstance();
        fragment.setArguments(arguments);
        fragment.setModifyNameListener(this);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.hide(this).add(R.id.fr_content, fragment).addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void gotoUpdatePage(FragmentActivity fragmentActivity) {
        if (fragmentActivity == null || fragmentActivity.getSupportFragmentManager() == null) {
            return;
        }
        com.opensdk.devicedetail.ui.DeviceDetailVersionFragment fragment = com.opensdk.devicedetail.ui.DeviceDetailVersionFragment.newInstance();
        fragment.setArguments(arguments);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.hide(this).add(R.id.fr_content, fragment).addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void gotoDeploymentPage(FragmentActivity fragmentActivity) {
        if (fragmentActivity == null || fragmentActivity.getSupportFragmentManager() == null) {
            return;
        }
        DeviceDetailDeploymentFragment fragment = DeviceDetailDeploymentFragment.newInstance();
        fragment.setArguments(arguments);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.hide(this).add(R.id.fr_content, fragment).addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void gotoSdCardPage(FragmentActivity fragmentActivity) {
        if (fragmentActivity == null || fragmentActivity.getSupportFragmentManager() == null) {
            return;
        }
        SdCardFragment fragment = SdCardFragment.Companion.newInstance();
        fragment.setArguments(arguments);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.hide(this).add(R.id.fr_content, fragment).addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_device_detail) {
            gotoModifyNamePage(getActivity());
        } else if (id == R.id.rl_cur_wifi) {
            LCDevice device = new LCDevice();
            device.setDeviceId(deviceListBean.deviceId);
            device.setName(deviceListBean.deviceName);
            device.setStatus(deviceListBean.getDeviceStatus());
            LCDeviceEngine.newInstance().deviceOnlineChangeNet(getActivity(), device, wifiInfo);
        } else if (id == R.id.rl_deployment) {
            gotoDeploymentPage(getActivity());
        } else if (id == R.id.rl_detail_version) {
            gotoUpdatePage(getActivity());
        } else if (id == R.id.rl_detele) {
            //????????????
            deleteDevice();
        } else if (id == R.id.rl_sdcard) {
            gotoSdCardPage(getActivity());
        } else if (id == R.id.iv_btn_cloud) {
            if ((boolean) ivBtnCloud.getTag()) {
                setAllStorageStrategy("off");
            } else
                setAllStorageStrategy("on");
        }
    }
    //    @Override
//    public void deviceCache(DeviceLocalCacheData deviceLocalCacheData) {
//        if (!isAdded()) {
//            return;
//        }
//        BitmapDrawable bitmapDrawable = MediaPlayHelper.picDrawable(deviceLocalCacheData.getPicPath());
//        if (bitmapDrawable != null) {
//            ivDevicePic.setImageDrawable(bitmapDrawable);
//        }
//    }
    /**
     * Delete Device
     *
     * ????????????
     */
    public void deleteDevice(){
        CommonDialog dialog = new CommonDialog.Builder(getActivity())
                .setTitle(R.string.device_delete_tip)
                .setMessage(R.string.device_delete_confirm_msg)
                .setGravity2(Gravity.CENTER)
                .setCancelButton(R.string.common_cancel, null)
                .setConfirmButton(R.string.common_confirm, (new com.opensdk.devicedetail.dialog.CommonDialog.OnClickListener() {
                    public final void onClick(CommonDialog dialog, int which, boolean isChecked) {
                        DeviceDetailService deviceDetailService = DetailInstanceManager.newInstance().getDeviceDetailService();
                        DialogUtils.show(getActivity());
                            /*
                              DeviceUnBindData deviceUnBindData = new DeviceUnBindData();
                              deviceUnBindData.data.deviceId = deviceListBean.deviceId;
                              deviceDetailService.unBindDevice(deviceUnBindData, this);
                              */

                        deviceDetailService.deletePermission(deviceListBean.deviceId, null, DeviceDetailMainFragment.this);
                    }
                }))
                .create();
        dialog.show(getChildFragmentManager(), null);
    }
    @Override
    public void unBindDevice(boolean result) {
        if (!isAdded()) {
            return;
        }
        DeviceUnBindData deviceUnBindData = new DeviceUnBindData();
        deviceUnBindData.data.deviceId = deviceListBean.deviceId;
        deviceDetailService.unBindDevice(deviceUnBindData, this);
        DialogUtils.dismiss();
        Toast.makeText(getContext(), getResources().getString(R.string.lc_demo_device_unbind_success), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra(DeviceConstant.IntentKey.LCDEVICE_UNBIND, true);
        deviceDetailActivity.setResult(RESULT_OK, intent);
        deviceDetailActivity.finish();
    }

    @Override
    public void onError(Throwable throwable) {
        if (!isAdded()) {
            return;
        }
        deviceDetailActivity.rlLoading.setVisibility(View.GONE);
        LogUtil.errorLog(TAG, "error", throwable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEVICE_SETTING_WIFI_LIST && resultCode == RESULT_OK && data != null) {
            CurWifiInfo curWifiInfo = (CurWifiInfo) data.getSerializableExtra(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO);
            if (curWifiInfo != null) {
                wifiInfo = curWifiInfo;
                tvCurrentWifi.setText(wifiInfo.getSsid());
            }
        }
    }

    @Override
    public void deviceName(String newName) {
        tvDeviceName.setText(newName);
        //?????????????????????
//        if (deviceListBean.channelList.size() == 0 || (deviceListBean.channelList.size() > 1 && MethodConst.ParamConst.fromList.equals(fromWhere))) {
        if (deviceListBean.channelList.size() == 0 || deviceListBean.channelList.size() > 1) {
            deviceListBean.deviceName = newName;
        } else {
            deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName = newName;
        }
        arguments.putString(MethodConst.ParamConst.deviceDetail,GsonUtils.toJson(deviceListBean));
        if (modifyNameListener != null) {
            modifyNameListener.deviceName(newName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //??????????????????
        showDeviceStatus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden) {
            //??????????????????
            showDeviceStatus();
        }
    }

    /**
     * Show Device Status
     *
     * ??????????????????
     */
    public void showDeviceStatus() {
        if (deviceListBean.channelList != null && deviceListBean.channelList.size() > 1) {//?????????
//          if("offline".equals(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelStatus)){//????????????  ?????????????????????????????????
            if ("offline".equals(deviceListBean.getDeviceStatus())) {//????????????   ????????????????????????NVR????????????(??????)
                showOfflineView();
            } else {
                getSdCardInfo();//??????Sd?????????
            }
        } else if (deviceListBean.channelList != null && deviceListBean.channelList.size() == 1) {//?????????
            showSingleChannelStatus();//????????????????????????????????????/?????????
        } else {//????????????????????????????????????
            if ("offline".equals(deviceListBean.getDeviceStatus())) {//????????????
                showOfflineView();
            } else {
                getSdCardInfo();//??????Sd?????????
            }
        }
    }

    /**
     * Display single channel device status (online/offline)
     *
     * ????????????????????????????????????/?????????
     */
    public void showSingleChannelStatus() {
        //?????????????????????
        if (!(deviceListBean!=null && "offline".equals(deviceListBean.getDeviceStatus()))) {
            getCurrentWifiInfo();
            getDeviceCloudInfo();//???????????????????????????
            getSdCardInfo();//??????Sd?????????
        } else {
            getDeviceCloudInfo();//???????????????????????????
            showOfflineView();//????????????
        }
    }

    /**
     * Obtain Sd card status information
     *
     * ??????Sd???????????????
     */
    public void getSdCardInfo() {
        deviceDetailService.getDeviceSdCardStatus(deviceListBean.deviceId, new IGetDeviceInfoCallBack.ICommon<String>() {
            @Override
            public void onCommonBack(String status) {
                if (!isAdded()) {
                    return;
                }
                rlSdcard.setAlpha(1f);
                rlSdcard.setClickable(true);
                tvSdCard.setText(getResources().getString(R.string.sd_cards));
                rlIvNext.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.VISIBLE);
                switch (status) {
                    case "normal":
                        tvStatus.setText(getResources().getString(R.string.sd_common));
                        tvStatus.setTextColor(getResources().getColor(R.color.lc_demo_color_8f8f8f));
                        getDeviceStoreInfo();
                        break;
                    case "recovering": //?????????????????????????????????????????????
                        tvStatus.setText(getResources().getString(R.string.sd_common));
                        tvStatus.setTextColor(getResources().getColor(R.color.lc_demo_color_8f8f8f));
                        getDeviceStoreInfo();
                        break;
                    case "abnormal":
                        tvUseSpace.setVisibility(View.INVISIBLE);
                        tvStatus.setText(getResources().getString(R.string.sd_uncommon));
                        tvStatus.setTextColor(getResources().getColor(R.color.lc_demo_color_FF4F4F));
                        break;
                    case "empty":
                        rlSdcard.setAlpha(OFFLINE_ALPHA);
                        rlIvNext.setVisibility(View.INVISIBLE);
                        rlSdcard.setClickable(false);
                        tvSdCard.setText(getResources().getString(R.string.sd_no_cards));
                        tvStatus.setVisibility(View.INVISIBLE);
                        tvUseSpace.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
                //???????????????SD??????????????????
                rlSdcard.setAlpha(OFFLINE_ALPHA);
                rlIvNext.setVisibility(View.INVISIBLE);
                rlSdcard.setClickable(false);
                tvSdCard.setText(getResources().getString(R.string.sd_no_cards));
                tvStatus.setVisibility(View.INVISIBLE);
                tvUseSpace.setVisibility(View.INVISIBLE);
                DeviceDetailMainFragment.this.onError(throwable);
            }
        });
    }

    /**
     * Obtain device storage information
     *
     * ????????????????????????
     */
    public void getDeviceStoreInfo() {
        deviceDetailService.getDeviceStoreInfo(deviceListBean.deviceId, new IGetDeviceInfoCallBack.ICommon<DevSdStoreData>() {
            @Override
            public void onCommonBack(DevSdStoreData devSdStoreData) {
                if (devSdStoreData.getTotalBytes() == null || devSdStoreData.getUsedBytes() == null) {
                    return;
                }
                if (!isAdded()) {
                    return;
                }
                long to = Long.parseLong(devSdStoreData.getTotalBytes());
                long us = Long.parseLong(devSdStoreData.getUsedBytes());
                long progress=0;
                if(to!=0 && us<=to) {
                    progress = (us * 100) / to;
                }
                updatePbStore(to, us, (int) progress);
            }
            @Override
            public void onError(Throwable throwable) {
                DeviceDetailMainFragment.this.onError(throwable);
            }
        });
    }

    /**
     * Obtain cloud storage information
     *
     * ?????????????????????
     */
    public void getDeviceCloudInfo() {
        ivBtnCloud.setTag(false);
        deviceDetailService.getDeviceCloudStatus(deviceListBean.deviceId, deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId,
                new IGetDeviceInfoCallBack.ICommon<Integer>() {
                    @Override
                    public void onCommonBack(Integer status) {
                        if (!isAdded()) {
                            return;
                        }
                        if (status == 1) {//-1???????????????0????????????1???????????????2?????????
                            ivBtnCloud.setTag(true);
                            ivBtnCloud.setImageResource(R.drawable.icon_cloud_btn_on);
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        DeviceDetailMainFragment.this.onError(throwable);
                    }
                });
    }

    /**
     * Set the cloud storage service switch of the current device
     *
     * ??????????????????????????????????????????
     */
    public void setAllStorageStrategy(String status) {
        if((Boolean) ivBtnCloud.getTag()){//???????????? "??????"
            ivBtnCloud.setTag(false);
            ivBtnCloud.setImageResource(R.drawable.icon_cloud_btn_off);
        }else {// "??????" ??????
            ivBtnCloud.setTag(true);
            ivBtnCloud.setImageResource(R.drawable.icon_cloud_btn_on);
        }
        deviceDetailService.setAllStorageStrategy(deviceListBean.deviceId, deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId, status,
                new IGetDeviceInfoCallBack.ICommon<Object>() {
                    @Override
                    public void onCommonBack(Object result) {
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        if (!isAdded()) {
                            return;
                        }
                        if((Boolean) ivBtnCloud.getTag()){//???????????? "??????"
                            ivBtnCloud.setTag(false);
                            ivBtnCloud.setImageResource(R.drawable.icon_cloud_btn_off);
                        }else {// "??????" ??????
                            ivBtnCloud.setTag(true);
                            ivBtnCloud.setImageResource(R.drawable.icon_cloud_btn_on);
                        }
                        if(TextUtils.equals(throwable.getMessage(),String.valueOf(HttpSend.NET_ERROR_CODE))){
                            Toast.makeText(getContext(), R.string.mobile_common_operate_fail, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        DeviceDetailMainFragment.this.onError(throwable);
                    }
                });
    }

    /**
     * Update the progress bar and storage information
     * @param total  Total Storage
     * @param used  Used Storage
     * @param progress  Progress
     *
     * ??????????????????????????????
     * @param total  ?????????
     * @param used  ???????????????
     * @param progress  ??????
     */
    public void updatePbStore(long total, long used, int progress) {
        //???????????????
        pbStore.setProgress(progress);
        tvUseSpace.setText(String.format(getString(R.string.sd_use_space_gb), getSizeGB(used), getSizeGB(total)));//????????????used  ??????total
        tvUseSpace.setVisibility(View.VISIBLE);
    }

    /**
     * Convert "byte" to "GB" and retain two decimal places
     * @param num  Long Numbers
     *
     * ??????byte???????????????GB???,?????????????????????
     * @param num  Long????????????
     */
    public static String getSizeGB(long num) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format((float) num / (1024 * 1024 * 1024));
    }

    /**
     * View the offline state
     *
     * ??????????????????View
     */
    private void showOfflineView() {
        //????????????
        changeViewAlpha(rlDeviceDetail, OFFLINE_ALPHA);
        changeViewAlpha(rlDetailVersion, OFFLINE_ALPHA);
        changeViewAlpha(rlDeployment, OFFLINE_ALPHA);
        changeViewAlpha(rlSdcard, OFFLINE_ALPHA);
        changeViewAlpha(rlCurWifi, OFFLINE_ALPHA);

        //????????????????????????
        rlDeviceDetail.setClickable(false);
        rlDetailVersion.setClickable(false);
        rlDeployment.setClickable(false);
        rlSdcard.setClickable(false);
        rlCurWifi.setClickable(false);

        //SD?????????????????????
        rlIvNext.setVisibility(View.INVISIBLE);
        tvSdCard.setText(getResources().getString(R.string.sd_no_cards));
        tvStatus.setVisibility(View.INVISIBLE);
        tvUseSpace.setVisibility(View.INVISIBLE);
    }

    /**
     * Change the child View transparency
     * @param viewGroup  ViewGroup subclass
     * @param alpha  transparency
     *
     * ?????????View?????????
     * @param viewGroup  ViewGroup??????
     * @param alpha  ?????????
     */
    private void changeViewAlpha(ViewGroup viewGroup, Float alpha) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeViewAlpha((ViewGroup) child, alpha);
            } else {
                child.setAlpha(alpha);
            }
        }
    }
}
