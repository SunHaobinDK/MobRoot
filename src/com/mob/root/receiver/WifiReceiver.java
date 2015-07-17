package com.mob.root.receiver;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import com.mob.root.AMApplication;
import com.mob.root.net.ConfigRequest;
import com.mob.root.net.WifiExternalIPRequest;
import com.mob.root.statistical.AMLocation;
import com.mob.root.statistical.AMLocation.AMLocationListener;
import com.mob.root.statistical.WifiRecord;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class WifiReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (null == intent || null == intent.getAction()) {
			return;
		}
		if (!WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			return;
		}
		Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if(null == parcelableExtra) {
			return;
		}
		NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
		State state = networkInfo.getState();
		SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		String lastSSID = sp.getString(AMConstants.SP_LAST_SSID, null);
		
		if (state == State.DISCONNECTED && !CommonUtils.isEmptyString(lastSSID)) { // 如果上次保存的ssid不为空，则证明曾经连接上过wifi，则需要记录断开时间点
			sp.edit().putBoolean(AMConstants.SP_WIFI_CONNECTED, false).commit();
			sp.edit().remove(AMConstants.SP_LAST_SSID).commit(); //断开连接以后删除上次成功连接ssid字段
			try {
				recordWifiDisconnected(lastSSID);
			} catch (Exception e) {
				AMLogger.e(null, e.getMessage());
			}
			return;
		}
		if (state != State.CONNECTED) { // 如果还没有成功连接，则忽略
			return;
		}
		
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		android.net.wifi.WifiInfo connectionInfo = wifiManager.getConnectionInfo();
		String ssid = connectionInfo.getSSID();
		int index = ssid.indexOf("\"");
		if(-1 != index) {
			ssid = ssid.substring(index + 1);
			ssid = ssid.substring(0, ssid.indexOf("\""));
		}
		sp.edit().putString(AMConstants.SP_LAST_SSID, ssid).commit();
		sp.edit().putBoolean(AMConstants.SP_WIFI_CONNECTED, true).commit();
		// 开始进行wifi信息统计
		long stamp = System.currentTimeMillis();
		WifiRecord record = new WifiRecord(stamp);
		record.record();
		
		WifiExternalIPRequest request = new WifiExternalIPRequest(null);
		request.start(stamp);
		
		AMLocation.getInstance(stamp).getCurrentLocation(new AMLocationListener() {
			
			@Override
			public void onLocationChanged(Location location, long stamp) {
				try {
					if(null == location) {
						return;
					}
					File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_WIFI);
					String json = CommonUtils.readFile(file);
					if (CommonUtils.isEmptyString(json)) {
						return;
					}
					JSONArray jsonArray = new JSONArray(json);
					int length = jsonArray.length();
					for (int i = 0; i < length; i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						long jsonStamp = jsonObject.getLong(AMConstants.FILE_WIFI_CONNECTED_STAMP);
						if (stamp != jsonStamp) {
							continue;
						}
						jsonObject.put(AMConstants.FILE_WIFI_LATITUDE, location.getLatitude());
						jsonObject.put(AMConstants.FILE_WIFI_LONGITUDE, location.getLongitude());
						jsonArray.put(i, jsonObject);
						break;
					}
					json  = jsonArray.toString();
					CommonUtils.writeFile(json, file);
				} catch (Exception e) {
					AMLogger.e(null, e.getMessage());
				}
			}
		});
	}

	private void recordWifiDisconnected(String ssid) throws Exception {
		File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_WIFI);
		String json = CommonUtils.readFile(file);
		if (CommonUtils.isEmptyString(json)) {
			return;
		}
		JSONArray jsonArray = new JSONArray(json);
		int length = jsonArray.length();
		for (int i = length - 1; i >= 0; i--) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String jsonSSID = jsonObject.getString(AMConstants.FILE_WIFI_SSID);
			if (CommonUtils.isEmptyString(jsonSSID) || CommonUtils.isEmptyString(ssid) || !jsonSSID.equals(ssid)) {
				continue;
			}
			jsonObject.put(AMConstants.FILE_WIFI_DISCONNECTED_STAMP, System.currentTimeMillis());
			jsonArray.put(i, jsonObject);
			break;
		}
		json  = jsonArray.toString();
		CommonUtils.writeFile(json, file);
	}
}
