package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferencesHelper {
    private final static String PRENAME = "lc_data";

    private static Context mContext			= null;
    private SharedPreferences mConfig				= null;

    private PreferencesHelper()
    {
    }

    private static class Instance{
        private static PreferencesHelper ph=new PreferencesHelper();
    }

    public static PreferencesHelper getInstance(Context context){
        mContext = context.getApplicationContext();
        return Instance.ph;
    }

    /**
     * If no Key exists, an empty string is returned
     * @param Key
     * @return String  String value
     *
     * 若不存在Key，则返回 空字符串
     * @param Key
     * @return String  字符串
     */
    public String getString(String Key){
        SharedPreferences config = getConfig();
        String result = config.getString(Key, "");
        return result;
    }

    /**
     * If no Key exists, the default value is returned
     * @param Key
     * @return String  String value
     *
     * 若不存在Key，则返回 默认值
     * @param Key
     * @return String  字符串
     */
    public String getString(String Key, String defValue) {
        SharedPreferences config = getConfig();
        String result = config.getString(Key, defValue);
        return result;
    }

    /**
     * If no Key exists, 0 is returned
     * @param Key
     *
     * 若不存在Key，则返回0
     * @param Key
     */
    public int getInt(String Key){
        SharedPreferences config = getConfig();
        int result = config.getInt(Key, 0);
        return result;
    }

    /**
     * If no Key exists, the default value is returned
     * @param Key
     *
     * 若不存在Key，则返回 默认值
     * @param Key
     */
    public int getInt(String Key, int defValue) {
        SharedPreferences config = getConfig();
        int result = config.getInt(Key, defValue);
        return result;
    }

    /**
     * If no Key exists, false is returned
     * @param Key
     *
     * 若不存在Key，则返回 false
     * @param Key
     */
    public boolean getBoolean(String Key){
        SharedPreferences config = getConfig();
        boolean result = config.getBoolean(Key, false);
        return result;
    }

    /**
     * If no Key exists, the default value is returned
     * @param Key
     *
     * 若不存在Key，则返回 默认值
     * @param Key
     */
    public boolean getBoolean(String Key, boolean defValue) {
        SharedPreferences config = getConfig();
        boolean result = config.getBoolean(Key, defValue);
        return result;
    }

    /**
     * If no Key exists, false is returned
     * @param Key
     *
     * 若不存在Key，则返回 false
     * @param Key
     */
    public float getFloat(String Key){
        SharedPreferences config = getConfig();
        float result = config.getFloat(Key, 0);
        return result;
    }

    /**
     * If no Key exists, the default value is returned
     * @param Key
     *
     * 若不存在Key，则返回 默认值
     * @param Key
     */
    public float getFloat(String Key, float defValue) {
        SharedPreferences config = getConfig();
        float result = config.getFloat(Key, defValue);
        return result;
    }

    /**
     * New message push module, professional default return true
     * @param Key
     *
     * 新消息推送模块,专业默认返回true的
     * @param Key
     */
    public boolean getMessagePushBoolean(String Key) {
        SharedPreferences config = getConfig();
        boolean result = config.getBoolean(Key, true);
        return result;
    }

    /**
     * If no Key exists, 0 is returned
     * @param Key
     *
     * 若不存在Key，则返回 0
     * @param Key
     */
    public long getLong(String Key){
        SharedPreferences config = getConfig();
        long result = config.getLong(Key, 0);
        return result;
    }

    public long getLong(String Key, long defValue){
        SharedPreferences config = getConfig();
        long result = config.getLong(Key, defValue);
        return result;
    }

    public void set(String key, String value){
        SharedPreferences config = getConfig();
        SharedPreferences.Editor editor = config.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void set(String key, int value){
        SharedPreferences config = getConfig();
        SharedPreferences.Editor editor = config.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void set(String key, boolean value){
        SharedPreferences config = getConfig();
        SharedPreferences.Editor editor = config.edit();
        editor.putBoolean(key, value);
        editor.commit();
        editor.apply();
    }

    public void set(String key, long value){
        SharedPreferences config = getConfig();
        SharedPreferences.Editor editor = config.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void set(String key, float value){
        SharedPreferences config = getConfig();
        SharedPreferences.Editor editor = config.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void set(String key, List<String> values) {
        if (values == null) {
            return;
        }

        Set<String> set = new HashSet<>(values);
        SharedPreferences config = getConfig();
        SharedPreferences.Editor editor = config.edit();
        editor.putStringSet(key, set);
        editor.commit();
    }

    public List<String> getStrings(String key) {
        List<String> list = new ArrayList<>();

        if (!contains(key)) {
            return list;
        }

        SharedPreferences config = getConfig();
        Set<String> set = config.getStringSet(key, new HashSet<String>());
        list.addAll(set);
        return list;
    }

    /**
     * Whether the key value exists
     * @param key
     *
     * 是否存在key值
     * @param key
     */
    public boolean contains(String key){
        SharedPreferences config = getConfig();
        return config.contains(key);
    }

    private SharedPreferences getConfig()
    {
        if(mConfig == null)
        {
            mConfig = mContext.getSharedPreferences(PRENAME, Context.MODE_PRIVATE);
        }
        return mConfig;
    }


    public void setData(String tempName, List<?> tempList) {
        SharedPreferences config = getConfig();
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(tempList);
            // 将字节流编码成base64的字符串
            String tempBase64 = new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));
            SharedPreferences.Editor editor = config.edit();
            editor.putString(tempName, tempBase64);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }


    public List<?> getData(String tempName, List<?> tempList) {
        SharedPreferences config = getConfig();
        String tempBase64 = config.getString(tempName, "");// 初值空
        if (TextUtils.isEmpty(tempBase64)) {
            return tempList;
        }
        // 读取字节
        byte[] base64 = Base64.decode(tempBase64,Base64.DEFAULT);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            // 再次封装
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 读取对象
            tempList = (List<?>) ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tempList;

    }

    /**
     * Object storage
     *
     * 对象存储
     */
    public void setObject(String key, Object temp) {
        SharedPreferences config = getConfig();
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(temp);
            // 将字节流编码成base64的字符串
            String tempBase64 = new String(Base64Help.encode(baos.toByteArray()));
            SharedPreferences.Editor editor = config.edit();
            editor.putString(key, tempBase64);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public Object getObject(String key, Object defaultValue) {
        SharedPreferences config = getConfig();
        String tempBase64 = config.getString(key, "");// 初值空
        if (TextUtils.isEmpty(tempBase64)) {
            return defaultValue;
        }
        // 读取字节
        byte[] base64 = Base64Help.decode(tempBase64);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            // 再次封装
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 读取对象
            defaultValue = ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return defaultValue;

    }

    // key value

    public static void clearKeyValueCache(Context context,String totalKey) {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(context);
        ArrayList<String> urlKeyList = (ArrayList<String>)
                preferencesHelper.getData(totalKey, new ArrayList<String>());
        for (String urlKey : urlKeyList) {
            preferencesHelper.set(urlKey, "");
        }
        preferencesHelper.setData(totalKey, new ArrayList<String>());
    }

    public static void clearKeyValueCacheBySnCode(Context context,String totalKey,String key) {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(context);
        ArrayList<String> urlKeyList = (ArrayList<String>)
                preferencesHelper.getData(totalKey, new ArrayList<String>());
        ArrayList<String> newList = new ArrayList<>();
        for (String urlKey : urlKeyList) {
            if(urlKey!=null && urlKey.contains(key)){
                preferencesHelper.set(urlKey, "");
            }else{
                newList.add(urlKey);
            }
        }
        preferencesHelper.setData(totalKey, newList);
    }
    public static void saveKeyValueToCache(Context context,String totalKey, String singleKey,String value) {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(context);
        preferencesHelper.set(singleKey ,value);

        ArrayList<String> urlKeyList = (ArrayList<String>) preferencesHelper.getData(totalKey, new ArrayList<>());
        if(!urlKeyList.contains(singleKey)){
            urlKeyList.add(singleKey);
        }
        preferencesHelper.setData(totalKey ,urlKeyList);
    }

    public static void removeKey(Context context,String key) {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(context);
        SharedPreferences.Editor editor = preferencesHelper.getConfig().edit();
        editor.remove(key);
        editor.commit();
    }

}