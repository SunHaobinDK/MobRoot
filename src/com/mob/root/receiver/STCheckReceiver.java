package com.mob.root.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mob.root.net.UploadDatasRequest;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class STCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!CommonUtils.isNetworkAvailable(context)) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		long currentTimeMillis = System.currentTimeMillis();
		Long dayDely = sp.getLong(AMConstants.SP_NEXT_UPLOAD_STAMP, 0);
		ConfigParser parser = new ConfigParser();
		try {
			String value = parser.getValue(context, AMConstants.NET_DATA_SWITCH);
			if(!CommonUtils.isEmptyString(value) && Integer.parseInt(value) == 0 && currentTimeMillis >= dayDely) {
				UploadDatasRequest request = new UploadDatasRequest(null);
				request.start();
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
