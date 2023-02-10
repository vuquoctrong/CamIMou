package com.opensdk.devicedetail.presenter

import android.os.Message
import com.mm.android.mobilecommon.AppConsume.BusinessException
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable
import com.mm.android.mobilecommon.base.LCBusinessHandler
import com.mm.android.mobilecommon.businesstip.HandleMessageCode
import com.opensdk.devicedetail.R
import com.opensdk.devicedetail.contract.SdCardContract
import com.opensdk.devicedetail.entity.DevSdStoreData
import com.opensdk.devicedetail.net.DeviceInfoOpenApiManager
import com.opensdk.devicedetail.ui.SdCardFragment
import java.lang.ref.WeakReference
import java.text.DecimalFormat


class SdCardPresenter(view: SdCardFragment) : SdCardContract.Presenter {
    var mView: WeakReference<SdCardFragment>? = WeakReference(view)
    var LOOP_ONCE_TIME = 3 * 1000 //轮询间隔时间3S
    var totalSpace :Long ?=null


    override fun deviceSdCardStatus(deviceId: String?) {
        mView?.get()?.showLoadingDialog()
        val handler: LCBusinessHandler = object : LCBusinessHandler() {
            override fun handleBusiness(msg: Message) {
                mView?.get()?.dismissLoadingDialog()
                if (msg.what == HandleMessageCode.HMC_SUCCESS) {
                    val status = msg.obj.toString()
                    //normal：正常，abnormal：异常，recovering：格式化中
                    when (status) {
                        "normal" -> {
                            mView?.get()?.showSdNormalStatus()
                            deviceStoreInfo(deviceId)
                        }
                        "abnormal" -> {
                            mView?.get()?.showSdBrokenStatus()
                        }
                        "recovering" -> {
                            mView?.get()?.showSdFormatStatus()
                        }
                    }
                } else {
//                    mView?.get()?.showToastInfo(msg.obj.toString())
                }
            }
        }
        object : BusinessRunnable(handler) {
            @Throws(BusinessException::class)
            override fun doBusiness() {
                val status = DeviceInfoOpenApiManager.deviceSdcardStatus(deviceId)
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, status).sendToTarget()
            }
        }
    }


    override fun deviceStoreInfo(deviceId: String?) {
        mView?.get()?.showLoadingDialog()
        val handler: LCBusinessHandler = object : LCBusinessHandler() {
            override fun handleBusiness(msg: Message) {
                mView?.get()?.dismissLoadingDialog()
                if (msg.what == HandleMessageCode.HMC_SUCCESS) { //返回成功
                    val devStoreInfo = msg.obj as DevSdStoreData
                    if(devStoreInfo.totalBytes==null || devStoreInfo.totalBytes==null)
                        return
                    val to = devStoreInfo.totalBytes!!.toLong()
                    val us = devStoreInfo.usedBytes!!.toLong()
                    var progress = 0.toLong()
                    if(to!=0.toLong() && us<=to){
                        progress = (us * 100) / to
                    }
                    mView?.get()?.showSdStoreInfo(to, us, progress.toInt())
                    totalSpace=to
                } else {
//                    mView?.get()?.showToastInfo(msg.obj.toString())
                }
            }
        }
        object : BusinessRunnable(handler) {
            @Throws(BusinessException::class)
            override fun doBusiness() {
                val devStoreInfo = DeviceInfoOpenApiManager.getDeviceStoreInfo(deviceId)
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, devStoreInfo).sendToTarget()
            }
        }
    }


    override fun startFormat(deviceId: String?) {
        mView?.get()?.showLoadingDialog()
        val handler: LCBusinessHandler = object : LCBusinessHandler() {
            override fun handleBusiness(msg: Message) {

                mView?.get()?.dismissLoadingDialog()
                if (msg.what == HandleMessageCode.HMC_SUCCESS) { //返回成功
                    val result = msg.obj.toString()
                    if (result == "start-recover") {
                        sdCardStatusLoop(deviceId)
                    } else if (result == "no-sdcard" || result == "sdcard-error") {
                        mView?.get()?.showToastInfo(R.string.sd_formatting_fail)
                    } else if (result == "in-recover") {
                        sdCardStatusLoop(deviceId)//轮询
                    }
                } else {
//                    mView?.get()?.showToastInfo(msg.obj.toString())
                }
            }
        }
        object : BusinessRunnable(handler) {
            @Throws(BusinessException::class)
            override fun doBusiness() {
                val result = DeviceInfoOpenApiManager.recoverSDCard(deviceId)
                //result ：	String	设备响应结果，start-recover：开始初始化（正常情况下）;no-sdcard：插槽内无SD; in-recover：正在初始化（有可能别的客户端已经请求初始化）；sdcard-error：其他SD卡错误
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, result).sendToTarget()
            }
        }
    }


    private fun sdCardStatusLoop(deviceId: String?) {

        val handler: LCBusinessHandler = object : LCBusinessHandler() {
            override fun handleBusiness(msg: Message) {
                if (msg.what == HandleMessageCode.HMC_SUCCESS) { //返回成功
                    val status = msg.obj.toString()
                    if (status == "normal") {
                        mView?.get()?.showToastInfo(R.string.sd_formatting_success)
                        mView?.get()?.showSdNormalStatus()
                        if(totalSpace==null)
                            return
                        mView?.get()?.showSdStoreInfo(totalSpace!!, 0, 0)
                    } else if (status == "abnormal") {
                        mView?.get()?.showToastInfo(R.string.sd_formatting_fail)
                        mView?.get()?.showSdBrokenStatus()
                    }
                } else {
//                    mView?.get()?.showToastInfo(msg.obj.toString())
                }
            }
        }
        object : BusinessRunnable(handler) {
            @Throws(BusinessException::class)
            override fun doBusiness() {
                var status: String
                while (true) {
                    status = DeviceInfoOpenApiManager.deviceSdcardStatus(deviceId)
                    if (status != "recovering") { //如果不是“格式化中” 就结束轮询
                        break
                    }
                    try { //间隔3S查询一次
                        Thread.sleep(LOOP_ONCE_TIME.toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, status).sendToTarget()
            }
        }
    }


    override fun getSizeGB(num: Long): String {
        val df = DecimalFormat("0.00")
        return df.format((num.toFloat() / (1024 * 1024 * 1024)).toDouble())
    }

    override fun onDestroyView() {
        mView?.clear()
    }

}
