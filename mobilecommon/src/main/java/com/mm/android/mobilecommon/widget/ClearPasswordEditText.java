package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import androidx.appcompat.widget.AppCompatEditText;

import com.mm.android.mobilecommon.R;

public class ClearPasswordEditText extends AppCompatEditText implements
        OnFocusChangeListener, TextWatcher, Callback {

	private Bitmap mClearDrawable, mOpenDrawable, mShutDrawable,
			mRightDrawable;

	private Paint mPaint;

	private boolean hasFous;

	private boolean mIsClearIconVisible = false;

	private boolean mIsPassWordShut = true;

	private TextWatcher mTextWatcher;

	private ClearEditText.ITextChangeListener mListener;

	/**
	 * Focus change listener, provided for external use
	 *
	 * 焦点改变监听，提供外部使用
	 */
	private ClearEditText.IFocusChangeListener mOnFocusChangeEXListener;

	/**
	 * Set focus change listening
	 *
	 * 设置焦点改变监听
	 */
	public void setOnFocusChangeEXListener(ClearEditText.IFocusChangeListener listener) {
		this.mOnFocusChangeEXListener = listener;
	}
	
	@SuppressWarnings("unchecked")
	public void setTextChangeListener(ClearEditText.ITextChangeListener l) {
		mListener = l;
	}

	public void setTextWathcher(TextWatcher textWatcher) {
		mTextWatcher = textWatcher;
	}

	public ClearPasswordEditText(Context context) {
		this(context, null);
	}

	public ClearPasswordEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}


	public ClearPasswordEditText(Context context, AttributeSet attrs,
                                 int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mClearDrawable = BitmapFactory.decodeResource(getResources(),
				R.drawable.mobile_common_icon_deleteinput);
		mOpenDrawable = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_password_visible);
		mShutDrawable = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_password_invisible);

		mRightDrawable = mShutDrawable;

		setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		setTypeface(Typeface.DEFAULT);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		//开启安全监控
		setFilterTouchesWhenObscured(true);

		// 密码输入框为自定义控件，初始化时调用setFilterTouchesWhenObscured(true)接口，开启安全监控
		setFilterTouchesWhenObscured(true);

		// 设置焦点改变的监听
		setOnFocusChangeListener(this);

		// 设置输入框里面内容发生改变的监听
		addTextChangedListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			if (mIsClearIconVisible) {
				isClearIconChange(x, y);
			}

			boolean change = isPassWordShutChange(x, y);
			if (change) {
				invalidate();
			}

		}

		return super.onTouchEvent(event);
	}

	private boolean isPassWordShutChange(int x, int y) {
		int height = mRightDrawable.getHeight();
		int distance = (getHeight() - height) / 10;
		boolean isInnerWidth = x > (getWidth() - mRightDrawable.getWidth() - getPaddingRight() - 5)
				&& x < (getWidth() - getPaddingRight() + 20);

		boolean isInnerHeight = y > distance - 20 && y < (distance + height) + 20;

	
		
		if (isInnerHeight && isInnerWidth) {
			int selIndexStart = getSelectionStart();
			int selIndexStop = getSelectionEnd();
			mIsPassWordShut = !mIsPassWordShut;
			if (mIsPassWordShut) {
				mRightDrawable = mShutDrawable;
				setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				setTypeface(Typeface.DEFAULT);

			} else {

				mRightDrawable = mOpenDrawable;
				setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				setTypeface(Typeface.DEFAULT);
			}
			setSelection(selIndexStart, selIndexStop);
			return true;
		}
		return false;
	}

	private void isClearIconChange(int x, int y) {
		int height = mClearDrawable.getHeight();
		int distance = (getHeight() - height) / 2;
		boolean isInnerWidth = x > (getWidth() - (mRightDrawable.getWidth()
				+ mClearDrawable.getWidth() + getPaddingRight() + 10))
				&& x < (getWidth() - (mRightDrawable.getWidth()
						+ getPaddingRight() + 10));

		boolean isInnerHeight = y > distance && y < (distance + height);

		if (isInnerHeight && isInnerWidth) {
			setText("");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		
		
		canvas.drawBitmap(mRightDrawable,
				getScrollX() + getWidth() - mRightDrawable.getWidth() - getPaddingRight(),
				(getHeight() - mRightDrawable.getHeight()) / 2, mPaint);
		

		if (mIsClearIconVisible) {
			canvas.drawBitmap(
					mClearDrawable,
					getScrollX()+ getWidth()
							- (mRightDrawable.getWidth()
									+ mClearDrawable.getWidth()
									+ getPaddingRight() + 10),
					(getHeight() - mClearDrawable.getHeight()) / 2, mPaint);
		}
		
	}

	public int getCompoundPaddingRight() {
		int paddingRight = super.getCompoundPaddingRight();

		return paddingRight + mRightDrawable.getWidth() + mClearDrawable.getWidth() + 50;
	}

	public void setCopyAble(boolean copyAble) {
		this.setLongClickable(copyAble);
		if (copyAble) {
			setCustomSelectionActionModeCallback(null);
		} else {
			setCustomSelectionActionModeCallback(this);
		}
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

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
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
		if (mListener != null) {
			mListener.afterChanged(this, s);
		}

		if (mTextWatcher != null) {
			mTextWatcher.afterTextChanged(s);
		}

		addTextChangedListener(this);
	}

	@Override
	public void onTextChanged(CharSequence text, int start, int lengthBefore,
                              int lengthAfter) {
		removeTextChangedListener(this);

		if (mListener != null) {
			mListener.onTextChanged(this, text, start, lengthBefore,
					lengthAfter);
		}

		if (mTextWatcher != null) {
			mTextWatcher.onTextChanged(text, start, lengthBefore, lengthAfter);
		}
		addTextChangedListener(this);

		if (hasFous) {
			setClearIconVisible(getText().toString().length() > 0);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFous = hasFocus;

		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}

		if(mOnFocusChangeEXListener != null) {
			mOnFocusChangeEXListener.onClearTextFocusChange(v,hasFocus);
		}
	}

	private void setClearIconVisible(boolean b) {
		mIsClearIconVisible = b;
		invalidate();
	}

	public void openEyeMode(boolean open){
		mIsPassWordShut = !open;
		if (open) {
			mRightDrawable = mOpenDrawable;
			setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			setTypeface(Typeface.DEFAULT);
		} else {
			mRightDrawable = mShutDrawable;
			setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			setTypeface(Typeface.DEFAULT);
		}
		invalidate();

	}

}
