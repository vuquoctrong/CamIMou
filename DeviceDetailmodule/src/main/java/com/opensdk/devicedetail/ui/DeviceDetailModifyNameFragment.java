package com.opensdk.devicedetail.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.NameLengthFilter;
import com.mm.android.mobilecommon.utils.WordInputFilter;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.opensdk.devicedetail.R;
import com.opensdk.devicedetail.callback.IGetDeviceInfoCallBack;
import com.opensdk.devicedetail.constance.MethodConst;
import com.opensdk.devicedetail.entity.DeviceDetailListData;
import com.opensdk.devicedetail.entity.DeviceModifyNameData;
import com.opensdk.devicedetail.manager.DetailInstanceManager;
import com.opensdk.devicedetail.net.DeviceDetailService;
import com.opensdk.devicedetail.tools.DialogUtils;
import com.opensdk.devicedetail.tools.GsonUtils;

public class DeviceDetailModifyNameFragment extends Fragment implements View.OnClickListener, IGetDeviceInfoCallBack.IModifyDeviceCallBack {
    private static final String TAG = DeviceDetailModifyNameFragment.class.getSimpleName();
    private Bundle arguments;
    private ClearEditText newName;
    private DeviceDetailListData.ResponseData.DeviceListBean deviceListBean;
    private DeviceDetailActivity deviceDetailActivity;
    private final int MAXLETHER = 40;
    private IGetDeviceInfoCallBack.IModifyDeviceName modifyNameListener;
    private String name;
    private String fromWhere;

    public static DeviceDetailModifyNameFragment newInstance() {
        DeviceDetailModifyNameFragment fragment = new DeviceDetailModifyNameFragment();
        return fragment;
    }

    public void setModifyNameListener(IGetDeviceInfoCallBack.IModifyDeviceName modifyNameListener) {
        this.modifyNameListener = modifyNameListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arguments = getArguments();
        if (arguments == null) {
            return;
        }
        String deviceListStr = arguments.getString(MethodConst.ParamConst.deviceDetail);
        if (TextUtils.isEmpty(deviceListStr)) {
            return;
        }
        deviceListBean = GsonUtils.fromJson(deviceListStr, DeviceDetailListData.ResponseData.DeviceListBean.class);
        //不为空 列表页跳转
        fromWhere = arguments.getString(MethodConst.ParamConst.fromList);
        if (deviceListBean == null) {
            return;
        }
        deviceDetailActivity = (DeviceDetailActivity) getActivity();
        deviceDetailActivity.llOperate.setVisibility(View.VISIBLE);
        deviceDetailActivity.llOperate.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_modify_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceDetailActivity deviceDetailActivity = (DeviceDetailActivity) getActivity();
        deviceDetailActivity.tvTitle.setText(getResources().getString(R.string.lc_demo_device_detail_title));
        initView(view);
    }

    private void initView(View view) {
        newName = view.findViewById(R.id.et_new_name);
        if (deviceListBean.channelList.size() > 1) {
            //多通道
            if (MethodConst.ParamConst.fromList.equals(fromWhere)) {
                newName.setText(deviceListBean.deviceName);
            } else {
                //特殊情况，为与IOS保持一致，通道暂时显示的是外层NVR的设备详情内容
                newName.setText(deviceListBean.deviceName);
                //正常情况下，通道显示自己的信息
//                newName.setText(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName);
            }
        } else if (deviceListBean.channelList.size() == 1) {
            newName.setText(deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName);
        } else {
            newName.setText(deviceListBean.deviceName);
        }
        if (!TextUtils.isEmpty(newName.getText().toString())) {
            newName.setSelection(newName.getText().toString().length());
        }
        newName.setFilters(new InputFilter[]{new WordInputFilter(WordInputFilter.REX_NAME), new NameLengthFilter(MAXLETHER)});
        newName.addTextChangedListener(mTextWatcher);
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            String devName = s.toString().trim();
            if (!TextUtils.isEmpty(devName)) {
                deviceDetailActivity.llOperate.setEnabled(true);
                newName.removeTextChangedListener(mTextWatcher);
                String filterDevName = DeviceAddHelper.strDeviceNameFilter(devName);
                if (!filterDevName.equals(devName)) {
                    newName.setText(filterDevName);
                    newName.setSelection(filterDevName.length());
                }
                newName.addTextChangedListener(mTextWatcher);
            } else {
                deviceDetailActivity.llOperate.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        //修改名称
        DialogUtils.show(getActivity());
        if (newName == null || newName.getText().toString().trim() == null || newName.getText().toString().trim().isEmpty()) {
            return;
        }
        name = newName.getText().toString().trim();
        DeviceDetailService deviceDetailService = DetailInstanceManager.newInstance().getDeviceDetailService();
        DeviceModifyNameData deviceModifyNameData = new DeviceModifyNameData();
        deviceModifyNameData.data.name = name;
        deviceModifyNameData.data.deviceId = deviceListBean.deviceId;
//        if (deviceListBean.channelList.size() > 1 && !MethodConst.ParamConst.fromList.equals(fromWhere)) {
//            //多通道
//            deviceModifyNameData.data.channelId = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId;
//        }
        deviceDetailService.modifyDeviceName(deviceModifyNameData, this);
    }

    @Override
    public void deviceModify(boolean result) {
        if (!isAdded()) {
            return;
        }
        deviceDetailActivity.llOperate.setVisibility(View.GONE);
        DialogUtils.dismiss();
        Toast.makeText(getContext(), getResources().getString(R.string.lc_demo_device_modify_success), Toast.LENGTH_SHORT).show();
        if (modifyNameListener != null) {
            modifyNameListener.deviceName(name);
        }
        //多通道设备详情
//        if (deviceListBean.channelList.size() == 0 || (deviceListBean.channelList.size() > 1 && MethodConst.ParamConst.fromList.equals(fromWhere))) {
        if (deviceListBean.channelList.size() == 0 || deviceListBean.channelList.size() > 1) {
            deviceListBean.deviceName = name;
        } else {
            deviceListBean.channelList.get(deviceListBean.checkedChannel).channelName = name;
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onError(Throwable throwable) {
        if (!isAdded()) {
            return;
        }
        DialogUtils.dismiss();
        deviceDetailActivity.rlLoading.setVisibility(View.GONE);
        LogUtil.errorLog(TAG, "error", throwable);
        if(TextUtils.equals(throwable.getMessage(),String.valueOf(HttpSend.NET_ERROR_CODE))){
            Toast.makeText(getContext(), R.string.mobile_common_operate_fail, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
