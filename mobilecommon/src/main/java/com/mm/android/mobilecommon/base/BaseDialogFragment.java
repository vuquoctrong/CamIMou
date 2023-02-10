package com.mm.android.mobilecommon.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Toast;

import com.mm.android.mobilecommon.utils.LogUtil;


public class BaseDialogFragment extends DialogFragment {

    private Toast mToast;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if( getActivity() instanceof BaseFragmentActivity){
            ((BaseFragmentActivity) getActivity()).addBaseDialogFragment(this);
        }
    }

    @Override
    public void onDetach() {
        if( getActivity() instanceof BaseFragmentActivity){
            ((BaseFragmentActivity) getActivity()).removeBaseDialogFragment(this);
        }
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
	@Override
	public int show(FragmentTransaction transaction, String tag) {

	    return show(transaction, tag, true);
	}

	int mBackStackId;

	public int show(FragmentTransaction transaction, String tag, boolean allowStateLoss) {

	    if (this.isAdded()) {//防止重复
            transaction.remove(this);
        }
	    transaction.add(this, tag);
	    mBackStackId = allowStateLoss ? transaction.commitAllowingStateLoss() : transaction.commit();
	    return mBackStackId;
	}
	
	@Override
	public void show(FragmentManager manager, String tag) {

		show(manager.beginTransaction(), tag, true);
	}
	
	protected void toast(int res) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            String content ="";
            try {
                content = getActivity().getString(res);
            }catch (Resources.NotFoundException e){
                LogUtil.debugLog("toast", "resource id not found!!!");
            }
            toast(content);
        }
    }
    
    protected void toast(String content) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (mToast == null) {
                mToast = Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(content);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void dismissAllowingStateLoss() {
        if(getActivity() == null || getFragmentManager() == null || getFragmentManager().isDestroyed()){
            return;
        }

        super.dismissAllowingStateLoss();
    }

    @Override
    public void dismiss() {
        if(getActivity() == null|| getFragmentManager() == null || getFragmentManager().isDestroyed() ){
            return;
        }
        super.dismissAllowingStateLoss();
    }

}
