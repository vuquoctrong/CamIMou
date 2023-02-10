package com.mm.android.mobilecommon.route

import com.alibaba.android.arouter.facade.template.IProvider

interface IPreviewProvider : IProvider {

    fun gotoPrevew(deviceDetail: String)

    fun addDevices(params: String)
}