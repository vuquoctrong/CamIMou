package com.mm.android.mobilecommon.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.mm.android.mobilecommon.utils.LogUtil;

import java.text.DecimalFormat;

/**
 * Gets latitude and longitude and de-coded utility classes
 * 1. Use the native provider first
 * 2.When the system is not available, the fusedLocation method is adopted
 * 3. Reverse coding of latitude and longitude is supported
 * note:need to assign necessary listener to your goal
 *
 * 获取经纬度和反编码的工具类
 * 1.首先使用原生的provider获取
 * 2.未获取到时，采用基于FusedLocation方式
 * 3.支持对经纬度进行反编码
 * 注意:需要为你的目标指派必要的Listener
 */
public class FuseLocationUtil {

    private Activity mActivity;
    private FetchAddressTask.OnGeoDecodeCompleted mGeoDecodeCompletedListener;
    private OnLocationGetCompleted mLocationGetCompletedListener;
    private Location mLocation;

    /**
     * Retrieves only the latitude and longitude callback interface
     *
     * 仅获取经纬度回调接口
     */
    public interface OnLocationGetCompleted {
        void onLocationGetCompleted(Location result);
    }

    /**
     * Determine whether the location is available
     *
     * 判断定位是否可用
     */
    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return lm != null && (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    /**
     * Determine whether the Gps is available
     *
     * 判断Gps是否可用
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * The Gps Settings screen is displayed
     *
     * 打开Gps设置界面
     */
    public static void openGpsSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * GPS coordinates are converted to angles
     *
     * GPS坐标 转换成 角度
     */
    public static String gpsToDegree(double location) {
        double degree = Math.floor(location);
        double minute_temp = (location - degree) * 60;
        double minute = Math.floor(minute_temp);
        String second = new DecimalFormat("#.##").format((minute_temp - minute) * 60);
        return (int) degree + "°" + (int) minute + "′" + second + "″";
    }

    /**
     * Not in China
     *
     * 不在中国范围内
     */
    public static boolean outOfChina(double longitude, double latitude) {
        return longitude < 72.004 || longitude > 137.8347 || latitude < 0.8293 || latitude > 55.8271;
    }

    /**
     * Compare the coordinates twice, see if they shift
     *
     * 比较两次坐标，是否偏移
     */
    public static boolean isMove(Location location, Location preLocation) {
        boolean isMove;
        if (preLocation != null) {
            double speed = location.getSpeed() * 3.6;
            double distance = location.distanceTo(preLocation);
            double compass = Math.abs(preLocation.getBearing() - location.getBearing());
            double angle;
            if (compass > 180) {
                angle = 360 - compass;
            } else {
                angle = compass;
            }
            if (speed != 0) {
                if (speed < 35 && (distance > 3 && distance < 10)) {
                    isMove = angle > 10;
                } else {
                    isMove = (speed < 40 && distance > 10 && distance < 100) ||
                            (speed < 50 && distance > 10 && distance < 100) ||
                            (speed < 60 && distance > 10 && distance < 100) ||
                            (speed < 9999 && distance > 100);
                }
            } else {
                isMove = false;
            }
        } else {
            isMove = true;
        }
        return isMove;
    }


    public Location getGpsInfoByProvider(Context context) {
        Location location = null;
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
            criteria.setAltitudeRequired(false);//不要求海拔
            criteria.setBearingRequired(false);//不要求方位
            criteria.setCostAllowed(true);//允许有花费
            criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //从可用的位置提供器中，匹配以上标准的最佳提供器
            if (locationManager != null) {
                String locationProvider = locationManager.getBestProvider(criteria, true);
                location = TextUtils.isEmpty(locationProvider) ? null : locationManager.getLastKnownLocation(locationProvider);
            }
            if (mLocationGetCompletedListener != null) {
                mLocationGetCompletedListener.onLocationGetCompleted(location);
            }
            LogUtil.debugLog("FuseLocationUtil", location == null ? "getGpsInfo by provider not worked" :
                    "getBestProvider worked! -> Longitude: " + location.getLongitude() + " & Latitude: " + location.getLatitude());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return location;
    }


}
