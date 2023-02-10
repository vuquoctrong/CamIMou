package com.mm.android.mobilecommon.base;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.base.api.IProgressDialogControlView;
import com.mm.android.mobilecommon.common.HandlerManager;
import com.mm.android.mobilecommon.eventbus.event.BaseEvent;
import com.mm.android.mobilecommon.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

/**
 * The base class, no additional functions
 *
 * 基类，不加其他功能
 */
public class BaseFragment extends Fragment implements IProgressDialogControlView {
    private static String TAG = "BaseFragment";
    private ProgressDialog mProgressDialog;
    private Toast mToast;
    private Toast mToastInCenter;
    private Toast mToastWithImg;
    protected HandlerManager mHandlerManager;

    public BaseFragment() {

    }

    public boolean isCurrentPageView() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent event) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadCast();
        mProgressDialog = new ProgressDialog(getActivity(), R.style.mobile_common_custom_dialog);
        Window window = mProgressDialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.mobile_common_dialog_anima);
        }

        mProgressDialog.setCanceledOnTouchOutside(false);
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        mHandlerManager = new HandlerManager();
    }

    @Override
    public void showProgressDialog(int layoutId) {
        if (isDialogEnable() && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setContentView(layoutId);
        }
    }

    @Override
    public void dissmissProgressDialog() {
        if (isDialogEnable() && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void cancleProgressDialog() {
        if (isDialogEnable() && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void setProgressDialogCancelable(boolean flag) {
        if (isDialogEnable()) {
            mProgressDialog.setCancelable(flag);
        }
    }

    @Override
    public void setProgressDialogCancelListener(DialogInterface.OnCancelListener cancelListener) {
        if (isDialogEnable()) {
            mProgressDialog.setOnCancelListener(cancelListener);
        }
    }

    private boolean isDialogEnable() {
        return getActivity() != null && !getActivity().isFinishing() && mProgressDialog != null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) getActivity()).addBaseFragment(this);
        }
    }

    @Override
    public void onDetach() {
        if (getActivity() instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) getActivity()).removeBaseFragment(this);
        }
        super.onDetach();
