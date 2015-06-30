package com.mob.root.receiver;

import java.util.Iterator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.mob.root.AMApplication;
import com.mob.root.entity.AD;
import com.mob.root.tools.CommonUtils;

public class AppInstalledReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == intent || CommonUtils.isEmptyString(intent.getAction()) || !Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
			return;
		}
		// 接收安装广播
		String packageName = intent.getDataString();
		if (CommonUtils.isEmptyString(packageName)) {
			return;
		}
		int index = packageName.indexOf(":") + 1;
		if (packageName.length() < index) {
			return;
		}
		packageName = packageName.substring(index);
		if(null == AMApplication.instance.installADs || AMApplication.instance.installADs.size() <= 0) {
			return;
		}
		Iterator<AD> iterator = AMApplication.instance.installADs.iterator();
		while (iterator.hasNext()) {
			AD ad = iterator.next();
			if(null == ad || CommonUtils.isEmptyString(ad.getPackageName()) || CommonUtils.isEmptyString(packageName) || !packageName.equals(ad.getPackageName())) {
				continue;
			}
			iterator.remove();
			int openType = ad.getOpenType();
			switch (openType) {
			case 0:
				Intent openIntent = new Intent(); 
			 	PackageManager packageManager = context.getPackageManager(); 
			 	openIntent = packageManager.getLaunchIntentForPackage(ad.getPackageName()); 
			 	openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ; 
			 	context.startActivity(openIntent);
				return;
			case 1:
				return;
			case 2:
				return;
			}
		}
	}
}
