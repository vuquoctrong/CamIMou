package com.mm.android.mobilecommon.businesstip;

public class HandleMessageCode {

	private final static int HMC_EXCEPTION_BASE= 0;		// 异常
	public final static int HMC_SUCCESS = HMC_EXCEPTION_BASE + 1;//执行成功
	public final static int HMC_EXCEPTION = HMC_EXCEPTION_BASE + 2;//执行失败
	public final static int HMC_BATCH_MIDDLE_RESULT = HMC_EXCEPTION_BASE + 3;//批量操作中间结果
}