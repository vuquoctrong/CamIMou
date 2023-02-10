package com.zxing;

/**
 * Cross module parameter definition
 * Note: This class is only used to hold cross-module arguments; in-module arguments should be put in the module's constants argument
 *
 * 跨模块参数定义
 * 注意：此类仅仅用于存放跨模块的参数，模块内的参数请放在模块的constants里
 */
public interface Extras {

    interface device{
        String EXREAS_CHANNEL = "extra_channel";
        String EXTRAS_DEVICE_DISPATCH = "extra_device_dispatch";
        String EXTRAS_FROM_DISPATCH = "extra_from_dispatch";
    }

    interface enterprise {
        String cropId = "extra_crop_id";
        String appId = "extra_app_id";
        String deptId = "extra_dept_id";
        String CREATE_DEPT = "extra_create_dept";//组织架构创建部门
    }


    /**
     * from
     **/
    interface FROM {
        String FROM = "from";
        String SCAN_BACK = "scan";
        String H5_SCAN_JUMP = "scan_jump";
    }

}
