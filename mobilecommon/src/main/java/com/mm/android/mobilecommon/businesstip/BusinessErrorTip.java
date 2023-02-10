package com.mm.android.mobilecommon.businesstip;

import android.os.Message;
import android.text.TextUtils;

import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.openapi.HttpSend;

public class BusinessErrorTip {

	public static String getErrorTip(Message message) {
		String errorDesc = "";

		if (message.obj != null && message.obj instanceof BusinessException) {
			BusinessException exception = (BusinessException)message.obj;
			if (!TextUtils.isEmpty(exception.errorDescription)) {
				errorDesc = exception.errorDescription;
			}
		}

		return errorDesc;
	}

	public static Throwable throwError(Message message) {
		BusinessException exception=new BusinessException();
		if (message.obj != null && message.obj instanceof BusinessException) {
			 exception = (BusinessException)message.obj;
		}
		if(exception.errorCode == HttpSend.NET_ERROR_CODE){
			return new Throwable(exception.errorCode + "");
		}else {
			return new Throwable(exception.errorDescription);
		}
	}

}
