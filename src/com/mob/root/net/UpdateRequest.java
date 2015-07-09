package com.mob.root.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.Header;

import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import com.loki.sdk.LokiService;
import com.mob.root.AMApplication;
import com.mob.root.entity.Version;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.net.parser.UpdateParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

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
					mHandler.post(this);
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
		File file = new File(dir + "\\update.zip");
		FileOutputStream fos = null;
		InputStream inputStream = null;
		try {
			fos = new FileOutputStream(file);
			URL url = new URL(mUrl);
			URLConnection connection = url.openConnection();
			connection.connect();
			inputStream = connection.getInputStream();
			int len = -1;
			byte[] buffer = new byte[1024];
			while (inputStream.read(buffer) != -1) {
				fos.write(buffer, 0, len);
			}
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
