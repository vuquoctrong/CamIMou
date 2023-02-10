package com.mm.android.mobilecommon.AppConsume;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.PreferencesHelper;

import java.io.File;

/**
 * Business necessary Attributes
 * The App configuration file must be configured.  Push id
 * {@code meta_data4project} The App configuration file must be configured .  Project name {@code meta_data4Type}
 * The App configuration file must be configured.   Protocol Initiator
 *
 * 业务必要属性
 * App配置文件必须要配置的 推送ID
 * {@code meta_data4project} App配置文件必须要配置的 项目名称 {@code meta_data4Type}
 * App配置文件必须要配置的 协议发起方
 */
public final class EnvironmentConfig {
    public static final String meta_data4Type = "meta-data4type";
    public static final String meta_data4AppId = "meta-data4appid";
    public static final String meta_isHttps = "meta-ishttps";

    /**
     * 1.clientType: Client type (mandatory) : "phone" -mobile phone, "web" -browser, "box" -box.
     * {@link} Environment configuration properties.
     *
     * 1.clientType：客户端类型（必填），"phone"-手机，"web"-浏览器，"box"-盒子。
     * {@link } 环境配置属性。
     */
    private String clientType;

    /**
     * 2.clientMac: Client MAC address (mandatory), which uniquely identifies the client.
     *
     * 2.clientMac：客户端MAC地址（必填），用于唯一标识这个客户端。
     */
    private String clientMac;

    /**
     * Server Address
     *
     * 服务端地址
     */
    private String host;

    /**
     * Whether it's an HTTPS protocol
     *
     * 是否是走https协议
     */
    private boolean isHttps;

    /**
     * Cache file name
     *
     * 缓存文件名
     */
    private String userAgent = "volley/0";

    /**
     * Cache path
     *
     * 缓存路径
     */
    private File cacheDir;

    /**
     * Application id
     *
     * 应用ID
     */
    private String appId;

    /**
     * Application
     *
     * 应用
     */
    private Context application;

    /**
     * Version name
     *
     * 版本名
     */
    private String versionName;

    private String language = null;

    public static Context mContext;// applicationContext

    public EnvironmentConfig(Builder b) {
        this.clientMac = b.clientMac;
        this.clientType = b.clientType;
        userAgent = b.userAgent;
        cacheDir = b.cacheDir;
        appId = b.appId;
        host = b.host;
        this.isHttps = b.isHttps;
        application = b.applicationContext;
        versionName = b.versionName;
        language = b.language;
    }

    public String getHost() {
        return host;
    }

    public String getClientType() {
        return clientType;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientMacAddress() {
        return clientMac;
    }

    public Context getContext() {
        return application;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getLanguage() {
        return language;
    }

    public static class Builder {
        private String clientType;
        private String clientMac;
        private String project;
        private String userAgent;
        private File cacheDir;
        private String appId;
        private String host;
        private boolean isHttps;
        private Context applicationContext;
        private String versionName;
        private String language = null;

        public Builder setContext(Context context) throws Exception {
            if (!(context instanceof Application)) {
                throw new Exception("context must instance application");
            }
            applicationContext = context;
            mContext = applicationContext.getApplicationContext();
            return this;
        }

        public Builder setClientType(String clientType) {
            this.clientType = clientType;
            return this;
        }

        private void setVersionName(Context context) {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = null;
            try {
                pi = pm.getPackageInfo(context.getPackageName(), 0);
                versionName = pi.versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void setClientMac(Context context) {
            try {
                // FIXME:Mac地址可能为空 IMEI 未识别信息，如果未发现，则功能不正常使用
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = telephonyManager.getDeviceId();
                LogUtil.debugLog("lechange", "imei : " + imei);
                if (imei == null) {
                    WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
                    String mac = info != null ? info.getMacAddress() : "";
                    LogUtil.debugLog("lechange", "mac : " + mac);
                    if (mac != null) {
                        mac = mac.replace(":", "");
                    } else {
                        mac = "";
                    }
                    clientMac = mac;
                } else {
                    clientMac = imei;
                }
                LogUtil.debugLog("lechange", "clientMac : " + clientMac);
            } catch (Exception e) {
                clientMac = "";
            }
        }

        public Builder setProject(String project) {
            this.project = project;
            return this;
        }

        private void setCacheFile(Context context) {
            cacheDir = context.getCacheDir();
            userAgent = "volley/0";
            try {
                String packageName = context.getPackageName();
                PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (NameNotFoundException e) {

            }
        }

        public Builder setHost(String host) {
            this.host = host;
            String historyHost = PreferencesHelper.getInstance(applicationContext).getString(PreferencesConfig.HOST_HELP);
            if (!TextUtils.isEmpty(historyHost)) {// 如果历史记录Host 不是nil
                // 则设置历史Host，用于测试服务器
                this.host = historyHost;
            }
            return this;
        }

        private void setDefaultHost(String host) {
            this.host = host;
            String historyHost = PreferencesHelper.getInstance(applicationContext).getString(PreferencesConfig.HOST_HELP);
            if (!TextUtils.isEmpty(historyHost)) {// 如果历史记录Host 不是nil
                // 则设置历史Host，用于测试服务器
                this.host = historyHost;
            }
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        private void setLanguage(Context context) {
            this.language = context.getResources().getConfiguration().locale.getLanguage();
        }

        public EnvironmentConfig build() {

            if (versionName == null || versionName.equals("")) {
                setVersionName(applicationContext);
            }

            if (cacheDir == null) {
                setCacheFile(applicationContext);
            }

            if (clientMac == null || clientMac.equals("")) {
                setClientMac(applicationContext);
            }

            if (language == null || language.equals("")) {
                setLanguage(applicationContext);
            }

            try {
                ApplicationInfo appInfo = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), PackageManager.GET_META_DATA);

                if (clientType == null || clientType.equals("")) {
                    clientType = appInfo.metaData.getString(meta_data4Type);
                }

                isHttps = appInfo.metaData.getBoolean(meta_isHttps);

            } catch (NameNotFoundException e) {

                e.printStackTrace();
            }
            return new EnvironmentConfig(this);
        }
    }
}
