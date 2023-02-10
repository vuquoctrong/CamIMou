package com.mm.android.deviceaddmodule.device_wifi;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseManagerFragmentActivity;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.mobilecommon.widget.CommonTitle;

import static com.mm.android.deviceaddmodule.device_wifi.ErrorTipActivity.ERROR_PARAMS;

/**
 * The device wifi list is displayed
 *
 * 设备wifi列表界面
 */
public class DeviceWifiPasswordActivity<T extends DeviceWifiPasswordConstract.Presenter>
        extends BaseManagerFragmentActivity<T> implements DeviceWifiPasswordConstract.View,
        CommonTitle.OnTitleClickListener, View.OnClickListener {

    protected TextView mWifiSSIDTv;
    protected Button mDoneBtn;
    protected ClearPasswordEditText mWifiPasswordEdt;
    private TextView mSaveWifiPwdCheckbox;
    private ImageView mWifiImg;
    private TextView m5GWifiTipTv;

    private final TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            mWifiPasswordEdt.removeTextChangedListener(mTextWatcher);
            String str = Utils4DeviceManager.wifiPwdFilter(s.toString());
            if (!str.equals(s.toString())) {
                mWifiPasswordEdt.setText(str);
                mWifiPasswordEdt.setSelection(str.length());
            }
            mWifiPasswordEdt.addTextChangedListener(mTextWatcher);
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_device_wifi_password);
    }

    @Override
    protected View initTitle() {
        CommonTitle title = (CommonTitle) findViewById(R.id.device_wifi_password_title);
        title.initView(R.drawable.mobile_common_title_back, 0, R.string.mobile_common_network_config);
        title.setOnTitleClickListener(this);
        return title;
    }

    @Override
    protected void initView() {
        super.initView();

        mWifiSSIDTv = (TextView) findViewById(R.id.device_wifi_ssid);
        mDoneBtn = (Button) findViewById(R.id.device_wifi_password_done_btn);
        mWifiPasswordEdt = (ClearPasswordEditText) findViewById(R.id.device_wifi_password);
        mWifiSSIDTv.setText(mPresenter.getWifiSSID());
        mDoneBtn.setOnClickListener(this);
        mWifiPasswordEdt.addTextChangedListener(mTextWatcher);
        mSaveWifiPwdCheckbox = (TextView) findViewById(R.id.wifi_pwd_check);
        mSaveWifiPwdCheckbox.setOnClickListener(this);
        String savePassword = mPresenter.getSavedWifiPassword();
        boolean wifiCheckBoxStatus = mPresenter.getSavedWifiCheckBoxStatus();
        if (!TextUtils.isEmpty(savePassword)) {
            mWifiPasswordEdt.setText(savePassword);
            mWifiPasswordEdt.setSelection(savePassword.length());
        }
        mSaveWifiPwdCheckbox.setSelected(wifiCheckBoxStatus);
        mWifiImg = (ImageView) findViewById(R.id.wifi_img);
        mWifiImg.setImageResource(mPresenter.getSupport5G() ? R.drawable.adddevice_icon_wifipassword_nosupport5g : R.drawable.adddevice_icon_wifipassword_nosupport5g);
        m5GWifiTipTv = findViewById(R.id.tv_5g_tip);
        m5GWifiTipTv.setOnClickListener(this);
        m5GWifiTipTv.setVisibility(mPresenter.getSupport5G() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void initPresenter() {
        mPresenter = (T) new DeviceWifiPasswordPresenter<>(this);
    }

    @Override
    protected void initData() {
        mPresenter.dispatchIntentData(getIntent());
    }

    @Override
    public void onCommonTitleClick(int id) {
        switch (id) {
            case CommonTitle.ID_LEFT:
                DeviceWifiPasswordActivity.this.finish();
                break;
        }
    }

    @Override
    public String getWifiPassword() {
        return mWifiPasswordEdt.getText().toString();
    }

    @Override
    public boolean isSavePwdChecked() {
        return mSaveWifiPwdCheckbox.isSelected();
    }

    @Override
    public void onWifiOperateSucceed(CurWifiInfo curWifiInfo) {
        DeviceWifiPasswordActivity.this.finish();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.device_wifi_password_done_btn) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mWifiPasswordEdt.getWindowToken(), 0);
            mWifiPasswordEdt.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPresenter.updateWifiCache();
                    mPresenter.wifiOperate();
                }
            }, 100);
        } else if (viewId == R.id.wifi_pwd_check) {
            view.setSelected(!view.isSelected());
            if (!view.isSelected()) {
                mPresenter.updateWifiCache();
            }
        } else if (viewId == R.id.tv_5g_tip) {
            Intent intent = new Intent(this, ErrorTipActivity.class);
            intent.putExtra(ERROR_PARAMS, 1);
            startActivity(intent);
        }
    }
}
