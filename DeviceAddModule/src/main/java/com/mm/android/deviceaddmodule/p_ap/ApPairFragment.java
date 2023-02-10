package com.mm.android.deviceaddmodule.p_ap;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.ApPairConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.AddApResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.mobilecommon.widget.CircleCountDownView;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;
import com.mm.android.deviceaddmodule.presenter.ApPairPresenter;

/**
 * Accessories gateway matching page
 *
 * 配件网关配对页
 **/
public class ApPairFragment extends BaseDevAddFragment implements ApPairConstract.View, CircleCountDownView.OnCountDownFinishListener {
    ApPairConstract.Presenter mPresenter;
    ImageView mTipImg,mPairImage1,mPairImage2;
    TextView mTipTxt,mTipTxt2;
    CircleCountDownView mCountDownView;


    public static ApPairFragment newInstance() {
        ApPairFragment fragment = new ApPairFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ap_pair, container, false);
    }

    @Override
    protected void initView(View view) {
        mPairImage1= view.findViewById(R.id.pair_img1);
        mPairImage2= view.findViewById(R.id.pair_img2);
        mTipImg= view.findViewById(R.id.tip_img);
        mTipTxt= view.findViewById(R.id.tip_txt);
        mTipTxt2= view.findViewById(R.id.tip_txt2);
        mCountDownView= view.findViewById(R.id.countdown_view);
        mCountDownView.setCountDownListener(this);
        mCountDownView.startCountDown();
        startAnimation();
    }

    private void startAnimation(){
        AnimatorSet animatorSet=new AnimatorSet();
        ObjectAnimator cloudImage1Anim=ObjectAnimator.ofFloat(mPairImage1,"translationX",-100f,0f);
        cloudImage1Anim.setRepeatMode(ValueAnimator.RESTART);
        cloudImage1Anim.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator cloudImage2Anim=ObjectAnimator.ofFloat(mPairImage2,"translationX",100f,0f);
        cloudImage2Anim.setRepeatMode(ValueAnimator.RESTART);
        cloudImage2Anim.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.play(cloudImage1Anim).with(cloudImage2Anim);
        animatorSet.setDuration(1500);
        animatorSet.start();
    }

    @Override
    protected void initData() {
        mPresenter=new ApPairPresenter(this);
        mPresenter.pair();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCountDownView.stopCountDown();
    }

    @Override
    public void countDownFinished() {
        mPresenter.stopPair();
        goErrorTipPage();
    }

    @Override
    public void middleTimeUp() {

    }

    @Override
    public void goErrorTipPage() {
        PageNavigationHelper.gotoErrorTipPage(this, DeviceAddHelper.ErrorCode.AP_ERROR_PAIR_TIMEOUT);
    }

    @Override
    public void goApBindSuccessPage(AddApResult addApResult) {
        mPresenter.stopPair();
        PageNavigationHelper.gotoApBindSuccessPage(this,addApResult);
    }
}
