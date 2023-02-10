package com.mm.android.mobilecommon.route

import com.alibaba.android.arouter.facade.template.IProvider

interface IPlaybackProvider : IProvider {

    fun gotoRecordList(deviceDetail: String, recordType: Int)

    fun gotoPlayback(deviceDetail: String, recordInfo: String, recordType: Int)

    fun gotoPlayLocal(filePath: String)
}