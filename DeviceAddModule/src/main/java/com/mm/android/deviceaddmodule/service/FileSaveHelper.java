package com.mm.android.deviceaddmodule.service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.mm.android.deviceaddmodule.LCDeviceEngine;
import com.mm.android.deviceaddmodule.openapi.data.DeviceLeadingInfoData;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.businesstip.BusinessErrorCode;
import com.mm.android.mobilecommon.entity.deviceadd.DeviceIntroductionInfo;
import com.mm.android.mobilecommon.utils.ContextHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Use to save the service return information to a file
 *
 * 用于将服务返回信息保存成文件
 **/
public class FileSaveHelper {
    public static String INTRODUCTION_INFO_NAME="introduction.json";          //设备引导

    public static void saveToJsonInfo(String content, String fileName){
        String directory = getCachePath();
        FileSaveHelper.writeToFile(content, directory, fileName);
    }


    public static DeviceIntroductionInfo getIntroductionInfoCache(String model, String lan) throws BusinessException {
        // 读缓存
        String directory = getCachePath();
        String contentJson = getJsonFromFile(directory, model+"_"+lan+"_"+ FileSaveHelper.INTRODUCTION_INFO_NAME);
        if (TextUtils.isEmpty(contentJson)) {
            throw new BusinessException(BusinessErrorCode.BEC_COMMON_NULL_POINT);
        }

        DeviceLeadingInfoData.Response  response = null;
        try {
            Gson gson = new Gson();
            response = gson.fromJson(contentJson,
                    DeviceLeadingInfoData.Response.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(BusinessErrorCode.BEC_COMMON_NULL_POINT);
        }

        if (response == null || response.data == null
                || response.data.introductions == null) {
            throw new BusinessException(BusinessErrorCode.BEC_COMMON_NULL_POINT);
        }

        return DeviceAddEntityChangeHelper.parse2DeviceIntroductionInfo(response.data);
    }

    private static String getCachePath() {
        String userId = TokenHelper.getInstance().userId;

        return ContextHelper.getAppContext().getFilesDir() + File.separator + userId + File.separator;
    }

    private static void writeToFile(String content, String directory, String fileName) {
        // mobile SD card path +path
        BufferedWriter os = null;
        File file = new File(directory);
        try {
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return;
                }
            }
            file = new File(file, fileName);
            os = new BufferedWriter(new FileWriter(file), 1024);
            os.write(content);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getJsonFromFile(String dirctory, String fileName) {
        // mobile SD card path +path
        BufferedReader os = null;
        StringBuffer stringBuffer = new StringBuffer();
        File file = new File(dirctory, fileName);
        try {
            if (!file.exists()) {
                return "";
            }
            os = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = os.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString().trim();
    }
}
