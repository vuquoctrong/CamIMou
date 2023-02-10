package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface HubApGuide1Constract  {
    interface Presenter extends IBasePresenter {
        void checkDevIntroductionInfo(String deviceModel);
    }


    interface View extends IBaseView<Presenter> {
        void updateTip(String tipImg, String tipTxt,String helpTxt);
        void showInfoView();
    }
}
