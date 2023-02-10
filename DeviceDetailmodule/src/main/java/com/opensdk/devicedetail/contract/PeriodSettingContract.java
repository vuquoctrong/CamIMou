package com.opensdk.devicedetail.contract;

import android.os.Bundle;

import com.mm.android.mobilecommon.openapi.data.TimeSlice;
import com.mm.android.deviceaddmodule.helper.InterfaceConstant;
import com.mm.android.mobilecommon.base.mvp.IBasePresenter;
import com.mm.android.mobilecommon.base.mvp.IBaseView;

import java.util.List;

/**
 * Created by zhengcong on 2018/8/21
 */

public interface PeriodSettingContract {
    interface Presenter extends IBasePresenter {
        /**
         * save period list async
         * @param timeSlices time Slices
         *
         *异步保存周期列表
         * @param timeSlices 时间周期
         */
        void savePeriodListAsync(List<TimeSlice> timeSlices);

        /**
         * edit time slice
         * @param position 索引
         *
         *编辑时间周期
         * @param position 索引
         */
        void editTimeSlice(int position);

        /**
         * add time slice
         *
         *
         *增加时间周期
         */
        String addTimeSlice();

        /**
         * delete time slice
         * @param position 索引
         *
         * 删除时间周期
         * @param position 索引
         */
        void deleteTimeSlice(int position);

        /**
         * change day
         * @param day day
         *
         * 更换星期
         * @param day 星期
         */
        void changeDay(InterfaceConstant.Period day);

        /**
         * is changed data
         *
         *
         *数据是否变化
         */
        boolean isChangedData();

        /**
         * dispatch intent data
         * @param bundle bundle
         *
         * 分发intent 数据
         * @param bundle bundle
         */
        void dispatchIntentData(Bundle bundle);

        /**
         * get time slices from server
         *
         *
         *获取时间周期
         */
        List<TimeSlice> getTimeSlices2Server();

        /**
         * period select confirm
         *
         * @param beginHour 开始小时
         * @param beginMinute 开始分钟
         * @param endHour 结束小时
         * @param endMinute 结束分钟
         *
         *显示删除对话框
         * @param beginHour 开始小时
         * @param beginMinute 开始分钟
         * @param endHour 结束小时
         * @param endMinute 结束分钟
         */
        String periodSelectConfirm(int beginHour, int beginMinute, int endHour, int endMinute);
    }

    interface View extends IBaseView {
        /**
         * view finish
         *
         *
         *关闭界面
         */
        void viewFinish();

        /**
         * refresh period list view
         *
         * @param period period
         * @param timeSlices time Slices
         *
         * 刷新周期列表
         * @param period 星期
         * @param timeSlices 时间周期
         */
        void refreshPeriodListView(InterfaceConstant.Period period, List<TimeSlice> timeSlices);

        /**
         * update week view
         * @param days days
         *
         *更新星期view
         * @param days 天数
         */
        void updateWeekView(List<InterfaceConstant.Period> days);

        /**
         * show period select dialog
         * @param beginHour 开始小时
         * @param beginMinute 开始分钟
         * @param endHour 结束小时
         * @param endMinute 结束分钟
         *
         *显示修改周期对话框
         * @param beginHour 开始小时
         * @param beginMinute 开始分钟
         * @param endHour 结束小时
         * @param endMinute 结束分钟
         */
        void showPeriodSelectDialog(int beginHour, int beginMinute, int endHour, int endMinute);

        /**
         * get period list
         *
         *
         *获取周期列表
         */
        List<TimeSlice> getPeriodList();

        /**
         * show delete dialog
         * @param position 索引
         *
         *显示删除对话框
         * @param position 索引
         */
        void showDeleteDialog(int position);

        /**
         * modify device alarm plansuccess
         *
         * 修改成功
         */
        void modifyDeviceAlarmPlanSuccess();

    }
}