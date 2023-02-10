package com.mm.android.deviceaddmodule.p_devlogin;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.DevSecCodeConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.deviceaddmodule.presenter.DevSecCodePresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Device security code page
 *
 * 设备安全码页面
 */
public class DevSecCodeFragment extends BaseDevAddFragment implements DevSecCodeConstract.View,View.OnClickListener {
    DevSecCodeConstract.Presenter mPresenter;
    ClearEditText mUserInputEdit;
    TextView mNext;
    Handler mHandler=new Handler();

    public static DevSecCodeFragment newInstance() {
        DevSecCodeFragment fragment = new DevSecCodeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dev_sec_code, container, false);
    }

    @Override
    protected void initView(View view) {
        mNext= view.findViewById(R.id.next_btn);
        mUserInputEdit = view.findViewById(R.id.et_user_input);
        mNext.setOnClickListener(this);
    }

    protected void initData(){
        mPresenter=new DevSecCodePresenter(this);
    }

    @Override
    public void showProgressDialog() {
        hideSoftKeyboard();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isDestoryView()) {
                    EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SHOW_LOADING_VIEW_ACTION));
                }
            }
        },100);

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.next_btn){
            mPresenter.validate();
        }
    }

    @Override
    public String getDeviceSecCode() {
        return mUserInputEdit.getText().toString();
    }

    @Override
    public void goErrorTipPage(int errorCode) {
        PageNavigationHelper.gotoErrorTipPage(this, errorCode);
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
}
