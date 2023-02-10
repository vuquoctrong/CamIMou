package com.mm.android.mobilecommon.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.mm.android.mobilecommon.R;


public class LoadingDialog extends Dialog {
    ProgressBar av_anim;

    public LoadingDialog(Context context) {
        super(context, R.style.custom_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_view);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        av_anim = (ProgressBar) findViewById(R.id.av_anim);
    }

    public void dismissLoading() {
        av_anim.setVisibility(View.GONE);
        dismiss();
    }
}
