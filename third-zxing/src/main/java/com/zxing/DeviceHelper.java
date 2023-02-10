package com.zxing;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.zxing.utils.Strings;
import com.zxing.utils.Validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Locale;

import static android.telephony.TelephonyManager.SIM_STATE_READY;

/**
 * Purpose: To obtain relevant information of equipment
 *
 * 用途：取设备相关信息
 */
public class DeviceHelper {

    /**
     * Get the version number of the application
     *
     * 获取应用的版本号
     */
    public static String getAppVersion() {
        Context context = ContextHelper.getAppContext();
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo;
            try {
                packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                return packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return Strings.EMPTY;
    }

    public static void ClipData(String content) {
        ClipboardManager cm = (ClipboardManager) ContextHelper.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (cm != null) {
            cm.setText(content);
        }
    }

    /**
     * Setting to launch the application
     *
     * 启动应用的设置
     */
    public static void startAppSettings(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Get the versioncode
     *
     * 获取版本信息 versioncode
     */
    public static int getVersionCode() {
        final Context context = ContextHelper.getAppContext();
        int version = 1;
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = null;
            try {
                packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packInfo != null) {
                version = packInfo.versionCode;
            }
        }
        return version;
    }

    /**
     * Obtain the manufacturer of the device
     *
     * 获取设备的制造商
     */
    public static String getFactory() {
        return Build.MANUFACTURER;
    }

    /**
     * Obtain the system version number
     *
     * 获取系统版本号
     */
    public static String getPhoneOS() {
        return "Android " + getSysVersion() + " " + Build.VERSION.RELEASE;
    }

    /**
     * Whether the version is above Android6.0
     *
     * 版本是否在Android6.0 以上
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Get the Android API version
     *
     * 获取Android API版本
     */
    public static String getSysVersion() {
        return Build.VERSION.SDK_INT + Strings.EMPTY;
    }

    /**
     * Get the Android API version
     *
     * 获取Android API版本
     */
    public static int getSysVersionInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Obtain the phone model
     *
     * 获取手机型号
     */
    public static String getPhoneModel() {
        String model = Build.BRAND + " " + Build.MODEL;
        if (!TextUtils.isEmpty(model) && model.length() > 50) {
            model = model.substring(0, 49);
        }
        return Validator.replaceHanzi(model);
    }

    /**
     * Check whether the IMEI is a numeric string
     *
     * 判断IMEI是否为纯数字串
     */
    private static boolean isNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        boolean isNumber = true;
        int i;
        char c;
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (!((c >= '0') && (c <= '9')) || "000000000000000".equals(str) || "0".equals(str)) {
                isNumber = false;
                break;
            }
        }
        return isNumber;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * Check whether the mac address is valid
     *
     * 判断mac地址是否合法
     */
    private static boolean isCorrectMacAddress(String address) {
        boolean flag = false;
        if (!TextUtils.isEmpty(address) && address.length() == 17) {
            address = address.replaceAll(":", Strings.EMPTY);
            flag = isHex(address);
        }
        return flag;
    }

