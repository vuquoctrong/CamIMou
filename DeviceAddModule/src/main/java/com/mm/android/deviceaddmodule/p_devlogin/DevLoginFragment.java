package com.mm.android.deviceaddmodule.p_devlogin;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.DevLoginConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.mobilecommon.widget.ClearPasswordEditText;
import com.mm.android.deviceaddmodule.presenter.DevLoginPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Device Login Page
 *
 * 设备登录页面
 */
public class DevLoginFragment extends BaseDevAddFragment implements DevLoginConstract.View,
        View.OnClickListener, ClearEditText.ITextChangeListener {
    DevLoginConstract.Presenter mPresenter;
    ClearPasswordEditText mPwdEdit;
    TextView mNext;
    Handler mHandler = new Handler();
    long mEventStartTime;       //统计开始时间

    public static DevLoginFragment newInstance() {
        DevLoginFragment fragment = new DevLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dev_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEventStartTime = System.currentTimeMillis();
    }

    protected void initView(View view) {
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        mNext = view.findViewById(R.id.tv_input_ok);
        mPwdEdit = view.findViewById(R.id.et_device_pwd);
        mPwdEdit.openEyeMode(ProviderManager.getAppProvider().getAppType() != LCConfiguration.APP_LECHANGE_OVERSEA);
        mNext.setOnClickListener(this);
        UIUtils.setEnabledEX(false, mNext);
        mPwdEdit.setTextChangeListener(this);
    }

    protected void initData() {
        mPresenter = new DevLoginPresenter(this);
    }

    @Override
    public void showProgressDialog() {
        hideSoftKeyboard();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDestoryView()) {
                    EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SHOW_LOADING_VIEW_ACTION));
                }
            }
        }, 100);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_input_ok) {
            mPresenter.devLogin();
        }
    }

    @Override
    public String getDevicePassword() {
        return mPwdEdit.getText().toString();
    }

    @Override
    public void goSoftAPWifiListPage() {
        PageNavigationHelper.gotoSoftApWifiListPage(this);
    }

    @Override
    public void goDeviceBindPage() {
        PageNavigationHelper.gotoDeviceBindPage(this);
    }

    @Override
    public void goBindSuceesPage() {
        PageNavigationHelper.gotoBindSuccessPage(this);
    }

    @Override
    public void goOtherUserBindTipPage() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_BIND_BY_OTHER);
    }

    @Override
    public void goErrorTipPage(int errorCode) {
        PageNavigationHelper.gotoErrorTipPage(this, errorCode);
    }

    @Override
    public void completeAction() {
       if(getActivity() != null) getActivity().finish();
    }

    @Override
    public void goDevLoginPage() {
        PageNavigationHelper.gotoDevLoginPage(this);
    }

    @Override
    public void goDevSecCodePage() {
        PageNavigationHelper.gotoDevSecCodePage(this);
    }

    @Override
    public void afterChanged(EditText v, Editable s) {

    }

    @Override
    public void beforeChanged(EditText v, CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(EditText v, CharSequence text, int start, int lengthBefore, int lengthAfter) {
        UIUtils.setEnabledEX(text.length() > 0, mNext);
    }

}
