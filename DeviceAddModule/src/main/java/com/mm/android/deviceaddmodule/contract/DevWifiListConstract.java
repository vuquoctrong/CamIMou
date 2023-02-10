package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;
import com.lechange.opensdk.searchwifi.WlanInfo;

import java.util.List;

public interface DevWifiListConstract {
    interface Presenter extends IBasePresenter {
        boolean isDevSupport5G();
        void getWifiList();
    }

    interface View extends IBaseView<Presenter> {
        void updateWifiList(List<WlanInfo> list);
        void goWifiPwdPage(WlanInfo wlanInfo, boolean isNotNeedLogin);
        void goHiddenWifiPwdPage(boolean isNotNeedLogin);
        void goDevLoginPage();
        void showListView();
        void showErrorInfoView();
    }
}
