package com.mm.android.mobilecommon.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.mobilecommon.widget.linechart.utils.Utils;

public class CommonItem extends RelativeLayout {
    private View mTopLine;
    private View mBottomLine;
    private TextView mNameTv;
    private TextView mSubTv;
    private TextView mTitleTv;
    private ImageView mImageView;
    private ProgressBar mLoadingPb;
    private TextView mSwitchTv;

    private OnSwitchClickListener mListener;

    public CommonItem(Context context) {
        this(context, null);
    }

    public CommonItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_common_itme, this);
        initView();
        setListeners();
    }

    public TextView getTittle() {
        return mTitleTv;
    }

    private void initView() {
        mTopLine = findViewById(R.id.top_line);
        mBottomLine = findViewById(R.id.bottom_line);
        mNameTv = (TextView) findViewById(R.id.name_tv);
        mSubTv = (TextView) findViewById(R.id.sub_tv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mImageView = (ImageView) findViewById(R.id.img_iv);
        mLoadingPb = (ProgressBar) findViewById(R.id.loading_pb);
        mSwitchTv = (TextView) findViewById(R.id.switch_tv);
    }

    private void setListeners() {
        mSwitchTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCommonSwitchClick(CommonItem.this);
                }
            }
        });
    }

    public void setOnSwitchClickListener(OnSwitchClickListener listener) {
        mListener = listener;
    }


    public interface OnSwitchClickListener {
        void onCommonSwitchClick(View view);
    }


    /**
     * Set the title
     * @param resId
     *
     * 设置标题
     * @param resId
     */
    public void setTitle(int resId) {
        if (mTitleTv == null || resId <= 0) {
            return;
        }
        mTitleTv.setText(resId);
    }

    /**
     * Currently only my pages are used, adding the left drawable to not show the underscore l
     * @param drawableLeft
     *
     * 目前仅我的页面使用，增加了左侧的drawable不显示下划线l
     * @param drawableLeft
     */
    @SuppressLint("ResourceType")
    public void setCompoundDrawableLeft(@DrawableRes int drawableLeft) {
        if (mTitleTv == null) {
            return;
        }
        mBottomLine.setVisibility(View.GONE);
        if (drawableLeft <= 0) {
            mTitleTv.setCompoundDrawablePadding(0);
            mTitleTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            mTitleTv.setCompoundDrawablePadding((int) Utils.dp2px(13));
            mTitleTv.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, 0, 0);
        }
    }

    /**
     * Set the title
     * @param title
     *
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        if (mTitleTv == null || title == null) {
            return;
        }
        mTitleTv.setText(title);
    }

    /**
     * Set the name
     * @param resId
     *
     * 设置名称
     * @param resId
     */
    public void setName(int resId) {
        if (mNameTv == null || resId <= 0) {
            return;
        }
        mNameTv.setText(resId);
        setNameVisible(true);
        setImageVisible(false);
        setLoadingVisible(false);
        setSwitchVisible(false);
    }

    /**
     * Set the name
     * @param name
     *
     * 设置名称
     * @param name
     */
    public void setName(String name) {
        if (mNameTv == null || name == null) {
            return;
        }
        mNameTv.setText(name);
        setNameVisible(true);
        setImageVisible(false);
        setLoadingVisible(false);
        setSwitchVisible(false);
    }

    /**
     * Set the name
     *
     * 设置名称
     */
    public String getName() {
        if (mNameTv == null) {
            return "";
        }
        return mNameTv.getText().toString();
    }

    /**
     * Set arrow display
     * @param visible
     *
     * 设置箭头显示
     * @param visible
     */
    public void setSubVisible(boolean visible) {
        if (mSubTv == null) {
            return;
        }
        if (visible) {
            mSubTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.device_manager_icon_nextarrow, 0);
        } else {
            mSubTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    /**
     * Set the progress bar
     * @param visible
     *
     * 设置进度条显示
     * @param visible
     */
    public void setLoadingVisible(boolean visible) {
        if (mLoadingPb == null) {
            return;
        }
        mLoadingPb.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            setNameVisible(false);
            setSwitchVisible(false);
            setImageVisible(false);
        }
    }

    private void setNameVisible(boolean visible) {
        if (mNameTv == null) {
            return;
        }
        mNameTv.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setSwitchVisible(boolean visible) {
        if (mSwitchTv == null) {
            return;
        }
        mSwitchTv.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setImageVisible(boolean visible) {
        if (mImageView == null) {
            return;
        }
        mImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Set the top divider display
     * @param visible
     *
     * 设置顶部分割线显示
     * @param visible
     */
    public void setTopLineVisible(boolean visible) {
        if (mTopLine == null) {
            return;
        }
        mTopLine.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Set the bottom divider display
     * @param visible
     *
     * 设置底部分割线显示
     * @param visible
     */
    public void setBottomLineVisible(boolean visible) {
        if (mBottomLine == null) {
            return;
        }
        mBottomLine.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setBottomLineLeftMargin(int dp) {
        if (mBottomLine == null) {
            return;
        }

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mBottomLine.getContext().getResources().getDisplayMetrics());
        setMargins(mBottomLine, px, 0, 0, 0);
    }

    public void setNameRightMargin(int dp){
        if (mNameTv == null) {
            return;
        }

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mNameTv.getContext().getResources().getDisplayMetrics());
        setMargins(mNameTv, 0, 0, px, 0);
    }


    /**
     * Set display without permission
     *
     * 设置无权限显示
     */
    public void setItemNoAuthority() {
        setName(R.string.common_no_authority);
        setItemEnable(false);
        setTitleEnable(true);
    }

    public void setTitleEnable(boolean enable) {
        if (mTitleTv == null) {
            return;
        }
        mTitleTv.setEnabled(enable);
    }

    /**
     * Set the gray display
     *
     * 设置置灰显示
     */
    public void setItemEnable(boolean enable) {
//        setTitleEnable(enable);
//        setNameEnable(enable);
        setDevManagerItemEnable(enable, this, mSubTv);
//        setSwitchEnable(enable);
//        setSubVisible(enable);
        setClickable(enable);
    }

    public void setIteEnableWithoutClickEnable(boolean enable) {
        setDevManagerItemEnable(enable, this, null);
    }

    private void setDevManagerItemEnable(boolean enabled, ViewGroup viewGroup, TextView tv) {
        UIUtils.setEnabledSub(enabled, viewGroup);

        if (tv == null) {
            return;
        }
        if (enabled) {
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.device_manager_icon_nextarrow, 0);
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    /**
     * Setting is clickable
     *
     * 设置是否可点击
     */
    public void setItemClickable(boolean clickable) {
        setClickable(clickable);
        setSubVisible(clickable);
    }

    /**
     * Setting Offline Display
     *
     * 设置离线显示
     */
    public void setItemOffLine() {
        setName(R.string.common_offline);
        setItemEnable(false);
    }

    /**
     * Set the cover image not to be displayed
     *
     * 设置封面图不显示
     */
    public void setImageInVisible() {
        setImageVisible(false);
        setNameVisible(false);
        setSwitchVisible(false);
        setLoadingVisible(false);
    }

    /**
     * Set whether to select
     * @param isSelected
     *
     * 设置是否选中
     * @param isSelected
     */
    public void setSwitchSelected(boolean isSelected) {
        if (mSwitchTv == null) {
            return;
        }
        mSwitchTv.setSelected(isSelected);
        setSwitchVisible(true);
        setNameVisible(false);
        setLoadingVisible(false);
        setSubVisible(false);
    }

    /**
     * Gets whether it is selected
     *
     * 获取是否选中
     */
    public boolean isSwitchSelected() {
        return mSwitchTv != null && mSwitchTv.isSelected();
    }

    /**
     * Set whether the button is clickable
     * @param enable
     *
     * 设置按钮是否可点击
     * @param enable
     */
    public void setSwitchEnable(boolean enable) {
        if (mSwitchTv == null) {
            return;
        }
        mSwitchTv.setEnabled(enable);
    }

    /**
     * Whether to display red dots
     * @param hasRedDot
     *
     * 是否显示红点
     * @param hasRedDot
     */
    public void setNameRedDot(boolean hasRedDot) {
        if (mNameTv == null) {
            return;
        }
        if (hasRedDot) {
            mNameTv.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.common_newmessage,
                    0);
        } else {
            mNameTv.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0);
        }
    }

    // 设置view 的margin值
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams p = (MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    //设置无箭头显示
    public void setNoArrowName(int resId) {
        setName(resId);
        setSubVisible(false);
        setLoadingVisible(false);
    }

    public void setRightDrawableCheck(boolean isCheck) {
        setName("");
        if (isCheck) {
            mNameTv.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.setting_icon_check,
                    0);
        } else {
            mNameTv.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0);
        }
    }
}
