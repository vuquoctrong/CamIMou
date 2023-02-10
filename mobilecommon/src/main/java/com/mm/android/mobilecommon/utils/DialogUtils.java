package com.mm.android.mobilecommon.utils;

import android.app.Activity;

import com.mm.android.mobilecommon.widget.LoadingDialog;


public class DialogUtils {
    static LoadingDialog mLoadingDialog;
    static Activity activity;

    public static void show(Activity mActivity) {
        if (mActivity.isFinishing()) {
            return;
        }
        if (activity != null) {
            if (activity == mActivity) {
                if (null != mLoadingDialog) {
                    if (mLoadingDialog.isShowing()) {
                        return;
                    }
                }
            }
        }
        activity = mActivity;
        if (null != mLoadingDialog) {
            dismiss();
        }
        mLoadingDialog = new LoadingDialog(mActivity);
        mLoadingDialog.show();
    }

    public static void dismiss() {
        if (null == mLoadingDialog) {
            return;
        }
        if (mLoadingDialog.isShowing()) {
            try {
                mLoadingDialog.dismissLoading();
                mLoadingDialog = null;
                activity = null;
            } catch (Exception e) {
                mLoadingDialog = null;
                activity = null;
            }
        }
    }
}
