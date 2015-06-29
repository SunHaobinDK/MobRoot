package com.mob.root.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import com.mob.root.statistical.WifiRecord;
import com.mob.root.tools.AMConstants;
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
			return;
		}
		if (state != State.CONNECTED) { // 如果还没有成功连接，则忽略
			return;
		}
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		android.net.wifi.WifiInfo connectionInfo = wifiManager.getConnectionInfo();
		String ssid = connectionInfo.getSSID();
		int index = ssid.indexOf("\"");
		ssid = ssid.substring(index + 1);
		ssid = ssid.substring(0, ssid.indexOf("\""));
		sp.edit().putString(AMConstants.SP_LAST_SSID, ssid).commit();
		// 开始进行wifi信息统计
		WifiRecord record = new WifiRecord();
		record.record();
	}
}
