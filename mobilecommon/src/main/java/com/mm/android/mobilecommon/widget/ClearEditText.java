package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.utils.StringUtils;

public class ClearEditText extends AppCompatEditText implements OnFocusChangeListener, TextWatcher,Callback {

    // EditTxt右侧的删除按钮
    private Drawable mClearDrawable;

    private boolean hasFous;

    private int mLimitedLen = -1;

    private boolean mbPWDFilter = false;

    private boolean mbUnregFilter = false;

    private TextWatcher mTextWatcher;

    private ITextChangeListener mListener;

    private IFocusChangeListener mFocusListener;

    private boolean isVisiableClearIcon = true;

    public interface IFocusChangeListener{
        public void onClearTextFocusChange(View v, boolean hasFocus);
    }

    public interface ITextChangeListener {

        public void afterChanged(EditText v, Editable s);

        public void beforeChanged(EditText v, CharSequence s, int start, int count, int after);
        
        public void onTextChanged(EditText v, CharSequence text, int start, int lengthBefore, int lengthAfter);
    }

    public void setClearTextFocusChange(IFocusChangeListener listener){
        mFocusListener = listener;
    }

    @SuppressWarnings("unchecked")
    public void setTextChangeListener(ITextChangeListener l) {
        mListener = l;
    }

    public void setTextWathcher(TextWatcher textWatcher) {
        mTextWatcher = textWatcher;
    }

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 获取EditText的DrawableRight, 假如没有设置我们就使用默认的图片，获取图片的顺序是左上右下（0.1.2.3）
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.ic_password_clear);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());

        //开启安全监控
        setFilterTouchesWhenObscured(true);

        // 默认设置隐藏图标
        setClearIconVisible(false);

        // 设置焦点改变的监听
        setOnFocusChangeListener(this);

        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);

        // 默认设置不能copy
