package com.mm.android.mobilecommon.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.base.BaseDialogFragment;
import com.mm.android.mobilecommon.utils.UIUtils;

public class LCAlertDialog extends BaseDialogFragment implements OnClickListener {

    public static final int CANCEL_BUTTON = 0;

    public static final int CONFIRM_BUTTON = 1;

    public static final int MESSAGE2_TEXT = 2;

    private CharSequence mMessage;

    private CharSequence mMessage2;

    private String mTitle;

    private int mTitleColor = -1;

    private String mNagativeBtnName;

    private String mPositiveBtnName;

    private boolean mIsCheckBoxEnable = false;

    private String mCheckBoxText;

    private OnClickListener mMessage2BtnListener;

    private OnClickListener mNagativeBtnListener;

    private OnClickListener mPositiveBtnListener;

    private OnClickListener mNeverRemindBtnListener;

    private OnDismissListener mDismissListener;

    private TextView mTitleTv;

    private TextView mMessageTv;

    private TextView mMessageTv2;

    private CheckBox mNeverRemindBtn;

    private TextView mLeftBtn;

    private TextView mRightBtn;

    private LinearLayout mTwoButtonLayout;

    private TextView mSingleBtn;
    private Integer mMessageGravity;

    public interface DialogShowListener {
        public void onShow();
    }

    private DialogShowListener mDialogShowListener;

    public interface OnClickListener {
        public void onClick(LCAlertDialog dialog, int which, boolean isChecked);
    }

    boolean canCanceledOnTouchOutside = false;

    public void setCanceledOnTouchOutside(boolean canCanceledDialogOnTouchOutside) {
        canCanceledOnTouchOutside = canCanceledDialogOnTouchOutside;
    }

    private void setMessage(CharSequence message) {
        mMessage = message;
    }

    private void setMessage2(CharSequence message2, OnClickListener listener) {
        mMessage2 = message2;
        mMessage2BtnListener = listener;
    }

    private void setTitle(String message) {
        mTitle = message;
    }

    private void setTitleColor(int titleColor) {
        mTitleColor = titleColor;
    }

    private void setNegativeButton(String name, OnClickListener l) {
        mNagativeBtnName = name;

        mNagativeBtnListener = l;
    }

    private void setPositiveButton(String name, OnClickListener l) {
        mPositiveBtnName = name;

        mPositiveBtnListener = l;
    }

    private void setOnDismissListener(OnDismissListener l) {
        mDismissListener = l;
    }

    private void setDialogShowListener(DialogShowListener l) {
        mDialogShowListener = l;
    }

    private void setCheckBoxEnable(boolean isEnable, OnClickListener l) {
        mIsCheckBoxEnable = isEnable;
        mNeverRemindBtnListener = l;
    }

    private void setCheckBoxText(String name) {
        mCheckBoxText = name;
    }
    private void setGravity2(Integer gravity) {
        mMessageGravity = gravity;
    }

    private void setGravity(String name) {
        mCheckBoxText = name;
    }

    public static LCAlertDialog newInstance() {
        return new LCAlertDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.mobile_common_checks_dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mobile_common_lc_alert_dialog_layout, null);
        initView(view);
        if (mDialogShowListener != null) {
            mDialogShowListener.onShow();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        translationUp();
    }

