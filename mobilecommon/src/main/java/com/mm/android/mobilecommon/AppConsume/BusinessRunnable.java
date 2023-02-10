package com.mm.android.mobilecommon.AppConsume;

import android.os.Handler;

import com.mm.android.mobilecommon.businesstip.BusinessErrorCode;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.utils.LogUtil;

import java.lang.ref.WeakReference;

public abstract class BusinessRunnable implements Runnable {

    private WeakReference<Handler> mHandler;

    public BusinessRunnable(Handler handle) {
        mHandler = new WeakReference<>(handle);
        Handler handler = getHander();
        if (handler != null && handler instanceof LCBaseHandler) {
            ((LCBaseHandler) handler).onStart();
        }
        ThreadPool.submit(this);
    }

    public Handler getHander() {
        return mHandler.get();
    }

    @Override
    public void run() {
        try {
            doBusiness();
        } catch (BusinessException e) {
            Handler handler = getHander();
            LogUtil.debugLog("BusinessRunnable", "hander == null ? " + (handler == null));
            if (handler != null) {
                handler.obtainMessage(HandleMessageCode.HMC_EXCEPTION,
                        e.errorCode, e.errorCode, e).sendToTarget();

            }

        } catch (Exception e) {
            LogUtil.debugLog("HsviewResponse Exception", e.getMessage());
            Handler handler = getHander();
            if (handler != null) {
                handler.obtainMessage(HandleMessageCode.HMC_EXCEPTION,
                        BusinessErrorCode.BEC_COMMON_UNKNOWN,
                        BusinessErrorCode.BEC_COMMON_UNKNOWN,new BusinessException(e)).sendToTarget();
            }
        }
    }

    public abstract void doBusiness() throws BusinessException;
}