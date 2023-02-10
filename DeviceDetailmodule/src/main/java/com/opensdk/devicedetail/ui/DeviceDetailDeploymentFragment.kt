package com.opensdk.devicedetail.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mm.android.mobilecommon.utils.DialogUtils
import com.mm.android.mobilecommon.utils.LogUtil
import com.opensdk.devicedetail.R
import com.opensdk.devicedetail.callback.IGetDeviceInfoCallBack
import com.opensdk.devicedetail.constance.MethodConst
import com.opensdk.devicedetail.dialog.CommonDialog
import com.opensdk.devicedetail.entity.DeviceAlarmStatusData
import com.opensdk.devicedetail.entity.DeviceChannelInfoData
import com.opensdk.devicedetail.entity.DeviceDetailListData
import com.opensdk.devicedetail.manager.DetailInstanceManager
import com.opensdk.devicedetail.tools.GsonUtils

class DeviceDetailDeploymentFragment : Fragment(), IGetDeviceInfoCallBack.IDeviceChannelInfoCallBack,
    View.OnClickListener, IGetDeviceInfoCallBack.IDeviceAlarmStatusCallBack {
    private var deviceListBean: DeviceDetailListData.ResponseData.DeviceListBean? = null
    private var deviceDetailActivity: DeviceDetailActivity? = null
    private var ivMoveCheck: ImageView? = null
    private var alarmStatus = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceDetailActivity = activity as DeviceDetailActivity?
        deviceDetailActivity!!.llOperate.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_detail_deployment_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        deviceDetailActivity = activity as DeviceDetailActivity?
//        deviceDetailActivity!!.tvTitle.text = resources.getString(R.string.lc_demo_device_deployment_title)
        deviceDetailActivity!!.tvTitle.text = ""
        if (arguments == null) {
            return
        }
        val deviceListStr = requireArguments().getString(MethodConst.ParamConst.deviceDetail)
        if (TextUtils.isEmpty(deviceListStr)) {
            return
        }
        deviceListBean = GsonUtils.fromJson(deviceListStr, DeviceDetailListData.ResponseData.DeviceListBean::class.java)
        initData()
    }

    private fun initView(view: View) {
        ivMoveCheck = view.findViewById(R.id.iv_move_check)
        ivMoveCheck?.setOnClickListener(this)

        // 布防时间段设置
        view.findViewById<View>(R.id.rl_deployment_setting_time).setOnClickListener(this)
    }

    private fun initData() {
        //获取动检状态/设备详情
        DialogUtils.show(activity)
        val deviceDetailService = DetailInstanceManager.newInstance().deviceDetailService
        val deviceChannelInfoData = DeviceChannelInfoData()
        deviceChannelInfoData.data.deviceId = deviceListBean!!.deviceId
        deviceChannelInfoData.data.channelId = deviceListBean!!.channelList[deviceListBean!!.checkedChannel].channelId
        deviceDetailService.bindDeviceChannelInfo(deviceChannelInfoData, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun deviceChannelInfo(result: DeviceChannelInfoData.Response) {
        if (!isAdded) {
            return
        }
        DialogUtils.dismiss()
        alarmStatus = result.data.alarmStatus
        if (result.data.alarmStatus == 1) {
            ivMoveCheck!!.setImageDrawable(resources.getDrawable(R.drawable.lc_demo_switch_on))
        } else {
            ivMoveCheck!!.setImageDrawable(resources.getDrawable(R.drawable.lc_demo_switch_off))
        }
    }

    override fun deviceAlarmStatus(result: Boolean) {
        if (!isAdded) {
            return
        }
        DialogUtils.dismiss()
        alarmStatus = if (alarmStatus == 1) 0 else 1
        if (alarmStatus == 1) {
            ivMoveCheck!!.setImageDrawable(resources.getDrawable(R.drawable.lc_demo_switch_on))
        } else {
            ivMoveCheck!!.setImageDrawable(resources.getDrawable(R.drawable.lc_demo_switch_off))
        }
    }

    override fun onError(throwable: Throwable) {
        if (!isAdded) {
            return
        }
        DialogUtils.dismiss()
        LogUtil.errorLog(TAG, "error", throwable)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.iv_move_check) {
            if (alarmStatus == 0) {
                val dialog = CommonDialog.Builder(activity).setTitle(R.string.device_manage_notice)
                        .setMessage(R.string.device_manager_default_defence_period_tip)
                        .setCheckBoxEnable(true) { dialog, which, isChecked -> changeNeverRemindStatus(isChecked) }
                        .setCancelButton(R.string.deploy_cancel, null)
                        .setConfirmButton(R.string.common_i_know) { dialog, which, isChecked -> changeAlarmStatus() }
                        .create()
                dialog.show(childFragmentManager, null)
            } else {
                val preferences = activity?.getSharedPreferences("SHARED_InitInfo", 0)
                val neverRemind = preferences?.getBoolean("NeverRemind" + deviceListBean?.deviceId, false)
                if (neverRemind == false) {
                    val dialog = CommonDialog.Builder(activity).setTitle(R.string.device_manage_notice)
                            .setMessage(R.string.device_manager_no_alarm_video_message_tip)
                            .setCheckBoxEnable(true) { dialog, which, isChecked -> changeNeverRemindStatus(isChecked) }
                            .setCancelButton(R.string.deploy_cancel, null)
                            .setConfirmButton(R.string.common_i_know) { dialog, which, isChecked -> changeAlarmStatus() }
                            .create()
                    dialog.show(childFragmentManager, null)
                } else {
                    changeAlarmStatus()
                }
            }
        } else if (id == R.id.rl_deployment_setting_time) {
            gotoDeploymentSettingTimePage()
        }
    }

    /**
     * change never remind status
     *
     * 切换记住提醒状态
     */
    private fun changeNeverRemindStatus(checked: Boolean) {
        val preferences = activity?.getSharedPreferences("SHARED_InitInfo", 0)
        preferences?.edit()?.putBoolean("NeverRemind" + deviceListBean?.deviceId, checked)?.apply()
    }

    /**
     * change alarm status
     *
     * 切换动检状态
     */
    private fun changeAlarmStatus(){
        //设置动检状态
        DialogUtils.show(activity)
        val deviceDetailService = DetailInstanceManager.newInstance().deviceDetailService
        val deviceAlarmStatusData = DeviceAlarmStatusData()
        deviceAlarmStatusData.data.deviceId = deviceListBean!!.deviceId
        deviceAlarmStatusData.data.channelId =
                deviceListBean!!.channelList[deviceListBean!!.checkedChannel].channelId
        //现在是开启状态，则关闭，反之
        deviceAlarmStatusData.data.enable = if (alarmStatus == 1) false else true
        deviceDetailService.modifyDeviceAlarmStatus(deviceAlarmStatusData, this)
    }


    /**
     * goto deployment setting time page
     *
     * 跳转布防时间设置界面
     */
    private fun gotoDeploymentSettingTimePage() {
        if (activity == null) {
            return
        }
        val i = Intent(activity, DeploymentSettingTimeActivity::class.java)
        i.putExtra(MethodConst.ParamConst.deviceDetail, requireArguments().getString(MethodConst.ParamConst.deviceDetail))
        startActivity(i)
    }

    companion object {
        private val TAG = DeviceDetailDeploymentFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(): DeviceDetailDeploymentFragment {
            return DeviceDetailDeploymentFragment()
        }
    }
}