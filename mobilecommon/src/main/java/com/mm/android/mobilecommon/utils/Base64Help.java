package com.mm.android.mobilecommon.utils;

import android.util.Base64;

/**
 * Function description：
 * Copyright notice：
 *
 * 功能说明：
 * 版权申明：
 */

public class Base64Help {
	public static String encode(byte[] bytes){
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}
	
	public static byte[] decode(String str) {
		return Base64.decode(str, Base64.DEFAULT);
	}
}
