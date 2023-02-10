package com.mm.android.deviceaddmodule.p_inputsn;

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
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.StringUtils;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

import org.greenrobot.eventbus.EventBus;

/**
 * iMei input page
 *
 * iMei输入页
 */
public class IMEIInputFragment extends BaseDevAddFragment implements View.OnClickListener
        ,ClearEditText.ITextChangeListener {
    private ClearEditText mUserInputET; // imei输入框
    private TextView mNextBtn;

    public static IMEIInputFragment newInstance() {
        IMEIInputFragment fragment = new IMEIInputFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_imei_input, container, false);
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
    }

    @Override
    protected void initData() {
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
     * 处理输入
     */
    private void handleInputDone() {
        String inputIMEI = mUserInputET.getText().toString().trim().toUpperCase();
        if (isIMEIInValid(inputIMEI)) {
            return;
        }
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
//        deviceAddInfo.setImeiCode(inputIMEI);

        goNBTipPage();
    }

    private boolean isIMEIInValid(String imei) {
        if(TextUtils.isEmpty(imei)
                || imei.length() < 15 || imei.length() > 17){
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(R.id.next_btn==id){
            handleInputDone();
        }
    }

    @Override
    public void afterChanged(EditText v, Editable s) {
        mNextBtn.setEnabled(!isIMEIInValid(mUserInputET.getText().toString()));
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


    private void goNBTipPage() {
        PageNavigationHelper.gotoNBTipPage(this);
    }

}
