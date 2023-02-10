package com.mm.android.deviceaddmodule.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.mobilecommon.base.BaseFragment;
import com.mm.android.mobilecommon.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseDevAddFragment extends BaseFragment {

    protected boolean isDestoryView;

    protected abstract void initView(View view);

    protected abstract void initData();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isDestoryView = false;
        LogUtil.debugLog("lcxw-fragment",getClass().getSimpleName()+ "--->onViewCreated");
        initView(view);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestoryView = true;
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if (getActivity() == null) return ;
        InputMethodManager im = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im.isActive() || getActivity().getCurrentFocus() != null) {
            im.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content)
                    .getWindowToken(), 0);
        }
    }

    public boolean isDestoryView() {
        return isDestoryView;
    }

    public Context getContextInfo() {
        return getActivity();
    }

    public boolean isViewActive() {
        return !isDestoryView();
    }

    public void showToastInfo(String msg) {
        toast(msg);
    }

    public void showToastInfo(int msgId) {
        toast(msgId);
    }

    public void showToastInfo(int msgId, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            toast(msg);
        } else {
            toast(msgId);
        }
    }

    public void showProgressDialog() {
        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SHOW_LOADING_VIEW_ACTION));
    }

    public void cancelProgressDialog() {
        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DISMISS_LOADING_VIEW_ACTION));
    }

    public void finish(){
        getActivity().finish();
    }
}
