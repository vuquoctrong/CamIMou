package com.opensdk.devicedetail.provider

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mm.android.deviceaddmodule.LCDeviceEngine
import com.mm.android.mobilecommon.route.IDeviceDetailProvider
import com.mm.android.mobilecommon.route.RoutePathManager
import com.opensdk.devicedetail.constance.MethodConst
import com.opensdk.devicedetail.ui.DeviceDetailActivity

@Route(path = RoutePathManager.ActivityPath.DEVICE_DETAIL_ACTIVITY)
class DeviceDetailProvider : IDeviceDetailProvider {
    private var context: Context? = null

    override fun gotoDeviceDetails(context: Activity, deviceDetail: String, from: String) {
        val bundle = Bundle()
        bundle.putString(MethodConst.ParamConst.deviceDetail, deviceDetail)
        bundle.putString(MethodConst.ParamConst.fromList, from)
        val intent = Intent(context, DeviceDetailActivity::class.java)
        intent.putExtras(bundle)
        context.startActivityForResult(intent, 0)
    }

    override fun initP2P(application: Application) {
        try {
            LCDeviceEngine.newInstance().initP2P(application)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun init(context: Context?) {
        this.context = context
    }
}