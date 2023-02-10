package com.mm.android.mobilecommon.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.entity.AddContact;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.List;

public class CommonHelper {
	private static final String HWMobile = "HUAWEI";

	/**
	 * To use this interface, you need to apply for the location permission first, otherwise the data returned to the gps will be empty
	 * @param context
	 * @return gpsInfo  Gps info
	 *
	 * 使用该接口需要先申请位置权限，否则返回gps的数据是空的
	 * @param context
	 * @return gpsInfo  gps信息
	 */
	public static double[] getGpsInfo(Context context) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
		criteria.setAltitudeRequired(false);//不要求海拔
		criteria.setBearingRequired(false);//不要求方位
		criteria.setCostAllowed(true);//允许有花费
		criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
		double[] gpsInfo = new double[2];
		try {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			//从可用的位置提供器中，匹配以上标准的最佳提供器
			if (locationManager != null) {
				String locationProvider = locationManager.getBestProvider(criteria, true);
				Location location = locationManager.getLastKnownLocation(locationProvider);
				if (location != null) {
					gpsInfo[0] = keepTwoDigitsOfDouble(location.getLongitude());
					gpsInfo[1] = keepTwoDigitsOfDouble(location.getLatitude());
				}
			}
			LogUtil.debugLog("CommonHelper", "Longitude: " + gpsInfo[0] + " & Latitude: " + gpsInfo[1]);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return gpsInfo;
	}

	/**
	 * Double data keeps 2 decimal places
	 * @param number Number
	 * @return double  Double data
	 *
	 * Double 数据保留2位小数点
	 * @param number 数字
	 * @return double  Double数字
	 */
	public static double keepTwoDigitsOfDouble(double number) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(2);
		return Double.valueOf(numberFormat.format(number));
	}

	/**
	 * The user profile picture is saved locally
	 *
	 * 用户头像保存到本地
	 */
	public static void savePhotoToLocal(String path, Bitmap bitmap) {
		File file = new File(path);
		if (file.exists()) {
			if (!file.delete())
				return;
		} else {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determine whether to install the application
	 * @param context
	 * @param packageName Package name
	 * @return boolean  Boolean value
	 *
	 * 判断是否安装应用
	 * @param context
	 * @param packageName 包名
	 * @return boolean  布尔值
	 */
	public static boolean isApkInstalled(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);

		if (pkgList != null) {
			for (int i = 0; i < pkgList.size(); i++) {
				PackageInfo pi = pkgList.get(i);
				if (pi.packageName.equalsIgnoreCase(packageName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Start the application
	 *
	 * 启动应用
	 */
	public static void startAppByPackageName(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		context.startActivity(intent);
	}

	public static void gotoWifiSetting(Context context) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings"));

		List result = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
		if (result.isEmpty()) {
			intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		}

		context.startActivity(intent);
	}

	public static void addContact(Context context, AddContact contact) {
		// 创建一个空的ContetnValues
		ContentValues values = new ContentValues();

		// 向RawContacts.CONTENT_URI 空值插入
		// 先获取android系统返回的rawContactId
		// 后面要基础此id插入值
		Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);
		values.clear();

		values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
		//内容类型
		values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
		//联系人名字
		values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getName());
		//向联系人URI添加联系人名字
		context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
		values.clear();


		for (AddContact.NumbersBean phone : contact.getNumbers()) {
			values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
			values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
			//联系人的电话号码
			values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getPhone());
			//电话类型
			values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
			//向联系人电话号码URI添加电话号码
			context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			values.clear();
		}
	}

	public static String getChannel(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String channel = appInfo.metaData.getString("APP_CHANNEL_VALUE");  // key为<meta-data>标签中的name
			if (!TextUtils.isEmpty(channel)) {
				return channel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Check whether it is a HuaWei channel package
	 * @return boolean  Boolean value
	 *
	 * 是否是华为渠道包
	 * @return boolean  布尔值
	 */
	public static boolean isHuaweiFlavor(Context context) {
		return "huawei".equals(getChannel(context));
	}

	/**
	 * Jump to the application market according to the channel package
	 * @param context
	 *
	 * 根据渠道包跳转到应用市场
	 * @param context
	 */
	public static void gotoPlayStore(Context context) {
		if (context == null) {
			return;
		}
		if (isHuaweiFlavor(context)) {
			gotoHuaweiPlayStore(context);
		} else {
			gotoGooglePlayStore(context);
		}
	}

	/**
	 * Jump to the Google App Store
	 * @param context
	 *
	 * 跳转谷歌应用商店
	 * @param context
	 */
	public static void gotoGooglePlayStore(Context context) {
		if (context == null) {
			return;
		}

		final String GOOGLE_PLAY = "com.android.vending";//谷歌商店，跳转别的商店改成对应的即可
		String appPkg = context.getPackageName();
		try {
			if (TextUtils.isEmpty(appPkg))
				return;
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setPackage(GOOGLE_PLAY);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			//跳转失败的处理
			Toast.makeText(context, R.string.mobile_common_no_google_play_detection, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Go to HuaWei App Store
	 * @param context
	 *
	 * 跳转华为应用商店
	 * @param context
	 */
	public static void gotoHuaweiPlayStore(Context context) {
		if (context == null) {
			return;
		}

		final String HUAWEI_PLAY = "com.huawei.appmarket";//华为商店，跳转别的商店改成对应的即可
		String appPkg = context.getPackageName();
		try {
			if (TextUtils.isEmpty(appPkg))
				return;
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setPackage(HUAWEI_PLAY);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			//跳转失败的处理
			Toast.makeText(context, R.string.mobile_common_no_huawei_play_detection, Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * Whether it is a system above P
	 * @return boolean  Boolean value
	 *
	 * 是否是P以上系统
	 * @return boolean 布尔值
	 */
	public static boolean isAndroidPOrLater() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
	}

	/**
	 * 是否是R以上系统
	 *
	 * @return
	 */
	public static boolean isAndroidROrLater() {
		return Build.VERSION.SDK_INT >= 30;
	}

	/**
	 * Whether it is a system above Q
	 * @return boolean  Boolean value
	 *
	 * 是否是Q以上系统
	 * @return boolean 布尔值
	 */
	public static boolean isAndroidQOrLater() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
	}

	/**
	 * Set the bottom underline and click Jump
	 *
	 * 设置底部下划线并可点击跳转
	 */
	public static void setProtocolClick(String str1, ClickableSpan span1, TextView textView) {
		SpannableString info = new SpannableString(str1);
		if (!TextUtils.isEmpty(str1)) {
			info.setSpan(span1, 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		textView.setText(info);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		textView.setHighlightColor(Color.TRANSPARENT);
	}

	public static void gotoBluetoothSetting(Activity activity, int requestCode) {
		if (activity == null) return;
		Intent intent = new Intent(
				Settings.ACTION_BLUETOOTH_SETTINGS);
		activity.startActivityForResult(intent, requestCode);
	}

	public static boolean needConnectToWifiByNetworkRequest(Context context) {
		return context != null && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.Q
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
	}
}
