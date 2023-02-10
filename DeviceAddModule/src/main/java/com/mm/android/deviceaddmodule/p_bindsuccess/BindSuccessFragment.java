package com.mm.android.deviceaddmodule.p_bindsuccess;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.BindSuccessConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.eventbus.event.CommonEvent;
import com.mm.android.mobilecommon.utils.NameLengthFilter;
import com.mm.android.mobilecommon.utils.WordInputFilter;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.deviceaddmodule.presenter.BindSuccessPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Binding success page
 *
 * 绑定成功页
 **/
public class BindSuccessFragment extends BaseDevAddFragment implements BindSuccessConstract.View,
        View.OnClickListener {
    private final int MAXLETHER = 40;

    BindSuccessConstract.Presenter mPresenter;
    ImageView mDevImg;
    TextView mOK, mModifyDevicePwdTv;
    RelativeLayout mBindSuccessRl;
    ClearEditText mDevNameEdit;
    ScrollView mBindSuccessSv;

    public static BindSuccessFragment newInstance() {
        BindSuccessFragment fragment = new BindSuccessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            String devName = s.toString().trim();
            if (!TextUtils.isEmpty(devName)) {
                mOK.setEnabled(true);
                mDevNameEdit.removeTextChangedListener(mTextWatcher);
                String filterDevName = DeviceAddHelper.strDeviceNameFilter(devName);
                if (!filterDevName.equals(devName)) {
                    mDevNameEdit.setText(filterDevName);
                    mDevNameEdit.setSelection(filterDevName.length());
                }
                mDevNameEdit.addTextChangedListener(mTextWatcher);
            } else {
                mOK.setEnabled(false);
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
        return inflater.inflate(R.layout.fragment_bind_success, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.SHARE);
    }


    protected void initView(View view) {
        mBindSuccessRl = view.findViewById(R.id.bind_success_rl);
        mDevImg = view.findViewById(R.id.dev_img);
        mOK = view.findViewById(R.id.tv_next);
        mBindSuccessSv = view.findViewById(R.id.bind_success_sv);
        mDevNameEdit = view.findViewById(R.id.device_name_input);
        mModifyDevicePwdTv = view.findViewById(R.id.tv_modify_device_pwd);
        setModifyDevicePwdTvClick(getString(R.string.mobile_common_modify_device_pwd_tip), getString(R.string.mobile_common_tap_to_view), getString(R.string.mobile_common_how_modify_device_pwd));
        mOK.setOnClickListener(this);

        mDevNameEdit.setFilters(new InputFilter[]{new WordInputFilter(WordInputFilter.REX_NAME), new NameLengthFilter(MAXLETHER)});
        mDevNameEdit.addTextChangedListener(mTextWatcher);
    }

    protected void initData() {
        mPresenter = new BindSuccessPresenter(this);
        mPresenter.getDevName();
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }


    @Override
    public void updateDevImg(String img) {
        Glide.with(BindSuccessFragment.this.getContext()).load(img).into(mDevImg);
    }


    @Override
    public String getDevName() {
        return mDevNameEdit.getText().toString().trim();
    }

    @Override
    public void setDevName(String name) {
        if (TextUtils.isEmpty(name))
            return;

        mDevNameEdit.setText(name);
        if (!TextUtils.isEmpty(mDevNameEdit.getText().toString()))
            mDevNameEdit.setSelection(mDevNameEdit.getText().toString().length());
    }

    @Override
    public void completeAction() {
        //添加结束哪来回哪，把DeviceAddActivity结束掉
        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.DESTROY_ACTION));
        //通知主页、我的设备列表自动刷新
        EventBus.getDefault().post(new CommonEvent(CommonEvent.DEVICE_ADD_SUCCESS_ACTION));
    }

    @Override
    public void deviceName(String name) {
        setDevName(name);
    }

    public void setModifyDevicePwdTvClick(String str1, String str2, String str3) {
        SpannableString info = new SpannableString(str1 + str2 + " " + str3);
        if (!TextUtils.isEmpty(str1)) {
            info.setSpan(null, 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!TextUtils.isEmpty(str2)) {
            info.setSpan(null, str1.length(), str1.length() + str2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!TextUtils.isEmpty(str3)) {
            info.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    ProviderManager.getAppProvider().goModifyDevicePwdGuidePage(getActivity());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                    ds.setColor(getResources().getColor(R.color.c0));
                }
            }, str1.length() + str2.length(), str1.length() + str2.length() + str3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        mModifyDevicePwdTv.setText(info);
        mModifyDevicePwdTv.setMovementMethod(LinkMovementMethod.getInstance());
        mModifyDevicePwdTv.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_next) {
            mPresenter.modifyDevName();
        }
    }
}
