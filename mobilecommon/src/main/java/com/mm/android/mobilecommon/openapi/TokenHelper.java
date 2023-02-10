package com.mm.android.mobilecommon.openapi;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.EnvironmentConfig;
import com.mm.android.mobilecommon.common.CommonParam;
import com.mm.android.mobilecommon.route.ProviderManager;

import java.util.HashMap;

public class TokenHelper {
	private static int TOKEN_TIME_OUT = 4 * 1000;

	private static TokenHelper instance;

	private TokenHelper() {
	}

	public static TokenHelper getInstance() {
		if (instance == null) {
			synchronized (TokenHelper.class) {
				if (instance == null) {
					instance = new TokenHelper();
				}
			}
		}
		return instance;
	}


	public String accessToken;
	public String subAccessToken;
	public String openid;
	public String userId = "";
	public CommonParam commonParam;

	private Throwable throwable;

	public void init(CommonParam commonParam) throws Throwable {
		this.commonParam = commonParam;
		this.accessToken = "";
		this.userId = "";
		this.throwable = null;

		/**
		 * Parameter calibration
		 *
		 * 参数校验
		 */
		commonParam.checkParam();

		/**
		 * Initialize the parameters
		 *
		 * 初始化参数
		 */
		initParam(commonParam);
		/**
		 * Get the open platform token
		 *
		 * 获取开放平台token
		 */
		initToken();

		if (TextUtils.isEmpty(accessToken) && throwable != null) {
			throw throwable;
		}
	}

	private void initParam(CommonParam commonParam) throws Exception {
		userId = TextUtils.isEmpty(commonParam.getUserId()) ? commonParam.getAppId() : commonParam.getUserId();
		//传入参数 AppId SecretKey  环境切换
		CONST.makeEnv(commonParam.getEnvirment(), commonParam.getAppId(), commonParam.getAppSecret());
//        ContextHelper.init(commonParam.getContext());
		new EnvironmentConfig.Builder().setContext(commonParam.getContext()).build();
	}


	private String initToken() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					accessToken = getToken();
				} catch (BusinessException e) {
					throwable = new Throwable(e.errorDescription);
				}
			}
		});
		try {
			thread.start();
			thread.join(4000);
		} catch (InterruptedException e) {
			throwable = e;
		}
		return accessToken;
	}


	/**
	 * Get token
	 * @return String  token
	 * @throws BusinessException
	 *
	 * 获取token
	 * @return String  token
	 * @throws BusinessException
	 */
	public static String getToken() throws BusinessException {
		// 获取管理员token
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		JsonObject jsonData = HttpSend.execute(paramsMap, CONST.METHOD_ACCESSTOKEN, TOKEN_TIME_OUT);
		String token = jsonData.get("accessToken").getAsString();
		return token;
	}


	public void setSubAccessToken(String subAccessToken, Application application) {
		this.subAccessToken = subAccessToken;
		ProviderManager.getDeviceDetailProvider().initP2P(application);
	}
}
