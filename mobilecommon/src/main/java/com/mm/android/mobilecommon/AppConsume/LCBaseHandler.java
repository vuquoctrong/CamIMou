package com.mm.android.mobilecommon.AppConsume;

import android.os.Message;

import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.base.mvp.IBaseView;

import java.lang.ref.WeakReference;

public abstract class LCBaseHandler<T extends IBaseView> extends LCBusinessHandler {

    WeakReference<T> mView;
    public LCBaseHandler(WeakReference<T> view){
        super();
        mView=view;
    }
    public abstract void onStart();
    protected abstract void onCompleted();
    protected abstract void handleBusinessFinally(Message msg);

    @Override
    public void handleBusiness(Message msg) {
            if(mView==null
                    || mView.get()==null
                    ||!mView.get().isViewActive()){
                return;
            }
            onCompleted();
            handleBusinessFinally(msg);
        }

}
