package com.androidhelper.sdk.receiver;

import com.androidhelper.sdk.tools.AMConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;

public class ChargeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
			int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
			if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
				edit.putBoolean(AMConstants.SP_CHARGING, true).commit();
			} else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
				edit.putBoolean(AMConstants.SP_CHARGING, false).commit();
			}
		}
	}
}
