package com.mob.root.ad.task;

import java.text.SimpleDateFormat;

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
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 简单通知栏广告
 * 
 */
class SimpleNotificationTask extends ADTask {

	private Context mContext;
//	private NotificationManager mNotificationManager;
	private AD mAD;

	SimpleNotificationTask(Context context) {
		mContext = context;
	}

	@Override
	public void start() {
		// 从服务器拉取最新的广告数据
		try {
			pullDatas();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void pullDatas() throws Exception {
	}

	@Override
	protected void displayAD() {
//		if (null == mNotificationManager) {
//			mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//		}
		if(null == mAD) {
			return;
		}
		ImageLoader.getInstance().loadImage(mAD.getIcon_url(), AMApplication.instance.displayOption, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				LokiService lokiService = LokiService.getInstance(mContext);
				if(null == lokiService) {
					return;
				}
				Intent webViewIntent = new Intent(); 
				webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				webViewIntent.setAction("android.intent.action.VIEW");    
		        Uri content_url = Uri.parse(mAD.getLandingPager());   
		        webViewIntent.setData(content_url);
				
				PendingIntent receiver = PendingIntent.getBroadcast(mContext, 0, webViewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				Notification.Builder builder = new Notification.Builder(mContext);
				builder.setWhen(System.currentTimeMillis());
				ApplicationInfo info = CommonUtils.getAppIcon(mContext);
				if(null == info) {
					return;
				}
				Notification notification = builder.setSmallIcon(info.icon).build();
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.defaults = Notification.DEFAULT_ALL;
				RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.simple_notification);
				remoteViews.setImageViewBitmap(R.id.appIcon, loadedImage);
				remoteViews.setTextViewText(R.id.appTitle, mAD.getTitle());
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm");
				String pushTime = timeFormat.format(new java.util.Date());
				remoteViews.setTextViewText(R.id.pushTime, pushTime);
				remoteViews.setTextViewText(R.id.appIntro, mAD.getDesc());
				
				notification.contentView = remoteViews;
				notification.contentIntent = receiver;
//				mNotificationManager.notify(0, notification);
				
				lokiService.sendNotificationAsPackage(info.packageName, 0, null, notification);
				
				SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
				sp.edit().putLong(AMConstants.SP_LAST_AD_STAMP, System.currentTimeMillis()).commit();
			}
		});
//		remoteViews.setImageViewResource(R.id.appLogo, R.drawable.test_logo);
	}
}