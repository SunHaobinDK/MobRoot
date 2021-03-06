package com.androidhelper.sdk;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.entity.Apk;
import com.androidhelper.sdk.net.UploadDeviceRequest;
import com.androidhelper.sdk.receiver.AppInstalledReceiver;
import com.androidhelper.sdk.receiver.ChargeReceiver;
import com.androidhelper.sdk.receiver.ConfigCheckReceiver;
import com.androidhelper.sdk.receiver.ConnectionChangeReceiver;
import com.androidhelper.sdk.receiver.RemoveAppReceiver;
import com.androidhelper.sdk.receiver.STCheckReceiver;
import com.androidhelper.sdk.receiver.ScreenLockReceiver;
import com.androidhelper.sdk.receiver.WifiReceiver;
import com.androidhelper.sdk.statistical.AMPhoneStateListener;
import com.androidhelper.sdk.tools.AMConstants;
import com.loki.sdk.LokiService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class AMApplication extends Application {
	
	public static AMApplication instance;
	public DisplayImageOptions displayOption;
	public List<AD> installADs;
	public List<Apk> installApks;

	@Override
	public void onCreate() {
		instance = this;
		installADs = new ArrayList<AD>();
		installApks = new ArrayList<Apk>();
		initSDK();
		initImageloader();
		checkConfig();
		registReceiver();
		checkTask();
	}

	private void initSDK() {
		LokiService lokiService = LokiService.getInstance(this);
		SDKListener sdkListener = new SDKListener();
		if (null != lokiService) {
			lokiService.registerListener(sdkListener);
		}
	}
	
	private void initImageloader() {
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
        .threadPoolSize(3) 
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .tasksProcessingOrder(QueueProcessingType.FIFO) 
        .denyCacheImageMultipleSizesInMemory()
        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
        .memoryCacheSize(2 * 1024 * 1024)
        .memoryCacheSizePercentage(13) 
        .diskCache(new UnlimitedDiskCache(getDir(AMConstants.IMAGE_CACHE_DIR, Context.MODE_PRIVATE)))
        .diskCacheSize(50 * 1024 * 1024)
        .diskCacheFileCount(100)
        .build();
		ImageLoader.getInstance().init(configuration);
		
		displayOption = new DisplayImageOptions.Builder()
		.cacheInMemory(true) // 启用内存缓存
		.cacheOnDisk(true) // 启用外存缓存
		.considerExifParams(true) // 启用EXIF和JPEG图像格式
		.build();
	}
	
	private void registReceiver() {
		//app安装监听
		AppInstalledReceiver appInstalledReceiver = new AppInstalledReceiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addDataScheme("package");
		registerReceiver(appInstalledReceiver, intentFilter);
		//wifi监听
		WifiReceiver wifiReceiver = new WifiReceiver();
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		//统计信息检查
		STCheckReceiver dayTaskReceiver = new STCheckReceiver();
		registerReceiver(dayTaskReceiver, new IntentFilter(AMConstants.STATISTICS_CHECK_ACTION));
		//配置信息检查
		ConfigCheckReceiver configCheckReceiver = new ConfigCheckReceiver();
		registerReceiver(configCheckReceiver, new IntentFilter(AMConstants.CONFIG_CHECK_ACTION));
		//用户锁屏状态
		ScreenLockReceiver lockReceiver = new ScreenLockReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(lockReceiver, filter);
		//充电
		ChargeReceiver chargeReceiver = new ChargeReceiver();
		registerReceiver(chargeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		//卸载app监听
		RemoveAppReceiver removeAppReceiver = new RemoveAppReceiver();
		registerReceiver(removeAppReceiver, new IntentFilter(Intent.ACTION_PACKAGE_REMOVED));
		//基站信息
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new AMPhoneStateListener(this), PhoneStateListener.LISTEN_CELL_LOCATION);
		//网络监听
		ConnectionChangeReceiver ccr = new ConnectionChangeReceiver();
		registerReceiver(ccr, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	//检查是否已经上报服务器设备数据
	private void checkConfig() {
		SharedPreferences sp = getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		int uploaded = sp.getInt(AMConstants.SP_DEVICE_UPLOAD, -1);
		if(-1 == uploaded) {
			UploadDeviceRequest<Object> request = new UploadDeviceRequest<Object>(null);
			request.start();
		}
	}
	
	private void checkTask() {
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		Intent intent = new Intent(AMConstants.CONFIG_CHECK_ACTION);
		PendingIntent checkTaskPI = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		alarmManager.setRepeating(type, System.currentTimeMillis() + 60 * 60 * 1000, 60 * 60 * 1000, checkTaskPI);
		alarmManager.setRepeating(type, 1 * 1000, 10 * 60 * 1000, checkTaskPI);
		
		intent = new Intent(AMConstants.STATISTICS_CHECK_ACTION);
		checkTaskPI = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		alarmManager.setRepeating(type, System.currentTimeMillis() + 60 * 60 * 1000, 60 * 60 * 1000, checkTaskPI);
		alarmManager.setRepeating(type, 1 * 1000, 10 * 60 * 1000, checkTaskPI);
	}
}