//        try {
//            Field childFragment = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragment.setAccessible(true);
//            childFragment.set(this, null);
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public void onDestroyView() {
        dissmissProgressDialog();
        mProgressDialog = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadCast();
        dissmissProgressDialog();
        mProgressDialog = null;
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        mHandlerManager.clearHandlers();
    }

    protected void toast(int res) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            String content = "";
            try {
                content = getActivity().getString(res);
            } catch (Resources.NotFoundException e) {
                LogUtil.debugLog("toast", "resource id not found!!!");
            }
            toast(content);
        }
    }

    protected void toast(String content) {
        if (!isAdded()) {
            return;
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            //系统版本大于等于android 9.0之后的版本
            if (Build.VERSION_CODES.O_MR1 < Build.VERSION.SDK_INT) {
                Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
            } else {
                if (mToast == null) {
                    mToast = Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(content);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        }
    }

    protected void toastInCenter(int res) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            String content = "";
            try {
                content = getActivity().getString(res);
            } catch (Resources.NotFoundException e) {
                LogUtil.debugLog("toast", "resource id not found!!!");
            }
            toastInCenter(content);
        }
    }

    protected void toastInCenter(String content) {
        if (!isAdded()) {
            return;
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            Toast toastInCenter = null;
            //系统版本大于等于android 9.0之后的版本
            if (Build.VERSION_CODES.O_MR1 < Build.VERSION.SDK_INT) {
                toastInCenter = Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT);
            } else {
                if (mToastInCenter == null) {
                    mToastInCenter = Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT);
                } else {
                    mToastInCenter.setText(content);
                    mToastInCenter.setDuration(Toast.LENGTH_SHORT);
                }
                toastInCenter = mToastInCenter;
            }
            if (toastInCenter == null) return;
            ViewGroup toastView = (ViewGroup) toastInCenter.getView();
            View imageCodeProject = toastView.getChildAt(0);
            if (imageCodeProject != null && (imageCodeProject instanceof ImageView)) {
                ((ImageView) imageCodeProject).setImageResource(0);
            }
            toastInCenter.show();
        }
    }

    protected void toastWithImg(int strResId, int imgId) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            String content = "";
            try {
                content = getActivity().getString(strResId);
            } catch (Resources.NotFoundException e) {
                LogUtil.debugLog("toast", "resource id not found!!!");
            }
            try {
                toastWithImg(content, imgId);
            } catch (Exception e) {
                Toast.makeText(getActivity(), content , Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void toastWithImg(String content, int imgId) {
        if (!isAdded()) {
            return;
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            Toast toastInCenter = null;
            //系统版本大于等于android 9.0之后的版本
            if (Build.VERSION_CODES.O_MR1 < Build.VERSION.SDK_INT) {
                toastInCenter = Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT);
            } else {
                if (mToastWithImg == null) {
                    mToastWithImg = Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT);
                } else {
                    mToastWithImg.setText(content);
                    mToastWithImg.setDuration(Toast.LENGTH_SHORT);
                }
                toastInCenter = mToastWithImg;
            }
            if (toastInCenter == null) return;

            toastInCenter.setGravity(Gravity.CENTER, 0, 0);
            TextView tv = toastInCenter.getView().findViewById(android.R.id.message);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv.setLineSpacing(0, 1.2f);
            tv.setBackgroundResource(0);
            // set padding
            int paddingHorizontal = (int) getResources().getDimension(R.dimen.mobile_common_dp_10);
            int paddingVertical = (int) getResources().getDimension(R.dimen.mobile_common_dp_10);
            ViewGroup toastView = (ViewGroup) toastInCenter.getView();
            toastView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            toastView.setBackgroundResource(R.drawable.mobile_common_shape_round_bg);
            if (imgId > 0) {
                View imageCodeProject = toastView.getChildAt(0);
                if (imageCodeProject == null || !(imageCodeProject instanceof ImageView)) {
                    imageCodeProject = new ImageView(getActivity());
                    toastView.addView(imageCodeProject, 0);
                }
                ((ImageView) imageCodeProject).setImageResource(imgId);
            } else {
                View imageCodeProject = toastView.getChildAt(0);
                if (imageCodeProject == null || !(imageCodeProject instanceof ImageView)) {
                    imageCodeProject = new ImageView(getActivity());
                    toastView.addView(imageCodeProject, 0);
                }
                ((ImageView) imageCodeProject).setImageResource(0);
            }
            toastInCenter.show();
        }

    }


    public boolean onBackPressed() {
        return false;
    }

    /********************** 注册广播 ****************************/
    private BroadcastReceiver broadcastReceiver = null;

    private void registerBroadCast() {
        IntentFilter mIntentFilter = createBroadCast();
        if (mIntentFilter != null && mIntentFilter.countActions() > 0) {
            broadcastReceiver = new BaseBroadcast();
            if (getActivity() != null)
                getActivity().registerReceiver(broadcastReceiver, mIntentFilter);
        }
    }

    protected void unRegisterBroadCast() {

        if (broadcastReceiver != null) {
            if (getActivity() != null) getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;

        }
    }

    protected IntentFilter createBroadCast() {
        return null;
    }

    protected void onReceive(Context context, Intent intent) {

    }

    private class BaseBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BaseFragment.this.onReceive(context, intent);
        }
    }


    protected final <E extends View> E getView(View rootView, int id) {
        try {
            return (E) rootView.findViewById(id);
        } catch (ClassCastException e) {
            LogUtil.errorLog("getView", "Cloud not cast view to concrete class.", e);
            throw e;
        } catch (NullPointerException e) {
            LogUtil.errorLog("getView", "rootView is null.", e);
            throw e;
        }
    }

    protected Handler addHandler(Handler handler) {
        return mHandlerManager.addHandler(handler);
    }

    protected <V> V inflateViewStub(ViewStub viewStub, Class<V> clz) {
        V view;
        if (viewStub.getParent() != null) {
            view = (V) viewStub.inflate();
            viewStub.setTag(view);
        } else {
            view = (V) viewStub.getTag();
        }
        return view;
    }

    /**
     * Whether it's already inflate
     * @param viewStub
     * @return true shows it's already inflate
     *
     * 是否已经inflate
     * @param viewStub
     * @return true表示已经inflate
     */
    protected boolean isViewStubInflate(ViewStub viewStub) {
        return viewStub.getParent() == null;
    }

    /**
     * Update the viewStub whether or not visible
     * @param viewStub
     * @param visible  Visible
     *
     * 更新ViewStub是否可见
     * @param viewStub
     * @param visible  可见度
     */
    protected void updateViewStubVisibility(ViewStub viewStub, int visible) {
        switch (visible) {
            case View.VISIBLE:
                inflateViewStub(viewStub, View.class).setVisibility(View.VISIBLE);
                break;
            default:
                if (isViewStubInflate(viewStub)) {
                    inflateViewStub(viewStub, View.class).setVisibility(visible);
                }
                break;
        }
    }

}
