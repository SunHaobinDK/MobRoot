package com.androidhelper.sdk.receiver;

import java.io.File;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.ad.task.ADTaskController;
import com.androidhelper.sdk.ad.task.TaskType;
import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.entity.Apk;
import com.androidhelper.sdk.tools.CommonUtils;

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
			// 0：自动打开，1：提示打开
			switch (openType) {
			case 0:
				Intent openIntent = new Intent(); 
			 	PackageManager packageManager = context.getPackageManager(); 
			 	openIntent = packageManager.getLaunchIntentForPackage(ad.getPackageName()); 
			 	openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ; 
			 	context.startActivity(openIntent);
				return;
			case 1:
				ADTaskController.getInstance().immediateADTask(TaskType.WINDOW_INSTALLED, ad);
				return;
			}
		}
		
		Iterator<Apk> apkIterator = AMApplication.instance.installApks.iterator();
		while (apkIterator.hasNext()) {
			Apk apk = apkIterator.next();
			if(null == apk) {
				continue;
			}
			String name = apk.getPackageName();
			if(CommonUtils.isEmptyString(name) || !name.endsWith(packageName)) {
				continue;
			}
			String referrer = apk.getReferrer();
			Intent referrerIntent = new Intent("com.android.vending.INSTALL_REFERRER");
			referrerIntent.putExtra("referrer", referrer);
			referrerIntent.setPackage(name);
			referrerIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			context.sendBroadcast(referrerIntent);
			apkIterator.remove();
		}
		File dir = AMApplication.instance.getFilesDir();
		File file = new File(dir.getAbsolutePath() + "/new.apk");
		if(null != file && file.exists()) {
			file.delete();
		}
	}
}
