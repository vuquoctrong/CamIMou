package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordCheckRules {

    private final static int AWARD_2 = 2;
    private final static int AWARD_3 = 3;
    private final static int AWARD_5 = 5;

    public static final int PASSWORD_INVALID = 59999;							//密码为空 不合法
    public static final int PASSWORD_VALID_OK = 60000;                 //密码符合规则
    public static final int PASSWORD_NOT_MATCH = 60001;        		//新密码和确认密码不一致
    public static final int PASSWORD_INVALID_COMBINATION = 60002;           		//新密码组合不合法（2种以上类型）
    public static final int PASSWORD_INVALID_LENGTH = 60003;            	//新密码的长度不合法（8~32位）
    public static final int PASSWORD_INVALID_NO_SN = 60004;            	//新密码不合法：不允许为序列号
    public static final int PASSWORD_NOT_SAFY = 60005;                //密码不符合安全基线要求

    /**
     * User-defined encryption password verification
     * @param pwd  password
     *
     * 自定义加密密码校验
     * @param pwd  密码
     */
    public static int checkSinglePasswordValidationLC( String pwd) {
        if (TextUtils.isEmpty(pwd)) return PASSWORD_INVALID;

        if (pwd.length() < 6 || pwd.length() > 32) return PASSWORD_INVALID_LENGTH;

        return PASSWORD_VALID_OK;
    }

    public static int checkSinglePasswordValidation(Context context, String pwd) {
        if (TextUtils.isEmpty(pwd)) return PASSWORD_INVALID;

        if (pwd.length() < 8 || pwd.length() > 32) return PASSWORD_INVALID_LENGTH;

        if (!isMultiCharacterPassword(pwd)) return PASSWORD_INVALID_COMBINATION;

        if(StringUtils.checkSafetyBaseline(null , pwd, context)) return PASSWORD_NOT_SAFY;

        return PASSWORD_VALID_OK;
    }

    /**
     * The password contains 8 to 32 letters and a combination of characters
     *
     * 密码为8~32位字母且字符组合
     */
    public static int checkPasswordValidation(String pwd, String conformPwd, Context context){

        if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(conformPwd)) return PASSWORD_INVALID;

        if (pwd.length() < 8 || pwd.length() > 32) return PASSWORD_INVALID_LENGTH;

        if(!isMultiCharacterPassword(pwd)) return PASSWORD_INVALID_COMBINATION;

        if (!pwd.equals(conformPwd)) return PASSWORD_NOT_MATCH;

        if(StringUtils.checkSafetyBaseline(null , pwd, context)) return PASSWORD_NOT_SAFY;

        return PASSWORD_VALID_OK;

    }

    /**
     * Bluetooth lock Administrator Password Reset The password contains 6 to 12 letters and a combination of characters
     *
     * 蓝牙锁管理员密码重置 密码为6~12位字母且字符组合
     */
    public static int checkLockPasswordValidation(String pwd, String conformPwd, Context context){

        if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(conformPwd)) return PASSWORD_INVALID;

        if (pwd.length() < 6 || pwd.length() > 12) return PASSWORD_INVALID_LENGTH;

        if (!pwd.equals(conformPwd)) return PASSWORD_NOT_MATCH;

        return PASSWORD_VALID_OK;

    }


    /**
     * Whether to contain other special characters
     *
     * 是否包含其他特殊字符
     */
    public static boolean isSpeCharacterPassword(String str) {

        //27个特殊符号ASC码：33、35、36、37、40、41、42、43、44、45、46、47、60、61、62、63、64、91、92、93、94、95、96、123、124、125、126
        String charEx = "[\\!\\#\\$\\%\\(\\)\\*\\+\\,\\-\\.\\/\\<\\=" +
                "\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~]*";
        Pattern pChar = Pattern.compile(charEx);
        Matcher mChar = pChar.matcher(str);
        if (mChar.matches()) {
            return false;
        }

        return true;
    }


    /**
     * Check whether the password contains more than two types of characters
     *
     * 判断密码是否包含两种以上字符
     */
    public static boolean isMultiCharacterPassword(String str) {
        String numEx = "[0-9]*"; // 不能纯数字
        Pattern pNum = Pattern.compile(numEx);
        Matcher mNum = pNum.matcher(str);
        if (mNum.matches()) {
            return false;
        }

        String lowEx = "[a-z]*"; //不能纯小写英语
        Pattern pLow = Pattern.compile(lowEx);
        Matcher mLow = pLow.matcher(str);
        if (mLow.matches()) {
            return false;
        }

        String highEx = "[A-Z]*"; //不能纯大写英语
        Pattern pHigh = Pattern.compile(highEx);
        Matcher mHigh = pHigh.matcher(str);
        if (mHigh.matches()) {
            return false;
        }

        //27个特殊符号ASC码：33、35、36、37、40、41、42、43、44、45、46、47、60、61、62、63、64、91、92、93、94、95、96、123、124、125、126
        String charEx = "[\\!\\#\\$\\%\\(\\)\\*\\+\\,\\-\\.\\/\\<\\=" +
                "\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~]*";
        Pattern pChar = Pattern.compile(charEx);
        Matcher mChar = pChar.matcher(str);
        if (mChar.matches()) {
            return false;
        }

        return true;
    }

    /**
     * Get password strength score
     * @param password  password
     *
     * 获取密码强度分数
     * @param password  密码
     */
    public static int getPwdStrength(String password){
        //遍历计算字符串中的数字个数、大写字母的个数、小写字母个数、符号个数
        int strength = 0;
        int lowerCaseNum = 0;
        int upperCaseNum = 0;
        int digitNum = 0;
        for (int i = 0;i<password.length();i++){
            if(Character.isDigit(password.charAt(i))){
                digitNum++;
            }else if(Character.isLowerCase(password.charAt(i))){
                lowerCaseNum++;
            }else if(Character.isUpperCase(password.charAt(i))){
                upperCaseNum++;
            }
        }
        int  characterNum = password.length() - lowerCaseNum - upperCaseNum - digitNum;

        //密码长度
        if (password.length() <= 4)
            strength = 5;
        else if(password.length() <= 7)
            strength = 10;
        else if(password.length() >= 8)
            strength = 25;

        //字母
        if (!(lowerCaseNum == 0 && upperCaseNum == 0)){
            strength = strength + 10;
            if(lowerCaseNum > 0 && upperCaseNum > 0)
                strength = strength + 10;
        }

        //数字
        if (digitNum == 1)
            strength = strength + 10;
        else if (digitNum > 1)
            strength = strength + 20;

        //符号
        if (characterNum == 1)
            strength = strength + 10;
        else if (characterNum > 1)
            strength = strength + 25;

        int awardScore = getAwardScore(upperCaseNum, lowerCaseNum, digitNum, characterNum);
        strength += awardScore;
        return strength;
    }

    /**
     * Five: reward rules test letters and symbols
     * 2 points: two character type combinations
     * 3 points: combination of three character types
     * 5 points: four character types
     *
     * 五：奖励规则则 检验字母和符号组合
     * 2 分: 两种字符类型组合
     * 3 分: 三种字符类型组合
     * 5 分: 四种字符类型组合
     */
    private static int getAwardScore(int upperCaseLetterCount, int lowerCaseLetterCount, int numberCount, int symbolCount) {

        upperCaseLetterCount = upperCaseLetterCount > 0 ? 1 : 0;
        lowerCaseLetterCount = lowerCaseLetterCount > 0 ? 1 : 0;
        numberCount = numberCount > 0 ? 1 : 0;
        symbolCount = symbolCount > 0 ? 1 : 0;
        switch (upperCaseLetterCount + lowerCaseLetterCount + numberCount + symbolCount) {
            case 2:
                return AWARD_2;
            case 3:
                return AWARD_3;
            case 4:
                return AWARD_5;
            default:
                return 0;
        }

    }

}
