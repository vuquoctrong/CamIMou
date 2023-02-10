package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {



    public static  void printDebug(String tag,String sep,Object...args){
        if(args == null || args.length == 0)return;
        if(sep == null){
            sep = "";
        }
        if(tag == null){
            tag = "";
        }
        StringBuffer buffer = new StringBuffer();
        int leng = args.length;
        for (int i = 0;i<leng;i++){
            String arg = ""+args[i];
            buffer.append(arg);
            if(i != leng-1){
                buffer.append(sep);
            }
        }
        debugLog(tag,buffer.toString());
    }

    //日志开关控制  true 打开日志  false 关闭日志
    public static boolean DEBUG = true;
    private static boolean mIsLogEnable = false;
    private static Context mContext;

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static void setLogEnable(boolean isLogEnable){
        mIsLogEnable = isLogEnable;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void debugLog(String tag, String content) {
        if (true) {
            try {
                tag = getFinalTag(tag);
                StackTraceElement target = getTargetStackTraceElement();
                if (target != null) {
                    d(tag, "(" + target.getFileName() + ":" + target.getLineNumber() + ")");
                }
                d(tag, content);
                if (mIsLogEnable) {
                    writeLog2File(mContext, tag, content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void errorLog(String tag, String content, Throwable throwable) {
        if (DEBUG) {
            try {
                tag = getFinalTag(tag);
                StackTraceElement target = getTargetStackTraceElement();
                if(target != null)
                    e(tag, "(" + target.getFileName() + ":" + target.getLineNumber() + ")");
                e(tag, content, throwable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void errorLog(String tag, String content) {
        if (DEBUG) {
            try {
                tag = getFinalTag(tag);
                StackTraceElement target = getTargetStackTraceElement();
                if(target != null)
                    e(tag, "(" + target.getFileName() + ":" + target.getLineNumber() + ")");
                e(tag, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void infoLog(String tag, String content) {
        if (DEBUG) {
            try {
                tag = getFinalTag(tag);
                StackTraceElement target = getTargetStackTraceElement();
                if(target != null)
                    i(tag, "(" + target.getFileName() + ":" + target.getLineNumber() + ")");
                i(tag, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void verboseLog(String tag, String content) {
        if (DEBUG) {
            try {
                tag = getFinalTag(tag);
                StackTraceElement target = getTargetStackTraceElement();
                if(target != null)
                    v(tag, "(" + target.getFileName() + ":" + target.getLineNumber() + ")");
                v(tag, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void warnLog(String tag, String content) {
        if (DEBUG) {
            try {
                tag = getFinalTag(tag);
                StackTraceElement target = getTargetStackTraceElement();
                if(target != null)
                    w(tag, "(" + target.getFileName() + ":" + target.getLineNumber() + ")");
                w(tag, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void warnLog(String tag, String content, Throwable e) {
        if (DEBUG) {
            try {
                tag = getFinalTag(tag);
                StackTraceElement target = getTargetStackTraceElement();
                if(target != null)
                    w(tag, "(" + target.getFileName() + ":" + target.getLineNumber() + ")", e);
               w(tag, content, e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    private static String sTag = "LeChange";
    private static String getFinalTag(String tag){
        if(!TextUtils.isEmpty(tag)){
            return tag;
        }
        return sTag;
    }
    private static StackTraceElement getTargetStackTraceElement(){
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTraceElements =  Thread.currentThread().getStackTrace();
        if(stackTraceElements == null){
            return null;
        }
        for(StackTraceElement stackTraceElement:stackTraceElements){
            boolean isLogMethod = stackTraceElement.getClassName().equals(LogUtil.class.getName());
            if(shouldTrace && !isLogMethod){
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }

    private static void writeLog2File(Context context, String tag, String text) {
        SimpleDateFormat logFile = TimeUtils.getDateFormatWithUS("yyyy-MM-dd");
        SimpleDateFormat logSdf = TimeUtils.getDateFormatWithUS("yyyy-MM-dd HH:mm:ss");

        Date nowTime = new Date();
        String needWriteFile =  logFile.format(nowTime)  + ".log";
        String logDesPath = context.getFilesDir() + File.separator + "Easy4ipTest"
                + File.separator + "DebugLog" + File.separator + needWriteFile;
//        String logDesPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Easy4ipTest"
//                            + File.separator + "DebugLog" + File.separator + needWriteFile;
        String needWriteMessage = logSdf.format(nowTime) + "    " + tag + "    " + text;
        createFilePath(null, logDesPath);
        File file = new File(logDesPath);

        FileWriter filerWriter = null;
        BufferedWriter bufWriter = null;
        try {
            filerWriter = new FileWriter(file, true);
            bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(filerWriter != null){
                    filerWriter.close();
                }

                if(bufWriter != null){
                    bufWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static boolean createFilePath(File file, String filePath) {
        int index = filePath.indexOf("/");
        if (index == -1) {
            return false;
        } else {
            if (index == 0) {
                filePath = filePath.substring(index + 1, filePath.length());
                index = filePath.indexOf("/");
            }

            String path = filePath.substring(0, index);
            File fPath;
            if (file == null) {
                fPath = new File(path);
            } else {
                fPath = new File(file.getPath() + "/" + path);
            }

            if (!fPath.exists() && !fPath.mkdir()) {
                return false;
            } else {
                if (index < filePath.length() - 1) {
                    String exPath = filePath.substring(index + 1, filePath.length());
                    createFilePath(fPath, exPath);
                }

                return true;
            }
        }
    }


    private static void w(String tag, String message){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.w(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.w(tag, message);
    }

    private static void w(String tag, String message, Throwable throwable){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.w(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.w(tag, message, throwable);
    }

    private static void e(String tag, String message){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.e(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.e(tag, message);
    }

    private static void e(String tag, String message, Throwable throwable){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.e(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.e(tag, message, throwable);
    }

    private static void v(String tag, String message){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.v(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.v(tag, message);
    }

    private static void v(String tag, String message, Throwable throwable){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.v(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.v(tag, message, throwable);
    }

    private static void i(String tag, String message){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.i(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.i(tag, message);
    }

    private static void i(String tag, String message, Throwable throwable){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.i(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.i(tag, message, throwable);
    }

    private static void d(String tag, String message){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.d(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.d(tag, message);
    }

    private static void d(String tag, String message, Throwable throwable){
        int max_str_length = 2001 - tag.length();
        while (message.length() > max_str_length){
            Log.d(tag, message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }

        Log.d(tag, message, throwable);
    }
}
