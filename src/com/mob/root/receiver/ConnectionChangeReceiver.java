package com.mob.root.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mob.root.net.ConfigRequest;
import com.mob.root.tools.AMConstants;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {
			SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			long configStamp = sp.getLong(AMConstants.SP_LAST_CONFIG_STAMP, -1);
			if (-1 == configStamp) {
				ConfigRequest request = new ConfigRequest(null);
				request.start();
			}
		}
	}
}
