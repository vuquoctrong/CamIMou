package com.mm.android.mobilecommon.utils;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Created by 29295 on 2017/3/14.
 */

public class WordInputFilter implements InputFilter {

    private String rex = "[^a-zA-Z0-9\\-_@.]";

    public static final String REX_EMAIL = "[^a-zA-Z0-9\\-\\_\\@\\.]";

    public static final String REX_PHONE = "[^0-9]";

    public static final String NEW_PHONE="^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    public static final String REGEX_EMAIl = "[A-z]+[A-z0-9_-]*\\@[A-z0-9]+\\.[A-z]+";

    /**
     * Regular expression: Verify mailboxes
     *
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL_GUO = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    public static final String REX_CHINESS = "[\\u4e00-\\u9fa5]";

    public static final String REX_DEVICE_SN = "[^0-9A-Za-z]";

    public static final String REX_PASSWORD = "[^a-zA-Z0-9\\!\\#\\$\\%\\(\\)\\*\\+\\,\\-\\.\\/\\<\\=" +
            "\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~]*";

    public static final String REX_NAME = "[^a-zA-Z0-9\\-\\u4e00-\\u9fa5\\_\\@\\s]";

    public static final String ACCOUNT_REX_NAME = "[^a-zA-Z0-9\\-\\u4e00-\\u9fa5\\_\\@]";


    public static final String REX_EMOJI = "[\\x{1F300}-\\x{1F5FF}\\x{1F600}-\\x{1F64F}\\x{1F900}-\\x{1F9FF}\\x{1F680}-\\x{1F6FF}\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}]";

    public static final String REX_NAME_NO_SPACE = "[^a-zA-Z0-9\\-\\u4e00-\\u9fa5\\_\\@]";

    public WordInputFilter(String rex) {
        this.rex = rex;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String result = source.toString().replaceAll(rex, "");
        if(source instanceof  Spanned){
            SpannableString str = new SpannableString(result);
            TextUtils.copySpansFrom((Spanned) source ,0 ,result.length(), null, str, 0 );
            return str;
        }

        return result;
    }
}
