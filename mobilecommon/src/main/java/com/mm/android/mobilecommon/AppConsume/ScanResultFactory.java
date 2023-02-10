package com.mm.android.mobilecommon.AppConsume;

import android.text.TextUtils;

import com.mm.android.mobilecommon.common.LCConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The corresponding classes are generated according to the two-dimensional code scanning results
 *
 * 根据二维码扫描结果生成相应的类
 */
public class ScanResultFactory {
    // 工厂方法.注意 返回类型为抽象产品角色
    private static String tag = "www.hsview.com";
    private static String tag_lc = "www.lechange.cn";

    public static ScanResult scanResult(String scanString) {
        if (ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA) {//海外二维码规则
            // 带NC标签的
            final String[] NC_ARRAYS = new String[]{"NC:", "nC:", "Nc:", "nc:", "NC：", "nC：", "Nc：", "nc："};
            // 带SC标签的
            final String[] SC_ARRAYS = new String[]{"SC:", "sC:", "Sc:", "Sc:", "SC：", "sC：", "Sc：", "sc："};
            boolean hasNc = hasTag(scanString, NC_ARRAYS);
            boolean hasSc = hasTag(scanString, SC_ARRAYS);
            if((hasNc || hasSc) && scanString.contains("{")) {
                return new PseudoJsonNcScanResult(scanString);
            } else {
                String deviceSN = "";
                String deviceSerial = "";
                String devicePid = "";
                if (scanString.contains(",")) {  //逗号分割，{SN:2J021B3PAK00120,DT:IPC-HFW1120SP-W-0280B,RC:564897}
                    scanString = scanString.substring(1, scanString.length() - 1); //去掉收尾花括号
                    String[] array = scanString.split(",");
                    for (String strArray : array) {
                        if (strArray.contains("SN:")) {
                            deviceSN = strArray.substring(strArray.indexOf("SN:") + 3, strArray.length());
                        }
                        if (strArray.contains("DT:")) {
                            deviceSerial = strArray.substring(strArray.indexOf("DT:") + 3, strArray.length());
                        }
                        if (strArray.contains("PID:")) {
                            devicePid = strArray.substring(strArray.indexOf("PID:") + 4, strArray.length());
                        }
                    }
                } else if (scanString.contains(";")) {//分号分割，{SN:2J021B3PAK00120;DT:IPC-HFW1120SP-W-0280B;RC:564897}
                    scanString = scanString.substring(1, scanString.length() - 1); //去掉收尾花括号
                    String[] array = scanString.split(";");
                    for (String strArray : array) {
                        if (strArray.contains("SN:")) {
                            deviceSN = strArray.substring(strArray.indexOf("SN:") + 3, strArray.length());
                        }
                        if (strArray.contains("DT:")) {
                            deviceSerial = strArray.substring(strArray.indexOf("DT:") + 3, strArray.length());
                        }
                        if (strArray.contains("PID:")) {
                            devicePid = strArray.substring(strArray.indexOf("PID:") + 4, strArray.length());
                        }
                    }
                } else if (scanString.contains(":")) {   //2M047C9PAN00005:LCI-ARD1611-W:PJ0V46
                    String[] array = scanString.split(":");
                    deviceSN = array[0];
                    deviceSerial = array[1];
                } else {
                    deviceSN = scanString;
                }
                // 兼容 俄语区Q4的订单采用标签二维码内容异常 格式为：LC-IPC-C35P,4K002C6PAJA49A7
                if(TextUtils.isEmpty(deviceSN)) {
                    String[] array = scanString.split(",");
                    if(array != null && array.length == 2) {
                        deviceSN = array[1];
                    }
                }

                ScanResult scanResult = new ScanResult();
                scanResult.setSn(deviceSN);
                scanResult.setMode(deviceSerial);
                scanResult.setProductId(devicePid);
                return scanResult;
            }
        } else {//国内二维码规则
            if(scanString.contains(tag) || scanString.contains(tag_lc)){
                if(scanString.contains("{")){
                    // 兼容TC1
                    ScanResult scanResult = new PseudoJsonScanResult(scanString);
                    return scanResult;
                }
                // 兼容老乐橙设备
                int index = scanString.indexOf('=');
                String sn = scanString.substring(index + 1, scanString.length());
                ScanResult scanResult = new ScanResult();
                scanResult.setSn(sn);
                return scanResult;
            } else if (scanString.contains("{")) {
                ScanResult result = new PseudoJsonNcScanResult(scanString);
                return result;
            } else if (!scanString.contains("SN:") || !scanString.contains("SN=") || !scanString.contains("SN =") ||
                    !scanString.contains("sn:") || !scanString.contains("sn=") || !scanString.contains("sn =")) {
                if (!checkString(scanString)) {
                    String[] strings = scanString.split(":");
                    if (strings != null && !scanString.startsWith("http://") && strings.length == 3) {
                        return new TwoColonsScanResult(scanString);

                    } else if (strings != null && !scanString.startsWith("http://") && strings.length == 2) {
                        return new OneColonScanResult(scanString);
                    }
                } else {
                    ScanResult scanResult = new ScanResult();
                    scanResult.setSn(scanString);
                    return scanResult;
                }
            }
        }
        return new ScanResult();
    }

    /**
     * Check whether there is a tag
     *
     * 判断是否有tag
     */
    private static boolean hasTag(String scanString, String[] tagArrays) {
        boolean hasTag = false;
        for (String tag : tagArrays) {
            if (scanString.contains(tag)) {
                hasTag = true;
                break;
            }
        }
        return hasTag;
    }

    /**
     * Determines whether to contain only numbers or upper - and lowercase letters
     * @param str  String
     * @return boolean  Boolean value
     *
     * 判断是否只包含数字或大小写字母
     * @param str  字符串
     * @return boolean 布尔值
     */
    public static boolean checkString(String str) {
        String regEx = "[0-9A-Za-z]*"; // 只能是数字以及个别字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

}