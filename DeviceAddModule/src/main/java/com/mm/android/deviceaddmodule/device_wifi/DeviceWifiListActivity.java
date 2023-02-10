package com.mm.android.deviceaddmodule.device_wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseManagerFragmentActivity;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.widget.CommonTitle;

import java.util.ArrayList;
import java.util.List;

import static com.mm.android.deviceaddmodule.device_wifi.ErrorTipActivity.ERROR_PARAMS;

/**
 * The device wifi list is displayed
 *
 * 设备wifi列表界面
 */
public class DeviceWifiListActivity<T extends DeviceWifiListConstract.Presenter> extends BaseManagerFragmentActivity<T> implements DeviceWifiListConstract.View,
        CommonTitle.OnTitleClickListener, AdapterView.OnItemClickListener{

    protected DeviceWifiListAdapter mAdapter;
    protected ListView mList;
    protected TextView mCurWifiSSIDTv;
    protected ImageView mCurWifiQualityIv;
    protected LinearLayout mNo5GLl;
    protected TextView mNo5GTv;
    protected TextView deviceWifi;

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_device_wifi_list);
    }

    @Override
    protected View initTitle() {
        CommonTitle title = (CommonTitle) findViewById(R.id.device_wifi_list_title);
        title.initView(R.drawable.mobile_common_title_back, R.drawable.common_title_refresh_selector, R.string.mobile_common_network_config);
        title.setOnTitleClickListener(this);
        return title;
    }

    @Override
    protected void initView() {
        super.initView();
        mList = (ListView) findViewById(R.id.device_wifi_list);
        mCurWifiSSIDTv = (TextView) findViewById(R.id.wifi_ssid);
        mCurWifiQualityIv = (ImageView) findViewById(R.id.wifi_quality_icon);
        mNo5GLl = findViewById(R.id.device_wifi_no_5g);
        mNo5GTv = findViewById(R.id.tv_5g_tip);

        mNo5GTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceWifiListActivity.this, ErrorTipActivity.class);
                intent.putExtra(ERROR_PARAMS,1);
                startActivity(intent);
            }
        });
        deviceWifi = findViewById(R.id.device_wifi_list_text);
        deviceWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceWifiListActivity.this, ErrorTipActivity.class);
                intent.putExtra(ERROR_PARAMS,2);
                startActivity(intent);
            }
        });
        mList.setOnItemClickListener(this);
        View view = LayoutInflater.from(this).inflate(R.layout.item_wifi_list_more, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceWifiListActivity.this, HiddenWifiActivity.class);
                intent.putExtra(LCConfiguration.Device_ID,mPresenter.getLCDevice().getDeviceId());
                intent.putExtra(LCConfiguration.SUPPORT_5G,mPresenter.isSupport5G(mPresenter.getLCDevice().getWifiTransferMode()));
                startActivity(intent);
            }
        });
        mList.addFooterView(view,null,true);
        mAdapter = new DeviceWifiListAdapter(R.layout.include_device_wifi_list_item, new ArrayList<WifiInfo>(), this);
        mList.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isActivityDestory()) {
                    mPresenter.getDeviceWifiListAsync();
                }
            }
        }, 100);
    }

    @Override
    public void initPresenter() {
        mPresenter = (T) new DeviceWifiListPresenter(this);
    }

    @Override
    protected void initData() {
        mPresenter.dispatchIntentData(getIntent());
    }

    @Override
    public void onCommonTitleClick(int id) {
        switch (id) {
            case CommonTitle.ID_LEFT:
                Intent intent = new Intent();
                intent.putExtra(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO, mPresenter.getCurWifiInfo());
                setResult(RESULT_OK, intent);
                DeviceWifiListActivity.this.finish();
                break;
            case CommonTitle.ID_RIGHT:
                mPresenter.getDeviceWifiListAsync();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,DeviceWifiPasswordActivity.class);
        intent.putExtra(DeviceConstant.IntentKey.DEVICE_WIFI_CONFIG_INFO,mPresenter.getWifiInfo(position));
        intent.putExtra(LCConfiguration.Device_ID,mPresenter.getLCDevice().getDeviceId());
        intent.putExtra(LCConfiguration.SUPPORT_5G,mPresenter.isSupport5G(mPresenter.getLCDevice().getWifiTransferMode()));
        startActivityForResult(intent,DeviceConstant.IntentCode.DEVICE_SETTING_WIFI_OPERATE);
    }

    @Override
    public void refreshListView(List<WifiInfo> wifiInfos) {
        if (mAdapter != null){
            mAdapter.clearData();
            mAdapter.addData(wifiInfos);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadSucceed(boolean isEmpty,boolean isError) {
        if (isEmpty) {
            findViewById(R.id.device_wifi_list_empty_view).setVisibility(View.VISIBLE);
            findViewById(R.id.device_wifi_list_layout).setVisibility(View.GONE);
            mNo5GLl.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.device_wifi_list_empty_view)).setText(isError?R.string.mobile_common_get_info_failed:R.string.device_manager_wifi_list_empty);
        } else {
            if (!mPresenter.isSupport5G(mPresenter.getLCDevice().getWifiTransferMode())) {
                mNo5GLl.setVisibility(View.VISIBLE);
            }
            findViewById(R.id.device_wifi_list_empty_view).setVisibility(View.GONE);
            findViewById(R.id.device_wifi_list_layout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateCurWifiLayout(CurWifiInfo curWifiInfo) {
        if (curWifiInfo == null || !curWifiInfo.isLinkEnable() || TextUtils.isEmpty(curWifiInfo.getSsid())) {
            findViewById(R.id.device_wifi_list_connected).setVisibility(View.GONE);
            findViewById(R.id.device_wifi_list_connected_info).setVisibility(View.GONE);
        } else {
            findViewById(R.id.device_wifi_list_connected).setVisibility(View.VISIBLE);
            findViewById(R.id.device_wifi_list_connected_info).setVisibility(View.VISIBLE);
            mCurWifiSSIDTv.setText(curWifiInfo.getSsid());
            mCurWifiQualityIv.setVisibility(View.VISIBLE);
            if (curWifiInfo.getIntensity() < 2) {
                mCurWifiQualityIv.setImageResource("OPEN".equalsIgnoreCase(curWifiInfo.getAuth())
                        ? R.drawable.devicedetail_wifi_nosingal : R.drawable.devicedetail_wifi_nosingal_lock);
            } else if (curWifiInfo.getIntensity() < 3) {
                mCurWifiQualityIv.setImageResource("OPEN".equalsIgnoreCase(curWifiInfo.getAuth())
                        ? R.drawable.devicedetail_wifi_1singal : R.drawable.devicedetail_wifi_1singal_lock);
            } else if (curWifiInfo.getIntensity() < 4) {
                mCurWifiQualityIv.setImageResource("OPEN".equalsIgnoreCase(curWifiInfo.getAuth())
                        ? R.drawable.devicedetail_wifi_2singal : R.drawable.devicedetail_wifi_2singal_lock);
            } else {
                mCurWifiQualityIv.setImageResource("OPEN".equalsIgnoreCase(curWifiInfo.getAuth())
                        ? R.drawable.devicedetail_wifi_3singal : R.drawable.devicedetail_wifi_3singal_lock);
            }
        }
    }

    @Override
    public void viewFinish() {
        DeviceWifiListActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DeviceConstant.IntentCode.DEVICE_SETTING_WIFI_OPERATE && resultCode == Activity.RESULT_OK
                && data != null) {
            CurWifiInfo curWifiInfo = (CurWifiInfo) data.getSerializableExtra(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO);
            Intent intent = new Intent();
            intent.putExtra(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO, curWifiInfo);
            setResult(RESULT_OK, intent);
            DeviceWifiListActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(DeviceConstant.IntentKey.DEVICE_CURRENT_WIFI_INFO, mPresenter.getCurWifiInfo());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
