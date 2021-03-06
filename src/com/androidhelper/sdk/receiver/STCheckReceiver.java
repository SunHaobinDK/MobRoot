package com.androidhelper.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.androidhelper.sdk.net.UploadDatasRequest;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;

public class STCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!CommonUtils.isNetworkAvailable(context)) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		long currentTimeMillis = System.currentTimeMillis();
		Long lastStamp = sp.getLong(AMConstants.SP_LAST_UPLOAD_STAMP, 0);
		ConfigParser parser = new ConfigParser();
		try {
			String value = parser.getValue(context, AMConstants.NET_DATA_UPLOAD_INTERVAL);
			long nextStamp = CommonUtils.isEmptyString(value) ? lastStamp : lastStamp + Integer.parseInt(value) * 60 * 60 * 1000;
//			long nextStamp = CommonUtils.isEmptyString(value) ? lastStamp : lastStamp + 10 * 60 * 1000;
			String dataSwitch = parser.getValue(context, AMConstants.NET_DATA_SWITCH);
			if(!CommonUtils.isEmptyString(dataSwitch) && Integer.parseInt(dataSwitch) == 0 && currentTimeMillis >= nextStamp) {
				UploadDatasRequest request = new UploadDatasRequest(null);
				request.start();
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
