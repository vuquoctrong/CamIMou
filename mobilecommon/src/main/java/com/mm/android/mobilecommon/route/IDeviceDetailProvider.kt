package com.mm.android.mobilecommon.route

import android.app.Activity
import android.app.Application
import com.alibaba.android.arouter.facade.template.IProvider

interface IDeviceDetailProvider : IProvider {

    fun gotoDeviceDetails(context: Activity, deviceDetail: String, from: String = "")

    fun initP2P(application: Application)
}