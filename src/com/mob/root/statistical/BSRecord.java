package com.mob.root.statistical;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

import com.mob.root.AMApplication;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class BSRecord extends RecordTask {
	
	public BSRecord(int cid, long stamp) {
		mCid = cid;
		mStamp = stamp;
	}
	
	private int mCid;
	private long mStamp;

	@Override
	public void run() {
		try {
			SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putInt(AMConstants.SP_LAST_CELL_ID, mCid).commit();
			String iip = CommonUtils.getIPAddress(true);
			long stamp = System.currentTimeMillis();
			Location lastLocation = AMLocation.getInstance(stamp).getLastLocation();
			
			File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_BS);
			String json = CommonUtils.readFile(file);
			
			JSONArray jsonArray = null;
			if (CommonUtils.isEmptyString(json)) { // 如果当前没有记录，则创建新记录
				jsonArray = new JSONArray();
			} else {
				jsonArray = new JSONArray(json);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(AMConstants.FILE_BS_CID, mCid);
			jsonObject.put(AMConstants.FILE_BS_IIP, iip);
			jsonObject.put(AMConstants.FILE_BS_LATITUDE, null == lastLocation ? "" : lastLocation.getLatitude() + "");
			jsonObject.put(AMConstants.FILE_BS_LONGITUDE, null == lastLocation ? "" : lastLocation.getLongitude() + "");
			jsonObject.put(AMConstants.FILE_BS_STAMP, mStamp);
			
			jsonArray.put(jsonArray.length(), jsonObject);
			json = jsonArray.toString();
			CommonUtils.writeFile(json, file);
		} catch (JSONException e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
