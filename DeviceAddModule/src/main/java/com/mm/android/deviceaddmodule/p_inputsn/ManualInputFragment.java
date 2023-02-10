package com.mm.android.deviceaddmodule.p_inputsn;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.ManualInputConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.ScanResult;
import com.mm.android.mobilecommon.annotation.DeviceState;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.NetWorkHelper;
import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.mobilecommon.utils.StringUtils;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.deviceaddmodule.presenter.ManualInputPresenter;
import com.mm.android.deviceaddmodule.views.AddBoxTipDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Manually enter the device serial number page
 *
 * 手动输入设备序列号页
 */
public class ManualInputFragment extends BaseDevAddFragment implements ManualInputConstract.View,View.OnClickListener
        ,ClearEditText.ITextChangeListener {
    ManualInputConstract.Presenter mPresenter;
    private ClearEditText mUserInputET; // 序列号输入框
    private ClearEditText mInputScET; // sc码输入框
    private TextView mNextBtn;

    public static ManualInputFragment newInstance() {
        ManualInputFragment fragment = new ManualInputFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual_input, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.BLANK);
    }

    protected void initView(View view){
        mUserInputET= view.findViewById(R.id.et_user_input);
        mUserInputET.setTextChangeListener(this);
        mNextBtn= view.findViewById(R.id.next_btn);
        mNextBtn.setOnClickListener(this);
        mNextBtn.setEnabled(false);

        mInputScET = view.findViewById(R.id.et_input_sc);
        mInputScET.setTextChangeListener(this);
    }

    @Override
    protected void initData() {
        mPresenter=new ManualInputPresenter(this);
    }

    @Override
    public void showProgressDialog() {
        hideSoftKeyboard();
        mNextBtn.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDestoryView()) {
                    EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.SHOW_LOADING_VIEW_ACTION));
                }
            }
        }, 100);

    }

    /**
     * Process the input
     *
     * 处理输入
     */
    private void handleInputDone() {
        String inputSn = mUserInputET.getText().toString().trim().toUpperCase();
        String inputSc = mInputScET.getText().toString().trim();
        if (mPresenter.isSnInValid(inputSn)) {
            return;
        }
        if(mPresenter.isScCodeInValid(inputSc)) {
            return;
        }
        if(inputSc.length() == 7) {
            toast(R.string.add_device_input_corrent_sc_tip);
            return;
        }
        ScanResult scanResult=mPresenter.parseScanStr(inputSn, inputSc);
        mPresenter.getDeviceInfo(scanResult.getSn(),scanResult.getMode(),scanResult.getProductId(),false);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(R.id.next_btn==id){
            if (NetWorkHelper.isConnected(getActivity())) {
                handleInputDone();
            }else {
                toast(R.string.mobile_common_bec_common_timeout);
            }
        }
    }

    @Override
    public void afterChanged(EditText v, Editable s) {
        mNextBtn.setEnabled(!mPresenter.isSnInValid(mUserInputET.getText().toString()) && !mPresenter.isScCodeInValid(mInputScET.getText().toString()));
    }

    @Override
    public void beforeChanged(EditText v, CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(EditText v, CharSequence text, int start, int lengthBefore, int lengthAfter) {
        String str = StringUtils.snFilter(text.toString());
        if (!str.equals(text.toString())) {
            v.setText(str);
            v.setSelection(str.length());
        }
    }

    @Override
    public void goTypeChoosePage() {
        PageNavigationHelper.gotoTypeChoosePage(this);
    }

    @Override
    public void goNotSupportBindTipPage() {
        //TODO 手输序列号的逻辑里面应该不存在不支持类型的设备在线的情况
        PageNavigationHelper.gotoErrorTipPage(this,DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND);
    }

    @Override
    public void goOtherUserBindTipPage(String defaultTip) {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.INPUT_SN_ERROR_BIND_BY_OTHER, defaultTip);
    }

    @Override
    public void showAddBoxTip() {
        if (!PreferencesHelper.getInstance(getActivity()).getBoolean(
                LCConfiguration.SHOW_ADD_BOX_TIP)) {
            AddBoxTipDialog a = new AddBoxTipDialog();
            a.setDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    goCloudConnectPage();
                }
            });
            a.show(getActivity().getSupportFragmentManager(), a.getClass().getName());
        }
        else {
            goCloudConnectPage();
        }
    }

    @Override
    public void goCloudConnectPage() {
        PageNavigationHelper.gotoCloudConnectPage(this);
    }

    @Override
    public void goDeviceLoginPage() {
        PageNavigationHelper.gotoDevLoginPage(this);
    }

    @Override
    public void goSecCodePage() {
        PageNavigationHelper.gotoDevSecCodePage(this);
    }

    @Override
    public void goDeviceBindPage() {
        PageNavigationHelper.gotoDeviceBindPage(this);
    }

    @Override
    public void goIMEIInputPage() {
        PageNavigationHelper.gotoIMEIInputPage(this);
    }

    @Override
    public void goBindSuccessPage() {
        PageNavigationHelper.gotoBindSuccessPage(this);
    }

    @Override
    public void gotoIotAddModule(DeviceAddInfo deviceAddInfo) {
        if (DeviceState.ONLINE.equals(deviceAddInfo.getStatus())) {
            PageNavigationHelper.gotoDeviceBindPage(ManualInputFragment.this, true);
        } else {
            if (deviceAddInfo != null && !TextUtils.isEmpty(deviceAddInfo.getDefaultWifiConfigMode()) && ("4g".equals(deviceAddInfo.getDefaultWifiConfigMode().toLowerCase())
                    || "lan".equals(deviceAddInfo.getDefaultWifiConfigMode().toLowerCase()))) {
                PageNavigationHelper.gotoIotGuidePage(this.getActivity());
            } else {
                PageNavigationHelper.gotoIotWifiChoosePage(this.getActivity());
            }
        }
    }
}
