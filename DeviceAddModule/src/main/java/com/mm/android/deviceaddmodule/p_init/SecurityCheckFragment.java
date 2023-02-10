package com.mm.android.deviceaddmodule.p_init;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.lechange.opensdk.media.DeviceInitInfo;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.SearchDeviceManager;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.SecurityCheckConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.widget.CircleCountDownView;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.SecurityCheckPresenter;

/**
 * The initial security check page is displayed
 *
 * 设备初始安全检查页面
 */
public class SecurityCheckFragment extends BaseDevAddFragment implements SecurityCheckConstract.View, CircleCountDownView.OnCountDownFinishListener {
    SecurityCheckConstract.Presenter mPresenter;
    CircleCountDownView mCountdown_view;
    TranslateAnimation mAnimation;
    ImageView mScanLine;
    long mEventStartTime;       //统计开始时间
    private SearchDeviceManager mServiceManager;

    public static SecurityCheckFragment newInstance() {
        SecurityCheckFragment fragment = new SecurityCheckFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security_check, container, false);
    }

    protected void initView(View view){
        mScanLine= view.findViewById(R.id.scan_line);
        mCountdown_view= view.findViewById(R.id.countdown_view);
        mCountdown_view.setCountDownListener(this);
        mCountdown_view.startCountDown();

        mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.2f, TranslateAnimation.RELATIVE_TO_PARENT, 0.7f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mScanLine.setAnimation(mAnimation);
        mEventStartTime = System.currentTimeMillis();
    }

    protected void initData(){
        mPresenter=new SecurityCheckPresenter(this);
        mServiceManager = SearchDeviceManager.getInstance();
        mServiceManager.registerListener((SearchDeviceManager.ISearchDeviceListener) mPresenter);
        mServiceManager.connectService((SearchDeviceManager.ISearchDeviceListener) mPresenter);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPresenter.checkDevice();
//            }
//        },1000);
        //安全检查页更多只能A方案
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.recyle();
        mCountdown_view.stopCountDown();
    }

    @Override
    public void countDownFinished() {
        //超时
        mPresenter.recyle();
        goErrorTipPage();
    }

    @Override
    public void middleTimeUp() {

    }

    @Override
    public void goInitPage(DeviceInitInfo device_net_info_ex) {
        PageNavigationHelper.gotoInitPage(this,device_net_info_ex);
    }

    @Override
    public void goErrorTipPage() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.INIT_ERROR_SERCRITY_CHECK_TIMEOUT);
    }

    @Override
    public void goDevLoginPage() {
        PageNavigationHelper.gotoDevLoginPage(this);
    }

    @Override
    public void goSoftApWifiListPage(boolean isNotNeedLogin) {
        PageNavigationHelper.gotoSoftApWifiListPage(this, isNotNeedLogin);
    }

    @Override
    public void goCloudConnectPage() {
        PageNavigationHelper.gotoCloudConnectPage(this);
    }
}
