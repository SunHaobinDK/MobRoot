package com.mob.root.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;

public class ScreenLockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
			if (!CommonUtils.isLauncher(context)) { // 如果不是launcher则忽略
				return;
			}
			// 判断广告显示条件
		} else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
			SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			boolean wifiConnected = sp.getBoolean(AMConstants.SP_WIFI_CONNECTED, false);
			boolean charging = sp.getBoolean(AMConstants.SP_CHARGING, false);
			if (wifiConnected && charging) { // wifi 且 正在充电
				//静默
			}
		}
	}
}
