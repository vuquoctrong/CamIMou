package com.mm.android.deviceaddmodule.presenter;

import com.mm.android.deviceaddmodule.contract.TimeoutConstract;

import java.lang.ref.WeakReference;

public class TimeoutPresenter implements TimeoutConstract.Presenter {
    WeakReference<TimeoutConstract.View> mView;
    int mErrorCode;
    String mDevTypeModel = "";

    public TimeoutPresenter(TimeoutConstract.View view) {
        mView = new WeakReference<>(view);
    }

    @Override
    public void setErrorData(int errorCode, String devTypeModel) {
        mErrorCode = errorCode;
        mDevTypeModel = devTypeModel;
        showTimeoutView();
    }

    private void showTimeoutView() {
        mView.get().showAView();
    }

    //按钮1事件
    @Override
    public void dispatchAction1() {
        mView.get().goScanPage();
    }
}
