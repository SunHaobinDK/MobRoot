package com.mob.root.net;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mob.root.AMApplication;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AESUtil;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class ADExternalIPRequest extends AMRequest<Object> {
	
	private int displayType;
	private AsyncHttpResponseHandler responseHandler;
	private JSONObject jsonObject;
	
	public ADExternalIPRequest(IResponseListener<Object> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		if(null != args && null != args[0]) {
			displayType = Integer.parseInt(args[0].toString());
		}
		if(null != args && null != args[1]) {
			responseHandler = (AsyncHttpResponseHandler) args[1];
		}
		if(null != args && null != args[2]) {
			jsonObject = (JSONObject) args[2];
		}
		doGet(AMConstants.EXTERNAL_IP_URI, null, this);
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		if (--retryCount > 0) {
			start(args);
			return;
		}
		try {
			getServerAd(null);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		try {
			if (CommonUtils.isEmptyString(datas)) {
				return;
			}
			getServerAd(datas);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

	private void getServerAd(String eip) throws Exception {
		if(null == jsonObject) {
			jsonObject = new JSONObject();
		}
		SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		String androidId = sp.getString(AMConstants.SP_ANDROID_ID, null);
		int osVersion = sp.getInt(AMConstants.SP_SDK_VERSION, 0);
		int sdkVersion = sp.getInt(AMConstants.SP_CLIENT_VERSION, 0);
		String language = sp.getString(AMConstants.SP_LANGUAGE, null);
		String country = sp.getString(AMConstants.SP_COUNTRY, null);
		String device = sp.getString(AMConstants.SP_MODEL, null);
		long memoryTotal = CommonUtils.getMemoryTotal();
		int networkType = CommonUtils.getNetworkType();
		String user_agent = sp.getString(AMConstants.SP_USER_AGENT, null);
		String flavors = sp.getString(AMConstants.SP_FLAVOR_IDS, null);
		String[] ids = null;
		if(null != flavors) {
			ids = flavors.split(";");
		}
		
		if(!CommonUtils.isEmptyString(androidId)) {
			jsonObject.put(AMConstants.SP_ANDROID_ID, androidId);
		}
		jsonObject.put(AMConstants.SP_SDK_VERSION, osVersion);
		if(!CommonUtils.isEmptyString(eip)) {
			jsonObject.put(AMConstants.NET_EIP, eip);
		}
		jsonObject.put(AMConstants.SP_CLIENT_VERSION, sdkVersion);
		if(!CommonUtils.isEmptyString(language)) {
			jsonObject.put(AMConstants.SP_LANGUAGE, language);
		}
		if(!CommonUtils.isEmptyString(country)) {
			jsonObject.put(AMConstants.SP_COUNTRY, country);
		}
		if(!CommonUtils.isEmptyString(device)) {
			jsonObject.put(AMConstants.SP_MODEL, device);
		}
		jsonObject.put(AMConstants.NET_MEMORY_SIZE, memoryTotal);
		jsonObject.put(AMConstants.NET_NETWORK_TYPE, networkType);
		if(!CommonUtils.isEmptyString(user_agent)) {
			jsonObject.put(AMConstants.SP_USER_AGENT, user_agent);
		}
		if(null != ids) {
			JSONArray array = new JSONArray();
			for (int i = 0; i < ids.length; i++) {
				array.put(ids[i]);
			}
			jsonObject.put(AMConstants.SP_FLAVOR_IDS, array);
		}
		jsonObject.put(AMConstants.NET_DISPLAY_TYPE, displayType);
		
		String datas = jsonObject.toString();
    	datas = AESUtil.encrypt(datas);
    	StringEntity entity = new StringEntity(datas);
    	ConfigParser parser = new ConfigParser();
		String url = parser.getValue(AMApplication.instance, AMConstants.NET_AD_REQUEST_URL);
		if (CommonUtils.isEmptyString(url)) {
			return;
		}
    	AMNetClient.post(AMApplication.instance, url, entity, AMConstants.CONTENT_TYPE, responseHandler);
	}
}
