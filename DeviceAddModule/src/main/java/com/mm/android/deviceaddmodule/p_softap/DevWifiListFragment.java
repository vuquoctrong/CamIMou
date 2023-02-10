package com.mm.android.deviceaddmodule.p_softap;


import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.adapter.WifiListAdapter;
import com.mm.android.deviceaddmodule.base.BaseWifiListenerFragment;
import com.mm.android.deviceaddmodule.contract.DevWifiListConstract;
import com.lechange.opensdk.searchwifi.WlanInfo;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.eventbus.event.BaseEvent;
import com.mm.android.deviceaddmodule.presenter.DevWifiListPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Soft AP adds Wifi selection page
 *
 * 软AP添加Wifi选择页
 */
public class DevWifiListFragment extends BaseWifiListenerFragment implements DevWifiListConstract.View, AdapterView.OnItemClickListener {
    private static final String IS_NOT_NEED_LOGIN = "isNotNeedLogin";
    DevWifiListConstract.Presenter mPresenter;
    private WifiListAdapter mAdapter;
    private List<WlanInfo> mListData;
    private boolean isNotNeedLogin;
    LinearLayout mWifiListContainer,mErrorTip;
    ListView mList;
    private TextView mWifiNameTip;
    private TextView mWifi5GTip;
    private ImageView mWifiIv;

    public static DevWifiListFragment newInstance(boolean isNotNeedLogin) {
        DevWifiListFragment fragment = new DevWifiListFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_NOT_NEED_LOGIN, isNotNeedLogin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onMessageEvent(BaseEvent event) {
        super.onMessageEvent(event);
        if (event instanceof DeviceAddEvent) {
            String code = event.getCode();
            if (DeviceAddEvent.SOFTAP_REFRSH_WIFI_LIST.equals(code)) {
                mPresenter.getWifiList();
            }
        }
    }

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_dev_wifi_list, container, false);
            initView(rootView);// 控件初始化
        }
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.REFRESH);
    }

    protected void initView(View view) {
        mWifiListContainer= view.findViewById(R.id.wifi_list_container);
        mWifiNameTip = view.findViewById(R.id.dev_wifi_name);
        mWifi5GTip = view.findViewById(R.id.tv_5g_tip);
        mWifi5GTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageNavigationHelper.gotoErrorTipPage(DevWifiListFragment.this, DeviceAddHelper.ErrorCode.COMMON_ERROR_NOT_SUPPORT_5G);
            }
        });
        mWifiNameTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageNavigationHelper.gotoErrorTipPage(DevWifiListFragment.this, DeviceAddHelper.ErrorCode.COMMON_ERROR_WIFI_NAME);
            }
        });
        mWifiIv = view.findViewById(R.id.iv_wifi);
        mErrorTip= view.findViewById(R.id.error_tip);
        mList = view.findViewById(R.id.wifi_list);
        mList.setOnItemClickListener(this);
    }

    protected void initData() {
        if (getArguments() != null) {
            isNotNeedLogin = getArguments().getBoolean(IS_NOT_NEED_LOGIN, false);
        }
        mPresenter = new DevWifiListPresenter(this, isNotNeedLogin);

        mPresenter.getWifiList();
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.device_add_wifi_list_item_more, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHiddenWifiPwdPage(isNotNeedLogin);
            }
        });
        mList.addFooterView(view,null,true);


        mListData = new ArrayList<>();
        mAdapter = new WifiListAdapter(R.layout.device_add_wifi_list_item, mListData, getActivity());
        mList.setAdapter(mAdapter);

        check5GWifiTip();
    }

    private void check5GWifiTip() {
        if (!mPresenter.isDevSupport5G()) {
            mWifi5GTip.setVisibility(View.VISIBLE);
            mWifiIv.setImageResource(R.drawable.adddevice_icon_wifipassword_nosupport5g_layer);
        } else {
            mWifi5GTip.setVisibility(View.GONE);
            mWifiIv.setImageResource(R.drawable.adddevice_icon_wifipassword);
        }
    }

    @Override
    public void updateWifiList(List<WlanInfo> list) {
        mList.setVisibility(View.VISIBLE);
        mListData.clear();
        if (list != null)
            mListData.addAll(list);
        mAdapter.refresh();
    }

    @Override
    public void goWifiPwdPage(WlanInfo wlanInfo, boolean isNotNeedLogin) {
        PageNavigationHelper.gotoSoftApWifiPwdPage(this, wlanInfo, isNotNeedLogin);
    }

    @Override
    public void goHiddenWifiPwdPage(boolean isNotNeedLogin) {
        PageNavigationHelper.gotoHiddenWifiPwdPage(this, isNotNeedLogin);
    }

    @Override
    public void goDevLoginPage() {
        PageNavigationHelper.gotoDevLoginPage(this);
    }

    @Override
    public void showListView() {
        mWifiListContainer.setVisibility(View.VISIBLE);
        mErrorTip.setVisibility(View.GONE);
    }

    @Override
    public void showErrorInfoView() {
        mWifiListContainer.setVisibility(View.GONE);
        mErrorTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goWifiPwdPage(mAdapter.getItem(position), isNotNeedLogin);
    }

}
