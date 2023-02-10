package com.mm.android.mobilecommon.base;

import android.os.Looper;
import android.os.Message;

/**
 * Network request base class that handles logout services
 *
 * 网络请求基类，处理登出业务
 */
public abstract class LCBusinessHandler extends BaseHandler{
	public LCBusinessHandler(){
		super();
	}
	public LCBusinessHandler(Looper looper){
		super(looper);
	}
	@Override
	public void authError(Message msg) {
		super.authError(msg);
	}
}
