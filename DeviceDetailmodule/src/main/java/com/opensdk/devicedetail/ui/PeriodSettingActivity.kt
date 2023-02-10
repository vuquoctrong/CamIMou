package com.opensdk.devicedetail.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import com.mm.android.deviceaddmodule.adapter.CommonSwipeAdapter
import com.mm.android.deviceaddmodule.adapter.TimeListAdapter
import com.mm.android.deviceaddmodule.device_wifi.DeviceConstant
import com.mm.android.mobilecommon.openapi.data.TimeSlice
import com.mm.android.deviceaddmodule.helper.InterfaceConstant
import com.mm.android.mobilecommon.base.BaseDialogFragment
import com.mm.android.mobilecommon.base.mvp.BaseMvpActivity
import com.mm.android.mobilecommon.common.LCConfiguration
import com.mm.android.mobilecommon.dialog.LCAlertDialog
import com.mm.android.mobilecommon.utils.LogUtil
import com.mm.android.mobilecommon.widget.CommonMenu4Lc
import com.mm.android.mobilecommon.widget.CommonTitle
import com.mm.android.deviceaddmodule.views.WeekView
import com.opensdk.devicedetail.R
import com.opensdk.devicedetail.contract.PeriodSettingContract
import com.opensdk.devicedetail.dialog.BottomMenuDialog
import com.opensdk.devicedetail.dialog.CommonDialog
import com.opensdk.devicedetail.dialog.PeriodSelectDialog
import com.opensdk.devicedetail.dialog.PeriodSelectDialog.PeriodSelectListener
import com.opensdk.devicedetail.presenter.PeriodSettingPresenter
import java.util.*

/**
 * Created by zhengcong on 2018/8/21
 * 布防时间段列表界面
 */
