package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;


public class SDsolutionUtility {

	private static String mUsername;
	private static String md5name;
	private static String[] dirFolder = {"snapshot","video","mp4","thumb","facedetection","cache", "temp"};
	private static String FirstFolder="demo";//一级目录
	private static String ALBUM_PATH=Environment.getExternalStorageDirectory() + File.separator + FirstFolder + File.separator;
	private  static String ALBUM_OLD = Environment.getExternalStorageDirectory() + File.separator + FirstFolder + File.separator;

	private static Context mContext;

	public static void initContext(Context context) {
		mContext = context;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			if (mContext != null) {
				if (mContext.getExternalFilesDir("demo") != null) {
					ALBUM_PATH = mContext.getExternalFilesDir("demo").getAbsolutePath();
				}
			}
			LogUtil.debugLog("rrrrr","ALBUM_PATH::"+ALBUM_PATH);
		}
	}

//	public static void moveFileto(){
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//			LogUtil.debugLog("060412","fileName:::"+getSnapshotPath());
//			copyAllFile(getSnapshotOldPath(),getSnapshotPath());
//			copyAllFile(getVidoeOldPath(),getVidoePath());
//		}
//	}
//
//	public static String getSnapshotOldPath() {
//		String uuid = String.valueOf(TokenHelper.getInstance().userId);
//		return ALBUM_OLD + uuid + File.separator + dirFolder[0] + File.separator;
//	}
//
//	public static String getVidoeOldPath() {
//		String uuid = String.valueOf(TokenHelper.getInstance().userId);
//		return ALBUM_OLD + uuid + File.separator + dirFolder[1] + File.separator;
//	}
//
//	public static String getSnapshotPath()
//	{
//		String uuid= String.valueOf(TokenHelper.getInstance().userId);
//		return ALBUM_PATH+uuid+File.separator+dirFolder[0]+File.separator;
//	}
//
//	public static String getVidoePath()
//	{
//		String uuid= String.valueOf(TokenHelper.getInstance().userId);
//		return ALBUM_PATH+uuid+File.separator+dirFolder[1]+File.separator;
//	}

	public static void createDir(String username)
	{
		mUsername = username.toLowerCase();
		try {
			//由于服务器不区分大小写，先统一将所有名字转成小写，再转MD5
			md5name = MD5Helper.getMD5(username.toLowerCase());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String userPath = ALBUM_PATH + md5name;
		String newUserPath = ALBUM_PATH;
		File dirUserFile = new File(userPath);
		File newDirUserFile = new File(newUserPath);
		if (dirUserFile.exists()) {
			FileHelper.renameFile(dirUserFile, newDirUserFile);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			for (int i = 0; i < dirFolder.length; i++) {
				String path = ALBUM_PATH+File.separator +dirFolder[i]+File.separator;
				LogUtil.debugLog("rrrrr","path::"+path);
				File dirEasy4ipFile = new File(path);
				if (!dirEasy4ipFile.exists()) {
					dirEasy4ipFile.mkdirs();
				}
			}

		} else {
			boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
			if (!sdCardExist) {
				//
			} else {
				for (int i = 0; i < dirFolder.length; i++) {
					String path = ALBUM_PATH + File.separator + dirFolder[i] + File.separator;
					File dirEasy4ipFile = new File(path);
					if (!dirEasy4ipFile.exists()) {
						dirEasy4ipFile.mkdirs();
					}
				}
			}
		}
	}



	public static String getCachePath()
	{
		return ALBUM_PATH+dirFolder[5]+File.separator;
//		String uuid = String.valueOf(TokenHelper.getInstance().userId);
//		return ALBUM_PATH + uuid + File.separator + dirFolder[5] + File.separator;
	}

	public static String getTempPath(){
		return ALBUM_PATH+dirFolder[6]+File.separator;
	}

	public static void copyAllFile(String srcPath, String targetPath){
		File f = new File(srcPath);
		File[] fileList = f.listFiles();
		if (fileList==null)return;
		for(File f1 : fileList){
			if(f1.isFile()) {
				copyFile(srcPath +  f1.getName(), targetPath +  f1.getName());
			}
			//判断是否是目录
			if(f1.isDirectory()) {
				copyAllFile(f1.getPath().toString(), targetPath + f1.getName());
			}
		}
	}

	public static void copyFile(String src, String target){
		LogUtil.debugLog("060412","src::"+src+" target:::"+target);
		InputStream is = null;
		OutputStream os = null;

		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(target);
			byte[] buff = new byte[1024];
			int len = 0;
			while((len = is.read(buff, 0, buff.length)) != -1) {
				os.write(buff, 0, len);
			}
			LogUtil.debugLog("060412","文件拷贝成功");
			System.out.println("文件拷贝成功！");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if(is!=null){
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

	}
}
