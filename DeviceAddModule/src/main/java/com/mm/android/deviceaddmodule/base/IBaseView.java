package com.mm.android.deviceaddmodule.base;

import android.content.Context;

/**
 * MVP mode V layer interface
 *
 * MVP模式V层接口
 **/
public interface IBaseView<T extends IBasePresenter> {
    Context getContextInfo();
    boolean isViewActive();            //View层是否处于活动状态
    //Toast
    void showToastInfo(String msg);
    void showToastInfo(int msgId);
    void showProgressDialog();
    void cancelProgressDialog();
}
