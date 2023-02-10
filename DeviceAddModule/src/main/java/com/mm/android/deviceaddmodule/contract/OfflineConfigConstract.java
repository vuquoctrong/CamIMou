package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface OfflineConfigConstract {
    interface Presenter extends IBasePresenter {
        void resetCache();

        /**
         * Obtain device information from a service
         * @param deviceSn  Device Serial Number
         * @param deviceModelName  Device model name
         *
         * 从服务获取设备信息
         * @param deviceSn  设备序列号
         * @param deviceModelName  设备模型名称
         */
        void getDeviceInfo(String deviceSn, String deviceModelName);
    }

    interface View extends IBaseView<Presenter> {
        void onGetDeviceInfoError();
    }
}
