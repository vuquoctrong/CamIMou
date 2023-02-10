package com.opensdk.devicedetail.presenter

import android.content.Intent
import com.mm.android.deviceaddmodule.device_wifi.BasePresenter
import com.mm.android.mobilecommon.openapi.data.TimeSlice
import com.mm.android.deviceaddmodule.helper.InterfaceConstant
import com.opensdk.devicedetail.callback.IGetDeviceInfoCallBack
import com.opensdk.devicedetail.contract.DeploymentSettingTimeContract
import com.opensdk.devicedetail.entity.DeviceDetailListData
import com.opensdk.devicedetail.manager.DetailInstanceManager
import java.util.*
import kotlin.collections.ArrayList

open class DeploymentSettingTimePresenter<T : DeploymentSettingTimeContract.View>(view: T) : BasePresenter<T>(view),
    DeploymentSettingTimeContract.Presenter {

    private var mTimeSlices: ArrayList<TimeSlice>? = null

    override fun unInit() {
    }

    /**
     * get period list
     *
     * @param deviceListBean deviceBean
     *
     * 获取周期列表
     * @param deviceListBean 设备实体
     */
    override fun getPeriodListAsync(deviceListBean: DeviceDetailListData.ResponseData.DeviceListBean) {
        mView.get()?.showProgressDialog()
        DetailInstanceManager.newInstance().deviceDetailService.deviceAlarmPlan(
            deviceListBean.deviceId,
            "0",
            object : IGetDeviceInfoCallBack.IDeviceAlarmPlanCallBack<List<TimeSlice>> {
                override fun getDeviceAlarmPlan(response: List<TimeSlice>?) {
                    mView.get()?.cancelProgressDialog()
                    initTimeSlices(response)
                }

                override fun onError(throwable: Throwable?) {
                    mView.get()?.cancelProgressDialog()
                    val message = throwable!!.message
                    mView.get()?.showToastInfo(message)
                }
            })
    }

    /**
     * dispatch intent data
     * @param intent intent
     *
     * 分发 intent 数据
     * @param intent intent
     */
    override fun dispatchIntentData(intent: Intent?) {
        super.dispatchIntentData(intent)
        if (intent != null && intent.extras != null) {
        }
    }

    /**
     * get time slices
     *
     *
     * 获取时间周期
     */
    override fun getTimeSlices(): ArrayList<TimeSlice>? {
        return mTimeSlices as ArrayList<TimeSlice>?
    }

    /**
     * get period map
     * @param timeSlices time slices
     *
     * 获取周期列表
     * @param timeSlices 周期
     */
    private fun getPeriodMap(timeSlices: List<TimeSlice>?): Map<InterfaceConstant.Period, MutableList<TimeSlice>>? {
        val periodMap: HashMap<InterfaceConstant.Period, MutableList<TimeSlice>> =
            HashMap<InterfaceConstant.Period, MutableList<TimeSlice>>()
        for (i in InterfaceConstant.Period.values().indices) {
            periodMap[InterfaceConstant.Period.values().get(i)] = ArrayList()
        }
        if (timeSlices != null && !timeSlices.isEmpty()) {
            for (timeSlice in timeSlices) {
                try {
                    val timeSlics = periodMap[InterfaceConstant.Period.valueOf(timeSlice.period)]!!
                    timeSlics.add(timeSlice)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
        return periodMap
    }

    /**
     * init time slices
     * @param timeSlices time slices
     *
     * 获取周期列表
     * @param timeSlices 周期
     */
    fun initTimeSlices(timeSlices: List<TimeSlice>?) {
        if (timeSlices != null) {
            mTimeSlices = ArrayList()
            for (timeSlice in timeSlices) {
                if ("everyday".equals(timeSlice.period, ignoreCase = true)) { //兼容服务period字段可能会返回everyday的情况
                    for (period in InterfaceConstant.Period.values()) {
                        mTimeSlices!!.add(
                                TimeSlice(
                                        period.name,
                                        timeSlice.beginTime, timeSlice.endTime
                                )
                        )
                    }
                } else {
                    if ("T240000".equals(timeSlice.endTime, ignoreCase = true)) {  //兼容服务endTime字段可能会返回T240000的情况
                        timeSlice.endTime = "T235959"
                    }
                    timeSlice.beginTime = "T" + timeSlice.beginTime.replace(":", "")
                    timeSlice.endTime = "T" + timeSlice.endTime.replace(":", "")
                    mTimeSlices!!.add(timeSlice)
                }
            }
            mView.get()?.refreshListView(getPeriodMap(mTimeSlices))
        }
    }
}