//        setCopyAble(false);
    }

    /**
     * @explain： isInnerWidth, isInnerHeight is true，The touch point is inside the delete icon, event.getX() obtains the X coordinate of the upper left corner of the corresponding itself.
     * event.getY() obtains the Y coordinate of the upper left corner of the corresponding itself.
     * getWidth() obtains the width of the control getTotalPaddingRight() Gets the distance from the left edge of the icon to the right edge of the control.
     * getPaddubgRight() gets the distance from the right edge of the icon to the right edge of the control.
     * getWidth() getTotalPaddingRight() calculates the distance from the left edge of the icon to the left edge of the control.
     * getWidth() -getPaddingRight () calculates the distance from the right edge of the icon to the left edge of the control.
     * distance Deletes the distance between the top edge of the icon and the top edge of the control distance + height Deletes the distance between the bottom edge of the icon and the top edge of the control
     *
     * @说明： isInnerWidth, isInnerHeight为true， 触摸点在删除图标之内，则视为点击了删除图标 event.getX() 获取相应自身左上角的X坐标
     *      event.getY() 获取相应自身左上角的Y坐标 getWidth() 获取控件的宽度 getHight() 获取控件的高度 getTotalPaddingRight()
     *      获取删除图标左边边缘到控件右边缘的距离 getPaddubgRight() 获取删除图标右边边缘到控件右边缘的距离 isInnerWidth: getWidth() -
     *      getTotalPaddingRight() 计算删除图标左边缘到控件左边缘的距离 getWidth() - getPaddingRight()
     *      计算删除图标右边缘到控件左边缘的距离 isInnerHight: `distance 删除图标顶部边缘带控件顶部边缘的距离 distance + height
     *      删除图标底部边缘到控件顶部边缘的距离
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mClearDrawable != null) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = mClearDrawable.getBounds();
                int height = rect.height();
                int distance = (getHeight() - height) / 2;
                boolean isInnerWidth = x > (getWidth() - getTotalPaddingRight())
                        && x < (getWidth() - getPaddingRight());
                boolean isInnerHeight = y > distance && y < (distance + height);

                if (isInnerHeight && isInnerWidth) {
                    setText("");
                }

            }
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    }

    public void setClearIconVisible(boolean visible) {
        if(visible && !isVisiableClearIcon){
            return;
        }

        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    public boolean isVisiableClearIcon() {
        return isVisiableClearIcon;
    }

    public void setVisiableClearIcon(boolean visiableClearIcon) {
        isVisiableClearIcon = visiableClearIcon;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFous = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }

        if(mFocusListener != null){
            mFocusListener.onClearTextFocusChange(v, hasFocus);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        removeTextChangedListener(this);
        if (mListener != null) {
            mListener.beforeChanged(this, s, start, count, after);
        }

        if (mTextWatcher != null) {
            mTextWatcher.beforeTextChanged(s, start, count, after);
        }

        addTextChangedListener(this);

    }

    @Override
    public void afterTextChanged(Editable s) {
        removeTextChangedListener(this);
        filter(s);
        if (mListener != null) {
            mListener.afterChanged(this, s);
        }

        if (mTextWatcher != null) {
            mTextWatcher.afterTextChanged(s);
        }

        addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    	
        removeTextChangedListener(this);
        
        if (mListener != null) {
            mListener.onTextChanged(this, text, start, lengthBefore, lengthAfter);
        }
        
        if (mTextWatcher != null) {
            mTextWatcher.onTextChanged(text, start, lengthBefore, lengthAfter);
        }
        addTextChangedListener(this);
        
        if (hasFous) {
            setClearIconVisible(getText().toString().length() > 0);
        }

    }
    
    /* 功能区 */

    private void filter(Editable s) {
        String str = s.toString();
        int indexStart = ClearEditText.this.getSelectionStart();

        if (mbPWDFilter) {
            str = StringUtils.strPwdFilter(str);
            int iDelLen = s.length() - str.length();
            if (iDelLen > 0) {
                ClearEditText.this.setText(str);
                if (indexStart - iDelLen >= 0 && indexStart - iDelLen <= str.length()) {
                    ClearEditText.this.setSelection(indexStart - iDelLen);
                    indexStart -= iDelLen;
                }
            }
        } else if (mbUnregFilter) {
            str = StringUtils.strFilter(s.toString());
            int iDelLen = s.length() - str.length();
            if (iDelLen > 0) {
                ClearEditText.this.setText(str);
                if (indexStart - iDelLen >= 0 && indexStart - iDelLen <= str.length()) {
                    ClearEditText.this.setSelection(indexStart - iDelLen);
                    indexStart -= iDelLen;
                }
            }
        }

        limitLenght(str, indexStart);
    }

    /**
     * Set the maximum input length, 1 Chinese counts two English, and automatically shield irregular characters
     * @param maxlen
     *
     * 设置最大输入长度，1个中文计两个英文，且自动屏蔽不规则字符
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
     * Whether the input box can copy | paste (this method is only suitable for API11) under
     * @param copyAble
     *
     * 输入框是否可以复制|粘贴(该方法只适合API11下的)
     * @param copyAble
     */
    public void setCopyAble(boolean copyAble) {
        this.setLongClickable(copyAble);
        if(copyAble){
            setCustomSelectionActionModeCallback(null);
        }else{
            setCustomSelectionActionModeCallback(this);
            setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        }
    }

    /**
     * Whether to restrict special character input
     * @param enbale
     *
     * 是否限制特殊字符输入
     * @param enbale
     */
    public void setUnregFilterEnbale(boolean enbale) {
        mbUnregFilter = enbale;
    }

    /**
     * Specifies whether to enter special characters in Chinese or system. This parameter is used to enter a password
     * @param enbale
     *
     * 是否限中文及制特殊字符输入，常用于密码输入
     * @param enbale
     */
    public void setCHFilterEnbale(boolean enbale) {
        mbPWDFilter = enbale;
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
                setText(str);
                if (indexStart >= 0 && indexStart <= str.length()) {
                    setSelection(indexStart);
                }
            }
        }
    }

    private int calcultateLength(CharSequence c) {
        int len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len++;
            } else {
                len += 2;
            }
        }
        return len;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        
    }

}