    private void translationUp() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            WindowManager.LayoutParams params = null;
            try {
                params = getDialog().getWindow().getAttributes();
            } catch (Exception e) {
            }
            if (params != null) {
                params.y = (int) -(100 * UIUtils.getScreenDensity(getActivity()) / 3.0f);
                DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
                int configure = getResources().getConfiguration().orientation;
                if (configure == Configuration.ORIENTATION_PORTRAIT) {
                    params.width = metrics.widthPixels * 4 / 5;
                } else if (configure == Configuration.ORIENTATION_LANDSCAPE) {
                    params.width = metrics.heightPixels * 4 / 5;
                } else {
                    params.width = metrics.widthPixels * 4 / 5;
                }

                getDialog().getWindow().setAttributes(params);
            }

        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(canCanceledOnTouchOutside);
    }

    private void initView(View rootView) {
        // rootView.setMinimumWidth(UIUtils.getDefaultDialogWidth(getActivity()));
        mTitleTv = rootView.findViewById(R.id.tv_title);
        mMessageTv = rootView.findViewById(R.id.tv_message);
        mMessageTv2 = rootView.findViewById(R.id.tv_message2);
        mMessageTv2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mMessageTv2.getPaint().setAntiAlias(true);//抗锯齿
        mMessageTv2.setTextColor(getActivity().getResources().getColor(R.color.c5));
        mLeftBtn = rootView.findViewById(R.id.tv_left_btn);
        mRightBtn = rootView.findViewById(R.id.tv_right_btn);
        mSingleBtn = rootView.findViewById(R.id.tv_single_btn);
        mNeverRemindBtn = rootView.findViewById(R.id.rb_never_remind);

        mMessageTv2.setOnClickListener(this);
        mNeverRemindBtn.setOnClickListener(this);
        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        mSingleBtn.setOnClickListener(this);

        mTwoButtonLayout = rootView.findViewById(R.id.two_button_layout);

        if (!TextUtils.isEmpty(mTitle)) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(mTitle);
        } else {
            mTitleTv.setVisibility(View.GONE);
        }

        if (mTitleColor != -1) {
            mTitleTv.setTextColor(mTitleColor);
        }

        if (mMessageGravity != null) {
            mMessageTv.setGravity(mMessageGravity);
        }

        if (!TextUtils.isEmpty(mMessage)) {
            mMessageTv.setVisibility(View.VISIBLE);
            mMessageTv.setText(mMessage);
        } else {
            mMessageTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mMessage2)) {
            mMessageTv2.setVisibility(View.VISIBLE);
            mMessageTv2.setText(mMessage2);
        } else {
            mMessageTv2.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mNagativeBtnName) && !TextUtils.isEmpty(mPositiveBtnName)) {
            mTwoButtonLayout.setVisibility(View.VISIBLE);
            mSingleBtn.setVisibility(View.GONE);

            mLeftBtn.setText(mNagativeBtnName);
            mRightBtn.setText(mPositiveBtnName);

        } else {
            mTwoButtonLayout.setVisibility(View.GONE);
            mSingleBtn.setVisibility(View.VISIBLE);
            mSingleBtn.setOnClickListener(this);
            if (!TextUtils.isEmpty(mNagativeBtnName)) {
                mSingleBtn.setText(mNagativeBtnName);
            } else if (!TextUtils.isEmpty(mPositiveBtnName)) {
                mSingleBtn.setText(mPositiveBtnName);
            }
        }

        if (mIsCheckBoxEnable) {
            mNeverRemindBtn.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(mCheckBoxText)) {
            mNeverRemindBtn.setText(mCheckBoxText);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(dialog);
        }
        if (mNeverRemindBtn != null) mNeverRemindBtn.setOnClickListener(null);
        if (mLeftBtn != null) mLeftBtn.setOnClickListener(null);
        if (mRightBtn != null) mRightBtn.setOnClickListener(null);
        if (mSingleBtn != null) mSingleBtn.setOnClickListener(null);
        mDismissListener = null;
        mDialogShowListener = null;
        mPositiveBtnListener = null;
        mNagativeBtnListener = null;
        mMessage2BtnListener = null;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.tv_left_btn) {

            if (mNagativeBtnListener != null) {
                mNagativeBtnListener.onClick(this, CANCEL_BUTTON, mNeverRemindBtn.isChecked());
            }
            dismissAllowingStateLoss();

        } else if (i == R.id.tv_right_btn) {

            if (mPositiveBtnListener != null) {
                mPositiveBtnListener.onClick(this, CONFIRM_BUTTON, mNeverRemindBtn.isChecked());
            }
            dismissAllowingStateLoss();

        } else if (i == R.id.tv_single_btn) {

            if (!TextUtils.isEmpty(mNagativeBtnName)) {
                if (mNagativeBtnListener != null) {
                    mNagativeBtnListener.onClick(this, CANCEL_BUTTON, mNeverRemindBtn.isChecked());
                }
            } else if (!TextUtils.isEmpty(mPositiveBtnName)) {
                if (mPositiveBtnListener != null) {
                    mPositiveBtnListener.onClick(this, CONFIRM_BUTTON, mNeverRemindBtn.isChecked());
                }
            }
            dismissAllowingStateLoss();
        } else if (i == R.id.rb_never_remind) {
            if (mNeverRemindBtnListener != null) {
                mNeverRemindBtnListener.onClick(this, CONFIRM_BUTTON, mNeverRemindBtn.isChecked());
            }
        } else if (i == R.id.tv_message2) {
            if (mMessage2BtnListener != null) {
                mMessage2BtnListener.onClick(this, MESSAGE2_TEXT, mNeverRemindBtn.isChecked());
            }
        }
    }

    public static class Builder {

        private Context mContext;

        private CharSequence mMessage;

        private CharSequence mMessage2;

        private String mTitle;

        private int mTitleColor = -1;

        private String mNagativeBtnName;

        private String mPositiveBtnName;

        private String mCheckBoxText;

        private Integer gravity2;

        private boolean mIsCheckBoxEnable = false;

        private OnClickListener mMessage2TextListener;

        private OnClickListener mNagativeBtnListener;

        private OnClickListener mPositiveBtnListener;

        private OnClickListener mNeverRemindBtnListener;

        private OnDismissListener mDismissListener;
        private DialogShowListener mDialogShowListener;

        //点击返回键默认可以取消弹框
        private boolean mCancelable = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public Builder setMessage(int resId) {
            mMessage = mContext.getResources().getString(resId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage2(int resId, OnClickListener l) {
            mMessage2 = mContext.getResources().getString(resId);
            mMessage2TextListener = l;
            return this;
        }

        public Builder setMessage2(CharSequence message) {
            mMessage2 = message;
            return this;
        }

        public Builder setGravity2(int gravity) {
            gravity2 = gravity;
            return this;
        }

        public Builder setTitle(int resId) {
            mTitle = mContext.getResources().getString(resId);
            return this;
        }

        public Builder setTitle(String message) {
            mTitle = message;
            return this;
        }

        public Builder setTitleColor(int resId) {
            mTitleColor = mContext.getResources().getColor(resId);
            return this;
        }

        public Builder setCancelButton(int resId, OnClickListener l) {
            mNagativeBtnName = mContext.getResources().getString(resId);

            mNagativeBtnListener = l;

            return this;
        }

        public Builder setConfirmButton(int resId, OnClickListener l) {
            mPositiveBtnName = mContext.getResources().getString(resId);

            mPositiveBtnListener = l;

            return this;
        }

        public Builder setConfirmButton(String resText, OnClickListener l) {
            mPositiveBtnName = resText;
            mPositiveBtnListener = l;

            return this;
        }



        public Builder setOnDismissLisenter(OnDismissListener l) {
            mDismissListener = l;
            return this;
        }

        public Builder setDialogShowListener(DialogShowListener showListener) {
            mDialogShowListener = showListener;
            return this;
        }

        public Builder setCheckBoxEnable(boolean isEnable, OnClickListener l) {
            mIsCheckBoxEnable = isEnable;
            mNeverRemindBtnListener = l;

            return this;
        }

        public Builder setCheckBoxText(int resId) {
            mCheckBoxText = mContext.getResources().getString(resId);
            mIsCheckBoxEnable = true;
            return this;
        }


        public LCAlertDialog create() {
            LCAlertDialog dialog = LCAlertDialog.newInstance();
            if (mDismissListener != null) {
                dialog.setOnDismissListener(mDismissListener);
            }
            if (!TextUtils.isEmpty(mTitle)) {
                dialog.setTitle(mTitle);
            }
            if (mTitleColor != -1) {
                dialog.setTitleColor(mTitleColor);
            }

            if (!TextUtils.isEmpty(mMessage)) {
                dialog.setMessage(mMessage);
            }

            if (!TextUtils.isEmpty(mMessage2)) {
                dialog.setMessage2(mMessage2, mMessage2TextListener);
            }

            if (!TextUtils.isEmpty(mNagativeBtnName)) {
                dialog.setNegativeButton(mNagativeBtnName, mNagativeBtnListener);
            }

            if (!TextUtils.isEmpty(mPositiveBtnName)) {
                dialog.setPositiveButton(mPositiveBtnName, mPositiveBtnListener);
            }
            if (mDialogShowListener != null) {
                dialog.setDialogShowListener(mDialogShowListener);
            }
            if (!TextUtils.isEmpty(mCheckBoxText)) {
                dialog.setCheckBoxText(mCheckBoxText);
            }
            if (gravity2 != null) {
                dialog.setGravity2(gravity2);
            }
            dialog.setCheckBoxEnable(mIsCheckBoxEnable,mNeverRemindBtnListener);

            dialog.setCancelable(mCancelable);

            return dialog;
        }
    }

}
