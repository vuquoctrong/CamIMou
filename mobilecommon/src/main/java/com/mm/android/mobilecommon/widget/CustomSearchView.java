package com.mm.android.mobilecommon.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.utils.LogUtil;

/**
 * Universal search box
 *
 * 通用搜索框
 */

public class CustomSearchView extends RelativeLayout implements View.OnClickListener,
        ClearEditText.IFocusChangeListener, ClearEditText.ITextChangeListener,
        TextView.OnEditorActionListener {
    private ClearEditText mSearchInput;
    private TextView mCancelTv;
    private InputMethodManager mInputMethodManager;
    private IOnTextChangeListener mTextChangeListener;
    private IOnFocusChangeListener mFocusChangeListener;

    public CustomSearchView(Context context) {
        this(context, null);
    }

    public CustomSearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public interface IOnFocusChangeListener {
        void onSearchFocusChange(View v, boolean hasFocus);
    }

    public void setOnSearchFocusChangeListener(IOnFocusChangeListener listener) {
        mFocusChangeListener = listener;
    }

    public interface IOnTextChangeListener {
        void onTextChange(View v, CharSequence ch);
    }

    public void setOnTextChangedListener(IOnTextChangeListener listener) {
        mTextChangeListener = listener;
    }

    public CustomSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.mobile_common_search_view, this);
        initView();
        initData();
    }

    private void initView() {
        mSearchInput =  findViewById(R.id.common_search_input);
        mCancelTv =  findViewById(R.id.common_search_cancel);

        mCancelTv.setOnClickListener(this);
        mSearchInput.setTextChangeListener(this);
        mSearchInput.setOnClickListener(this);
        mSearchInput.setClearTextFocusChange(this);
        mSearchInput.setOnEditorActionListener(this);
    }

    private void initData() {
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.common_search_cancel) {
            mSearchInput.clearFocus();
        } else if (id == R.id.common_search_input) {
            mSearchInput.requestFocus();
        }
    }

    @Override
    public void onClearTextFocusChange(View v, boolean hasFocus) {
        LogUtil.debugLog("33084", " onClearTextFocusChange hasFocus = " + hasFocus);
        if (hasFocus) {
            mCancelTv.setVisibility(View.VISIBLE);
        } else {
            mCancelTv.setVisibility(View.GONE);
            mSearchInput.setText("");
            mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        if (mFocusChangeListener != null) {
            mFocusChangeListener.onSearchFocusChange(v, hasFocus);
        }
    }

    @Override
    public void afterChanged(EditText v, Editable s) {

    }

    @Override
    public void beforeChanged(EditText v, CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(EditText v, CharSequence text, int start, int lengthBefore, int lengthAfter) {
        LogUtil.debugLog("33084", " onTextChanged");
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(v, text);
        }
    }

    @Override
    public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (mInputMethodManager != null) {
                mInputMethodManager.hideSoftInputFromWindow(tv.getWindowToken(), 0);
            }
            return true;
        }
        return false;
    }

    /**
     * Setting Prompts
     * @param resId
     *
     * 设置提示语
     * @param resId
     */
    public void setSearchTextHint(int resId) {
        mSearchInput.setHint(getResources().getString(resId));
    }

    /**
     * Gets the contents of the input box
     *
     * 获取输入框内容
     */
    public String getEditText() {
        return mSearchInput.getText().toString();
    }

    private void onDestroyView() {
        if (mInputMethodManager != null) {
            mInputMethodManager = null;
        }
        if (mTextChangeListener != null) {
            mTextChangeListener = null;
        }
    }
}
