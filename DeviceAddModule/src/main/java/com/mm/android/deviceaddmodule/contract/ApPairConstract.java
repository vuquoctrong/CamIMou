package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;
import com.mm.android.mobilecommon.entity.AddApResult;

public interface ApPairConstract {
    interface Presenter extends IBasePresenter {
        void pair();
        void stopPair();
    }


    interface View extends IBaseView<Presenter> {
        void goErrorTipPage();
        void goApBindSuccessPage(AddApResult addApResult);
    }
}
