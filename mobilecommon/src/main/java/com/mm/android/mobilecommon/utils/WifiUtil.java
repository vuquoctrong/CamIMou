package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class WifiUtil {
    private final String LC_PACKAGE_NAME = "com.lechange.demo.a";

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiLock mWifiLock;
    private Context mContext;

    public WifiUtil(Context context) {
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
        this.mContext = context;
    }

    public void openWifi() {
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(true);
        }

    }

    public void closeWifi() {
        if (this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
        }

    }

    public boolean isWifiEnabled(){
        return this.mWifiManager.isWifiEnabled();
    }

    public boolean isWifi5G(Context context) {
        int freq = 0;
        if (VERSION.SDK_INT > 21) {
            freq = this.mWifiInfo.getFrequency();
        } else {
            String ssid = this.mWifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = this.mWifiManager.getScanResults();
                Iterator var6 = scanResults.iterator();

                while (var6.hasNext()) {
                    ScanResult scanResult = (ScanResult) var6.next();
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }

        return freq > 4900 && freq < 5900;
    }

    public int checkState() {
        return this.mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        this.mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (this.mWifiLock.isHeld()) {
            this.mWifiLock.acquire();
        }

    }

    public void creatWifiLock() {
        this.mWifiLock = this.mWifiManager.createWifiLock("Test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.mWifiConfiguration;
    }

    public void connectConfiguration(int index) {
        if (index <= this.mWifiConfiguration.size()) {
            this.mWifiManager.enableNetwork(((WifiConfiguration) this.mWifiConfiguration.get(index)).networkId, true);
        }
    }

    public void startScan() {
        this.mWifiManager.startScan();
        this.mWifiList = this.mWifiManager.getScanResults();
        this.mWifiConfiguration = this.mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getWifiList() {
        return this.mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < this.mWifiList.size(); ++i) {
            stringBuilder.append("Index_" + (new Integer(i + 1)).toString() + ":");
            stringBuilder.append(((ScanResult) this.mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }

        return stringBuilder;
    }

    public String getMacAddress() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.getBSSID();
    }

    public int getIPAddress() {
        return this.mWifiInfo == null ? 0 : this.mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return this.mWifiInfo == null ? 0 : this.mWifiInfo.getNetworkId();
    }

    public String getWifiInfo() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.toString();
    }

    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = this.mWifiManager.addNetwork(wcg);
        return this.mWifiManager.enableNetwork(wcgID, true);
    }

    public boolean addNetworkEx(WifiConfiguration wcg) {
//        int wcgID = this.mWifiManager.addNetwork(wcg);
//        Method connectMethod = this.connectWifiByReflectMethod(wcgID);
        Method connectMethod = connectWifiByReflectMethod(wcg);
        if (connectMethod == null) {
            int wcgID = this.mWifiManager.addNetwork(wcg);
            boolean isOK = this.mWifiManager.enableNetwork(wcgID, true);
            return isOK;
        } else {
            return true;
        }
    }

    private Method connectWifiByReflectMethod(WifiConfiguration wcg) {
        Method connectMethod = null;
        if (this.isAndroidBetween8To9()) {
            Method[] var3 = this.mWifiManager.getClass().getDeclaredMethods();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Method methodSub = var3[var5];
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0 && "android.net.wifi.WifiConfiguration".equalsIgnoreCase(types[0].getName())) {
                        connectMethod = methodSub;
                        break;
                    }
                }
            }

            if (connectMethod != null) {
                try {
                    connectMethod.setAccessible(true);
                    connectMethod.invoke(this.mWifiManager, wcg, null);
                } catch (Exception var8) {
                    var8.printStackTrace();
                    return null;
                }
            }
        }

        return connectMethod;
    }

    private Method connectWifiByReflectMethod(int netId) {
        Method connectMethod = null;
        if (this.isAndroidBetween8To9()) {
            Method[] var3 = this.mWifiManager.getClass().getDeclaredMethods();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Method methodSub = var3[var5];
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0 && "int".equalsIgnoreCase(types[0].getName())) {
                        connectMethod = methodSub;
                        break;
                    }
                }
            }

            if (connectMethod != null) {
                try {
                    connectMethod.setAccessible(true);
                    connectMethod.invoke(this.mWifiManager, netId, null);
                } catch (Exception var8) {
                    var8.printStackTrace();
                    return null;
                }
            }
        }

        return connectMethod;
    }

    private boolean isAndroidBetween8To9() {
        return VERSION.SDK_INT >= 26 && VERSION.SDK_INT <= 28;
    }

    public void disconnectWifi(int netId) {
        this.mWifiManager.disableNetwork(netId);
        this.mWifiManager.disconnect();
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        //config.allowedAuthAlgorithms.clear();
        //config.allowedGroupCiphers.clear();
        //config.allowedKeyManagement.clear();
        //config.allowedPairwiseCiphers.clear();
        //config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        Class clz = WifiConfiguration.class;
        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            this.mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(0);
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }

            config.allowedAuthAlgorithms.set(0);
            config.allowedAuthAlgorithms.set(1);
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(0);
            config.allowedGroupCiphers.set(2);
            config.allowedGroupCiphers.set(3);
            config.allowedKeyManagement.set(1);
            config.allowedPairwiseCiphers.set(1);
            config.allowedPairwiseCiphers.set(2);
            config.allowedProtocols.set(1);
            config.allowedProtocols.set(0);
            config.status = 2;
        }

        try {
            Field filed = clz.getField("validatedInternetAccess");
            filed.set(config, true);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return config;
    }

    private static boolean isHexWepKey(String wepKey) {
        int len = wepKey.length();
        return len != 10 && len != 26 && len != 58 ? false : isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; --i) {
            char c = key.charAt(i);
            if ((c < '0' || c > '9') && (c < 'A' || c > 'F') && (c < 'a' || c > 'f')) {
                return false;
            }
        }

        return true;
    }

    public WifiConfiguration IsExsits(String SSID) {
        if (TextUtils.isEmpty(SSID)) {
            return null;
        } else {
            String targetSSID = SSID.startsWith("\"") ? SSID : "\"" + SSID + "\"";

            List<WifiConfiguration> existingConfigs = this.mWifiManager.getConfiguredNetworks();

            if (existingConfigs == null) {
                return null;
            } else {
                Iterator var4 = existingConfigs.iterator();

                WifiConfiguration existingConfig;
                do {

                    if (!var4.hasNext()) {
                        return null;
                    }

                    existingConfig = (WifiConfiguration)var4.next();
                } while(!existingConfig.SSID.equals(targetSSID));

                return existingConfig;
            }
        }
    }

    public boolean connectWifi(String SSID, String passWord) {
        if (VERSION.SDK_INT >= 23) {
            WifiConfiguration tempConfig = this.IsExsits(SSID);
            if (tempConfig != null) {
                boolean ret = this.mWifiManager.removeNetwork(tempConfig.networkId);
                if (!ret) {
                    return this.mWifiManager.enableNetwork(tempConfig.networkId, true);
                }
            }
        }

        return this.addNetwork(this.createWifiInfo(SSID, passWord, WifiCipherType.WIFICIPHER_NOPASS));
    }

    public boolean connectWifi(String SSID, String passWord, WifiCipherType type) {
        if (VERSION.SDK_INT >= 23) {
            WifiConfiguration tempConfig = this.IsExsits(SSID);
            if (tempConfig != null) {
                boolean ret = this.mWifiManager.removeNetwork(tempConfig.networkId);
                if (!ret) {
                    return this.mWifiManager.enableNetwork(tempConfig.networkId, true);
                }
            }
        }

        return this.addNetwork(this.createWifiInfo(SSID, passWord, type));
    }

    public boolean connectWifiEx(String SSID, String passWord, WifiCipherType type) {
        if (VERSION.SDK_INT >= 23) {
            WifiConfiguration tempConfig = this.IsExsits(SSID);
            if (tempConfig != null) {
                boolean ret = this.mWifiManager.removeNetwork(tempConfig.networkId);
                boolean isLechangeCreat = TextUtils.equals(LC_PACKAGE_NAME, getCreatorName(tempConfig)) || TextUtils.equals(LC_PACKAGE_NAME, getLastUpdateName(tempConfig));
                //判断是该应用创建的直接走addNetworkEx
                if (!ret && !isLechangeCreat) {
                    return this.mWifiManager.enableNetwork(tempConfig.networkId, true);
                }
            }
        }

        return this.addNetworkEx(this.createWifiInfo(SSID, passWord, type));
    }

    public String getCreatorName(WifiConfiguration config) {
        String creatorName = null;

        if(config == null){
            return creatorName;
        }

        try{
            Field[] fields = config.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("creatorName")) {
                    field.setAccessible(true);
                    try {
                        if (field.get(config) != null) {
                            creatorName = field.get(config).toString();
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e){

        }

        return creatorName;
    }

    public String getLastUpdateName(WifiConfiguration config) {
        String creatorName = null;

        if(config == null){
            return creatorName;
        }

        try{
            Field[] fields = config.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("lastUpdateName")) {
                    field.setAccessible(true);
                    try {
                        if (field.get(config) != null) {
                            creatorName = field.get(config).toString();
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e){

        }

        return creatorName;
    }



    public WifiCipherType getCipherType(String ssid) {
        if (this.mWifiManager == null) {
            return WifiCipherType.WIFICIPHER_WEP;
        } else {
            List<ScanResult> list = this.mWifiManager.getScanResults();
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                ScanResult scResult = (ScanResult)var3.next();
                if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                    String capabilities = scResult.capabilities;
                    if (!TextUtils.isEmpty(capabilities)) {
                        if (!capabilities.contains("WPA") && !capabilities.contains("wpa") && !capabilities.contains("RSN")) {
                            if (!capabilities.contains("WEP") && !capabilities.contains("wep")) {
                                return WifiCipherType.WIFICIPHER_NOPASS;
                            }

                            return WifiCipherType.WIFICIPHER_WEP;
                        }

                        return WifiCipherType.WIFICIPHER_WPA;
                    }
                }
            }

            return WifiCipherType.WIFICIPHER_WEP;
        }
    }

    public String getGatewayIp() {
        DhcpInfo dhcpInfo = this.mWifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            long gatewayIps = (long)dhcpInfo.gateway;
            return this.long2ip(gatewayIps);
        } else {
            return "";
        }
    }

    public String long2ip(long ip) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.valueOf((int)(ip & 255L)));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((int)(ip >> 8 & 255L)));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((int)(ip >> 16 & 255L)));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((int)(ip >> 24 & 255L)));
        return stringBuffer.toString();
    }

    public WifiInfo getCurrentWifiInfo() {
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
        return this.mWifiInfo;
    }

    public int getWifiEncryption(String capabilities) {
        int encryption = 255;
        String cap = capabilities.toUpperCase(Locale.US);
        if (cap.indexOf("WPA2") != -1) {
            if (cap.indexOf("WPA2-PSK-TKIP") != -1) {
                encryption = 6;
            } else if (cap.indexOf("WPA2-PSK-AES") != -1) {
                encryption = 7;
            } else if (cap.indexOf("WPA2-TKIP") != -1) {
                encryption = 10;
            } else if (cap.indexOf("WPA2-AES") != -1) {
                encryption = 11;
            } else if (cap.indexOf("WPA2-PSK-CCMP") != -1) {
                encryption = 12;
            }
        } else if (cap.indexOf("WPA") != -1) {
            if (cap.indexOf("WPA-PSK-TKIP") != -1) {
                encryption = 4;
            } else if (cap.indexOf("WPA-PSK-CCMP") != -1) {
                encryption = 5;
            } else if (cap.indexOf("WPA-TKIP") != -1) {
                encryption = 8;
            } else if (cap.indexOf("WPA-CCMP") != -1) {
                encryption = 9;
            }
        } else if (cap.indexOf("WEP") != -1) {
            if (cap.indexOf("WEP_Open") != -1) {
                encryption = 2;
            } else if (cap.indexOf("WEP_Shared") != -1) {
                encryption = 3;
            }
        } else {
            encryption = 255;
        }

        return encryption;
    }

    public ScanResult getScanResult() {
        ScanResult scanResult = null;
        if (this.mWifiManager == null) {
            return null;
        } else {
            this.getCurrentWifiInfo();
            if (this.mWifiInfo.getSSID() != null) {
                try {
                    if (this.mWifiList == null) {
                        this.startScan();
                    }

                    if (this.mWifiList == null) {
                        return null;
                    }

                    Iterator var2 = this.mWifiList.iterator();

                    while(var2.hasNext()) {
                        ScanResult s = (ScanResult)var2.next();
                        if (s.SSID.replaceAll("\"", "").equals(this.mWifiInfo.getSSID().replaceAll("\"", ""))) {
                            scanResult = s;
                            break;
                        }
                    }
                } catch (Exception var4) {
                    return null;
                }
            }

            return scanResult;
        }
    }

    public boolean isNoPasswordWifi() {
        ScanResult scanResult = this.getScanResult();
        if (scanResult == null) {
            return false;
        } else {
            int encypt = this.getWifiEncryption(scanResult.capabilities);
            return encypt == 2 || encypt == 255;
        }
    }

    public String getDoorbellSSID(String deviceSn) {
        return "Doorbell-" + deviceSn;
    }

    public boolean isConnectedDoorbellHot(String deviceSn) {
        WifiInfo wifiInfo = this.getCurrentWifiInfo();
        if (wifiInfo == null) {
            return false;
        } else {
            String ssid = this.getDoorbellSSID(deviceSn);
            ssid = "\"" + ssid + "\"";
            return wifiInfo.getSSID().equals(ssid);
        }
    }

    public void disconnectCurrentWifi(String SSID) {
        WifiInfo wifiInfo = this.getCurrentWifiInfo();

        if (wifiInfo != null) {
            WifiConfiguration currentConfig = this.IsExsits(wifiInfo.getSSID());
            if (!wifiInfo.getSSID().equals(SSID)&& currentConfig != null) {
                this.mWifiManager.disableNetwork(currentConfig.networkId);
                this.mWifiManager.disconnect();
            }
        }

    }

    public static enum WifiCipherType {
        WIFICIPHER_WEP,
        WIFICIPHER_WPA,
        WIFICIPHER_NOPASS,
        WIFICIPHER_INVALID;

        private WifiCipherType() {
        }
    }


    private static final String HWMobile = "HUAWEI";

    public static boolean isHuaweiMobile(){
        return HWMobile.equalsIgnoreCase(Build.MANUFACTURER == null ? "UNKNOWN" : Build.MANUFACTURER.trim()); //false;
    }


}
