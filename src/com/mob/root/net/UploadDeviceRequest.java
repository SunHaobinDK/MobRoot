package com.mob.root.net;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.mob.root.AMApplication;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class UploadDeviceRequest<T> extends AMRequest<T> {
	
	public UploadDeviceRequest(IResponseListener<T> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					CommonUtils.getDeviceInfo();
				} catch (Exception e) {
					AMLogger.e(null, "UploadDeviceRequest : " + e.getMessage());
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void v) {
				try {
					SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
					JSONObject jsonObject = new JSONObject();
					//ua
					WebView webView = new WebView(AMApplication.instance);
					String userAgent = webView.getSettings().getUserAgentString();
					sp.edit().putString(AMConstants.SP_USER_AGENT, userAgent).commit();
					
					jsonObject.put(AMConstants.SP_USER_AGENT, userAgent);
					jsonObject.put(AMConstants.SP_IMEI, sp.getString(AMConstants.SP_IMEI, null));
					jsonObject.put(AMConstants.SP_IMSI, sp.getString(AMConstants.SP_IMSI, null));
					jsonObject.put(AMConstants.SP_MAC, sp.getString(AMConstants.SP_MAC, null));
					jsonObject.put(AMConstants.SP_MODEL, sp.getString(AMConstants.SP_MODEL, null));
					jsonObject.put(AMConstants.SP_SDK_VERSION, sp.getInt(AMConstants.SP_SDK_VERSION, 0));
					jsonObject.put(AMConstants.SP_KERNEL_VERSION, sp.getString(AMConstants.SP_KERNEL_VERSION, null));
					jsonObject.put(AMConstants.SP_CLIENT_VERSION, sp.getInt(AMConstants.SP_CLIENT_VERSION, 0));
					jsonObject.put(AMConstants.SP_SCREEN_WIDTH, sp.getInt(AMConstants.SP_SCREEN_WIDTH, 0));
					jsonObject.put(AMConstants.SP_SCREEN_HEIGHT, sp.getInt(AMConstants.SP_SCREEN_HEIGHT, 0));
					jsonObject.put(AMConstants.SP_LANGUAGE, sp.getString(AMConstants.SP_LANGUAGE, null));
					jsonObject.put(AMConstants.SP_COUNTRY, sp.getString(AMConstants.SP_COUNTRY, null));
					jsonObject.put(AMConstants.SP_ANDROID_ID, sp.getString(AMConstants.SP_ANDROID_ID, null));
					jsonObject.put(AMConstants.SP_ANDROID_ADID, sp.getString(AMConstants.SP_ANDROID_ADID, null));
					jsonObject.put(AMConstants.SP_MCC, sp.getInt(AMConstants.SP_MCC, 0));
					jsonObject.put(AMConstants.SP_MNC, sp.getInt(AMConstants.SP_MNC, 0));
					doPost(AMConstants.UPLOAD_DEVICE_URI, jsonObject, false);
				} catch (Exception e) {
					AMLogger.e(null, e.getMessage());
				}
			}
		};	
		task.execute();
	}
	 
	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		super.onFailure(statusCode, headers, responseString, throwable);
		AMLogger.e(null, statusCode);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		try {
//			super.onSuccess(statusCode, headers, datas);
			JSONObject jsonObject = new JSONObject(datas);
			int status = jsonObject.getInt(AMConstants.NET_DATAS_UPLOAD_STATUS);
			AMLogger.e(null, "status : " + status);
			if(0 == status) {
				SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
				sp.edit().putInt(AMConstants.SP_DEVICE_UPLOAD, 1).commit();
				//设备信息上报完成，直接开始请求config
				ConfigRequest config = new ConfigRequest(null);
				config.start();
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
