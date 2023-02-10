package com.mm.android.deviceaddmodule.device_wifi;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.deviceaddmodule.base.BaseManagerFragmentActivity;
import com.mm.android.mobilecommon.common.PermissionHelper;
import com.mm.android.mobilecommon.widget.CommonTitle;

/**
 * The device wifi list is displayed
 *
 * 设备wifi列表界面
 */
public class ErrorTipActivity extends BaseManagerFragmentActivity implements CommonTitle.OnTitleClickListener {

    public static String ERROR_PARAMS = "error_params";

    private ImageView imageView;
    private TextView textView;
    private TextView textView1;
    private TextView mHelpLinkTxt,mHelpPhoneTv;
    int errorcode;

    private PermissionHelper mPermissionHelper;

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_error_tip);
    }

    @Override
    protected View initTitle() {
        CommonTitle title = (CommonTitle) findViewById(R.id.error_tip_title);
        title.initView(R.drawable.mobile_common_title_back, 0, R.string.mobile_common_network_config);
        title.setOnTitleClickListener(this);
        return title;
    }

    @Override
    protected void initView() {
        super.initView();
        imageView = findViewById(R.id.tip_img);
        textView = findViewById(R.id.tip_txt);
        textView1 = findViewById(R.id.tip_txt_1);
        mHelpLinkTxt = findViewById(R.id.help_link);
        mHelpLinkTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProviderManager.getDeviceAddCustomProvider().goFAQWebview(ErrorTipActivity.this);
            }
        });
        mHelpPhoneTv = findViewById(R.id.tv_help_phone);
        mHelpPhoneTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mHelpPhoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (errorcode == 1) {
            imageView.setBackgroundResource(R.drawable.adddevice_icon_wifiexplain);
            textView1.setVisibility(View.VISIBLE);
            textView.setText(R.string.add_device_tip_not_support_5g_1);
        } else if (errorcode == 2) {
            imageView.setBackgroundResource(R.drawable.adddevice_icon_wifiexplain_choosewifi);
            textView.setText(R.string.add_device_tip_wifi_name);
        }
    }

    @Override
    protected void initData() {
        errorcode = getIntent().getIntExtra(ERROR_PARAMS, 0);

    }

    @Override
    public void onCommonTitleClick(int id) {
        switch (id) {
            case CommonTitle.ID_LEFT:
                ErrorTipActivity.this.finish();
                break;
        }
    }
}
