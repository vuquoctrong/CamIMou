package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface TypeChooseConstract {
    interface Presenter extends IBasePresenter {
        void getDeviceInfoSync(String deviceModelName);
        void checkDevIntroductionInfo(String deviceModelName);

        /**
         * Go back to the device selection page and clear the device password cache
         *
         * 回到设备选择页，清空设备密码缓存
         */
        void resetDevPwdCache();
    }

    interface View extends IBaseView<Presenter> {
        void showSearchError();
    }
}
