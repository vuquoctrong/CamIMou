package com.mm.android.deviceaddmodule;

import android.app.Application;

import java.io.Serializable;

public class CommonParam implements Serializable {
    private Application context;
    private String userId;
    private String appId;
    private String appSecret;

    private String envirment= "";

    public String getEnvirment() {
        return envirment;
    }

    public void setEnvirment(String envirment) {
        this.envirment = envirment;
    }

    public Application getContext() {
        return context;
    }

    public void setContext(Application context) {
        this.context = context;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void checkParam() throws Exception {
        if (context == null) {
            throw new Exception("context must not null");
        }

        if (appId == null || appId.isEmpty()) {
            throw new Exception("appId must not empty");
        }

        if (appSecret == null || appSecret.isEmpty()) {
            throw new Exception("appSecret must not empty");
        }
    }
}
