package com.opensdk.devicedetail.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.mm.android.deviceaddmodule.device_wifi.DeviceConstant
import com.mm.android.deviceaddmodule.device_wifi.DeviceConstant.IntentKey.LCDEVICE_INFO
import com.mm.android.mobilecommon.openapi.data.TimeSlice
import com.mm.android.deviceaddmodule.helper.InterfaceConstant
import com.mm.android.mobilecommon.base.mvp.BaseMvpActivity
import com.mm.android.mobilecommon.widget.CommonTitle
import com.opensdk.devicedetail.R
import com.opensdk.devicedetail.adapter.PeriodListAdapter
import com.opensdk.devicedetail.constance.MethodConst
import com.opensdk.devicedetail.contract.DeploymentSettingTimeContract
import com.opensdk.devicedetail.entity.DeviceDetailListData
import com.opensdk.devicedetail.presenter.DeploymentSettingTimePresenter
import com.opensdk.devicedetail.tools.GsonUtils

class DeploymentSettingTimeActivity<T : DeploymentSettingTimeContract.Presenter> : BaseMvpActivity<T>(),
    DeploymentSettingTimeContract.View, CommonTitle.OnTitleClickListener {

    private lateinit var mAdapter: PeriodListAdapter
    private lateinit var mListView: ListView
    private var deviceListBean: DeviceDetailListData.ResponseData.DeviceListBean? = null

    /**
     * init layout
     *
     * 初始化布局
     */
    override fun initLayout() {
        setContentView(R.layout.activity_deployment_setting_time)
    }

    /**
     * init view
     *
     * 初始化视图
     */
    override fun initView() {
        mListView = findViewById<ListView>(R.id.period_list)
        mAdapter = PeriodListAdapter(this)
        mListView.adapter = mAdapter
        mListView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> gotoPeriodSettingPage(position) }

        val title = findViewById<View>(R.id.common_title) as CommonTitle
        title.setIconRight(R.drawable.icon_set)
        title.setVisibleBottom(View.GONE)
        title.setOnTitleClickListener(this)
    }

    /**
     * init data
     *
     * 初始化数据
     */
    override fun initData() {
        mPresenter = DeploymentSettingTimePresenter(this) as T
        val deviceListStr = intent.getStringExtra(MethodConst.ParamConst.deviceDetail)
        if (TextUtils.isEmpty(deviceListStr)) {
            return
        }
        deviceListBean = GsonUtils.fromJson(
            deviceListStr,
            DeviceDetailListData.ResponseData.DeviceListBean::class.java
        )
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ deviceListBean?.let { mPresenter.getPeriodListAsync(it) } }, 100)
    }

    /**
     * refresh listview
     * @param periodMap  periodMap
     *
     * refresh 列表
     * @param periodMap  数据源
     */
    override fun refreshListView(periodMap: MutableMap<InterfaceConstant.Period, MutableList<TimeSlice>>?) {
        mAdapter.setData(periodMap)
        mAdapter.notifyDataSetChanged()
    }

    /**
     * common title click
     * @param id  id
     *
     * 标题栏点击事件
     * @param id  id
     */
    override fun onCommonTitleClick(id: Int) {
        when (id) {
            CommonTitle.ID_LEFT -> {
                onBackPressed()
            }
            CommonTitle.ID_RIGHT -> {
                gotoPeriodSettingPage(0)
            }
        }
    }

    /**
     * goto period setting page
     * @param position     position
     *
     * 跳转设置布防时间界面
     * @param position     索引
     */
    override fun gotoPeriodSettingPage(position: Int) {
        mPresenter.timeSlices?.also {
            val bundle = Bundle()
            val intent = Intent()
            bundle.putSerializable(DeviceConstant.IntentKey.TIME_SLICES_LIST, mPresenter.timeSlices)
            bundle.putInt(DeviceConstant.IntentKey.PERIOD_POS, position)
            bundle.putSerializable(LCDEVICE_INFO, deviceListBean)

            intent.setClass(this, PeriodSettingActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
}