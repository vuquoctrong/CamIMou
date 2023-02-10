package com.mm.android.deviceaddmodule.p_softap;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.SoftApWifiPwdConstract;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.CommonHelper;
import com.mm.android.mobilecommon.utils.UIHelper;
import com.mm.android.mobilecommon.utils.WifiUtil;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.SoftApWifiPwdPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Enter wifi password page
 *
 * 输入wifi密码页
 */
public class SoftApWifiPwdFragment extends BaseDevAddFragment implements SoftApWifiPwdConstract.View, View.OnClickListener {
    private static String WLAN_PARAM = "wlan_param";
    private static final String IS_NOT_NEED_LOGIN = "isNotNeedLogin";
    SoftApWifiPwdConstract.Presenter mPresenter;
    private TextView mNextTv;
    private TextView mWifiSsidTv;
    private ClearPasswordEditText mWifiPwdEt;
    private TextView m5GWifiTipTv;
    private TextView mSaveWifiPwdChebox;
    private ImageView mSwitchWifiIv;
    private ImageView mWifiIv;
    public  WlanInfo mWlanInfo;
    private WifiUtil mWifiUtil;

    public static SoftApWifiPwdFragment newInstance(WlanInfo wlanInfo, boolean isNotNeedLogin) {
        SoftApWifiPwdFragment fragment = new SoftApWifiPwdFragment();
        Bundle args = new Bundle();
        args.putSerializable(WLAN_PARAM, wlanInfo);
        args.putBoolean(IS_NOT_NEED_LOGIN, isNotNeedLogin);
        fragment.setArguments(args);
        return fragment;
    }

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
            setConnectButtonState();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wifi_pwd, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void initView(View view) {
        mWifiIv = view.findViewById(R.id.iv_wifi);
        mSwitchWifiIv = view.findViewById(R.id.switch_wifi);
        mSwitchWifiIv.setVisibility(View.GONE);
        mNextTv = view.findViewById(R.id.next);
        m5GWifiTipTv = view.findViewById(R.id.tv_5g_tip);
        mWifiSsidTv = view.findViewById(R.id.ssid);
        mWifiPwdEt = view.findViewById(R.id.wifi_pwd);
        mWifiPwdEt.openEyeMode(ProviderManager.getAppProvider().getAppType() != LCConfiguration.APP_LECHANGE_OVERSEA);
        mWifiPwdEt.addTextChangedListener(mTextWatcher);
        mNextTv.setOnClickListener(this);
        mSaveWifiPwdChebox = view.findViewById(R.id.wifi_pwd_check);
        m5GWifiTipTv.setOnClickListener(this);
        mSaveWifiPwdChebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if (!view.isSelected()) {
                    mPresenter.updateWifiCache();
                }
            }
        });
        mSwitchWifiIv.setOnClickListener(this);
        mWifiUtil =  new WifiUtil(getActivity());
    }

    protected void initData() {
        mPresenter = new SoftApWifiPwdPresenter(this);
        if (getArguments() != null) {
            mWlanInfo = (WlanInfo) getArguments().getSerializable(WLAN_PARAM);
            mPresenter.setWlanInfo(mWlanInfo);
            boolean isNotNeedLogin = getArguments().getBoolean(IS_NOT_NEED_LOGIN, false);
            mPresenter.setIsNotNeedLogin(isNotNeedLogin);
        }
        check5GWifiTip();
        setConnectButtonState();
        setSSIDTextView();
        setWifiPwd();

        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        if (deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.LAN.name())) {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
        } else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        }
    }

    private void setSSIDTextView() {
//        if (null != getActivity()) {
//            mWifiSsidTv.setText("");
//            return;
//        }
        mWifiSsidTv.setText(mPresenter.getCurWifiName());
    }

    private void setWifiPwd() {
        String wifiPwd = mPresenter.getSavedWifiPwd();
        boolean wifiCheckBoxStatus = mPresenter.getSavedWifiCheckBoxStatus();

        if (!TextUtils.isEmpty(wifiPwd)) {
            mWifiPwdEt.setText(wifiPwd);
            mSaveWifiPwdChebox.setSelected(wifiCheckBoxStatus);
        }else{
            mWifiPwdEt.setText("");
            mSaveWifiPwdChebox.setSelected(wifiCheckBoxStatus);
        }
    }

    private void check5GWifiTip() {
        if (!mPresenter.isDevSupport5G()) {
            m5GWifiTipTv.setVisibility(View.VISIBLE);
            mWifiIv.setImageResource(R.drawable.adddevice_icon_wifipassword_nosupport5g_layer);
        } else {
            m5GWifiTipTv.setVisibility(View.INVISIBLE);
            mWifiIv.setImageResource(R.drawable.adddevice_icon_wifipassword);
        }
    }

    private void setConnectButtonState() {
        UIHelper.setEnabledEX(true, mNextTv);
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
//            if (null != getActivity() && !Utils4AddDevice.isWifi(getActivity())) {
//                toastInCenter(R.string.add_device_con_wifi);
//                return;
//            }
            mPresenter.connectWifi(mWifiSsidTv.getText().toString().trim(),getWifiPwd(),mWifiUtil.getGatewayIp());
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

    @Override
    public boolean isSavePwdChecked() {
        return mSaveWifiPwdChebox.isSelected();
    }

    @Override
    public void goCloudConnectPage() {
        PageNavigationHelper.gotoCloudConnectPage(this);
    }
}
