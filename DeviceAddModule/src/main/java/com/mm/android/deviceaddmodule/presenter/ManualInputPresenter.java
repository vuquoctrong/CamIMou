package com.mm.android.deviceaddmodule.presenter;

import android.text.TextUtils;

import com.mm.android.deviceaddmodule.contract.ManualInputConstract;
import com.mm.android.mobilecommon.AppConsume.ScanResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;
import com.mm.android.deviceaddmodule.model.DeviceAddModel;

public class ManualInputPresenter extends ScanPresenter {
    public ManualInputPresenter(ManualInputConstract.View view){
        super(view);
        DeviceAddInfo deviceAddInfo = DeviceAddModel.newInstance().getDeviceInfoCache();
        deviceAddInfo.setManualInput(true);
    }

    @Override
    public ScanResult parseScanStr(String scanStr, String sc) {
        return super.parseScanStr(scanStr, sc);
    }

    @Override
    public boolean isManualInputPage() {
        return true;
    }

    @Override
    public boolean isSnInValid(String sn) {
        if(TextUtils.isEmpty(sn)){
            return true;
        }else{
                if(sn.length()<10){
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean isScCodeInValid(String scCode) {
        return false;
    }
}
