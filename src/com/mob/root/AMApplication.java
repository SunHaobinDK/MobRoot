package com.mob.root;

import java.util.ArrayList;
import java.util.List;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import com.loki.sdk.LokiService;
import com.mob.root.entity.AD;
import com.mob.root.net.UploadDeviceRequest;
import com.mob.root.receiver.AppInstalledReceiver;
import com.mob.root.receiver.WifiReceiver;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;
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

	@Override
	public void onCreate() {
		instance = this;
		installADs = new ArrayList<AD>();
		initSDK();
		initImageloader();
		checkConfig();
		registReceiver();
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
		//注册app安装监听
		AppInstalledReceiver appInstalledReceiver = new AppInstalledReceiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addDataScheme("package");
		registerReceiver(appInstalledReceiver, intentFilter);
		//wifi监听
		WifiReceiver wifiReceiver = new WifiReceiver();
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		//统计信息检查接受者
		
		//配置信息检查接受者
		
		//用户锁屏状态
		
		//充电
	}
	
	//检查是否已经上报服务器设备数据
	private void checkConfig() {
		SharedPreferences sp = getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		String uuid = sp.getString(AMConstants.SP_UUID, null);
		if(CommonUtils.isEmptyString(uuid)) {
			UploadDeviceRequest request = new UploadDeviceRequest();
			request.start();
		}
	}
}
