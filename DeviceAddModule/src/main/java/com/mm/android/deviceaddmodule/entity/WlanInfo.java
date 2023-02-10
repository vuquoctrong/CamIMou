package com.mm.android.deviceaddmodule.entity;

import java.io.Serializable;


public class WlanInfo implements Serializable {
	//序列化ID
	private static final long serialVersionUID = 1L;

	private String wlanSSID;									      //wifi名称
	private int wlanQuality;										  //wifi信号
	private int wlanEncry;										 //加密方式
	private int wlanAuthMode;                                     //当wlanAuthMode和wlanEncrAlgr都为0时就是不需要密码
	private int wlanEncrAlgr;
	private String wlanPassword;									  //wifi密码
	private String wlanBSSID;										//wifi Mac地址

	public int getWlanQuality() {
		return wlanQuality;
	}

	public void setWlanQuality(int wlanQuality) {
		this.wlanQuality = wlanQuality;
	}

	public String getWlanSSID() {
		return wlanSSID;
	}

	public void setWlanSSID(String wlanSSID) {
		this.wlanSSID = wlanSSID;
	}

	public int getWlanEncry() {
		return wlanEncry;
	}

	public void setWlanEncry(int wlanEncry) {
		this.wlanEncry = wlanEncry;
	}

	public String getWlanPassword() {
		return wlanPassword;
	}

	public void setWlanPassword(String wlanPassword) {
		this.wlanPassword = wlanPassword;
	}

	public int getWlanAuthMode() {
		return wlanAuthMode;
	}

	public void setWlanAuthMode(int wlanAuthMode) {
		this.wlanAuthMode = wlanAuthMode;
	}

	public int getWlanEncrAlgr() {
		return wlanEncrAlgr;
	}

	public void setWlanEncrAlgr(int wlanEncrAlgr) {
		this.wlanEncrAlgr = wlanEncrAlgr;
	}

	public String getWlanBSSID() {
		return wlanBSSID;
	}

	public void setWlanBSSID(String wlanBSSID) {
		this.wlanBSSID = wlanBSSID;
	}

	@Override
	public String toString() {
		return "WlanInfo{" +
				"wlanSSID='" + wlanSSID + '\'' +
				", wlanQuality=" + wlanQuality +
				", wlanEncry=" + wlanEncry +
				", wlanAuthMode=" + wlanAuthMode +
				", wlanEncrAlgr=" + wlanEncrAlgr +
				", wlanPassword='" + wlanPassword + '\'' +
				", wlanBSSID='" + wlanBSSID + '\'' +
				'}';
	}
}
