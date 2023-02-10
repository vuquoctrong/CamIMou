package com.mm.android.mobilecommon.base.mvp;

import android.content.Intent;

/**
 * MVP mode p layer base interface
 *
 * MVP模式P层基类接口
 */
public interface IBasePresenter {
    /**
     * Processing of intent data
     *
     * 处理intent数据
     */
    void dispatchIntentData(Intent intent);

    /**
     * Anti-initialization, whether relevant resources
     *
     * 反初始化，以是否相关资源
     */
    void unInit();
}
