package com.mm.android.deviceaddmodule.p_ap;


import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.ApBindSuccessConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.AddApResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.eventbus.event.CommonEvent;
import com.mm.android.mobilecommon.utils.NameLengthFilter;
import com.mm.android.mobilecommon.utils.WordInputFilter;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.ApBindSuccessPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Accessory binding success page
 *
 * 配件绑定成功页
 **/
public class ApBindSuccessFragment extends BaseDevAddFragment implements ApBindSuccessConstract.View,
        View.OnClickListener {
    private static String AP_RESULT_PARAM = "ap_result_param";
    private final int MAXLETHER = 20;

    ApBindSuccessConstract.Presenter mPresenter;
    ClearEditText mApNameEdit;
    TextView mNextBtn;
    ImageView mDevImg;

    public static ApBindSuccessFragment newInstance(AddApResult addApResult) {
        ApBindSuccessFragment fragment = new ApBindSuccessFragment();
        Bundle args = new Bundle();
        args.putSerializable(AP_RESULT_PARAM, addApResult);
        fragment.setArguments(args);
        return fragment;
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            String devName=s.toString().trim();
            if(!TextUtils.isEmpty(devName)){
                mNextBtn.setEnabled(true);
                mApNameEdit.removeTextChangedListener(mTextWatcher);
                String filterDevName = DeviceAddHelper.strDeviceNameFilter(devName);
                if (!filterDevName.equals(devName)) {
                    mApNameEdit.setText(filterDevName);
                    mApNameEdit.setSelection(filterDevName.length());
                }
                mApNameEdit.addTextChangedListener(mTextWatcher);
            }else{
                mNextBtn.setEnabled(false);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ap_bind_success, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.BLANK);
    }

    @Override
    protected void initView(View view) {
        mApNameEdit = view.findViewById(R.id.ap_name_input);
        mNextBtn = view.findViewById(R.id.tv_next);
        mNextBtn.setOnClickListener(this);

        mApNameEdit.setFilters(new InputFilter[] {new WordInputFilter(WordInputFilter.REX_NAME) , new NameLengthFilter(MAXLETHER)});
        mApNameEdit.addTextChangedListener(mTextWatcher);
        mDevImg = view.findViewById(R.id.dev_img);
    }

    @Override
    protected void initData() {
        mPresenter = new ApBindSuccessPresenter(this);
        if (getArguments() != null) {
            AddApResult addApResult = (AddApResult) getArguments().getSerializable(AP_RESULT_PARAM);
            mPresenter.setData(addApResult);
        }
    }

    @Override
    public String getApName() {
        return mApNameEdit.getText().toString().trim();
    }

    @Override
    public void setApName(String name) {
        mApNameEdit.setText(name);
    }

    @Override
    public void setApImg(String img) {
        if (!TextUtils.isEmpty(img)) {
            Glide.with(ApBindSuccessFragment.this.getContext()).load(img).into(mDevImg);
        }
    }

    @Override
    public void completeAction() {
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        boolean isDeviceDetail = deviceAddInfo.isDeviceDetail();
        String deviceId = deviceAddInfo.getGatewayInfo().getSn();
        String apId = deviceAddInfo.getDeviceSn();

        if (getActivity() != null) getActivity().finish();

        String code = CommonEvent.AP_PAIR_SUCCEED_2_MID_ACTION;
        if(!isDeviceDetail) {
            //添加结束跳转到首页设备列表
            code = CommonEvent.AP_PAIR_SUCCEED_2_MAIN_ACTION;
            ProviderManager.getDeviceAddCustomProvider().goHomePage(getContext());
        }

        Bundle bundle = new Bundle();
        bundle.putString(LCConfiguration.Device_ID, deviceId);
        bundle.putString(LCConfiguration.AP_ID, apId);
        CommonEvent commonEvent = new CommonEvent(code);
        commonEvent.setBundle(bundle);
        EventBus.getDefault().post(commonEvent);

        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DESTROY_ACTION));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_next) {
            mPresenter.modifyApName();
        }
    }



}
