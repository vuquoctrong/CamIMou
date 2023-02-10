package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkHelper {
	
    /**
	 * Get the current network type
	 * @param context
	 *
	 * 获取当前网络类型
	 * @param context
	 */
	public static int checkNetwork(Context context) {
		int flag = -1;
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMan == null) {
			flag = -1;
		} else {
			NetworkInfo[] info = conMan.getAllNetworkInfo();
			if (info != null) {
				for (NetworkInfo anInfo : info) {
					if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
						flag = anInfo.getType();
						break;
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * Check whether the current network is connected
	 * @param context
	 * @return boolean  Boolean value
	 *
	 * 获取当前网络是否连接
	 * @param context
	 * @return boolean  布尔值
	 */
	public static boolean isConnected(Context context) {
		if(context == null){
			return false;
		}
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conn == null){
        	return false;
        }
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }
	

	public static boolean isWifiNetworkAvailable(Context context) {
		if(context == null)return false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			// 判断wifi
			return true;
		}
		return false;
	}






	public static int getIpV4Value(String ipOrMask) {
		byte[] addr = getIpV4Bytes(ipOrMask);
		int address = addr[3] & 0xFF;
		address |= ((addr[2] << 8) & 0xFF00);
		address |= ((addr[1] << 16) & 0xFF0000);
		address |= ((addr[0] << 24) & 0xFF000000);
		return address;
	}

	public static byte[] getIpV4Bytes(String ipOrMask) {
		try{
			String[] addrs = ipOrMask.split("\\.");
			int length = addrs.length;
			byte[] addr = new byte[length];
			for(int i = 0; i < length; i++) {
				addr[i] = (byte)(Integer.parseInt(addrs[i]) & 0xff);
			}
			return addr;
		}catch (Exception e) {

		}
		return new byte[4];
	}


	
	/**
	 * Whether the network is available
	 * @param context
	 * @return boolean Boolean value
	 *
	 * 网络是否可用
	 * @param context
	 * @return boolean 布尔值
	 */
	public static boolean isNetworkAvailable(Context context) {
		android.net.ConnectivityManager connectivity = (android.net.ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取系统网络连接管理�?
		if (connectivity == null) { // 如果网络管理器为null
			return false; // 返回false表明网络无法连接
		} else {
			android.net.NetworkInfo[] info = connectivity.getAllNetworkInfo(); // 获取�?��的网络连接对�?
			if (info != null) { // 网络信息不为null
				for (NetworkInfo anInfo : info) { // 遍历网路连接对象
					if (anInfo.isConnected()) { // 当有�?��网络连接对象连接上网络时
						return true; // 返回true表明网络连接正常
					}
				}
			}
		}
		return false;
	}
	


}
