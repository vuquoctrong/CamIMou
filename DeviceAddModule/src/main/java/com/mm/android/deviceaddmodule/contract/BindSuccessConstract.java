package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;


public interface BindSuccessConstract {
    interface Presenter extends IBasePresenter {
        void refreshDevice(boolean isExit);

        void modifyDevName();

        void getDevName();
    }


    interface View extends IBaseView<Presenter> {

        void updateDevImg(String img);

        String getDevName();

        void setDevName(String name);

        void completeAction();

        void deviceName(String name);
    }
}
