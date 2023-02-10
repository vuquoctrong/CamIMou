package com.lc.media.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lc.media.R;
import com.mm.android.mobilecommon.widget.ClearEditText;

public class EncryptKeyInputDialog extends Dialog {

    private TextView tvTitle;
    private ClearEditText encryptKey;
    private TextView tvCancel;
    private TextView tvSure;
    private OnClick onClick;

    public EncryptKeyInputDialog(Context context) {
        super(context, R.style.custom_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_encryptkey_input);
        setCanceledOnTouchOutside(false);
        initView();
    }

    public void setText(String content){
        tvTitle.setText(content);
    }


    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        encryptKey = findViewById(R.id.encrypt_key);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSure = findViewById(R.id.tv_sure);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissLoading();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = encryptKey.getText().toString().trim();
                if (onClick != null && !TextUtils.isEmpty(key)) {
                    onClick.onSure(key);
                    dismissLoading();
                }
            }
        });
    }

    public void dismissLoading() {
        dismiss();
    }

    public interface OnClick {
        void onSure(String txt);
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }
}
