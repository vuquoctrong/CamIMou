package com.zxing.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regular expression
 *
 * 正则表达式
 */
@SuppressLint("WrongConstant")
public class Validator {

    public static boolean password(String password) {
        return password.matches("[0-9a-zA-Z]{8,16}") && !password.matches("[0-9]+") && !password.matches("[a-zA-Z]+");
    }

    public static List<String> findMac(String src) {
        List<String> list = new ArrayList<String>();
        if (src == null || "".equals(src))
            return list;
        Pattern pattern = Pattern
                .compile("[0-9a-z]{2}:[0-9a-z]{2}:[0-9a-z]{2}:[0-9a-z]{2}:[0-9a-z]{2}:[0-9a-z]{2}");
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            list.add(matcher.group(0));
        }
        return list;
    }

    public static List<String> findColor(String src) {
        List<String> list = new ArrayList<String>();
        if (src == null || src.equals(""))
            return list;
        Pattern pattern = Pattern
                .compile("#[0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8}");
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            list.add(matcher.group(0));
        }
        return list;
    }

    /**
     * Chinese characters were removed and normalized to code
     *
     * 去除汉字,归正编码
     */
    public static String replaceHanzi(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }

        /**
         * Reformed coding
         *
         * 归正编码
         */
        byte[] bytes = input.getBytes();
        String info = "";

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                bytes[i] = 32;
            }
            info = info + new String(new byte[]{bytes[i]});
        }

        /**
         * Remove the Chinese
         *
         * 去除中文
         */
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(info);
        List<String> inputs = new ArrayList<>();

        if (m.find()) {
            for (int i = 0; i < info.length(); i++) {
                String ever = info.substring(i, i + 1);
                Matcher m1 = p.matcher(ever);
                if (m1.find()) {
                    ever = "";
                }
                inputs.add(ever);
            }

            String inputNew = "";
            for (int i = 0; i < inputs.size(); i++) {
                inputNew = inputNew + inputs.get(i);
            }
            return inputNew.trim();

        }
        return info.trim();
    }

    /**
     * Validation email
     *
     * 验证邮箱
     */
    public static boolean checkEmail(String email) {
        boolean flag;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * Get the first letter of Pinyin
     *
     * 获得汉语拼音首字母
     */
    public static String getAlpha(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

    /**
     * Check the URL
     *
     * 校验URL
     */
    public static boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) return false;
        boolean flag;
        try {
            String check = "^((https|http|ftp|rtsp|mms|axd):\\/\\/)[^\\s]+";
            Pattern regex = Pattern.compile(check, Pattern.CASE_INSENSITIVE);
            Matcher matcher = regex.matcher(url.replaceAll(" ", ""));
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    /**
     * Verify image URL
     *
     * 校验图片URL
     */
    public static boolean checkImageUrl(String url) {
        if (TextUtils.isEmpty(url)) return false;
        boolean flag;
        try {
            String check = "^((https|http):\\/\\/)[^\\s]+.(png|jpg|gif|webp)";
            Pattern regex = Pattern.compile(check, Pattern.CASE_INSENSITIVE);
            Matcher matcher = regex.matcher(url);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    /**
     * Check positive integers
     *
     * 校验正整数
     */
    public static boolean checkInt(String intContent) {
        if (TextUtils.isEmpty(intContent)) return false;
        boolean flag;
        try {
            String check = "^[1-9]\\d*$";
            Pattern regex = Pattern.compile(check, Pattern.CASE_INSENSITIVE);
            Matcher matcher = regex.matcher(intContent);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    public static boolean checkColor(String color) {
        if (TextUtils.isEmpty(color))
            return false;
        boolean flag;
        try {
            String check = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$";
            Pattern regex = Pattern.compile(check, Pattern.CASE_INSENSITIVE);
            Matcher matcher = regex.matcher(color);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}