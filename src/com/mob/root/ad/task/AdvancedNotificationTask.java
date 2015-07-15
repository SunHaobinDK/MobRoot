package com.mob.root.ad.task;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import com.loki.sdk.LokiService;
import com.mob.root.AMApplication;
import com.mob.root.R;
import com.mob.root.entity.AD;
import com.mob.root.net.AdDisplayUploadRequest;
import com.mob.root.net.AdvancedNotificationRequest;
import com.mob.root.net.IResponseListener;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 复杂通知栏广告
 * 
 */
class AdvancedNotificationTask extends ADTask implements IResponseListener<AD> {

	private Context mContext;
	private AD mAD;
	private boolean isLoaded;
	private Notification notification;
	private RemoteViews remoteViews;
	private PendingIntent receiver;
	private ApplicationInfo info;
	private LokiService lokiService;

	AdvancedNotificationTask(Context context) {
		mContext = context;
	}

	@Override
	public void start() {
		// 从服务器拉取最新的广告数据
		try {
			pullDatas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void pullDatas() throws Exception {
		AdvancedNotificationRequest request = new AdvancedNotificationRequest(this);
		request.start();
	}

	@Override
	protected void displayAD() {
		if(null == mAD || CommonUtils.isEmptyString(mAD.getLandingPager())) {
			return;
		}
		lokiService = LokiService.getInstance(mContext);
		if(null == lokiService) {
			return;
		}
		Intent webViewIntent = new Intent(); 
		webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		webViewIntent.setAction("android.intent.action.VIEW");    
        Uri content_url = Uri.parse(mAD.getLandingPager());   
        webViewIntent.setData(content_url);
		
		receiver = PendingIntent.getActivity(mContext, 0, webViewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Notification.Builder builder = new Notification.Builder(mContext);
		builder.setWhen(System.currentTimeMillis());
		info = CommonUtils.getAppIcon(mContext);
		if(null == info) {
			return;
		}
		notification = builder.setSmallIcon(info.icon).build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;
		
		remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.advanced_notification);
		ImageLoader.getInstance().loadImage(mAD.getIcon_url(), AMApplication.instance.displayOption, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				remoteViews.setImageViewBitmap(R.id.appIcon, loadedImage);
				if(isLoaded) {
					showNotification();
				} else {
					isLoaded = true;
				}
			}
		});
		ImageLoader.getInstance().loadImage(mAD.getCover_url(), AMApplication.instance.displayOption, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				remoteViews.setImageViewBitmap(R.id.appCover, loadedImage);
				if(isLoaded) {
					showNotification();
				} else {
					isLoaded = true;
				}
			}
		});
	}
	
	private void showNotification() {
		remoteViews.setTextViewText(R.id.appTitle, mAD.getTitle());
		remoteViews.setTextViewText(R.id.appIntro, mAD.getDesc());
		remoteViews.setOnClickPendingIntent(R.id.viewBtn, receiver);
		
		notification.bigContentView = remoteViews;
		notification.contentIntent = receiver;
		lokiService.sendNotificationAsPackage(info.packageName, 0, null, notification);
		SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong(AMConstants.SP_LAST_AD_STAMP, System.currentTimeMillis()).commit();
		
		AdDisplayUploadRequest request = new AdDisplayUploadRequest(null);
		request.start(mAD.getPackageName(), 1);
	}

	@Override
	public void onResponse(AD ad) {
		mAD = ad;
		displayAD();
	}
}