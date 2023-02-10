package com.mm.android.deviceaddmodule.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.mm.android.deviceaddmodule.R;

public class NotSupport5GTipDialog extends Dialog {

    public NotSupport5GTipDialog(@NonNull Context context) {
        super(context, R.style.sign_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_not_support_g_tip);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        findViewById(R.id.confirm_btn).setOnClickListener(new mClickListener());
    }


    private class mClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.confirm_btn) {
                dismiss();
            }
        }
    }
}
