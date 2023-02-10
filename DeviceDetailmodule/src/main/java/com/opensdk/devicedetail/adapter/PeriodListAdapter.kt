package com.opensdk.devicedetail.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mm.android.deviceaddmodule.device_wifi.Utils4DeviceManager
import com.mm.android.mobilecommon.entity.ClipRect
import com.mm.android.mobilecommon.openapi.data.TimeSlice
import com.mm.android.deviceaddmodule.helper.InterfaceConstant
import com.mm.android.mobilecommon.widget.TimeBlockBar
import com.opensdk.devicedetail.R
import java.util.*

class PeriodListAdapter(context: Activity?) : BaseAdapter() {

    private var mContext: Activity? = null
    private var mPeriodMap: HashMap<InterfaceConstant.Period, List<TimeSlice>?>? = null
    private var mWeekdayList: MutableList<InterfaceConstant.Period>? = null

    init {
        mContext = context
        mPeriodMap = HashMap<InterfaceConstant.Period, List<TimeSlice>?>()
        mWeekdayList = ArrayList<InterfaceConstant.Period>()
        for (i in InterfaceConstant.Period.values().indices) {
            mWeekdayList!!.add(InterfaceConstant.Period.values()[i])
            mPeriodMap!![InterfaceConstant.Period.values()[i]] = null
        }
    }

    fun setData(periodMap: Map<InterfaceConstant.Period, List<TimeSlice>?>?) {
        mPeriodMap!!.clear()
        mPeriodMap!!.putAll(periodMap!!)
    }

    override fun getCount(): Int {
        return mPeriodMap!!.size
    }

    override fun getItem(position: Int): Any? {
        return mPeriodMap!![mWeekdayList!![position]]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val holder: PeriodListItemHolder
        if (convertView == null) {
            holder = PeriodListItemHolder()
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_period_list, null)
            holder.weekdayItem = convertView!!.findViewById<View>(R.id.week_day) as TextView?
            holder.timeBlockBar = convertView.findViewById<View>(R.id.time_block_bar) as TimeBlockBar?
            convertView.setTag(holder)
        } else {
            holder = convertView.tag as PeriodListItemHolder
        }
        val period: String
        val i: InterfaceConstant.Period = mWeekdayList!![position]
        period = if (i === InterfaceConstant.Period.Monday) {
            mContext!!.resources.getString(R.string.device_manager_monday)
        } else if (i === InterfaceConstant.Period.Tuesday) {
            mContext!!.resources.getString(R.string.device_manager_tuesday)
        } else if (i === InterfaceConstant.Period.Wednesday) {
            mContext!!.resources.getString(R.string.device_manager_wednesday)
        } else if (i === InterfaceConstant.Period.Thursday) {
            mContext!!.resources.getString(R.string.device_manager_thursday)
        } else if (i === InterfaceConstant.Period.Friday) {
            mContext!!.resources.getString(R.string.device_manager_friday)
        } else if (i === InterfaceConstant.Period.Saturday) {
            mContext!!.resources.getString(R.string.device_manager_saturday)
        } else if (i === InterfaceConstant.Period.Sunday) {
            mContext!!.resources.getString(R.string.device_manager_sunday)
        } else {
            mContext!!.resources.getString(R.string.device_manager_sunday)
        }
        holder.weekdayItem!!.text = period
        val slices: List<TimeSlice>? = mPeriodMap!![mWeekdayList!![position]]
        if (slices != null) {
            val clips: ArrayList<ClipRect> = getTimeSlices(slices)
            holder.timeBlockBar?.setClipRects(clips)
            holder.timeBlockBar?.setScale(1.0f)
            holder.timeBlockBar?.setProgress(0.0f)
        }
        return convertView
    }

    private class PeriodListItemHolder {
        var weekdayItem: TextView? = null
        var timeBlockBar: TimeBlockBar? = null
    }

    private fun getTimeSlices(slices: List<TimeSlice>): ArrayList<ClipRect> {
        val clips: ArrayList<ClipRect> = ArrayList<ClipRect>()
        for (slice in slices) {
            var sliceBeginTime: String = slice.getBeginTime()
            var sliceEndTime: String = slice.getEndTime()
            if (sliceBeginTime.contains("T") && sliceEndTime.contains("T")) {
                sliceBeginTime = sliceBeginTime.replace("T", "").substring(0, 4)
                sliceEndTime = sliceEndTime.replace("T", "").substring(0, 4)
            }
            val beginTime: Long = Utils4DeviceManager.convertTimeInfo(sliceBeginTime)
            val endTime: Long = Utils4DeviceManager.convertTimeInfo(sliceEndTime)
            clips.add(ClipRect(beginTime, endTime))
        }
        return clips
    }
}