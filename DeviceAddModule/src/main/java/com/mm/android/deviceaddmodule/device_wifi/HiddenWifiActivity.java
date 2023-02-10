package com.mm.android.deviceaddmodule.device_wifi;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseManagerFragmentActivity;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.mobilecommon.widget.CommonTitle;

import static com.mm.android.deviceaddmodule.device_wifi.ErrorTipActivity.ERROR_PARAMS;

public class HiddenWifiActivity<T extends HiddenWifiConstract.Presenter>
        extends BaseManagerFragmentActivity<T> implements HiddenWifiConstract.View {


    private TextView mNext;

    private ClearEditText mWifiName;

    private ClearPasswordEditText mWifiPsw;

    @Override
    protected View initTitle() {
        CommonTitle title = findViewById(R.id.device_hidden_title);
        title.initView(R.drawable.mobile_common_title_back, 0, R.string.device_manager_wifi_title);
        title.setOnTitleClickListener(new CommonTitle.OnTitleClickListener() {
            @Override
            public void onCommonTitleClick(int id) {
                if (id == CommonTitle.ID_LEFT) {
                    HiddenWifiActivity.this.finish();
                }
            }
        });
        return title;
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_device_hidden_wifi);
    }

    protected void initView() {
        super.initView();

        mWifiName = findViewById(R.id.wifi_name);
        mWifiPsw = findViewById(R.id.wifi_psw);

        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mWifiPsw.getWindowToken(), 0);
                mWifiPsw.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.wifiOperate();
                    }
                }, 100);
            }
        });
        findViewById(R.id.tv_5g_tip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HiddenWifiActivity.this, ErrorTipActivity.class);
                intent.putExtra(ERROR_PARAMS, 1);
                startActivity(intent);
            }
        });

        mWifiName.addTextChangedListener(new SimpleTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
              mNext.setEnabled(s.length()>0);
            }
        });

    }

    @Override
    public void initPresenter() {
        mPresenter = (T) new HiddenWifiPresenter<>(this);
    }

    @Override
    protected void initData() {
        mPresenter.dispatchIntentData(getIntent());
    }


    @Override
    public String getWifiSSID() {
        return mWifiName.getText().toString().trim();
    }

    @Override
    public String getWifiPassword() {
        return mWifiPsw.getText().toString().trim();
    }

    @Override
    public void onWifiOperateSucceed(CurWifiInfo curWifiInfo) {
        finish();
    }
}
