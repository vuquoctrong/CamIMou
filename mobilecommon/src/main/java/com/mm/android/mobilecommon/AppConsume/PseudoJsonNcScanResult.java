package com.mm.android.mobilecommon.AppConsume;


import com.mm.android.mobilecommon.utils.LogUtil;

import org.json.JSONObject;

/**
 * Qr code scanning results in pseudo-JSON format (with NC)
 *
 * 伪json格式的二维码扫描结果(带NC)
 */
public class PseudoJsonNcScanResult extends ScanResult {

    private static final String TAG = "JsonScanResult";
    private static final String[] SN_TAGS = new String[]{"SN", "sN", "Sn", "sn"};
    private static final String[] DT_TAGS = new String[]{"DT", "dT", "Dt", "dt"};
    private static final String[] RD_TAGS = new String[]{"RD", "rD", "Rd", "rd"};
    private static final String[] RC_TAGS = new String[]{"RC", "rC", "Rc", "rc"};
    private static final String[] NC_TAGS = new String[]{"NC", "nC", "Nc", "nc"};
    private static final String[] SC_TAGS = new String[]{"SC", "sC", "Sc", "sc"};
    private static final String[] IMEI_TAGS = new String[]{"IMEI", "imei"};
    private static final String[] PID_TAGS = new String[]{"PID", "pid","Pid","PId","pID","PiD"};

    /**
     * Create a new instance JsonScanResult
     * @param scanString
     *
     * 创建一个新的实例JsonScanResult.
     * @param scanString
     */
    public PseudoJsonNcScanResult(String scanString) {
        super(scanString);

        //解析伪Json 类似{SN:DVRP2P00LJL0028,DT:LC/HCVR1604HG-SFD-V4/-AF-DVR-II-A/16-16,NC:QR,RC:SQ93W5}

        //替换中文"："
        scanString = scanString.replace('：', ':');

        // 补充"{" "}"
        int first = scanString.indexOf("{");
        if (first < 0) {
            scanString = "{" + scanString;
        }
        int last = scanString.indexOf("}");
        if (last < 0) {
            scanString = scanString + "}";
        }

        // 补充引号"""
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < scanString.length(); i++) {
            char c = scanString.charAt(i);
            if(c == '{') {
                buffer.append(c).append("\"");
            }else if(c == ':' || c == ',' || c == ';') {
                buffer.append("\"").append(c).append("\"");
            } else if(c == '}') {
                buffer.append("\"").append(c);
            } else {
                buffer.append(c);
            }
        }

        String sn = "";
        String dt = "";
        String rd = "";
        String nc = "";
        String sc = "";
        String imeiCode = "";
        String pid = "";

        try {
            JSONObject jsonObject = new JSONObject(buffer.toString());
            sn = getValue(jsonObject, SN_TAGS, "");
            dt = getValue(jsonObject, DT_TAGS, "");
            rd = getValue(jsonObject, RD_TAGS, "");
            rd = getValue(jsonObject, RC_TAGS, "");
            nc = getValue(jsonObject, NC_TAGS, "");
            sc = getValue(jsonObject, SC_TAGS, "");
            imeiCode = getValue(jsonObject, IMEI_TAGS, "");
            pid = getValue(jsonObject, PID_TAGS, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setSn(sn);
        this.setRegcode(rd);
        this.setMode(dt);
        this.setNc(nc);
        this.setSc(sc);
        this.setProductId(pid);
        LogUtil.debugLog("PseudoJsonNcScanResult", this.toString());
    }

    private String getValue(JSONObject json, String[] tags, String defaultStr) {
        for (String tag : tags) {
            if (json.has(tag)) {
                return json.optString(tag, defaultStr);
            }
        }
        return defaultStr;
    }

}
