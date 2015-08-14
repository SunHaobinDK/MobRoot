package com.androidhelper.sdk.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.Header;

import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.entity.Version;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.net.parser.UpdateParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;
import com.loki.sdk.LokiService;

public class UpdateRequest extends AMRequest<String> implements Runnable {

	public UpdateRequest(IResponseListener<String> listener) {
		super(listener);
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	private Handler mHandler;
	private String mUrl;

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
			ConfigParser parser = new ConfigParser();
			String url = parser.getValue(AMApplication.instance, AMConstants.NET_VERSION_CONTROL_URL);
			if (CommonUtils.isEmptyString(url)) {
				return;
			}
			doPost(url, null, true);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
			UpdateParser parser = new UpdateParser();
			Version version = parser.parse(datas);
			if(null != version && !CommonUtils.isEmptyString(version.getUrl())) {
				PackageInfo packageInfo = AMApplication.instance.getPackageManager().getPackageInfo(AMApplication.instance.getPackageName(), 0);
				if(version.getVersionCode() > packageInfo.versionCode) {
					mUrl = version.getUrl();
//					mHandler.post(this);
					new Thread(this).start();
				}
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void run() {
		File file = downloadZip();
		LokiService lokiService = LokiService.getInstance(AMApplication.instance);
		if(null != file && file.exists() && null != lokiService) {
			lokiService.upgrade(file.getAbsolutePath());
		}
	}
	
	private File downloadZip() {
		File dir = AMApplication.instance.getFilesDir();
		File file = new File(dir.getAbsolutePath() + "/update.zip");
		if(file.exists()) {
			file.delete();
		}
		FileOutputStream fos = null;
		InputStream inputStream = null;
		try {
			file.createNewFile();
			fos = new FileOutputStream(file);
			URL url = new URL(mUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			connection.connect();
			int responseCode = connection.getResponseCode();
			if(200 != responseCode) {
				connection.disconnect();
				return null;
			}
			inputStream = connection.getInputStream();
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = inputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			connection.disconnect();
			String[] command = {"chmod", "777", file.getAbsolutePath()};
	        ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if(null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					AMLogger.e(null, e.getMessage());
				} finally {
					if(null != inputStream) {
						try {
							inputStream.close();
						} catch (IOException e) {
							AMLogger.e(null, e.getMessage());
						}
					}
				}
			}
		}
		return file;
	}
}
