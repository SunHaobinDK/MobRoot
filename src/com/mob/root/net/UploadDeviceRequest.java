package com.mob.root.net;

import org.apache.http.Header;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import com.mob.root.AMApplication;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class UploadDeviceRequest extends AMRequest {
	
	@Override
	public void start(Object... args) {
		try {
			CommonUtils.getDeviceInfo();
			SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(AMConstants.SP_IMEI, sp.getString(AMConstants.SP_IMEI, null));
			jsonObject.put(AMConstants.SP_IMSI, sp.getString(AMConstants.SP_IMSI, null));
			jsonObject.put(AMConstants.SP_MAC, sp.getString(AMConstants.SP_MAC, null));
			jsonObject.put(AMConstants.SP_MODEL, sp.getString(AMConstants.SP_MODEL, null));
			jsonObject.put(AMConstants.SP_SDK_VERSION, sp.getInt(AMConstants.SP_SDK_VERSION, 0));
			jsonObject.put(AMConstants.SP_KERNEL_VERSION, sp.getString(AMConstants.SP_KERNEL_VERSION, null));
			jsonObject.put(AMConstants.SP_SCREEN_WIDTH, sp.getInt(AMConstants.SP_SCREEN_WIDTH, 0));
			jsonObject.put(AMConstants.SP_SCREEN_HEIGHT, sp.getInt(AMConstants.SP_SCREEN_HEIGHT, 0));
			jsonObject.put(AMConstants.SP_LANGUAGE, sp.getString(AMConstants.SP_LANGUAGE, null));
			jsonObject.put(AMConstants.SP_ANDROID_ID, sp.getString(AMConstants.SP_ANDROID_ID, null));
			jsonObject.put(AMConstants.SP_MCC, sp.getInt(AMConstants.SP_MCC, 0));
			jsonObject.put(AMConstants.SP_MNC, sp.getInt(AMConstants.SP_MNC, 0));
			doPost(AMConstants.UPLOAD_DEVICE_URI, jsonObject, false);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	 
	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		AMLogger.e(null, statusCode);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		try {
			super.onSuccess(statusCode, headers, datas);
			JSONObject jsonObject = new JSONObject(resultDatas);
			String uuid = jsonObject.getString(AMConstants.SP_UUID);
			AMLogger.e(null, "uuid : " + uuid);
			SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			sp.edit().putString(AMConstants.SP_UUID, uuid).commit();
			//请求完成uuid，直接开始请求config
			ConfigRequest config = new ConfigRequest();
			config.start();
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
