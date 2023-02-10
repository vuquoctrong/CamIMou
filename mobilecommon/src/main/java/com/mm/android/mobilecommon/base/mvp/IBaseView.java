package com.mm.android.mobilecommon.base.mvp;

import android.content.Context;

import com.mm.android.mobilecommon.eventbus.event.BaseEvent;

/**
 * MVP mode v layer base interface
 *
 * MVP模式V层基类接口
 **/
public interface IBaseView {
    /**
     * Get the context information
     *
     * 获取上下文信息
     */
    Context getContextInfo();

    /**
     * Whether the view layer is active or not
     *
     * View层是否处于活动状态，是否已销毁
     */
    boolean isViewActive();

    void showToastInfo(String msg);

    void showToastInfo(int msgId);

    void showToastInfo(int msgId, String errorDesc);

    /**
     * Display Progress Dialog
     *
     * 显示加载框
     */
    void showProgressDialog();

    /**
     * Hidden Progress Dialog
     *
     * 隐藏加载框
     */
    void cancelProgressDialog();

    /**
     * EventBus message notification
     *
     * eventBus 消息通知
     */
    void onMessageEvent(BaseEvent event);

    /**
     * Anti-initialization, whether relevant resources
     *
     * 反初始化，以是否相关资源
     */
    void unInit();

    /**
     * Initialize presenter
     *
     * 初始化presenter
     */
    void initPresenter();

    /**
     * Exit current page
     *
     * 退出当前页
     */
    void finish();
}
