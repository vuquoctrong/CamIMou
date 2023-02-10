package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface TimeoutConstract {
    interface Presenter extends IBasePresenter {
        void setErrorData(int errorCode, String timeoutDevtypeModel);

        void dispatchAction1();
    }

    interface View extends IBaseView<Presenter> {

        void showAView();

        void goScanPage();

    }
}
