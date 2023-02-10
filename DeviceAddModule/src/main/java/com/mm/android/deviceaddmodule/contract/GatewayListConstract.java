package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;
import com.mm.android.mobilecommon.entity.device.LCDevice;

import java.util.List;

public interface GatewayListConstract {
    interface Presenter extends IBasePresenter {
        List<LCDevice> getGatewayData(boolean selectedGateway);
        int gatewaySize();
        void dispatchCurSelect(int pos);
        int getSelectedpos();
    }

    interface View extends IBaseView<Presenter> {
        void goTipPage();
        void setApSn(String apSn);
        void setApImg(String img);
        void setSelectedPos(int pos);
    }
}
