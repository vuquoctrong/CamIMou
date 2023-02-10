package com.mm.android.deviceaddmodule.p_typechoose;


import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.TypeChooseConstract;
import com.mm.android.deviceaddmodule.event.DeviceAddEvent;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.mobilecommon.widget.ClearEditText;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.TypeChoosePresenter;
import com.mm.android.deviceaddmodule.views.ChooseNetDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Device type selection page
 *
 * 设备类型选择页
 */
public class TypeChooseFragment extends BaseDevAddFragment implements TypeChooseConstract.View, View.OnClickListener {
    TypeChooseConstract.Presenter mPresenter;
    View mView;
    private ClearEditText mTvNameInput;
    private TextView mTvTip;
    private TextView mTvSure;
    private boolean isSelfInput = true;
    private ImageView ivSelfInput;
    private ImageView ivChooseOther;

    public static TypeChooseFragment newInstance() {
        TypeChooseFragment fragment = new TypeChooseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isDestoryView = false;
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        } else {
            mView = inflater.inflate(R.layout.fragment_type_input, container, false);
            initView(mView);
            initData();
        }
        if (mPresenter != null) {
            mPresenter.resetDevPwdCache();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    protected void initView(View view) {
        mTvNameInput = view.findViewById(R.id.tv_name_input);
        view.findViewById(R.id.ll_choose_other).setOnClickListener(this);
        view.findViewById(R.id.ll_self_input).setOnClickListener(this);
        mTvTip = view.findViewById(R.id.tv_tip);
        ivSelfInput = view.findViewById(R.id.iv_self_input);
        ivChooseOther = view.findViewById(R.id.iv_choose_other);
        mTvSure = view.findViewById(R.id.tv_sure);
        mTvSure.setOnClickListener(this);

        String str1 = getResources().getString(R.string.add_device_choose_type_tip1);
        String str2 = getResources().getString(R.string.add_device_choose_type_tip2);
        String str3 = getResources().getString(R.string.add_device_choose_type_tip3);
        String str4 = getResources().getString(R.string.add_device_choose_type_tip4);
        String str5 = getResources().getString(R.string.add_device_choose_type_tip5);
        String str6 = getResources().getString(R.string.add_device_choose_type_tip6);
        SpannableString info = new SpannableString(str1 + str2 + str3 + str4 + str5 + str6);
        if (!TextUtils.isEmpty(str1)) {
            info.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.cf4)), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            info.setSpan(new StyleSpan(Typeface.BOLD), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(str2)) {
            info.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c40)), str1.length(), str1.length() + str2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            info.setSpan(new StyleSpan(Typeface.BOLD), str1.length(), str1.length() + str2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(str3)) {
            info.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c40)), str1.length() + str2.length(), str1.length() + str2.length() + str3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(str4)) {
            info.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c40)), str1.length() + str2.length() + str3.length(), str1.length() + str2.length() + str3.length() + str4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            info.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),str1.length() + str2.length() + str3.length(), str1.length() + str2.length() + str3.length() + str4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(str5)) {
            info.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c40)), str1.length() + str2.length() + str3.length() + str4.length(), str1.length() + str2.length() + str3.length() + str4.length() + str5.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(str6)) {
            info.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c40)), str1.length() + str2.length() + str3.length() + str4.length() + str5.length(), str1.length() + str2.length() + str3.length() + str4.length() + str5.length() + str6.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            info.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),str1.length() + str2.length() + str3.length() + str4.length() + str5.length(), str1.length() + str2.length() + str3.length() + str4.length() + str5.length() + str6.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mTvTip.setText(info);
    }

    protected void initData() {
        mPresenter = new TypeChoosePresenter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.resetDevPwdCache();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_choose_other) {
            isSelfInput = false;
            ivSelfInput.setImageResource(R.drawable.adddevice_box_checkbox);
            ivChooseOther.setImageResource(R.drawable.adddevice_box_checkbox_checked);
        } else if (id == R.id.ll_self_input) {
            isSelfInput = true;
            ivChooseOther.setImageResource(R.drawable.adddevice_box_checkbox);
            ivSelfInput.setImageResource(R.drawable.adddevice_box_checkbox_checked);
        } else if (id == R.id.tv_sure) {
            if (UIUtils.isFastDoubleClick()) {
                return;
            }
            if (isSelfInput) {
                //手动输入
                String name = mTvNameInput.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    toast(R.string.device_tip_content);
                    return;
                }
                mPresenter.getDeviceInfoSync(name);
            } else {
                //其他选择
                ChooseNetDialog chooseNetDialog = new ChooseNetDialog(getContext());
                chooseNetDialog.setOnChooseNetLisenter(new ChooseNetDialog.OnChooseNetLisenter() {
                    @Override
                    public void softap() {
                        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_SOFTAP_ACTION));
                        DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.SOFTAP);
                    }

                    @Override
                    public void wlan() {
                        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_WIRELESS_ACTION));
                        DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.WLAN);
                    }

                    @Override
                    public void lan() {
                        EventBus.getDefault().post(new DeviceAddEvent(DeviceAddEvent.CHANGE_TO_WIRED_ACTION));
                        DeviceAddModel.newInstance().getDeviceInfoCache().setCurDeviceAddType(DeviceAddInfo.DeviceAddType.LAN);
                    }
                });
                chooseNetDialog.show();
            }
        }
    }

    LCAlertDialog mLCAlertDialog;
    private final String searchError = "searchError";

    @Override
    public void showSearchError() {
        LCAlertDialog.Builder builder = new LCAlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setMessage(R.string.add_device_choose_type_error);
        builder.setCancelButton(R.string.common_cancel, null);
        builder.setConfirmButton(R.string.common_confirm,
                new LCAlertDialog.OnClickListener() {
                    @Override
                    public void onClick(LCAlertDialog dialog, int which, boolean isChecked) {
                        dismissLCAlertDialog();
                    }
                });

        mLCAlertDialog = builder.create();
        mLCAlertDialog.show(getActivity().getSupportFragmentManager(),
                searchError);
    }

    private void dismissLCAlertDialog() {
        if (mLCAlertDialog != null && mLCAlertDialog.isVisible()) {
            mLCAlertDialog.dismissAllowingStateLoss();
            mLCAlertDialog = null;
        }
    }

}
