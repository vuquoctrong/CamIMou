package com.mm.android.mobilecommon.base;

import android.os.Bundle;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.eventbus.event.BaseEvent;
import com.mm.android.mobilecommon.eventbus.event.CloseProgressWindow;

public class ProgressActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.mobile_common_progressdialog_layout);

        setFinishOnTouchOutside(false);
    }

    @Override
    public void onMessageEvent(BaseEvent event) {
        if(event instanceof CloseProgressWindow){
            finish();
        }
    }
}
