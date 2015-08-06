package com.androidhelper.sdk.statistical;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;

public class AppRecord extends RecordTask {
	
	public AppRecord(String packageName) {
		mPackageName = packageName;
	}
	
	private String mPackageName;

	@Override
	public void run() {
		try {
			String stamp = System.currentTimeMillis() + "";
			File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_APP_SWITCH);
			String json = CommonUtils.readFile(file);
			JSONArray jsonArray = null;
			if (CommonUtils.isEmptyString(json)) { // 如果当前没有记录，则创建新记录
				jsonArray = new JSONArray();
			} else {
				jsonArray = new JSONArray(json);
			}
			if (jsonArray.length() > 0) {
				JSONObject lastObject = jsonArray.getJSONObject(jsonArray.length() - 1);
				lastObject.put(AMConstants.FILE_APP_RECORD_STAMP_END, stamp);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(AMConstants.FILE_APP_RECORD_STAMP_START, stamp);
			jsonObject.put(AMConstants.FILE_APP_RECORD_PACKAGE_NAME, mPackageName);
			jsonArray.put(jsonArray.length(), jsonObject);
			json = jsonArray.toString();
			CommonUtils.writeFile(json, file);
		} catch (JSONException e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
