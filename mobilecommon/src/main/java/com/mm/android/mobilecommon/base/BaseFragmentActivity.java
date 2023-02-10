package com.mm.android.mobilecommon.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.common.HandlerManager;
import com.mm.android.mobilecommon.eventbus.event.BaseEvent;
import com.mm.android.mobilecommon.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BaseFragmentActivity extends FragmentActivity implements IActivityResultDispatch {

    private static final String TAG = BaseFragmentActivity.class.getSimpleName();
    private long mLastOnClickTime = 0;
    protected Context mContext;
    private ProgressDialog mProgressDialog;
    private boolean onDetachedFromWindow = false;
    private Toast mToastInCenter;
    private Toast mToast;
    private List<BaseFragment> mFragments = new ArrayList<>();
    private List<BaseDialogFragment> mDialogFragments = new ArrayList<>();
    protected HandlerManager mHandlerManager;
    private boolean isDestroyed = false;

    public synchronized List<BaseFragment> getBaseFragments() {
        return mFragments;
    }
    protected synchronized void addBaseFragment(BaseFragment fragment){
        if(mFragments.contains(fragment)){
            return;
        }
        mFragments.add(fragment);
    }
    protected synchronized void removeBaseFragment(BaseFragment fragment){
        if(mFragments.contains(fragment)){
            mFragments.add(fragment);
        }
    }

    public  synchronized List<BaseDialogFragment> getDialogFragments() {
        return mDialogFragments;
    }

    protected synchronized void addBaseDialogFragment(BaseDialogFragment fragment){
       if(mDialogFragments.contains(fragment)){
           return;
       }
        mDialogFragments.add(fragment);
    }

    protected synchronized void removeBaseDialogFragment(BaseDialogFragment fragment){
        if(mDialogFragments.contains(fragment)){
            mDialogFragments.remove(fragment);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent event){}

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext, R.style.mobile_common_custom_dialog);
        Window window = mProgressDialog.getWindow();
        if(window != null){
            window.setWindowAnimations(R.style.mobile_common_dialog_anima);
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
        registerBroadCast();
        ActivityManager.getScreenManager().pushActivity(this);
        getWindow().setBackgroundDrawable(null);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        mHandlerManager = new HandlerManager();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDetachedFromWindow = true;
    }

    public boolean isDetachedFromWindow() {
        return onDetachedFromWindow;
    }

    public boolean isActivityDestory() {
        return isDestroyed;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        unRegisterBroadCast();
        // 退出该页面时，手动清理内存缓存
        dissmissProgressDialog();
        mProgressDialog = null;
        ActivityManager.getScreenManager().popActivity(this);
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        mHandlerManager.clearHandlers();
        listeners.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /********************** 注册广播 ****************************/
    private BroadcastReceiver broadcastReceiver = null;

    private void registerBroadCast() {
        IntentFilter mIntentFilter = createBroadCast();
        if (mIntentFilter != null && mIntentFilter.countActions() > 0) {
            broadcastReceiver = new BaseBroadcast();
            registerReceiver(broadcastReceiver, mIntentFilter);
        }
    }

    private void unRegisterBroadCast() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    protected IntentFilter createBroadCast() {
        return null;
    }

    protected void onReceive(Context context, Intent intent) {

    }

    @Override
    public void addOnActivityResultListener(IActivityResultDispatch.OnActivityResultListener listener) {
            listeners.add(listener);
    }

    @Override
    public void removeOnActivityResultListener(IActivityResultDispatch.OnActivityResultListener listener) {
            listeners.remove(listener);
    }

    private class BaseBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BaseFragmentActivity.this.onReceive(context, intent);
        }
    }

    /**
     * Detect quick double click events
     * @return boolean  Boolean Value
     *
     * 检测快速双击事件
     * @return boolean  布尔值
     */
    public Boolean isWindowLocked() {
        long current = SystemClock.elapsedRealtime();
        if (current - mLastOnClickTime > 500) {
            mLastOnClickTime = current;
            return false;
        }
        return true;
    }

    protected void dissmissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showProgressDialog(int layoutId) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setContentView(layoutId);
        }
    }

    protected void cancleProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    protected void setProgressDialogCancle(boolean flag) {
        if (mProgressDialog != null) {
            mProgressDialog.setCancelable(flag);
        }
    }

    protected void toast(int res) {
        String content ="";
        try {
            content = getString(res);
        }catch (Resources.NotFoundException e){
            LogUtil.debugLog("toast", "resource id not found!!!");
        }
        toast(content);
    }

    protected void toast(String content) {
        //系统版本大于等于android 9.0之后的版本
        if(Build.VERSION_CODES.O_MR1 < Build.VERSION.SDK_INT){
            Toast.makeText(mContext,content,Toast.LENGTH_SHORT).show();
        }else{
            if (mToast == null) {
                mToast = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(content);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }
    }

    /**
     * Toast with error code
     * @param errorCode  Error Code
     *
     * 带错误码的toast
     * @param errorCode  错误码
     */
    public void toast(int res, int errorCode) {
        //系统版本大于等于android 9.0之后的版本
        if(Build.VERSION_CODES.O_MR1 < Build.VERSION.SDK_INT){
            Toast.makeText(mContext,getString(res) + "(" + errorCode + ")",Toast.LENGTH_SHORT).show();
        }else {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, getString(res) + "(" + errorCode + ")", Toast.LENGTH_SHORT);
            } else {
                mToast.setText(getString(res) + "(" + errorCode + ")");
            }
            mToast.show();
        }
    }

    protected void toastInCenter(int res) {
        String content ="";
        try {
            content = getString(res);
        }catch (Resources.NotFoundException e){
            LogUtil.debugLog("toast", "resource id not found!!!");
        }
        toastInCenter(content);

    }

    protected void toastInCenter(String content) {
        //系统版本大于等于android 9.0之后的版本
        if(Build.VERSION_CODES.O_MR1 < Build.VERSION.SDK_INT){
            Toast toastInCenter = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
            toastInCenter.setGravity(Gravity.CENTER, 0, 0);
            TextView tv = toastInCenter.getView().findViewById(android.R.id.message);
            tv.setGravity(Gravity.CENTER);
            toastInCenter.show();

        }else{
            if (mToastInCenter == null) {
                mToastInCenter = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
                mToastInCenter.setGravity(Gravity.CENTER, 0, 0);
                TextView tv = mToastInCenter.getView().findViewById(android.R.id.message);
                tv.setGravity(Gravity.CENTER);
            } else {
                mToastInCenter.setText(content);
                mToastInCenter.setDuration(Toast.LENGTH_SHORT);
            }
            mToastInCenter.show();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.mobile_common_slide_in_right, R.anim.mobile_common_slide_out_left);
    }
    
    public void startActivityNoAnimation(Intent intent){
    	super.startActivity(intent);
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        super.startActivityFromFragment(fragment, intent, requestCode);
        overridePendingTransition(R.anim.mobile_common_slide_in_right, R.anim.mobile_common_slide_out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }
    
    public void startActivityForResultWithAnimation(Intent intent, int requestCode){
    	super.startActivityForResult(intent, requestCode);
    	overridePendingTransition(R.anim.mobile_common_slide_in_right, R.anim.mobile_common_slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(
                R.anim.mobile_common_slide_left_back_in,
                R.anim.mobile_common_slide_right_back_out);
    }
    
    public void finishNoAnimation(){
    	super.finish();
    }

    protected final <E extends View> E getView(int id){
        try{
            return (E) findViewById(id);
        }catch (ClassCastException e){
            LogUtil.errorLog("getView","Cloud not cast view to concrete class.",e);
            throw  e;
        }
    }

    protected Handler addHandler(Handler handler){
        return mHandlerManager.addHandler(handler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        synchronized (listeners) {
            for (OnActivityResultListener listener : listeners) {
                if (listener != null) {
                    listener.onActivityResult(requestCode, resultCode, data);
                }
            }

        }
    }

    private final List<OnActivityResultListener> listeners = new ArrayList<>();

}
