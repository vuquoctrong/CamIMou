package com.mm.android.mobilecommon.base;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

import com.mm.android.mobilecommon.R;

/**
 * Contains only one Fragment of Activity
 *
 * 只包含一个Fragment的Activity
 */
public abstract class BaseSingleFragmentActivity<T extends BaseFragment> extends BaseFragmentActivity {
    protected T mFragment;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.mobile_common_comment);
        if (arg0 == null) {
            initContent();
        }
    }

   protected abstract T createFragment();

    protected void initContent() {
        Bundle bundle = getIntent().getExtras();
        mFragment = createFragment();
        if(mFragment == null){
            return;
        }
        mFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.comment, mFragment);
        transaction.commitAllowingStateLoss();
    }
}
