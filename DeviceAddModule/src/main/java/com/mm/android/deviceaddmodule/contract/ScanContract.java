package com.mm.android.deviceaddmodule.contract;

import com.mm.android.deviceaddmodule.base.IBasePresenter;
import com.mm.android.deviceaddmodule.base.IBaseView;
import com.mm.android.mobilecommon.AppConsume.ScanResult;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceAddInfo;

/**
 * Qr code scanning contract class, define the QR code scanning page related View layer and Presenter layer interface
 *
 * 二维码扫描契约类，定义二维码扫描页面相关View层和Presenter层的接口
 **/
public interface ScanContract {
    interface Presenter extends IBasePresenter{
        /**
         * Analyze the scanned two-dimensional code
         * @param scanStr  Scanning a String
         * @param sc  Security code
         * @return  Scan result objects
         *
         * 解析扫描到的二维码
         * @param scanStr  扫描字符串
         * @param sc  安全码
         * @return ScanResult  扫描结果类对象
         */
        ScanResult parseScanStr(String scanStr, String sc);

        /**
         * Obtain device information from a service
         * @param deviceSn  Device Serial Number
         * @param deviceCodeModel  Device code model
         * @param productId  Product Id
         * @param isScan  Whether the scanning
         *
         * 从服务获取设备信息
         * @param deviceSn  设备序列号
         * @param deviceCodeModel 设备码模型
         * @param productId  产品Id
         * @param isScan  是否扫描
         */
        void getDeviceInfo(String deviceSn, String deviceCodeModel, String productId, boolean isScan);

        /**
         * Enter the device SN manually or not
         * @return boolean Boolean Value
         *
         * 是否为手动输入设备序列号页面
         * @return boolean 布尔值
         */
        boolean isManualInputPage();
        boolean isSnInValid(String sn);
        void recycle();
        void resetCache();
        boolean isScCodeInValid(String scCode);
    }

    interface View extends IBaseView<Presenter>{
        /**
         * The device type selection page is displayed
         *
         * 跳转到设备类型选择页
         */
        void goTypeChoosePage();

        /**
         * The page for devices that do not support binding is displayed
         *
         * 跳转到不支持绑定的设备页面
         */
        void goNotSupportBindTipPage();

        /**
         * The page indicating that the device is bound to another user is displayed
         *
         * 跳转至设备被其他用户绑定提示页
         * @param defaultTip
         */
        void goOtherUserBindTipPage(String defaultTip);

        /**
         * Box added hints to maintain Leorange logic
         *
         * 盒子添加提示，维持乐橙逻辑
         */
        void showAddBoxTip();

        /**
         * Go to the Cloud Platform Connections page
         *
         * 跳转至云平台连接页
         */
        void goCloudConnectPage();

        /**
         * The device login page is displayed
         *
         * 跳转至设备登录页
         */
        void goDeviceLoginPage();

        /**
         * The security code verification page is displayed
         *
         * 跳转至安全码验证页
         */
        void goSecCodePage();

        /**
         * The device binding page is displayed
         *
         * 跳转至设备绑定页
         */
        void goDeviceBindPage();
        void goIMEIInputPage();

        /**
         * The binding success page is displayed
         *
         * 进入绑定成功页
         */
        void goBindSuccessPage();
        void gotoIotAddModule(DeviceAddInfo deviceAddInfo);
    }
}
