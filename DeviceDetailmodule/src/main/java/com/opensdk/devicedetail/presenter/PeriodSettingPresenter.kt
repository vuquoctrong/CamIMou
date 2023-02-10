package com.opensdk.devicedetail.presenter

import android.os.Bundle
import com.mm.android.deviceaddmodule.device_wifi.BasePresenter
import com.mm.android.deviceaddmodule.device_wifi.DeviceConstant
import com.mm.android.deviceaddmodule.device_wifi.DeviceConstant.IntentKey
import com.mm.android.deviceaddmodule.device_wifi.Utils4DeviceManager
import com.mm.android.mobilecommon.openapi.data.TimeSlice
import com.mm.android.deviceaddmodule.helper.InterfaceConstant
import com.opensdk.devicedetail.R
import com.opensdk.devicedetail.callback.IGetDeviceInfoCallBack
import com.opensdk.devicedetail.contract.PeriodSettingContract
import com.opensdk.devicedetail.entity.DeviceDetailListData
import com.opensdk.devicedetail.manager.DetailInstanceManager
import java.util.*

/**
 * Created by zhengcong on 2018/9/19
 */
open class PeriodSettingPresenter<T : PeriodSettingContract.View?>(view: T) : BasePresenter<T>(view),
    PeriodSettingContract.Presenter {
    private var mTimeSliceMap: HashMap<InterfaceConstant.Period?, MutableList<TimeSlice>>? = null
    private var mCurDay: InterfaceConstant.Period? = null
    private var mIsDataChanged = false
    private var mCurEditPosition = -1 //当前编辑的位置
    private var mSelectPos = 0 //上个页面返回的选择位置
    private val mPeriodList = listOf(
        InterfaceConstant.Period.Sunday,
        InterfaceConstant.Period.Monday,
        InterfaceConstant.Period.Tuesday,
        InterfaceConstant.Period.Wednesday,
        InterfaceConstant.Period.Thursday,
        InterfaceConstant.Period.Friday,
        InterfaceConstant.Period.Saturday
    )
    private var deviceBean: DeviceDetailListData.ResponseData.DeviceListBean? = null

    /**
     * save period list async
     *
     * @param timeSlices time Slices
     *
     *异步保存周期列表
     * @param timeSlices 时间周期
     */
    override fun savePeriodListAsync(timeSlices: List<TimeSlice>) {
        if (timeSlices == null) {
            mView.get()?.showToastInfo(R.string.device_manager_save_failed)
            return
        }
        for (temp in timeSlices) {
            temp.beginTime = temp.beginTime.substring(1, 3) + ":" +
                    temp.beginTime.substring(3, 5) + ":" +
                    temp.beginTime.substring(5, 7)
            temp.endTime =
                temp.endTime.substring(1, 3) + ":" + temp.endTime.substring(3, 5) + ":" + temp.endTime.substring(5, 7)
        }
        mView.get()?.showProgressDialog()
        DetailInstanceManager.newInstance().deviceDetailService.modifyDeviceAlarmPlan(
            deviceBean!!.deviceId,
            "0",
            timeSlices,
            object : IGetDeviceInfoCallBack.ICommon<Boolean?> {
                override fun onCommonBack(response: Boolean?) {
                    mView.get()?.cancelProgressDialog()
                    mView.get()?.modifyDeviceAlarmPlanSuccess()
                }

                override fun onError(throwable: Throwable) {
                    mView.get()?.cancelProgressDialog()
                    mView.get()?.showToastInfo(throwable.message)
                }
            })
    }

    /**
     * is changed data
     *
     *
     *数据是否变化
     */
    override fun isChangedData(): Boolean {
        return mIsDataChanged
    }

    /**
     * dispatch intent data
     * @param bundle bundle
     *
     * 分发intent 数据
     * @param bundle bundle
     */
    override fun dispatchIntentData(bundle: Bundle) {
        var timeSlices: List<TimeSlice>? = null
        if (bundle != null) {
            timeSlices = bundle.getSerializable(IntentKey.TIME_SLICES_LIST) as List<TimeSlice>?
            mSelectPos = bundle.getInt(IntentKey.PERIOD_POS)
            deviceBean =
                bundle.getSerializable(IntentKey.LCDEVICE_INFO) as DeviceDetailListData.ResponseData.DeviceListBean?
        }
        if (timeSlices == null) {
            timeSlices = ArrayList()
        }
        mTimeSliceMap = HashMap()
        mTimeSliceMap!![InterfaceConstant.Period.Monday] = ArrayList()
        mTimeSliceMap!![InterfaceConstant.Period.Tuesday] = ArrayList()
        mTimeSliceMap!![InterfaceConstant.Period.Wednesday] = ArrayList()
        mTimeSliceMap!![InterfaceConstant.Period.Thursday] = ArrayList()
        mTimeSliceMap!![InterfaceConstant.Period.Friday] = ArrayList()
        mTimeSliceMap!![InterfaceConstant.Period.Saturday] = ArrayList()
        mTimeSliceMap!![InterfaceConstant.Period.Sunday] = ArrayList()
        for (timeSlice in timeSlices) {
            try {
                mTimeSliceMap!![InterfaceConstant.Period.valueOf(timeSlice.period)]!!.add(timeSlice)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        val periods: MutableList<InterfaceConstant.Period> = ArrayList()
        if (!mTimeSliceMap!![InterfaceConstant.Period.Monday]!!.isEmpty()) periods.add(InterfaceConstant.Period.Monday)
        if (!mTimeSliceMap!![InterfaceConstant.Period.Tuesday]!!.isEmpty()) periods.add(InterfaceConstant.Period.Tuesday)
        if (!mTimeSliceMap!![InterfaceConstant.Period.Wednesday]!!.isEmpty()) periods.add(InterfaceConstant.Period.Wednesday)
        if (!mTimeSliceMap!![InterfaceConstant.Period.Thursday]!!.isEmpty()) periods.add(InterfaceConstant.Period.Thursday)
        if (!mTimeSliceMap!![InterfaceConstant.Period.Friday]!!.isEmpty()) periods.add(InterfaceConstant.Period.Friday)
        if (!mTimeSliceMap!![InterfaceConstant.Period.Saturday]!!.isEmpty()) periods.add(InterfaceConstant.Period.Saturday)
        if (!mTimeSliceMap!![InterfaceConstant.Period.Sunday]!!.isEmpty()) periods.add(InterfaceConstant.Period.Sunday)
        mView.get()?.updateWeekView(periods)
        mCurDay = mPeriodList[mSelectPos]
        mView.get()?.refreshPeriodListView(mCurDay, mTimeSliceMap!![mPeriodList[mSelectPos]])
    }

    /**
     * get time slices from server
     *
     *获取时间周期
     */
    override fun getTimeSlices2Server(): MutableList<TimeSlice> {
        val timeSlices: MutableList<TimeSlice> = ArrayList()
        for (period in mTimeSliceMap!!.keys) {
            timeSlices.addAll(mTimeSliceMap!![period]!!)
        }
        return timeSlices
    }

    /**
     * period select confirm
     * @param beginHour begin hour
     * @param beginMinute begin minute
     * @param endHour end hour
     * @param endMinute end minute
     *
     *显示删除对话框
     * @param beginHour 开始小时
     * @param beginMinute 开始分钟
     * @param endHour 结束小时
     * @param endMinute 结束分钟
     */
    override fun periodSelectConfirm(beginHour: Int, beginMinute: Int, endHour: Int, endMinute: Int): String {
        val startMinutes = beginHour * 60 + beginMinute
        val endMinutes = endHour * 60 + endMinute
        if (endMinutes <= startMinutes) return DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_END_LESS_THAN_START
        if (endMinutes - startMinutes <= 10) return DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_END_TOO_LITTLE
        mIsDataChanged = true
        val beginTime = Utils4DeviceManager.getServerBeginTime(beginHour, beginMinute)
        val endTime = Utils4DeviceManager.getServerEndTime(endHour, endMinute)
        val timeSlice: TimeSlice
        if (mCurEditPosition == -1) {  //添加操作
            timeSlice = TimeSlice(mCurDay!!.name, beginTime, endTime)
            timeSlice.enable = true
            mTimeSliceMap!![mCurDay]!!.add(timeSlice)
        } else {
            timeSlice = mTimeSliceMap!![mCurDay]!![mCurEditPosition]
            timeSlice.beginTime = beginTime
            timeSlice.endTime = endTime
        }
        mView.get()?.refreshPeriodListView(mCurDay, mTimeSliceMap!![mCurDay])
        return DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_OK
    }

    /**
     * edit time slice
     * @param position 索引
     *
     * 编辑时间周期
     * @param position 索引
     */
    override fun editTimeSlice(position: Int) {
        mCurEditPosition = position
        val timeSlice = mTimeSliceMap!![mCurDay]!![mCurEditPosition]
        val beginCalender = Utils4DeviceManager.resolveTime(timeSlice.beginTime)
        val endCalender = Utils4DeviceManager.resolveTime(timeSlice.endTime)
        mView.get()?.showPeriodSelectDialog(
            beginCalender[Calendar.HOUR_OF_DAY], beginCalender[Calendar.MINUTE],
            endCalender[Calendar.HOUR_OF_DAY], endCalender[Calendar.MINUTE]
        )
    }

    /**
     * add time slice
     *
     * 增加时间周期
     */
    override fun addTimeSlice(): String {
        if (mTimeSliceMap!![mCurDay]!!.size >= 6) return DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_MAX
        mCurEditPosition = -1
        mView.get()?.showPeriodSelectDialog(0, 0, 0, 0)
        return DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_OK
    }

    /**
     * delete time slice
     * @param position position
     *
     * 删除时间周期
     * @param position 索引
     */
    override fun deleteTimeSlice(position: Int) {
        mIsDataChanged = true
        mTimeSliceMap!![mCurDay]!!.removeAt(position)
        mView.get()?.refreshPeriodListView(mCurDay, mTimeSliceMap!![mCurDay])
    }

    /**
     * change day
     * @param day day
     *
     * 更换星期
     * @param day 星期
     */
    override fun changeDay(day: InterfaceConstant.Period) {
        mCurDay = day
        mView.get()?.refreshPeriodListView(mCurDay, mTimeSliceMap!![day])
    }
}