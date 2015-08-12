package com.androidhelper.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.androidhelper.sdk.ADType;
import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.EventType;
import com.androidhelper.sdk.ad.ADController;
import com.androidhelper.sdk.ad.task.ADTaskBuilder;
import com.androidhelper.sdk.ad.task.TaskType;
import com.androidhelper.sdk.net.SilentRequest;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;

public class ScreenLockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
			if (!CommonUtils.isLauncher(context)) { // 如果不是launcher则忽略
				return;
			}
			// 判断广告显示条件
			try {
				ADType adType = ADController.getInstance().getDisplayADType(AMApplication.instance, EventType.UNLOCK_SCREEN);
				if(null != adType) {
					ADTaskBuilder builder = new ADTaskBuilder();
					builder.setADType(TaskType.values()[adType.ordinal()], AMApplication.instance, null);
					builder.build().start();
				}
			} catch (Exception e) {
				AMLogger.e(null, e.getMessage());
			}
		} else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
			SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			boolean wifiConnected = sp.getBoolean(AMConstants.SP_WIFI_CONNECTED, false);
			boolean charging = sp.getBoolean(AMConstants.SP_CHARGING, false);
			if (wifiConnected && charging) { // wifi 且 正在充电
				//静默
				try {
					ADType adType = ADController.getInstance().getDisplayADType(AMApplication.instance, EventType.SILENT);
					if(null != adType) {
						SilentRequest request = new SilentRequest(null);
						request.start();
					}
				} catch (Exception e) {
					AMLogger.e(null, e.getMessage());
				}
			}
		}
	}
}