    /**
     * Check whether the string is a pure hexadecimal number
     *
     * 判断是否为纯16进制数字串
     */
    private static boolean isHex(String str) {
        boolean isHexFlg = true;
        int i;
        char c;
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (!(((c >= '0') && (c <= '9')) ||
                    ((c >= 'A') && (c <= 'F')) ||
                    (c >= 'a') && (c <= 'f'))) {
                isHexFlg = false;
                break;
            }
        }
        return isHexFlg;
    }

    /**
     * Determine if there is a camera application in the system that can be launched
     * @return Returns true if present and false if not
     *
     * 判断系统中是否存在可以启动的相机应用
     * @return 存在返回true，不存在返回false
     */
    public static boolean hasCamera(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    /**
     * Check whether the system is MIUI
     *
     * 检测系统是否为MIUI
     */
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    /**
     * Access to
     *
     * 获取渠道
     */
    public static String getChannel() {
        return "";
    }

    /**
     * Get the width and height of the phone
     *
     * 获取手机宽高
     */
    public static String getPhonePixels(Activity activity) {
        if (activity != null) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int widthPixels = dm.widthPixels;
            int heightPixels = dm.heightPixels;
            return widthPixels + "-" + heightPixels;
        }
        return "0-0";
    }

    /**
     * The width of the screen
     *
     * 屏幕宽度
     */
    public static int getDeviceWidth(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Point p = new Point();
                wm.getDefaultDisplay().getSize(p);
                return p.x;
            }
        }
        return 0;
    }

    /**
     * The width of the screen
     *
     * 屏幕宽度
     */
    public static int getDeviceWidth(Activity activity) {
        if (activity != null) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels;
        }
        return 0;
    }

    /**
     * The height of the screen
     *
     * 屏幕高度
     */
    public static int getDeviceHeight(Activity activity) {
        if (activity != null) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm.heightPixels;
        }
        return 0;
    }


    /**
     * Check whether there is a network connection
     *
     * 判断当前有没有网络连接
     */
    public static boolean getNetworkState() {
        Context context = ContextHelper.getAppContext();
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            return !(networkinfo == null || !networkinfo.isAvailable());
        }
        return false;
    }

    /**
     * Whether the SD card is mounted
     *
     * SD卡是否挂载
     */
    public static boolean mountedSdCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Check whether the application is installed
     *
     * 检测应用是否安装
     **/
    public static boolean isApkInstalled(String packageName) {
        Context context = ContextHelper.getAppContext();
        if (context != null) {
            final PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
            for (int i = 0; i < pinfo.size(); i++) {
                if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * call up
     *
     * 打电话
     */
    public static void callPhone(Activity activity, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * Invokes the system to send short messages
     *
     * 调用系统发送短信
     */
    public static void sendSMSView(Activity activity, String phone, String sms) {
        Uri smsToUri = Uri.parse("smsto:" + phone);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        sendIntent.putExtra("sms_body", sms);
        activity.startActivity(sendIntent);
    }

    private static TelephonyManager getTelManager() {
        Context context = ContextHelper.getAppContext();
        if (context == null) {
            return null;
        }
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Get the ISO country code, which is equivalent to the country code provided for the SIM card
     *
     * 获取ISO国家码，相当于提供SIM卡的国家码
     */
    public static String getSimCountryIso() {
        if (getTelManager() != null) {
            return getTelManager().getSimCountryIso();
        }

        return Strings.EMPTY;
    }

    /**
     * Obtaining the Carrier Name
     *
     * 获取运营商名称
     */
    public static String getSimOperatorName() {
        if (getTelManager() != null && SIM_STATE_READY == getTelManager().getSimState()) {
            return getTelManager().getSimOperatorName();
        }

        return Strings.EMPTY;
    }

    /**
     * Gets the system running memory size (KB)
     *
     * 获取系统运行内存大小 单位KB
     */
    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            if (TextUtils.isEmpty(str2)) {
                arrayOfString = str2.split("\\s+");
                initial_memory = Integer.valueOf(arrayOfString[1]);// 获得系统总内存，单位是KB
            }
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return initial_memory;// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * Device language coding
     *
     * 设备语言编码
     */
    public static String getLanguage() {
        String language = Strings.EMPTY;
        Resources resources = ContextHelper.getResources();
        if (resources != null) {
            Locale locale = ContextHelper.getResources().getConfiguration().locale;
            language = locale.getLanguage();
        }

        return language;
    }

    /**
     * Get total storage (excluding SD card)
     *
     * 获取机身总存储(不包含SD卡)
     */
    public static long getRomMemory() {
        long[] romInfo = new long[1];
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        //Total rom memory
        romInfo[0] = blockSize * totalBlocks;
        return romInfo[0];
    }

    /**
     * Get the maximum CPU frequency (in KHZ)
     *
     * 获取CPU最大频率（单位KHZ）
     */
    public static String getMaxCpuFreq() {
        StringBuilder result = new StringBuilder(Strings.EMPTY);
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result.append(new String(re));
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = new StringBuilder("N/A");
        }
        return result.toString().trim();
    }

    /**
     * Whether all information is fetched
     *
     * 是否取到所有信息
     */
    private static boolean isGetSuccess() {
        return !TextUtils.isEmpty(getPhoneModel()) && !TextUtils.isEmpty(getFactory())
                && !TextUtils.isEmpty(getMaxCpuFreq()) && getRomMemory() > 0 && getTotalMemory() > 0;
    }
}
