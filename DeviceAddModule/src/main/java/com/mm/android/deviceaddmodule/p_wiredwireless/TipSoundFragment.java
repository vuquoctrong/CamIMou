package com.mm.android.deviceaddmodule.p_wiredwireless;


import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.base.BaseTipFragment;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

/**
 * Device sound prompt page
 *
 * 设备声音提示页
 */
public class TipSoundFragment extends BaseTipFragment {

    private int mCountDownTime = 5;             // 倒计时5s
    private Handler mHandle = new Handler();

    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            if(!isAdded() || getActivity().isFinishing()){
                return;
            }
            if(mCountDownTime > 0) {
                mNextBtn.setText(String.format(getString(R.string.add_device_next_step_count_down), mCountDownTime));
                mCountDownTime--;
                mHandle.postDelayed(this, 1000);
            } else {
                nextAction();
            }
        }
    };

    public static TipSoundFragment newInstance() {
        TipSoundFragment fragment = new TipSoundFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tipImageMatch();
        mTipImg.setImageResource(R.drawable.adddevice_netsetting_near);
        mTipImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        String configMode=DeviceAddModel.newInstance().getDeviceInfoCache().getConfigMode();
        boolean isSupportSoundWave = configMode!=null && configMode.contains(DeviceAddInfo.ConfigMode.SoundWave.name());
        boolean isSupportSoundWaveV2 = DeviceAddHelper.isSupportSoundWaveV2(DeviceAddModel.newInstance().getDeviceInfoCache());
        if(isSupportSoundWave || isSupportSoundWaveV2){
            mTipTxt.setText(R.string.add_device_turn_up_volume_notice);
            mTipTxt2.setVisibility(View.VISIBLE);
            if(ProviderManager.getAppProvider().getAppType()==1) {
                mTipTxt2.setText(R.string.add_device_phone_will_emit_signal);
            } else {
                mTipTxt2.setText(isSupportSoundWaveV2 ? R.string.add_device_will_hear_jiji : R.string.add_device_will_hear_bugu);
            }
        }else {
            mTipTxt.setText(R.string.add_device_keep_phone_close_to_device);
            mTipImg.setImageResource(R.drawable.adddevice_netsetting_closeto);
        }

        mHandle.post(mRunable);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceAddInfo deviceAddInfo=DeviceAddModel.newInstance().getDeviceInfoCache();
        if(DeviceAddInfo.ConfigMode.LAN.name().equalsIgnoreCase(deviceAddInfo.getConfigMode())
                || !deviceAddInfo.getConfigMode().contains(DeviceAddInfo.ConfigMode.LAN.name()))
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
        else {
            DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE2);
        }
    }

    @Override
    protected void nextAction() {
        mHandle.removeCallbacks(mRunable);

        if (getActivity() != null) {
            AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (current < max * 0.8) {  //音量未调至最大的80%则需要toast提示
                showToastInfo(getString(R.string.add_device_add_volume_tip));
            }
        }

        PageNavigationHelper.gotoSmartConfigPage(this);
    }

    @Override
    protected void helpAction() {

    }

    @Override
    protected void init() {
        initView(mView);
        initData();
    }
}