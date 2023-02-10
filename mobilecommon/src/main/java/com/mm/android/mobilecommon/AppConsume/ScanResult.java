package com.mm.android.mobilecommon.AppConsume;

/**
 * Two-dimensional code scan results base class
 *
 * 二维码扫描结果基类
 */
public class ScanResult {
    /**
     * Device Serial Number
     *
     * 设备序列号
     */
    private String sn = "";

    /**
     * Device Mode
     *
     * 设备型号
     */
    private String mode = "";

    /**
     * Device verification code and device security code
     *
     * 设备验证码、设备安全码
     */
    private String regcode = "";

    /**
     * Device random code, standby
     *
     * 设备随机码，备用
     */
    private String rd = "";

    /**
     * Device Capability
     *
     * 设备能力
     */
    private String nc = "";

    /**
     * Device security verification code
     *
     * 设备安全验证码
     */
    private String sc = "";

    /**
     * Iot product number
     *
     * iot产品号
     */
    private String productId = "";

    private String userId = "";//用户id

    private String appConfigId = "";//app 面板id

    /**
     * Create a new instance ScanResult
     * @param scanString
     *
     * 创建一个新的实例ScanResult
     * @param scanString
     */
    public ScanResult(String scanString) {
        // TODO Auto-generated constructor stub
    }

    /**
     * Create a new instance ScanResult
     *
     * 创建一个新的实例ScanResult.
     */
    public ScanResult() {
        // TODO Auto-generated constructor stub
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getRegcode() {
        return regcode;
    }

    public void setRegcode(String regcode) {
        this.regcode = regcode;
    }

    public String getRd() {
        return rd;
    }

    public void setRd(String rd) {
        this.rd = rd;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppConfigId() {
        return appConfigId;
    }

    public void setAppConfigId(String appConfigId) {
        this.appConfigId = appConfigId;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "sn='" + sn + '\'' +
                ", mode='" + mode + '\'' +
                ", regcode='" + regcode + '\'' +
                ", nc='" + nc + '\'' +
                ", sc='" + sc + '\'' +
                '}';
    }
}
