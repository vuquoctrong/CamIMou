package com.lc.playback.provider

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.lc.playback.contacts.MethodConst
import com.lc.playback.ui.DeviceRecordListActivity
import com.lc.playback.ui.DeviceRecordPlayActivityNew
import com.lc.playback.ui.PlayLocalActivity
import com.mm.android.mobilecommon.route.IPlaybackProvider
import com.mm.android.mobilecommon.route.RoutePathManager

@Route(path = RoutePathManager.ActivityPath.MEDIA_PLAYBACK_PROVIDER)
class MediaPlaybackProvider : IPlaybackProvider {

    private var context: Context? = null

    override fun gotoRecordList(deviceDetail: String, recordType: Int) {
        val bundle = Bundle()
        bundle.putString(MethodConst.ParamConst.deviceDetail, deviceDetail)
        bundle.putInt(MethodConst.ParamConst.recordType, recordType)
        val intent = Intent(context, DeviceRecordListActivity::class.java)
        intent.putExtras(bundle)
        if (context !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context?.startActivity(intent)
    }

    override fun gotoPlayback(deviceDetail: String, recordInfo: String, recordType: Int) {
        val bundle = Bundle()
        bundle.putString(MethodConst.ParamConst.deviceDetail, deviceDetail)
        bundle.putString(MethodConst.ParamConst.recordData, recordInfo)
        bundle.putInt(MethodConst.ParamConst.recordType, recordType)
        val intent = Intent(context, DeviceRecordPlayActivityNew::class.java)
        intent.putExtras(bundle)
        if (context !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context?.startActivity(intent)
    }

    override fun gotoPlayLocal(filePath: String) {
        val bundle = Bundle()
        bundle.putString(MethodConst.ParamConst.filePath, filePath)
        val intent = Intent(context, PlayLocalActivity::class.java)
        intent.putExtras(bundle)
        if (context !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context?.startActivity(intent)
    }

    override fun init(context: Context?) {
        this.context = context
    }
}