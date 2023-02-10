package com.mm.android.mobilecommon.base;

import android.content.Intent;

public interface IActivityResultDispatch {
    public interface OnActivityResultListener {
        /**
         * callBack
         * @param requestCode requestCode
         * @param resultCode  resultCode
         * @param data        data
         *
         * 回调
         * @param requestCode 请求码
         * @param resultCode  结果码
         * @param data        返回数据
         */
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public void addOnActivityResultListener(OnActivityResultListener listener);

    public void removeOnActivityResultListener(OnActivityResultListener listener);
}
