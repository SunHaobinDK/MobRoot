package com.mob.root.net;

import java.io.File;
import org.apache.http.Header;
import android.content.Context;
import android.content.SharedPreferences;
import com.mob.root.AMApplication;
import com.mob.root.entity.ADConfig;
import com.mob.root.net.parser.ConfigParser;
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
			CommonUtils.writeFile(resultDatas, file);
			ConfigParser parser = new ConfigParser();
			ADConfig config = parser.parse(resultDatas);
			SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			android.content.SharedPreferences.Editor editor = sp.edit();
			// 下次获取配置文件缓存时间
			int cacheControlDays = config.getCacheControl();
			editor.putLong(AMConstants.SP_NEXT_CONFIG_STAMP, System.currentTimeMillis() + cacheControlDays * 24 * 60 * 60 * 1000).commit();
			// 下次数据上报时间
			String dataUploadInterval = config.getDataUploadInterval();
			if(!CommonUtils.isEmptyString(dataUploadInterval)) {
				editor.putLong(AMConstants.SP_NEXT_UPLOAD_STAMP, System.currentTimeMillis() + Integer.parseInt(dataUploadInterval) * 60 * 60 * 1000).commit();
			}
			//检查升级
			UpdateRequest request = new UpdateRequest(null);
			request.start();
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

	@Override
	public void start(Object... args) {
		try {
			doPost(AMConstants.UPLOAD_CONFIG_URI, null, true);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
