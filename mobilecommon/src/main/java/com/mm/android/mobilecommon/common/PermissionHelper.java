package com.mm.android.mobilecommon.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.base.DefaultPermissionListener;
import com.mm.android.mobilecommon.dialog.LCAlertDialog;
import com.mm.android.mobilecommon.utils.AppUtils;
import com.mm.android.mobilecommon.utils.CommonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic permissions encapsulate helper classes twice. Do not use them in onResume
 * <p>
 * 动态权限二次封装帮助类，切记不可在onResume里使用
 */
public class PermissionHelper {
	public static final int REQUEST_CODE_PERMISSION = 0X123;
	private FragmentActivity mActivity;

	public PermissionHelper(FragmentActivity activity) {
		mActivity = activity;
	}

	/**
	 * If used in a fragment, you must pass the fragment, not getActivity
	 *
	 * @param fragment 如果在fragment使用，必须传fragment，而非getActivity
	 * @param fragment
	 */
	public PermissionHelper(Fragment fragment) {
		mActivity = fragment.getActivity();
	}

	/**
	 * Whether the permission is specified
	 *
	 * @param permission Permission to name
	 * @param permission 权限名
	 * @return boolean  Boolean value
	 * <p>
	 * 是否有指定权限
	 * @return boolean  布尔值
	 */
	public boolean hasPermission(String permission) {
		if (TextUtils.isEmpty(permission)) {
			return false;
		}
		return ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Whether location permissions are always allowed, compatible with Q systems
	 *
	 * @return boolean Boolean value
	 * <p>
	 * 是否始终允许位置权限，兼容Q系统
	 * @return boolean 布尔值
	 */
	public boolean hasAlawaysLocationPermission() {
		if (CommonHelper.isAndroidQOrLater()) {
			return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
		} else {
			return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
		}
	}


	/**
	 * 申请位置权限，主要兼容了Q系统的后台位置权限功能
	 *
	 * @param listener
	 */
	public void requestLocationWithBackgroundPermission(final IPermissionListener listener) {
		if (CommonHelper.isAndroidROrLater()) {
			checkBackgroundLocationPermissionAPI30(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, listener);
		} else if (CommonHelper.isAndroidQOrLater()) {       //Q以上系统增加后台位置权限，需要兼容处理
			requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, new IPermissionListener() {
				@Override
				public void onGranted() {
					if (listener != null) {
						listener.onGranted();
					}
				}

				@Override
				public boolean onDenied() {         //当用户选择仅在使用该应用时允许，相当于后台位置权限没获取到
					if (listener == null) {
						return false;
					}
					boolean isDealed = listener.onDenied();
					if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
						if (!isDealed) {      //调用者未处理，弹后台权限通用说明
							String[] permission = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
							gotoSettingPage(permission, listener);
							return true;
						}
					}
					return isDealed;
				}

				@Override
				public void onGiveUp() {
					if (listener != null) {
						listener.onGiveUp();
					}
				}
			});
		} else {
			requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, listener);
		}
	}


	private void checkBackgroundLocationPermissionAPI30(final String[] permissions, final IPermissionListener listener) {
		if (hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
			return;
		LCAlertDialog.Builder builder = new LCAlertDialog.Builder(mActivity);
		builder.setTitle(R.string.mobile_common_permission_apply)
				.setCancelable(false)
				.setMessage(R.string.geofence_backgroud_location_permission_explain)
				.setCancelButton(R.string.common_cancel, new LCAlertDialog.OnClickListener() {
					@Override
					public void onClick(LCAlertDialog dialog, int which, boolean isChecked) {
						if (listener != null) {
							listener.onGiveUp();
						}
					}
				})
				.setConfirmButton(R.string.common_confirm,
						new LCAlertDialog.OnClickListener() {

							@Override
							public void onClick(LCAlertDialog dialog,
												int which, boolean isChecked) {
								requestPermissions(permissions, listener);
								dialog.dismiss();
							}
						});
		LCAlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show(mActivity.getSupportFragmentManager(), null);
	}

	public void requestPermissions(String[] permissions, final IPermissionListener listener) {
		//存放待授权的权限
		List<String> permissionList = new ArrayList<>();
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
				permissionList.add(permission);
			}
		}
		if (permissionList.isEmpty()) {      //集合为空，则都已授权
			if (listener != null) {
				listener.onGranted();
				return;
			}
		} else {      //不为空，去授权
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (AppUtils.getTargetSdkVersion(mActivity) >= Build.VERSION_CODES.M) {
					mActivity.requestPermissions(permissionList.toArray(new String[permissionList.size()]), Constants.PERMISSION_REQUEST_ID);
				}
			}
		}
	}

	@RequiresApi(api = 31)
	public void requestBlueToothPermission(DefaultPermissionListener permissionListener) {
		requestPermissions(new String[]{"android.permission.BLUETOOTH_SCAN", "android.permission.BLUETOOTH_CONNECT"}, new DefaultPermissionListener() {
			@Override
			public void onGranted() {
				if (permissionListener != null)
					permissionListener.onGranted();
			}

			@Override
			public boolean onDenied() {
				if (permissionListener != null) permissionListener.onDenied();
				return true;
			}
		});
	}


	public void gotoSettingPage(String[] permission, final IPermissionListener listener) {
		LCAlertDialog.Builder builder = new LCAlertDialog.Builder(mActivity);
		builder.setTitle(R.string.mobile_common_permission_apply)
				.setCancelable(false)
				.setMessage(getExplain(permission[0]))
				.setCancelButton(R.string.mobile_common_common_ignore, new LCAlertDialog.OnClickListener() {
					@Override
					public void onClick(LCAlertDialog dialog, int which, boolean isChecked) {
						if (listener != null) {
							listener.onGiveUp();
						}
					}
				})
				.setConfirmButton(R.string.go_to_setting,
						new LCAlertDialog.OnClickListener() {

							@Override
							public void onClick(LCAlertDialog dialog,
												int which, boolean isChecked) {
								Intent intent = new Intent();
								intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
								mActivity.startActivityForResult(intent, REQUEST_CODE_PERMISSION);
								dialog.dismiss();
							}
						});
		LCAlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show(mActivity.getSupportFragmentManager(), null);
	}

	/**
	 * Customize the information box
	 *
	 * @param tip Tip message
	 *            <p>
	 *            自定义提示信息框
	 * @param tip 提示信息
	 */
	public void showCustomTip(String tip, LCAlertDialog.OnClickListener listener) {
		LCAlertDialog.Builder builder = new LCAlertDialog.Builder(mActivity);
		builder.setTitle(R.string.mobile_common_permission_apply)
				.setCancelable(false)
				.setMessage(tip)
				.setCancelButton(R.string.mobile_common_common_ignore, listener)
				.setConfirmButton(R.string.go_to_setting,
						new LCAlertDialog.OnClickListener() {
							@Override
							public void onClick(LCAlertDialog dialog, int which, boolean isChecked) {
								Intent intent = new Intent();
								intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
								mActivity.startActivityForResult(intent, REQUEST_CODE_PERMISSION);
								dialog.dismiss();
							}
						});
		LCAlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show(mActivity.getSupportFragmentManager(), null);
	}

	/**
	 * Different copywriting content is prompted according to different permissions
	 *
	 * @param permission Permission to name
	 * @param permission 权限名
	 * @return String  Tip content
	 * <p>
	 * 根据不同的权限提示不同的文案内容
	 * @return String  提示内容
	 */
	public String getExplain(String permission) {
		String explain = mActivity.getString(R.string.mobile_common_lack_permission_then_exit);
		switch (permission) {
			case Manifest.permission.READ_CONTACTS:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_get_accounts);
				break;

			case Manifest.permission.WRITE_CONTACTS:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_write_accounts);
				break;

			case Manifest.permission.CALL_PHONE:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_read_phone_state);
				break;

			case Manifest.permission.CAMERA:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_camera);
				break;

			case Manifest.permission.ACCESS_FINE_LOCATION:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_access_location_usage);
				break;

			case Manifest.permission.ACCESS_COARSE_LOCATION:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_access_location_usage);
				break;

			case Manifest.permission.WRITE_EXTERNAL_STORAGE:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_external_storage);
				break;

			case Manifest.permission.RECORD_AUDIO:
				explain = mActivity.getString(R.string.mobile_common_permission_explain_record_audio);
				break;

			case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
				explain = mActivity.getString(R.string.geofence_backgroud_location_permission_explain);
				break;
		}
		return explain;
	}

	public interface IPermissionListener {
		/**
		 * Grant success
		 * <p>
		 * 授权成功
		 */
		void onGranted();

		/**
		 * Grant deny
		 *
		 * @return true: the caller handles it by himself.
		 * false: handles it by default, displays the reason for requiring permission, and directs the user to the Settings page
		 * <p>
		 * 授权失败
		 * @return true：调用者自己处理  false：默认处理，弹出需要权限的原因，引导用户跳转到设置页面
		 */
		boolean onDenied();

		/**
		 * When redirecting the user to the Settings page, the user clicks Cancel
		 * <p>
		 * 引导用户跳转到设置页时，用户点击取消
		 */
		void onGiveUp();
	}
}
