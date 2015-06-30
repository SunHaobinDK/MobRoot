package com.mob.root.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.mob.root.net.ConfigRequest;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;

public class ConfigCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!CommonUtils.isNetworkAvailable(context)) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		long currentTimeMillis = System.currentTimeMillis();

		long nextConfig = sp.getLong(AMConstants.SP_NEXT_CONFIG_STAMP, -1);
		if (nextConfig == -1 || currentTimeMillis - nextConfig >= 0) { // 如果没有时间点，或者当前时间已经超过了重新获取配置的时间
			ConfigRequest config = new ConfigRequest(null);
			config.start();
		}
	}
}
