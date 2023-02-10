package com.mm.android.mobilecommon.openapi;

import com.mm.android.mobilecommon.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {
	private static final String TAG = HttpClient.class.getSimpleName();

	/**
	 * post
	 * @param urlStr  url
	 * @param paramStr   param
	 * @param contentType  ContentType
	 * @param debugTag  Debug tag
	 * @return  result
	 *
	 * post
	 * @param urlStr  url
	 * @param paramStr   参数类型
	 * @param contentType  内容类型
	 * @param debugTag  debug标志
	 * @return  结果
	 */
	public static String post(String urlStr, String paramStr, String contentType, String debugTag, int timeOut) throws IOException {
		LogUtil.debugLog(TAG, urlStr + "..." + paramStr);
		HttpURLConnection conn = null;
		String resultData = "";
		OutputStream outputStream = null;
		InputStream inputStream = null;
		ByteArrayOutputStream baos = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			if (conn instanceof HttpsURLConnection) {
				((HttpsURLConnection) conn).setHostnameVerifier(SSLSocketClient.getHostnameVerifier());
			}

			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(true);
			conn.setConnectTimeout(timeOut);
			conn.setReadTimeout(timeOut);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", contentType);
			conn.connect();
			outputStream = conn.getOutputStream();
			outputStream.write(paramStr.getBytes());
			outputStream.flush();
			outputStream.close();
			int responseCode = conn.getResponseCode();
			if (debugTag != null)
				LogUtil.debugLog(debugTag, "response code " + responseCode);
			if (200 == responseCode) {
				inputStream = conn.getInputStream();
				byte[] buffer = new byte[1024];
				baos = new ByteArrayOutputStream();
				boolean var13 = true;
				int len;
				while ((len = inputStream.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				byte[] b = baos.toByteArray();
				resultData = new String(b, "utf-8");
				baos.flush();
				if (debugTag != null)
					LogUtil.debugLog(debugTag, "response data " + resultData);
			} else
				throw new IOException(String.format("http_get failed: status=%d", responseCode));
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			throw new IOException(e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (baos != null) {
					baos.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return resultData;
	}


}
