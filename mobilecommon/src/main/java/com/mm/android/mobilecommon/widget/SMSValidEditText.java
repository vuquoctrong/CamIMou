package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mm.android.mobilecommon.R;

public class SMSValidEditText extends LinearLayout implements OnClickListener{

	private final static int TIME = 60000 + 500;
	
	private EditText mSMSValidEt;

	private TextView mTimeCountTv;
	
	private TimeCount mTimeCount;
	
	private SMSValidOnclick mListener;
	
	public interface SMSValidOnclick{
		
		public void requestValidateCode();
	}
	
	public void setSMSValidOnclick(SMSValidOnclick listener){
		mListener = listener;
	}

	public SMSValidEditText(Context context) {
		this(context, null);
	}

	public SMSValidEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SMSValidEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		LayoutInflater.from(context).inflate(R.layout.sms_valid_view, this);
		initView();
		
		mListener = new DefaultSMSValidOnclick();
		mTimeCount = new TimeCount(TIME, 1000);

	}
	
	public void startTimeCount(){
		mTimeCount.start();
	}

	private void initView() {
		mSMSValidEt = findViewById(R.id.et_sms_valid);

		mTimeCountTv = findViewById(R.id.tv_sms_valid_tip);
		
		mTimeCountTv.setOnClickListener(this);
	}
	
	public String getValidCode(){
		return mSMSValidEt.getText().toString();
	}

	public void setValidText(int valid){
		mTimeCount.cancel();
		mTimeCountTv.setText(valid);
		mTimeCountTv.setEnabled(true);
		mTimeCountTv.setClickable(true);
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mTimeCountTv.setClickable(false);
			mTimeCountTv.setEnabled(false);
			millisUntilFinished -= 650;
			mTimeCountTv.setText(getResources().getString(R.string.valid_code_retrieve_valid_num, millisUntilFinished / 1000));
			
			if((millisUntilFinished / 1000) == 0){
				mTimeCountTv.setText(getResources().getString(R.string.device_manager_regain));
				mTimeCountTv.setEnabled(true);
				mTimeCountTv.setClickable(true);
			}
			
		}

		@Override
		public void onFinish() {
			mTimeCountTv.setText(getResources().getString(R.string.device_manager_regain));
			mTimeCountTv.setEnabled(true);
			mTimeCountTv.setClickable(true);
			
		}

	}

	@Override
	public void onClick(View v) {
		mListener.requestValidateCode();
	}
	
	//取消倒计时
	public void cancelCountDown(){
		if(mTimeCount != null){
			mTimeCount.cancel();
		}
	}
	
	public void addTextWatcher(TextWatcher watcher){
		mSMSValidEt.addTextChangedListener(watcher);
	}
	
	public void setHintText(int hint){
		mSMSValidEt.setHint(hint);
	}

	public void setFilters(InputFilter[] inputFilters) {
		mSMSValidEt.setFilters(inputFilters);
	}
	
	static class DefaultSMSValidOnclick implements SMSValidOnclick{

		@Override
		public void requestValidateCode() {
			
		}
	}

}
