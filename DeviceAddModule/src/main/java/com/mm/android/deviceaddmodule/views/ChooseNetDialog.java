package com.mm.android.deviceaddmodule.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mm.android.deviceaddmodule.R;

public class ChooseNetDialog extends Dialog {

    public ChooseNetDialog(@NonNull Context context) {
        super(context, R.style.sign_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
        setContentView(R.layout.dialog_choose_net);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        findViewById(R.id.tv_softap).setOnClickListener(new mClickListener());
        findViewById(R.id.tv_wlan).setOnClickListener(new mClickListener());
        findViewById(R.id.tv_lan).setOnClickListener(new mClickListener());
        findViewById(R.id.tv_cancel).setOnClickListener(new mClickListener());
    }


    private class mClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.tv_softap) {
                if (lisenter != null) {
                    lisenter.softap();
                }
                dismiss();
            } else if (i == R.id.tv_wlan) {
                if (lisenter != null) {
                    lisenter.wlan();
                }
                dismiss();
            } else if (i == R.id.tv_lan) {
                if (lisenter != null) {
                    lisenter.lan();
                }
                dismiss();
            } else if (i == R.id.tv_cancel) {
                dismiss();
            }
        }
    }

    public interface OnChooseNetLisenter {
        /**
         * Soft AP
         *
         * 软AP
         */
        void softap();

        /**
         * wireless
         *
         * 无线
         */
        void wlan();

        /**
         * The cable
         *
         * 有线
         */
        void lan();
    }

    OnChooseNetLisenter lisenter;

    public void setOnChooseNetLisenter(OnChooseNetLisenter lisenter) {
        this.lisenter = lisenter;
    }

}
