package com.mm.android.deviceaddmodule.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.base.BaseDialogFragment;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.utils.PreferencesHelper;

/**
 * Box scanning QR code added when the prompt page
 *
 * 乐盒扫描二维码添加时的提示页面
 */
public class AddBoxTipDialog extends BaseDialogFragment {
    private PreferencesHelper mPreferencesHelper = null;

    private CheckBox mViewNotShow = null;

    private TextView mViewClose = null;


    private DialogInterface.OnDismissListener mDismissListener;

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.mDismissListener = dismissListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Dialog dialog = new Dialog(getActivity(), R.style.checks_dialog);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.box_add_tip, null, false);
        init(view);
        dialog.setContentView(view);
        return dialog;
    }

    private void init(View mView) {
        mPreferencesHelper = PreferencesHelper.getInstance(getActivity());
        mViewNotShow = mView.findViewById(R.id.not_show);
        mViewClose = mView.findViewById(R.id.close);
        mViewNotShow.setChecked(mPreferencesHelper.getBoolean(LCConfiguration.SHOW_ADD_BOX_TIP));
        mViewClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewNotShow.isChecked()) {
                    mPreferencesHelper.set(LCConfiguration.SHOW_ADD_BOX_TIP, true);
                } else {
                    mPreferencesHelper.set(LCConfiguration.SHOW_ADD_BOX_TIP, false);
                }
                dismiss();
            }

        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(dialog);
        }
    }
}
