package com.mob.root.net;

import java.io.File;
import java.util.Date;

import org.apache.http.Header;

import android.content.Context;
import android.content.SharedPreferences;

import com.mob.root.AMApplication;
import com.mob.root.entity.ADConfig;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class ConfigRequest extends AMRequest<ADConfig> {

	public ConfigRequest(IResponseListener<ADConfig> listener) {
		super(listener);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
	}

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
			CommonUtils.writeFile(data, file2);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
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
