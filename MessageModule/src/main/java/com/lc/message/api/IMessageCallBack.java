package com.lc.message.api;

import androidx.annotation.NonNull;


import com.lc.message.api.data.AlarmMessageData;
import com.lc.message.entity.AlarmMassge;

import java.util.List;

public interface IMessageCallBack {

    public interface IAlarmMessageCallBack {
        /**
         * Querying Alarm Messages
         * @param result  Alarm response data
         *
         * 查询告警信息
         * @param result  告警信息响应数据
         */
        void getAlarmMessage(@NonNull AlarmMessageData.Response result, String dateString);

        void deleteSuccess(List<AlarmMassge> alarmMassges);

        /**
         * Error correction
         * @param throwable
         *
         * 错误回调
         * @param throwable
         */
        void onError(@NonNull Throwable throwable, String dateString);
    }

}
