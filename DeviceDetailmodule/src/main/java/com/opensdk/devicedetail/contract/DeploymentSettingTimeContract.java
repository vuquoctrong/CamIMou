package com.opensdk.devicedetail.contract;

import com.mm.android.mobilecommon.openapi.data.TimeSlice;
import com.mm.android.deviceaddmodule.helper.InterfaceConstant;
import com.mm.android.mobilecommon.base.mvp.IBaseView;
import com.mm.android.mobilecommon.base.mvp.IBasePresenter;
import com.opensdk.devicedetail.entity.DeviceDetailListData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DeploymentSettingTimeContract {
    interface Presenter extends IBasePresenter {
        /**
         * get period list
         * @param deviceBean deviceBean
         *
         * 获取周期列表
         * @param deviceBean 设备实体
         */
        void getPeriodListAsync(DeviceDetailListData.ResponseData.DeviceListBean deviceBean);
        /**
         * get time slices
         *
         *
         * 获取时间周期
         */
        ArrayList<TimeSlice> getTimeSlices();
    }

    interface View extends IBaseView {
        /**
         * refresh list view
         * @param periodMap deviceBean
         *
         * 刷新时间列表
         * @param periodMap 周期
         */
        void refreshListView(Map<InterfaceConstant.Period, List<TimeSlice>> periodMap);

        /**
         * goto period setting page
         * @param position position
         *
         * 跳转周期设置界面
         * @param position 索引
         */
        void gotoPeriodSettingPage(int position);
    }
}
