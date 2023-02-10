package com.opensdk.devicedetail.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mm.android.mobilecommon.base.BaseDialogFragment;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.AbstractWheel;
import com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.OnWheelChangedListener;
import com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import com.opensdk.devicedetail.R;

/**
 * Created by zhengcong on 2018/9/17.
 */

public class PeriodSelectDialog extends BaseDialogFragment implements DialogInterface.OnKeyListener
,View.OnClickListener{

    private final String format = "%02d";

    private TextView mConfirmBtn;
    private TextView mCancelBtn;
    private AbstractWheel mBeginHourWheel;
    private AbstractWheel mBeginMinuteWheel;
    private AbstractWheel mEndHourWheel;
    private AbstractWheel mEndMinuteWheel;
    private PeriodSelectListener mListener;
    private int mBeginHour = 0;
    private int mBeginMinute = 0;
    private int mEndHour = 0;
    private int mEndMinute = 0;

    public interface PeriodSelectListener{
        void onPeriodConfirm(int beginHour, int beginMinute, int endHour, int endMinute, BaseDialogFragment fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL , R.style.mobile_common_checks_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_period_select, container, false);

        initView(view);
        initData();
        initWheels();

        return view;
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mBeginHour = bundle.getInt(LCConfiguration.BEGIN_HOUR , 0);
            mBeginMinute =  bundle.getInt(LCConfiguration.BEGIN_MINUTE, 0);
            mEndHour = bundle.getInt(LCConfiguration.END_HOUR, 0);
            mEndMinute = bundle.getInt(LCConfiguration.END_MINUTE, 0);
        }
    }

    private void initView(View view) {
        mConfirmBtn = (TextView) view.findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm(v);
            }
        });
        mCancelBtn = (TextView) view.findViewById(R.id.cancal_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel(v);
            }
        });

        mBeginHourWheel = (AbstractWheel) view.findViewById(R.id.begin_hour_wheel);
        mBeginMinuteWheel = (AbstractWheel) view.findViewById(R.id.begin_minute_wheel);
        mEndHourWheel = (AbstractWheel) view.findViewById(R.id.end_hour_wheel);
        mEndMinuteWheel = (AbstractWheel) view.findViewById(R.id.end_minute_wheel);
    }

    private void initWheels() {
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(getActivity(), 0, 23, format);
        hourAdapter.setTextSize(20);
        hourAdapter.setItemResource(R.layout.item_effective_time_hour_list);
        hourAdapter.setItemTextResource(R.id.text);

        NumericWheelAdapter minuteAdapter = new NumericWheelAdapter(getActivity(), 0, 59, format);
        minuteAdapter.setTextSize(20);
        minuteAdapter.setItemResource(R.layout.item_effective_time_minute_list);
        minuteAdapter.setItemTextResource(R.id.text);

        mBeginHourWheel.setViewAdapter(hourAdapter);
        mBeginHourWheel.setCyclic(true);
        mBeginHourWheel.setInterpolator(new AnticipateOvershootInterpolator());
        mBeginHourWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                mBeginHour = newValue;
            }
        });

        mBeginHourWheel.setCurrentItem(mBeginHour);

        mBeginMinuteWheel.setViewAdapter(minuteAdapter);
        mBeginMinuteWheel.setCyclic(true);
        mBeginMinuteWheel.setInterpolator(new AnticipateOvershootInterpolator());
        mBeginMinuteWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                mBeginMinute = newValue;
            }
        });

        mBeginMinuteWheel.setCurrentItem(mBeginMinute);

        mEndHourWheel.setViewAdapter(hourAdapter);
        mEndHourWheel.setCyclic(true);
        mEndHourWheel.setInterpolator(new AnticipateOvershootInterpolator());
        mEndHourWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                mEndHour = newValue;
            }
        });

        mEndHourWheel.setCurrentItem(mEndHour);

        mEndMinuteWheel.setViewAdapter(minuteAdapter);
        mEndMinuteWheel.setCyclic(true);
        mEndMinuteWheel.setInterpolator(new AnticipateOvershootInterpolator());
        mEndMinuteWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                mEndMinute = newValue;
            }
        });

        mEndMinuteWheel.setCurrentItem(mEndMinute);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);

        Window win = getDialog().getWindow();
        if (win != null) {
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams localLayoutParams = win.getAttributes();
            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            localLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            localLayoutParams.gravity = Gravity.BOTTOM;
            getDialog().getWindow().setAttributes(localLayoutParams);
        }
    }

    public void setPeriodSelectListener(PeriodSelectListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.confirm_btn){
            onConfirm(v);
        }if(v.getId() == R.id.cancal_btn){
            onCancel(v);
        }
    }


    public void onConfirm(View view){
        if(mListener != null){
            mListener.onPeriodConfirm(mBeginHour, mBeginMinute, mEndHour, mEndMinute , this);
        }
    }


    public void onCancel(View view){
        dismiss();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dismissAllowingStateLoss();
            return false;
        }
        return false;
    }
}
