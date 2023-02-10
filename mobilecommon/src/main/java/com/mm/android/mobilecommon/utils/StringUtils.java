package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StringUtils {

    /**
     * Irregular character filtering
     * @param str
     *
     * 不规则字符过滤
     * @param str
     */
    public static String strFilter(String str) {

        if (TextUtils.isEmpty(str)) {
            return str;
        }

        String strEx = "^[a-zA-Z0-9\\-\u4E00-\u9FA5\\_\\@\\,\\.]+";

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (!temp.matches(strEx)) {
                str = str.replace(temp, "");
                return strFilter(str);
            }
        }

        return str;
    }

    /**
     * Filtering the password
     *
     * 过滤密码
     */
    public static String strPsswordFilter(String str) {

//        String strEx = "^[a-zA-Z0-9\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)" +
//                "\\-\\_\\+\\=\\{\\[\\}\\]\\|\\\\\\;\\:\\'\\\"\\<\\>\\,\\.\\/\\?]+";

        //27个特殊符号ASC码：33、35、36、37、40、41、42、43、44、45、46、47、60、61、62、63、64、91、92、93、94、95、96、123、124、125、126
        String strEx = "^[a-zA-Z0-9\\!\\#\\$\\%\\(\\)\\*\\+\\,\\-\\.\\/\\<\\=" +
                "\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~]*";

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (!temp.matches(strEx)) {
                str = str.replace(temp, "");
                return strPsswordFilter(str);
            }
        }

        return str;
    }


    /**
     * Check security
     * @param username  UserName
     * @param password  Password
     * @return true：Check Fail
     *
     * 校验安全性
     * @param username  用户名
     * @param password  密码
     * @return true 表示校验失败
     */
    public static boolean checkSafetyBaseline(String username, String password, Context context) {
        return checkPasswordWithUername(username, password) || checkRepeat(password) || checkCryptographicLibrary(password, context);
    }


    /**
     * The client adds the verification of non-compliant passwords. When the password is the user name itself or the reverse order of the user name, when you click "Get
     * the verification Code", toast prompts "password is too simple", and you do not need to clear the password in the password box.
     * @param username  UserName
     * @param password  Password
     * @return boolean  Boolean value
     *
     * 客户端新增不合规密码的校验，当密码是用户名本身或者用户名的逆序，在点击【获取验证码】时，toast提示【密码过于简单】，无需清除密码框的密码；
     * @param username  用户名
     * @param password  密码
     * @return boolean  布尔值
     */
    public static boolean checkPasswordWithUername(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        String userNameLowerCase = username.toLowerCase();
        String passwordLowerCase = password.toLowerCase();
        String reverse = new StringBuilder(userNameLowerCase).reverse().toString();
        return TextUtils.equals(passwordLowerCase, reverse) || TextUtils.equals(passwordLowerCase, userNameLowerCase);
    }

    /**
     * Weak password library verification is added on the client. When the password set by the user is "weak password", when you click "Get
     * the verification Code", toast prompts "password is too simple", and you do not need to clear the password in the password box.
     * @param password  Password
     * @return boolean  Boolean value
     *
     * 客户端新增弱密码库校验，当用户设置的密码属于“弱密码”，在点击【获取验证码】时，toast提示【密码过于简单】，无需清除密码框的密码；
     * @param password  密码
     * @return boolean  布尔值
     */
    public static boolean checkCryptographicLibrary(String password, Context context) {
        List<String> library = getCryptographicLirary(context);
        return library.contains(password);
    }


    /**
     * Get the password library
     * @param context
     *
     * 获取密码库
     * @param context
     */
    private static List<String> getCryptographicLirary(Context context) {
        List<String> array = new ArrayList<>();

        try {
            XmlPullParser pullParser = Xml.newPullParser();
            InputStream is = context.getAssets().open("password.xml");
            pullParser.setInput(is, "utf-8");

            int eventType = pullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        array = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("string".equals(pullParser.getName())) {
                            String str = pullParser.getAttributeValue(0);
                            array.add(str);
                        }
                        break;
                }

                eventType = pullParser.next();

            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;
    }

    /**
     * The client adds the password duplicate character verification. When the password contains more than six consecutive duplicate characters, when you click "Get
     * the Verification Code", toast prompts "The password is too simple", and you do not need to clear the password in the password box.
     * @param password  Password
     * @return boolean  Boolean value
     *
     * 客户端新增密码重复字符校验，当密码中重复的字符连续超过6次（含6次），在点击【获取验证码】时，toast提示【密码过于简单】，无需清除密码框的密码；
     * @param password  密码
     * @return boolean  布尔值
     */
    public static boolean checkRepeat(String password) {
        String rex = "(.)\\1+";

        List<String> arr = new ArrayList<>();

        Pattern pattern = Pattern.compile(rex);
        Matcher matcher = pattern.matcher(password);

        while (matcher.find()) {
            String str = matcher.group();
            arr.add(str);
        }

        if (arr.isEmpty()) {
            return false;
        }

        for (String str : arr) {
            if (str.length() >= 6) {
                return true;
            }
        }

        return false;
    }

    /**
     * Filters non-alphanumeric characters in passwords
     * @param str
     * @return String  String value
     *
     * 过滤密码中的非数字字母字符
     * @param str
     * @return String  字符串值
     */
    public static String snFilter(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String E1 = "[A-Z]";// 大写英文字母
        String E2 = "[a-z]";// 小写英文字母
        String E3 = "[0-9]";// 数字

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (!temp.matches(E1) && !temp.matches(E2)
                    && !temp.matches(E3)) {
                str = str.replace(temp, "");
                return snFilter(str);
            }
        }
        return str;
    }

    /**
     * Character filtering for passwords
     * @param str
     * @return String  String value
     *
     * 字符过滤，用于密码
     * @param str
     * @return String  字符串值
     */
    public static String strPwdFilter(String str) {

        if (TextUtils.isEmpty(str)) {
            return str;
        }

        String strEx = "^[a-zA-Z0-9\\-\\_\\@]+";

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (!temp.matches(strEx)) {
                str = str.replace(temp, "");
                return strPwdFilter(str);
            }
        }

        return str;
    }

    public static String wifiPwdFilter(String str) {

        if (TextUtils.isEmpty(str)) {
            return str;
        }
        // TD:20780
        String chinese1 = "[\u2E80-\uA4CF]";
        String chinese2 = "[\uF900-\uFAFF]";
        String chinese3 = "[\uFE30-\uFE4F]";

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(chinese1) || temp.matches(chinese2) || temp.matches(chinese3)) {
                str = str.replace(temp, "");
                return wifiPwdFilter(str);
            }
        }
        return str;
    }

    public static long checkVersion(String strVersion) {
        long version = -1;
        try {
            strVersion = strVersion.replace(".", "").replace("v", "").replace("V", "");
            if (!TextUtils.isEmpty(strVersion)) {
                version = Long.parseLong(strVersion);
            }
        } catch (Exception e) {
        }
        return version;
    }
}
