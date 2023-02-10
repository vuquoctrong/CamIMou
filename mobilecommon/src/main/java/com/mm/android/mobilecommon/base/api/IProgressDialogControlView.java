package com.mm.android.mobilecommon.base.api;

import android.content.DialogInterface;

import androidx.annotation.LayoutRes;

/**
 * Interface that controls the display wait box
 *
 * 控制显示等待框的接口
 */

public interface IProgressDialogControlView {

    /**
     * Displays wait by id
     * @param layoutId  Layout Id
     *
     * 根据 id 显示 等待
     * @param layoutId  布局Id
     */
    void showProgressDialog(@LayoutRes int layoutId);
    /**
     * Hide waiting for
     *
     * 隐藏等待
     */
    void dissmissProgressDialog();

    /**
     * cancelling Dialog
     *
     * 取消弹框
     */
    void cancleProgressDialog();

    /**
     * Can the Settings be cancelled
     * @param flag  Boolean value
     *
     * 设置是否可以取消
     * @param flag  布尔值
     */
   void setProgressDialogCancelable(boolean flag);

   void setProgressDialogCancelListener(DialogInterface.OnCancelListener cancelListener);

}
