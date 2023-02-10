package com.mm.android.mobilecommon.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mm.android.mobilecommon.businesstip.HandleMessageCode;
import com.mm.android.mobilecommon.utils.BusinessAuthUtil;
import com.mm.android.mobilecommon.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * File description: File name, class name Function Description: Copyright Statement:
 *
 * 文件描述：文件名、类名 功能说明： 版权申明：
 */

public abstract class BaseHandler extends Handler {

    private final static String TAG = "LeChange.BaseHandler";

    private AtomicBoolean isCancled = new AtomicBoolean(false);

    public BaseHandler(){
        super();
    }
    public BaseHandler(Looper looper){
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (!isCancled.get()) {
            if (msg.what == HandleMessageCode.HMC_EXCEPTION) {
                LogUtil.debugLog(TAG, "base hander throw exception. what =" + msg.what);

                if (BusinessAuthUtil.isAuthFailed(msg.arg1)) {
                    authError(msg);
                    return ;
                }
            }
            handleBusiness(msg);
        }
    }

    /**
     * Callback message
     * @param msg  message
     *
     * 回调信息
     * @param msg  信息
     */
    public abstract void handleBusiness(Message msg);

    /**
     * Authentication information fails
     *
     * 鉴权信息失败
     */
    public void authError(Message msg) {

    }

    /**
     * Cancels the data callback
     *
     * 取消数据回调
     */
    public void cancle() {
        isCancled.set(true);
    }

    /**
     * Whether to continue running
     * @return boolean  Boolean Value
     *
     * 是否继续运行
     * @return boolean  布尔值
     */
    public boolean isCanceled() {
        return isCancled.get();
    }
}
