package com.opensdk.devicedetail.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.utils.DialogUtils;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.opensdk.devicedetail.R;
import com.opensdk.devicedetail.callback.IGetDeviceInfoCallBack;
import com.opensdk.devicedetail.constance.MethodConst;
import com.opensdk.devicedetail.dialog.DeviceUpdateDialog;
import com.opensdk.devicedetail.entity.DeviceDetailListData;
import com.opensdk.devicedetail.entity.DeviceVersionListData;
import com.opensdk.devicedetail.manager.DetailInstanceManager;
import com.opensdk.devicedetail.net.DeviceDetailService;
import com.opensdk.devicedetail.tools.GsonUtils;
import com.opensdk.devicedetail.view.LcDetailProgressBar;

public class DeviceDetailVersionFragment extends Fragment implements IGetDeviceInfoCallBack.IDeviceVersionCallBack, View.OnClickListener , IGetDeviceInfoCallBack.IDeviceUpdateCallBack {
    private static final String TAG = DeviceDetailVersionFragment.class.getSimpleName();
    private Bundle arguments;
    private LcDetailProgressBar pgUpodate;
    private TextView tvVersionTip;
    private LinearLayout llNewVersion;
    private TextView tvNewVersionTip;
    private TextView tvNewVersion;
    private TextView tvDeviceCurrentVersion;
    private DeviceDetailActivity deviceDetailActivity;
    private DeviceDetailService deviceDetailService;
    private DeviceDetailListData.ResponseData.DeviceListBean deviceListBean;
    public boolean upIsDone = false;//设备升级完成
    private DeviceVersionListData deviceVersionListData;
    public Handler taskHandler;


    public static DeviceDetailVersionFragment newInstance() {
        DeviceDetailVersionFragment fragment = new DeviceDetailVersionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceDetailActivity = (DeviceDetailActivity) getActivity();
        deviceDetailActivity.llOperate.setVisibility(View.GONE);
        arguments = getArguments();
        taskHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                checkDeviceVersion();
                if(!upIsDone){
                    taskHandler.sendEmptyMessageDelayed(0,5000);
                }


            }
        };

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_detail_version, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceDetailActivity deviceDetailActivity = (DeviceDetailActivity) getActivity();
        deviceDetailActivity.tvTitle.setText(getResources().getString(R.string.lc_demo_device_version_title));
        initView(view);
        initData();
    }

    private void initView(View view) {
        tvDeviceCurrentVersion = view.findViewById(R.id.tv_device_current_version);
        tvNewVersion = view.findViewById(R.id.tv_new_version);
        tvNewVersionTip = view.findViewById(R.id.tv_new_version_tip);
        llNewVersion = view.findViewById(R.id.ll_new_version);
        tvVersionTip = view.findViewById(R.id.tv_version_tip);
        pgUpodate = view.findViewById(R.id.pg_upodate);
        pgUpodate.setOnClickListener(this);
    }

    private void initData() {
        //获取设备版本信息
        if (arguments == null) {
            return;
        }
        String deviceListStr = arguments.getString(MethodConst.ParamConst.deviceDetail);
        if (TextUtils.isEmpty(deviceListStr)) {
            return;
        }
        deviceListBean = GsonUtils.fromJson(deviceListStr, DeviceDetailListData.ResponseData.DeviceListBean.class);
        if (deviceListBean == null) {
            return;
        }
        DialogUtils.show(getActivity());
        deviceDetailService = DetailInstanceManager.newInstance().getDeviceDetailService();
        deviceVersionListData = new DeviceVersionListData();
        deviceVersionListData.data.deviceIds = deviceListBean.deviceId;
        deviceVersionListData.data.productId = deviceListBean.productId;
        checkDeviceVersion();
    }


    private void checkDeviceVersion(){
        deviceDetailService.deviceVersionList(deviceVersionListData, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void deviceVersion(DeviceVersionListData.Response responseData) {
        if (!isAdded()){
            return;
        }
        DialogUtils.dismiss();
        if (responseData.data == null || responseData.data.deviceList == null || responseData.data.deviceList.size() == 0) {
            return;
        }
        DeviceVersionListData.ResponseData.DeviceVersionListBean deviceVersionListBean = responseData.data.deviceList.get(0);
        if (deviceVersionListBean == null) {
            return;
        }
        tvDeviceCurrentVersion.setText(deviceVersionListBean.getVersion());
        if (deviceVersionListBean.getUpgradeInfo() == null) {
            //已是最新版本
            llNewVersion.setVisibility(View.GONE);
            pgUpodate.setVisibility(View.GONE);
            tvVersionTip.setVisibility(View.VISIBLE);
            tvVersionTip.setText(getResources().getString(R.string.lc_demo_device_version_new_tip));
            upIsDone = true;
        } else {
            //需要更新设备
            llNewVersion.setVisibility(View.VISIBLE);
            pgUpodate.setVisibility(View.VISIBLE);
            tvVersionTip.setVisibility(View.GONE);
            tvNewVersion.setText(deviceVersionListBean.getUpgradeInfo().getVersion());
            tvNewVersionTip.setText(deviceVersionListBean.getUpgradeInfo().getAttention());
            upIsDone = false;
        }
    }

    @Override
    public void deviceUpdate(boolean result) {
        if (!isAdded()){
            return;
        }
        if(result){
            taskHandler.sendEmptyMessageDelayed(0,5000);
        }else{
            DialogUtils.dismiss();
            Toast.makeText(getContext(), "Update is error", Toast.LENGTH_SHORT).show();
            deviceDetailActivity.finish();
        }


    }

    @Override
    public void onError(Throwable throwable) {
        if (!isAdded()){
            return;
        }
        DialogUtils.dismiss();
        LogUtil.errorLog(TAG, "error", throwable);
        pgUpodate.setText(getResources().getString(R.string.lc_demo_device_update));
        if(TextUtils.equals(throwable.getMessage(),String.valueOf(HttpSend.NET_ERROR_CODE))){
            Toast.makeText(getContext(), R.string.mobile_common_operate_fail, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pg_upodate) {
            if(pgUpodate.getText().equals(getResources().getString(R.string.lc_demo_device_updateing))){
                Toast.makeText(getContext(),getResources().getString(R.string.lc_demo_device_updateing), Toast.LENGTH_SHORT).show();
                return;
            }
            DeviceUpdateDialog deviceUpdateDialog = new DeviceUpdateDialog(getContext());
            deviceUpdateDialog.setOnOkClickLisenter(new DeviceUpdateDialog.OnOkClickLisenter() {
                @Override
                public void OnOK() {
//                    deviceDetailActivity.rlLoading.setVisibility(View.VISIBLE);
                    pgUpodate.setText(getResources().getString(R.string.lc_demo_device_updateing));
                    if(deviceDetailService==null){
                        deviceDetailService =  DetailInstanceManager.newInstance().getDeviceDetailService();
                    }
                    deviceDetailService.upgradeDevice(deviceListBean.deviceId,DeviceDetailVersionFragment.this);
                }
            });
            deviceUpdateDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskHandler.removeMessages(0);


    }
}
