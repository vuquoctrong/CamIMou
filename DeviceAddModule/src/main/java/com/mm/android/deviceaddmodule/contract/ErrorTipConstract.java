package com.mm.android.deviceaddmodule.contract;

import androidx.fragment.app.Fragment;
import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;

public interface ErrorTipConstract {
    interface Presenter extends IBasePresenter {
        void dispatchError(int errorCode);

        /**
         * Universal error page, and no buttons
         * @return boolean Boolean value
         *
         * 通用错误页，及没有按钮
         * @return boolean 布尔值
         */
        boolean isResetPage();

        /**
         * Whether to bind the prompt page for the device
         * @return boolean Boolean value
         *
         * 是否为设备绑定提示页
         * @return boolean 布尔值
         */
        boolean isUserBindTipPage();

        /**
         * A message is displayed indicating that the device is bound to another user
         * @return boolean Boolean value
         *
         * 绑定时提示设备被其他用户绑定
         * @return boolean 布尔值
         */
        boolean isUserBindTipPageByBind();
    }

    interface View extends IBaseView<Presenter> {
        Fragment getParent();
        void updateInfo(String info,String img,boolean isNeedMatch);
        void updateInfo(int infoId,int tip2Id,String img,boolean isNeedMatch);
        void updateInfo(int infoId,String img,boolean isNeedMatch);
        void hideTipTxt();
        void hideHelp();

    }
}
