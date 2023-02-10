package com.mm.android.deviceaddmodule.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mm.android.deviceaddmodule.R;

/**
 * Boot prompt page base class
 *
 * 引导提示页基类
 **/
public abstract class BaseTipFragment extends BaseDevAddFragment implements View.OnClickListener {
    protected ImageView mTipImg;
    protected TextView mTipTxt, mTipTxt2, mHelpTxt, mNextBtn;
    protected CheckBox mConfirmCheck;
    protected View mView;

    protected abstract void nextAction();       //下一步操作

    protected abstract void helpAction();       //帮助操作

    protected abstract void init();             //初始化view及Data

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        } else {
            mView = inflater.inflate(R.layout.fragment_base_tip, container, false);
            init();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void initView(View view) {
        mTipImg = view.findViewById(R.id.tip_img);
        mTipTxt = view.findViewById(R.id.tip_txt);
        mTipTxt2 = view.findViewById(R.id.tip_txt2);
        mHelpTxt = view.findViewById(R.id.help_tip);
        mNextBtn = view.findViewById(R.id.tv_next);
        mConfirmCheck = view.findViewById(R.id.cb_confirm);
        mNextBtn.setOnClickListener(this);
        mConfirmCheck.setOnClickListener(this);
        mHelpTxt.setOnClickListener(this);
    }

    //提示图平铺
    protected void tipImageMatch(){
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mTipImg.getLayoutParams();
        params.height= RelativeLayout.LayoutParams.WRAP_CONTENT;
        params.width=RelativeLayout.LayoutParams.MATCH_PARENT;
        params.setMargins(0,0,0,0);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_next) {
            nextAction();
        } else if (id == R.id.cb_confirm) {
            mNextBtn.setEnabled(mConfirmCheck.isChecked());
        } else if (id == R.id.help_tip) {
            helpAction();
        }
    }
}
