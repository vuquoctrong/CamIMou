package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;
import com.mm.android.mobilecommon.entity.AddApResult;

public interface ApBindSuccessConstract {
    interface Presenter extends IBasePresenter {
        void modifyApName();
        void setData(AddApResult addApResult);
    }


    interface View extends IBaseView<Presenter> {
        String getApName();
        void setApName(String name);
        void setApImg(String img);
        void completeAction();
    }
}
