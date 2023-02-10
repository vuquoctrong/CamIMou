package com.example.usermodule.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mm.android.mobilecommon.openapi.CONST;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;



import java.util.HashMap;

public class UserApiManager {

	private static int TIME_OUT = 10 * 1000;
	private static int TOKEN_TIME_OUT = 4 * 1000;
	private static int DMS_TIME_OUT = 45 * 1000;

	public static String getSubAccountToken(String openId) throws BusinessException {
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("token", TokenHelper.getInstance().accessToken);
		paramsMap.put("openid", openId);
		JsonObject json = HttpSend.execute(paramsMap, CONST.SUB_ACCOUNT_TOKEN, TIME_OUT);
		return json.get("accessToken").getAsString();
	}

	public static String createSubAccountToken(String userName) throws BusinessException {
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("token", TokenHelper.getInstance().accessToken);
		paramsMap.put("account", userName);
		JsonObject json = HttpSend.execute(paramsMap, CONST.CREATE_SUB_ACCOUNT, TIME_OUT);
		return json.get("openid").getAsString();
	}


	public static String getUserOpenIdByAccout(String userName) throws BusinessException {
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("token", TokenHelper.getInstance().accessToken);
		paramsMap.put("account", userName);
		JsonObject json = HttpSend.execute(paramsMap, CONST.GET_OPEN_ID_AY_ACCOUNT, TIME_OUT);
		JsonElement openid = json.get("openid");
		String oid = openid.getAsString();
		return oid;
	}



}
