package com.mm.android.mobilecommon.AppConsume;

import android.text.TextUtils;

/**
 * Qr code scan results in pseudo json format
 *
 * 伪json格式的二维码扫描结果
 */
public class PseudoJsonScanResult extends ScanResult {

    private static final String TAG = "JsonScanResult";
    private static final String[] SN_TAGS = new String[]{"SN:", "sN:", "Sn:", "sn:", "SN：", "sN：", "Sn：", "sn："};
    private static final String[] DT_TAGS = new String[]{"DT:", "dT:", "Dt:", "dt:", "DT：", "dT：", "Dt：", "dt："};
    private static final String[] RD_TAGS = new String[]{"RD:", "rD:", "Rd:", "rd:", "RD：", "rD：", "Rd：", "rd："};
    private static final String[] RC_TAGS = new String[]{"RC:", "rC:", "Rc:", "rc:", "RC：", "rC：", "Rc：", "rc："};

    /**
     * Create a new instance JsonScanResult.
     * @param scanString
     *
     * 创建一个新的实例JsonScanResult.
     * @param scanString
     */
    public PseudoJsonScanResult(String scanString) {
        super(scanString);

        //解析伪Json 类似{SN:DVRP2P00LJL0028,DT:LC/HCVR1604HG-SFD-V4/-AF-DVR-II-A/16-16,RC:SQ93W5}

        String jsonStr;
        int firstIndex = scanString.indexOf("{");
        if (firstIndex >= 0 && firstIndex <= scanString.length() - 1) {
            jsonStr = scanString.substring(firstIndex, scanString.length());
        } else {
            jsonStr = scanString;
        }

        int snEndIndex = -1;
        int dtEndIndex = -1;
        int rdEndIndex = -1;
        int snStartIndex = getStartIndex(jsonStr, SN_TAGS, -1);
        int dtStartIndex = getStartIndex(jsonStr, DT_TAGS, -1);
        int rdStartIndex = getStartIndex(jsonStr, RD_TAGS, -1);
        rdStartIndex = getStartIndex(jsonStr, RC_TAGS, rdStartIndex);

        if (snStartIndex == -1) {
            this.setSn("");
            return;
        }


        if (snStartIndex > dtStartIndex && snStartIndex > rdStartIndex) { // sn在最后
            if (dtStartIndex == -1 && rdStartIndex == -1) { // {sn:}
                snEndIndex = jsonStr.length() - 1;

            } else if (dtStartIndex == -1 && rdStartIndex != -1) { // {rd:,sn:}
                rdEndIndex = snStartIndex - 1;
                snEndIndex = jsonStr.length() - 1;
            } else if (dtStartIndex != -1 && rdStartIndex == -1) { // {dt:,sn:}
                dtEndIndex = snStartIndex - 1;
                snEndIndex = jsonStr.length() - 1;
            } else {
                if (dtStartIndex > rdStartIndex) { // {rd:,dt:,sn:}
                    rdEndIndex = dtStartIndex - 1;
                    dtEndIndex = snStartIndex - 1;
                    snEndIndex = jsonStr.length() - 1;
                } else {    //  {dt:,rd:,sn:}
                    dtEndIndex = rdStartIndex - 1;
                    rdEndIndex = snStartIndex - 1;
                    snEndIndex = jsonStr.length() - 1;
                }
            }
        } else if (dtStartIndex > snStartIndex && dtStartIndex > rdStartIndex) {    // dt在最后
            if (rdStartIndex == -1) {
                snEndIndex = dtStartIndex - 1;
                dtEndIndex = jsonStr.length() - 1;
            } else if (snStartIndex > rdStartIndex) {
                rdEndIndex = snStartIndex - 1;
                snEndIndex = dtStartIndex - 1;
                dtEndIndex = jsonStr.length() - 1;

            } else if (snStartIndex < rdStartIndex) {
                snEndIndex = rdStartIndex - 1;
                rdEndIndex = dtStartIndex - 1;
                dtEndIndex = jsonStr.length() - 1;
            }
        } else if (rdStartIndex > snStartIndex && rdStartIndex > dtStartIndex) {    // rd在最后
            if (dtStartIndex == -1) {
                snEndIndex = rdStartIndex - 1;
                rdEndIndex = jsonStr.length() - 1;
            } else if (snStartIndex > dtStartIndex) {
                dtEndIndex = snStartIndex - 1;
                snEndIndex = rdStartIndex - 1;
                rdEndIndex = jsonStr.length() - 1;

            } else if (snStartIndex < dtStartIndex) {
                snEndIndex = dtStartIndex - 1;
                dtEndIndex = rdStartIndex - 1;
                rdEndIndex = jsonStr.length() - 1;
            }
        }

        if (rdStartIndex != -1) {
            this.setRegcode(filterInvalidString(jsonStr.substring(rdStartIndex + 2, rdEndIndex)));
        } else {
            this.setRegcode("");
        }

        if (dtStartIndex != -1) {
            this.setMode(filterInvalidString4Type(jsonStr.substring(dtStartIndex + 2, dtEndIndex)));
        } else {
            this.setMode("");
        }
        this.setSn(filterInvalidString(jsonStr.substring(snStartIndex + 2, snEndIndex)));

    }

    private int getStartIndex(String jsonStr, String[] tags, int startIndex) {
        for (String dttag : tags) {
            if (jsonStr.contains(dttag)) {
                return jsonStr.indexOf(dttag) + 1;
            }
        }
        return startIndex;
    }

    public static String filterInvalidString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String numberAndAbc = "[a-zA-Z0-9]";
        StringBuilder buffer = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(numberAndAbc)) {
                buffer.append(temp);
            }
        }
        return buffer.toString();
    }

    public static String filterInvalidString4Type(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String numberAndAbc = "[a-zA-Z0-9-/\\\\]";
        StringBuilder buffer = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(numberAndAbc)) {
                buffer.append(temp);
            }
        }
        return buffer.toString();
    }

}
