package com.androidhelper.sdk.net;

import org.apache.http.Header;
import org.json.JSONObject;

import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;

public class CheckApkRequest extends AMRequest<String> {

	public CheckApkRequest(IResponseListener<String> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		if(null != args && args.length > 0) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("package_name", args[0].toString());
//				doPost(AMConstants.APK_CHECK_URI, jsonObject, true);
			} catch (Exception e) {
				AMLogger.e(null, e.getMessage());
			}
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
			JSONObject jsonObject = new JSONObject(datas);
			String apkUrl = jsonObject.getString("apk_url");
			listener.onResponse(apkUrl);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		super.onFailure(statusCode, headers, responseString, throwable);
		listener.onResponse(null);
	}
}
