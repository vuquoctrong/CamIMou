package com.mm.android.mobilecommon.base.mvp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.base.BaseFragment;
import com.mm.android.mobilecommon.eventbus.event.BaseEvent;


/**
 * Created by Wang_wenpeng on 2018/7/25
 * The MVP model fragment base class, inherited from the common module base class fragment,
 * implements the baseview and the interface, and all the snipps of the MVP model need to be inherited since.
 *
 * Created by Wang_wenpeng on 2018/7/25
 * MVP模式Fragment基类，继承自common模块基类Fragment，实现BaseView中通用接口，所有MVP模式的Fragment需继承自此类。
 **/
public abstract class BaseMvpFragment<T extends IBasePresenter> extends BaseFragment implements IBaseView {
    protected boolean isDestoryView;

    protected abstract void initData();           //初始化数据
    protected T mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXME: 2018/8/7  待调整，生命周期
//        isDestoryView = false;
//        initData();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isDestoryView = false;
        initPresenter();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestoryView = true;
        hideSoftKeyboard();
        if(mPresenter != null) mPresenter.unInit();
    }

    public void hideSoftKeyboard() {
        if(getActivity() == null || getActivity().findViewById(android.R.id.content) == null) {
            return;
        }
        InputMethodManager im = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im.isActive() || getActivity().getCurrentFocus() != null) {
            im.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content)
                    .getWindowToken(), 0);
        }
    }
    @Override
    public Context getContextInfo() {
        return getActivity();
    }

    @Override
    public boolean isViewActive() {
        return !isDestoryView && getActivity() != null && isAdded();
    }

    @Override
    public void showToastInfo(String msg) {
        toast(msg);
    }

    @Override
    public void showToastInfo(int msgId) {
        toast(msgId);
    }

    @Override
    public void showProgressDialog() {
        showProgressDialog(R.layout.mobile_common_progressdialog_layout);
    }

    public void cancelProgressDialog() {
        dissmissProgressDialog();
    }

    @Override
    public void onMessageEvent(BaseEvent event) {

    }

    public void showToastInfo(int msgId, String errorDesc) {
        if (!TextUtils.isEmpty(errorDesc)) {
            toast(errorDesc);
        } else {
            toast(msgId);
        }
    }

    @Override
    public void finish() {
        if(getActivity() != null){
            getActivity().finish();
        }
    }

    @Override
    public void unInit() {

    }
}