open class PeriodSettingActivity<T : PeriodSettingContract.Presenter?> : BaseMvpActivity<T>(),
    PeriodSettingContract.View,
    CommonSwipeAdapter.OnMenuItemClickListener, PeriodSelectListener, CommonTitle.OnTitleClickListener {
    private var mAddPeriodBtn: LinearLayout? = null
    protected var mPeriodList: ListView? = null
    private var mAdapter: TimeListAdapter? = null
    private var mWeekView: WeekView? = null
    private var mScrollView: ScrollView? = null
    private var mWeekTv: TextView? = null

    /**
     * init layout
     *
     * 初始化布局
     */
    override fun initLayout() {
        setContentView(R.layout.activity_period_setting)
    }

    /**
     * init presenter
     *
     * 初始化presenter
     */
    override fun initPresenter() {
        mPresenter = PeriodSettingPresenter(this) as T
    }

    /**
     * init data
     *
     * 初始化数据
     */
    override fun initData() {
        mPresenter?.dispatchIntentData(intent.extras)
    }

    /**
     * init view
     *
     * 初始化视图
     */
    override fun initView() {
        mWeekView = findViewById<View>(R.id.week_view) as WeekView
        mWeekTv = findViewById<View>(R.id.tv_week) as TextView
        mAddPeriodBtn = findViewById<View>(R.id.add_period_btn) as LinearLayout
        mPeriodList = findViewById<View>(R.id.custom_period_list) as ListView
        findViewById<View>(R.id.period_setting_save).setOnClickListener {
            val timeSlices: List<TimeSlice>? = mPresenter?.getTimeSlices2Server()
            LogUtil.errorLog("225650", timeSlices.toString())
            mPresenter?.savePeriodListAsync(timeSlices)
        }
        var timeSlices: List<TimeSlice?>? = null
        var select_pos = 0
        if (intent != null && intent.extras != null) {
            timeSlices = intent.extras!!.getSerializable(DeviceConstant.IntentKey.TIME_SLICES_LIST) as List<TimeSlice?>?
            select_pos = intent.extras!!.getInt(DeviceConstant.IntentKey.PERIOD_POS)
            mWeekTv!!.text = getWeekText(select_pos)
        }
        if (timeSlices == null) {
            viewFinish()
            return
        }
        val title = findViewById<View>(R.id.common_title) as CommonTitle
        title.setTitleCenter(R.string.lc_demo_device_setting_time)
        title.setVisibleBottom(View.GONE)
        title.setOnTitleClickListener(this)
        mAddPeriodBtn!!.setOnClickListener {
            val result: String? = mPresenter?.addTimeSlice()
            if (DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_MAX.equals(result, ignoreCase = true)) toastWithImg(
                getString(
                    R.string.device_manager_no_more_than_six_periods
                ),0
            )
        }
        mPeriodList!!.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l -> mPresenter?.editTimeSlice(i) }
        mPeriodList!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            showDeleteDialog(i)
            true
        }
        mWeekView!!.setOnItemClickListener{ period: InterfaceConstant.Period, position: Int ->
                mPresenter?.changeDay(period);
                mWeekTv!!.text = getWeekText(position);
        }
        mAdapter = TimeListAdapter(ArrayList(), contextInfo, this)
        mPeriodList!!.adapter = mAdapter
        mScrollView = findViewById(R.id.scroll_view)
    }

    /**
     * get week text
     * @param period  period
     *
     * 获取星期的文字信息
     * @param period  周期
     */
    private fun getWeekText(period: Int): String {
        when (period) {
            0 -> return resources.getString(R.string.device_manager_sunday)
            1 -> return resources.getString(R.string.device_manager_monday)
            2 -> return resources.getString(R.string.device_manager_tuesday)
            3 -> return resources.getString(R.string.device_manager_wednesday)
            4 -> return resources.getString(R.string.device_manager_thursday)
            5 -> return resources.getString(R.string.device_manager_friday)
            6 -> return resources.getString(R.string.device_manager_saturday)
        }
        return ""
    }

    /**
     * finish view
     *
     * 关闭视图
     */
    override fun viewFinish() {
        finish()
    }

    /**
     * back pressed
     *
     * 按下返回键
     */
    override fun onBackPressed() {
        exit()
    }

    /**
     * exit
     *
     * 退出
     */
    protected fun exit() {
        val isChanged: Boolean? = mPresenter?.isChangedData()
        isChanged?.let {
            if (!isChanged) finish() else {
                val dialog = CommonDialog.Builder(this).setTitle(R.string.device_manage_notice)
                        .setMessage(R.string.quit_response_not_save_hint)
                        .setGravity2(Gravity.CENTER)
                    .setCancelButton(R.string.lc_demo_device_cancel, null)
                    .setConfirmButton(R.string.device_confirm_exit) { dialog, which, isChecked -> finish() }
                    .create()
                dialog.show(supportFragmentManager, null)
            }
        }
    }

    /**
     * refresh period list view
     * @param period  period
     * @param timeSlices  time Slices
     *
     * 刷新周期列表
     * @param period  星期
     * @param timeSlices  时间周期
     */
    override fun refreshPeriodListView(period: InterfaceConstant.Period, timeSlices: List<TimeSlice>) {
        if (timeSlices == null || timeSlices.isEmpty()) mWeekView!!.removeSelectedDay(period) else mWeekView!!.addSelectedDay(
            period
        )
        mAdapter!!.clearData()
        mAdapter!!.addData(timeSlices)
        mAdapter!!.notifyDataSetChanged()
        mWeekView!!.setSelectedPeriod(period)
        if (mScrollView!!.handler != null) {
            mScrollView!!.handler.post { mScrollView!!.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    /**
     * update week view
     * @param days  days
     *
     * 更新星期view
     * @param days  天数
     */
    override fun updateWeekView(days: List<InterfaceConstant.Period>) {
        mWeekView!!.setSelectedDays(days)
    }

    /**
     * menu item click
     * @param menuResId  menu ResId
     * @param menuId  menu id
     * @param position  position
     *
     * 菜单点击事件
     * @param menuResId  menu的ResId
     * @param menuId  menu下标，暂时都返回0，后面支持多个menu后扩展用
     * @param position  ListView中的下标
     */
    override fun onMenuItemClick(menuResId: Int, menuId: Int, position: Int) {
        onDeleteTimeSlice(position)
    }

    /**
     * show period select dialog
     * @param beginHour  开始小时
     * @param beginMinute  开始分钟
     * @param endHour  结束小时
     * @param endMinute  结束分钟
     *
     *显示修改周期对话框
     * @param beginHour  开始小时
     * @param beginMinute  开始分钟
     * @param endHour  结束小时
     * @param endMinute  结束分钟
     */
    override fun showPeriodSelectDialog(beginHour: Int, beginMinute: Int, endHour: Int, endMinute: Int) {
        val periodSelectDialog = PeriodSelectDialog()
        periodSelectDialog.setPeriodSelectListener(this)
        val bundle = Bundle()
        bundle.putInt(LCConfiguration.BEGIN_HOUR, beginHour)
        bundle.putInt(LCConfiguration.BEGIN_MINUTE, beginMinute)
        bundle.putInt(LCConfiguration.END_HOUR, endHour)
        bundle.putInt(LCConfiguration.END_MINUTE, endMinute)
        bundle.putString(LCConfiguration.COMMON_SELECT_DIALOG_TITLE, getString(R.string.device_manager_period))
        periodSelectDialog.arguments = bundle
        periodSelectDialog.show(supportFragmentManager, "PeriodSelectDialog")
    }

    /**
     * get period list
     *
     * 获取周期列表
     */
    override fun getPeriodList(): MutableList<TimeSlice>? {
        return mPresenter?.timeSlices2Server
    }

    /**
     * show delete dialog
     * @param position  索引
     *
     * 显示删除对话框
     * @param position  索引
     */
    override fun showDeleteDialog(position: Int) {
        val mList = ArrayList<CommonMenu4Lc>()
        val mResout4Lc = CommonMenu4Lc()
        mResout4Lc.stringId = R.string.common_delete
        mResout4Lc.colorId = R.color.c40
        mResout4Lc.drawId = R.drawable.device_module_greyline_btn
        mResout4Lc.setTextSize(resources.getDimensionPixelSize(R.dimen.t3))
        mList.add(mResout4Lc)
        val mResout4Lc2 = CommonMenu4Lc()
        mResout4Lc2.stringId = R.string.common_cancel
        mResout4Lc2.colorId = R.color.c12
        mResout4Lc2.drawId = R.drawable.device_module_greyline_btn
        mResout4Lc2.setTextSize(resources.getDimensionPixelSize(R.dimen.t3))
        mResout4Lc2.setMargins(0, resources.getDimensionPixelSize(R.dimen.mobile_common_dp_10), 0, 0)
        mList.add(mResout4Lc2)
        val b = BottomMenuDialog()
        b.setOnClickListener { t ->
            if (t.stringId == R.string.common_delete) {
                onDeleteTimeSlice(position)
            }
        }
        val mBundle = Bundle()
        mBundle.putParcelableArrayList(BottomMenuDialog.BUTTON_KEY_LIST, mList)
        b.arguments = mBundle
        b.show(supportFragmentManager, b.javaClass.simpleName)
    }

    /**
     * modify device alarm plansuccess
     *
     * 修改成功
     */
    override fun modifyDeviceAlarmPlanSuccess() {
        showToastInfo(R.string.deployment_time_part_modify_success)
        setResult(101)
        viewFinish()
    }

    /**
     * on period confirm
     * @param beginHour  begin hour
     * @param beginMinute  begin minute
     * @param endHour  end hour
     * @param endMinute  end minute
     * @param fragment  fragment
     *
     * 周期确认
     * @param beginHour 开始小时
     * @param beginMinute  开始分钟
     * @param endHour  结束小时
     * @param endMinute  结束分钟
     * @param fragment  对话框
     */
    override fun onPeriodConfirm(
        beginHour: Int, beginMinute: Int, endHour: Int, endMinute: Int,
        fragment: BaseDialogFragment
    ) {
        val result: String? = mPresenter?.periodSelectConfirm(beginHour, beginMinute, endHour, endMinute)
        when {
            DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_END_TOO_LITTLE.equals(
                result,
                ignoreCase = true
            ) -> toast(R.string.device_manager_time_set_tenmin_error)
            DeviceConstant.PeriodSettingValidity.TIME_VALIDITY_END_LESS_THAN_START.equals(
                result,
                ignoreCase = true
            ) -> toast(
                R.string.device_manager_end_time_less_than_start
            )
            else -> fragment.dismiss()
        }
    }

    /**
     * delete time slice
     * @param position  position
     *
     * 删除时间周期
     * @param position  索引
     */
    private fun onDeleteTimeSlice(position: Int) {
        mPresenter?.deleteTimeSlice(position)
    }

    override fun onCommonTitleClick(id: Int) {
        when (id) {
            CommonTitle.ID_LEFT -> onBackPressed()
            CommonTitle.ID_RIGHT -> {
            }
        }
    }
}