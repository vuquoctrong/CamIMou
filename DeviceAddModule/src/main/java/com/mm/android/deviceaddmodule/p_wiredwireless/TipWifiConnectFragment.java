package com.mm.android.deviceaddmodule.p_wiredwireless;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.TipWifiConnectConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.deviceaddmodule.helper.Utils4AddDevice;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.CommonHelper;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.deviceaddmodule.presenter.TipWifiConnectPresenter;

/**
 * wifi connection prompt page
 *
 * wifi连接提示页
 */
public class TipWifiConnectFragment extends BaseDevAddFragment implements TipWifiConnectConstract.View{
    TipWifiConnectConstract.Presenter mPresenter;
    TextView mGotoWifiSetting;
    private int last_network_type = -3;

    public static TipWifiConnectFragment newInstance() {
        TipWifiConnectFragment fragment = new TipWifiConnectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wifi_connect_tip, container, false);
    }

    @Override
    protected void initView(View view) {
        mGotoWifiSetting = (TextView)view.findViewById(R.id.tv_goto_connect);
        mGotoWifiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonHelper.gotoWifiSetting(getActivity());
            }
        });
    }

    protected void initData(){
        mPresenter=new TipWifiConnectPresenter(this);
        String configMode = mPresenter.getConfigMode();
        if (DeviceAddInfo.ConfigMode.LAN.name().equalsIgnoreCase(configMode)
                || !configMode.contains(DeviceAddInfo.ConfigMode.LAN.name()))
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
        }
    }

    @Override
    protected void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (LCConfiguration.CONNECTIVITY_CHAGET_ACTION.equals(intent.getAction())) {
            final int netWorkState = Utils4AddDevice.getNetWorkState(getContextInfo());
            LogUtil.debugLog("TipWifiConnectFragment", "netWorkState : " + netWorkState);
            if(netWorkState == Utils4AddDevice.NETWORK_WIFI && last_network_type != Utils4AddDevice.NETWORK_WIFI) {
                mPresenter.searchDevice();
            }
            last_network_type = netWorkState;
        }
    }

    @Override
    protected IntentFilter createBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LCConfiguration.CONNECTIVITY_CHAGET_ACTION);
        return intentFilter;
    }

    @Override
    public void goDevInitPage(DeviceInitInfo device_net_info_ex) {
        PageNavigationHelper.gotoSecurityCheckPage(this);
    }

    @Override
    public void goWifiConfigPage() {
        PageNavigationHelper.gotoWifiPwdPage(this,this);
    }

    @Override
    public void goCloudConnectPage() {
        PageNavigationHelper.gotoCloudConnectPage(this);
    }
}
