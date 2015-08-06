package com.androidhelper.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.androidhelper.sdk.net.ConfigRequest;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;

public class ConfigCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (!CommonUtils.isNetworkAvailable(context)) {
				return;
			}
			SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			long currentTimeMillis = System.currentTimeMillis();

			long lastStamp = sp.getLong(AMConstants.SP_LAST_CONFIG_STAMP, -1);
			ConfigParser parser = new ConfigParser();
			String value = parser.getValue(context, AMConstants.NET_CACHE_CONTROL);
			if(CommonUtils.isEmptyString(value)) {
				return;
			}
			long interval = Integer.parseInt(value) * 60 * 60 * 1000;
			if (lastStamp == -1 || currentTimeMillis - (interval + lastStamp) >= 0) { // 如果没有时间点，或者当前时间已经超过了重新获取配置的时间
				ConfigRequest config = new ConfigRequest(null);
				config.start();
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
