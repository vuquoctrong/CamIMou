package com.lc.message.api;

import android.os.Message;

import com.lc.message.api.data.AlarmMessageData;
import com.lc.message.entity.AlarmMassge;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.BusinessRunnable;
import com.mm.android.mobilecommon.base.LCBusinessHandler;
import com.mm.android.mobilecommon.businesstip.BusinessErrorTip;
import com.mm.android.mobilecommon.businesstip.HandleMessageCode;

import java.util.List;

public class DeviceMessageService {
	private volatile static DeviceMessageService deviceRecordService;

	public static DeviceMessageService newInstance() {
		if (deviceRecordService == null) {
			synchronized (DeviceMessageService.class) {
				if (deviceRecordService == null) {
					deviceRecordService = new DeviceMessageService();
				}
			}
		}
		return deviceRecordService;
	}


	/**
	 * Querying Alarm Messages
	 * @param alarmMessageData  Alarm message Data
	 * @param alarmMessageCallBack  Alarm message callback
	 *
	 * 查询告警消息
	 * @param alarmMessageData  告警消息数据
	 * @param alarmMessageCallBack  告警消息回调
	 */
	public void getAlarmMessage(final String dateString, final AlarmMessageData alarmMessageData, final IMessageCallBack.IAlarmMessageCallBack alarmMessageCallBack) {
		final LCBusinessHandler handler = new LCBusinessHandler() {
			@Override
			public void handleBusiness(Message msg) {
				if (alarmMessageCallBack == null) {
					return;
				}
				if (msg.what == HandleMessageCode.HMC_SUCCESS) {
					//成功
					alarmMessageCallBack.getAlarmMessage((AlarmMessageData.Response) msg.obj, dateString);
				} else {
					//失败
					alarmMessageCallBack.onError(BusinessErrorTip.throwError(msg), dateString);
				}
			}
		};
		new BusinessRunnable(handler) {
			@Override
			public void doBusiness() throws BusinessException {
				try {
					AlarmMessageData.Response response = MessageOpenApiManager.getAlarmMessage(alarmMessageData);
					handler.obtainMessage(HandleMessageCode.HMC_SUCCESS, response).sendToTarget();
				} catch (BusinessException e) {
					throw e;
				}
			}
		};
	}

	public void deleteAlarmMessage(String token, List<AlarmMassge> alarmMassges, String deviceId, String channelId, final IMessageCallBack.IAlarmMessageCallBack alarmMessageCallBack) {
		final LCBusinessHandler handler = new LCBusinessHandler() {
			@Override
			public void handleBusiness(Message msg) {
				if (alarmMessageCallBack == null) {
					return;
				}
				if (msg.what == HandleMessageCode.HMC_SUCCESS) {
					//成功
					alarmMessageCallBack.deleteSuccess(alarmMassges);
				} else {
					//失败
					alarmMessageCallBack.onError(BusinessErrorTip.throwError(msg), "");
				}
			}
		};
		new BusinessRunnable(handler) {
			@Override
			public void doBusiness() throws BusinessException {
				try {
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < alarmMassges.size(); i++) {
						sb.append(alarmMassges.get(i).getAlarmId());
						if (i != alarmMassges.size() - 1) {
							sb.append(",");
						}
					}
					MessageOpenApiManager.deleteAlarmMessage(token, sb.toString(), deviceId, channelId);
					handler.obtainMessage(HandleMessageCode.HMC_SUCCESS).sendToTarget();
				} catch (BusinessException e) {
					throw e;
				}
			}
		};
	}

}
