package com.mm.android.mobilecommon.utils;

import android.text.InputFilter;
import android.text.Spanned;


public class NameLengthFilter implements InputFilter {

    private final int mMax;

    public NameLengthFilter(int max) {
        mMax = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int destCount = getWordCountRegex(dest.toString());
        int sourceCount = getWordCountRegex(source.toString());
        if(destCount + sourceCount > mMax){
            int surplusCount = mMax - destCount;
            String result = "";
            int resultCount = 0;
            int index = 0;
            while (surplusCount > resultCount){
                result += source.charAt(index);
                resultCount = getWordCountRegex(result);
                index++;
            }

            if(surplusCount == resultCount){
                return source.subSequence(start, index);
            }else{
                return source.subSequence(start, index - 1);
            }

        }else{
            return source;
        }

    }

    private int getWordCountRegex(String str){
        if(str == null){
            return 0;
        }

        str = str.replaceAll("[^\\x00-\\xff]" , "**");
        return str.length();
    }
}
