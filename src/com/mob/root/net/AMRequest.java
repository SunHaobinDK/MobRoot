package com.mob.root.net;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mob.root.AMApplication;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public abstract class AMRequest<T> extends TextHttpResponseHandler {
	
	public AMRequest(IResponseListener<T> listener){
		this.listener = listener;
		ConfigParser parser = new ConfigParser();
		try {
			String value = parser.getValue(AMApplication.instance, AMConstants.NET_FAILOVER_TRY_COUNT);
			if (!CommonUtils.isEmptyString(value)) {
				this.retryCount = Integer.parseInt(value);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

	protected int retryCount;
//	protected String resultDatas;
	protected IResponseListener<T> listener;
	protected Object[] args;
	
	public abstract void start(Object... args);
	
	/**
	 * 请求广告
	 * 
	 * @param displayType
	 *            0为单个广告，1为8个广告
	 * @throws Exception 
	 */
	protected void doAdPost(int displayType, AsyncHttpResponseHandler responseHandler, JSONObject jsonObject) throws Exception {
		ADExternalIPRequest request = new ADExternalIPRequest(null);
		request.start(displayType, responseHandler, jsonObject);
	}
	
	protected void doPost(String url, String json, AsyncHttpResponseHandler responseHandler) throws Exception {
		AMNetClient.post(AMApplication.instance, url, new StringEntity(json), AMConstants.CONTENT_TYPE, responseHandler);
	}
	
	protected void doPost(String url, JSONObject jsonObject, boolean isNeedParams) throws Exception {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		if (isNeedParams) {
			SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			int os_version = sp.getInt(AMConstants.SP_SDK_VERSION, 0);
			String language = sp.getString(AMConstants.SP_LANGUAGE, null);
			String country = sp.getString(AMConstants.SP_COUNTRY, null);
			String user_agent = sp.getString(AMConstants.SP_USER_AGENT, null);
			String androidId = sp.getString(AMConstants.SP_ANDROID_ID, null);
			jsonObject.put(AMConstants.SP_SDK_VERSION, os_version);
			if(!CommonUtils.isEmptyString(language)) {
				jsonObject.put(AMConstants.SP_LANGUAGE, language);
			}
			if(!CommonUtils.isEmptyString(country)) {
				jsonObject.put(AMConstants.SP_COUNTRY, country);
			}
			if(!CommonUtils.isEmptyString(user_agent)) {
				jsonObject.put(AMConstants.SP_USER_AGENT, user_agent);
			}
			if(!CommonUtils.isEmptyString(androidId)) {
				jsonObject.put(AMConstants.SP_ANDROID_ID, androidId);
			}
		}
    	String datas = jsonObject.toString();
//    	File f = AMApplication.instance.getFileStreamPath("aa");
//    	if(url.equals("http://sdk.fb-api.net/daily.php")) {
//    		CommonUtils.writeFile(datas, f);
//    	}
//    	datas = AESUtil.encrypt(datas);
    	StringEntity entity = new StringEntity(datas, "utf-8");
    	AMNetClient.post(AMApplication.instance, url, entity, AMConstants.CONTENT_TYPE, this);
	}
	
	protected void doGet(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		AMNetClient.get(url, params, responseHandler);
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		AMLogger.e(null, throwable.getMessage());
		if (--retryCount > 0) {
			start(args);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		if (CommonUtils.isEmptyString(datas)) {
			return;
		}
//		resultDatas = AESUtil.desEncrypt(datas);
	}
	
//	protected static JSONObject getParams() throws Exception {
//    	SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
//		int os_version = sp.getInt(AMConstants.SP_SDK_VERSION, 0);
//		String language = sp.getString(AMConstants.SP_LANGUAGE, null);
//		String country = sp.getString(AMConstants.SP_COUNTRY, null);
//		String user_agent = sp.getString(AMConstants.SP_USER_AGENT, null);
//		String androidId = sp.getString(AMConstants.SP_ANDROID_ID, null);
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put(AMConstants.SP_SDK_VERSION, os_version);
//		if(!CommonUtils.isEmptyString(language)) {
//			jsonObject.put(AMConstants.SP_LANGUAGE, language);
//		}
//		if(!CommonUtils.isEmptyString(country)) {
//			jsonObject.put(AMConstants.SP_COUNTRY, country);
//		}
//		if(!CommonUtils.isEmptyString(user_agent)) {
//			jsonObject.put(AMConstants.SP_USER_AGENT, user_agent);
//		}
//		if(!CommonUtils.isEmptyString(androidId)) {
//			jsonObject.put(AMConstants.SP_ANDROID_ID, androidId);
//		}
//		return jsonObject;
//    }
}
