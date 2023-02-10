package com.mm.android.deviceaddmodule.p_cloudconnect;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.CloudConnectConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.widget.CircleCountDownView;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.CloudConnectPresenter;

public abstract class BaseCloudFragment extends BaseDevAddFragment implements CloudConnectConstract.View, CircleCountDownView.OnCountDownFinishListener {
    CloudConnectConstract.Presenter mPresenter;

    TextView mTipTxt;
    CircleCountDownView mCountdown_view;
    ImageView mProgressImg,mCloudImage1,mCloudImage2;
    Animatable mCloudAnimation;
    public abstract void goErrorTipPage();
    public abstract void initAction();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_process, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
    }

    protected void initView(View view){
        mTipTxt= view.findViewById(R.id.tv_image_show_tip);
        mProgressImg= view.findViewById(R.id.cloud_progrees_img);
        mCloudImage1= view.findViewById(R.id.cloud_image1);
        mCloudImage2= view.findViewById(R.id.cloud_image2);
        mCountdown_view= view.findViewById(R.id.countdown_view);
        mCountdown_view.setCountDownListener(this);
        mCloudAnimation=(Animatable)mProgressImg.getDrawable();
        startAnimation();
    }

    protected void initData(){
        mPresenter=new CloudConnectPresenter(this);
        initAction();
    }

    private void startAnimation(){
        mCloudAnimation.start();
        AnimatorSet animatorSet=new AnimatorSet();
        ObjectAnimator cloudImage1Anim=ObjectAnimator.ofFloat(mCloudImage1,"translationX",0f,150f,0f);
        cloudImage1Anim.setRepeatMode(ValueAnimator.RESTART);
        cloudImage1Anim.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator cloudImage2Anim=ObjectAnimator.ofFloat(mCloudImage2,"translationX",0f,150f,0f);
        cloudImage2Anim.setRepeatMode(ValueAnimator.RESTART);
        cloudImage2Anim.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.play(cloudImage1Anim).with(cloudImage2Anim);
        animatorSet.setDuration(3000);
        animatorSet.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCountdown_view.stopCountDown();
        if(mCloudAnimation.isRunning()){
            mCloudAnimation.stop();
        }
        mPresenter.recycle();
    }

    @Override
    public void countDownFinished() {
        mPresenter.recycle();
        DeviceAddModel.newInstance().setLoop(false);
        if(mPresenter.isWifiOfflineConfiMode()){
            toast(R.string.add_device_config_failed);
            completeAction();
            mPresenter.stopConnectTiming();
            return;
        }
        goErrorTipPage();
    }

    @Override
    public void completeAction() {
       if(getActivity() != null) getActivity().finish();
    }

    @Override
    public void goOtherUserBindTipPage() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_BIND_BY_OTHER);
    }

    @Override
    public void goNotSupportBuindTipPage() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.DEVICE_BIND_ERROR_NOT_SUPPORT_TO_BIND);
    }

    @Override
    public void goErrorTipPage(int errorCode) {
        PageNavigationHelper.gotoErrorTipPage(this, errorCode);
    }
}
