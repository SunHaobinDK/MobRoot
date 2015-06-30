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
import com.mob.root.tools.AESUtil;
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
	protected String resultDatas;
	protected IResponseListener<T> listener;
	protected Object[] args;
	
	public abstract void start(Object... args);
	
	protected void doPost(String url, JSONObject jsonObject, boolean isNeedParams) throws Exception {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		if (isNeedParams) {
			jsonObject.put(AMConstants.ENTITY_PARAMS, getParams());
		}
    	String datas = jsonObject.toString();
    	datas = AESUtil.encrypt(datas);
    	StringEntity entity = new StringEntity(datas);
    	AMNetClient.post(AMApplication.instance, url, entity, AMConstants.CONTENT_TYPE, this);
	}
	
	protected void doGet(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		AMNetClient.get(url, params, responseHandler);
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		if (--retryCount > 0) {
			start(args);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		if (CommonUtils.isEmptyString(datas)) {
			return;
		}
		resultDatas = AESUtil.desEncrypt(datas);
	}
	
	protected static JSONObject getParams() throws Exception {
    	SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		int os_version = sp.getInt(AMConstants.SP_SDK_VERSION, 0);
		String language = sp.getString(AMConstants.SP_LANGUAGE, null);
		String user_agent = sp.getString(AMConstants.SP_USER_AGENT, null);
		String uuid = sp.getString(AMConstants.SP_UUID, null);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(AMConstants.SP_SDK_VERSION, os_version);
		if(!CommonUtils.isEmptyString(language)) {
			jsonObject.put(AMConstants.SP_LANGUAGE, language);
		}
		if(!CommonUtils.isEmptyString(user_agent)) {
			jsonObject.put(AMConstants.SP_USER_AGENT, user_agent);
		}
		if(!CommonUtils.isEmptyString(uuid)) {
			jsonObject.put(AMConstants.SP_UUID, uuid);
		}
		return jsonObject;
    }
}
