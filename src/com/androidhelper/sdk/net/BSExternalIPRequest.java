package com.androidhelper.sdk.net;

import java.io.File;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;

public class BSExternalIPRequest extends AMRequest<Object> {
	
	public BSExternalIPRequest(IResponseListener<Object> listener) {
		super(listener);
	}

	private String stamp;
	
	@Override
	public void start(Object... args) {
		if (null == args || args.length <= 0 || null == args[0]) {
			return;
		}
		stamp = args[0].toString();
		doGet(AMConstants.EXTERNAL_IP_URI, null, this);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		try {
			if (CommonUtils.isEmptyString(datas)) {
				return;
			}
			if (CommonUtils.isEmptyString(stamp)) {
				return;
			}
			File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_BS);
			String json = CommonUtils.readFile(file);
			JSONArray jsonArray = new JSONArray(json);
			int length = jsonArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String jsonStamp = jsonObject.getString(AMConstants.FILE_BS_STAMP);
				if (CommonUtils.isEmptyString(stamp) || CommonUtils.isEmptyString(jsonStamp) || !jsonStamp.equals(stamp)) {
					continue;
				}
				jsonObject.put(AMConstants.FILE_BS_EIP, datas);
				jsonArray.put(i, jsonObject);
				break;
			}
			json  = jsonArray.toString();
			CommonUtils.writeFile(json, file);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
