package com.mm.android.mobilecommon.utils;


import android.content.Context;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 32716 on 2017/5/19.
 */
public class FileHelper {



    public static boolean isMP4File(String path) {
        if (path.toLowerCase().endsWith(".mp4") ||
                path.toLowerCase().contains(".mp4")) {
            return true;
        }
        return false;
    }

    /**
     * Renaming a file
     * @param oldFile
     * @param newFile
     * @return boolean  Boolean value
     *
     * 重命名文件
     * @param oldFile  旧文件
     * @param newFile  新文件
     * @return boolean  布尔值
     */
    public static boolean renameFile(File oldFile, File newFile) {
        if (oldFile.exists()) {
            if (newFile.exists()) {
                newFile.delete();
            }
            return oldFile.renameTo(newFile);
        } else {
            return false;
        }
    }

    private static File getDataBaseFile(Context context, String dataBaseName) {
        String dataBasePath = context.getApplicationContext().getDatabasePath(dataBaseName).getPath();
        return new File(dataBasePath);
    }

    public static boolean updateDataBaseName(Context context, String oldName, String newName) {
        File oldDataBaseFile = getDataBaseFile(context, oldName);
        File newDataBaseFile = getDataBaseFile(context, newName);
        return renameFile(oldDataBaseFile, newDataBaseFile);
    }
}
