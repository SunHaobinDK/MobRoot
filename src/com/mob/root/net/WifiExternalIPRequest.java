package com.mob.root.net;

import java.io.File;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mob.root.AMApplication;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class WifiExternalIPRequest extends AMRequest<Object> {
	
	public WifiExternalIPRequest(IResponseListener<Object> listener) {
		super(listener);
	}

	private String connectedStamp;
	
	@Override
	public void start(Object... args) {
		if (null == args || args.length <= 0 || null == args[0]) {
			return;
		}
		connectedStamp = args[0].toString();
		doGet(AMConstants.EXTERNAL_IP_URI, null, this);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		try {
			if (CommonUtils.isEmptyString(datas)) {
				return;
			}
			if (CommonUtils.isEmptyString(connectedStamp)) {
				return;
			}
			File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_WIFI);
			String json = CommonUtils.readFile(file);
			JSONArray jsonArray = new JSONArray(json);
			int length = jsonArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String jsonStamp = jsonObject.getString(AMConstants.FILE_WIFI_CONNECTED_STAMP);
				if (CommonUtils.isEmptyString(connectedStamp) || CommonUtils.isEmptyString(jsonStamp) || !jsonStamp.equals(connectedStamp)) {
					continue;
				}
				jsonObject.put(AMConstants.FILE_WIFI_EIP, datas);
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
