package com.mm.android.deviceaddmodule.presenter;

import android.os.Message;
import android.text.TextUtils;

import com.dahua.mobile.utility.network.DHNetworkUtil;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.contract.BindSuccessConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.openapi.data.DeviceDetailListData;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.openapi.DeviceAddOpenApiManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BindSuccessPresenter implements BindSuccessConstract.Presenter {
    private static final String CHANNEL_INDEX = "0";
    WeakReference<BindSuccessConstract.View> mView;
    String mDevDefaultName;
    DeviceAddInfo mDeviceAddInfo;
    boolean mIsOverSea;

    public BindSuccessPresenter(BindSuccessConstract.View view) {
        mView = new WeakReference<>(view);
        initViewData();
    }

    private void initViewData() {
        mDeviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        mDevDefaultName = mDeviceAddInfo.getDeviceDefaultName();
        mView.get().setDevName(mDevDefaultName);
        updateDevImg();
        mIsOverSea = ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA;
    }

    private void updateDevImg() {
        DeviceIntroductionInfo deviceIntroductionInfo = mDeviceAddInfo.getDevIntroductionInfo();
        if (deviceIntroductionInfo != null && deviceIntroductionInfo.getImageInfos() != null) {
            HashMap<String, String> imageInfos = deviceIntroductionInfo.getImageInfos();
            String img = "";
            if (imageInfos.containsKey(DeviceAddHelper.OMSKey.WIFI_MODE_FINISH_DEVICE_IMAGE)) {
                img = imageInfos.get(DeviceAddHelper.OMSKey.WIFI_MODE_FINISH_DEVICE_IMAGE);
            } else if (imageInfos.containsKey(DeviceAddHelper.OMSKey.SOFT_AP_MODE_RESULT_PROMPT_IMAGE)) {
                img = imageInfos.get(DeviceAddHelper.OMSKey.SOFT_AP_MODE_RESULT_PROMPT_IMAGE);
            } else if (imageInfos.containsKey(DeviceAddHelper.OMSKey.LOCATION_MODE_FINISH_DEVICE_IMAGE)) {
                img = imageInfos.get(DeviceAddHelper.OMSKey.LOCATION_MODE_FINISH_DEVICE_IMAGE);
            } else if (imageInfos.containsKey(DeviceAddHelper.OMSKey.THIRD_PARTY_PLATFORM_MODE_RESULT_PROMPT_IMAGE)) {
                img = imageInfos.get(DeviceAddHelper.OMSKey.THIRD_PARTY_PLATFORM_MODE_RESULT_PROMPT_IMAGE);
            }
            mView.get().updateDevImg(img);
        }
    }

    @Override
    public void refreshDevice(final boolean isExit) {
        mView.get().completeAction();
    }

    @Override
    public void modifyDevName() {
        if (!DHNetworkUtil.isConnected(mView.get().getContextInfo())) {
            mView.get().showToastInfo(R.string.mobile_common_bec_common_network_exception);
            return;
        }

        final String sn = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
        String channelId = "0";
        // 国内单通道设备修改通道名称
        if (ProviderManager.getAppProvider().getAppType() != LCConfiguration.APP_LECHANGE_OVERSEA
                && DeviceAddModel.newInstance().getDeviceInfoCache().getChannelNum() == 1) {
            channelId = CHANNEL_INDEX;
        }
        final String devName = mView.get().getDevName();
        if (TextUtils.isEmpty(devName)) {
            mView.get().showToastInfo(R.string.device_manager_input_device_name);
            return;
        }

        mView.get().showProgressDialog();
        LCBusinessHandler modifyNameHandler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                if (mView.get() != null && mView.get().isViewActive()) {
                    mView.get().cancelProgressDialog();
                    if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                        refreshDevice(true);
                    } else {
                        mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                    }
                }
            }
        };
        DeviceAddModel.newInstance().modifyDeviceName(sn, channelId, devName, modifyNameHandler);
    }

    @Override
    public void getDevName() {
        mView.get().showProgressDialog();
        final LCBusinessHandler handler = new LCBusinessHandler() {
            @Override
            public void handleBusiness(Message msg) {
                mView.get().cancelProgressDialog();
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    //成功
                    DeviceDetailListData.Response response = (DeviceDetailListData.Response) msg.obj;
                    mView.get().deviceName(response.data.deviceList.get(0).deviceName);
                } else {
                    //失败
                    mView.get().showToastInfo(BusinessErrorTip.getErrorTip(msg));
                }
            }
        };
        new BusinessRunnable(handler) {
            @Override
            public void doBusiness() throws BusinessException {
                try {
                    //获取设备详情列表request参数封装
                    DeviceDetailListData deviceDetailListData = new DeviceDetailListData();
                    List<DeviceDetailListData.RequestData.DeviceListBean> deviceListBeans = new ArrayList<>();
                    DeviceDetailListData.RequestData.DeviceListBean deviceListBean = new DeviceDetailListData.RequestData.DeviceListBean();
                    deviceListBean.deviceId = DeviceAddModel.newInstance().getDeviceInfoCache().getDeviceSn();
                    deviceListBean.productId = DeviceAddModel.newInstance().getDeviceInfoCache().getProductId();
                    deviceListBeans.add(deviceListBean);
                    deviceDetailListData.data.deviceList = deviceListBeans;
                    //获取设备详情列表
                    DeviceDetailListData.Response result = DeviceAddOpenApiManager.listDeviceDetailsByIds(deviceDetailListData);
                    handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, result).sendToTarget();
                } catch (BusinessException e) {
                    throw e;
                }
            }
        };
    }
}
