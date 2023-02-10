package com.opensdk.devicedetail.contract

import com.mm.android.deviceaddmodule.base.IBasePresenter
import com.mm.android.deviceaddmodule.base.IBaseView

interface SdCardContract {
    interface Presenter : IBasePresenter {
        /**
         * Obtain the SD card status
         * @param deviceId  Device Id
         *
         * 获取SD卡状态
         * @param deviceId  设备Id
         */
        fun deviceSdCardStatus(deviceId:String?)
        /**
         * Obtain device storage information
         * @param deviceId  Device Id
         *
         * 获取设备存储信息
         * @param deviceId  设备Id
         */
        fun deviceStoreInfo(deviceId:String?)
        /**
         * Start format
         * @param deviceId  Device Id
         *
         * 开始初始化
         * @param deviceId  设备Id
         */
        fun startFormat(deviceId:String?)

        /**
         * Convert "byte" to "GB" and retain two decimal places
         * @param num  Long Numbers
         *
         * 将“byte”转化为“GB”,并保留两位小数
         * @param num  Long数字
         */
        fun getSizeGB(num: Long): String
        fun onDestroyView()
    }

    interface View : IBaseView<Presenter?> {
        /**
         * The SD card normal status is displayed
         *
         * 显示SD卡正常状态
         */
        fun showSdNormalStatus()
        /**
         * The SD card abnormal status is displayed
         *
         * 显示SD卡损坏状态
         */
        fun showSdBrokenStatus()
        /**
         * The SD card formatting status is displayed
         *
         * 显示SD卡格式化状态
         */
        fun showSdFormatStatus()

        /**
         * The Sd card storage information is displayed
         * @param totalStorage  Total Storage
         * @param usedStorage  Used Storage
         * @param progressStore  Progress Store
         *
         * 显示Sd卡存储信息
         * @param totalStorage  总内存
         * @param usedStorage  已使用内存
         * @param progressStore  存储进度条
         */
        fun showSdStoreInfo(totalStorage: Long, usedStorage: Long,progressStore :Int)

        /**
         * The format popover is displayed
         *
         * 显示格式化弹窗
         */
        fun showFormatDialog()
        /**
         * The popover is loading
         *
         * 显示正在加载弹窗
         */
        fun showLoadingDialog()
        /**
         * Hide the loading popover
         *
         * 隐藏正在加载弹窗
         */
        fun dismissLoadingDialog()
    }
}
