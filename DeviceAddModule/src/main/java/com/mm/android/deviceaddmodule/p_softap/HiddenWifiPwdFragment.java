package com.mm.android.deviceaddmodule.p_softap;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.HiddenWifiPwdConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.CommonHelper;
import com.mm.android.mobilecommon.utils.UIHelper;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.HiddenWifiPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Enter the hidden wifi password page
 *
 * 输入隐藏wifi密码页
 */
public class HiddenWifiPwdFragment extends BaseDevAddFragment implements HiddenWifiPwdConstract.View, View.OnClickListener {

    private static String WLAN_PARAM = "wlan_param";
    private static final String IS_NOT_NEED_LOGIN = "isNotNeedLogin";
    HiddenWifiPwdConstract.Presenter mPresenter;
    private TextView mNextTv;
    private ClearEditText mWifiSsidTv;
    private ClearPasswordEditText mWifiPwdEt;
    private TextView m5GWifiTipTv;

    public static HiddenWifiPwdFragment newInstance(boolean isNotNeedLogin) {
        HiddenWifiPwdFragment fragment = new HiddenWifiPwdFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_NOT_NEED_LOGIN, isNotNeedLogin);
        fragment.setArguments(args);
        return fragment;
    }

    private final TextWatcher mSsidTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            if (!mWifiSsidTv.getText().toString().equalsIgnoreCase("")) {
                setConnectButtonState(true);
            }else {
                setConnectButtonState(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };

    private final TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            mWifiPwdEt.removeTextChangedListener(mTextWatcher);
            String str = Utils4AddDevice.wifiPwdFilter(s.toString());
            if (!str.equals(s.toString())) {
                mWifiPwdEt.setText(str);
                mWifiPwdEt.setSelection(str.length());
            }
            mWifiPwdEt.addTextChangedListener(mTextWatcher);

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hidden_wifi, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void initView(View view) {

        mNextTv = view.findViewById(R.id.next);
        mWifiSsidTv = view.findViewById(R.id.wifi_name);
        mWifiPwdEt = view.findViewById(R.id.wifi_psw);
        mWifiPwdEt.openEyeMode(ProviderManager.getAppProvider().getAppType() != LCConfiguration.APP_LECHANGE_OVERSEA);
        mWifiSsidTv.addTextChangedListener(mSsidTextWatcher);
        mWifiPwdEt.addTextChangedListener(mTextWatcher);
        mNextTv.setOnClickListener(this);

        m5GWifiTipTv = view.findViewById(R.id.tv_5g_tip);
        m5GWifiTipTv.setOnClickListener(this);

    }

    protected void initData() {
        mPresenter = new HiddenWifiPresenter(this);
        if (getArguments() != null) {

            boolean isNotNeedLogin = getArguments().getBoolean(IS_NOT_NEED_LOGIN, false);
            mPresenter.setIsNotNeedLogin(isNotNeedLogin);
        }
        setConnectButtonState(false);

        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.LAN.name())) {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
        } else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        }

        if (mPresenter.isDevSupport5G()) {
            m5GWifiTipTv.setVisibility(View.GONE);
        } else {
            m5GWifiTipTv.setVisibility(View.VISIBLE);
        }
    }

    private void setSSIDTextView() {
        if (null != getActivity() && !Utils4AddDevice.isWifi(getActivity())) {
            mWifiSsidTv.setText("");
            return;
        }
        mWifiSsidTv.setText(mPresenter.getCurWifiName());
    }

    private void setConnectButtonState(boolean enable) {
        UIHelper.setEnabledEX(enable, mNextTv);
    }

    @Override
    public void showProgressDialog() {
        hideSoftKeyboard();
        mNextTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDestoryView()) {
                    EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SHOW_LOADING_VIEW_ACTION));
                }
            }
        }, 100);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != getActivity() && mWifiPwdEt != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mWifiPwdEt.getWindowToken(), 0);

        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.next == id) {

            mPresenter.connectWifi();
        } else if (R.id.tv_5g_tip == id) {
            PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_5G);
        }else if(R.id.switch_wifi == id){
            CommonHelper.gotoWifiSetting(getActivity());
        }
    }

    @Override
    public String getWifiPwd() {
        return mWifiPwdEt.getText().toString();
    }

    public String getWifiSSID() {
        return mWifiSsidTv.getText().toString();
    }


    @Override
    public void goCloudConnectPage() {
        PageNavigationHelper.gotoCloudConnectPage(this);
    }

}
