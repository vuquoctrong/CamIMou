package com.opensdk.devicedetail.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.opensdk.devicedetail.R;

public class DeviceUpdateDialog extends Dialog {
    private TextView tv_title;
    private TextView tv_msg;
    private TextView btn_ok;
    private TextView btn_cancel;

    public DeviceUpdateDialog(Context context) {
        super(context, R.style.sign_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_device_update);
        tv_title = findViewById(R.id.tv_title);
        tv_msg = findViewById(R.id.tv_msg);
        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnOkClickLisenter != null) {
                    mOnOkClickLisenter.OnOK();
                }
                dismiss();

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });
    }

    public interface OnOkClickLisenter {
        void OnOK();
    }

    private OnOkClickLisenter mOnOkClickLisenter;

    public void setOnOkClickLisenter(OnOkClickLisenter lisenter) {
        this.mOnOkClickLisenter = lisenter;
    }

}
