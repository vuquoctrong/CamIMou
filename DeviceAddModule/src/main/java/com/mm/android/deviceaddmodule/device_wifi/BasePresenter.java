package com.mm.android.deviceaddmodule.device_wifi;

import android.content.Intent;
import android.os.Handler;

import com.mm.android.mobilecommon.base.mvp.IBasePresenter;
import com.mm.android.mobilecommon.base.mvp.IBaseView;
import com.mm.android.mobilecommon.common.HandlerManager;

import java.lang.ref.WeakReference;

/**
 * The base class of presenters from which all presenters inherit
 *
 * Presenter基类，所有Presenter继承自此类
 */
public abstract class BasePresenter<T extends IBaseView> implements IBasePresenter {
    protected WeakReference<T> mView;
    private HandlerManager mHandlerManager;
    public BasePresenter(T view) {
        mView = new WeakReference<>(view);
    }

    protected Handler addHandler(Handler handler){
        if(mHandlerManager == null){
            mHandlerManager = new HandlerManager();
        }
        return mHandlerManager.addHandler(handler);
    }

    private void clearHandlers(){
        if(mHandlerManager != null){
            mHandlerManager.clearHandlers();
        }
    }

    @Override
    public void unInit() {
        clearHandlers();
        mView.clear();
    }

    @Override
    public void dispatchIntentData(Intent intent) {

    }


}
