package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.mm.android.mobilecommon.utils.StringUtils;

public class ClearAutoCompleteText extends AppCompatAutoCompleteTextView {// delete the onFocusChangeListener,
                                                         // incorrect use, at 20150512
    private Drawable imgLeftFoucus = null;

    private Drawable imgLeftUnFoucus = null;

    private Drawable imgRightFoucus = null;

    private Drawable imgRightUnFoucus = null;

    private Context context;

    private int mLimitedLen = -1;

    private boolean mbUnregFilter = false;

    private boolean mbPWDFilter = false;

    private boolean mbFocus = false;

    private int errorTip;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ClearAutoCompleteText.this.removeTextChangedListener(mTextWatcher);
            filter(s);
            setDrawable();
            ClearAutoCompleteText.this.addTextChangedListener(mTextWatcher);
        }
    };

    public ClearAutoCompleteText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ClearAutoCompleteText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public ClearAutoCompleteText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        addTextChangedListener(mTextWatcher);
        // setOnFocusChangeListener(this); //not need, deleted at 20150512
        setDrawable();
    }

    /**
     * Whether the input box can copy | paste
     * @param copyAble
     *
     * ???????????????????????????|??????
     * @param copyAble
     */
    public void setCopyAble(boolean copyAble) {
        this.setLongClickable(copyAble);
    }

    /**
     * Set the image before and after the edit box
     * @param leftFocusResId When it is 0, it is not displayed. Otherwise, it is the resource file
     * @param leftUnFocusResId When the value is 0, it is not displayed. Otherwise, it takes the resource file
     * @param rightFocusResId If the value is 0, the resource file is not displayed
     * @param rightUnFocusResId If the value is 0, the resource file is not displayed
     *
     * ???????????????????????????
     * @param leftFocusResId
     *            ?????????????????????????????????0?????????????????????????????????????????????
     * @param leftUnFocusResId
     *            ?????????????????????????????????0?????????????????????????????????????????????
     * @param rightFocusResId
     *            ??????0?????????????????????????????????????????????
     * @param rightUnFocusResId
     *            ??????0?????????????????????????????????????????????
     */
    public void setDrawables(int leftFocusResId, int leftUnFocusResId, int rightFocusResId, int rightUnFocusResId) {

        if (leftFocusResId > 0) {
            imgLeftFoucus = context.getResources().getDrawable(leftFocusResId);
        }

        if (leftUnFocusResId > 0) {
            imgLeftUnFoucus = context.getResources().getDrawable(leftUnFocusResId);
        }

        if (rightFocusResId > 0) {
            imgRightFoucus = context.getResources().getDrawable(rightFocusResId);
        }

        if (rightUnFocusResId > 0) {
            imgRightUnFoucus = context.getResources().getDrawable(rightUnFocusResId);
        } else {
            imgRightUnFoucus = null;
        }

        setDrawable();
    }

    public void setFocus(boolean mFocus) {
        mbFocus = mFocus;
    }

    /**
     * Set delete image
     *
     * ??????????????????
     */
    private void setDrawable() {
        if (mbFocus) {
            if (length() == 0) {
                setCompoundDrawablesWithIntrinsicBounds(imgLeftFoucus, null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(imgLeftFoucus, null, imgRightFoucus, null);
            }
        } else {
            if (length() == 0) {
                setCompoundDrawablesWithIntrinsicBounds(imgLeftUnFoucus, null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(imgLeftUnFoucus, null, imgRightUnFoucus, null);
            }
        }
    }

    /**
     * event.getX() gets the X coordinate corresponding to its upper left corner event.
     *   getY() gets the Y coordinate corresponding to its upper left corner getWidth() gets the width of the control
     *
     * getTotalPaddingRight() Gets the distance from the left edge of the icon to the right edge of the control.
     *   getPaddingRight() gets the distance from the right edge of the icon to the right edge of the control. getWidth() -
     *
     * getTotalPaddingRight() calculates the distance from the left edge of the icon to the left edge of the control.
     *   getWidth() - getPaddingRight() calculates the distance from the right edge of the icon to the left edge of the control
     *
     *
     * event.getX() ?????????????????????????????????X?????? event.getY() ?????????????????????????????????Y?????? getWidth() ?????????????????????
     *
     * getTotalPaddingRight() ?????????????????????????????????????????????????????? getPaddingRight() ?????????????????????????????????????????????????????? getWidth() -
     *
     * getTotalPaddingRight() ?????????????????????????????????????????????????????? getWidth() - getPaddingRight() ??????????????????????????????????????????????????????
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgRightFoucus != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            // ???????????????????????????????????????
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight()))
                    && (x < (getWidth() - getPaddingRight()));
            // ??????????????????????????????????????????Rect??????
            Rect rect = imgRightFoucus.getBounds();
            // ???????????????????????????
            int height = rect.height();
            int y = (int) event.getY();
            // ??????????????????????????????????????????
            int distance = (getHeight() - height) / 2;
            // ???????????????????????????????????????(?????????????????????)
            // ????????????????????????distance??????distance+????????????????????????????????????????????????????????????
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            if (isInnerWidth && isInnerHeight && mbFocus) {
                setText("");
            }

        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void onFocusChangeSelf(View v, boolean hasFocus) {
        mbFocus = hasFocus;
        setDrawable();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {

        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        onFocusChangeSelf(this, focused);
    }

    /**
     * Set the maximum input length, 1 Chinese counts two English, and automatically shield irregular characters
     * @param maxlen
     *
     * ???????????????????????????1?????????????????????????????????????????????????????????
     * @param maxlen
     */
    public void setMaxLenth(int maxlen) {
        if (maxlen > 0) {
            mLimitedLen = maxlen;
        } else {
            mLimitedLen = 20;
        }
    }

    /**
     * Whether to restrict special character input
     * @param enable
     *
     * ??????????????????????????????
     * @param enable
     */
    public void setUnregFilterEnable(boolean enable) {
        mbUnregFilter = enable;
    }

    /**
     * Specifies whether to enter special characters in Chinese or system. This parameter is used to enter a password
     * @param enable
     *
     * ???????????????????????????????????????????????????????????????
     * @param enable
     */
    public void setCHFilterEnable(boolean enable) {
        mbPWDFilter = enable;
    }

    private int calcultateLength(CharSequence c) {
        int len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len++;
            } else {
                len += 2;
            }
        }
        return len;
    }

    private void filter(Editable s) {
        String str = s.toString();
        int indexStart = ClearAutoCompleteText.this.getSelectionStart();

        if (mbPWDFilter) {
            str = StringUtils.strPwdFilter(str);
            int iDelLen = s.length() - str.length();
            if (iDelLen > 0) {
                ClearAutoCompleteText.this.setText(str);
                if (indexStart - iDelLen >= 0 && indexStart - iDelLen <= str.length()) {
                    ClearAutoCompleteText.this.setSelection(indexStart - iDelLen);
                    indexStart -= iDelLen;
                }
            }
        } else if (mbUnregFilter) {
            str = StringUtils.strFilter(s.toString());
            int iDelLen = s.length() - str.length();
            if (iDelLen > 0) {
                ClearAutoCompleteText.this.setText(str);
                if (indexStart - iDelLen >= 0 && indexStart - iDelLen <= str.length()) {
                    ClearAutoCompleteText.this.setSelection(indexStart - iDelLen);
                    indexStart -= iDelLen;
                }
            }
        }

        limitLenght(str, indexStart);
    }

    private void limitLenght(String str, int indexStart) {
        if (mLimitedLen > 0) {

            boolean bFlag = false;
            if (calcultateLength(str) > mLimitedLen) {
                bFlag = true;
            }
            while (calcultateLength(str) > mLimitedLen) {
                if (indexStart > 0 && indexStart <= str.length()) {
                    str = str.substring(0, indexStart - 1) + str.substring(indexStart, str.length());
                } else {
                    str = str.substring(0, str.length() - 1);
                }
                indexStart = indexStart - 1;
            }

            if (bFlag) {
                ClearAutoCompleteText.this.setText(str);
                if (indexStart >= 0 && indexStart <= str.length()) {
                    ClearAutoCompleteText.this.setSelection(indexStart);
                }
            }
        }
    }

    public int getErrorTip() {
        return errorTip;
    }

    public void setErrorTip(int errorTip) {
        this.errorTip = errorTip;
    }

}