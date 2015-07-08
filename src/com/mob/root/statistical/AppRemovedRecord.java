package com.mob.root.statistical;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.mob.root.AMApplication;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class AppRemovedRecord extends RecordTask {
	
	public AppRemovedRecord(String packageName) {
		mPackageName = packageName;
	}
	
	private String mPackageName;

	@Override
	public void run() {
		try {
			String stamp = System.currentTimeMillis() + "";
			File file = AMApplication.instance.getFileStreamPath(AMConstants.APP_REMOVED);
			String json = CommonUtils.readFile(file);
			JSONArray jsonArray = null;
			if (CommonUtils.isEmptyString(json)) { // 如果当前没有记录，则创建新记录
				jsonArray = new JSONArray();
			} else {
				jsonArray = new JSONArray(json);
			}
			JSONObject jsonObject = new JSONObject();
			PackageManager packageManager = AMApplication.instance.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(mPackageName, 0);
			int versionCode = info.versionCode;
			String versionName = info.versionName;
			long firstInstallTime = info.firstInstallTime;
			Signature[] signatures = info.signatures;
			StringBuilder sb = new StringBuilder();
			for (Signature signature : signatures) {
				sb.append(signature.toCharsString());
	        }
			String sign = sb.toString();
			String appName = info.applicationInfo.loadLabel(packageManager).toString();
			jsonObject.put(AMConstants.NET_DATAS_APP_VERSION_CODE, versionCode);
			jsonObject.put(AMConstants.NET_DATAS_APP_VERSION_NAME, versionName);
			jsonObject.put(AMConstants.APP_REMOVED_PACKAGE_NAME, mPackageName);
			jsonObject.put(AMConstants.NET_DATAS_APP_APP_NAME, appName);
			jsonObject.put(AMConstants.NET_DATAS_APP_SIGNATURE, sign);
			jsonObject.put(AMConstants.APP_REMOVED_STAMP, stamp);
			jsonObject.put(AMConstants.NET_DATAS_APP_INSTALLTIME, firstInstallTime);
			ApplicationInfo ai = packageManager.getApplicationInfo(info.packageName, 0);
			if((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				jsonObject.put(AMConstants.NET_DATAS_APP_APP_TYPE, 1);
			} else {
				jsonObject.put(AMConstants.NET_DATAS_APP_APP_TYPE, 3);
			}
			jsonArray.put(jsonArray.length(), jsonObject);
			json = jsonArray.toString();
			CommonUtils.writeFile(json, file);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
